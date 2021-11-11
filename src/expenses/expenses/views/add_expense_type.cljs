(ns expenses.expenses.views.add-expense-type
	(:require [reagent.core :as r]
						[re-frame.core :refer [dispatch]]
						[expenses.expenses.events :as events]
						[expenses.commons.components :refer [text-field button radio]]
						["@material-ui/core/Typography" :default Typography]
						["@material-ui/core/Container" :default Container]))

(defn add-expense-type []
	(let [path [:form :expense-type]
				radio-path (conj path :values :recurrent?)]
		(fn []
			[:> Container {:component :main :maxWidth :xs :style {:margin-top 20}}
			 [:div {:class "add-expense-div-class"}
				[:> Typography {:component :h1 :variant :h5} "New expense type"]
				[:form {:class "add-expense-form-class" :noValidate true}
				 [text-field path {:id :expense-type :label "Expense" :required true :autoFocus true}]
				 [radio "Recurrent" "yes" [{:value "yes"
																		:label "Yes"
																		:custom-function #(dispatch [:expenses.commons.events/set-input-value radio-path true])}
																	 {:value "no"
																		:label "No"
																		:custom-function #(dispatch [:expenses.commons.events/set-input-value radio-path false])}]]

				 [button "Add" {:className "add-expense-type-btn-class"
												:on-click (fn [] (dispatch [::events/add-expense-type]))}]]]])))