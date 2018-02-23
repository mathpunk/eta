(ns eta.twitter.core
  (:require [aero.core :as aero]
            [twitter.oauth :as oauth]
            [twitter.callbacks.handlers :as callback]
            [twitter.api.restful :as api])
  (:import [twitter.callbacks.protocols SyncSingleCallback]))

(def secrets
  (get (aero/read-config "resources/secrets.edn") :twitter))


(def credentials (oauth/make-oauth-creds (:app-consumer-key secrets)
                                         (:app-consumer-secret secrets)
                                         (:user-access-token secrets)
                                         (:user-access-token-secret secrets)))


(def ^:dynamic *callback*
  (SyncSingleCallback. callback/response-return-body
                       callback/get-twitter-error-message ;; expecting 420: Enhance Your Calm
                       callback/response-throw-error))


(defn query [api-fn & key-value-pairs]
  (api-fn :oauth-creds credentials
          :callbacks *callback*
          :params (apply assoc (conj key-value-pairs {}))))
