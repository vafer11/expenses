(ns expenses.commons.subs
	(:require [re-frame.core :refer [reg-sub]]))

(reg-sub
	::path-value
	(fn [db [_ path]]
		(get-in db path)))