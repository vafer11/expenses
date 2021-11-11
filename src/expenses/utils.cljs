(ns expenses.utils
	(:require [clojure.string :refer [capitalize]]))

(defn get-date []
	(.. (js/Date.) (toISOString) (slice 0 10)))

(defn add-months [date-param months-param]
	(let [date (js/Date. (.toString date-param))
				month (.getMonth date)
				new-date (js/Date. (.setMonth date (+ month months-param)))]
		new-date))

(defn get-last-six-month []
	(let [val [{:value "month" :name "Select your month"}]
				reduce-fun (fn [acc m]
										 (let [today (js/Date.)
													 current-year (.. today (getFullYear))
													 current-month (.. today (getMonth))
													 calculated-date (js/Date. current-year (- current-month m) 1)
													 formatted-date (.. calculated-date (toString))
													 calculated-month (.. calculated-date (toLocaleString "default" #js {:month "long"}))
													 calculated-year (.. calculated-date (getFullYear))
													 month-year (capitalize (str calculated-month " " calculated-year))]
											 (conj acc {:value formatted-date
																	:name month-year})))]
		(reduce reduce-fun val [6 5 4 3 2 1 0])))

(defn format-expense-types [expense-types]
	(let [default [{:value "default" :name "Select Expense"}]
				reduce-function (fn [acc k {:keys [expense-type]}]
													(conj acc {:value (name k)
																		 :name expense-type}))]
		(reduce-kv reduce-function default expense-types)))

(defn reduce-expenses-sum-up [expenses]
	(let [result (reduce
								 (fn [acc {:keys [expense-type-code expense-type amount]}]
									 (if (contains? acc expense-type-code)
										 (update-in acc [expense-type-code :amount] + amount)
										 (assoc acc expense-type-code {:expense-type expense-type
																								 :amount amount})))
								 {}
								 expenses)]
		result))