(ns opencpu-clj.core-test
  (:require [midje.sweet :refer [fact facts]]
            [opencpu-clj.core :refer :all]
            [clojure.core.matrix :refer [shape]]
            [opencpu-clj.test-support :refer [server-url j]]))



(fact "Can retrieve datasets"
  (shape ( get-dataset server-url "MASS" "Boston")) => [506 14])

(fact "can call function which returns vector"
  (call-function-json-RPC server-url "base" "seq" {:from 1 :to 5})
      => [1 2 3 4 5])

(fact "can call function which returns a S3 class"
      (count (call-function server-url "utils" "sessionInfo" {} ))
      => 184)

(fact "status 400 returns plain text as is"
      (call-function-json-RPC server-url "utils" "sessionInfo" {} ) => "No method asJSON S3 class: sessionInfo\n")

(fact "can pass dataset as json"
      (call-function-json-RPC server-url "base" "dim" {:x (j [{"a":1}]) }) => [1 1])

(fact "can pass unquoted symbols"
      (count (call-function "http://public.opencpu.org" "stats" "lm" {:formula "mpg~am" :data "mtcars"})) => 184)


(facts
  "Methods can be called RPC style and normal"
  (fact "methjod 'seq' can be called RPC style"
        (call-function-json-RPC "http://localhost:6124" "base" "seq" {:from 1 :to 10}) => (range 1 11))
  (fact "methjod 'seq' can be called non-RPC style"
        (call-function "http://localhost:6124" "base" "seq" {:from 1 :to 10}) => #"/ocpu/tmp.*")

  )