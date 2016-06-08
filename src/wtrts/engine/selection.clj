(ns wtrts.engine.selection
  (:require [wtrts.ui.input :refer :all]))


(defn setup-mouse-selection [state]
  state)


(defn- filter-entities-indexed [state pred]
  (keep-indexed #(when (pred %2) %1) (:entities state)))

(defn- filter-entity-indices [state pred coll]
  (filter (fn [i] (pred (get-in state [:entities i]))) coll))


(defn- sqr [x] (* x x))

(defn- mouse-pick? [state e]
  (let [e-radius 5]
    (< (+ (sqr (- (:x e) (mouse-x state))) (sqr (- (:y e) (mouse-y state)))) (sqr e-radius))))

(defn- update-mouse-pick [state]
  (assoc state :picked
    (filter-entities-indexed state (partial mouse-pick? state))))

(defn- update-mouse-select [state]
  (if (mouse-was-pressed? state)
    (let [selectable-pick (filter-entity-indices state :selectable (:picked state))]
      (if (not (empty? selectable-pick))
        (assoc state :selected selectable-pick)
        state))
    state))

(defn update-mouse-selection [state]
  (-> state
      update-mouse-pick
      update-mouse-select))

(filter-entity-indices {:entities [{:selectable true} {:selectable true}]} :selectable [0])

(update-mouse-select {:entities [{:selectable true} {:selectable true}]
                      :mouse {:pressed true, :first-press true, :x 62, :y 41}
                      :picked [1]})
(update-mouse-select {:entities [{:selectable true} {:selectable true}]
                      :mouse {:pressed true, :first-press true, :x 62, :y 41}
                      :picked [] :selected [1]})
