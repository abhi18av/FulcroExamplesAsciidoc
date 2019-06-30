(ns book.demos.server-return-values-manually-merging
  (:require
    [com.fulcrologic.fulcro.dom :as dom]
    [fulcro.server :as server]
    [com.fulcrologic.fulcro.mutations :as m]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; SERVER:
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(server/defmutation crank-it-up [{:keys [value]}]
  (action [env]
    {:new-volume (inc value)}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; CLIENT:
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti merge-return-value (fn [state sym return-value] sym))

; Do all of the work on the server.
(m/defmutation crank-it-up [params]
  (remote [env] true))

(defmethod merge-return-value `crank-it-up
  [state _ {:keys [new-volume]}]
  (assoc-in state [:child/by-id 0 :volume] new-volume))

(defsc Child [this {:keys [id volume]}]
  {:initial-state (fn [params] {:id 0 :volume 5})
   :query         [:id :volume]
   :ident         [:child/by-id :id]}
  (dom/div
    (dom/p "Current volume: " volume)
    (dom/button {:onClick #(comp/transact! this `[(crank-it-up ~{:value volume})])} "+")))

(def ui-child (comp/factory Child))

(defsc Root [this {:keys [child]}]
  {:initial-state (fn [params] {:child (comp/get-initial-state Child {})})
   :query         [{:child (comp/get-query Child)}]}
  (dom/div (ui-child child)))
