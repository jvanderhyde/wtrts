(ns wtrts.engine.selection
  (:require [wtrts.ui.input :refer :all]))

(defn setup-mouse-selection [state]
  (-> state
      (assoc :picked [])
      (assoc :selected [])))

(defn- sqr [x] (* x x))

(defn- mouse-pick? [state e]
  (let [e-radius 5]
    (< (+ (sqr (- (:x e) (mouse-x state))) (sqr (- (:y e) (mouse-y state)))) (sqr e-radius))))

(defn- update-mouse-pick [state]
  (assoc state :picked
    (filter (partial mouse-pick? state) (:entities state))))

(defn- update-mouse-select [state]
  (if (mouse-was-pressed? state)
    (assoc state :selected
      (filter :selectable (:picked state)))
    state))

(defn update-mouse-selection [state]
  (-> state
      update-mouse-pick
      update-mouse-select))
