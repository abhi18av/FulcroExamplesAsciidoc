(ns book.queries.dynamic-queries
  (:require
    [com.fulcrologic.fulcro.dom :as dom]
    [goog.object]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m]))

(declare ui-leaf)

; This component allows you to toggle the query between [:x] and [:y]
(defsc Leaf [this {:keys [x y]}]
  {:initial-state (fn [params] {:x 1 :y 42})
   :query         (fn [] [:x])                              ; avoid error checking so we can destructure both :x and :y in props
   :ident         (fn [] [:LEAF :ID])}                      ; there is only one leaf in app state
  (dom/div
    (dom/button {:onClick (fn [] (comp/set-query! this ui-leaf {:query [:x]}))} "Set query to :x")
    (dom/button {:onClick (fn [] (comp/set-query! this ui-leaf {:query [:y]}))} "Set query to :y")
    ; If the query is [:x] then x will be defined, otherwise it will not.
    (dom/button {:onClick (fn [e] (if x
                                    (m/set-value! this :x (inc x))
                                    (m/set-value! this :y (inc y))))}
      (str "Count: " (or x y)))                             ; only one will be defined at a time
    " Leaf"))

(def ui-leaf (comp/factory Leaf {:qualifier :x}))

(defsc Root [this {:keys [root/leaf] :as props}]
  {:initial-state (fn [p] {:root/leaf (comp/get-initial-state Leaf {})})
   :query         (fn [] [{:root/leaf (comp/get-query ui-leaf)}])}
  (dom/div (ui-leaf leaf)))
