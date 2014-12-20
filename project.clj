(defproject opencpu-clj "0.2.0-SNAPSHOT"
            :description "OpenCPU-clj is a Clojure library for using the OpenCPU API"
            :url "https://github.com/behrica/opencpu-clj"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :min-lein-version "2.0.0"
            :dependencies [[net.mikera/core.matrix "0.31.1"]
                            [org.clojure/clojure "1.6.0"]
                            [org.clojure/data.json "0.2.5"]
                            [clj-http "1.0.0"]]
            :plugins [[lein-midje-doc "0.0.24"]]
            :profiles {:dev {:dependencies [[midje "1.6.3"]]}}
            :documentation
            {:files
             {"ocpu_test"
              {:input "test/opencpu_clj/ocpu_test.clj"
               :title "clj-opencpu/ocpu_text "
               :sub-title "clojure client for opencpu.org"
               :author "Carsten Behring"
               :email  "carsten.behring@gmail.com"}
              "core_test"
              {:input "test/opencpu_clj/core_test.clj"
               :title "clj-opencpu/core_test"
               :sub-title "clojure client for opencpu.org"
               :author "Carsten Behring"
               :email  "carsten.behring@gmail.com"}}})
