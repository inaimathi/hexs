(ns hexs.model
  (:require [clojure.string :as str]))

(def OFFs
  [[-1 1 0] [-1 0 1]
   [0 -1 1] [1 -1 0]
   [0 1 -1] [1 0 -1]])

(defn neighborhood-points [point & {:keys [radius] :or {radius 1}}]
  (let [offs (if (= 1 radius)
               OFFs
               (disj
                (reduce
                 (fn [memo _]
                   (set (mapcat neighborhood-points memo)))
                 (set OFFs) (range (dec radius)))
                point))]
    (vec (map #(vec (map + %1 %2)) offs (repeat point)))))

(defn neighborhood-of [grid point & {:keys [radius] :or {radius 1}}]
  (->> (neighborhood-points point :radius radius)
       (select-keys grid)))

;; Not sure if we even need these if we're implementing a grid as {[Int Int Int] -> Space}, but lets keep them around for now?
(defn empty-grid [& {:keys [radius] :or {radius 0}}]
  (let [zero [0 0 0]]
    (if (zero? radius) {}
        (assoc
         (reduce
          (fn [memo space] (assoc memo space nil)) {}
          (neighborhood-points zero :radius radius))
         zero nil))))

(defn grid-insert [grid point space] (assoc grid point space))
(defn grid-slice [grid points] (select-keys grid points))
(defn grid-merge [& grids] (apply merge grids))
