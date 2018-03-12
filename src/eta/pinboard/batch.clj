(ns eta.pinboard.batch
  (:require [java-time :as time]
            [eta.pinboard.api :as api]))

(defn today []
  (-> (time/local-date)
      (time/format)))

(defn small-batch []
  (api/get-posts (today)))

(defn large-batch []
  (api/all-posts))
