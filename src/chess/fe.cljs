(ns ^:figwheel-hooks chess.fe
  (:require
   [goog.dom          :as gdom]
   [reagent.core      :as r]
   [ajax.core         :as ajax]
   [cognitect.transit :as ts]

   [chess.fe.views    :as views]
   [chess.fe.state    :as state]))

(defn page [state]
  [:div views/page-style
   [:div (views/info-style 100) (str "ROUND "   (:move @state))]
   [:div (views/info-style 160) (str "POINTS: " (:points @state))]
   [:div (views/info-style 260) (str "TURN: "   (:color  @state))]
   [:div (views/info-style 340) (str "KILLS: "  (:kills  @state))]
   [:div (assoc (views/info-style 440 true) :onClick #(state/update-start!)) "NEW GAME"]
   [:div (if (not= {} (:board @state)) (views/board (:board @state)) "click START GAME to begin")]
   ])

(defn get-app-element []
  (gdom/getElement "app"))

(defn mount [el]
  (r/render-component [page state/app-state] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (state/update-start!)
    (mount el)))

(defn ^:after-load on-reload []
  (mount-app-element))

(comment

  :end)
