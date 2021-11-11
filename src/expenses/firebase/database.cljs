(ns expenses.firebase.database
	(:require [clojure.string :as str]
						[clojure.set :as set]
						[re-frame.core :refer [dispatch]]
						["firebase/app" :default firebase]))

(defn ^:private db-ref [path]
	(.. firebase (database) (ref (str/join "/" path))))

(defn save! [path value]
	(.set (db-ref path) value))

(defn push! [path value on-success-event]
	(.. (db-ref path) (push value)
		(then (fn [response]
						(dispatch (conj on-success-event))))))

(defn user-data-observer! [uid event]
	(.. (db-ref [:users uid])
		(on "value"
			(fn [snapshot]
				(let [user-data (-> (.val snapshot)
													(js->clj :keywordize-keys true)
													(set/rename-keys {(keyword ":expense-types") :expense-types
																						(keyword ":expenses") :expenses})
													(assoc :uid uid))]
					(dispatch (conj event user-data)))))))