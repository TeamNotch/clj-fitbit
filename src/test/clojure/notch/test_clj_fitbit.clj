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

;(automated-fitbit-oauth testuser_email testuser_password)

(deftest test-clj-fitbit
  (let [fitbit_test_user (automated-fitbit-oauth testuser_email testuser_password)
        userinfo (get-userinfo fitbit_test_user)]
    (is (and (not (str/blank? (:gender userinfo)))
          (not (str/blank? (:displayName userinfo)))
          (not (str/blank? (:encodedId userinfo)))))

    )
  )
#_(def green_tea_results
  {:foods [{:accessLevel "PUBLIC", :brand "Honest Tea", :calories 30, :defaultServingSize 1, :defaultUnit {:id 304, :name "serving", :plural "servings"}, :foodId 61614, :name "Green Dragon Tea", :units [304 209 189 128 364 349 91 256 279]} {:accessLevel "PUBLIC", :brand "", :calories 63, :defaultServingSize 8, :defaultUnit {:id 128, :name "fl oz", :plural "fl oz"}, :foodId 81042, :name "Green Tea", :units [304 209 189 128 364 349 91 256 279 226 180 147 389]} {:accessLevel "PUBLIC", :brand "Fuze", :calories 0, :defaultServingSize 8, :defaultUnit {:id 128, :name "fl oz", :plural "fl oz"}, :foodId 5748, :name "Green Tea, Diet", :units [209 189 128 364 349 91 256 279]} {:accessLevel "PUBLIC", :brand "Meijer", :calories 0, :defaultServingSize 1, :defaultUnit {:id 304, :name "serving", :plural "servings"}, :foodId 63429, :name "Green Tea Bag", :units [304 251]} {:accessLevel "PUBLIC", :brand "Bob Evans", :calories 2, :defaultServingSize 1, :defaultUnit {:id 304, :name "serving", :plural "servings"}, :foodId 40644, :name "Green Tea", :units [304 226 180 147 389]} {:accessLevel "PUBLIC", :brand "Kozy Shack", :calories 100, :defaultServingSize 4, :defaultUnit {:id 226, :name "oz", :plural "oz"}, :foodId 252090, :name "Green Tea Chai", :units [226 180 147 389]} {:accessLevel "PUBLIC", :brand "Dunkin Donuts", :calories 0, :defaultServingSize 10, :defaultUnit {:id 128, :name "fl oz", :plural "fl oz"}, :foodId 41647, :name "Green Tea", :units [209 189 128 364 349 91 256 279]} {:accessLevel "PUBLIC", :brand "Yogurtland", :calories 107, :defaultServingSize 0.5, :defaultUnit {:id 91, :name "cup", :plural "cups"}, :foodId 51049, :name "Green Tea Frozen Yogurt", :units [91 256 279 226 180 147 389]} {:accessLevel "PUBLIC", :brand "Fuze", :calories 60, :defaultServingSize 8, :defaultUnit {:id 128, :name "fl oz", :plural "fl oz"}, :foodId 10274, :name "Green Tea", :units [209 189 128 364 349 91 256 279]} {:accessLevel "PUBLIC", :brand "Nestle toll House cafe", :calories 140, :defaultServingSize 0.5, :defaultUnit {:id 91, :name "cup", :plural "cups"}, :foodId 52003, :name "Green Tea Ice Cream", :units [91 256 279]} {:accessLevel "PUBLIC", :brand "Country Style", :calories 190, :defaultServingSize 1, :defaultUnit {:id 304, :name "serving", :plural "servings"}, :foodId 39846, :name "Green Tea Latte", :units [304 226 180 147 389]} {:accessLevel "PUBLIC", :brand "Yogurt Stop", :calories 424, :defaultServingSize 1, :defaultUnit {:id 91, :name "cup", :plural "cups"}, :foodId 43915, :name "Green Tea Latte Powder", :units [91 256 279 226 180 147 389]} {:accessLevel "PUBLIC", :brand "Nestea", :calories 85, :defaultServingSize 8, :defaultUnit {:id 128, :name "fl oz", :plural "fl oz"}, :foodId 70494, :name "Green Tea Mix, Citrus", :units [209 189 128 364 349 91 256 279]} {:accessLevel "PUBLIC", :brand "The Human Bean", :calories 63, :defaultServingSize 1, :defaultUnit {:id 301, :name "scoop", :plural "scoops"}, :foodId 45389, :name "Green Tea Powders", :units [301]} {:accessLevel "PUBLIC", :brand "Roland", :calories 0, :defaultServingSize 5, :defaultUnit {:id 147, :name "gram", :plural "grams"}, :foodId 38419, :name "Green Tea", :units [226 180 147 389]} {:accessLevel "PUBLIC", :brand "The Human Bean", :calories 269, :defaultServingSize 1, :defaultUnit {:id 304, :name "serving", :plural "servings"}, :foodId 45390, :name "Green Tea Smoothies  12Oz", :units [304 226 180 147 389]} {:accessLevel "PUBLIC", :brand "The Human Bean", :calories 209, :defaultServingSize 1, :defaultUnit {:id 304, :name "serving", :plural "servings"}, :foodId 45394, :name "Green Tea Smoothies  8Oz", :units [304 226 180 147 389]} {:accessLevel "PUBLIC", :brand "Snapple", :calories 60, :defaultServingSize 8, :defaultUnit {:id 128, :name "fl oz", :plural "fl oz"}, :foodId 37454, :name "Green Tea", :units [209 189 128 364 349 91 256 279]} {:accessLevel "PUBLIC", :brand "Souper Salad", :calories 45, :defaultServingSize 1, :defaultUnit {:id 304, :name "serving", :plural "servings"}, :foodId 54718, :name "Green Tea", :units [304]} {:accessLevel "PUBLIC", :brand "Ys", :calories 110, :defaultServingSize 1, :defaultUnit {:id 91, :name "cup", :plural "cups"}, :foodId 15906, :name "Green Tea Soy Latte", :units [91 256 279]} {:accessLevel "PUBLIC", :brand "T.G. Lee Dairy", :calories 70, :defaultServingSize 1, :defaultUnit {:id 304, :name "serving", :plural "servings"}, :foodId 68993, :name "Green Tea", :units [304 209 189 128 364 349 91 256 279]} {:accessLevel "PUBLIC", :brand "Lipton", :calories 60, :defaultServingSize 8, :defaultUnit {:id 128, :name "fl oz", :plural "fl oz"}, :foodId 233946, :name "Green Tea With Honey", :units [209 189 128 364 349 91 256 279]} {:accessLevel "PUBLIC", :brand "Dunkin Donuts", :calories 20, :defaultServingSize 10, :defaultUnit {:id 128, :name "fl oz", :plural "fl oz"}, :foodId 41648, :name "Green Tea with Milk", :units [209 189 128 364 349 91 256 279]} {:accessLevel "PUBLIC", :brand "Dunkin Donuts", :calories 10, :defaultServingSize 10, :defaultUnit {:id 128, :name "fl oz", :plural "fl oz"}, :foodId 41650, :name "Green Tea with Skim Milk", :units [209 189 128 364 349 91 256 279]} {:accessLevel "PUBLIC", :brand "Dunkin Donuts", :calories 60, :defaultServingSize 10, :defaultUnit {:id 128, :name "fl oz", :plural "fl oz"}, :foodId 41652, :name "Green Tea with Sugar", :units [209 189 128 364 349 91 256 279]}]}
  )

#_(->> green_tea_results
  :foods
  (map :name))
#_(search-foods fitbit_test_user "green tea")

#_(log-food fitbit_test_user "green tea" 1 "2012-01-27")
#_(log-activity fitbit_test_user 4030 "2012-01-27" "23:01" (* 60 1000))
;(run-tests 'notch.test-clj-fitbit)
#_(get-activities fitbit_test_user "2012-01-27")
#_(->
  (browse-activities fitbit_test_user )
  :categories
  (second)
  )

#_(for [activity_id (map :logId (get-activities fitbit_test_user "2012-01-27"))]
  (delete-activity fitbit_test_user activity_id)
)

