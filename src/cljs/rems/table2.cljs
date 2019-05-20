(ns rems.table2
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [rems.atoms :refer [close-symbol search-symbol sort-symbol]]
            [rems.text :refer [text]]))

(defn- flip [order]
  (case order
    :desc :asc
    :desc))

(defn- change-sort-order [old-column old-order new-column]
  (if (= old-column new-column)
    (flip old-order)
    :asc))

(rf/reg-event-db
 ::toggle-sorting
 (fn [db [_ spec sort-column]]
   (update-in db [::sorting (:id spec)]
              (fn [sorting]
                (-> sorting
                    (assoc :sort-column sort-column)
                    (assoc :sort-order (change-sort-order (:sort-column sorting)
                                                          (:sort-order sorting)
                                                          sort-column)))))))

(rf/reg-sub
 ::sorting
 (fn [db [_ spec]]
   (or (get-in db [::sorting (:id spec)])
       {:sort-order :asc
        :sort-column (:default-sort-column spec)})))

(rf/reg-event-db
 ::set-filtering
 (fn [db [_ spec filtering]]
   (assoc-in db [::filtering (:id spec)] filtering)))

(rf/reg-sub
 ::filtering
 (fn [db [_ spec]]
   (get-in db [::filtering (:id spec)])))

(rf/reg-sub
 ::sorted-rows
 (fn [[_ spec] _]
   [(rf/subscribe [(:rows spec)])
    (rf/subscribe [::sorting spec])])
 (fn [[rows sorting] _]
   (->> rows
        (sort-by #(get-in % [(:sort-column sorting) :sort-value])
                 (case (:sort-order sorting)
                   :desc #(compare %2 %1)
                   #(compare %1 %2))))))

(defn- display-row? [row columns filters]
  (some (fn [column]
          (str/includes? (get-in row [(:key column) :filter-value])
                         filters))
        columns))

(rf/reg-sub
 ::sorted-and-filtered-rows
 (fn [[_ spec] _]
   [(rf/subscribe [::sorted-rows spec])
    (rf/subscribe [::filtering spec])])
 (fn [[rows filtering] [_ spec]]
   (let [filters (str/lower-case (str (:filters filtering)))
         columns (->> (:columns spec)
                      (filter :filterable?))]
     (->> rows
          (map (fn [row]
                 ;; performance optimization: hide DOM nodes instead of destroying them
                 (assoc row ::display-row? (display-row? row columns filters))))))))

(defn search [spec]
  (let [filtering @(rf/subscribe [::filtering spec])
        on-search (fn [event]
                    (rf/dispatch [::set-filtering spec (-> filtering
                                                           (assoc :filters (-> event .-target .-value)))]))
        ;; TODO: focus needs to be moved to the search field after opening it, especially for screen readers
        on-toggle (fn [_event]
                    (rf/dispatch [::set-filtering spec (-> filtering
                                                           (update :show-filters not)
                                                           (assoc :filters ""))]))]
    (if (:show-filters filtering)
      [:div.rems-table-search-toggle.d-flex.flex-row
       [:div.flex-grow-1.d-flex
        [:input.flex-grow-1 {:type "text"
                             :default-value (:filters filtering)
                             :aria-label (text :t.search/search-parameters)
                             :on-change on-search}]]
       [:button.btn.btn-secondary {:type "button"
                                   :aria-label (text :t.search/close-search)
                                   :on-click on-toggle}
        [close-symbol]]]
      [:div.rems-table-search-toggle.d-flex.flex-row-reverse
       [:button.btn.btn-primary {:type "button"
                                 :aria-label (text :t.search/open-search)
                                 :on-click on-toggle}
        [search-symbol]]])))

(defn- table-header [spec]
  (let [sorting @(rf/subscribe [::sorting spec])]
    (into [:tr]
          (for [column (:columns spec)]
            [:th
             (when (:sortable? column)
               {:on-click #(rf/dispatch [::toggle-sorting spec (:key column)])})
             (:title column)
             " "
             (when (:sortable? column)
               (when (= (:key column) (:sort-column sorting))
                 [sort-symbol (:sort-order sorting)]))]))))

(defn- table-row [row spec]
  ;; performance optimization: hide DOM nodes instead of destroying them
  (into [:tr {:style {:display (if (::display-row? row)
                                 "table-row"
                                 "none")}}]
        (for [column (:columns spec)]
          (get-in row [(:key column) :td]))))

(defn table [spec]
  (let [rows @(rf/subscribe [::sorted-and-filtered-rows spec])
        language @(rf/subscribe [:language])]
    [:div.table-border
     [:table.rems-table {:class (:id spec)}
      [:thead
       [table-header spec]]
      [:tbody {:key language} ; performance optimization: rebuild instead of update existing components
       (for [row rows]
         ^{:key (:key row)} [table-row row spec])]]]))
