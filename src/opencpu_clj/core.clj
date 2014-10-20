(ns opencpu-clj.core
  (:require [clojure.data.json :as json :refer[read-str write-str]]
            [clojure.string :as s]
            [clojure.core.matrix.dataset :as ds]
            [opencpu-clj.ocpu :as ocpu]))

(defn json-to-ds [json]
    (let [column-names (keys (first json))]
      (ds/dataset column-names json)))

(defn- path-to-keyword [path]
  (keyword (s/join "/" (drop 4 (s/split path #"/"))))

  )



(defn get-dataset
  "Retrieves a dataset from an already installed R package on the OpenCPU server.
  It get return as a core.matric.dataset"
  [server-url package-name dataset-path]
  {:body (json-to-ds (:body (ocpu/object server-url package-name :data dataset-path nil :json)))})


(defn call-function
  "Calls a function of an installed package on the OpenCPU server.
   The parameters to the metho a given as a map, which need to match the named parametesr of the
   R function to call. For details on parameter formats and mappings see the documentation.
   The functions returns the session key, which can be used to retrieve further information,
   such as the result of the function call. For details see the OpenCPU API documentation.

   Given that the parameters are correct, this call will always succeed, even if the result cannot eventually not be marshalled as Json.
   (Which means then that the result cannot be transfered in an usefull way from the server to the caller.)
   But the session key can be used as a parameter to call other functions.
   "
   [server-url package-name function-name params]
  (let [session-links (:body (ocpu/object server-url package-name :R function-name params))]
    {:body (nth (s/split (first session-links) #"/") 3)}))

(defn session-data [server-url session-key data-path output-format]
  "Access to the details of the session data"
  (ocpu/session server-url (format "/ocpu/tmp/%s/%s" session-key data-path) output-format))


"Calls a function of an installed package on the OpenCPU server.
 The parameters to the function are given as a map, which need to match the named parametesr of the
 R function to call. For details on parameter formats and mappings see the documentation.
 The function returns directly the result which format is further detailed in the API documentation.
 This only works for functions, which return 'standart' types, such as vectors, lists, dataframes with numbers and strings in it.
 If the return value cannot be converted to json by OpenCPU, this function will fail."
(defn call-function-json-RPC
  [server-url package-name function-name params]
  (ocpu/object server-url package-name :R function-name params :json))


(defn- get-data [server-url session variable output-format]
  {(keyword variable) (:body (session-data server-url session (format "R/%s" (name variable)) output-format))})


(defn- params-to-R [input-variables]
  (let [json-params (into {} (map #(hash-map (first %) (write-str (second %))) input-variables))
        vars-declare-code (clojure.string/join (map #(format "%s<-fromJSON('%s');" (name (first %)) (second %)) json-params))
         ]
    (format "library(jsonlite);%s" vars-declare-code)))

(defn eval-R
  "Evaluates arbitrary R expressions.
  Important: They need to be self contained, as they run in an empty R session."
  ([server-url r-code input-variables out-variables]
  (eval-R server-url r-code input-variables out-variables :json))

  ([server-url r-code input-variables out-variables output-format]
  (let [r-code-enhanced (format "%s%s" (params-to-R input-variables) r-code)
        session (:body (call-function server-url "evaluate" "evaluate"  {:input r-code-enhanced}))]
    (into {} (map #(get-data server-url session % output-format) out-variables)))))







