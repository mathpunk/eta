(ns eta.twitter
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


(defn status [id]
  (first (query api/statuses-lookup :id id)))


(-> (status 957651298991706112)
    keys)

;; Social

(defn followers-by-id [name]
  (first (query api/followers-ids :target-screen-name name)))


(defn follows-by-id [name]
  (first (query api/friends-ids :target-screen-name name)))

(def some-user-id (first (:ids (query api/friends-ids :target-screen-name "mathpunk"))))

(->
 (query api/users-lookup :user-id some-user-id)
 first
 keys)

;; (def all-user-keys [:description :profile_link_color :profile_sidebar_border_color :is_translation_enabled :profile_image_url :profile_use_background_image :default_profile :profile_background_image_url :is_translator :profile_text_color :name :profile_background_image_url_https :favourites_count :screen_name :entities :listed_count :profile_image_url_https :statuses_count :has_extended_profile :contributors_enabled :following :lang :utc_offset :notifications :default_profile_image :status :profile_background_color :id :follow_request_sent :url :translator_type :time_zone :profile_sidebar_fill_color :protected :profile_background_tile :id_str :geo_enabled :location :followers_count :friends_count :verified :created_at])


;; (def clearly-interesting-user-keys
;;   [:description
;;    :screen_name
;;    :name
;;    :following
;;    :notifications
;;    :id
;;    :utc_offset
;;    :time_zone
;;    :url ;; should resolve from t.co
;;    :protected
;;    :location])


;; (->
;;  (get-body-optimistically api/users-lookup :user-id some-user-id)
;;  first
;;  (select-keys clearly-interesting-user-keys))

;; (followers-by-id "mathpunk")


