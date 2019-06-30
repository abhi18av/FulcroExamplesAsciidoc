(ns book.merge-component
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.data-fetch :as df]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]))

(defsc Counter [this {:keys [counter/id counter/n] :as props} {:keys [onClick] :as computed}]
  {:query [:counter/id :counter/n]
   :ident [:counter/by-id :counter/id]}
  (dom/div :.counter
    (dom/span :.counter-label
      (str "Current count for counter " id ":  "))
    (dom/span :.counter-value n)
    (dom/button {:onClick #(onClick id)} "Increment")))

(def ui-counter (comp/factory Counter {:keyfn :counter/id}))

; the * suffix is just a notation to indicate an implementation of something..in this case the guts of a mutation
(defn increment-counter*
  "Increment a counter with ID counter-id in a Fulcro database."
  [database counter-id]
  (update-in database [:counter/by-id counter-id :counter/n] inc))

(defmutation increment-counter [{:keys [id] :as params}]
  ; The local thing to do
  (action [{:keys [state] :as env}]
    (swap! state increment-counter* id))
  ; The remote thing to do. True means "the same (abstract) thing". False (or omitting it) means "nothing"
  (remote [env] true))

(defsc CounterPanel [this {:keys [counters]}]
  {:initial-state (fn [params] {:counters []})
   :query         [{:counters (comp/get-query Counter)}]
   :ident         (fn [] [:panels/by-kw :counter])}
  (let [click-callback (fn [id] (comp/transact! this
                                  `[(increment-counter {:id ~id}) :counter/by-id]))]
    (dom/div
      ; embedded style: kind of silly in a real app, but doable
      (dom/style ".counter { width: 400px; padding-bottom: 20px; }
                  button { margin-left: 10px; }")
      ; computed lets us pass calculated data to our component's 3rd argument. It has to be
      ; combined into a single argument or the factory would not be React-compatible (not would it be able to handle
      ; children).
      (map #(ui-counter (comp/computed % {:onClick click-callback})) counters))))

(def ui-counter-panel (comp/factory CounterPanel))

(defonce timer-id (atom 0))
(declare sample-of-counter-app-with-merge-component-fulcro-app)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The code of interest...
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn add-counter
  "NOTE: A function callable from anywhere as long as you have a reconciler..."
  [app counter]
  (merge/merge-component! app Counter counter
    :append [:panels/by-kw :counter :counters]))

(defsc Root [this {:keys [panel]}]
  {:query         [{:panel (comp/get-query CounterPanel)}]
   :initial-state {:panel {}}}
  (dom/div {:style {:border "1px solid black"}}
    ; NOTE: A plain function...pretend this is happening outside of the UI...we're doing it here so we can embed it in the book...
    (dom/button {:onClick #(add-counter (comp/any->app this) {:counter/id 4 :counter/n 22})} "Simulate Data Import")
    (dom/hr)
    "Counters:"
    (ui-counter-panel panel)))
