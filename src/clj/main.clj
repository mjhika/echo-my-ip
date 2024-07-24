(ns main
  (:gen-class)
  (:require
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [html]
   [integrant.core :as ig]
   [reitit.ring :as ring]
   [ring.adapter.jetty9 :as jetty]
   [ring.util.response :as resp]))

(defn- trim-brackets [s]
  (str/replace s #"\[|\]" ""))

(defn remote-addr [r]
  (resp/response (trim-brackets (:remote-addr r))))

(defn index [r]
  (resp/response
   (html/default-page {:head {:title "what is my public ip address"}
                       :body [(html/header :header-col [{:link    "/api"
                                                         :display "API"}])
                              (html/main :addr (trim-brackets (:remote-addr r)))]})))

(defn other-page [code message]
  (html/default-page {:head {:title "what is my public ip address | error"}
                      :body [(html/header :header-col [{:link    "/api"
                                                        :display "API"}])
                             (html/other-main :message (format "%d | %s" code message))]}))

(defmulti default-handler :status)

(defmethod default-handler 405 [_]
  {:status  405
   :headers {}
   :body    (other-page 405 "Method Not Allowed")})

(defmethod default-handler 406 [_]
  {:status  406
   :headers {}
   :body    (other-page 406 "Not Acceptable")})

(defmethod default-handler :default [_]
  {:status  404
   :headers {}
   :body    (other-page 404 "File Not Found")})

(def routes
  [["/" {:name ::root
         :get  #'index}]
   ["/api" {:name ::api
            :get  #'remote-addr}]])

(defn config [& {:as opts}]
  {:adapter/jetty9 {:port    (or (:port opts)
                                 8080)
                    :handler (ig/ref :handler/main)}
   :handler/main   {:dev? false}})

(defmethod ig/init-key :adapter/jetty9 [_ {:keys [handler] :as opts}]
  (log/infof "Starting server on port: %d" (:port opts))
  (jetty/run-jetty handler (-> opts (dissoc :handler) (assoc :join? false))))

(defmethod ig/init-key :handler/main [_ {:keys [dev?]}]
  (let [handler #(ring/ring-handler (ring/router routes)
                                    (ring/routes (ring/create-resource-handler {:path "/"})
                                                 (ring/create-default-handler
                                                  {:not-found          (constantly (default-handler {:status 404}))
                                                   :method-not-allowed (constantly (default-handler {:status 405}))
                                                   :not-acceptable     (constantly (default-handler {:status 406}))})))]
    (if dev?
      (ring/reloading-ring-handler handler)
      (handler))))

(defmethod ig/halt-key! :adapter/jetty9 [_ server]
  (.stop server))

(defn -main [& {:as opts}]
  (let [port (get opts "--port")]
    (ig/init (config :port (when port (Integer/parseInt port))))))

(comment
  (def system
    (ig/init (config :port 3000)))

  (ig/halt! system))
