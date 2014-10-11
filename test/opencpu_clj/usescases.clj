(ns opencpu-clj.usescases
  (:require [midje.sweet :refer [fact]]
            [opencpu-clj.core :refer :all]
            [clojure.core.matrix.dataset :as ds :refer [dataset]]
            [clojure.core.matrix :refer [shape matrix]]
            [opencpu-clj.test-support :refer [server-url j]]
            [clojure.data.json :as json]))

(def mydat
  (let [country ["Angola" "UK" "France"]
        gdp-1960 [1 2 3]
        gdp-1970 [2 4 6]]

         (ds/dataset {"country" country
                           "gdp.1960" gdp-1960
                           "gdp.1970" gdp-1970})))

(fact "can use clojure.matrix dataset as parameter - 1"
  (let [ds (ds/row-maps mydat)]
    (call-function-json-RPC server-url "base" "dim" {:x (j ds)}) => [3 3]))


(fact "can use clojure.matrix dataset as parameter - 2"
      (let [mydat-json (ds/row-maps mydat)]
        (shape (json-to-ds (call-function-json-RPC server-url "stats" "reshape"
                                          {:data (j mydat-json)
                                           :varying  (j [2,3])
                                           :v.names  (j "gdp")
                                           :direction (j "long")
                                           }))) => [6 5]))

(fact "can send matrix to R and calculate eigen values with R"
      (let [m (matrix [[13 2][5 4]])]
        (call-function-json-RPC server-url "base" "eigen" {:x (json/write-str m)})
           => {:values [14 3], :vectors [[0.8944 -0.1961] [0.4472 0.9806]]}))