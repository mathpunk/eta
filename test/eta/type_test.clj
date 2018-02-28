(ns eta.type-test
  (:require [eta.type]
            [java-time :as time]
            [clojure.spec.alpha :as s]
            [clojure.test :refer [deftest testing is]]))

(deftest test-tag-set
  (testing "tags are comma-separated, can have spaces"
    (is (s/valid? :eta.type/tag "tag"))
    (is (s/valid? :eta.type/tag "tag or term"))
    (is (not (s/valid? :eta.type/tag "commas, removed"))))
  (testing "collections of tags" ;; Note: Space-separate vs comma-separate confusion remains
    (is (s/valid? :eta.type/tag-set #{"term", "another term"}))
    (is (s/valid? :eta.type/tag-set ["term", "another term"]))
    (is (not (s/valid? :eta.type/tag-set #{"comma, separated" "terms"})))
    (is (not (s/valid? :eta.type/tag-set #{"terms" 123})))))

(deftest test-timestamps
  (is (s/valid? :eta.type/timestamp (time/instant)))
  (is (s/valid? :eta.type/timestamp (time/local-date 2011 11 11))))


