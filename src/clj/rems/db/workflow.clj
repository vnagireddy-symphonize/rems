(ns rems.db.workflow
  (:require [rems.application.events :as events]
            [rems.common.util :refer [apply-filters]]
            [rems.db.core :as db]
            [rems.json :as json]
            [rems.schema-base :as schema-base]
            [schema.coerce :as coerce]
            [schema.core :as s]
            [medley.core :refer [update-existing-in]]))

(s/defschema WorkflowBody
  {:type (apply s/enum events/workflow-types)
   :handlers [s/Str]
   (s/optional-key :forms) [{:form/id s/Num}]
   (s/optional-key :licenses) [s/Int]
   (s/optional-key :disable-commands) [schema-base/DisableCommandRule]})

(def ^:private coerce-workflow-body
  (coerce/coercer! WorkflowBody coerce/string-coercion-matcher))

(def ^:private validate-workflow-body
  (s/validator WorkflowBody))

(defn create-workflow! [{:keys [organization type title handlers forms licenses disable-commands]}]
  (let [body (cond-> {:type type
                      :handlers handlers
                      :forms forms
                      :licenses licenses}
               (seq disable-commands) (assoc :disable-commands disable-commands))]
    (:id (db/create-workflow! {:organization (:organization/id organization)
                               :title title
                               :workflow (json/generate-string
                                          (validate-workflow-body body))}))))

(defn- enrich-and-format-workflow [wf]
  (-> wf
      (update :workflow #(coerce-workflow-body (json/parse-string %)))
      (update :organization (fn [id] {:organization/id id}))
      (update-in [:workflow :licenses] #(mapv (fn [id] {:license/id id}) %))
      (update-in [:workflow :handlers] #(mapv (fn [userid] {:userid userid}) %))))

(defn get-workflow [id]
  (when-let [wf (db/get-workflow {:wfid id})]
    (enrich-and-format-workflow wf)))

(defn get-workflows [filters]
  (->> (db/get-workflows)
       (map enrich-and-format-workflow)
       (apply-filters filters)))

(defn get-all-workflow-roles [userid]
  (when (some #(contains? (set (map :userid (get-in % [:workflow :handlers]))) userid)
              (get-workflows nil))
    #{:handler}))

(defn- unrich-workflow [workflow]
  ;; TODO: keep handlers always in the same format, to avoid this conversion (we can ignore extra keys)
  (-> workflow
      (update-existing-in [:workflow :handlers] #(map :userid %))
      (update-existing-in [:workflow :licenses] #(map :license/id %))))

(defn edit-workflow! [{:keys [id organization title handlers disable-commands]}]
  (let [workflow (unrich-workflow (get-workflow id))
        workflow-body (cond-> (:workflow workflow)
                        handlers (assoc :handlers handlers)
                        disable-commands (assoc :disable-commands disable-commands))]
    (db/edit-workflow! {:id (or id (:id workflow))
                        :title (or title (:title workflow))
                        :organization (or (:organization/id organization)
                                          (get-in workflow [:organization :organization/id]))
                        :workflow (json/generate-string workflow-body)}))
  {:success true})

(defn set-workflow-enabled! [{:keys [id enabled]}]
  (db/set-workflow-enabled! {:id id :enabled enabled}))

(defn set-workflow-archived! [{:keys [id archived]}]
  (db/set-workflow-archived! {:id id :archived archived}))
