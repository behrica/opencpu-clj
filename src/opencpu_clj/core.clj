(ns opencpu-clj.core
  (:require
            [clj-http.client :as client :refer [post]]
            [clojure.data.json :as json :refer [read-str write-str]]
            [clojure.core.matrix.dataset :as ds :refer[dataset row-maps]]
            )
  )


(defn- extract-session-key [body]
  (let [first-line (first (clojure.string/split-lines body))]
    (nth (clojure.string/split  first-line #"/" ) 3)))

(defn- make-package-url [base-url package-name]
  (format "%s/ocpu/library/%s" base-url package-name))

(defn json-to-ds [json]
    (let [column-names (keys (first json))]
      (ds/dataset column-names json)))


(defn get-dataset
  [base-url package-name dataset-path]
  (let [resp (client/get (format "%s/data/%s/json" (make-package-url base-url package-name) dataset-path))
        data (json/read-str (:body resp))
        ]
        (json-to-ds data)))

(defn call-function [base-url package-name function-name params output-format]
  (let [response (client/post (format "%s/R/%s/%s " (make-package-url base-url package-name) function-name (name output-format))
                              {:form-params params
                               :content-type :json
                               :throw-exceptions false
                               :debug-body true
                              :debug true
                              })
        body (:body response)
        status (:status response)
        content-type (get (:headers response) "content-type")
        ]
    ;(println "response: " response)
    ;(println "params:" params)
    (cond
            (and (= "application/json" content-type)
                 (or  (= 200 status)
                      (= 201 status)))
            (read-str body)

            (= status 400) body
            true (extract-session-key body))))