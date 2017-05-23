(defproject zeroci "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [cheshire "5.7.1"]
                 [ring/ring-defaults "0.3.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [ch.qos.logback/logback-classic "1.2.2"]
                 [kubernetes-api "0.1.0"]
                 [http.async.client "1.2.0"]
                 [route-map "0.0.4"]
                 [hiccup "1.0.5"]
                 [pandect "0.6.1"]
                 [clj-json-patch "0.1.4"]
                 [http-kit "2.2.0"]]
  :uberjar-name "zeroci.jar"
  :profiles {:uberjar {:aot :all :omit-source true}})
