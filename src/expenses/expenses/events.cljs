(ns expenses.expenses.events
	(:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx reg-cofx]]
						[expenses.firebase.database :as database]
						[expenses.expenses.views.validation :refer [add-expense-validation exp-type-validation]]))

(reg-fx
	:fb-db-add-expense-type
	(fn [{:keys [uid expense-type recurrent?]}]
		(database/push!
			[:users uid :expense-types]
			#js {:expense-type expense-type
					 :recurrent? recurrent?}
			[::add-expense-type-success])))

(reg-fx
	:fb-db-add-expense
	(fn [{:keys [uid expense-type-code amount note date]}]
		(database/push!
			[:users uid :expenses]
			#js {:expense-type-code expense-type-code
					 :amount amount
					 :note note
					 :date date}
			[::add-expense-success])))

(reg-fx
	:fb-db-delete-expense
	(fn [{:keys [key uid]}]
		(database/save! [:users uid :expenses (name key)] nil)))

(reg-event-fx
	::delete-expense
	(fn [{:keys [db]} [_ key]]
		(let [uid (get-in db [:current-user :uid])]
			{:fb-db-delete-expense {:key key
															:uid uid}})))

(reg-event-fx
	::add-expense-type
	(fn [{:keys [db]} _]
		(let [values (get-in db [:form :expense-type :values])
					form-errors (exp-type-validation values)]
			(if (empty? form-errors)
				(let [uid (get-in db [:current-user :uid])
							params (assoc values :uid uid)]
					{:fb-db-add-expense-type params})
				{:fx [[:dispatch [::show-exp-type-form-errors form-errors]]]}))))

(reg-event-fx
	::add-expense
	(fn [{:keys [db]} _]
		(let [values (get-in db [:form :add-expense :values])
					form-errors (add-expense-validation values)]
			(if (empty? form-errors)
				(let [uid (get-in db [:current-user :uid])]
					{:fb-db-add-expense (assoc values :uid uid)})
				{:fx [[:dispatch [::show-exp-form-errors form-errors]]]}))))

(reg-event-db
	::show-exp-type-form-errors
	(fn [db [_ errors]]
		(assoc-in db [:form :expense-type :errors] errors)))

(reg-event-db
	::show-exp-form-errors
	(fn [db [_ errors]]
		(assoc-in db [:form :add-expense :errors] errors)))

(reg-event-db
	::add-expense-type-success
	(fn [db _]
		(-> db
			(assoc-in [:form :expense-type :values :expense-type] "")
			(assoc-in [:form :notification]
				{:open true
				 :success true
				 :msg "Expense Type successfully added"}))))

(reg-event-db
	::add-expense-success
	(fn [db _]
		(-> db
			(assoc-in [:form :add-expense :values :expense-type-code] "default")
			(assoc-in [:form :add-expense :values :amount] "")
			(assoc-in [:form :add-expense :values :note] "")
			(assoc-in [:form :notification]
				{:open    true
				 :success true
				 :msg     "Expense successfully added"}))))
