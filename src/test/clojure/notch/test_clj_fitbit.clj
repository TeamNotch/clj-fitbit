(ns notch.test-clj-fitbit
  (:require [clj-webdriver.core :as browser])
  (:use notch.clj-fitbit :reload)
  (:require [clojure.data.json :as json])
  (:require [clojure.string :as str])
  (:use clojure.test)
  )

(def testuser_email (get notch.clj-fitbit/properties "fitbit.test_user.email"))
(def testuser_password (get notch.clj-fitbit/properties "fitbit.test_user.password"))
(def testuser_token (get notch.clj-fitbit/properties "fitbit.test_user.token"))
(def testuser_tokensecret (get notch.clj-fitbit/properties "fitbit.test_user.token_secret"))
(def fitbit_test_user {:token testuser_token
                  :token_secret testuser_tokensecret })

;;;;For hacking around in this file:
#_(def fitbit_test_user
  (automated-fitbit-oauth "EMAIL" "PASSWORD"))

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


(get-userinfo fitbit_test_user)
(get-steps-series fitbit_test_user "2012-01-01" "2012-02-01")

(deftest test-clj-fitbit
  ;;Test oauth stuff by authing and getting the user info
  (let [fitbit_test_user (automated-fitbit-oauth testuser_email testuser_password)
        userinfo (get-userinfo fitbit_test_user)]
    (is (and (not (str/blank? (:gender userinfo)))
          (not (str/blank? (:displayName userinfo)))
          (not (str/blank? (:encodedId userinfo)))))

    )

  ;;Smoke test getting steps series
  (is (< 0
        (count (get-steps-series fitbit_test_user "2012-01-01" "2012-02-01"))))
  )

;(run-tests 'notch.test-clj-fitbit)



