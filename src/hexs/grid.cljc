(ns hexs.grid
  (:require [clojure.string :as str]))

;; This is a generic implementation of a cubic hex grid.
;;   Cribbing aggressively but impressionistically
;; from https://www.redblobgames.com/grids/hexagons

;; Point  :: [ Number Number Number ]
;; Space   :: [ Int Int Int ]
;; Grid g :: { Space -> a }

(def OFFs
  ;; [ Space ]
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

(defn abs
  "(abs n) is the absolute value of n"
  [n]
  (cond
    (not (number? n)) (throw (IllegalArgumentException.
                              "abs requires a number"))
    (neg? n) (- n)
    :else n))

(defn ^:export distance
  "Space -> Space -> Int"
  [space-a space-b]
  (apply max (map (comp abs -) space-a space-b)))

(defn ^:export space-round
  "Point -> Space"
  [space]
  (let [[x y z] space
        [rx ry rz :as rs] (map #(Math/round %) space)
        [xd yd zd :as ds] (map #(abs (- %1 %2)) rs space)]
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

(defn ^:export find-path
  "TODO - Grid a -> (a -> Boolean) -> Space -> Space -> [ Space ]"
  [grid obstacle?-fn space-a space-b]
  (line space-a space-b))

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
