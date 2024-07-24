(ns build
  (:refer-clojure :exclude [test])
  (:require
   [clojure.tools.build.api :as b]))

(defn build-tailwind []
  (println "Building CSS")
  (b/process {:out          :capture
              :env          {"PATH" (System/getenv "PATH")}
              :command-args ["npm" "run" "build"]}))

(defn- uber-opts [opts]
  (let [app     'com.mjhika/echo-my-ip ; group/artifact
        version "1.0.0"
        target  "target"
        classes (str target "/classes")]
    (assoc opts
           :app        app
           :version    version
           ;; group/artifact-version.jar is the expected naming convention:
           :uber-file  (format "target/%s-%s.jar" app version)
           :basis      (b/create-basis {})
           :ns-compile '[main]
           :class-dir  classes
           :target-dir classes ; for b/copy-dir
           :target     target
           :path       target ; for b/delete
           :src-dirs   ["src"]
           :main       'main)))

(defn uber [opts]
  (let [opts (uber-opts opts)]
    ;; clojure.tools.build.api functions return nil:
    (b/delete opts)
    (build-tailwind)
    (b/copy-dir (update opts :src-dirs conj "resources"))
    (b/compile-clj opts)
    (println "\nWriting" (:uber-file opts))
    (b/uber opts))
  ;; return original opts for chaining:
  opts)

(comment
  (uber {}))
