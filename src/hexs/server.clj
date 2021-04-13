(ns hexs.server
  (:require [org.httpkit.server :as server]
            [bidi.ring :as bring]
            [ring.middleware.params :refer [wrap-params]]))

(defonce +server+ (atom nil))

(defn start
  [routes port]
  (reset! +server+
          (server/run-server
           (-> routes
               bring/make-handler
               wrap-params)
           {:port port :thread 16}))
  (println "Started..."))

(defn stop
  []
  (when-let [s @+server+]
    (s :timeout 100)
    (reset! +server+ nil)
    (println "Stopped...")))

(defn restart
  [routes port]
  (stop)
  (start routes port))
