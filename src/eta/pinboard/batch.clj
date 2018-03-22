(ns eta.pinboard.batch
  (:require [cheshire.core :as json]
            [clojure.core.match :refer [match]]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [eta.pinboard.api :as api]))

(def indexes (keys (api/post-counts-by-date)))

(def counts (api/post-counts-by-date))

(defn fetch [date]
  (let [result (api/get-batch-with-status date)]
    (if (= 200 (result :status))
      [:success {:date date
                 :posts (-> result
                            :body
                            (json/decode true)
                            :posts)}]
      [:failure result])))

(defn fetch! [index]
  (let [retrieval (fetch index)]
    (match retrieval
           [:success batch] batch
           [:failure response] (throw (ex-info
                                       (str "API call failed with status " (response :status))
                                       {:status (response :status)
                                        :reason (response :reason-phrase)
                                        :value response})))))

(def snapshot-all-as-lines
  "Having used curl to download a json file of all of the pins, this provides a sequence of lines of that file. One line corresponds to a record. The first and last record viewed as lines are not valid json, because they have the initial `[` and terminal `]`."
  (line-seq (io/reader (io/resource "pinboard_all.json"))))

(defn object-stringify-first [line]
  (string/join (drop 1 line)))

(defn object-stringify-last [line]
  (string/join (drop-last 2 line)))

(def snapshot-all
  (map #(json/decode % true)
       (-> snapshot-all-as-lines
           vec
           (update 0 object-stringify-first)
           (update (- (count snapshot-all-as-lines) 1) object-stringify-last))))
