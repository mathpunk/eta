(ns eta.pinboard.api
  (:require [cheshire.core :as json]
            [clj-http.client :as client]
            [com.rpl.specter :refer [MAP-VALS transform]]
            [eta.auth :refer [pinboard-credentials]]))

(def auth-token (pinboard-credentials :auth-token))

(def base-uri "https://api.pinboard.in/v1/")

(defn get-posts
  ([]
   (let [method "posts/get"
         uri (str base-uri method)
         params {"auth_token" auth-token "format" "json"}]
     (-> (client/get uri {:query-params params})
         :body
         json/decode
         (get "posts"))))
  ([date]
   (let [method "posts/get"
         uri (str base-uri method)
         params {"auth_token" auth-token "format" "json"}]
     (-> (client/get uri {:query-params (merge params {"dt" date})})
         :body
         json/decode
         (get "posts")))))

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
         json/decode true))))

(defn get-batch-with-status [date]
  (let [method "posts/get"
        uri (str base-uri method)
        params {"auth_token" auth-token "format" "json"}]
    (client/get uri {:query-params (merge params {"dt" date})})))

(defn recent-posts []
  (-> (call "posts/recent")
      (get "posts")))

(defn post-counts-by-date []
  (->> (get (call "posts/dates") "dates")
       (transform [MAP-VALS] #(Integer. %))
       sort))

(defn all-posts
  "This finishes within a minute or so. You can only call it every five minutes by Pinboard.in policy."
  []
  (call "posts/all"))

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
