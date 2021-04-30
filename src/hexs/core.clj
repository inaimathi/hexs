(ns hexs.core
  (:require [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.edn :as edn]
            [hiccup.core :as hic]
            [bidi.ring :as bring]

            [hexs.server :as server]))

;; (def SVG (xml/parse (io/file (io/resource "svg/hexagon-tiles.svg"))))
;; (->> SVG :content second :content (take 4) (map (fn [path] [:path (:attrs path)])) zerofi)
(defn -parse-path [path-string]
  (->> (str/split path-string #" ")
       (partition 2)
       (map (fn [[a b]]
              (if (re-find #"^\d" a)
                [(edn/read-string a) (edn/read-string b)]
                [(keyword (subs a 0 1))
                 (edn/read-string (subs a 1))
                 (edn/read-string b)])))))

(defn -unparse-path [path-vec]
  (->> path-vec
       (mapcat
        (fn [el]
          (if (= 2 (count el))
            (map str el)
            (cons (str (name (first el)) (second el))
                  (map str (rest (rest el)))))))
       (str/join " ")))

(defn -box-of [points]
  (reduce
   (fn [[[minx miny] [maxx maxy]] [x y]]
     [[(min minx x) (min miny y)]
      [(max maxx x) (max maxy y)]])
   [(first points) (first points)]
   (rest points)))

(defn zerofi [paths]
  (let [points (->> paths
                    (map second)
                    (map :d)
                    (map -parse-path))
        xys (mapcat
             (partial map (fn [tup] (if (= 2 (count tup)) tup (vec (rest tup)))))
             points)
        [[minx miny] [maxx maxy]] (-box-of xys)
        dx (int (/ (- maxx minx) 2))
        dy (int (/ (- maxy miny) 2))]

    (map
     (fn [path]
       (update-in
        path [1 :d]
        (fn [path-str]
          (->> path-str
               -parse-path
               (map (fn [el]
                      (if (= 2 (count el))
                        (let [[x y] el]
                          [(int (- x dx)) (int (- y dy))])
                        (let [[name x y] el]
                          [name (int (- x dx)) (int (- y dy))]))))
               -unparse-path))))
     paths)))

(defn serve-resource
  [name content-type]
  (fn [req]
    {:status 200
     :headers {"Content-Type" (str content-type "; charset=utf-8")}
     :body (slurp (io/resource name))}))

(defn index
  [request]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (hic/html [:html
                    [:head
                     [:script {:src "/js/hexs.js" :type "application/javascript"}]
                     [:link {:href "/css/hexs.css" :media "screen"
                             :rel "stylesheet" :type "text/css"}]]
                    [:body [:div {:id "app"} "Hello!"]]])})

(def routes
  ["" [["" index]
       ["/" index]
       ["/js/hexs.js" (serve-resource "hexs.js" "application/javascript")]
       ["/css/hexs.css" (serve-resource "hexs.css" "text/css")]]])

;;;;;;;;;; CLI Interface
(def cli-options
  [["-p" "--port PORT" "Specify a port to start the server on"]])

(defn -main
  [& args]
  (let [parsed (parse-opts args cli-options)]
    (server/start
     routes
     (edn/read-string
      (get-in parsed [:options :port] "4040")))))
