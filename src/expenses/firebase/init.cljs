(ns expenses.firebase.init
	(:require ["firebase/app" :as fb]
						["firebase/database"]
						["firebase/auth"]))

(def firebase (.-default fb))

(defn firebase-init
	[]
	(if (zero? (alength (.-apps firebase)))
		(firebase.initializeApp
			#js {:apiKey "..."
					 :authDomain "..."
					 :databaseURL "..."
					 :projectId "..."
					 :storageBucket "..."
					 :messagingSenderId "..."
					 :appId "..."})
		(firebase.app)))