(ns eta.extract.auth
  (:require [aero.core :as aero]))

(def secrets
  (aero/read-config "resources/secrets.edn") )

#_secrets

(def twitter-credentials
  (secrets :twitter))

#_twitter-credentials

(def pinboard-credentials
  (secrets :pinboard))

#_pinboard-credentials
