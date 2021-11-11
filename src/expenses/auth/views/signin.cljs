(ns expenses.auth.views.signin
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


(defn signin []
  (let [path [:form :signin]]
    (fn []
      [:> Container {:maxWidth :xs :style {:margin-top 20}}
       [:div {:class "login-div"}
        [:> Typography {:component :h1 :variant :h5} "Sign In"]
        [:form {:class "login-form"}
         [text-field path {:id :email
                           :label "Email Address"
                           :autoFocus true}]
         [text-field path {:id :pw
                           :type :password
                           :label "Password"}]
         [:> FormControlLabel {:control (r/as-element [:> Checkbox {:value "remember"
                                                                    :checked true
                                                                    :color :primary}])
                               :label "Remember me"}]

         [button "Sign In" {:class "signin-btn"
                            :on-click #(dispatch [::auth-events/signin])}]
         [:> Grid {:container true}
          [:> Grid {:item true :xs true}
           [:> Link {:variant "body2"
                     :on-click #(dispatch [::events/navigate :reset-pw])} "Forgot password?"]]
          [:> Grid {:item true :xs true}
           [:> Link {:variant "body2"
                     :on-click #(dispatch [::events/navigate :signup])} "Don't have an account? Sign Up"]]]]]])))