(ns eta.transforms
  (:require [clj-time.core :as time]
            [clojure.string :as string]))

(defn str->date [date-string]
  (let [tokens (string/split date-string #"-")
        integers (map #(Integer. %) tokens)]
    (apply time/local-date integers)))

