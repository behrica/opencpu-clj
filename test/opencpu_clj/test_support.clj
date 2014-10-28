(ns opencpu-clj.test-support
  (:require [clojure.data.json :as json :refer[write-str]]))

(def server-url "http://public.opencpu.org")

(def j json/write-str)
