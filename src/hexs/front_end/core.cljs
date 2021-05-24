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

(defonce
  selection
  (r/atom []))

(defonce
  game-state
  (r/atom
   {:grid
    (assoc
     (grid/empty {} :radius 8)
     [0 0 0]
     {:units [:player]})
    :units {:player [0 0 0]}}))

(defn updates-in [m ks f & ks+fs]
  (assert (even? ks+fs) "You must pass an even number of key/function pairs")
  (reduce
   (fn [memo [ks f]]
     (update-in memo ks f))
   m (cons ks (cons f ks+fs))))

(defn move-sprite! [name [x y z]]
  (let [dest [x y z]]
    (swap!
     game-state
     (fn [state]
       (let [src (get-in state [:units name])]
         (-> state
             (update-in [:grid src :units] #(vec (remove (fn [s] (= s name)) %)))
             (update-in [:grid dest :units] #(vec (conj % name)))
             (assoc-in [:units name] dest)))))))

(defn handle-space-click! [] nil)
(defn handle-sprite-click! [] nil)

(defn units [[x y z] units]
  (when (contains? (set units) :player)
    (sprites/alien :class "player" :transform (svg/unparse-transform {:translate [5 -25]}))))

(defn hex [[x y z] & {:keys [space pointy? scale translate] :or {pointy? nil space nil}}]
  (let [pointy? (if (nil? pointy?) true (not (not pointy?)))
        [dx dy] (-space-layout pointy? x y z)
        cur [x y z]]
    (let [transform (svg/unparse-transform
                     {:scale 0.3
                      :translate [(+ (* dx 35) 20) (+ (* dy 35) 20)]})]
      [:g {:transform transform
           :onMouseEnter #(.log js/console (str "ENTERING" [x y z]))
           :onMouseMove #(.log js/console (str "MOVING AT" [x y z]))
           :onMouseLeave #(.log js/console (str "LEAVING" [x y z]))
           :on-click #(move-sprite! :player [x y z])}()
       (sprites/pointy-space)
       [units [x y z] (:units space)]])))

(defn grid->svg [grid & {:keys [pointy?] :or {pointy? false}}]
  (->> grid
       (grid/map
        (fn [coords space]
          [hex coords :space space :pointy? pointy?]))
       (cons {:transform (svg/unparse-transform {:translate [170 140]})})
       (cons :g)
       vec))

(defn game []
  [:div {}
   [:svg {:xmlns "http://www.w3.org/2000/svg" :viewBox "0 0 841.9 595.3"}
    [:g
     (grid->svg (:grid @game-state) :pointy? true)
     [:circle :cx "420.69" :cy "296.5" :r "45.7"]
     [:path {:d "M520.5 78.1z"}]]]])

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
