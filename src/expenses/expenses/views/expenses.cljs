(ns expenses.expenses.views.expenses
	(:require [reagent.core :as r]
						[re-frame.core :refer [subscribe]]
						[expenses.expenses.subs :as subs]
						[expenses.utils :as utils]
						[expenses.commons.components :refer [item-dialog delete-item-dialog select list-comp
																								 expenses-sum-up-table]]
						["@material-ui/core/Typography" :default Typography]
						["@material-ui/core/Container" :default Container]))


(defn home []
	(let [last-six-month (utils/get-last-six-month)
				expenses (subscribe [::subs/formatted-expenses])]
		(fn []
			[:> Container {:component :main :maxWidth :xs :style {:margin-top 20}}
			 [:div {:class "expenses-div-class"}
				[:> Typography {:component :h1 :variant :h5} "Expenses sum up"]
				[select [:form :home] {:id :filter :label "Filter by month"} last-six-month]
				[expenses-sum-up-table (utils/reduce-expenses-sum-up
																 @expenses)]]])))

(defn expenses []
	(let [last-six-month (utils/get-last-six-month)
				state (r/atom {:item-data nil :delete-item-key nil})
				expenses (subscribe [::subs/formatted-expenses])]
		(fn []
			[:> Container {:component :main :maxWidth :xs :style {:margin-top 20}}
			 [item-dialog state]
			 [delete-item-dialog state]
			 [:div {:class "expenses-div-class"}
				[:> Typography {:component :h1 :variant :h5} "My Expenses"]
				[select [:form :expenses] {:id :filter :label "Filter by month"} last-six-month]
				[list-comp state expenses]]])))
