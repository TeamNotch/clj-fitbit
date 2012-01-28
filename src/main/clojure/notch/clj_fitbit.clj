(ns notch.clj-fitbit
  (:import [com.fitbit.api.client FitbitAPIEntityCache FitbitApiCredentialsCache FitbitApiSubscriptionStorage])
  (:import [com.fitbit.api.client.service FitbitAPIClientService])
  (:require [clojure.data.json :as json])
  (:require [clojure.string :as str])
  )

(defn- load-properties
  "load and return a properties file"
  [filename]
  (let [properties (doto (java.util.Properties.)
    (.load
      (-> (Thread/currentThread)
        (.getContextClassLoader)
        (.getResourceAsStream filename))))]
    (reduce merge {}
      (for [key (.keySet properties)]
        {key (.get properties key)}
        )))
  )

(def properties
  (load-properties "fitbit.properties"))

(def fitbit_api_url (get properties "fitbit.api_url"))
(def consumer_key (get properties "fitbit.consumer_key"))
(def consumer_secret (get properties "fitbit.consumer_secret"))

(defn- create-fitbit-client
  ([token token_secret]
    (doto (create-fitbit-client)
      (.setOAuthAccessToken (com.fitbit.api.client.http.AccessToken. token token_secret))))
  ([]
    (doto (com.fitbit.api.client.http.HttpClient.)
      (.setOAuthConsumer consumer_key consumer_secret)
      (.setRetryCount 0)
      (.setRequestHeader "X-Fitbit-Client" "Your mom")
      (.setRequestHeader "X-Fitbit-Client-Version" "1")
      (.setRequestHeader "X-Fitbit-Client-URL" "http://wiki.fitbit.com/Fitbit-API-Java-Client")
      (.setRequestTokenURL "https://api.fitbit.com/oauth/request_token")
      (.setAuthorizationURL "https://api.fitbit.com/oauth/authorize")
      (.setAccessTokenURL "https://api.fitbit.com/oauth/access_token")
      )))

(def ^{:dynamic true} *client*)

(defn with-user* [user func]
  (binding [*client* (create-fitbit-client (:token user) (:token_secret user))]
    (func)
    )
  )

(defmacro ^{:private true} with-user [user & body]
  `(with-user* ~user (fn [] ~@body)))

(defn- fitbit-call [user uri]
  (with-user user
    (-> *client*
      (.get (str fitbit_api_url "/1/user/-/" uri) true)
      (.asString)
      (json/read-json)
      )
    )
  )

(defn- fitbit-call-post [user uri]
  (with-user user
    (-> *client*
      (.post (str fitbit_api_url "/1/user/-/" uri) true)
      (.asString)
      (json/read-json)
      )
    )
  )

(defn- fitbit-call-delete [user uri]
  (with-user user
    (-> *client*
      (.delete (str fitbit_api_url "/1/user/-/" uri) true)
      (.asString)
      (json/read-json)
      )
    )
  )

(defn get-fitbit-request-token
  "Make a call to the fitbit api to get the oauth request token"
  [callback_url]
  (let [java_token (-> (create-fitbit-client)
    (.getOauthRequestToken callback_url)
    )]
    {:token (.getToken java_token)
     :token_secret (.getTokenSecret java_token)
     :authorization_url (.getAuthorizationURL java_token)}))

(defn get-fitbit-access-token
  "Second step in fitbit oauth process"
  [request_token request_token_secret verifier]
  (let [java_token (-> (create-fitbit-client)
    (.getOAuthAccessToken request_token request_token_secret verifier))]
    {:token (.getToken java_token)
     :token_secret (.getTokenSecret java_token)
     }))


(defn get-userinfo [user]
  (:user
    (fitbit-call user "profile.json")))

#_(defn get-steps [user start_day stop_day]
    (:activities-steps
      (fitbit-call user (str "activities/steps/date/" start_day "/" stop_day ".json"))))

(defn get-minutes-asleep [user start_day stop_day]
  (:sleep-minutesAsleep
    (fitbit-call user (str "sleep/minutesAsleep/date/" start_day "/" stop_day ".json"))))

(defn get-minutes-awake [user start_day stop_day]
  (:sleep-minutesAwake
    (fitbit-call user (str "sleep/minutesAwake/date/" start_day "/" stop_day ".json"))))

(defn get-minutes-in-bed [user start_day stop_day]
  (:sleep-timeInBed
    (fitbit-call user (str "sleep/timeInBed/date/" start_day "/" stop_day ".json"))))

(defn get-bedtime [user start_day stop_day]
  (:sleep-startTime
    (fitbit-call user (str "sleep/startTime/date/" start_day "/" stop_day ".json"))))

(defn get-bedtime [user start_day stop_day]
  (:sleep-startTime
    (fitbit-call user (str "sleep/startTime/date/" start_day "/" stop_day ".json"))))

(defn get-sleep [user day]
  (:sleep
    (fitbit-call user (str "sleep/date/" day".json"))))

(defn get-steps [user day]
  (:dataset
    (:activities-steps-intraday
      (fitbit-call user (str "activities/steps/date/" day "/1d.json")))))

(defn subscribe [user subscriber_id]
  (fitbit-call-post user (str "apiSubscriptions/" subscriber_id  ".json")))

(defn get-subscriptions [user]
  (fitbit-call user (str "apiSubscriptions.json"))
  )

(defn delete-subscription [user subscriber_id]
  (fitbit-call-delete user (str "apiSubscriptions/" subscriber_id ".json"))
  )