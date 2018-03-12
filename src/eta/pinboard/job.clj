(ns eta.pinboard.job
  (:require [eta.pinboard.shape :as shape]
            [eta.pinboard.persistence :as persist]
            [eta.pinboard.batch :as batch]))


(defonce batch (batch/small-batch))

(def job
  (comp
   persist/write
   #_shape/validate-pin
   shape/shape-pin))

(defn run []
  (map job batch))


(->> (run)
     first)





;; validate
;; throw ui
;; make-durable

