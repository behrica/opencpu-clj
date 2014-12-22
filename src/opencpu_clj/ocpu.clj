(ns opencpu-clj.ocpu
  (:require [clj-http.client :as client]
            [opencpu-clj.utils :refer [params-map]]))

; Low level interface to the OpenCPU HTTP Api.
; The methods in here should be only thin wrappers arround the http API and do not convert input and ouput data,
; but transfer data as-is.

(defn- make-package-url [base-url library-name package-name]
    (format "%s/ocpu/%s/%s" base-url (name library-name) package-name))


(defn- make-post-R-url [base-url library-name package-name function-name output-format]
  (format "%s/R/%s/%s " (make-package-url base-url library-name package-name) function-name (name output-format)))

(defn- do-post [url params]
  (let [response (client/post url (merge (params-map params)
                                     {:throw-exceptions false
                                      ;:debug-body true
                                      ;:debug true
                                      :as :auto}))
        status (:status response)
        body (:body response)
        ]
;    (Println "!!------------------------------------------------")
;    (println body)
;    (println "!!------------------------------------------------")
    (if (= 201 status)
      {:result (clojure.string/split-lines body) :status status} ;todo this is data transformation. Should be on higher level ?
      {:result body :status status})))

(defn- get-body
  ([url]
   ( get-body url nil))
  ([url query-params]
    ;  (println url)
    (let [resp (client/get url {
                                ;;  :debug true
                                :query-params query-params
                                :as :auto :throw-exceptions false })]
      {:result (:body resp)
       :status (:status resp)
       })))

(defn object
  "Does a call to the OpenCPU 'object' endpoint.
  base-url : url and port of the OpencPU server
  library-name : :library or :cran
   package-name : name of system wide installed R package to use
   object-type : :R or :data to acces R-functions or data
   object-name : name of the function or data object
   params : map of params to the R function to be called. Need to be a map where each key will be used to set
            a named argument in the R function call. The values need to be encoded in Json in a format which the R function
            jsonlite::fromJSON is able to convert to the correct R type. If not nil this will do a http POST, else a http GET.
            For the special case of file uploads, the parameter value can be a map like this: {:file filename}
  output-format : can be a keyword for choosing any of the valid output-formats (see OpenCPU docu)
  query-params: additional query params, used for arguments of output-format conversion functions

  If library-name is :gist, then the following paramters get's interpreted as 'gist-username', 'gist', 'filename'
  and the gist file gets executed (no parameter passing possible)

   Returns a map with keys :result and :status , containing the result in output-format of the call or an error message.
  The value of :status is the http status code. The :result can either be
  - a list of session links (for a function call, if :json was not specified)
  - a clojure data structure (for a function call and :json was specified and return was a simple value which http-client cou auto-coerce from json)
  - a string in the output format asked for (:json, :csv, ..)
  "

  ([base-url library-name package-name object-type object-name]
   (object base-url library-name package-name object-type object-name nil))
  ([base-url library-name package-name object-type object-name params]
  (object base-url library-name package-name object-type object-name params "" nil))
  ([base-url library-name package-name object-type object-name params output-format query-params]
     (cond
      (= library-name :gist) (do-post (clojure.string/join "/" [base-url "ocpu" "gist" package-name object-type object-name]) {} )
      (= library-name :script) (do-post (clojure.string/join "/" [base-url "ocpu" package-name object-type object-name]) {} )
      (and params (= :R object-type)) (do-post (make-post-R-url base-url library-name package-name object-name output-format) params)
      :else (get-body (clojure.string/join "/" (filter #(not (nil? %)) [ (make-package-url base-url library-name package-name)
                                                                                  (name object-type)
                                                                                  object-name
                                                                                  (name output-format)]))
                      query-params))))

(defn session
  "Does a call to the OpenCPU 'session' endpoint.
  base-url : url and port of the OpenCPU server
  session-path : The path to the session object, as returned from the 'object' function.

  Returns a map with keys :result and :status , containing the result of the call or an error message.
  The value of :status is the http status code."
  [base-url session-path output-format]
  (get-body (format "%s/%s/%s" base-url session-path (name output-format))))


(defn library
  "Does a call to the OpenCPU 'library' endpoint.
  base-url : url and port of the OpenCPU server
  package-name : name of system wide installed R package to use
  package-location-info : suports
  {:type :user :user-name 'jeroen'} for accessing R package of a user

  Returns a map with keys :result and :status , containing the result of the call or an error message.
  The value of :status is the http status code."
  ([base-url]
   (get-body (format "%s/ocpu/library" base-url)))

  ([base-url package-name]
   (get-body (make-package-url base-url :library package-name)))

  ([base-url package-location-info package-name]
   (get-body (make-package-url
              base-url
              (format "%s/%s/library"
                      (name (:type package-location-info))
                      (name (:user-name package-location-info)))
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
                  (make-package-url base-url :library package-name)
                  (clojure.string/join "/" (cons path (map name man-params))))))
