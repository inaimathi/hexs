(ns hexs.front-end.core
  (:require [reagent.core :as r]
            [reagent.dom :as rd]))

(defonce click-count (r/atom 0))

(defn stateful-component []
  [:div {:on-click #(swap! click-count inc)}
   "I have been clicked [" @click-count "] times."])

(defn ^:export run []
  (rd/render [stateful-component]
             (js/document.getElementById "app")))

(.log js/console "HELLO FROM CLJS")

(defn on-load [callback]
  (.addEventListener
   js/window
   "DOMContentLoaded"
   callback))

(on-load
 (fn []
   (.log js/console "DOMContentLoaded callback")
   (run)))
