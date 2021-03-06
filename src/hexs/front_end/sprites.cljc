(ns hexs.front-end.sprites)

(defn =s [& elems]
  (fn [& {:keys [id class transform click mouse-move mouse-enter mouse-leave mouse-up mouse-down drag]}]
    (let [opts (reduce
                (fn [memo [k v]] (if v (assoc memo k v) memo))
                {} (partition 2 [:id id
                                 :class class
                                 :transform transform
                                 :ondrag drag
                                 :on-click click
                                 :onMouseDown mouse-down
                                 :onMouseUp mouse-up
                                 :onMouseMove mouse-move
                                 :onMouseEnter mouse-enter
                                 :onMouseLeave mouse-leave]))]
      (->> elems (cons opts) (cons :g) vec))))

(def pointy-space
  (=s
   [:path
    {:d "M-33 3 L-33 -28 Q-16 -36 0 -45 L32 -28 32 3 0 20 -33 3",
     :fill "#8DC435",
     :stroke "none"}]
   [:path
    {:d
     "M32 5 L32 16 26 12 21 22 15 18 10 27 4 23 0 33 -5 23 -11 27 -16 18 -22 22 -27 12 -33 16 -33 5 0 22 32 5",
     :fill "#80BE1F",
     :stroke "none"}]
   [:path
    {:d
     "M32 16 L32 27 0 44 -33 27 -33 16 -27 12 -22 22 -16 18 -11 27 -5 23 0 33 4 23 10 27 15 18 21 22 26 12 32 16",
     :fill "#C58F5C",
     :stroke "none"}]
   [:path
    {:d "M32 3 L32 5 0 22 -33 5 -33 3 0 20 32 3",
     :fill "#A6D162",
     :stroke "none"}]
   [:path
    {:d "M-33 5 L0 22 0 44 -33 27 -33 5",
     :fill-opacity "0.10",
     :fill "#000000",
     :stroke "none"}]))

(def alien
  (=s
   [:path
    {:d
     "M9 16 Q15 19 15 23 15 27 9 30 4 32 -2 32 -9 32 -14 30 -20 27 -20 23 -20 19 -14 16 -9 13 -2 13 4 13 9 16",
     :fill-opacity "0.050980392156862744",
     :fill "#000000",
     :stroke "none"}]
   [:path
    {:d
     "M6 9 Q7 10 7 11 L6 22 Q4 24 1 22 L0 11 Q0 10 0 9 2 7 3 8 5 7 6 9 M-4 9 Q-2 9 -2 10 -1 11 -2 13 L-6 23 Q-8 26 -10 23 L-8 13 Q-8 11 -7 10 -6 9 -4 9",
     :fill "#6FC4A9",
     :stroke "none"}]
   [:path
    {:d
     "M-13 -15 Q-13 -20 -9 -24 -5 -28 0 -28 5 -28 9 -24 13 -20 13 -15 L13 9 Q13 13 9 15 5 17 0 17 -5 17 -9 15 -13 13 -13 9 L-13 -15",
     :fill "#6FC4A9",
     :stroke "none"}]
   [:path
    {:d
     "M8 -5 Q7 -5 6 -5 5 -6 5 -8 5 -9 6 -10 7 -11 8 -11 9 -11 10 -10 11 -9 11 -8 11 -6 10 -5 9 -5 8 -5 M7 -2 L6 -1 Q4 0 3 0 1 0 0 -1 -1 -2 -1 -2 -1 -3 -1 -3 L0 -3 0 -3 0 -3 Q0 -2 0 -2 1 -1 3 -1 4 -1 5 -2 L6 -3 6 -3 Q6 -3 6 -3 L7 -3 7 -2 M-2 -5 Q-3 -5 -4 -5 -5 -6 -5 -8 -5 -9 -4 -10 -3 -11 -2 -11 -1 -11 0 -10 0 -9 0 -8 0 -6 0 -5 -1 -5 -2 -5",
     :fill "#347E67",
     :stroke "none"}]
   [:path
    {:d
     "M10 4 L10 0 10 0 Q17 2 17 9 17 10 16 10 16 11 15 11 15 11 14 10 14 10 14 9 14 4 10 4 M-12 4 Q-16 4 -16 9 -16 10 -16 10 -17 11 -17 11 -18 11 -18 10 -19 10 -19 9 -19 2 -12 0 L-12 0 -12 4",
     :fill "#6FC4A9",
     :stroke "none"}]
   [:path
    {:d
     "M-19 -13 Q-19 -21 -14 -27 -8 -33 0 -33 8 -33 14 -27 19 -21 19 -13 19 -4 14 0 8 6 0 6 -8 6 -14 0 -19 -4 -19 -13 M-11 -25 Q-16 -20 -16 -13 -16 -6 -11 -1 -6 3 0 3 6 3 11 -1 16 -6 16 -13 16 -20 11 -25 6 -30 0 -30 -6 -30 -11 -25",
     :fill "#FFFFFF",
     :stroke "none"}]
   [:path
    {:d
     "M-11 -25 Q-6 -30 0 -30 6 -30 11 -25 16 -20 16 -13 16 -6 11 -1 6 3 0 3 -6 3 -11 -1 -16 -6 -16 -13 -16 -20 -11 -25 M11 -23 Q9 -25 7 -25 4 -25 2 -23 1 -21 1 -19 1 -17 2 -15 4 -13 7 -13 9 -13 11 -15 12 -17 12 -19 12 -21 11 -23",
     :fill-opacity "0.2",
     :fill "#FFFFFF",
     :stroke "none"}]
   [:path
    {:d
     "M11 -23 Q12 -21 12 -19 12 -17 11 -15 9 -13 7 -13 4 -13 2 -15 1 -17 1 -19 1 -21 2 -23 4 -25 7 -25 9 -25 11 -23",
     :fill-opacity "0.30196078431372547",
     :fill "#FFFFFF",
     :stroke "none"}]
   [:path
    {:d
     "M0 11 Q0 10 1 10 2 10 3 11 3 12 3 13 3 14 3 14 2 15 1 15 0 15 0 14 -1 14 -1 13 -1 12 0 11 M9 7 Q10 7 10 8 10 9 9 9 9 10 8 10 7 10 7 9 6 9 6 8 6 7 7 7 7 6 8 6 9 6 9 7 M7 13 Q7 13 6 13 6 13 5 13 5 12 5 12 5 11 5 11 6 11 6 11 7 11 7 11 8 11 8 12 8 12 7 13",
     :fill-opacity "0.5019607843137255",
     :fill "#8BCFBA",
     :stroke "none"}]))
