(ns expenses.nav.subs
	(:require [re-frame.core :refer [reg-sub]]))

(reg-sub
	::authenticated?
	(fn [db _]
		(get-in db [:current-user :uid])))
