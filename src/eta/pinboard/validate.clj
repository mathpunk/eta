(ns eta.pinboard.validate
  (:require [java-time :as time]
            [clojure.edn :as edn]
            [eta.pinboard.pin]
            [clojure.spec.alpha :as s]
            [clojure.java.io :as io])
  (:import java.net.URI java.io.File))

(defn rehydrate-objects [tagged-item]
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
  (edn/read-string {:readers {'object rehydrate-objects}} s))

(def total-written
  (->> (File. "./resources/pinboard")
       file-seq
       (remove #(.isDirectory %))))

(def records
  (->> total-written
       (map slurp)
       (map reader)))

(defn records-valid? []
  (every? #(s/valid? :pinboard.pin/entity %) records))

(comment "ex3. Learn about invalid entities")

(some #(not (s/valid? :pinboard.pin/entity %)) records)
;; Some records are invalid

(->> records
     (remove #(s/valid? :pinboard.pin/entity %))
     count)
;; There are 13 invalid records (2018-03-08)

(require '[clojure.pprint :refer [pprint]])

(->> records
     (remove #(s/valid? :pinboard.pin/entity %))
     (map #(s/explain-data :pinboard.pin/entity %))
     #_(map keys)
     (map #(get % :clojure.spec.alpha/problems ))
     pprint)

(comment "There are two kinds of errors. The second is easy to explain and fix, the first is pretty simple but mysterious."

         ;; Type 1: Description ends up a boolean, not freetext.
         (({:path [:pinboard.pin/description],
            :pred clojure.core/string?,
            :val false,
            :via [:pinboard.pin/entity :eta.type/freetext],
            :in [:pinboard.pin/description]})
          ({:path [:pinboard.pin/description],
            :pred clojure.core/string?,
            :val false,
            :via [:pinboard.pin/entity :eta.type/freetext],
            :in [:pinboard.pin/description]})

          ;; Type 2: Lacks an href, due to bad-uri
          ({:path [],
            :pred
            (clojure.core/fn [%] (clojure.core/contains? % :pinboard.pin/href)),
            :val
            {:pinboard.pin/to-read false,
             :pinboard.pin/description
             "Consumer Trust Is Not What You Think It Is These Days",
             :pinboard.pin/tags #{""},
             :pinboard.pin/pinned-at
             #object[java.time.OffsetDateTime 0x6fe9c39a "2017-02-21T21:10:16Z"],
             :bad-uri
             "https://medium.com/@mitchjoel/consumer-trust-is-not-what-you-think-it-is-these-days-e10cd8f5371c#.d0yzfdwaj#pq=ZBIkIe",
             :pinboard.pin/extended
             "More than price, consumers want trust & data safety, says @mitchjoel & @accenture data  ",
             :pinboard.pin/shared true},
            :via [:pinboard.pin/entity],
            :in []})
          ;; Type 1
          ({:path [:pinboard.pin/description],
            :pred clojure.core/string?,
            :val false,
            :via [:pinboard.pin/entity :eta.type/freetext],
            :in [:pinboard.pin/description]})
          ({:path [:pinboard.pin/description],
            :pred clojure.core/string?,
            :val false,
            :via [:pinboard.pin/entity :eta.type/freetext],
            :in [:pinboard.pin/description]})
          ({:path [:pinboard.pin/description],
            :pred clojure.core/string?,
            :val false,
            :via [:pinboard.pin/entity :eta.type/freetext],
            :in [:pinboard.pin/description]})
          ({:path [:pinboard.pin/description],
            :pred clojure.core/string?,
            :val false,
            :via [:pinboard.pin/entity :eta.type/freetext],
            :in [:pinboard.pin/description]})
          ({:path [:pinboard.pin/description],
            :pred clojure.core/string?,
            :val false,
            :via [:pinboard.pin/entity :eta.type/freetext],
            :in [:pinboard.pin/description]})
          ({:path [:pinboard.pin/description],
            :pred clojure.core/string?,
            :val false,
            :via [:pinboard.pin/entity :eta.type/freetext],
            :in [:pinboard.pin/description]})
          ({:path [:pinboard.pin/description],
            :pred clojure.core/string?,
            :val false,
            :via [:pinboard.pin/entity :eta.type/freetext],
            :in [:pinboard.pin/description]})
          ({:path [:pinboard.pin/description],
            :pred clojure.core/string?,
            :val false,
            :via [:pinboard.pin/entity :eta.type/freetext],
            :in [:pinboard.pin/description]})
          ;; Type 2
          ({:path [],
            :pred
            (clojure.core/fn [%] (clojure.core/contains? % :pinboard.pin/href)),
            :val
            {:pinboard.pin/to-read true,
             :pinboard.pin/description
             "Tweet from eyeo organizers (@eyeofestival)",
             :pinboard.pin/tags #{""},
             :pinboard.pin/pinned-at
             #object[java.time.OffsetDateTime 0x5bb84337 "2018-01-12T23:03:21Z"],
             :bad-uri "https://t.co/vGA76ByoSk|",
             :pinboard.pin/extended "",
             :pinboard.pin/shared true},
            :via [:pinboard.pin/entity],
            :in []})
          ;; Type 1
          ({:path [:pinboard.pin/description],
            :pred clojure.core/string?,
            :val false,
            :via [:pinboard.pin/entity :eta.type/freetext],
            :in [:pinboard.pin/description]})))

(comment "ex2. Learn about hash and meta"

         (->> (total-written)
              (partition-by #(.getParent %))
              (filter #(> (count %) 1)))
         ;;=> ()

         "Conclusion: None of my pins have `changed` in the sense that two have the same hash, but different meta."
         )
#_(let [written-pins (total-written)]
    (map (comp #(s/valid? :pinboard.pin/entity %) reader slurp) written-pins))

(comment "ex1. Learn about bad-uris"
         (def bad-uris (->> (total-written)
                            (map slurp)
                            (map reader)
                            (filter #(get % :bad-uri))))

         (count bad-uris)

         bad-uris

         (require '[clojure.java.browse :refer [browse-url]])

         (browse-url ((first bad-uris) :bad-uri)))
