(ns eta.pinboard.batch
  (:require [java-time :as time]
            [clojure.core.match :refer [match]]
            [cheshire.core :as json]
            [eta.pinboard.shape :as shape]
            [eta.pinboard.api :as api]
            [clojure.set :as set]))

(def indexes (keys (api/post-counts-by-date)))

(def counts (api/post-counts-by-date))

(defn fetch [date]
  (let [result (api/get-batch-with-status date)]
    (if (= 200 (result :status))
      [:success {:date date
                 :posts (-> result
                            :body
                            (json/decode ,,, true)
                            :posts)}]
      [:failure result])))

(defn throw-api-failure [retrieval]
  (match retrieval
         [:success batch] batch
         [:failure response] (throw (ex-info
                                     (str "API call failed with status " (response :status))
                                     {:status (response :status)
                                      :reason (response :reason-phrase)
                                      :value response}))))

(defn fetch-and-conform! [index]
  (-> index
      fetch
      throw-api-failure
      (update :posts (fn [items] (map shape/shape! items)))
      (set/rename-keys {:date :index, :posts :items})))


#_(fetch-and-conform! "2012-11-14")
