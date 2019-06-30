(ns book.queries.recursive-demo-1
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]))

(defn make-person
  "Make a person data map with optional children."
  [id name children]
  (cond-> {:db/id id :person/name name}
    children (assoc :person/children children)))

(declare ui-person)

; The ... in the query means there will be children of the same type, of arbitrary depth
; it is equivalent to (comp/get-query Person), but calling get query on yourself would
; lead to infinite compiler recursion.
(defsc Person [this {:keys [:person/name :person/children]}]
  {:query         (fn [] [:db/id :person/name {:person/children '...}])
   :initial-state (fn [p]
                    (make-person 1 "Joe"
                      [(make-person 2 "Suzy" [])
                       (make-person 3 "Billy" [])
                       (make-person 4 "Rae"
                         [(make-person 5 "Ian"
                            [(make-person 6 "Zoe" [])])])]))
   :ident         [:person/id :db/id]}
  (dom/div
    (dom/h4 name)
    (when (seq children)
      (dom/div
        (dom/ul
          (map (fn [p]
                 (ui-person p))
            children))))))

(def ui-person (comp/factory Person {:keyfn :db/id}))

(defsc Root [this {:keys [person-of-interest]}]
  {:initial-state {:person-of-interest {}}
   :query         [{:person-of-interest (comp/get-query Person)}]}
  (dom/div
    (ui-person person-of-interest)))
