(ns expenses.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [expenses.events :as events]
   [expenses.routes :as routes]
   [expenses.views :as views]
   [expenses.firebase.init :refer [firebase-init]]))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (firebase-init)
  (routes/start!)
  (re-frame/dispatch-sync [::events/initialize-db])
  (mount-root))
