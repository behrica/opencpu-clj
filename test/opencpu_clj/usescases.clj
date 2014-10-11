(ns opencpu-clj.usescases
  (:require [midje.sweet :refer [fact]]
            [opencpu-clj.core :refer :all]
            [clojure.core.matrix.dataset :as ds :refer[dataset]]
            [clojure.core.matrix :refer [shape]]
            [opencpu-clj.test-support :refer [server-url j]]))

(def mydat
  (let [country ["Angola" "UK" "France"]
        gdp-1960 [1 2 3]
        gdp-1970 [2 4 6]]

         (ds/dataset {"country" country
                           "gdp.1960" gdp-1960
                           "gdp.1970" gdp-1970})))

(fact "can use clojure.matrix dataset as parameter - 1"
  (let [ds (ds/row-maps mydat)]
    (call-function server-url "base" "dim" {:x (j ds)} :json) => [3 3]))


(fact "can use clojure.matrix dataset as parameter - 2"
      (let [mydat-json (ds/row-maps mydat)]
        (shape (json-to-ds (call-function server-url "stats" "reshape"
                                          {:data (j mydat-json)
                                           :varying  (j [2,3])
                                           :v.names  (j "gdp")
                                           :direction (j "long")
                                           }

                                          :json))) => [6 5]))