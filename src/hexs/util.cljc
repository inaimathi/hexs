(ns hexs.util)

(defn -tap [label v]
  (println label v)
  v)

(defn abs
  "(abs n) is the absolute value of n"
  [n]
  (cond
    (not (number? n)) (throw (IllegalArgumentException.
                              "abs requires a number"))
    (neg? n) (- n)
    :else n))

(defn always [return-value] (fn [& _] return-value))
