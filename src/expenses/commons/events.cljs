(ns expenses.commons.events
	(:require [re-frame.core :refer [reg-event-db]]))

(reg-event-db
	::set-field-error
	(fn [db [_ path error]]
		(assoc-in db path error)))

(reg-event-db
	::set-input-value
	(fn [db [_ path value]]
		(assoc-in db path value)))

(reg-event-db
	::remove-field-value
	(fn [db [_ path value]]
		(assoc-in db path "")))

(reg-event-db
	::remove-field-error
	(fn [db [_ input-path id]]
		(let [path (drop-last input-path)]
			(update-in db path dissoc id))))

(reg-event-db
	::close-notification
	(fn [db _]
		(-> db
			(assoc-in [:form :notification :open] false)
			(assoc-in [:form :notification :msg] ""))))