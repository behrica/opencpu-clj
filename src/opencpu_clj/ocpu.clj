(ns opencpu-clj.ocpu
  (:require [clj-http.client :as client]))


; Low level interface to the OpenCPU HTTP Api.
; The methods in here should be only thin wrappers arround the http API and do not convert input and ouput data,
; but transfer as is.

(defn- make-package-url [base-url package-name]
  (format "%s/ocpu/library/%s" base-url package-name))


(defn- do-post [base-url package-name function-name output-format params]

  (let [response (client/post (format "%s/R/%s/%s " (make-package-url base-url package-name) function-name (name output-format))
                              {:form-params params
                               :throw-exceptions false
                               ;:debug-body true
                               ;:debug true
                               :as :auto})
        status (:status response)
        body (:body response)
        ]
    ;(println "!!------------------------------------------------")
    ;(println body)
    ;(println "!!------------------------------------------------")
    (if (= 201 status)
      {:result (clojure.string/split-lines body) :status status} ;todo this is data transformation. Should be on higher level ?
      {:result body :status status})))

(defn- get-body [url]
  (let [resp (client/get url {:as :auto})]
   {:result (:body resp)
    :status (:status resp)
    }))



(defn object
  ([base-url package-name object-type object-name]
   (object base-url package-name object-type object-name nil))
  ([base-url package-name object-type object-name params]
  (object base-url package-name object-type object-name params ""))
  ([base-url package-name object-type object-name params output-format]

   (if params
     (do-post base-url package-name object-name  output-format params)
     (get-body (format "%s/%s/%s/%s" (make-package-url base-url package-name)
                                     (name object-type)
                                      object-name
                                     (name output-format))))))


(defn session [base-url session-path output-format]
  (get-body (format "%s/%s/%s" base-url session-path (name output-format))))


(defn library
  ([base-url]
  (get-body (format "%s/ocpu/library" base-url)))

  ([base-url package-name]
   (get-body (format "%s/ocpu/library/%s" base-url package-name)))

  ([base-url package-location-info package-name]
  (get-body (format "%s/ocpu/%s/%s/library/%s"
                             base-url
                             (name (:type package-location-info))
                             (name (:user-name package-location-info))
                             package-name))))


(defn package [base-url package-name path & man-params]
  (get-body (format "%s/%s"
                  (make-package-url base-url package-name)
                  (clojure.string/join "/" (cons path (map name man-params))))))
