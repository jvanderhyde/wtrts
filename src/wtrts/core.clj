(ns wtrts.core
  (:require [wtrts.ui.input :refer :all])
  (:require [quil.core :as q]
            [quil.middleware :as qm]))


(defn setup-game []
  (-> {}
      setup-keys
      setup-mouse))

(defn update-game [state]
  (-> state
      (update-keys [\a])
      update-mouse))

(defn draw-game! [state]
  (q/background 204)
  (when (key-is-down? state \a)
    (q/line 20 20 80 80))
  (when (mouse-is-down? state)
    (q/ellipse (q/mouse-x) (q/mouse-y) 12 12)))


(q/defsketch example
  :size [640 480]
  :setup setup-game
  :update update-game
  :draw draw-game!
  ;:features [:keep-on-top]
  :middleware [qm/fun-mode])

