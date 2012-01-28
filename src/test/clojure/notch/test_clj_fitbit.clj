(ns notch.test-clj-fitbit
  (:require [clj-webdriver.core :as browser])
  (:use notch.clj-fitbit :reload)
  (:require [clojure.data.json :as json])
  (:require [clojure.string :as str])
  (:use clojure.test)
  )

(def testuser_email (get notch.clj-fitbit/properties "fitbit.test_user.email"))
(def testuser_password (get notch.clj-fitbit/properties "fitbit.test_user.password"))

(defn automated-fitbit-oauth [testuser_email testuser_password]
  (let [request_token (get-fitbit-request-token "http://127.0.0.1/random_string_lkjaosupdisuvna")
        ;;Start the browser
        b (browser/start {:browser :firefox} (:authorization_url request_token))
        ]
    ;; Input username/email into the "Login or Email" field
    (-> b
      (browser/find-it {:xpath "//input[@id='email']"}) ; :xpath and :css options
      (browser/input-text testuser_email))
    (-> b
      (browser/find-it {:xpath "//input[@id='password']"}) ; :xpath and :css options
      (browser/input-text testuser_password))
    (-> b
      (browser/find-it {:xpath "//input[@id='oauth_login_allow']"}) ; use of regular expressions
      browser/click)

    (Thread/sleep 2000)

    (let [verifier (second (re-find #"oauth_verifier=([a-z0-9]+)" (browser/current-url b)))
          access_token (get-fitbit-access-token (:token request_token) (:token_secret request_token) verifier)]
      (browser/close b)
      access_token
      )
    )
  )

(def fitbit_test_user (automated-fitbit-oauth testuser_email testuser_password))

(deftest test-clj-fitbit
  (let [;fitbit_test_user (automated-fitbit-oauth testuser_email testuser_password)
        userinfo (get-userinfo fitbit_test_user)]
    (is (and (not (str/blank? (:gender userinfo)))
          (not (str/blank? (:displayName userinfo)))
          (not (str/blank? (:encodedId userinfo)))))

    ;;Test subscription stuff
    ;(fitbit-call-post fitbit_test_user (str "apiSubscriptions/" 1337  ".json"))
    ;(is (= "1337" (:subscriptionId (first (:apiSubscriptions (fitbit-call fitbit_test_user (str "apiSubscriptions.json")))))))
    ;(delete-subscription fitbit_test_user 1337)
    ;(is (= 0 (count (:apiSubscriptions (fitbit-call fitbit_test_user (str "apiSubscriptions.json"))))))

    )
  )

;(fitbit-call-post fitbit_test_user (str "apiSubscriptions/" 1337  ".json"))
;(fitbit-call-post fitbit_test_user (str "apiSubscriptions/" 1338  ".json"))


;(run-tests 'notch.test-clj-fitbit)