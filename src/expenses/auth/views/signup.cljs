(ns expenses.auth.views.signup
	(:require [reagent.core :as r]
						[re-frame.core :refer [dispatch]]
						[expenses.events :as events]
						[expenses.auth.events :as auth-events]
						[expenses.commons.components :refer [text-field button]]
						["@material-ui/core/Typography" :default Typography]
						["@material-ui/core/Container" :default Container]
						["@material-ui/core/FormControlLabel" :default FormControlLabel]
						["@material-ui/core/Checkbox" :default Checkbox]
						["@material-ui/core/Grid" :default Grid]
						["@material-ui/core/Link" :default Link]))

(defn signup []
	(let [path [:form :signup]]
		(fn []
			[:> Container {:component :main :maxWidth :xs :style {:margin-top 20}}
			 [:div {:class "signup-div"}
				[:> Typography {:component :h1 :variant :h5} "Sign Up"]
				[:form {:class "login-form" :noValidate true}
				 [:> Grid {:container true :spacing 2}
					[:> Grid {:item true :xs 12 :sm 6}
					 [text-field path {:id :name
														 :label "Name"
														 :required true
														 :autoFocus true}]]
					[:> Grid {:item true :xs 12 :sm 6}
					 [text-field path {:id :last-name
														 :label "Last name"
														 :required true}]]
					[:> Grid {:item true :xs 12}
					 [text-field path {:id :email
														 :label "Email"
														 :required true}]]
					[:> Grid {:item true :xs 12}
					 [text-field path {:id :pw
														 :type :password
														 :label "Password"
														 :required true}]]
					[:> Grid {:item true :xs 12}
					 [text-field path {:id :confirm-pw
														 :type :password
														 :label "Confirm password"
														 :required true}]]
					[:> Grid {:item true :xs 12}
					 [:> FormControlLabel
						{:control (r/as-element [:> Checkbox {:value "Accept"
																									:checked true
																									:color :primary}])
						 :label "I accept terms and conditions"}]]]
				 [button "Sign up" {:className "signup-btn"
														:on-click #(dispatch [::auth-events/signup])}]
				 [:> Grid {:container true :justifyContent "flex-end"}
					[:> Grid {:item true}
					 [:> Link {:variant "body2"
										 :on-click #(dispatch [::events/navigate :signin])}
						"Already have an account? Sign in"]]]]]])))