(ns html
  (:require [hiccup.page :as hp]))

(defn head [& {:keys [title]}]
  [:head
   [:title title]
   [:meta {:name    "description"
           :content "A simple and free website for retrieving your static IP address online."}]
   [:meta {:name    "viewport"
           :content "width=device-width, initial-scale=1"}]
   [:meta {:charset "UTF-8"}]
   (hp/include-css "css/styles.css")])

(defn body [& {:keys [body]}]
  (into [:body {:class "p-2 w-screen h-screen flex flex-col"}]
        body))

(defn default-page [content]
  (hp/html5 {:lang "en"}
            (head :title (get-in content [:head :title]))
            (body content)))

(defn header [& {:keys [header-col]}]
  [:div {:class "w-full flex flex-row justify-around text-2xl"}
   [:div
    [:a {:class "" :href "/"} "Public IP"]]
   [:div
    [:ul {:class "flex flex-row space-x-4"}
     (for [i header-col]
       [:li {:class ""}
        [:a {:href (:link i)} (:display i)]])]]])

(defn main [& {:keys [addr]}]
  [:div {:class "p-4 w-full flex flex-col text-center grow"}
   [:div {:class "h-full"}]
   [:h1 {:class "text-3xl"} "IP Address:"]
   [:p {:class "text-4xl"} addr]
   [:div {:class "h-full"}]])

(defn other-main [& {:keys [message]}]
  [:div {:class "p-4 w-full flex flex-col text-center grow"}
   [:div {:class "h-full"}]
   [:p {:class "text-4xl"} message]
   [:div {:class "h-full"}]])
