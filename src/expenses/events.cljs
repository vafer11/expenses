(ns expenses.events
  (:require
    [expenses.utils :refer [get-date]]
    [re-frame.core :refer [reg-event-fx reg-event-db reg-fx reg-cofx inject-cofx]]))

(reg-fx
  ::set-hash-localStorage
  (fn [hash]
    (.setItem js/localStorage "hash" (name hash))))

(reg-cofx
  ::local-store
  (fn [cofx]
    (let [hash (keyword (str (js->clj (.getItem js/localStorage "hash")) "-panel"))
          uid (js->clj (.getItem js/localStorage "uid"))]
      (-> cofx
        (assoc :uid uid)
        (assoc :hash hash)))))

(reg-event-fx
  ::initialize-db
  [(inject-cofx ::local-store)]
  (fn [{:keys [db hash uid] :as cfx} _]
    {:db {:form {:expense-type {:values {:expense-type ""
                                         :recurrent? true}}
                 :add-expense {:values {:expense-type-code "default"
                                        :amount ""
                                        :note ""
                                        :date (get-date)}}
                 :expenses {:values {:filter "month"}}
                 :home {:values {:filter "month"}}
                 :signin {:values {:email ""
                                   :pw ""}}
                 :signup {:values {:name ""
                                   :last-name ""
                                   :email ""
                                   :pw ""
                                   :confirm-pw ""}}
                 ::reset-pw {:values {:email ""}}}
          :active-panel hash
          :current-user {:uid uid}}
     :set-user-observer {:on-user-event [:on-user-observer]
                         :on-nil-user-event [:dissoc-current-user]}}))

(reg-event-fx
  ::navigate
  (fn [_ [_ handler]]
    {:navigate handler
     ::set-hash-localStorage handler}))

(reg-event-db
  ::set-active-panel
  (fn [db [_ active-panel]]
    (assoc db :active-panel active-panel)))