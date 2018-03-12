(ns eta.pinboard.validate
  (:refer-clojure :exclude [read])
  (:require [java-time :as time]
            [eta.specify :refer [conform!]]
            [clojure.edn :as edn]
            [eta.pinboard.pin]
            [clojure.spec.alpha :as s]
            [clojure.java.io :as io])
  (:import java.net.URI java.io.File))

(defn rehydrate-objects [tagged-item]
  (let [data-type (first tagged-item)
        data (last tagged-item)]
    (case data-type
      java.time.OffsetDateTime (time/offset-date-time data)
      java.net.URI (URI. data)
      (throw #_(Throwable. "Inexplicably unexpected data type")
             (ex-info "Inexplicably unexpected data type"
                      {:type (str data-type)
                       :value data})))))

(defn rehydrate-data [s]
  (edn/read-string {:readers {'object rehydrate-objects}} s))

(defn read [filename]
  (let [f (File. filename)]
    (rehydrate-data (slurp f))))
