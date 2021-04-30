(ns hexs.front-end.sprites)

(defn pointy-space [& {:keys [class transform]}]
  [:g {:class class :transform transform}
   [:path
    {:d "M-22 14 L-22 -17 -5 -25 10 -34 L43 -17 43 14 10 31 -22 14",
     :fill "#8DC435",
     :stroke "none"}]
   [:path
    {:d
     "M43 16 L43 27 37 23 32 33 26 29 21 38 15 34 10 44 5 34 0 38 -5 29 -11 33 -16 23 -22 27 -22 16 10 33 43 16",
     :fill "#80BE1F",
     :stroke "none"}]
   [:path
    {:d
     "M43 27 L43 38 10 55 -22 38 -22 27 -16 23 -11 33 -5 29 0 38 5 34 10 44 15 34 21 38 26 29 32 33 37 23 43 27",
     :fill "#C58F5C",
     :stroke "none"}]
   [:path
    {:d "M43 14 L43 16 10 33 -22 16 -22 14 10 31 43 14",
     :fill "#A6D162",
     :stroke "none"}]])
