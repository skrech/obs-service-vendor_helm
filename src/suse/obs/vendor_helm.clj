#!/usr/bin/env bb

(ns suse.obs.vendor-helm
  (:require [babashka.cli :as cli]
            [babashka.fs :as fs]
            [babashka.process :as bp]
            [clojure.string :as str])
  (:gen-class))

(def cli-config
  {:spec
   {:subdir {:desc "Directory where Chart.yaml is located"
             :alias :d
             :require true
             :validate fs/directory?}
    :include {:desc "Glob pattern of paths to include into the final archive"
              :alias :i
              :default "*"}}})

(defn update-helm-deps [dir]
  (bp/shell {:dir dir} "helm dependency update"))

(defn files-relative-to-dir [dir include-pattern]
  (->> (fs/glob dir include-pattern {:recursive true})
       (map (partial fs/relativize dir))
       (map str)))

(defn archive-content [dir include-pattern]
  (let [files (files-relative-to-dir dir include-pattern)
        outfile "contents.tar"]
    (apply bp/shell "tar -cvf" outfile "-C" dir files)
    (println "Output tar:" (str/join "/" [(fs/cwd) outfile]))))

(defn vendor-helm [{:keys [subdir, include]}]
  (update-helm-deps subdir)
  (archive-content subdir include))

(defn show-help [config]
  (println "OBS service to vendor Helm charts")
  (println)
  (println (cli/format-opts config))
  (println))

(defn -main [& args]
  (let [opts (cli/parse-opts args cli-config)]
    (if (or (:help opts) (:h opts))
      (show-help cli-config)
      (vendor-helm opts))))

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
