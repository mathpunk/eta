(ns eta.transforms
  (:require [java-time :as time]
            [eta.pinboard.pin]
            [clojure.spec.alpha :as s]
            [clojure.set :refer [rename-keys]]
            [clojure.string :as string]))

;; Time and Date
;; =================
(defn- string-is-pattern [re s]
  (= (re-find re s) s))

(def simple-date-pattern #"\d\d\d\d-\d\d-[0123]\d")

(s/def ::simple-date-code
  #(string-is-pattern simple-date-pattern %))

(defn str->date [s]
  (if (s/valid? ::simple-date-code s)
    (time/local-date "yyyy-MM-dd" s)
    (time/offset-date-time s)))

#_(str->date "2015-01-23")

#_(str->date "2012-11-14T05:44:51Z")


;; Strings and Tags
;; ====================
(defn str->tags [tag-string]
  (let [separated (string/split tag-string #",")
        tags (map string/trim separated)]
    (set tags)))


;; Pins
;; ===============
(def pin-keymap
  {"extended" :pinboard.pin/extended
   "description" :pinboard.pin/description
   "time" :pinboard.pin/pinned-at
   "href" :pinboard.pin/href
   "shared" :pinboard.pin/shared
   "tags" :pinboard.pin/tags})

(defn transform-pin [input-pin]
  (-> input-pin
      (rename-keys pin-keymap)
      (update-in [:pinboard.pin/href] #(java.net.URI. %))
      (update-in [:pinboard.pin/tags] str->tags)
      (update-in [:pinboard.pin/pinned-at] str->date)
      (update-in [:pinboard.pin/shared] #(if (= "yes" %) true false))
      (dissoc "hash" "meta")

      ))


(def sample {"extended" "I'm havingfuntrying to imagine the Cyborg-Historical /c The network reveals. — ", "shared" "yes", "hash" "8a78c6f32579cb4a65a7dd94a8582387", "tags" "", "href" "http://yes.thatcan.be/my/next/tweet/", "time" "2012-11-14T05:44:51Z", "meta" "e2a06ea74ba8d8644be57b4dc4cb9ea7", "description" "That can be my next tweet", "toread" "no"})

(transform-pin sample)

(s/valid? :pinboard.pin/entity (transform-pin sample))




