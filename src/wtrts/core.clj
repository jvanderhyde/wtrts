(ns wtrts.core
  (:require [wtrts.ui.input :refer :all]
            [wtrts.engine.flags :refer :all])
  (:require [quil.core :as q]
            [quil.middleware :as qm]))

(defn draw-farmer [e]
  (q/ellipse (e :x) (e :y) 12 12 ))

(defn setup-entities [state]
  (assoc state :entities
    [{:type :farmer :x 40 :y 40 :state :standing :selected false :draw draw-farmer}]))

(defn setup-game []
  (-> {}
      setup-keyboard
      setup-mouse
      setup-flags
      setup-entities))

(defn handle-user-commands [state]
  (if (key-was-pressed? state \s)
    (add-timed-flag state :show-background 60)
    state))

(defn update-game [state]
  (-> state
      (update-keyboard [\a \s])
      update-mouse
      update-flags
      handle-user-commands
      ))

(def state-for-repl (atom nil))

(defn draw-entity [e]
  ((:draw e) e))

(defn draw-game! [state]
  (reset! state-for-repl state)
  (q/background 204)
  (dorun (map draw-entity (:entities state)))
  (when (key-is-down? state \a)
    (q/line 20 20 80 80))
  (when (flag? state :show-background)
    (q/rect 40 40 20 20 ))
  (when (mouse-is-down? state)
    (q/ellipse (q/mouse-x) (q/mouse-y) 12 12)))


(q/defsketch example
  :size [640 480]
  :setup setup-game
  :update update-game
  :draw draw-game!
  ;:features [:keep-on-top]
  :middleware [qm/fun-mode])

(deref state-for-repl)
