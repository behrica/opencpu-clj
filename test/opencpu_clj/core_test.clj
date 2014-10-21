(ns opencpu-clj.core-test
   (:require [midje.sweet :refer [fact facts tabular]]
             [opencpu-clj.core :refer :all]
             [clojure.core.matrix :refer [shape]]
             [opencpu-clj.test-support :refer [server-url j]]))


 (fact "Can retrieve datasets"
       (shape (:result (get-dataset server-url "MASS" "Boston"))) => [506 14])

  (fact "Can get error on retrieve non existing datasets"
       (get-dataset server-url "MASS" "xxx")=> {:result "data set 'xxx' not found\n" :status 400 })


(fact "can call function which returns vector"
      (:result (call-function-json-RPC server-url "base" "seq" {:from 1 :to 5})) => [1 2 3 4 5])

(fact "can call function which returns a S3 class"
      (count (:result (call-function server-url "utils" "sessionInfo" {} ))) => 11)

(fact "status 400 returns plain text as is"
      (:result (call-function-json-RPC server-url "utils" "sessionInfo" {} ))
      => "No method asJSON S3 class: sessionInfo\n")

(fact "can pass dataset as json"
      (:result (call-function-json-RPC server-url "base" "dim" {:x (j [{"a":1}]) })) => [1 1])

(fact "can pass unquoted symbols"
      (count (:result (call-function server-url "stats" "lm" {:formula "mpg~am" :data "mtcars"}))) => 11)

(facts "Methods can be called RPC style and normal"
  (fact "methjod 'seq' can be called RPC style"
        (:result (call-function-json-RPC server-url "base" "seq" {:from 1 :to 10})) => (range 1 11))
  (fact "method 'seq' can be called non-RPC style"
        (:result (call-function server-url "base" "seq" {:from 1 :to 10})) => #"x0.*"))

(facts "can evaluate R code and get variables back"
       (fact (:result (eval-R server-url "a<-1" {} [:a] :csv)) => {:a "\"x\"\r\n1\r\n"})
       (fact (:result (eval-R server-url "a<-1" {} [:a] :json)) => {:a [1]})

       (tabular "can evaluate R code and get variables back"
                (fact (:result (eval-R server-url ?code {} ?output)) => ?expected)
?code                         ?output      ?expected
"a<-1;b<-c(1,2,3,4)"         [:a :b]          {:a [1] :b [1 2 3 4]}
"a<-list(a=c(1,2),b=c(3,4))" [:a]             {:a {:a [1 2], :b [3 4]}}
"a<-list(a=c(1,2),a=c(3,4))" [:a]             {:a {:a [1 2], :a.1 [3 4]}}))

(fact "can pass simple value into eval-R"
      (:result (eval-R server-url "a<-seq(from,to)" {:from 1 :to 10} [:a] :json)) => {:a [1 2 3 4 5 6 7 8 9 10]})

(fact "can pass simple int vector into eval-R"
      (:result (eval-R server-url "a<-length(v)" {:v [1 2 3 4 5] } [:a] :json)) => {:a [5]})

(fact "call functions returns error"
  (call-function server-url "evaluate" "blu"  {:input "zz"})  =>
      {:result "object 'blu' not found\n\nIn call:\nget(reqobject, paste(\"package\", reqpackage, sep = \":\"), inherits = FALSE)\n"
       :status 400}
      )

(fact "Get error on eval-R"
   (eval-R server-url "xxxx" {} [:a] :json) =>
      {:result "object 'xxxx' not found\n\nIn call:\nparse_all(input)\n", :status 400})

