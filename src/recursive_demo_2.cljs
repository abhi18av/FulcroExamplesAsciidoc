(ns book.queries.recursive-demo-2
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.dom :as dom]))

(declare ui-person)

(defmutation make-older [{:keys [id]}]
  (action [{:keys [state]}]
    (swap! state update-in [:person/id id :person/age] inc)))

(defsc Person [this {:keys [db/id person/name person/spouse person/age]}]
  {:query         (fn [] [:db/id :person/name :person/age {:person/spouse 1}]) ; force limit the depth
   :initial-state (fn [p]
                    ; this does look screwy...you can nest the same map in the recursive position,
                    ; and it'll just merge into the one that was previously normalized during normalization.
                    ; You need to do this or you won't get the loop in the database.
                    {:db/id         1
                     :person/name   "Joe"
                     :person/age    20
                     :person/spouse {:db/id         2
                                     :person/name   "Sally"
                                     :person/age    22
                                     :person/spouse {:db/id 1 :person/name "Joe"}}})
   :ident         [:person/id :db/id]}
  (dom/div
    (dom/div "Name:" name)
    (dom/div "Age:" age
      (dom/button {:onClick
                   #(comp/transact! this `[(make-older {:id ~id})])} "Make Older"))
    (when spouse
      (dom/ul
        (dom/div "Spouse:" (ui-person spouse))))))

(def ui-person (comp/factory Person {:keyfn :db/id}))

(defsc Root [this {:keys [person-of-interest]}]
  {:initial-state {:person-of-interest {}}
   :query         [{:person-of-interest (comp/get-query Person)}]}
  (dom/div
    (ui-person person-of-interest)))
