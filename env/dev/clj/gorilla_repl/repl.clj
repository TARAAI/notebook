(ns gorilla-repl.repl
  (:require [gorilla-repl.figwheel :refer [config]]
            [figwheel-sidecar.repl-api :as ra :refer [start-figwheel! cljs-repl]]
            [gorilla-repl.system :as gsys])
  (:use ;; reagent-sample.handler
        ;; ring.server.standalone
        [ring.middleware file-info file]))

(defonce server (atom nil))

;; (start-figwheel! (config))

;; (ra/print-config)
;; (cljs-repl)

#_(defn get-handler []
  ;; #'app expands to (var app) so that when we reload our code,
  ;; the server is forced to re-resolve the symbol in the var
  ;; rather than having its own copy. When the root binding
  ;; changes, the server picks it up without having to restart.
   (-> #'app
      ; Makes static assets in $PROJECT_DIR/resources/public/ available.
       (wrap-file "resources")
      ; Content-Type, Content-Length, and Last Modified headers for files in body
       (wrap-file-info)))

#_(defn start-server
   "used for starting the server in development mode from REPL"
   [& [port]]
   (let [port (if port (Integer/parseInt port) 3000)]
     (reset! server
             (serve (get-handler)
                    {:port port
                     :auto-reload? true
                     :join? false}))
     (println (str "You can view the site at http://localhost:" port))))

#_(defn stop-server []
   (.stop @server)
   (reset! server nil))
