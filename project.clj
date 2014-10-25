(defproject opencpu-clj "0.1.0-SNAPSHOT"
            :description "OpenCPU-clj is a Clojure library for using the OpenCPU API"
            :url "http://incanter.org/"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :min-lein-version "2.0.0"
            :dependencies [[net.mikera/core.matrix "0.29.1"]
                            [org.clojure/clojure "1.6.0"]
                            [org.clojure/data.json "0.2.5"]
                            [clj-http "1.0.0"]]
            :profiles {:dev {:dependencies [[midje "1.6.3"]]}})
