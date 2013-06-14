(defproject data "0.0.1"
  :description "tools for working with EDN in text and binary"
  :url "http://zig.io"
  :license {:name "Copyright 2013 Duramec LLC"
            :url "http://www.duramec.com" }
  :plugins [[com.duramec/lein-scalac "0.1.1"]
            [com.duramec/lein-scalatest "0.0.2"]]
  :java-source-paths ["src/java"]
  :scala-version "2.10.2"
  :scala-test-paths ["test/scala"]
  :dependencies [[org.fressian/fressian "0.6.3"]
                 [us.bpsm/edn-java "0.4.2"]])

