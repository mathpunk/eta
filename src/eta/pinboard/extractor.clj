(ns eta.pinboard.extractor
  (:require [com.rpl.specter :refer [transform MAP-VALS MAP-KEYS]]
            [eta.pinboard.api :as api]
            [eta.transforms :as xforms]))

(defn post-counts-by-date []
  (->> (get (api/call "posts/dates") "dates")
       (transform [MAP-VALS] #(Integer. %))
       (transform [MAP-KEYS] xforms/str->date)
       sort))

(defn posts-on-date [date]
  (get (api/call "posts/get" {"dt" (str date)}) "posts"))

(-> (posts-on-date "2017-01-12")
    first
    )

#_(defn posts-since-date
    "Hey why doesn't this work"
    [date]
    (get (api/call "posts/all" {"fromdt" (str date)}) "posts"))

(def ^:dynamic *writer* prn)


;;;;;;;;;;;;;;;;;;;;;;;



(def post-counts (post-counts-by-date))

(first (keys post-counts))

(def first-day "2012-11-13")

(posts-on-date "2012-11-14")

(defn extract-one []
  (let [items (posts-on-date "2012-11-14")]
    (*writer* (first items))))

(extract-one)

(defn validate-one []
  (let [items (posts-on-date "2012-11-14")
        item (first items)]
    clojure.edn/read item))

(validate-one)

