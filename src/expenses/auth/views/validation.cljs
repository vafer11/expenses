(ns expenses.auth.views.validation
	(:require [cljs.spec.alpha :as s]
						[expenses.spec :as model]
						[expenses.commons.events :as component-events]
						[expenses.auth.subs :as subs]
						[re-frame.core :refer [dispatch subscribe]]))

(defmulti signup-custom-errors
	(fn [{:keys [in]} _]
		(let [key (first in)]
			(case key
				:last-name :name
				:confirm-pw :pw
				key))))

(defmethod signup-custom-errors :name [{:keys [val in via]} errors]
	(let [id (first in) error-type (last via)]
		(cond
			(= "" val) (assoc errors id "Can't be blank")
			(= :expenses.spec/at-least-two error-type) (assoc errors id "At least two characters")
			(= :expenses.spec/just-letters error-type) (assoc errors id "I must contains just letters"))))

(defmethod signup-custom-errors :email [{:keys [val in via]} errors]
	(let [id (first in) error-type (last via)]
		(cond
			(= "" val) (assoc errors id "Can't be blank")
			(= :expenses.spec/email error-type) (assoc errors id "Invalid email"))))

(defmethod signup-custom-errors :pw [{:keys [val in via]} errors]
	(let [id (first in) error-type (last via)]
		(cond
			(= "" val) (assoc errors id "Can't be blank")
			(= :expenses.spec/at-least-six error-type) (assoc errors id "At least six characters"))))


(defn ^:private validate-confirm-pw [{:keys [pw confirm-pw]} errors]
	(let [valid-pw (not (contains? errors :pw))
				valid-confirm-pw (not (contains? errors :confirm-pw))]
		(cond
			(or (not valid-pw) (not valid-confirm-pw)) errors
			(and valid-pw valid-confirm-pw (not= pw confirm-pw))
			(-> (assoc errors :pw "Password does not match")
					(assoc :confirm-pw "Password does not match"))
			(and valid-pw valid-confirm-pw (= pw confirm-pw))
			(-> (dissoc errors :pw)
				  (dissoc :confirm-pw)))))

(defn signup-validation [values]
	(->> values
		(s/explain-data ::model/signup-form)
		:cljs.spec.alpha/problems
		(reduce #(signup-custom-errors %2 %1) {})
		(validate-confirm-pw values)))

(defn signin-validation [values]
	(if-not (s/valid? ::model/signin-form values)
		{:email "Invalid Email or Password" :pw "Invalid Email or Password"}
		{}))