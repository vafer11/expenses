(ns expenses.nav.views.nav
	(:require [re-frame.core :refer [subscribe]]
						[expenses.nav.subs :as subs]
						["@material-ui/core/AppBar" :default AppBar]
						["@material-ui/core/Toolbar" :default Toolbar]
						["@material-ui/core/Typography" :default Typography]
						[expenses.nav.views.authenticated :refer [authenticated]]))
(defn public []
	[:> AppBar {:position :static}
	 [:> Toolbar
	 [:> Typography {:variant :h6 :className "nav-title-class"} "MyExps"]]])

(defn nav []
	(let [authenticated? @(subscribe [::subs/authenticated?])]
		(if authenticated?
			[authenticated]
			[public])))