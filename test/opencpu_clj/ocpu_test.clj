(ns opencpu-clj.ocpu-test
  (:require [midje.sweet :refer [fact facts n-of anything contains]]
            [opencpu-clj.ocpu :refer [object session package library]]
            [opencpu-clj.test-support :refer [server-url j]]))



(fact "can call function which returns session"
      (object server-url "base" :R "seq" {:from 1 :to 5}) => (n-of anything 6))

(fact "can call function as json which returns vector"
      (object  server-url "base" :R  "seq" {:from 1 :to 5} :json) => [1 2 3 4 5])

(fact "can access data in package"
      (object server-url "MASS" :data "Boston") => #"        crim    zn indus chas    nox    rm   age     dis rad tax ptratio  black\n.*")

(fact "can access data in package as json"
      (object server-url "MASS" :data "Boston" nil :csv) => #"\"crim\",\"zn\",\"indus\",\"chas\",\"nox.*")


(fact "can access session path as json"
      (let [first-key-path (first (object server-url "base" :R "seq" {:from 1 :to 5}))]
        (session server-url first-key-path :json) => [1 2 3 4 5]))

(fact "can access session path, which is a dataframe. "
      (let [first-key-path (first (object server-url "base" :R "data.frame"  {:gender "c(\"M\",\"M\",\"F\")" :ht "c(172,186.5,165)" :wt "c(91,99,74)"}))]
        (session server-url first-key-path :json) => [{:ht 172, :wt 91, :gender "M"} {:ht 186.5, :wt 99, :gender "M"} {:ht 165, :wt 74, :gender "F"}]))

(fact "can get info of a package"
      (package server-url "base" "info" ) => #"\n\t\tInformation on package 'base'.*")

(fact "can get man info as html on method"
      (package server-url "base" "man" "seq" :html) => #".*page for seq \{base\}.*")

(fact "can get man info on method"
      (package server-url "base" "man" "seq") => #"seq                    package:base.*")

(fact "can get list of man pages"
      (package server-url "base" "man") => #"abbreviate\nagrep.*")

(fact "can get list of R objects from package"
      (package server-url "MASS" "R" ) => #"abbey\n.*")



(fact "can get info on user package"
      (library "http://public.opencpu.org" {:type :user :user-name "jeroen"} "jsonlite") => (contains "Information on package 'jsonlite'"))

(fact "can get list of installed packages"
      (library server-url) => (contains "jsonlite"))

(fact "can get info on a package"
      (library server-url "jsonlite") => (contains "\n\t\tInformation on package 'jsonlite'"))
