(ns opencpu-clj.core
  (:require
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
  [base-url package-name dataset-path]
  (json-to-ds (ocpu/get-R-dataset base-url package-name dataset-path :json :json)))


(defn call-function
  "Calls a function of an installed package on the OpenCPU server.
   The parameters to the metho a given as a map, which need to match the named parametesr of the
   R function to call. For details on parameter formats and mappings see the documentation.
   The functions returns the links to the session data, whcih can be used to retrieve further information,
   such as the result of the function call. For details see the OpenCPU API documentation.

   Given that the parameters are correct, this call will always succeed, even if the result cannot eventually not be marshalled as Json.
   (Which means then that the result cannot be transfered in an usefull way from the server to the caller.)
   But the session key can be used as a parameter to call other functions.
   "
   [base-url package-name function-name params]
  (let [session-links (ocpu/object base-url package-name function-name params)]
    (nth (s/split (first session-links) #"/") 3)))

(defn session-data [server-url session-key data-path output-format]
  (ocpu/session server-url (format "/ocpu/tmp/%s/%s" session-key data-path) output-format))


"Calls a function of an installed package on the OpenCPU server.
 The parameters to the metho a given as a map, which need to match the named parametesr of the
 R function to call. For details on parameter formats and mappings see the documentation.
 The function returns directly the result which format is further detailed in the API documentation.
 This only works for functions, which return 'standart' types, such as vectors, lists, dataframes with numbers and strings in it.
 If the return value cannot be converted to json by OpenCPU, this function will fail."
  (defn call-function-json-RPC
  [base-url package-name function-name params]
  (ocpu/object base-url package-name function-name params :json))


