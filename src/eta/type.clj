(ns eta.type
  (:require [clojure.string :as string]
            [clojure.spec.alpha :as s])
  (:import org.joda.time.DateTime))

;; Tags and sets of them
(s/def ::tag
  (s/and string?
         #(not (string/includes? % ","))))
(s/def ::tag-set (s/coll-of ::tag))

;; Freetext. Could be any string, but semantically, it's a little different. Wouldn't know how or whether to specify further.
(s/def ::freetext string?)

;; Timestamps. Instants should be okay, but sometimes I'll only have dates.
(s/def ::timestamp (s/or :instant inst?
                         :datetime #(= org.joda.time.DateTime (type %))
                         :date #(= org.joda.time.LocalDate (type %))))

;; "Links." What's the difference between an href, a uri, and a url?
(s/def ::uri uri?)

