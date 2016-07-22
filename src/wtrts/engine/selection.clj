(ns wtrts.engine.selection
  (:require [wtrts.ui.input :refer :all]))


(defn setup-mouse-selection [state]
  state)


(defn filter-entities-indexed [state pred]
  (keep-indexed #(when (pred %2) %1) (:entities state)))

(defn- filter-entity-indices [state pred coll]
  (filter (fn [i] (pred (get-in state [:entities i]))) coll))

(defn get-entities-by-index [state coll keys]
  (reduce (fn [c i] (conj c (select-keys (get-in state [:entities i]) keys))) [] coll))

(defn- sqr [x] (* x x))

(defn entity-in-circle? [cx cy radius e]
  (< (+ (sqr (- (:x e) cx)) (sqr (- (:y e) cy))) (sqr radius)))

(defn- mouse-pick? [state e]
  (entity-in-circle? (mouse-x state) (mouse-y state) 5 e))

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

(get-entities-by-index {:entities [{:a 1} {:b 2} {:a 3}]} [1 0] [:a])

(update-mouse-select {:entities [{:selectable true} {:selectable true}]
                      :mouse {:pressed true, :first-press true, :x 62, :y 41}
                      :picked [1]})
(update-mouse-select {:entities [{:selectable true} {:selectable true}]
                      :mouse {:pressed true, :first-press true, :x 62, :y 41}
                      :picked [] :selected [1]})

