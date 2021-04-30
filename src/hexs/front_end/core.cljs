(ns hexs.front-end.core
  (:require [clojure.string :as str]

            [reagent.core :as r]
            [reagent.dom :as rd]

            [hexs.grid :as grid]
            [hexs.svg :as svg]
            [hexs.front-end.sprites :as sprites]))

(defn -space-layout [pointy? x y z]
  (if pointy?
    [(* (- y x) 0.95) (* z 1.6)]
    [(* x 1.6) (* (- y z) 0.95)]))

(defonce current (r/atom {}))

(defn hex [[x y z] & {:keys [pointy? scale translate] :or {pointy? nil}}]
  (let [pointy? (if (nil? pointy?) true (not (not pointy?)))
        [dx dy] (-space-layout pointy? x y z)
        cur [x y z]]
    ;; [:polygon
    ;;  {:class (str "hex" (when (contains? (:line @current) cur) " lined"))
    ;;   :points (if pointy?
    ;;             "0,-1 -0.875,-0.5 -0.875,0.5 0,1 0.875,0.5 0.875,-0.5"
    ;;             "-1,0 -0.5,-0.875 0.5,-0.875 1,0 0.5,0.875 -0.5,0.875")
    ;;   :transform (svg/transform :scale 10 :translate [dx dy])
    ;;   :onMouseMove #(let [state @current]
    ;;                   (swap!
    ;;                    current
    ;;                    (fn [v]
    ;;                      (assoc
    ;;                       v :moved cur
    ;;                       :line (if (:clicked state) (set (grid/line (:clicked state) cur)) #{})))))
    ;;   :on-click #(swap! current (fn [s] (if (= (:clicked s) cur) (dissoc s :clicked) (assoc s :clicked cur))))}]
    (sprites/pointy-space :transform (svg/transform :scale 0.5 :translate [(+ (* dx 35) 200) (+ (* dy 35) 200)]))
    ))

(defn grid->svg [grid & {:keys [pointy?] :or {pointy? false}}]
  (->> grid
       (map (fn [[coords _]] [hex coords :pointy? pointy?]))
       (cons {:transform (svg/transform :translate [170 140])})
       (cons :g)
       vec))

(defn space []
  (hex :pointy? true :scale 10 :translate [10 10]))

(defn game []
  [:div {}
   [:div "The last thing clicked is " (str @current)]
   [:svg {:xmlns "http://www.w3.org/2000/svg" :viewBox "0 0 841.9 595.3"}
    [:g
     (grid->svg (grid/empty nil :radius 8) :pointy? true)
     [:circle :cx "420.69" :cy "296.5" :r "45.7"]
     [:path {:d "M520.5 78.1z"}]]]])

;; (defonce click-count 0)

;; (defn stateful-component []
;;   [:div {:on-click #(swap! click-count inc)}
;;    "I have been clicked [" @click-count "] times."])

(defn ^:export run []
  (rd/render
   [game]
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
