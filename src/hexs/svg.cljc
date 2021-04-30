(ns hexs.svg
  (:require [clojure.string :as str]))

(defn transform [& {:keys [scale translate rotate]}]
  (let [scale (when scale (str "scale(" scale ")"))
        translate (when translate
                    (let [[trans-x trans-y] translate]
                      (str "translate(" trans-x ", " trans-y ")")))
        rotate (when rotate
                 (str "rotate(" rotate ")"))
        ts (remove nil? [scale translate rotate])
        transform (when (not (empty? ts)) (str/join ", " ts))]
    transform))

(defn ->hic [svg-tree] svg-tree)
