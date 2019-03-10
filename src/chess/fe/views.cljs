(ns chess.fe.views
  (:require
   [goog.dom          :as gdom]
   [reagent.core      :as r]

   [chess.fe.state    :as state]))

(declare square)
(declare square-row-style)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Util
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-pieces
  "gets the pieces from the state.board and inserts them into dom"
  [row data]
  (let [base  (map-indexed
               (fn [idx [k v]]
                 (let [piece    (:name v)
                       color    (:color v)
                       clicked? (:clicked? v)]
                   (square row idx piece color clicked?)))
               (get-in data [(-> row str keyword)]))
        final (reduce (fn [acc v] (conj acc v))
                      [:div square-row-style]
                      base)]
    final))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Styles
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn square-style [row n color clicked?]
  {:style
   {:box-sizing       "border-box"
    :color            (if (= :black color) "blue" "#000")
    :width            "60px"
    :height           "60px"
    :border           "1px solid #0c60f0"
    :margin           "6px 6px 0 0"
    :padding          "20px 25px"
    :cursor           "pointer"
    :background-color (cond
                        clicked?                    "red"
                        (and (odd?  row) (odd?  n)) "#dde"
                        (and (even? row) (even? n)) "#dde"
                        :else                       "#999")}})

(def square-row-style
  {:style
   {:display        "flex"
    :flex-direction "row"}})

(def board-style
  {:style
   {:display        "flex"
    :flex-direction "column"}})

(def file-row-style
  {:style
   {:color "#555"
    :padding "10px 29px"}})

(def rank-row-style
  {:style
   {:color "#555"
    :padding "22px 10px"}})

(def page-style
  {:style
   {:height           "100%"
    :width            "100%"
    :margin           "100px"
    :background-color "#222"}})

(def body-style
  {:style
   {:background-color "#222"}})

(defn turn-style [round]
  {:style
   {:color "#fff"
    :font-size "20px"}})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Markup
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn square
  "View for each square of the board"
  [row n piece color clicked?]
  (let [id  (str "div#" row n)
        div (keyword id)]
  [div
   (assoc
    (square-style row n color clicked?)
    :onClick
    #(state/update-move! (-> % .-target .-id)))
   piece]))

(def file-row
  "In chess, the horizontal rows are called 'file'"
  [:div
   [:span file-row-style "a"]
   [:span file-row-style "b"]
   [:span file-row-style "c"]
   [:span file-row-style "d"]
   [:span file-row-style "e"]
   [:span file-row-style "f"]
   [:span file-row-style "g"]
   [:span file-row-style "h"]])

(defn board
  "Main board view"
  [data]
  [:div board-style
   [:div square-row-style (get-pieces 8 data)
    [:span rank-row-style "8"]]
   [:div square-row-style (get-pieces 7 data)
    [:span rank-row-style "7"]]
   [:div square-row-style (get-pieces 6 data)
    [:span rank-row-style "6"]]
   [:div square-row-style (get-pieces 5 data)
    [:span rank-row-style "5"]]
   [:div square-row-style (get-pieces 4 data)
    [:span rank-row-style "4"]]
   [:div square-row-style (get-pieces 3 data)
    [:span rank-row-style "3"]]
   [:div square-row-style (get-pieces 2 data)
    [:span rank-row-style "2"]]
   [:div square-row-style (get-pieces 1 data)
    [:span rank-row-style "1"]]
   file-row])

(defn turn
  "Displays whose turn it is"
  [round]
  [:div (turn-style round) (if (odd? round) :white :black)])


(comment

  (def test-state
{:move {:piece :p, :from [:7 :a], :to [:6 :a]},
 :round 3,
 :points {:white 0, :black 0},
 :kills {:white [], :black []},
 :board
 {:8 {:a :r, :b :b, :c :k, :d :Q, :e :K, :f :k, :g :b, :h :r},
  :7 {:a "", :b :p, :c :p, :d :p, :e :p, :f :p, :g :p, :h :p},
  :6 {:a :p, :b "", :c "", :d "", :e "", :f "", :g "", :h ""},
  :5 {:a "", :b "", :c "", :d "", :e "", :f "", :g "", :h ""},
  :4 {:a "", :b "", :c "", :d :p, :e "", :f "", :g "", :h ""},
  :3 {:a "", :b "", :c "", :d "", :e "", :f "", :g "", :h ""},
  :2 {:a :p, :b :p, :c :p, :d "", :e :p, :f :p, :g :p, :h :p},
  :1 {:a :r, :b :b, :c :k, :d :Q, :e :K, :f :k, :g :b, :h :r}}})

  (def test-board
    {:8
 {:a
  {:max 8,
   :direction :straight,
   :attack :straight,
   :name :r,
   :value 5,
   :color :black},
  :b
  {:max 8,
   :direction :diagonal,
   :attack :diagonal,
   :name :b,
   :value 3,
   :color :black},
  :c
  {:max 2,
   :direction :el,
   :attack :el,
   :name :k,
   :value 3,
   :color :black},
  :d
  {:max 8,
   :direction :multi,
   :attack :multi,
   :name :Q,
   :value 9,
   :color :black},
  :e
  {:max 1,
   :direction :multi,
   :attack :multi,
   :name :K,
   :value 25,
   :color :black},
  :f
  {:max 2,
   :direction :el,
   :attack :el,
   :name :k,
   :value 3,
   :color :black},
  :g
  {:max 8,
   :direction :diagonal,
   :attack :diagonal,
   :name :b,
   :value 3,
   :color :black},
  :h
  {:max 8,
   :direction :straight,
   :attack :straight,
   :name :r,
   :value 5,
   :color :black}},
 :7
 {:a
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :black},
  :b
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :black},
  :c
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :black},
  :d
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :black},
  :e
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :black},
  :f
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :black},
  :g
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :black},
  :h
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :black}},
 :6 {:a "", :b "", :c "", :d "", :e "", :f "", :g "", :h ""},
 :5 {:a "", :b "", :c "", :d "", :e "", :f "", :g "", :h ""},
 :4
 {:a
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :white},
  :b "",
  :c "",
  :d "",
  :e "",
  :f "",
  :g "",
  :h ""},
 :3 {:a "", :b "", :c "", :d "", :e "", :f "", :g "", :h ""},
 :2
 {:a "",
  :b
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :white},
  :c
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :white},
  :d
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :white},
  :e
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :white},
  :f
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :white},
  :g
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :white},
  :h
  {:max 2,
   :direction :straight,
   :attack :diagonal,
   :name :p,
   :value 1,
   :color :white}},
 :1
 {:a
  {:max 8,
   :direction :straight,
   :attack :straight,
   :name :r,
   :value 5,
   :color :white},
  :b
  {:max 8,
   :direction :diagonal,
   :attack :diagonal,
   :name :b,
   :value 3,
   :color :white},
  :c
  {:max 2,
   :direction :el,
   :attack :el,
   :name :k,
   :value 3,
   :color :white},
  :d
  {:max 8,
   :direction :multi,
   :attack :multi,
   :name :Q,
   :value 9,
   :color :white},
  :e
  {:max 1,
   :direction :multi,
   :attack :multi,
   :name :K,
   :value 25,
   :color :white},
  :f
  {:max 2,
   :direction :el,
   :attack :el,
   :name :k,
   :value 3,
   :color :white},
  :g
  {:max 8,
   :direction :diagonal,
   :attack :diagonal,
   :name :b,
   :value 3,
   :color :white},
  :h
  {:max 8,
   :direction :straight,
   :attack :straight,
   :name :r,
   :value 5,
   :color :white}}})

(board test-board)

 :end)
