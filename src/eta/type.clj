(ns eta.type
  (:require [clojure.string :as string]
            [clojure.spec.alpha :as s]))

;; Tags and sets of them
(s/def ::tag
  (s/and string?
         #(not (string/includes? % ","))))
(s/def ::tag-set (s/coll-of ::tag))

