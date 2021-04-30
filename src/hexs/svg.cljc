(ns hexs.svg
  (:require [clojure.string :as str]))

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
;; (def SVG (xml/parse (io/file (io/resource "svg/hexagon-tiles.svg"))))
;; (->> SVG :content second :content (take 4) (map (fn [path] [:path (:attrs path)])) zerofi)

(defn zerofi [paths]
  (let [points (->> paths
                    (map second)
                    (map :d)
                    (map svg/parse-path))
        xys (mapcat
             (partial map (fn [tup] (if (= 2 (count tup)) tup (vec (rest tup)))))
             points)
        [[minx miny] [maxx maxy]] (svg/box-of xys)
        dx (int (/ (- maxx minx) 2))
        dy (int (/ (- maxy miny) 2))]

    (map
     (fn [path]
       (update-in
        path [1 :d]
        (fn [path-str]
          (->> path-str
               svg/parse-path
               (map (fn [el]
                      (if (= 2 (count el))
                        (let [[x y] el]
                          [(int (- x dx)) (int (- y dy))])
                        (let [[name x y] el]
                          [name (int (- x dx)) (int (- y dy))]))))
               svg/unparse-path))))
     paths)))
