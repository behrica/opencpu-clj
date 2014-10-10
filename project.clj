(defproject opencpu-clj "0.0.1-SNAPSHOT"
            :description "Incanter-ocpu is the ocpu module of the Incanter project."
            :url "http://incanter.org/"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :min-lein-version "2.0.0"
            :dependencies [[net.mikera/core.matrix "0.29.1"]
                            [org.clojure/clojure "1.6.0"]
                            [org.clojure/data.json "0.2.5"]
                            [clj-http-lite "0.2.0"]]
            :profiles {:dev {:dependencies [[midje "1.6.3"]]}})
