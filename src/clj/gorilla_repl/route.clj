(ns gorilla-repl.route
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.core :as compojure]
            [gorilla-repl.websocket-relay :as ws-relay]
    ; [gorilla-repl.renderer :as renderer]            ; this is needed to bring the render implementations into scope
            [gorilla-repl.hiccup_renderer :as renderer]     ; this is needed to bring the render implementations into scope
            [gorilla-repl.handle :as handle]))

;; TODO Somebody clean up the routes!
(defn create-api-routes
  [prefix]
  [(GET (str prefix "load") [] ((comp handle/wrap-cors-handler handle/wrap-api-handler) handle/load-worksheet))
   (POST (str prefix "save") [] (handle/wrap-api-handler handle/save))
   (GET (str prefix "gorilla-files") [] (handle/wrap-api-handler handle/gorilla-files))
   (GET (str prefix "config") [] (handle/wrap-api-handler handle/config))
   (GET (str prefix "repl") [] (ws-relay/repl-ring-handler ws-relay/on-receive-mem))
   ; (GET "/repl" [] (ws-relay/repl-ring-handler ws-relay/on-receive-net))
   (GET (str prefix ":document.html") [document] (partial handle/document-utf8 (str document ".html")))])

(defn create-repl-routes
  [prefix receive-fn]
  [(GET (str prefix "repl") [] (ws-relay/repl-ring-handler receive-fn))])


(defn create-resource-routes
  [prefix]
  [(route/resources prefix)                                 ;; Needed during development
   ;; ^{:name "res2"} (route/resources "/" {:root "public"}) ; Yuck, seems to be required for uberwar
   ; (wrap-webjars handler "/webjars") ;; TODO Fix this
   ;; ^{:name ":res2"} (route/resources "/" {:root "META-INF/resources/"}) ;; webjars servlet 3 style
   (route/resources prefix {:root "gorilla-repl-client"})
   (route/files (str prefix "project-files") {:root "."})
   (route/not-found "Bummer, not found")]
  )

(def default-api-routes (create-api-routes "/"))
(def default-repl-routes (create-repl-routes "/" ws-relay/on-receive-mem))
(def remote-repl-routes (create-repl-routes "/" ws-relay/on-receive-net))
(def default-resource-routes (create-resource-routes "/"))
(def dev-routes (apply compojure/routes (concat default-api-routes
                                                default-repl-routes
                                                default-resource-routes)))
(def remote-repl-dev-routes (apply compojure/routes (concat default-api-routes
                                                            remote-repl-routes
                                                            default-resource-routes)))

(def redirect-route (GET "/" [] handle/redirect-app))

(defn war-routes [prefix]
  (apply compojure/routes
         (concat (create-api-routes (str prefix gorilla_repl.GorillaReplListener/PREFIX))
                 (create-resource-routes (str prefix gorilla_repl.GorillaReplListener/PREFIX)))))

