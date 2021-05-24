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

(defn round-to
  ([num] (round-to num 2))
  ([num digits]
   (let [d (Math/pow 10 digits)]
     (/ (Math/round (* num d)) d))))

(defn drop-but [n coll]
  (drop (- (count coll) n) coll))

(defn update-inf [m ks f]
  (if (get-in m ks) (update-in m ks f) m))

(defn updatef
  ([m k f] (if (get m k) (update m k f) m))
  ([m k f & kf-pairs]
   (reduce
    (fn [memo [k f]] (updatef memo k f))
    m (partition 2 (cons k (cons f kf-pairs))))))

(defn always [return-value] (fn [& _] return-value))
