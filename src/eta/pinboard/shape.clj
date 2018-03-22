(ns eta.pinboard.shape
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [eta.specify :as spec]
            [eta.transforms :as xform])
  (:import [java.net URI URISyntaxException]))

(def input->output-map
  [[:extended    :pinboard.pin/extended    identity]
   [:description :pinboard.pin/description identity]
   [:hash        :pinboard.pin/hash        identity]
   [:meta        :pinboard.pin/meta        identity]
   [:tags        :pinboard.pin/tags        xform/space-separated->tags]
   [:href        :pinboard.pin/href        #(URI. %)]
   [:time        :pinboard.pin/pinned-at   xform/string->date]
   [:shared      :pinboard.pin/shared      xform/yn->bool]
   [:toread      :pinboard.pin/to-read     xform/yn->bool]])

(def key-map
  (let [ks (map first input->output-map)
        fqks (map second input->output-map)]
    (zipmap ks fqks)))

(def val-map
  (let [fqks (map second input->output-map)
        fns (map #(nth % 2) input->output-map)]
    (zipmap fqks fns)))

(defn shape-entry [pin field]
  (try
    (update pin field (get val-map field))
    (catch URISyntaxException e
      (let [bad-data (get pin field)]
        (-> (assoc pin :pinboard.pin/href bad-data))))))

(defn shape-pin [pin]
  (-> (set/rename-keys pin key-map)
      (shape-entry :pinboard.pin/extended)
      (shape-entry :pinboard.pin/description)
      (shape-entry :pinboard.pin/tags)
      (shape-entry :pinboard.pin/href)
      (shape-entry :pinboard.pin/hash)
      (shape-entry :pinboard.pin/pinned-at)
      (shape-entry :pinboard.pin/shared)
      (shape-entry :pinboard.pin/to-read)
      (dissoc :pinboard.pin/meta)))

(s/fdef shape-pin
        :ret :pinboard.pin/entity)

(defn shape! [input-pin]
  (->> input-pin
       shape-pin
       (spec/conform! :pinboard.pin/entity)))


(defn shape-batch! [batch]
  (-> batch
      (update :posts (fn [items] (map shape! items)))
      (set/rename-keys {:date :index, :posts :items})))

(s/fdef shape-batch!
        :args (s/keys :req-un [::date ::posts]))
