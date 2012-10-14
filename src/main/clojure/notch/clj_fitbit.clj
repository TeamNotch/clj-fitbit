(ns notch.clj-fitbit
  (:use clojure.tools.logging)
  (:import [com.fitbit.api.client FitbitAPIEntityCache FitbitApiCredentialsCache FitbitApiSubscriptionStorage])
  (:import [com.fitbit.api.client.service FitbitAPIClientService])
  (:import [com.fitbit.api.FitbitApiError])
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
  {:pre [(and (:token user) (:token_secret user))]}
  (try
    (binding [*client* (create-fitbit-client (:token user) (:token_secret user))]
      (func))
    (catch Exception e
      (do (error e)
        (throw e))))
  )

(defmacro ^{:private true} with-user [user & body]
  `(with-user* ~user (fn [] ~@body)))

(defn- fitbit-call [user uri]
  (with-user user
    (-> *client*
      (.get (str fitbit_api_url "/1/" uri) true)
      (.asString)
      (json/read-json)
      )
    )
  )

(defn- map-to-postparameters [m]
  (->> m
    (map #(com.fitbit.api.client.http.PostParameter. (name (key %)) (str (val %))))
    (into-array com.fitbit.api.client.http.PostParameter)
    ))

(defn- fitbit-call-post [user uri params]
(debug (str uri " " params))
  (with-user user
    (-> *client*
      (.post (str fitbit_api_url "/1/" uri) (map-to-postparameters params) true)
      (.asString)
      (json/read-json)
      )
    )
  )

(defn- fitbit-call-delete [user uri]
  (with-user user
    (-> *client*
      (.delete (str fitbit_api_url "/1/" uri) true)
      (.asString)
      ;(json/read-json)
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
    (fitbit-call user "user/-/profile.json")))

#_(defn get-steps [user start_day stop_day]
    (:activities-steps
      (fitbit-call user (str "user/-/activities/steps/date/" start_day "/" stop_day ".json"))))

(defn get-minutes-asleep [user start_day stop_day]
  (:sleep-minutesAsleep
    (fitbit-call user (str "user/-/sleep/minutesAsleep/date/" start_day "/" stop_day ".json"))))

(defn get-minutes-awake [user start_day stop_day]
  (:sleep-minutesAwake
    (fitbit-call user (str "user/-/sleep/minutesAwake/date/" start_day "/" stop_day ".json"))))

(defn get-minutes-in-bed [user start_day stop_day]
  (:sleep-timeInBed
    (fitbit-call user (str "user/-/sleep/timeInBed/date/" start_day "/" stop_day ".json"))))

(defn get-bedtime [user start_day stop_day]
  (:sleep-startTime
    (fitbit-call user (str "user/-/sleep/startTime/date/" start_day "/" stop_day ".json"))))

(defn get-bedtime [user start_day stop_day]
  (:sleep-startTime
    (fitbit-call user (str "user/-/sleep/startTime/date/" start_day "/" stop_day ".json"))))

(defn get-sleep [user day]
  (:sleep
    (fitbit-call user (str "user/-/sleep/date/" day".json"))))

(defn get-steps [user day]
  (:dataset
    (:activities-steps-intraday
      (fitbit-call user (str "user/-/activities/steps/date/" day "/1d.json")))))

(defn get-steps-series
  "Get total steps for a day over a range of days"
  [user start_day stop_day]
  (:activities-steps
      (fitbit-call user (str "user/-/activities/steps/date/" start_day "/" stop_day ".json"))))

(defn get-calories-series
  "Get total calories for a day over a range of days"
  [user start_day stop_day]
  (:activities-calories
    (fitbit-call user (str "user/-/activities/calories/date/" start_day "/" stop_day ".json"))))

(defn get-weight-series
  "Get weight for a day over a range of days"
  [user start_day stop_day]
  (:body-weight
    (fitbit-call user (str "user/-/body/weight/date/" start_day "/" stop_day ".json"))))

(defn get-calories-in-series
  "Get calories in for a day over a range of days"
  [user start_day stop_day]
  (:foods-log-caloriesIn
  (fitbit-call user (str "user/-/foods/log/caloriesIn/date/" start_day "/" stop_day ".json"))))

(defn get-water-in-series
  "Get water in for a day over a range of days"
  [user start_day stop_day]
  (:foods-log-water
    (fitbit-call user (str "user/-/foods/log/water/date/" start_day "/" stop_day ".json"))))



(defn subscribe [user subscriber_id]
  (fitbit-call-post user (str "user/-/apiSubscriptions/" subscriber_id  ".json")))

(defn get-subscriptions [user]
  (fitbit-call user (str "user/-/apiSubscriptions.json"))
  )

(defn delete-subscription [user subscriber_id]
  (fitbit-call-delete user (str "user/-/apiSubscriptions/" subscriber_id ".json"))
  )

(defn search-foods [user query]
  (fitbit-call user (str "foods/search.json?query=" (java.net.URLEncoder/encode query)))
  )

(defn browse-activities [user]
  (fitbit-call user (str "activities.json" ))
  )

#_(defn log-food [user food_name ^Number amount date]
  (let [params {:foodName food_name
                :mealTypeId 7
                :unitId 304
                :amount amount
                :date date}]
    (fitbit-call-post user (str "user/-/foods/log.json") params))

  )

(defn log-activity [user activity_id date start_time duration]
  (let [params {:activityId activity_id
                :date date
                :startTime start_time
                :durationMillis duration}]
    (fitbit-call-post user (str "user/-/activities.json") params))

  )

(defn get-activities [user day]
  (:activities
    (fitbit-call user (str "user/-/activities/date/" day".json"))))

(defn delete-activity [user activity_log_id]
  (fitbit-call-delete user (str "user/-/activities/" activity_log_id ".json")))


