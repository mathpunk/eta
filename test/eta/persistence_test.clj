(ns eta.persistence-test
  (:require [eta.pinboard.persistence :as sut]
            [java-time :as time]
            [clojure.test :refer [deftest testing is]]))

(def pin
  {:pinboard.pin/pinned-at (time/local-date "2018-03-01")
   :pinboard.pin/hash "0000"})

(deftest filenames-from-pins
  (is (= "./resources/pinboard/2018/03/01/0000.edn"
         (sut/filename pin))))
