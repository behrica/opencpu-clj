(ns opencpu-clj.ocpu-test
  (:require [midje.sweet :refer [fact facts]]
            [opencpu-clj.ocpu :refer [call-R-function session package library]]
            [opencpu-clj.test-support :refer [server-url j]]))

(fact "can access session path as json"
      (let [first-key-path (first (call-R-function server-url "base" "seq" {:from 1 :to 5}))]
        (session server-url first-key-path :json) => [1 2 3 4 5]))


(fact "can access session path, which is a dataframe. "
      (let [first-key-path (first (call-R-function server-url "base" "data.frame"  {:gender "c(\"M\",\"M\",\"F\")" :ht "c(172,186.5,165)" :wt "c(91,99,74)"}))]
        (session server-url first-key-path :json) => [{:ht 172, :wt 91, :gender "M"} {:ht 186.5, :wt 99, :gender "M"} {:ht 165, :wt 74, :gender "F"}]))

(fact "can call info of package"
      (package server-url "base" "info" ) => #"\n\t\tInformation on package 'base'.*")

(fact "can get man info as html on method"
      (package server-url "base" "man" "seq" :html) => #".*page for seq \{base\}.*")

(fact "can get man info on method"
      (package server-url "base" "man" "seq") => #"seq                    package:base.*")

(fact "can get list of man pages"
      (package server-url "base" "man") => #"abbreviate\nagrep.*")


(fact "can get info on user package"
      (library server-url {:type :user :user-name "carsten"} "ggplot2") => #"carsten.*")
