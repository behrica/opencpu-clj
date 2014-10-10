(ns opencpu-clj.core
  (:require
            [clj-http.lite.client :as client :refer [post]]
            [clojure.data.json :as json :refer [read-str]]
            [clojure.core.matrix.dataset :as ds :refer[dataset]]
            )
  )


(defn- extract-session-key [body]
  (let [first-line (first (clojure.string/split-lines body))]
    (clojure.string/join "/" (take 4 (clojure.string/split  first-line #"/" )))))

(defn- make-package-url [base-url package-name]
  (format "%s/ocpu/library/%s" base-url package-name))

(defn get-dataset
  [base-url package-name dataset-path]
  (let [resp (client/get (format "%s/data/%s/json" (make-package-url base-url package-name) dataset-path))
        data (json/read-str (:body resp))
        column-names (keys (first data))]
        (ds/dataset column-names data)))

(defn call-function [base-url package-name function-name params output-format]
  (let [response (client/post (format "%s/R/%s/%s " (make-package-url base-url package-name) function-name output-format)
                              {:form-params params
                               :content-type "application/x-www-form-urlencoded"
                               :throw-exceptions false
                               })
        body (:body response)
        status (:status response)
        content-type (get (:headers response) "content-type")
        ]
    (println "response: " response)
    (println "type: " content-type)

    (if (and (= "application/json" content-type)
             (or  (= 200 status)
                  (= 201 status)))
      (read-str body)
      (extract-session-key body)
      )))