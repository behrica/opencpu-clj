(ns opencpu-clj.ocpu
  (:require [clj-http.client :as client]))


(defn- make-package-url [base-url package-name]
  (format "%s/ocpu/library/%s" base-url package-name))


(defn get-R-dataset
  [base-url package-name dataset-path]
  (let [resp (client/get (format "%s/data/%s/json" (make-package-url base-url package-name) dataset-path)
                         {:as :json})]
    (:body resp)))

(defn call-R-function [base-url package-name function-name params output-format]
  (let [response (client/post (format "%s/R/%s/%s " (make-package-url base-url package-name) function-name (name output-format))
                              {:form-params params
                               :content-type :json
                               :throw-exceptions false
                               ;:debug-body true
                               ;:debug true
                               :as :auto
                              })]
    (:body response)))
