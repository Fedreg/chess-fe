(ns chess.fe.state
  (:require
   [reagent.core      :as r]
   [ajax.core         :as ajax]
   [cognitect.transit :as ts]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; HOST
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def HOST "http://localhost:9000/")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; STATE
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defonce app-state
  (r/atom
   {:board       {}
    :move        []
    :legal-move? true
    }))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; HANDLERS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn deserialize [data]
  (let [in (ts/reader :json)]
    (ts/read in data)))

(defn start-handler [res]
  (let [clean-res (deserialize res)]
    (swap! app-state assoc :board       clean-res
                           :move        []
                           :legal-move? true)))

(defn move-handler [res]
  (let [clean-res (deserialize res)]
    (if (= :illegal clean-res)
      (swap! app-state assoc :legal-move? false)
      (swap! app-state assoc :board clean-res :legal-move? true))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; HTTP
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn start-game []
  (ajax/GET (str HOST "start")
            {:handler #(start-handler %)}))

(defn move [param]
  (ajax/GET (str HOST "move")
    {:params {:xy param}
     :handler #(move-handler %)}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; UPDATE
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn update-start! []
  (start-game))

(defn update-move! [id]
  (let [cols     [:a :b :c :d :e :f :g :h]
        x        (->  id first keyword)
        y        (->> id last js/parseInt (nth cols))
        move-arr (:move @app-state)]
    (cond
      (and (= 0 (count move-arr))
           (= "" (get-in @app-state [:board x y])))
      (println "EMPTY")

      (and (= 0 (count move-arr))
           (not= "" (get-in @app-state [:board x y])))
      (let [val {:id id :x x :y y}]
        (println "FIRST CLICK")
        (swap! app-state #(-> %
                              (update-in [:move] conj val)
                              (assoc-in  [:board x y :clicked?] true))))

      :else
      (let [sx  (-> move-arr first :x name)
            sy  (-> move-arr first :y name)
            id  (-> move-arr first :id)
            ex  (name x)
            ey  (name y)
            req (str sx sy ex ey)]
      (println "SECOND CLICK")
        (move req)
        (println "MOVE LEG?" (:legal-move? @app-state) sx sy id ex ey req)
        (when (:legal-move? @app-state)
          (swap! app-state #(-> %
                                (assoc-in [:move] [])
                                (assoc-in [:board sx sy :clicked?] false))))))))

(comment

  (move "2a4a")

  @app-state

  :end)
