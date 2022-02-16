(ns opencpu-clj.test-support
  (:require [clojure.data.json :as json :refer[write-str]]))

(def server-url "http://cloud.opencpu.org")
;;(def server-url "http://localhost:3752")



(def j json/write-str)
