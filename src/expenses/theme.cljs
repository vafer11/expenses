(ns expenses.theme
	(:require ["@material-ui/core/styles" :refer [createTheme]]))

(defn expenses-theme []
	(createTheme
		(clj->js
			{:palette
			 {:primary  {:main "#618833" }
				:secondary {:main "#b28900"} }
			 :typography
			 {:useNextVariants true}})))
