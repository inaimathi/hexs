(defproject hexs "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.844"]
                 [org.clojure/tools.cli "0.3.5"]

                 [http-kit "2.5.3"]
                 [ring "1.9.2"]
                 [hiccup "1.0.5"]
                 [bidi "2.1.6"]
                 [cheshire "5.10.0"]
                 [reagent "1.0.0"]]
  :hooks [leiningen.cljsbuild]
  :plugins [[lein-cljsbuild "1.1.7"]]
  :cljsbuild {:builds
              [{:source-paths ["src/hexs/front_end"]
                :compiler {:output-to "resources/hexs.js"
                           :optimizations :whitespace
                           :pretty-print true}
                :jar true}]}
  :repl-options {:init-ns hexs.core}

  :main hexs.core
  :aot [hexs.core])
