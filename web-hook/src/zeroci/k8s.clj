(ns zeroci.k8s
  (:require
   [org.httpkit.client :as http-client]
   [clojure.tools.logging :as log]
   [clj-json-patch.core :as patch]
   [zeroci.k8s :as k8s]
   [clojure.walk :as walk]
   [cheshire.core :as json]))

(defn url [cfg pth]
  (str "http://localhost:8001/" pth))

(defn query [cfg rt & [pth]]
  (-> @(http-client/get
        (url cfg (str "apis/" (:apiVersion cfg) "/namespaces/" (:ns cfg) "/" (name rt) "/" pth))
        {:headers {"Content-Type" "application/json"}})
      :body
      (json/parse-string)))

(defn list [cfg rt] (query cfg rt))
(defn find [cfg rt id] (query cfg rt id))

(defn create [cfg rt res]
  (-> @(http-client/post
        (url cfg (str "apis/" (:apiVersion cfg) "/namespaces/" (:ns cfg) "/" (name rt)))
        {:body (json/generate-string res)
         :headers {"Content-Type" "application/json"}})
      :body
      (json/parse-string)))

(defn delete [cfg rt id]
  (-> @(http-client/delete
        (url cfg (str "apis/" (:apiVersion cfg) "/namespaces/" (:ns cfg) "/" (name rt) "/" id))
        {:headers {"Content-Type" "application/json"}})
      :body
      (json/parse-string)))

(defn patch [cfg rt id patch]
  (let [res (find cfg rt id)]
    (if-not (= "Failure" (get res "status"))
      (let [diff (patch/diff res (walk/stringify-keys patch))]
        (->
         @(http-client/patch
           (url cfg (str "apis/" (:apiVersion cfg) "/namespaces/" (:ns cfg) "/" (name rt) "/" id))
           {:body (json/generate-string diff)
            :headers {"Content-Type" "application/json-patch+json"}})
         :body))
      res)))

(def cfg {:apiVersion "zeroci.io/v1" :ns "default"})

(comment
  (list cfg :builds)
  (find cfg :builds "test-1")
  (patch cfg :builds "test-1" {:status "inprogress"})

  (delete cfg :builds "test-1")

  (create cfg :builds
          {:kind "Build"
           :apiVersion "zeroci.io/v1"
           :metadata {:name "test-1"}})
  )

#_(query {:apiVersion "zeroci.io/v1"
        :ns "default"}
       :builds "test-1")

#_(query {:apiVersion "zeroci.io/v1"
        :ns "default"}
       :builds "test-1")
