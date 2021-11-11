(ns expenses.views
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/core/CssBaseline" :default CssBaseline]
   ["@material-ui/core/styles" :refer [ThemeProvider]]
   [re-frame.core :refer [subscribe]]
   [expenses.events :as events]
   [expenses.subs :as subs]
   [expenses.auth.subs :as auth-subs]
   [expenses.auth.views.signin :refer [signin]]
   [expenses.auth.views.signup :refer [signup]]
   [expenses.auth.views.reset-pw :refer [reset-pw]]
   [expenses.nav.views.nav :refer [nav]]
   [expenses.theme :refer [expenses-theme]]
   [expenses.commons.components :refer [toast-notification]]
   [expenses.expenses.views.add-expense-type :refer [add-expense-type]]
   [expenses.expenses.views.add-expense :refer [add-expense]]
   [expenses.expenses.views.expenses :refer [expenses home]]))

(defmulti panels identity)

(defmethod panels :signin-panel [_ authenticated?]
 (if authenticated? [home] [signin]))

(defmethod panels :signup-panel [_ authenticated?]
 (if authenticated? [home] [signup]))

(defmethod panels :reset-pw-panel [_ _]
 [reset-pw])

(defmethod panels :expenses-panel [_ authenticated?]
 (if authenticated? [expenses] [signin]))

(defmethod panels :add-expense-type-panel [_ authenticated?]
 (if authenticated? [add-expense-type] [signin]))

(defmethod panels :add-expense-panel [_ authenticated?]
 (if authenticated? [add-expense] [signin]))

(defmethod panels :home-panel [_ authenticated?]
 (if authenticated? [home] [signin]))

(defmethod panels :default [] [:div "No panel found for this route."])

(defn main-panel []
  (let [active-panel @(subscribe [::subs/active-panel])
        notification @(subscribe [::subs/form-notification])
        authenticated? @(subscribe [::auth-subs/authenticated?])]
   [:> ThemeProvider {:theme (expenses-theme)}
    [:<>
     [:> CssBaseline]
     [nav]
     [toast-notification notification]
     (panels active-panel authenticated?)]]))
