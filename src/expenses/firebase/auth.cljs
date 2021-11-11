(ns expenses.firebase.auth
	(:require [expenses.events :as events]
						[re-frame.core :refer [dispatch]]
						["firebase/app" :default firebase]))

(defn signout [{:keys [on-success]}]
	(.. firebase auth (signOut)
		(then (fn [response]
						(dispatch on-success)))
		(catch (fn [error]
						 (.log js/console (str "Error when logging user out : " error))))))

(defn signin! [{:keys [email pw]} {:keys [on-success on-failure]}]
	(.. firebase auth (signInWithEmailAndPassword email pw)
		(then (fn [response]
						(dispatch (conj on-success response))))
		(catch (fn [error]
						 (.log js/console (str "Error when logging user : " error))
						 (dispatch (conj on-failure error))))))

(defn signup! [{:keys [email pw] :as form} {:keys [on-success on-failure]}]
	(.. firebase auth (createUserWithEmailAndPassword email pw)
		(then (fn [response]
						(dispatch (conj on-success form response))))
		(catch (fn [errors]
						 (dispatch (conj on-failure (-> errors
																					(.toJSON)
																					(js->clj :keywordize-keys true))))))))

(defn ^:private on-auth-state-changed [func]
	(.. firebase auth (onAuthStateChanged func)))

(defn user-observer! [{:keys [on-user-event on-nil-user-event]}]
	(on-auth-state-changed
		(fn [user]
			(if user
				(dispatch (conj on-user-event (.-uid user)))
				(dispatch on-nil-user-event)))))

(defn get-current-user []
	(.-currentUser (.auth firebase)))
