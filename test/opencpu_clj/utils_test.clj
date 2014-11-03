(ns opencpu-clj.utils-test
  (:require [opencpu-clj.utils :refer :all]
            [midje.sweet :refer :all]))


(fact "can convert simplest params map"
      (params-map {:a 1}) => { :form-params {:a 1}})

(fact "can convert map with single file "
      (params-map {:a {:file "resources/test.csv"}}) => { :multipart [{ :name "a" :content (clojure.java.io/file "resources/test.csv")}]})

(fact "converts map with single file and other params"
      (params-map {:a {:file "resources/test.csv"}
                   :b 1}) => { :multipart [{:name "b" :content 1}
                                           {:name "a" :content (clojure.java.io/file "resources/test.csv")}]})


(fact "converts map with multiple files and other params"
      (params-map {:a {:file "resources/test.csv"}
                   :b 1
                   :c {:file "resources/test2.csv"}})
      => { :multipart [{:name "c" :content (clojure.java.io/file "resources/test2.csv")}
                       {:name "b" :content 1}
                       {:name "a" :content (clojure.java.io/file "resources/test.csv")}]})
