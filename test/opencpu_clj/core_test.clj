(ns opencpu-clj.core-test
  (:require [midje.sweet :refer [fact]]
            [opencpu-clj.core :refer :all]
            [clojure.core.matrix :refer [shape]]
            [opencpu-clj.test-settings :refer [server-url]]
            ))



(fact "Can retrieve datasets"
  (shape ( get-dataset  server-url "MASS" "Boston")) => [506 14])

(fact "can call function which returns vector"
  (call-function server-url "base" "seq" {:from 1 :to 5} :json)
      => [1 2 3 4 5])

(fact "can call function which returns a S3 class"
      (count (call-function server-url "utils" "sessionInfo" {} ""))
      => 184)

(fact "status 400 returns plain text as is"
      (call-function server-url "utils" "sessionInfo" {} :json) => "No method asJSON S3 class: sessionInfo\n")

(fact "can pass dataset as json"
      (call-function server-url "base" "dim" {:x [{"a":1}]} :json) => [1 1])

