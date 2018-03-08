(ns eta.pinboard.extract
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [eta.pinboard.api :as api]
            [eta.pinboard.extract.transform :as xform]
            [java-time :as time])
  (:import java.net.URISyntaxException))

(defn today []
  (-> (time/local-date)
      (time/format)))

(defn small-batch []
  (api/get-posts (today)))

(defn large-batch []
  (api/all-posts))

(def input->output-transforms
  [["extended"    :pinboard.pin/extended    identity]
   ["description" :pinboard.pin/description identity]
   ["hash"        :pinboard.pin/hash        identity]
   ["meta"        :pinboard.pin/meta        identity]
   ["tags"        :pinboard.pin/tags        xform/space-separated->tags]
   ["href"        :pinboard.pin/href        #(java.net.URI. %)]
   ["time"        :pinboard.pin/pinned-at   xform/string->date]
   ["shared"      :pinboard.pin/shared      xform/yn->bool]
   ["toread"      :pinboard.pin/to-read     xform/yn->bool]])

(def key-transforms
  (let [ks (map first input->output-transforms)
        fqks (map second input->output-transforms)]
    (zipmap ks fqks)))

(def val-transforms
  (let [fqks (map second input->output-transforms)
        fns (map #(nth % 2) input->output-transforms)]
    (zipmap fqks fns)))

(defn transform-entry [pin field]
  (try
    (update pin field (get val-transforms field))
    (catch URISyntaxException e
      (let [bad-data (get pin field)]
        (-> (dissoc pin :pinboard.pin/href)
            (assoc :bad-uri bad-data))))))

(defn transform-pin [pin]
  (-> (set/rename-keys pin key-transforms)
      (transform-entry :pinboard.pin/extended)
      (transform-entry :pinboard.pin/description)
      (transform-entry :pinboard.pin/tags)

      (transform-entry :pinboard.pin/href)

      (transform-entry :pinboard.pin/hash)
      (transform-entry :pinboard.pin/meta)
      (transform-entry :pinboard.pin/pinned-at)
      (transform-entry :pinboard.pin/shared)
      (transform-entry :pinboard.pin/to-read)))

(def ^:dynamic *persistence-target* "./resources/pinboard")

(defn prepare-transaction [pin]
  (let [transformed (transform-pin pin)
        unique-id (get transformed :pinboard.pin/hash)
        change-id (get transformed :pinboard.pin/meta)]
    [(str *persistence-target* "/" unique-id "/" change-id ".edn") ;; unique-id change-id
     (dissoc transformed :pinboard.pin/meta :pinboard.pin/hash)]))

(defn persist! [pin]
  (let [transaction (prepare-transaction pin)]
    (io/make-parents (first transaction))
    (spit (first transaction) (second transaction))))

#_(def pin
    (-> (small-batch)
        first))

#_(prepare-transaction pin)

#_(persist! pin)

#_(map persist! (small-batch))

#_(def all (large-batch))

#_(map persist! (large-batch))

#_(map persist! all)

