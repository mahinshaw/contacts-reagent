(ns contacts.core
  (:require
   [clojure.string :as str]
   [reagent.core :as r]))

(enable-console-print!)

(defn escape-html
  "Change special characters into HTML character entities."
  [text]
  (when text
    (str/escape text
                {\& "&amp;"
                 \< "&lt;"
                 \> "&gt;"
                 \" "&quot;"
                 \' "&#39;"})))

(def contact-id (atom 0))

(defn get-new-id []
  (swap! contact-id inc))

(def contacts (r/atom [{:id (get-new-id) :name "bob" :phone "336" :email "bob@email.com" :edit false}
                       {:id (get-new-id) :name "tim" :phone "324" :email "tim@email.com" :edit false}]))

(defn update-in-contact [data id key val]
  (mapv
   (fn [m] (if (= (:id m) id)
             (assoc m key val)
             m))
   data))

(defn toggle-edit [contact]
  (swap! contacts update-in-contact (:id contact) :edit (not (:edit contact))))

(defn add-contact
  "Add a contact, if all three values are present (name, phone, email)."
  [contact]
  (when-let [{:keys [name phone email] :as contact} contact]
   (swap! contacts conj contact)))

(defn update-contact [data contact]
  (mapv
   (fn [m] (if (= (:id m) (:id contact))
             contact
             m))
   data))

(defn text-input
  ([state key]
   [text-input state key "text"])
  ([state key type-str]
   (let [label (name key)
         placeholder (str/capitalize label)]
     [:input {:type type-str
              :name label
              :value (get @state key "")
              :placeholder placeholder
              :on-change #(swap! state assoc key (-> % .-target .-value))}])))

(defn add-contact-form []
  (let [id (get-new-id)
        contact (r/atom {:id id})]
    (fn []
      [:div.contact
       [:div.column-1
        [text-input contact :name]]
       [:div.column-2
        [text-input contact :phone]]
       [:div.column-3
        [text-input contact :email "email"]]
       [:button.button.add {:type "submit"
                            :on-click #(do (add-contact @contact)
                                           (reset! contact {:id (get-new-id)}))}
        "Add "]
       [:div.clear-row]])))

(defn read-contact [contact]
  [:div.contact
   [:div.contact-text
    [:div.column-1 (escape-html (:name contact))]
    [:div.column-2 (escape-html (:phone contact))]
    [:div.column-3 (escape-html (:email contact))]]
   [:div.button-group
    [:div
     [:button.button.edit {:type "submit"
                           :on-click #(toggle-edit contact)}
      [:i.fa.fa-pencil]]]
    [:div
     [:button.button.remove {:type "submit"}
      [:i.fa.fa-remove]]]]
   [:div.clear-row]])

;; TODO: Handle Edits.
(defn edit-contact [contact]
  (let [cont (r/atom contact)]
    [:div.contact
     [:div.column-1
      [text-input cont :name]]
     [:div.column-2
      [text-input cont :phone]]
     [:div.column-3
      [text-input cont :email "email"]]
     [:button.button.update {:type "button"
                             :on-click #(do (swap! cont assoc :edit false)
                                            (swap! contacts update-contact @cont))}
      "Update"]
     [:div.clear-row]]))

(defn display-contact [contact]
  (if (:edit contact)
    [edit-contact contact]
    [read-contact contact]))

(defn list-contacts []
  [:div
   (for [contact @contacts]
     ^{:key (:id contact)} [display-contact contact])])

(defn contacts-app []
  [:div#wrapper
   [:h1#content-title "Contacts"]
   [:div#contacts
    [:div.contact.header
       [:div.column-1
        [:i.fa.fa-user.fa-style] " Name"]
       [:div.column-2
        [:i.fa.fa-phone.fa-style] " Phone"]
       [:div.column-3
        [:i.fa.fa-envelope.fa-style] " Email"]
     [:div.clear-row]]
    [list-contacts]
    [add-contact-form]
    ]])

(defn by-id [id]
  (.getElementById js/document id))

(defn mountit []
  (r/render [contacts-app] (by-id "app")))

(mountit)
