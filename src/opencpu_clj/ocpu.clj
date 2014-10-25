(ns opencpu-clj.ocpu
  (:require [clj-http.client :as client]))

; Low level interface to the OpenCPU HTTP Api.
; The methods in here should be only thin wrappers arround the http API and do not convert input and ouput data,
; but transfer data as-is.

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
  (let [resp (client/get url {:as :auto :throw-exceptions false })]
   {:result (:body resp)
    :status (:status resp)
    }))



(defn object
  "Does a call to the OpenCPU 'object' endpoint.
   base-url : url and port of the OpencPU server
   package-name : name of system wide installed R package to use
   object-type : :R or :Data to acces R-functions or data
   object-name : name of the function or data object
   params : map of params to the R function to be called. Need to be a map where each key will be used to set
            a named argument in the R function call. The values need to be encoded in Json in a format which the R function
            jsonlite::fromJSON is able to convert to the correct R type. If not nil this will do a http POST, else a http GET.
   output-format : can be a keyword for choosing any of teh valid output-formats (see OpenCPU docu)

   Returns a map with keys :result and :status , containing the result in output-format of the call or an error message.
   The value of :status is teh http status code. "
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


(defn session
  "Does a call to the OpenCPU 'session' endpoint.
   base-url : url and port of the OpenCPU server
   session-path : The path to the session object, as returned from the 'object' function.

   Returns a map with keys :result and :status , containing the result of the call or an error message.
   The value of :status is teh http status code."
  [base-url session-path output-format]
  (get-body (format "%s/%s/%s" base-url session-path (name output-format))))


(defn library
  "Does a call to the OpenCPU 'library' endpoint.
   base-url : url and port of the OpenCPU server
   package-name : name of system wide installed R package to use
   package-location-info : suports
             {:type :user :user-name 'jeroen'} for accessing R package of a user

   Returns a map with keys :result and :status , containing the result of the call or an error message.
   The value of :status is teh http status code."
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


(defn package
   "Does a call to the OpenCPU 'package' endpoint.
   base-url : url and port of the OpenCPU server
   package-name : name of system wide installed R package to use
   path : 'man' for man pages , 'info' for package info
   man-params : further parameters

   Returns a map with keys :result and :status , containing the result of the call or an error message.
   The value of :status is the http status code."
  [base-url package-name path & man-params]
  (get-body (format "%s/%s"
                  (make-package-url base-url package-name)
                  (clojure.string/join "/" (cons path (map name man-params))))))
