(ns eta.pinboard.persist
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [java-time :as time]))

(defn batch-directory [batch]
  (let [base "./extract/pinboard"
        tokens (string/split (batch :index) #"-")]
    (string/join "/" (cons base tokens))))

(defn pin-filename [pin]
  (str (pin :pinboard.pin/hash) ".edn"))

(defn batch-paths [batch]
  (let [dir (batch-directory batch)]
    (map #(str dir "/" (pin-filename %)) (batch :items))))

(defn prepare-writes [batch]
  (zipmap (batch-paths batch) (batch :items)))

(defn write-batch [batch]
  (let [path-data-pairs (prepare-writes batch)]
    (io/make-parents (first (first path-data-pairs)))
    (map #(apply spit %) path-data-pairs)))

(defn pin-directory [pin]
  (let [base "./extract/pinboard"
        date (->> pin
                  :pinboard.pin/pinned-at
                  second
                  (time/format "yyyy/MM/dd"))
        name (str (pin :pinboard.pin/hash) ".edn")]
    (string/join "/" [base date name] )))

(defn write [pin]
  (let [dir (pin-directory pin)]
    (io/make-parents dir)
    (spit dir pin)))
