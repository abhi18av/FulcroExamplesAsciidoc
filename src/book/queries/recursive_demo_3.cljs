(ns book.queries.recursive-demo-3
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.dom :as dom]))

(declare ui-person)

(defmutation make-older [{:keys [id]}]
  (action [{:keys [state]}]
    (swap! state update-in [:person/id id :person/age] inc)))

; We use computed to track the depth. Targeted refreshes will retain the computed they got on
; the most recent render. This allows us to detect how deep we are.
(defsc Person [this
               {:keys [db/id person/name person/spouse person/age]} ; props
               {:keys [render-depth] :or {render-depth 0}}] ; computed
  {:query         (fn [] [:db/id :person/name :person/age {:person/spouse 1}]) ; force limit the depth
   :initial-state (fn [p]
                    {:db/id         1 :person/name "Joe" :person/age 20
                     :person/spouse {:db/id         2 :person/name "Sally"
                                     :person/age    22
                                     :person/spouse {:db/id 1 :person/name "Joe"}}})
   :ident         [:person/id :db/id]}
  (dom/div
    (dom/div "Name:" name)
    (dom/div "Age:" age
      (dom/button {:onClick
                   #(comp/transact! this `[(make-older {:id ~id})])} "Make Older"))
    (when (and (= 0 render-depth) spouse)
      (dom/ul
        (dom/div "Spouse:"
          ; recursively render, but increase the render depth so we can know when a
          ; targeted UI refresh would accidentally push the UI deeper.
          (ui-person (comp/computed spouse {:render-depth (inc render-depth)})))))))

(def ui-person (comp/factory Person {:keyfn :db/id}))

(defsc Root [this {:keys [person-of-interest]}]
  {:initial-state {:person-of-interest {}}
   :query         [{:person-of-interest (comp/get-query Person)}]}
  (dom/div
    (ui-person person-of-interest)))
