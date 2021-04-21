(ns hexs.grid
  (:require [clojure.string :as str]

            [hexs.util :as util]))

;; This is a generic implementation of a cubic hex grid.
;;   Cribbing aggressively but impressionistically
;; from https://www.redblobgames.com/grids/hexagons

;; Point  :: [ Number Number Number ]
;; Space   :: [ Int Int Int ]
;; Grid a :: { Space -> a }

(def OFFs
  "[ Space ]"
  [[-1 1 0] [-1 0 1]
   [0 -1 1] [1 -1 0]
   [0 1 -1] [1 0 -1]])

(defn ^:export neighborhood
  "Space -> Int? -> [ Space ]"
  [space & {:keys [radius] :or {radius 1}}]
  (let [offs (if (= 1 radius)
               OFFs
               (disj
                (reduce
                 (fn [memo _]
                   (set (mapcat neighborhood memo)))
                 (set OFFs) (range (dec radius)))
                space))]
    (vec (map #(vec (map + %1 %2)) offs (repeat space)))))

(defn ^:export neighborhood-of
  "Grid g -> Space -> Grid g"
  [grid space & {:keys [radius] :or {radius 1}}]
  (->> (neighborhood space :radius radius)
       (select-keys grid)))

(defn ^:export distance
  "Space -> Space -> Int"
  [space-a space-b]
  (apply max (map (comp util/abs -) space-a space-b)))

(defn ^:export space-round
  "Point -> Space"
  [space]
  (let [[x y z] space
        [rx ry rz :as rs] (map #(Math/round %) space)
        [xd yd zd :as ds] (map #(util/abs (- %1 %2)) rs space)]
    (cond (and (> xd yd) (> xd zd))
          [(- (- ry) rz) ry rz]

          (> yd zd)
          [rx (- (- rx) rz) rz]

          :else
          [rx ry (- (- rx) ry)])))

(defn -lerp [a b t] (+ a (* (- b a) t)))
(defn -space-lerp [a b t] (vec (map #(-lerp %1 %2 t) a b)))

(defn ^:export line
  "Space -> Space -> [ Space ]"
  [space-a space-b]
  (let [dist (distance space-a space-b)]
    (map
     #(->> (* (/ 1.0 dist) %)
           (-space-lerp space-a space-b)
           space-round)
     (range (inc dist)))))

(defn -->path [came-from-map dest]
  (if (contains? came-from-map dest)
    (loop [path (list dest)
           next (get came-from-map dest)]
      (if (nil? next)
        (vec path)
        (recur (cons next path)
               (get came-from-map next))))
    nil))

(defn ^:export find-path
  "Grid a -> (Space -> Boolean)? -> (Space -> Space -> Number)? -> Space -> Space -> [ Space ]

  Takes a grid, a boolean to check whether a given space is an obstacle, and two spaces. Returns a path from the first space to the second (if found) that avoids the obstacles.
  TODO - Use A* search (currently runs a greedy algorithm)"
  [grid space-a space-b & {:keys [obstacle-f cost-f]
                           :or {obstacle-f (util/always false)
                                cost-f (util/always 1)}}]
  (loop [cur space-a
         frontier (vec (sort-by #(distance % space-b) (neighborhood space-a)))
         cost-so-far {space-a 0}
         came-from {space-a nil}
         count 0]
    (if (or (nil? cur) (= cur space-b) (> count 10))
      (-->path came-from space-b)
      (let [next (first frontier)
            next-cost (assoc cost-so-far next
                             (+ (get cost-so-far cur)
                                (cost-f cur next)))]
        (recur next
               (->> (neighborhood next)
                    (concat frontier)
                    (remove #(contains? next-cost %))
                    (filter #(contains? grid %))
                    (remove #(obstacle-f %))
                    (sort-by #(distance % space-b))
                    vec)
               next-cost
               (assoc came-from next cur)
               (inc count))))))

;; (grid/find-path (grid/empty nil :radius 5) [-2 2 0] [2 -2 0])

(defn ^:export slice
  "Grid a -> [ Space ] -> Grid a"
  [grid spaces]
  (select-keys grid spaces))

(defn empty
  "Int? -> a -> Grid a"
  [empty-val & {:keys [radius] :or {radius 0}}]
  (let [zero [0 0 0]]
    (if (zero? radius) {}
        (assoc
         (reduce
          (fn [memo space] (assoc memo space empty-val)) {}
          (neighborhood zero :radius radius))
         zero empty-val))))
