(ns eta.pinboard.job
  (:require [eta.pinboard.batch :as batch]
            [eta.pinboard.persist :as persist]
            [eta.pinboard.shape :as shape]
            [java-time :as time]))

(defn log [pin]
  (let [pinned-at (second (get pin :pinboard.pin/pinned-at))]
    (println "Pinned" (time/format "yyyy/MM/dd, hh:mm:ss" pinned-at)))
  pin)

#_(def ^:dynamic *extract-location* "./extract/pinboard")

(def extract-strings-to-flat-files
  "Given a sequence of pins (as strings), shape them into objects (throwing in the event of validation failure) and write them to directories based on dates. Note: I'm still assuming that there are no pins with the same hash and different metas, which I'm not convinced is 100% true."
  (comp persist/write log shape/shape!))

(defn run [job pins]
  (map job pins))

(defn extract-snapshot []
  (run extract-strings-to-flat-files batch/snapshot-all))

#_(extract-snapshot)
