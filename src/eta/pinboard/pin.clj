(ns eta.pinboard.pin
  (:require [eta.type]
            [clojure.spec.alpha :as s]))

(s/def :pinboard.pin/href :eta.type/uri)
(s/def :pinboard.pin/tags :eta.type/tag-set)
(s/def :pinboard.pin/description :eta.type/freetext)
(s/def :pinboard.pin/extended :eta.type/freetext)
(s/def :pinboard.pin/shared boolean?)
(s/def :pinboard.pin/pinned-at :eta.type/timestamp)

(s/def :pinboard.pin/entity
  (s/keys :req [:pinboard.pin/href
                :pinboard.pin/tags
                :pinboard.pin/description
                :pinboard.pin/extended
                :pinboard.pin/shared
                :pinboard.pin/pinned-at
                ]))
