(defproject notch/clj-fitbit "0.0.1-SNAPSHOT"
  :description "Fitbit API Clojure Wrapper"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/data.json "0.1.2"]
                 [joda-time/joda-time "1.5"]
                 [org.json/json "20090211"]
                 [commons-lang/commons-lang "2.3"]
                 [commons-logging/commons-logging "1.1"]
                 [clj-webdriver "0.5.0-alpha5"]
                 [com.fitbit/fitbit4j "1.0.22"]
                 ]
  :source-path "src/main/clojure"
  :test-path "src/test/clojure"
  :java-source-path "src/main/java"
  :resources-path "src/main/resources"
  :dev-resources-path "src/test/resources"
  :compile-path "target/classes"
  :javac-options {:destdir "target/classes/" :encoding "UTF-8"}

  :deploy-repositories {"releases" "file:../public-repo/releases"
                        "snapshots" "file:../public-repo/snapshots"}

  :repositories { ;"fitbit-gh" {:url "https://raw.github.com/fitbit/fitbit4j/tree/gh-pages/release"}
                 "notch-public" {:url "http://raw.github.com/TeamNotch/public-repo/master/releases"}
                 }
  )

