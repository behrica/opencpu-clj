(ns opencpu-clj.test-support
  (:require [clojure.data.json :as json :refer[write-str]]))

(def server-url "http://public.opencpu.org")
;;(def server-url "http://localhost:3752")



(def j json/write-str)
