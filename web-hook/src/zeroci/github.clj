(ns zeroci.github
  (:require [org.httpkit.client :as http]
            [cheshire.core :as json]
            [clojure.java.io :as io]))

(comment
  (-> @(http/get "https://api.github.com/orgs/clojure/repos"
                 #_{:query-params {:access_key access_key}})
      :body
      (json/parse-string keyword)
      (->> (map :url))))

;; https://gist.github.com/whitlockjc/0373e7bc9f0e883fab7281d9e1f4f684


;; (http/get "http://localhost:8001/apis/zeroci.io/v1/builds?watch=true"
;;           {:as :stream}
;;           (fn [{:keys [status body]}]))

