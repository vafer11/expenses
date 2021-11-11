(ns expenses.spec
	(:require [cljs.spec.alpha :as s]))

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(def date-regex #"^2\d{3}\-(0[1-9]|1[012])\-(0[1-9]|[12][0-9]|3[01])$")
(def letters-regex #"[a-zA-Z ]*")
(def numbers-regex #"[0-9]+")

(s/def ::just-letters #(re-matches letters-regex %))
(s/def ::just-numbers #(re-matches numbers-regex %))
(s/def ::not-default #(not= "default" %))
(s/def ::valid-date #(re-matches date-regex %))
(s/def ::at-least-two #(<= 2 (count %) 25))
(s/def ::at-least-six #(<= 6 (count %) 25))
(s/def ::no-validation string?)

(s/def ::email (s/and string? #(re-matches email-regex %)))
(s/def ::pw (s/and string? ::at-least-six))
(s/def ::confirm-pw (s/and string? ::at-least-six))
(s/def ::name (s/and string? ::just-letters ::at-least-two))
(s/def ::last-name (s/and string? ::just-letters ::at-least-two))

(s/def ::expense-type (s/and string? ::at-least-two))
(s/def ::recurrent? boolean?)

(s/def ::amount (s/and string? ::just-numbers))
(s/def ::expense-type-code (s/and string? ::not-default))
(s/def ::note ::no-validation)
(s/def ::date (s/and string? ::valid-date))

(s/def ::signup-form (s/keys :req-un [::last-name ::name ::email ::pw ::confirm-pw]))
(s/def ::signin-form (s/keys :req-un [::email ::pw]))
(s/def ::expense-type-form (s/keys :req-un [::expense-type ::recurrent?]))
(s/def ::add-expense-form (s/keys :req-un [::expense-type-code ::amount ::date] :opt-un [::note]))