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
  viewport
  (r/atom {:offset [20 20]
           :dragging false
           :drag-origin [0 0]}))

(defonce
  selection
  (r/atom #{}))

(defonce
  game-state
  (r/atom
   {:grid
    (assoc
     (grid/empty {} :radius 8)
     [0 0 0]
     {:units [:player]})
    :units {:player {:id :player :position [0 0 0]}}}))

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
       (let [src (get-in state [:units name :position])]
         (-> state
             (update-in [:grid src :units] #(vec (remove (fn [s] (= s name)) %)))
             (update-in [:grid dest :units] #(vec (conj % name)))
             (assoc-in [:units name :position] dest)))))))

(defn handle-space-click! [[x y z]]
  (move-sprite! :player [x y z]))
(defn handle-sprite-click! [sprite-id]
  (swap! selection
         #(if (contains? % sprite-id)
            (disj % sprite-id)
            (conj % sprite-id))))

(defn units [[x y z] units]
  (->> units
       (map
        (fn [unit-id]
          (let [unit (sprites/alien
                      :id (name unit-id) :class (name unit-id) :transform (svg/=>t {:translate [5 -25]})
                      :click #(handle-sprite-click! unit-id))]
            (if (contains? @selection unit-id)
              [:g
               [:animate
                {:attributeType "CSS" :attributeName "opacity"
                 ;; :from "1" :to "0.5"
                 :dur "2s" :repeatCount "indefinite"
                 :keyTimes "0;0.5;1" :values "1;0.5;1"}]
               ;; [:animateTransform
               ;;  {:attributeName "transform"
               ;;   :type "translate"
               ;;   :from "0, 0" :to "150, 20"
               ;;   ;; :values "0 0;0 -50;0 0;0 50;0 0"
               ;;   ;; :keyTimes "0;0.25;0.5;0.75;0.9;1"
               ;;   :dur "1s" :repeatCount "0"}]
               unit]
              unit))))
       (cons :g)
       vec))

(defn hex [[x y z] & {:keys [space pointy? scale translate] :or {pointy? nil space nil}}]
  (let [pointy? (if (nil? pointy?) true (not (not pointy?)))
        [dx dy] (-space-layout pointy? x y z)
        cur [x y z]]
    (let [[vx vy] (:offset @viewport)
          transform (svg/=>t {:scale 0.4 :translate [(+ (* dx 34) vx) (+ (* dy 30) vy)]})]
      ((sprites/=s
        (sprites/pointy-space)
        [units [x y z] (:units space)])
       :transform transform
       ;; :mouse-enter #(.log js/console (str "ENTERING" [x y z]))
       ;; :mouse-move #(.log js/console (str "MOVING AT" [x y z]))
       ;; :mouse-leave #(.log js/console (str "LEAVING" [x y z]))
       :click #(handle-space-click! [x y z])))))

(defn grid->svg [grid & {:keys [pointy?] :or {pointy? false}}]
  (->> grid
       (grid/map
        (fn [coords space]
          [hex coords :space space :pointy? pointy?]))
       (cons {:transform (svg/=>t {:translate [170 140]})})
       (cons :g)
       vec))

(defn game []
  [:div {}
   [:svg {:xmlns "http://www.w3.org/2000/svg" :viewBox "0 0 841.9 595.3"}
    ((sprites/=s
      (grid->svg (:grid @game-state) :pointy? true)
      [:circle :cx "420.69" :cy "296.5" :r "45.7"]
      [:path {:d "M520.5 78.1z"}])
     :mouse-down #(swap!
                   viewport
                   (fn [v]
                     (assoc v :dragging true
                            :drag-origin [(.-clientX %) (.-clientY %)])))
     :mouse-move #(when (:dragging @viewport)
                    (swap!
                     viewport
                     (fn [v]
                       (let [[ox oy] (:drag-origin v)
                             [x y] [(.-clientX %) (.-clientY %)]
                             dx (- x ox)
                             dy (- y oy)
                             [offx offy] (:offset v)]
                         (assoc
                          v :offset [(+ offx dx) (+ offy dy)]
                          :drag-origin [x y])))))
     :mouse-up #(swap! viewport (fn [v] (assoc v :dragging false))))]])

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
