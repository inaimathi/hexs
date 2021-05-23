(ns hexs.svg
  (:require [clojure.string :as str]
            [clojure.edn :as edn]))

(defn drop-but [n coll]
  (drop (- (count coll) n) coll))

(defn parse-path [path-string]
  (->> (str/split path-string #" ")
       (partition 2)
       (map (fn [[a b]]
              (if (re-find #"^\d" a)
                [(edn/read-string a) (edn/read-string b)]
                [(keyword (subs a 0 1))
                 (edn/read-string (subs a 1))
                 (edn/read-string b)])))))

(defn unparse-path [path-vec]
  (->> path-vec
       (mapcat
        (fn [el]
          (if (= 2 (count el))
            (map str el)
            (cons (str (name (first el)) (second el))
                  (map str (rest (rest el)))))))
       (str/join " ")))

(defn unparse-transform [& {:keys [scale translate rotate]}]
  (let [scale (when scale (str "scale(" scale ")"))
        translate (when translate
                    (let [[trans-x trans-y] translate]
                      (str "translate(" trans-x ", " trans-y ")")))
        rotate (when rotate
                 (str "rotate(" rotate ")"))
        ts (remove nil? [scale translate rotate])
        transform (when (not (empty? ts)) (str/join ", " ts))]
    transform))

(defn box-of [points]
  (reduce
   (fn [[[minx miny] [maxx maxy]] [x y]]
     [[(min minx x) (min miny y)]
      [(max maxx x) (max maxy y)]])
   [(first points) (first points)]
   (rest points)))

(defn ->hic [svg-tree] svg-tree)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Hacking with SVG sprites
;; (->> "svg/hexagon-tiles.svg" io/resource io/file xml/parse :content second :content (take 5) (map (fn [path] [:path (:attrs path)])) zerofi)

(defn update-inf [m ks f]
  (if (get-in m ks) (update-in m ks f) m))

(defn -normalize [path]
  (update-inf
   path [1 :fill-opacity]
   #(let [opacity (edn/read-string %)]
      (->> (edn/read-string %)
           (format "%.2f")))))

(defn zerofi [paths]
  (let [paths (map -normalize paths)
        points (->> paths
                    (map second)
                    (map :d)
                    (map parse-path))
        xys (mapcat
             (partial map (fn [tup] (if (= 2 (count tup)) tup (vec (rest tup)))))
             points)
        [[minx miny] [maxx maxy]] (box-of xys)
        w (- maxx minx)
        h (- maxy miny)
        [dx dy] (map #(do (println %) (Math/round (float %))) [(+ minx (/ w 2)) (+ miny (/ h 2))])]

    (map
     (fn [path]
       (update-in
        path [1 :d]
        (fn [path-str]
          (->> path-str
               parse-path
               (map (fn [el]
                      (if (= 2 (count el))
                        (let [[x y] el]
                          [(int (- x dx)) (int (- y dy))])
                        (let [[name x y] el]
                          [name (int (- x dx)) (int (- y dy))]))))
               unparse-path))))
     paths)))