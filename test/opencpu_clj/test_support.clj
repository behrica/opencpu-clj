(ns opencpu-clj.test-support
  (:require [clojure.data.json :as json :refer[write-str]]))

(def server-url "http://localhost:5959")

(def j json/write-str)