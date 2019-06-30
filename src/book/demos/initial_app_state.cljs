(ns book.demos.initial-app-state
  (:require
    [com.fulcrologic.fulcro.dom :as dom]
    [com.fulcrologic.fulcro.mutations :as m]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]))

(defmethod m/mutate 'nav/settings [{:keys [state]} sym params]
  {:action (fn [] (swap! state assoc :panes [:settings :singleton]))})

(defmethod m/mutate 'nav/main [{:keys [state]} sym params]
  {:action (fn [] (swap! state assoc :panes [:main :singleton]))})

(defsc ItemLabel [this {:keys [value]}]
  {:initial-state (fn [{:keys [value]}] {:value value})
   :query         [:value]
   :ident         (fn [] [:labels/by-value value])}
  (dom/p value))

(def ui-label (comp/factory ItemLabel {:keyfn :value}))

;; Foo and Bar are elements of a mutli-type to-many union relation (each leaf can be a Foo or a Bar). We use params to
;; allow initial state to put more than one in place and have them be unique.
(defsc Foo [this {:keys [label]}]
  {:query         [:type :id {:label (comp/get-query ItemLabel)}]
   :initial-state (fn [{:keys [id label]}] {:id id :type :foo :label (comp/get-initial-state ItemLabel {:value label})})}
  (dom/div
    (dom/h2 "Foo")
    (ui-label label)))

(def ui-foo (comp/factory Foo {:keyfn :id}))

(defsc Bar [this {:keys [label]}]
  {:query         [:type :id {:label (comp/get-query ItemLabel)}]
   :initial-state (fn [{:keys [id label]}] {:id id :type :bar :label (comp/get-initial-state ItemLabel {:value label})})}
  (dom/div
    (dom/h2 "Bar")
    (ui-label label)))

(def ui-bar (comp/factory Bar {:keyfn :id}))

;; This is the to-many union component. It is the decision maker (it has no state or rendering of it's own)
;; The initial state of this component is the to-many (vector) value of various children
;; The render just determines which thing it is, and passes on the that renderer
(defsc ListItem [this {:keys [id type] :as props}]
  {:initial-state (fn [params] [(comp/get-initial-state Bar {:id 1 :label "A"}) (comp/get-initial-state Foo {:id 2 :label "B"}) (comp/get-initial-state Bar {:id 3 :label "C"})])
   :query         (fn [] {:foo (comp/get-query Foo) :bar (comp/get-query Bar)}) ; use lambda for unions
   :ident         (fn [] [type id])}                        ; lambda for unions
  (case type
    :foo (ui-foo props)
    :bar (ui-bar props)
    (dom/p "No Item renderer!")))

(def ui-list-item (comp/factory ListItem {:keyfn :id}))

;; Settings and Main are the target "Panes" of a to-one union (e.g. imagine tabs...we use buttons as the tab switching in
;; this example). The initial state looks very much like any other component, as does the rendering.
(defsc Settings [this {:keys [label]}]
  {:initial-state (fn [params] {:type :settings :id :singleton :label (comp/get-initial-state ItemLabel {:value "Settings"})})
   :query         [:type :id {:label (comp/get-query ItemLabel)}]}
  (ui-label label))

(def ui-settings (comp/factory Settings {:keyfn :type}))

(defsc Main [this {:keys [label]}]
  {:initial-state (fn [params] {:type :main :id :singleton :label (comp/get-initial-state ItemLabel {:value "Main"})})
   :query         [:type :id {:label (comp/get-query ItemLabel)}]}
  (ui-label label))

(def ui-main (comp/factory Main {:keyfn :type}))

;; This is a to-one union component. Again, it has no state of its own or rendering. The initial state is the single
;; child that should appear. Fulcro (during startup) will detect this component, and then use the query to figure out
;; what other children (the ones that have initial-state defined) should be placed into app state.
(defsc PaneSwitcher [this {:keys [id type] :as props}]
  {:initial-state (fn [params] (comp/get-initial-state Main nil))
   :query         (fn [] {:settings (comp/get-query Settings) :main (comp/get-query Main)})
   :ident         (fn [] [type id])}
  (case type
    :settings (ui-settings props)
    :main (ui-main props)
    (dom/p "NO PANE!")))

(def ui-panes (comp/factory PaneSwitcher {:keyfn :type}))

;; The root. Everything just composes to here (state and query)
;; Note, in core (where we create the app) there is no need to say anything about initial state!
(defsc Root [this {:keys [panes items]}]
  {:initial-state (fn [params] {:panes (comp/get-initial-state PaneSwitcher nil)
                                :items (comp/get-initial-state ListItem nil)})
   :query         [{:items (comp/get-query ListItem)}
                   {:panes (comp/get-query PaneSwitcher)}]}
  (dom/div
    (dom/button {:onClick (fn [evt] (comp/transact! this '[(nav/settings)]))} "Go to settings")
    (dom/button {:onClick (fn [evt] (comp/transact! this '[(nav/main)]))} "Go to main")

    (ui-panes panes)

    (dom/h1 "Heterogenous list:")

    (dom/ul
      (mapv ui-list-item items))))
