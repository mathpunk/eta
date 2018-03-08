(ns eta.pinboard.validate
  (:require [java-time :as time]
            [clojure.edn :as edn]
            [eta.pinboard.pin]
            [clojure.spec.alpha :as s])
  (:import java.net.URI java.io.File))

(defn revert-data [tagged-item]
  (let [data-type (first tagged-item)
        data (last tagged-item)]
    ;; "(~symbol. data)" is an intersting idea but let's not open that level of generality
    (case data-type
      java.time.OffsetDateTime (time/offset-date-time data)
      java.net.URI (java.net.URI. data)
      (throw #_(Throwable. "Inexplicably unexpected data type")
             (ex-info "Inexplicably unexpected data type"
                      {:type (str data-type)
                       :value data})))))

(defn reader [s]
  (edn/read-string {:readers {'object revert-data}} s))

(defn total-written []
  (->> (File. "./resources/pinboard")
       file-seq
       (remove #(.isDirectory %))))

(->> (total-written)
     count)

#_(some #(contains?  % :bad-uri) red-files)


#_(let [written-pins (total-written)]
    (map (comp #(s/valid? :pinboard.pin/entity %) reader slurp) written-pins))
