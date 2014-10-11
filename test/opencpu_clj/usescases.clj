(ns opencpu-clj.usescases
  (:require [midje.sweet :refer [fact]]
            [opencpu-clj.core :refer :all]
            [clojure.core.matrix.dataset :as ds :refer[dataset]]
            [clojure.core.matrix :refer [shape]]
            ))

(def mydat
  (let [country ["Angola" "UK" "France"]
        gdp-1960 [1 2 3]
        gdp-1970 [2 4 6]]

         (ds/dataset {"country" country
                           "gdp.1960" gdp-1960
                           "gdp.1970" gdp-1970})))

(fact "can use clojure.matrix dataset as parameter - 1"
  (let [mydat-json (ds/row-maps mydat)]
    (call-function "https://public.opencpu.org" "base" "dim" {:x mydat-json} :json) => [3 3]))


(fact "can use clojure.matrix dataset as parameter - 2"
      (let [mydat-json (ds/row-maps mydat)]
        (shape (json-to-ds (call-function "https://public.opencpu.org" "stats" "reshape"
                                          {:data mydat-json
                                           :varying  [2,3]
                                           :v.names  "gdp"
                                           :direction "long"
                                           }

                                          :json))) => [6 5]))