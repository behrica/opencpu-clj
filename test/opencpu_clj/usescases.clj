(ns opencpu-clj.usescases
  (:require [midje.sweet :refer [fact =>]]
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
        (call-function-json-RPC server-url "base" "eigen" {:x (json/write-str m)}))
           => {:values [14 3], :vectors [[0.8944 -0.1961] [0.4472 0.9806]]})



(fact "can call lm and get data back"
      (let [fit (call-function server-url "stats" "lm" {:formula "mpg~wt" :data "mtcars"})
            coef (call-function-json-RPC server-url "stats" "coefficients" {:object fit})
            residuals (call-function-json-RPC server-url "stats" "residuals" {:object fit})
            fitted (call-function-json-RPC server-url "stats" "fitted" {:object fit})
            ]
       {:coef      coef
         :residuals residuals
         :fitted fitted
        }
        ) => {:coef [37.2851 -5.3445], :residuals [-2.2826 -0.9198 -2.086 1.2973 -0.2001 -0.6933 -3.9054 4.1637 2.35 0.2999 -1.1001 0.8669 -0.0502 -1.883 1.1733 2.1033 5.9811 6.8727 1.7462 6.422 -2.611 -2.9726 -3.7269 -3.4624 2.4644 0.3564 0.152 1.2011 -4.5432 -2.7809 -3.2054 -1.0275], :fitted [23.2826 21.9198 24.886 20.1027 18.9001 18.7933 18.2054 20.2363 20.45 18.9001 18.9001 15.5331 17.3502 17.083 9.2267 8.2967 8.7189 25.5273 28.6538 27.478 24.111 18.4726 18.9269 16.7624 16.7356 26.9436 25.848 29.1989 20.3432 22.4809 18.2054 22.4275]})