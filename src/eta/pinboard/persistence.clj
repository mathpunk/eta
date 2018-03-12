(ns eta.pinboard.persistence
  (:require [clojure.string :as string]
            [java-time :as time]
            [clojure.java.io :as io]))


(def ^:dynamic *persistence-target* "./resources/pinboard")

(defn filename [pin]
  (let [pinned-at (pin :pinboard.pin/pinned-at)
        date-parts (string/split
                    (time/format "yyyy-MM-dd" pinned-at) #"-")
        unique-id (pin :pinboard.pin/hash)
        path-parts (cons *persistence-target*
                         (conj date-parts
                               (str unique-id ".edn")))]
    (string/join "/" path-parts)))

(defn write [pin]
  (let [filename (filename pin)]
    (io/make-parents filename)
    (spit filename pin)
    filename))
