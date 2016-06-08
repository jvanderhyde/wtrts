(ns wtrts.core
  (:require [wtrts.ui.input :refer :all]
            [wtrts.engine.flags :refer :all]
            [wtrts.engine.selection :refer :all])
  (:require [quil.core :as q]
            [quil.middleware :as qm]))


; Setup

(defn draw-farmer [e]
  (q/stroke 0 0 0)
  (q/fill 255 255 255)
  (q/ellipse (e :x) (e :y) 12 12 ))

(defn setup-entities [state]
  (update (assoc state :entities []) :entities conj
    {:type :farmer :x 40 :y 40 :state :standing :draw draw-farmer :selectable true}
    {:type :farmer :x 60 :y 40 :state :standing :draw draw-farmer :selectable true}
    ))

(defn setup-game []
  (-> {}
      setup-keyboard
      setup-mouse
      setup-flags
      setup-entities
      setup-mouse-selection))


; Update

(defn assoc-entity [state i key value]
  (assoc-in state [:entities i] key value))

(defn get-entity [state i]
  (get-in state [:entities i]))

(defn handle-click-ground [state]
  (if (and (mouse-was-pressed? state) (empty? (:picked state)))
    state state))

(defn handle-user-commands [state]
  (if (key-was-pressed? state \s)
    (add-timed-flag state :show-background 60)
    state))

(defn update-game [state]
  (-> state
      (update-keyboard [\a \s])
      update-mouse
      update-flags
      update-mouse-selection
      handle-user-commands
      ))


; Draw

(def state-for-repl (atom nil))

(defn draw-entity [e]
  ((:draw e) e))

(defn draw-selected [e]
    (q/no-fill)
    (q/stroke 255 0 0)
    (q/ellipse (:x e) (:y e) 5 5))

(defn draw-game! [state]
  (reset! state-for-repl state)
  (q/background 204)
  (dorun (map draw-entity (:entities state)))
  (when (key-is-down? state \a)
    (q/line 20 20 80 80))
  (when (flag? state :show-background)
    (q/rect 40 40 20 20 ))
  (when (not (empty? (:selected state)))
    (draw-selected (get-entity state (first (:selected state)))))
  (when (not (empty? (:picked state)))
    (q/ellipse 400 400 12 12)))


; Run

(q/defsketch example
  :size [640 480]
  :setup setup-game
  :update update-game
  :draw draw-game!
  ;:features [:keep-on-top]
  :middleware [qm/fun-mode])

(deref state-for-repl)
