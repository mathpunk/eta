(ns eta.type-test
  (:require [eta.type :as sut]
            [clojure.spec.alpha :as s]
            [clojure.test :refer [deftest testing is]]))


(deftest test-tag-set
  (testing "tags are comma-separated, can have spaces"
    (is (s/valid? :eta.type/tag "tag"))
    (is (s/valid? :eta.type/tag "tag or term"))
    (is (not (s/valid? :eta.type/tag "commas, removed"))))
  (testing "collections of tags"
    (is (s/valid? :eta.type/tag-set #{"term", "another term"}))
    (is (s/valid? :eta.type/tag-set ["term", "another term"]))
    (is (not (s/valid? :eta.type/tag-set #{"comma, separated" "terms"})))))


