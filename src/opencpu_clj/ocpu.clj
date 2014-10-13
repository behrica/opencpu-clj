(ns opencpu-clj.ocpu
  (:require [clj-http.client :as client]))


(defn- make-package-url [base-url package-name]
  (format "%s/ocpu/library/%s" base-url package-name))


(defn get-R-dataset
  [base-url package-name dataset-path]
  (let [resp (client/get (format "%s/data/%s/json" (make-package-url base-url package-name) dataset-path)
                         {:as :json})]
    (:body resp)))

(defn call-R-function
  ([base-url package-name function-name params]
  (call-R-function base-url package-name function-name params ""))
  ([base-url package-name function-name params output-format]
  (let [response (client/post (format "%s/R/%s/%s " (make-package-url base-url package-name) function-name (name output-format))
                              {:form-params params
                               :throw-exceptions false
                               ;:debug-body true
                               ;:debug true
                               :as :auto
                              })
        status (:status response)
        body (:body response)
        ]
    (if (= 201 status)
      (clojure.string/split-lines body)
      body))))


(defn session [base-url session-path output-format]
  (:body (client/get (format "%s/%s/%s" base-url session-path (name output-format))
                     {:as :auto
                     })))


(defn library [base-url package-location-info package-name]
  ;{:type :user :user-name "carsten"}
  (:body (client/get (format "%s/ocpu/%s/%s/library/%s"
                             base-url
                             (name (:type package-location-info))
                             (name (:user-name package-location-info))
                             package-name)
                     )))

(defn package [base-url package-name path & man-params]
  (:body (client/get (format "%s/%s "
                             (make-package-url base-url package-name)
                             (if (= "man" path)
                               (clojure.string/join "/" (cons path (map name man-params)))
                               path)))))
