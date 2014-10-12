(ns opencpu-clj.ocpu-test
  (:require [midje.sweet :refer [fact facts]]
            [opencpu-clj.ocpu :refer [call-R-function session]]
            [opencpu-clj.test-support :refer [server-url j]]))

(fact "can access session path as json"
      (let [first-key-path (first (call-R-function server-url "base" "seq" {:from 1 :to 5}))]
        (session server-url first-key-path :json) => [1 2 3 4 5]))


(fact "can access session path, which is a dataframe. "
      (let [first-key-path (first (call-R-function server-url "base" "data.frame"  {:gender "c(\"M\",\"M\",\"F\")" :ht "c(172,186.5,165)" :wt "c(91,99,74)"}))]
        (session server-url first-key-path :json) => [{:ht 172, :wt 91, :gender "M"} {:ht 186.5, :wt 99, :gender "M"} {:ht 165, :wt 74, :gender "F"}]))


