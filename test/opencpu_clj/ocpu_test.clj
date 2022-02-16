(ns opencpu-clj.ocpu-test
  (:require [midje.sweet :refer [fact facts n-of anything contains has-prefix]]
            [opencpu-clj.ocpu :refer [object session package library]]
            [opencpu-clj.test-support :refer [server-url j]]))





(fact "can call function which returns session"
      (:result (object server-url :library "base" :R "seq" {:from 1 :to 5})) => (n-of anything 7))

(fact "can use keywords for package and object"
      (:result (object server-url :library :base :R :seq {:from 1 :to 5})) => (n-of anything 7))

(fact "can call function as json which returns vector"
      (:result (object  server-url :library "base" :R  "seq" {:from 1 :to 5} :json nil)) => [1 2 3 4 5])

(fact "can access data in package as plain"
      (:result (object server-url :library "MASS" :data "Boston")) => #"     crim    zn indus chas    nox    rm   age    dis rad tax ptratio  black\n.*")

(fact "can access data in package as csv"
      (:result (object server-url :library "MASS" :data "Boston" nil :csv nil)) => #"\"crim\",\"zn\",\"indus\",\"chas\",\"nox.*")

(fact "can call method from package on cran"
      (:result  (object server-url :cran "MASS" :R "rational" {:x 10} :json nil)) => [10])

(fact "can call method from github package"
      (:result  (object server-url :github "hadley/plyr" :R "desc" {:x 10} :json nil)) => [-10])

(fact "can list R objects from bioconductor package"
      (:result  (object server-url :bioc "KEGGREST" :R nil)) => (has-prefix "color.pathway.by.objects"))

(fact "Json RPC to method return class lm fails"
      (object server-url :library "stats" :R "lm" {:formula "dist ~ speed" :data "cars"} :json nil) => {:result "No method asJSON S3 class: lm", :status 400})

(fact "can upload a file"
      (last  (:result  (object server-url :library "utils" :R "read.csv" {:skip (j 1) :file {:file "resources/test.csv"}})))
      => (contains "test.csv"))

(fact "can upload an other file"
      (last  (:result  (object server-url :library "utils" :R "read.csv" {:file {:file "resources/test2.csv"}}))) =>
      (contains "test2.csv"))

(fact "can upload file with an other parameter"
      (last  (:result  (object server-url :library "base" :R "file" {:description {:file "resources/test.csv"}})))
      (contains "test.csv"))

(fact "uses other param in file upload"
      (:result  (object server-url :library "base" :R "file" {:blub "1"  :description {:file "resources/test.csv"}}))
      => (contains "blub"))

(fact "can access session path as json"
      (let [first-key-path (first (:result (object server-url :library "base" :R "seq" {:from 1 :to 5})))]
        (:result (session server-url first-key-path :json)) => [1 2 3 4 5]))

(fact "can access session path, which is a dataframe. "
      (let [first-key-path (first (:result (object server-url :library "base" :R "data.frame"  {:gender "c(\"M\",\"M\",\"F\")" :ht "c(172,186.5,165)" :wt "c(91,99,74)"})))]
         (:result (session server-url first-key-path :json)) => [{:ht 172, :wt 91, :gender "M"} {:ht 186.5, :wt 99, :gender "M"} {:ht 165, :wt 74, :gender "F"}]))

(fact "can get data frames as clojure map"
      (count  (:result  (object server-url :library "base" :R "identity" {:x "mtcars"} :json nil))) => 32)

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
      (:result (library server-url {:type :user :user-name "jeroen"} "jsonlite")) => (contains "Information on package 'jsonlite'"))

(fact "can get list of installed packages"
      (:result (library server-url)) => (contains "jsonlite"))

(fact "can get info on a package"
      (:result (library server-url "jsonlite")) => (contains "\n\t\tInformation on package 'jsonlite'"))

(fact "can execute R gist file"
      (:result  (object server-url :gist "behrica" "6f9e20e30387038ad0a6" "gistfile1.r")) => (n-of anything 5))

(fact "can execute R script in package of library"
      (:result  (object server-url :script "library" "MASS" "scripts/ch01.R")) => (n-of anything 13))

(fact "can execute Rmd script in package of library"
      (:result  (object server-url :script "library" "knitr" "examples/knitr-minimal.Rmd")(object server-url :script "library" "knitr" "examples/knitr-minimal.Rmd"))
      => (n-of anything 13))

(fact "can set query params"
      (:result (object server-url :library "datasets" :data "mtcars" nil :tab {:sep "\"|\""})) => (contains "|145|175|"))
