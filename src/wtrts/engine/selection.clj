(ns wtrts.engine.selection
  (:require [wtrts.ui.input :refer :all]))


(defn setup-mouse-selection [state]
  state)


; Utilities for accessing entities by index

(defn filter-entities-indexed [state pred]
  (keep-indexed #(when (pred %2) %1) (:entities state)))

(defn- filter-entity-indices [state pred coll]
  (filter (fn [i] (pred (get-in state [:entities i]))) coll))

(defn get-entities-by-index [state coll keys]
  (reduce (fn [c i] (conj c (select-keys (get-in state [:entities i]) keys))) [] coll))


; Mouse pick

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


; Line of sight

(defn- update-entity-line-of-sight [state i e]
  (if (:can-see e)
    (-> e
        (assoc :los
          (filter (fn [x] (not= x i))
                  (filter-entities-indexed state (partial entity-in-circle? (:x e) (:y e) 30))))
        (assoc :los-entities (get-entities-by-index state (:los e) [:type :x :y])))
    e))

(defn update-line-of-sight [state]
  (assoc
    state :entities
    (into [] (map-indexed (partial update-entity-line-of-sight state) (:entities state)))))


; Tests

(filter-entity-indices {:entities [{:selectable true} {:selectable true}]} :selectable [0])

(get-entities-by-index {:entities [{:a 1} {:b 2} {:a 3}]} [1 0] [:a])

(update-mouse-select {:entities [{:selectable true} {:selectable true}]
                      :mouse {:pressed true, :first-press true, :x 62, :y 41}
                      :picked [1]})
(update-mouse-select {:entities [{:selectable true} {:selectable true}]
                      :mouse {:pressed true, :first-press true, :x 62, :y 41}
                      :picked [] :selected [1]})

