(ns expenses.expenses.subs
	(:require [re-frame.core :refer [reg-sub]]
						[expenses.utils :as utils]))

(reg-sub
	::expense-types
	(fn [db _]
		(get-in db [:current-user :expense-types])))

(reg-sub
	::expenses
	(fn [db _]
		(get-in db [:current-user :expenses])))

(reg-sub
	::expense-type-filter
	(fn [db _]
		(let [current-page (:active-panel db)]
			(if (= current-page :home-panel)
				(get-in db [:form :home :values :filter])
				(get-in db [:form :expenses :values :filter])))))

(reg-sub
	::formatted-expenses
	:<- [::expense-types]
	:<- [::expenses]
	:<- [::expense-type-filter]
	(fn [[expense-types expenses filter-value] _]
		(let [reduce-function (fn [acc k {:keys [expense-type-code amount note date]}]
														(let [initial-date-filter (js/Date. filter-value)
																	end-date-filter (utils/add-months initial-date-filter 1)
																	expense-date (js/Date. (str date "T00:00:00"))
																	>=initial-date-filter (>= expense-date initial-date-filter)
																	<end-date-filter (< expense-date end-date-filter)]
															(if (and >=initial-date-filter <end-date-filter)
																(let [expense-name (get-in expense-types [(keyword expense-type-code) :expense-type])
																			expense {:key k
																							 :expense-type-code (keyword expense-type-code)
																							 :primary expense-name
																							 :secondary (str amount "$" " - " date)
																							 :expense-type expense-name
																							 :amount (js/parseInt amount 10)
																							 :note note
																							 :date date}]
																	(conj acc expense))
																acc)))]
			(reduce-kv reduce-function [] expenses))))