(ns expenses.expenses.views.validation
	(:require [cljs.spec.alpha :as s]
						[expenses.spec :as model]))

(defn exp-type-validation [{:keys [expense-type]}]
	(when (= "" expense-type)
		{:expense-type "Can't be blank"}))

(defn ^:private exp-form-custom-errors [acc {:keys [val via] :as problem}]
	(let [error-type (last via)
				empty-val? (or (empty? val) (= val "default"))
				expense-type-code-error? (= error-type ::model/not-default)
				amount-error? (= error-type ::model/just-numbers)
				date-error? (= error-type ::model/valid-date)]
		(cond
			(and empty-val? expense-type-code-error?) (assoc acc :expense-type-code "Must select an expense")
			(and empty-val? amount-error?) (assoc acc :amount "Can't be black")
			(and empty-val? date-error?) (assoc acc :date "Can't be blank")
			(and (not empty-val?) amount-error?) (assoc acc :amount "Must be just numbers")
			(and (not empty-val?) date-error?) (assoc acc :date "Invalid date"))))

(defn add-expense-validation [{:keys [amount date] :as values}]
	(->> values
		(s/explain-data ::model/add-expense-form)
		:cljs.spec.alpha/problems
		(reduce #(exp-form-custom-errors %1 %2) {})))