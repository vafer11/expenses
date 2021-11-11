(ns expenses.auth.views.reset-pw
	(:require [reagent.core :as r]
						["@material-ui/core/Typography" :default Typography]
						["@material-ui/core/Container" :default Container]
						["@material-ui/core/Grid" :default Grid]
						[expenses.commons.components :refer [text-field button]]))

(defn reset-pw []
	(let [path [:form :reset-pw]]
		[:> Container {:component :main :maxWidth :xs :style {:margin-top 20}}
		 [:div {:class "reset-pw-div-class"}
			[:> Typography {:component :h1 :variant :h5} "Reset your password"]
			[:form {:class "reset-pw-form-class" :noValidate true}
			 [:> Grid {:container true :spacing 2}
				[:> Grid {:item true :xs 12}
				 [text-field path {:id :email :label "Email" :name "email" :required true :autoFocus true}]]]
			 [button "Reset password" {:class "reset-pw-btn-class"
																 :on-click #()}]]]]))
