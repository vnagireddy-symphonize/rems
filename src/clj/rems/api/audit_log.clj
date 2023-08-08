(ns rems.api.audit-log
  (:require [compojure.api.sweet :refer :all]
            [rems.service.audit-log :as audit-log]
            [schema.core :as s])
  (:import (org.joda.time DateTime)))

(s/defschema AuditLogEntry
  {:time DateTime
   :path s/Str
   :method s/Str
   :apikey (s/maybe s/Str)
   :userid (s/maybe s/Str)
   :status s/Str})

(def audit-log-api
  (context "/audit-log" []
    :tags ["audit log"]
    (GET "/" []
      :summary "Get audit log entries"
      :query-params [{userid :- (describe s/Str "Only show entries for this user") nil}
                     {application-id :- (describe s/Str "Only show requests for `/api/application/<application>*` endpoints") nil}
                     {after :- (describe DateTime "Only show entries after this time") nil}
                     {before :- (describe DateTime "Only show entries before this time") nil}]
      :roles #{:reporter}
      :return [AuditLogEntry]
      (audit-log/get-audit-log {:userid userid
                                :after after
                                :path (when application-id
                                        (str "/api/applications/" application-id "%"))
                                :before before}))))

(defn get-audit-log []
  (audit-log/get-audit-log))

(defn add-to-audit-log! [entry]
  (audit-log/add-to-audit-log! entry))
