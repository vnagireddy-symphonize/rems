(ns rems.test.locales
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [rems.locales :as locales]
            [rems.test.testing :refer [create-temp-dir delete-recursively]]
            [rems.util :refer [getx-in]])
  (:import (java.io FileNotFoundException)))

(def loc-en (read-string (slurp (io/resource "translations/en.edn"))))

(def loc-fi (read-string (slurp (io/resource "translations/fi.edn"))))

(defn map-structure
  "Recurse into map m and replace all leaves with true."
  [m]
  (let [transform (fn [v] (if (map? v) (map-structure v) true))]
    (reduce-kv (fn [m k v] (assoc m k (transform v))) {} m)))

(deftest test-all-languages-defined
  (is (= (map-structure loc-en)
         (map-structure loc-fi))))

(deftest load-translations-test
  (testing "loads internal translations"
    (let [translations (locales/load-translations {:languages [:en :fi]
                                                   :translations-directory "translations/"
                                                   :extra-translations-directory nil})]
      (is (= [:en :fi] (sort (keys translations))))
      (is (not (empty? (:en translations))))
      (is (not (empty? (:fi translations))))))

  (testing "loads external translations"
    (let [translations-dir (create-temp-dir)
          config {:translations-directory translations-dir
                  :extra-translations-directory nil
                  :languages [:xx]}
          translation {:some-key "some val"}]
      (try
        (spit (io/file translations-dir "xx.edn")
              (pr-str translation))
        (is (= translation (:xx (locales/load-translations config))))
        (finally
          (delete-recursively translations-dir)))))

  (testing "loads translations only for listed languages"
    (is (= [:en] (keys (locales/load-translations {:languages [:en]
                                                   :translations-directory "translations/"
                                                   :extra-translations-directory nil}))))
    (is (= [:fi] (keys (locales/load-translations {:languages [:fi]
                                                   :translations-directory "translations/"
                                                   :extra-translations-directory nil})))))

  (testing "missing translations-directory in config is an error"
    (is (thrown-with-msg? RuntimeException #"^\Q:translations-directory was not set in config\E$"
                          (locales/load-translations {:languages [:xx]}))))

  (testing "missing translations is an error"
    (is (thrown-with-msg? FileNotFoundException #"^\Qtranslations could not be found in some-dir/xx.edn file or some-dir/xx.edn resource\E$"
                          (locales/load-translations {:translations-directory "some-dir/"
                                                      :languages [:xx]})))))

(deftest override-translations-with-extra-translations
  (testing "extra translations override translations"
    (let [translations (locales/load-translations {:languages [:en]
                                                   :translations-directory "translations/"
                                                   :extra-translations-directory "test-data/translations-test/"})]
      (is (= "Overridden extra translation" (getx-in translations [:en :t :actions :applicant])))))
  (testing "extra translations don't override keys that are not defined in extras"
    (let [translations (locales/load-translations {:languages [:en]
                                                   :translations-directory "translations/"
                                                   :extra-translations-directory "test-data/translations-test/"})]
      (is (= "Active" (getx-in translations [:en :t :administration :active]))))))

