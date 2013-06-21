(defproject data "0.1.5"
            :description "tools for working with EDN in text and binary"
            :url "http://zig.io"
            :license {:name "Copyright 2013 Duramec LLC"
                      :url "http://www.duramec.com" }
            :profiles {:dev { :plugins [[com.duramec/lein-scalac "0.1.1"]
                                        [com.duramec/lein-scalatest "0.0.3"]]
                             :scala-test-paths ["test/scala"]}}
            :java-source-paths ["src/java"]
            :scala-version "2.10.2"
            :dependencies [[org.fressian/fressian "0.6.3"]
                           [us.bpsm/edn-java "0.4.3"]])

