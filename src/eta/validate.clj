(ns eta.validate
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.spec.alpha :as s]))

;; For this iteration, let's keep assuming we do everything at once and then verify it
(defn load-batch []
  (->> "pinboard"
       (io/resource)
       (io/as-file)
       (file-seq)
       (remove #(.isDirectory %))))

#_(-> (load-batch)
      first
      slurp
      edn/read-string) ;; This won't work without a definition of readers for the objects
