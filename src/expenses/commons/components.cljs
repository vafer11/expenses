(ns expenses.commons.components
	(:require [reagent.core :as r]
						[reagent.impl.template :as rtpl]
						[cljs.spec.alpha :as s]
						[re-frame.core :refer [subscribe dispatch]]
						[expenses.spec :as model]
						[expenses.commons.events :as events]
						[expenses.expenses.events :as expense-events]
						[expenses.commons.subs :as subs]
						["@material-ui/core/TextField" :default TextField]
						["@material-ui/core/Button" :default Button]
						["@material-ui/core/FormControlLabel" :default FormControlLabel]
						["@material-ui/core/FormControl" :default FormControl]
						["@material-ui/core/InputLabel" :default InputLabel]
						["@material-ui/core/FormLabel" :default FormLabel]
						["@material-ui/core/RadioGroup" :default RadioGroup]
						["@material-ui/core/Radio" :default Radio]
						["@material-ui/core/Select" :default Select]
						["@material-ui/core/FormHelperText" :default FormHelperText]
						["@material-ui/core/MenuItem" :default MenuItem]
						["@material-ui/core/List" :default List]
						["@material-ui/core/ListItem" :default ListItem]
						["@material-ui/core/ListItemText" :default ListItemText]
						["@material-ui/core/ListItemSecondaryAction" :default ListItemSecondaryAction]
						["@material-ui/core/IconButton" :default IconButton]
						["@material-ui/icons/Delete" :default DeleteIcon]
						["@material-ui/core/Dialog" :default Dialog]
						["@material-ui/core/DialogTitle" :default DialogTitle]
						["@material-ui/core/DialogContent" :default DialogContent]
						["@material-ui/core/DialogContentText" :default DialogContentText]
						["@material-ui/core/DialogActions" :default DialogActions]

						["@material-ui/core/TableContainer" :default TableContainer]
						["@material-ui/core/Table" :default Table]
						["@material-ui/core/TableHead" :default TableHead]
						["@material-ui/core/TableBody" :default TableBody]
						["@material-ui/core/TableRow" :default TableRow]
						["@material-ui/core/TableCell" :default TableCell]
						["@material-ui/core/Paper" :default Paper]

						["@material-ui/core/Snackbar" :default Snackbar]
						["@material-ui/core/SnackbarContent" :default SnackbarContent]
						["@material-ui/icons/Close" :default CloseIcon]))

;; -------- this code was taken from ---------
;; https://github.com/reagent-project/reagent/blob/master/doc/examples/material-ui.md
(defn event-value
	[^js/Event e]
	(let [^js/HTMLInputElement el (.-target e)]
		(.-value el)))

(def ^:private input-component
	(r/reactify-component
		(fn [props]
			[:input (-> props
								(assoc :ref (:inputRef props))
								(dissoc :inputRef))])))
;;------------------------------------------------------

(defn get-spec-function [id]
	(case id
		:name ::model/name
		:last-name ::model/last-name
		:email ::model/email
		:pw ::model/pw
		:confirm-pw ::model/confirm-pw
		:expense-type ::model/expense-type
		:expense-type-code ::model/expense-type-code
		:amount ::model/amount
		:date ::model/date
		::model/no-validation))

(defn on-change-event [value-path error-path id e]
	(let [value (event-value e)]
		(dispatch [::events/set-input-value value-path value])
		(when (s/valid? (get-spec-function id) value)
			(if-not (nil? error-path)
				(dispatch [::events/remove-field-error error-path id])))))

(defn text-field [path {:keys [id] :as input-props} & children]
	(let [value-path (conj path :values id)
				error-path (conj path :errors id)
				value @(subscribe [::subs/path-value value-path])
				error @(subscribe [::subs/path-value error-path])
				default-props {:variant :outlined
											 :margin :normal
											 :fullWidth true
											 :value value
											 :error (not (nil? error))
											 :helperText error
											 :on-change (fn [e] (on-change-event value-path error-path id e))
											 :InputProps {:inputComponent input-component}}
				text-props (rtpl/convert-prop-value (merge default-props input-props))]
		(apply r/create-element TextField text-props (map r/as-element children))))

(defn date-field [path {:keys [id] :as props}]
	(let [value-path (conj path :values id)
				error-path (conj path :errors id)
				value @(subscribe [::subs/path-value value-path])
				error @(subscribe [::subs/path-value error-path])]
		[:> TextField (merge {:type :date
													:variant :outlined
													:margin :normal
													:value value
													:error (not (nil? error))
													:helperText error
													:on-change #(on-change-event value-path error-path id %)}
										props)]))

(defn button [text props]
	[:> Button (merge {:variant :contained
										 :color :primary
										 :fullWidth true} props) text])

(defn formControlLabel [{:keys [label value func]}]
	[:> FormControlLabel {:value value
												:control (r/as-element [:> Radio
																								{:color :primary
																								 :on-change func}])
												:label label
												:labelPlacement :start}])

(defn select [path {:keys [id label] :as props} items]
	(let [value-path (conj path :values id)
				error-path (conj path :errors id)
				value @(subscribe [::subs/path-value value-path])
				error @(subscribe [::subs/path-value error-path])]
		[:> FormControl {:variant :outlined
										 :margin :normal
										 :required true
										 :error (not (nil? error))}
		 [:> InputLabel {:id (str "select-" label)} label]
		 [:> Select (merge {:labelId (str "select-" label)
												:value value
												:on-change #(on-change-event value-path error-path id %)}
									props)
			(for [{:keys [value name]} items]
				^{:key value}
				[:> MenuItem {:value value} name])]
		 [:> FormHelperText error]]))

(defn radio [form-label default-value items]
	[:> FormControl {:component :fieldset}
	 [:> FormLabel {:component :legend} form-label]
	 [:> RadioGroup {:row true
									 :aria-label :position
									 :defaultValue default-value}
		(for [{:keys [value label custom-function]} items]
			^{:key value}
			[:> FormControlLabel {:value value
														:control (r/as-element [:> Radio
																										{:color :primary
																										 :on-change custom-function}])
														:label label
														:labelPlacement :start}])]])

(defn item-dialog [state]
	(let [{:keys [expense-type amount note date]} (:item-data @state)]
		[:> Dialog {:open (if (:item-data @state) true false)
							:on-close #(swap! state assoc :item-data nil)}
		 [:> DialogTitle "Expense details"
			[:> DialogContent
			 [:> DialogContentText (str "Expense Type: " expense-type)]
			 [:> DialogContentText (str "Amount: " amount "$")]
			 [:> DialogContentText (str "Note: " note)]
			 [:> DialogContentText (str "Date: " date)]]
			[:> DialogActions
			 [:> Button {:color "primary"
									 :on-click #(swap! state assoc :item-data nil)} "Back"]]]]))

(defn delete-item-dialog [state]
	[:> Dialog {:open (if (:delete-item-key @state) true false)
							:on-close #(swap! state assoc :delete-item-key nil)}
	 [:> DialogTitle "Delete expense"
		[:> DialogContent
		 [:> DialogContentText "Are you sure you want to delete this expense?"]]
		[:> DialogActions
		 [:> Button {:color "primary"
								 :on-click #(swap! state assoc :delete-item-key nil)} "Cancel"]
		 [:> Button {:color "primary"
								 :on-click #(let [key (:delete-item-key @state)]
															(dispatch [::expense-events/delete-expense key])
															(swap! state assoc :delete-item-key nil))} "Delete"]]]])

(defn list-comp [state items]
	[:> List
	 (for [{:keys [primary secondary key] :as item} @items]
		 ^{:key key}
		 [:> ListItem {:button true
									 :on-click #(swap! state assoc :item-data item)}
			[:> ListItemText {:primary primary
												:secondary secondary}]
			[:> ListItemSecondaryAction
			 [:> IconButton {:edge "end"
											 :on-click #(swap! state assoc :delete-item-key key)}
				[:> DeleteIcon]]]])])

(defn expenses-sum-up-table [expenses]
	[:> TableContainer {:component Paper}
	 [:> Table {:size "small"}
		[:> TableHead
		 [:> TableRow
			[:> TableCell "Expense"]
			[:> TableCell "Amount"]]]
		[:> TableBody
		 (for [[key values] expenses]
			 ^{:key key}
			 [:> TableRow
				[:> TableCell (:expense-type values)]
				[:> TableCell  (:amount values)]])]]])

(defn toast-notification [{:keys [open success msg]}]
	(let [success-style {:backgroundColor "#618833"}
				failure-style {:backgroundColor "#FF4848"}
				style (if success success-style failure-style)]
		[:> Snackbar {:open open
									:anchorOrigin {:vertical "bottom" :horizontal "right"}
									:autoHideDuration 4000
									:on-close #(dispatch [::events/close-notification])}
		 [:> SnackbarContent {:message msg
													:style style
													:role "alert"
													:action (r/as-element
																		[:> IconButton
																		 {:edge "end"
																			:size "small"
																			:aria-label "close"
																			:on-click #(dispatch [::events/close-notification])}
																		 [:> CloseIcon]])}]]))
