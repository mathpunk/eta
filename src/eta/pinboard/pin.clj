(ns eta.pinboard.pin
  (:require [eta.type]
            [clojure.spec.alpha :as s]))

(s/def :pinboard.pin/description
  (s/or :text :eta.type/freetext :readable false?))
(s/def :pinboard.pin/extended
  (s/or :text :eta.type/freetext :readable false?))
(s/def :pinboard.pin/href
  (s/or :uri :eta.type/uri :bad-uri string?))
(s/def :pinboard.pin/tags :eta.type/tag-set)
(s/def :pinboard.pin/pinned-at :eta.type/timestamp)
(s/def :pinboard.pin/shared boolean?)

(s/def :pinboard.pin/entity
  (s/keys :req [:pinboard.pin/href
                :pinboard.pin/tags
                :pinboard.pin/description
                :pinboard.pin/extended
                :pinboard.pin/shared
                :pinboard.pin/pinned-at]))
