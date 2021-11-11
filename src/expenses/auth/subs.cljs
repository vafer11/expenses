(ns expenses.auth.subs
	(:require [re-frame.core :refer [reg-sub]]))

(reg-sub
	::signup-errors
	(fn [db _]
		(get-in db [:form :signup :errors])))

(reg-sub
	::authenticated?
	(fn [db _]
		(get-in db [:current-user :uid])))
