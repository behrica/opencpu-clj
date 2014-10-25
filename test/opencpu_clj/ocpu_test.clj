(ns opencpu-clj.ocpu-test
  (:require [midje.sweet :refer [fact facts n-of anything contains]]
            [opencpu-clj.ocpu :refer [object session package library]]
            [opencpu-clj.test-support :refer [server-url j]]))



(fact "can call function which returns session"
      (:result (object server-url :library "base" :R "seq" {:from 1 :to 5})) => (n-of anything 6))

(fact "can call function as json which returns vector"
      (:result (object  server-url :library "base" :R  "seq" {:from 1 :to 5} :json)) => [1 2 3 4 5])

(fact "can access data in package as plain"
      (:result (object server-url :library "MASS" :data "Boston")) => #"        crim    zn indus chas    nox    rm   age     dis rad tax ptratio  black\n.*")

(fact "can access data in package as csv"
      (:result (object server-url :library "MASS" :data "Boston" nil :csv)) => #"\"crim\",\"zn\",\"indus\",\"chas\",\"nox.*")

(fact "can call method from package on cran"
      (:result  (object server-url :cran "MASS" :R "rational" {:x 10} :json)) => [10])

(fact "can call method from github package"
      (:result  (object server-url :github "hadley/plyr" :R "desc" {:x 10} :json)) => [-10])

(fact "can access session path as json"
      (let [first-key-path (first (:result (object server-url :library "base" :R "seq" {:from 1 :to 5})))]
        (:result (session server-url first-key-path :json)) => [1 2 3 4 5]))

(fact "can access session path, which is a dataframe. "
      (let [first-key-path (first (:result (object server-url :library "base" :R "data.frame"  {:gender "c(\"M\",\"M\",\"F\")" :ht "c(172,186.5,165)" :wt "c(91,99,74)"})))]
         (:result (session server-url first-key-path :json)) => [{:ht 172, :wt 91, :gender "M"} {:ht 186.5, :wt 99, :gender "M"} {:ht 165, :wt 74, :gender "F"}]))

(fact "can get info of a package"
      (:result (package server-url "base" "info" )) => #"\n\t\tInformation on package 'base'.*")

(fact "can get man info as html on method"
      (:result (package server-url "base" "man" "seq" :html)) => #".*page for seq \{base\}.*")

(fact "can get man info on method"
      (:result (package server-url "base" "man" "seq")) => #"seq                    package:base.*")

(fact "can get list of man pages"
      (:result (package server-url "base" "man")) => #"abbreviate\nagrep.*")

(fact "can get list of R objects from package"
      (:result (package server-url "MASS" "R" )) => #"abbey\n.*")


(fact "can get info on user package"
      (:result (library "http://public.opencpu.org" {:type :user :user-name "jeroen"} "jsonlite")) => (contains "Information on package 'jsonlite'"))

(fact "can get list of installed packages"
      (:result (library server-url)) => (contains "jsonlite"))

(fact "can get info on a package"
      (:result (library server-url "jsonlite")) => (contains "\n\t\tInformation on package 'jsonlite'"))
