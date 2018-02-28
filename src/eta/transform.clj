(ns eta.transform
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


;; Strings and Tags
;; ====================
(defn space-separate-tags [tag-string]
  (let [separated (string/split tag-string #"\s+")
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
      (update-in [:pinboard.pin/tags] space-separate-tags)
      (update-in [:pinboard.pin/pinned-at] str->date)
      (update-in [:pinboard.pin/shared] #(if (= "yes" %) true false))
      ))

