(ns eta.specify
  (:require [clojure.spec.alpha :as s]))

(defn conform!
  "Like s/conform, but throws an error with s/explain-data on failure."
  ([spec x]
   (conform! spec x ""))
  ([spec x msg]
   (let [conformed (s/conform spec x)]
     (if (= :clojure.spec.alpha/invalid conformed)
       (throw (ex-info (str "Failed to conform " spec ", see ex-data")
                       {:data (s/explain-data spec x)
                        :value x}))
       conformed))))
