(ns rems.collapsible
  (:require [rems.guide :refer :all]))

(defn- header
  [id expanded title]
  [:h3.card-header
   [:a.card-title (merge {:data-toggle "collapse" :data-parent "#accordion" :href (str "#" id) :aria-expanded expanded :aria-controls id}
                         (when-not expanded {:class "collapsed"}))
    title]])

(defn- block [id expanded content]
  (let [classes (str "collapse" (when expanded " show"))]
    [:div {:id id :class classes}
     content]))

(defn component [id expanded title content]
  (list
    (header id expanded title)
    (block id expanded content)))

(defn guide
  []
  (list (example "Collapsible component expanded by default"
           (list
             [:div#accordion
              (component "hello" true "Collapse expanded" [:p "I am content"])]))
        (example "Collapsible component closed by default"
           (list
             [:div#accordion
              (component "hello2" false "Collapse minimized" [:p "I am content"])]))))