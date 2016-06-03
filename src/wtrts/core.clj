(ns wtrts.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup-game []
  {})

(defn update-game [state]
  state)

(defn draw-game! [state]
  (q/background 204))


(q/defsketch example
  :size [640 480]
  :setup setup-game
  :draw draw-game!
  :update update-game
  ;:features [:keep-on-top]
  :middleware [m/fun-mode])

