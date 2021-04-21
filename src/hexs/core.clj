(ns hexs.core
  (:require [clojure.java.io :as io]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.edn :as edn]
            [hiccup.core :as hic]
            [bidi.ring :as bring]

            [hexs.server :as server]))

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
