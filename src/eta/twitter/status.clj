(ns eta.twitter.status
  (:require [twitter.api.restful :as api]
            [eta.twitter.core :refer [query]]))

(defn status [id]
  (first (query api/statuses-lookup :id id)))


(-> (status 957651298991706112)
    :text)
