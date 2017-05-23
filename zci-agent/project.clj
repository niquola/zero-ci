(defproject zci-agent "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [http-kit "2.2.0"]
                 [clj-json-patch "0.1.4"]
                 [cheshire "5.7.1"]]
  :uberjar-name "zeroci-agent.jar"
  :profiles {:uberjar {:aot :all :omit-source true}})
