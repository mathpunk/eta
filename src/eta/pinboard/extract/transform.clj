(ns eta.pinboard.extract.transform
  (:require [java-time :as time]
            [com.rpl.specter :refer [ transform ]]
            [clojure.string :as string]
            [clojure.spec.alpha :as s]))

;; Time and Date
;; =================
(defn- string-is-pattern [re s]
  (= (re-find re s) s))

(def simple-date-pattern #"\d\d\d\d-\d\d-[0123]\d")

(s/def ::simple-date-code
  #(string-is-pattern simple-date-pattern %))

(defn string->date [s]
  (if (s/valid? ::simple-date-code s)
    (time/local-date "yyyy-MM-dd" s)
    (time/offset-date-time s)))


;; Strings and Tags
;; ====================
(defn space-separate-tags [tag-string]
  (let [separated (string/split tag-string #"\s+")
        tags (map string/trim separated)]
    (set tags)))
