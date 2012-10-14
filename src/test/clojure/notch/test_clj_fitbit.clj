(ns notch.test-clj-fitbit
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

(get-calories-series fitbit_test_user (:memberSince (get-userinfo fitbit_test_user)) "2015-01-01")

(deftest test-basics
    (is (not-empty (get-userinfo fitbit_test_user)))
    (is (not-empty (get-steps-series fitbit_test_user (:memberSince (get-userinfo fitbit_test_user)) "2015-01-01")))
    (is (not-empty (get-calories-series fitbit_test_user (:memberSince (get-userinfo fitbit_test_user)) "2015-01-01")))
    (is (not-empty (get-weight-series fitbit_test_user (:memberSince (get-userinfo fitbit_test_user)) "2015-01-01")))
    (is (not-empty (get-calories-in-series fitbit_test_user (:memberSince (get-userinfo fitbit_test_user)) "2015-01-01")))
    (is (not-empty (get-water-in-series fitbit_test_user (:memberSince (get-userinfo fitbit_test_user)) "2015-01-01")))

  )

;(run-tests 'notch.test-clj-fitbit)



