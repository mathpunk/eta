(ns eta.transforms
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [java-time :as time]))

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

;; Tags / Keywords
;; ======================
(defn space-separated->tags [tag-string]
  (let [separated (string/split tag-string #"\s+")
        tags (map string/trim separated)]
    (set tags)))

;; Booleans
;; =================
(s/def ::yes-or-no #{"yes" "no"})

(defn yn->bool [yn]
  (case yn
    "yes" true
    "no" false
    (throw (ex-info (str "Expected 'yes' or 'no', got something else:")
                    {:data (s/explain ::yes-or-no yn)
                     :value yn}))))
