(ns book.forms.whole-form-logic
  (:require
    [com.fulcrologic.fulcro.dom :as dom]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [fulcro.server :refer [defquery-root]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [fulcro.ui.icons :as i]
    [fulcro.ui.forms :as f]
    [com.fulcrologic.fulcro.data-fetch :as df]
    [fulcro.ui.bootstrap3 :as b]))

;; SERVER
(def users #{"tony" "sam"})

(defquery-root :name-in-use
  (value [env {:keys [name]}]
    (if (contains? users name) :duplicate :ok)))

;; CLIENT
(defn field-with-label
  "A non-library helper function, written by you to help lay out your form."
  ([comp form name label] (field-with-label comp form name label nil))
  ([comp form name label validation-message]
   (dom/div :.form-group {:className (when (f/invalid? form name) "has-error")}
     (dom/label :.col-sm-2 {:htmlFor name} label)
     (dom/div :.col-sm-10 (f/form-field comp form name))
     (when validation-message
       (dom/div :.col-sm-offset-2.col-sm-10 {:className (str name)} validation-message)))))

(defmutation check-username-available
  "Sample mutation that simulates legal username check"
  [{:keys [form-id kind field]}]
  (action [{:keys [state] :as env}]
    (when (and (= kind :blur) (= :person/name field))
      (let [value (get-in @state (conj form-id field))]
        ; Set the UI to let them know we're checking...
        (swap! state assoc-in (conj form-id :ui/name-status) :checking)
        ; Send a query to the server to see if it is in use
        (df/load-action env :name-in-use nil {:target  (conj form-id :ui/name-status)
                                              :refresh [f/form-root-key]
                                              :marker  false
                                              :params  {:name value}}))))
  (remote [env] (df/remote-load env)))

(defsc Person [this {:keys [ui/name-status] :as props}]
  {:initial-state (fn [params] (f/build-form this (or params {})))
   :query         [f/form-root-key f/form-key :db/id :person/name :person/age :ui/name-status]
   :ident         [:person/id :db/id]
   :form-fields   [(f/id-field :db/id)
                   (f/on-form-change `check-username-available)
                   (f/text-input :person/name)
                   (f/integer-input :person/age)]}
  (dom/div :.form-horizontal
    (field-with-label this props :person/name "Username:"
      (case name-status
        :duplicate (b/alert {:kind :warning}
                     "That username is in use." (i/icon :error))
        :checking (b/alert {:color :neutral}
                    "Checking if that username is in use...")
        :ok (b/alert {:color :success} "OK" (i/icon :check))
        ""))
    (field-with-label this props :person/age "Age:")
    (dom/div :.button-group
      (dom/button :.btn.btn-default {:disabled (not (f/dirty? props)) :onClick #(f/commit-to-entity! this :remote true)}
        "Save!"))))

(def ui-person (comp/factory Person {:keyfn :db/id}))

(defsc Root [this {:keys [person]}]
  {:initial-state (fn [_] {:person (comp/get-initial-state Person {:db/id 1})})
   :query         [{:person (comp/get-query Person)}]}
  (dom/div
    (ui-person person)))
