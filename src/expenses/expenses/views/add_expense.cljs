(ns expenses.expenses.views.add-expense
	(:require [reagent.core :as r]
						[re-frame.core :refer [dispatch subscribe]]
						[expenses.utils :refer [format-expense-types]]
		        ; -- Expenses Event --
						[expenses.expenses.events :as events]
						[expenses.expenses.subs :as subs]

						[expenses.commons.components :refer [select text-field date-field button]]
						["@material-ui/core/Typography" :default Typography]
						["@material-ui/core/Container" :default Container]))

(defn add-expense []
	(let [path [:form :add-expense]]
		(fn []
			(let [expense-types @(subscribe [::subs/expense-types])]
				[:> Container {:component :main :maxWidth :xs :style {:margin-top 20}}
				 [:div {:class "add-expense-div-class"}
					[:> Typography {:component :h1 :variant :h5} "New expense"]
					[:form {:class "add-expense-form-class" :noValidate true}
					 [select path {:id :expense-type-code
												 :label "Expense"} (format-expense-types expense-types)]
					 [text-field path {:id :amount
														 :label "Amount"
														 :required true}]
					 [text-field path {:id :note
														 :name "note"
														 :label "Note"}]
					 [date-field path {:id :date
														 :label "Date"
														 :required true}]
					 [button "Add" {:className "add-expense-btn-class"
													:on-click #(dispatch [::events/add-expense])}]]]]))))
