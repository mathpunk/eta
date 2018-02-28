(ns eta.pinboard.api
  (:require [eta.extract.auth :refer [pinboard-credentials]]
            [com.rpl.specter :refer [transform MAP-VALS MAP-KEYS]]
            [eta.transform :as xform]
            [clj-http.client :as client]
            [cheshire.core :as json]))

(def auth-token (pinboard-credentials :auth-token))

(def base-uri "https://api.pinboard.in/v1/")

(defn call
  ([method]
   (let [uri (str base-uri method)
         params {"auth_token" auth-token, "format" "json"}]
     (-> (client/get uri {:query-params params})
         ;; check status
         ;; handle rate limits
         ;; handle errors
         :body
         json/decode)))
  ([method args]
   (let [uri (str base-uri method)
         default-params {"auth_token" auth-token, "format" "json"}
         params (merge args default-params)]
     (-> (client/get uri {:query-params params})
         ;; check status
         ;; handle rate limits
         :body
         json/decode))))


;; Rate limiting
;; -----------------
;; Make sure your API clients check for 429 Too Many Requests server errors and back off appropriately. If possible, keep doubling the interval between requests until you stop receiving errors.


;; Error Handling
;; -------------------
;; The Pinboard API does its best to mimic the behavior Delicious API. If something goes wrong, you'll get the mysterious:

;; <result code="something went wrong" />

;; If an action succeeds, you'll get:

;; <result code="done" />

;; or their JSON equivalents.



(defn post-counts-by-date []
  (->> (get (call "posts/dates") "dates")
       (transform [MAP-VALS] #(Integer. %))
       (transform [MAP-KEYS] xform/str->date)
       sort))

(defn posts-on-date [date]
  (let [posts (get (call "posts/get" {"dt" (str date)}) "posts")]
    {:date (str date)
     :posts posts}))

(defn all-posts
  "This finishes within a minute or so. You can only call it every five minutes by Pinboard.in policy."
  []
  (call "posts/all"))

#_(defn posts-since-date
    "Hey why doesn't this work"
    [date]
    (get (api/call "posts/all" {"fromdt" (str date)}) "posts"))
