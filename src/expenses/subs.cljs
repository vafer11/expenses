(ns expenses.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
  ::form-notification
  (fn [db _]
    (get-in db [:form :notification])))