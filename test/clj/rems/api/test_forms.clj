(ns ^:integration rems.api.test-forms
  (:require [clojure.test :refer :all]
            [rems.api.testing :refer :all]
            [rems.db.core :as db]
            [rems.db.form :as form]
            [rems.handler :refer [handler]]
            [ring.mock.request :refer :all])
  (:import (java.util UUID)))

(use-fixtures
  :once
  api-fixture)

(deftest forms-api-test
  (let [api-key "42"
        user-id "owner"]

    (testing "get"
      (let [data (-> (request :get "/api/forms")
                     (authenticate api-key user-id)
                     handler
                     assert-response-is-ok
                     read-body)]
        (is (:id (first data)))))

    (testing "create"
      (let [command {:organization "abc"
                     :title (str "form title " (UUID/randomUUID))
                     :items [{:title {:en "en title"
                                      :fi "fi title"}
                              :optional true
                              :type "text"
                              :input-prompt {:en "en prompt"
                                             :fi "fi prompt"}}]}]

        (testing "invalid create"
          ;; TODO: silence the logging for this expected error
          (let [command-with-invalid-maxlength (assoc-in command [:items 0 :maxlength] -1)
                response (-> (request :post "/api/forms/create")
                             (authenticate api-key user-id)
                             (json-body command-with-invalid-maxlength)
                             handler)]
            (is (= 400 (:status response))
                "can't send negative maxlength")))

        (testing "invalid create: field too long"
          (let [command-with-long-prompt (assoc-in command [:items 0 :input-prompt :en]
                                                   (apply str (repeat 10000 "x")))
                response (-> (request :post "/api/forms/create")
                             (authenticate api-key user-id)
                             (json-body command-with-long-prompt)
                             handler)]
            (is (= 500 (:status response)))))

        (testing "valid create"
          (-> (request :post "/api/forms/create")
              (authenticate api-key user-id)
              (json-body command)
              handler
              assert-response-is-ok))

        (testing "and fetch"
          (let [body (-> (request :get "/api/forms")
                         (authenticate api-key user-id)
                         handler
                         assert-response-is-ok
                         read-body)
                forms (->> body
                           (filter #(= (:title %) (:title command))))
                form (first forms)
                form-template (-> (request :get (str "/api/forms/v2/" (:id form)))
                                  (authenticate api-key user-id)
                                  handler
                                  assert-response-is-ok
                                  read-body)]
            (is (= 1 (count forms))
                "only one form got created")

            (testing "v2 result matches input"
              (is (= (select-keys command [:title :organization])
                     (select-keys form-template [:title :organization])))
              (is (= (:items command)
                     (:fields form-template))))
            (testing "v1 result matches input"
              (is (= (select-keys command [:title :organization])
                     (select-keys form [:title :organization])))
              (is (= (:items command)
                     (:fields (form/get-form-template (:id form))))))))))))

(deftest form-update-test
  (let [api-key "42"
        user-id "owner"
        form-id (-> (request :post "/api/forms/create")
                    (authenticate api-key user-id)
                    (json-body {:organization "abc" :title "form update test"
                                :items []})
                    handler
                    read-ok-body
                    :id)]
    (is (not (nil? form-id)))
    (testing "update"
      (is (:success (-> (request :put "/api/forms/update")
                        (authenticate api-key user-id)
                        (json-body {:id form-id
                                    :enabled false
                                    :archived true})
                        handler
                        read-ok-body))))
    (testing "fetch"
      (let [form (-> (request :get (str "/api/forms/" form-id))
                     (authenticate api-key user-id)
                     handler
                     read-ok-body)]
        (is (false? (:enabled form)))
        (is (true? (:archived form)))))
    (testing "fetch v2"
      (let [form (-> (request :get (str "/api/forms/v2/" form-id))
                     (authenticate api-key user-id)
                     handler
                     read-ok-body)]
        (is (false? (:enabled form)))
        (is (true? (:archived form)))))
    (testing "update again"
      (is (:success (-> (request :put "/api/forms/update")
                        (authenticate api-key user-id)
                        (json-body {:id form-id
                                    :enabled true
                                    :archived false})
                        handler
                        read-ok-body))))
    (testing "fetch again"
      (let [form (-> (request :get (str "/api/forms/" form-id))
                     (authenticate api-key user-id)
                     handler
                     read-ok-body)]
        (is (true? (:enabled form)))
        (is (false? (:archived form)))))
    (testing "fetch v2 again"
      (let [form (-> (request :get (str "/api/forms/v2/" form-id))
                     (authenticate api-key user-id)
                     handler
                     read-ok-body)]
        (is (true? (:enabled form)))
        (is (false? (:archived form)))))
    (testing "can't archive a form that's in use"
      (let [catalogue-id (:id (db/create-catalogue-item! {:title "catalogue item"
                                                          :form form-id
                                                          :enabled true :archived false
                                                          :resid nil :wfid nil}))
            resp (-> (request :put "/api/forms/update")
                     (authenticate api-key user-id)
                     (json-body {:id form-id
                                 :enabled true
                                 :archived false})
                     handler
                     read-ok-body)]
        (is (false? (:success resp)))
        (is (= [{:type "t.administration.errors/form-in-use" :catalogue-items [catalogue-id]}]
               (:errors resp)))
        (testing "but can archive a form that's not in use"
          (db/set-catalogue-item-state! {:id catalogue-id :enabled true :archived true})
          (is (true? (-> (request :put "/api/forms/update")
                         (authenticate api-key user-id)
                         (json-body {:id form-id
                                     :enabled true
                                     :archived false})
                         handler
                         read-ok-body
                         :success))))))))

(deftest option-form-item-test
  (let [api-key "42"
        user-id "owner"]
    (testing "create"
      (let [command {:organization "abc"
                     :title (str "form title " (UUID/randomUUID))
                     :items [{:title {:en "en title"
                                      :fi "fi title"}
                              :optional true
                              :type "option"
                              :options [{:key "yes"
                                         :label {:en "Yes"
                                                 :fi "Kyllä"}}
                                        {:key "no"
                                         :label {:en "No"
                                                 :fi "Ei"}}]}]}]
        (-> (request :post "/api/forms/create")
            (authenticate api-key user-id)
            (json-body command)
            handler
            assert-response-is-ok)

        (testing "and fetch"
          (let [body (-> (request :get "/api/forms")
                         (authenticate api-key user-id)
                         handler
                         assert-response-is-ok
                         read-body)
                form (->> body
                          (filter #(= (:title %) (:title command)))
                          first)]
            (is (= (:items command)
                   (:fields (form/get-form-template (:id form)))))))))))

(deftest forms-api-filtering-test
  (let [unfiltered (-> (request :get "/api/forms")
                       (authenticate "42" "owner")
                       handler
                       assert-response-is-ok
                       read-body)
        filtered (-> (request :get "/api/forms" {:active true})
                     (authenticate "42" "owner")
                     handler
                     assert-response-is-ok
                     read-body)]
    (is (coll-is-not-empty? unfiltered))
    (is (coll-is-not-empty? filtered))
    (is (every? #(contains? % :active) unfiltered))
    (is (every? :active filtered))
    (is (< (count filtered) (count unfiltered)))))

(deftest forms-api-security-test
  (testing "without authentication"
    (testing "list"
      (let [response (-> (request :get (str "/api/forms"))
                         handler)
            body (read-body response)]
        (is (response-is-unauthorized? response))
        (is (= "unauthorized" body))))
    (testing "create"
      (let [response (-> (request :post "/api/forms/create")
                         (json-body {:organization "abc"
                                     :title "the title"
                                     :items []})
                         handler)]
        (is (response-is-unauthorized? response))
        (is (= "Invalid anti-forgery token" (read-body response))))))

  (testing "without owner role"
    (testing "list"
      (let [response (-> (request :get (str "/api/forms"))
                         (authenticate "42" "alice")
                         handler)
            body (read-body response)]
        (is (response-is-forbidden? response))
        (is (= "forbidden" body))))
    (testing "create"
      (let [response (-> (request :post "/api/forms/create")
                         (authenticate "42" "alice")
                         (json-body {:organization "abc"
                                     :title "the title"
                                     :items []})
                         handler)]
        (is (response-is-forbidden? response))
        (is (= "forbidden" (read-body response)))))))
