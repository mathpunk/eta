(ns eta.pinboard.extractor
  (:require [eta.pinboard.api :as api]
            [java-time :as time]
            [eta.transforms :as xforms]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]))

#_(defonce (atom {:pin-index 0 :error-code nil}))

#_(defn all []
    (api/all-posts))

(def sample {"extended" "I'm havingfuntrying to imagine the Cyborg-Historical /c The network reveals. — ", "shared" "yes", "hash" "8a78c6f32579cb4a65a7dd94a8582387", "tags" "", "href" "http://yes.thatcan.be/my/next/tweet/", "time" "2012-11-14T05:44:51Z", "meta" "e2a06ea74ba8d8644be57b4dc4cb9ea7", "description" "That can be my next tweet", "toread" "no"})

(map type (vals sample ))

(xforms/transform-pin sample)

(s/valid? :pinboard.pin/entity (xforms/transform-pin sample))

(defn pin-data [input-pin]
  (xforms/transform-pin input-pin))

(defn unique-id [pin] (pin "hash"))

(defn changed-id [pin] (pin "meta"))

(defn pin-filename [pin]
  (let [date-tokens (map str (-> pin
                                 :pinboard.pin/pinned-at
                                 (time/as :year :month-of-year :day-of-month)))
        path-tokens (concat '("pinboard")
                            date-tokens
                            `(~(unique-id pin) ~(changed-id pin)))]
    (str (string/join "/" path-tokens) ".edn")))


(-> sample
    transform-pin
    pin-filename)


(defn write-pin [pin]
  (let [filename (pin-data-filename pin)]
    (do
      (io/make-parents filename)
      #_(spit filename pin))))

#_(spit (pin-data-filename sample-post) sample-post)

#_(write-pin sample-post)

#_(def first-day "2012-11-13")

#_(def first-day-with-several "2012-11-14")


#_(defn exfiltrate []
    (let [dates (api/post-counts-by-date)]
      ;; check count of files
      (let [persisting (take 1 (keys dates))]
        )
      ))

;; decide on batch
;;   get date
;; get posts by date

;; or... get all?



#_(def sample-post (-> (api/posts-on-date "2012-11-14")
                       :posts
                       first))

#_sample-post
