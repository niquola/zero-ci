(ns zeroci.core
  (:require
   [clojure.string :as str]
   [org.httpkit.server :as http-kit]
   [clojure.tools.logging :as log]
   [clojure.java.shell :as sh]
   [cheshire.core :as json]
   [ring.util.codec])
  (:gen-class))

(defonce server (atom nil))

(def repos (atom nil))

(defn exec [& args]
  (log/info "Execute: " args)
  (let [res (apply sh/sh args)]
    (log/info "Result: " res)
    res))

(defn process-keys [res]
  (reduce (fn [acc [k v]]
            (let [pth (mapv keyword (str/split k #"\."))]
              (assoc-in acc pth v)))
          {} (get res "data")))

(defn repo-key [h]
  (fn [req]
    (let [repo-key (str/replace (:uri req) #"^/" "")]
      (if (str/blank? repo-key)
        {:body (str "Hi, i'm zeroci please use /repo-key path to trigger")}
        (h (assoc req :repo-key repo-key :log []))))))

(defn get-repositories [h]
  (fn [req]
    (if-let [rs (or @repos
                 (reset! repos 
                         (let [res (exec "kubectl" "get" "configmaps" "repositories" "-o" "json")]
                           (println res)
                           (process-keys (json/parse-string (:out res))))))]
      (h (assoc req :repositories rs))
      {:body (str "Could not get repositories configmap") :status 500})))

(defn get-config [h]
  (fn [{rk :repo-key rs :repositories :as req}]
    (if-let [cfg (get rs (keyword rk))]
      (h (assoc req :config cfg))
      {:body (str "Could not get config for " rk) :status 500})))

(defn checkout-project [h]
  (fn [{{rp :repo :as cfg} :config rk :repo-key rs :repositories :as req}]
    (exec "rm" "-rf" (str  "/tmp/" rk))
    (let [res (exec "git" "clone" rp rk  :dir "/tmp")]
      (if (= 0 (:exit res))
        (h req)
        {:body (str res) :status 500}))))

(defn run-command
  [{{rp :repo cmd :command :as cfg} :config rk :repo-key rs :repositories :as req}]
  (exec "git" "pull" :dir (str "/tmp/" rk))
  (let [res (exec cmd :dir (str "/tmp/" rk))]
    (if (= 0 (:exit res))
      {:body (json/generate-string res)}
      {:body (str res) :status 500})))


(def handle
  (-> run-command
      checkout-project
      get-config
      get-repositories
      repo-key))


(defn restart []
  ;; todo validate config
  (when-let [s @server]
    (log/info "Stoping server")
    (@server)
    (reset! server nil))
  (log/info "Starting server on " 8888)
  (reset! server (http-kit/run-server #'handle {:port 8888})))



(defn -main [& args]
  (restart))

(comment
  (restart)
  )
