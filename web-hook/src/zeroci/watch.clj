(ns zeroci.watch
  (:require
   [http.async.client :as c]
   [http.async.client.request :as req]
   [org.httpkit.client :as http-client]
   [clojure.tools.logging :as log]
   [clj-json-patch.core :as patch]
   [cheshire.core :as json]))

(def url "http://localhost:8001/apis/zeroci.io/v1/builds?watch=true")

;; http://localhost:8001/api/v1/namespaces/default/pods?labelSelector=system=ci

;; http://localhost:8001/api/v1/namespaces/default/pods/sansara-test-2017-05-23--04-47-37-aidbox-test-runner/log

(def client (c/create-client))

(defonce reqs (atom []))

(comment

  (->
   @(http-client/post
     "http://localhost:8001/apis/zeroci.io/v1/namespaces/default/builds"
     {:body (json/generate-string {:kind "Build"
                                   :apiVersion "zeroci.io/v1"
                                   :metadata {:name "test-1"}})
      :headers {"Content-Type" "application/json"}})
   :body)


  (def diff (patch/diff {:kind "Build"
                         :apiVersion "zeroci.io/v1"
                         :metadata {:name "test-1"}}
                        {:kind "Build"
                         :apiVersion "zeroci.io/v1"
                         :metadata {:name "test"}
                         :status "in-progress"}))
  (->
   @(http-client/get
     "http://localhost:8001/apis/zeroci.io/v1/namespaces/default/builds/test-1"
     {:headers {"Content-Type" "application/json"}})
   :body)

  (->
   @(http-client/patch
     "http://localhost:8001/apis/zeroci.io/v1/namespaces/default/builds/test-1"
     {:body (json/generate-string diff)
      :headers {"Content-Type" "application/json-patch+json"}})
   :body)


  (->
   @(http-client/get "http://localhost:8001/apis/zeroci.io/v1/builds")
   :body)


  )


(defn watch []
  (println "WATCH")
  (swap! reqs conj
         (req/execute-request
          client
          (req/prepare-request :get url :timeout -1)
          :part (fn [_ baos] (println "PART" baos) [nil :continue])
          :completed (fn [_]
                       (println "DONE"))
          :error (fn [_ t]
                   (println "ERROR" (type t))
                   (.start (Thread. watch)) t))))


(comment
  (.start (Thread. watch))
  )
