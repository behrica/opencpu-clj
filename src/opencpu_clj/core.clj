(ns opencpu-clj.core
  (:require
            [clojure.core.matrix.dataset :as ds :refer[dataset row-maps]]
            [opencpu-clj.ocpu :as ocpu]))

;TODO use this
(defn- extract-session-key [body]
  (let [first-line (first (clojure.string/split-lines body))]
    (nth (clojure.string/split  first-line #"/" ) 3)))


(defn json-to-ds [json]
    (let [column-names (keys (first json))]
      (ds/dataset column-names json)))


(defn get-dataset
  [base-url package-name dataset-path]
  (json-to-ds (ocpu/get-R-dataset base-url package-name dataset-path)))


(defn call-function
   [base-url package-name function-name params]
  (ocpu/call-R-function base-url package-name function-name params ""))

(defn call-function-json-RPC
  [base-url package-name function-name params]
  (ocpu/call-R-function base-url package-name function-name params :json))

