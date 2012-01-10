(defproject notch/clj_fitbit "0.0.1-SNAPSHOT"
  :description "Fitbit API Clojure Wrapper"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [compojure "1.0.0-RC2"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/data.json "0.1.2"]

                 ]
  ;:dev-dependencies [[lein-ring "0.4.6"]];;0.4.6 isn't the most recent.. but the most recent is broken
  :source-path "src/main/clojure"
  :test-path "src/test/clojure"
  :java-source-path "src/main/java"
  :resources-path "src/main/resources"
  ;:ring {:handler notch.api/app}
  )

