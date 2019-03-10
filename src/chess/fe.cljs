(ns ^:figwheel-hooks chess.fe
  (:require
   [goog.dom          :as gdom]
   [reagent.core      :as r]
   [ajax.core         :as ajax]
   [cognitect.transit :as ts]

   [chess.fe.views    :as views]
   [chess.fe.state    :as state]))

(defn page [state]
  [:div
   [:div (str (:legal-move? @state))]
   [:div (str (:move @state))]
   [:div (if (not= {} (:board @state)) (views/board (:board @state)) "click START GAME to begin")]
   [:button {:onClick #(state/update-start!)} "START GAME"]
   ])

(defn get-app-element []
  (gdom/getElement "app"))

(defn mount [el]
  (r/render-component [page state/app-state] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

(defn ^:after-load on-reload []
  (mount-app-element))

(comment

  :end)
