(ns expenses.nav.views.authenticated
	(:require [reagent.core :as r]
						[re-frame.core :refer [dispatch]]
						[expenses.events :as events]
						[expenses.auth.events :as auth]
						["@material-ui/core/AppBar" :default AppBar]
						["@material-ui/core/IconButton" :default IconButton]
						["@material-ui/core/Toolbar" :default Toolbar]
						["@material-ui/core/Typography" :default Typography]
						["@material-ui/core/Menu" :default Menu]
						["@material-ui/core/MenuItem" :default MenuItem]
						["@material-ui/icons/Menu" :default MenuIcon]))

(defn menu-item-click [state page]
	(swap! state assoc :menu-bar nil)
	(dispatch [::events/navigate page]))

(defn authenticated []
	(let [state (r/atom {})]
		(fn []
			[:> AppBar {:position :static}
			 [:> Toolbar
				[:> IconButton {:edge :start
												:className "nav-class"
												:color :inherit
												:aria-label :menu
												:on-click #(swap! state assoc :menu-bar (.-target %))}
				 [:> MenuIcon]]
				[:> Typography {:variant :h6
												:className "nav-title-class"} "MyExps"]
				[:> Menu {:id "menu-appbar"
									:anchorEl (:menu-bar @state)
									:keepMounted true
									:open (if (:menu-bar @state) true false)
									:onClose #(swap! state assoc :menu-bar false)}
				 [:> MenuItem {:onClick #(menu-item-click state :home)} "Home"]
				 [:> MenuItem {:onClick #(menu-item-click state :expenses)} "My Expenses"]
				 [:> MenuItem {:onClick #(menu-item-click state :add-expense)} "Add Expense"]
				 [:> MenuItem {:onClick #(menu-item-click state :add-expense-type)} "Add Expense Type"]
				 [:> MenuItem {:onClick #(dispatch [::auth/signout])} "Sign Out"]]]])))