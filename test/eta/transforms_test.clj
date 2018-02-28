(ns eta.transforms-test
  (:require [eta.transforms :as sut]
            [clojure.test :refer [deftest testing is]]
            [clojure.spec.alpha :as s]
            [java-time :as time]))

(deftest test-string-to-date
  (is (= 2015 (time/as (sut/str->date "2015-01-23") :year)))
  (is (= 2018 (time/as (sut/str->date "2018-02-28T04:53:32Z") :year))))


(deftest test-string-to-tag-set
  (let [tag-set (sut/str->tags "term, other term")] 
    (is (s/valid? :eta.type/tag-set tag-set))
    (is (some #{"term"} tag-set))))
