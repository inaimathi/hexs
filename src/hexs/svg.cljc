(ns hexs.svg
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.edn :as edn]

            [hexs.util :as util]))

(defn parse-path [path-string]
  (->> (str/split path-string #" ")
       (partition 2)
       (map (fn [[a b]]
              (if (re-find #"^[\d-+]" a)
                [(edn/read-string a) (edn/read-string b)]
                [(keyword (subs a 0 1))
                 (edn/read-string (subs a 1))
                 (edn/read-string b)])))
       vec))

(defn unparse-path [path-vec]
  (->> path-vec
       (mapcat
        (fn [el]
          (cond (keyword? el) (name el)
                (number? el) (str el " ")
                (= 2 (count el)) (map #(str % " ") el)
                (seqable? el) (cons (str (name (first el)) (second el) " ")
                                    (map #(str % " ") (rest (rest el))))
                :else (str el " "))))
       (apply str)
       str/trim))

(def transform-names
  #{"rotate" "translate" "scale" "skewX" "skewY" "matrix"})

(defn parse-transform [transform-str]
  (->> transform-str
       (re-seq #"(\w+)\(([\d\-\+\., ]+)\)")
       (map rest)
       (map
        (fn [[op-name params]]
          (assert (contains? transform-names op-name)
                  (str "Unknown SVG transform found: '" op-name "'"))
          [(keyword op-name)
           (->> (str/split params #"[\s,]+")
                (map edn/read-string)
                vec)]))
       (into {})))

(defn unparse-transform [opts]
  (->> opts
       (map
        (fn [[k v]]
          (let [op-name (name k)]
            (assert (contains? transform-names op-name)
                    (str "Unknown SVG transform found: '" op-name "'"))
            (str op-name "("
                 (cond (string? v) v
                       (seqable? v) (str/join " " v)
                       :else v) ")"))))
       (str/join ", ")))

(defn -svg-map-opts [f svg-tree]
  (let [[node opts children]
        (if (map? (second svg-tree))
          [(first svg-tree) (second svg-tree) (rest (rest svg-tree))]
          [(first svg-tree) nil (rest svg-tree)])
        effed (when opts (f opts))
        mapped (map #(-svg-map-opts f %) children)]
    (->> (if opts
           (cons effed mapped)
           mapped)
         (cons node)
         vec)))

(defn parse [svg-tree]
  (-svg-map-opts
   (fn [opts]
     (util/updatef
      opts :d parse-path
      :transform parse-transform))
   svg-tree))

(defn unparse [svg-tree]
  (-svg-map-opts
   (fn [opts]
     (util/updatef
      opts :d #(if (string? %) % (unparse-path %))
      :transform #(if (string? %) % (unparse-transform %))))
   svg-tree))

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

(defn -normalize [path]
  (util/update-inf
   path [1 :fill-opacity]
   #(let [opacity (edn/read-string %)]
      (->> (edn/read-string %)
           util/round-to
           str))))

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
