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
    :round       1
    :color       :white
    :kills       nil
    :points      nil
    }))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; HANDLERS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn deserialize [data]
  (let [in (ts/reader :json)]
    (ts/read in data)))

(defn start-handler [res]
  (let [clean-res (deserialize res)]
    (swap! app-state assoc
           :board  (:board  clean-res)
           :round  (:round  clean-res)
           :points (:points clean-res)
           :kills  (:kills  clean-res)
           :move   []
           :round  1
           :color  :white)))

(defn move-handler [res sx sy]
  (let [clean-res (deserialize res)]
    (when (not= :illegal clean-res)
      (swap!
       app-state #(-> %
                      (assoc-in  [:move  ] [])
                      (assoc-in  [:round ] (:round  clean-res))
                      (assoc-in  [:points] (:points clean-res))
                      (assoc-in  [:kills ] (:kills  clean-res))
                      (assoc-in  [:board ] (:board  clean-res))
                      (update-in [:color ] (fn [] (if (= :white (:color @app-state)) :black :white)))
                      (assoc-in  [:board sx sy :clicked?] false))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; HTTP
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn start-game []
  (ajax/GET (str HOST "start")
            {:handler #(start-handler %)
             :api     (js/XMLHttpRequest.)}))

(defn move [param sx sy]
  (ajax/GET (str HOST "move")
    {:params  {:xy param}
     :api     (js/XMLHttpRequest.)
     :handler #(move-handler % sx sy)}))

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
           (not= "" (get-in @app-state [:board x y]))
           (= (:color @app-state) (get-in @app-state [:board x y :color])))
      (let [val {:id id :x x :y y}]
        (swap! app-state #(-> %
                              (update-in [:move] conj val)
                              (assoc-in  [:board x y :clicked?] true))))

      (and
       (= 0 (count move-arr))
       (not= (:color @app-state)
             (get-in @app-state [:board x y :color])))
      (println "ILLEGAL")

      (and
       (= 1 (count move-arr))
       (= id  (:id (first move-arr))))
      (swap! app-state #(-> %
                            (assoc :move [])
                            (assoc-in [:board x y :clicked?] false)))

      :else
      (let [sx  (-> move-arr first :x name)
            sy  (-> move-arr first :y name)
            id  (-> move-arr first :id)
            ex  (name x)
            ey  (name y)
            req (str sx sy ex ey)]
        (move req sx sy)))))

(comment

  (move "2a4a")

 (:move @app-state)

  :end)
