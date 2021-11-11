(ns expenses.auth.events
	(:require [expenses.auth.views.validation :refer [signup-validation signin-validation]]
						[expenses.firebase.auth :as authentication]
						[expenses.firebase.database :as database]
						[re-frame.core :refer [dispatch reg-fx reg-event-fx reg-event-db reg-cofx inject-cofx]]))

;----------------- Effect Handlers -----------------;
(reg-fx
	:firebase-auth-fx
	(fn [{:keys [method params] :as request}]
		(case method
			:signout (authentication/signout request)
			:signin (authentication/signin! params request)
			:signup (authentication/signup! params request))))

(reg-fx
	:insert-user-into-database
	(fn [{:keys [form response]}]
		(let [^firebase::auth::User user (.-user response)
					uid (.-uid user)
					{name :name last-name :last-name email :email} form]
			(database/save! [:users uid] #js {:displayName (str name " " last-name)
																				:email email}))))
(reg-fx
	:set-user-observer
	(fn [events]
		(authentication/user-observer! events)))

(reg-fx
	:set-user-data-observer
	(fn [uid]
		(database/user-data-observer! uid [::assoc-current-user])))

(reg-fx
	:set-uid-localStorage
	(fn [uid]
		(.setItem js/localStorage "uid" uid)))

(reg-fx
	:remove-uid-localStorage
	(fn []
		(.removeItem js/localStorage "uid")))
;----------------- Event Handlers -----------------;

(reg-event-fx
	:on-user-observer
	(fn [cfx [_ uid]]
		{:set-uid-localStorage uid
		 :set-user-data-observer uid}))

(reg-event-fx
	::signup
	(fn [{:keys [db] :as cfx} _]
		(let [values (get-in db [:form :signup :values])
					form-errors (signup-validation values)]
			(if (empty? form-errors)
				{:firebase-auth-fx {:method :signup
														:params values
														:on-success [::signup-success]
														:on-failure [::signup-failure]}}
				{:fx [[:dispatch [::show-signup-form-errors form-errors]]]}))))

(reg-event-fx
	::signout
	(fn [cfx _]
		{:firebase-auth-fx {:method :signout
												:on-success [::signout-success]}}))

(reg-event-fx
	::signout-success
	(fn [cfx _]
		{:remove-uid-localStorage {}
		 :fx [[:dispatch [:expenses.events/navigate :signin]]]}))

(reg-event-fx
	::signin
	(fn [{:keys [db] :as cfx} _]
		(let [values (get-in db [:form :signin :values])
					form-errors (signin-validation values)]
			(if (empty? form-errors)
				{:firebase-auth-fx {:method :signin
														:params values
														:on-success [::signin-success]
														:on-failure [::signin-failure]}}
				{:fx [[:dispatch [::show-signin-form-errors form-errors]]]}))))

(reg-event-fx
	::signup-success
	(fn [{:keys [db] :as cfx} [_ form response]]
		{:insert-user-into-database {:form form :response response}
		 :fx [[:dispatch [:expenses.events/navigate :home]]]
		 :db (update-in db [:form] dissoc :signup)}))

(reg-event-fx
	::signup-failure
	(fn [cfx [_ {:keys [code message]}]]
		(when (= code "auth/email-already-in-use")
			{:fx [[:dispatch
						 [:expenses.commons.events/set-field-error
							[:form :signup :errors :email]
							message]]]})))

(reg-event-fx
	::signin-success
	(fn [cfx _]
		{:set-user-observer {:on-user-event [:on-user-observer]
												 :on-nil-user-event [:dissoc-current-user]}
		 :fx [[:dispatch [:expenses.events/navigate :home]]]}))

(reg-event-fx
	::signin-failure
	(fn [cfx [_ _]]
		{:fx [[:dispatch
					 [:expenses.commons.events/set-field-error [:form :signin :errors :email]
						"Invalid Email or Password"]]]}))
(reg-event-db
	::show-signup-form-errors
	(fn [db [_ errors]]
		(assoc-in db [:form :signup :errors] errors)))

(reg-event-db
	::show-signin-form-errors
	(fn [db [_ errors]]
		(assoc-in db [:form :signin :errors] errors)))

(reg-event-db
	::assoc-current-user
	(fn [db [_ user-data]]
		(assoc db :current-user user-data)))

(reg-event-db
	:dissoc-current-user
	(fn [db _]
		(dissoc db :current-user)))
