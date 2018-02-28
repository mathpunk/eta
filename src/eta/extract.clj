(ns eta.extract
  (:require [eta.pinboard.api :as api]
            [clojure.java.io :as io]
            [java-time :as time]
            [clojure.string :as string]
            [eta.transform :as xform]))

;; Batching
;; ==============
;; Construct batch. May be unnecessary if the whole set fits in memory

;; (defn retrieve-all [] ...)
;; (defn retrieve-batch [batch-index] ...)
;; (defn batch-index [] ...) ;; For pins, the dates

(defn retrieve-batch []
  (api/all-posts))

(defonce batch
  (retrieve-batch))

;; Transformation & Persistence
;; ===============================
(def pin (first batch))

(defn unique-id [pin] (pin "hash"))

(defn changed-id [pin] (pin "meta"))

(def ^:dynamic *persistence-root* "./resources/")

(defn pin-filename [pin]
  (let [date-tokens (map str (-> pin
                                 :pinboard.pin/pinned-at
                                 (time/as :year :month-of-year :day-of-month)))
        path-tokens (concat '("pinboard")
                            date-tokens
                            `(~(unique-id pin) ~(changed-id pin)))]
    (str *persistence-root* (string/join "/" path-tokens) ".edn")))

(defn prepare-transaction [pin]
  (let [pin-data (xform/transform-pin pin)]
    [(pin-filename pin-data) (dissoc pin-data "hash" "meta")]))

#_(-> pin
      prepare-transaction)

(defn persist-pin [pin]
  (let [tx (prepare-transaction pin)]
    (do (io/make-parents (first tx))
        (apply spit tx))))

#_(-> pin
      persist-pin)

;; Job
;; ======

;; get a batch
;; LOOP get one pin from one batch
;; transform it
;; write it
;; validate the write
;; either except, or, recur to LOOP
;; map over batches

