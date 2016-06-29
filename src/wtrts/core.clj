(ns wtrts.core
  (:require [wtrts.ui.input :refer :all]
            [wtrts.engine.flags :refer :all]
            [wtrts.engine.selection :refer :all]
            [wtrts.behaviors :refer :all])
  (:require [quil.core :as q]
            [quil.middleware :as qm]))


; Setup

(defn draw-farmer [e]
  (q/stroke 0 0 0)
  (q/fill 255 255 255)
  (q/ellipse (int (e :x)) (int (e :y)) 12 12 ))

(defn draw-tree [e]
  (q/stroke 0 100 0)
  (q/fill 0 100 0)
  (q/triangle (e :x) (- (e :y) 8) (- (e :x) 7) (+ (e :y) 4) (+ (e :x) 7) (+ (e :y) 4)))

(defn setup-entities [state]
  (update (assoc state :entities []) :entities conj
    {:type :farmer, :x 40, :y 40, :behavior walker-behavior, :state (:initial walker-behavior), :draw draw-farmer, :selectable true}
    {:type :farmer, :x 60, :y 40, :behavior walker-behavior, :state (:initial walker-behavior), :draw draw-farmer, :selectable true}
    {:type :tree, :x 275, :y 150, :amount 10, :draw draw-tree}
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

(defn update-selected-entities
  ([state f]
   (reduce (fn [s i] (update-in s [:entities i] f)) state (:selected state)))
  ([state f x]
   (reduce (fn [s i] (update-in s [:entities i] f x)) state (:selected state)))
  ([state f x y]
   (reduce (fn [s i] (update-in s [:entities i] f x y)) state (:selected state))))

(defn- save-mouse [e x y]
  (-> e
      (assoc :mouse-pick-x x)
      (assoc :mouse-pick-y y)))

(defn handle-click-ground [state]
    (update-selected-entities state save-mouse (mouse-x state) (mouse-y state)))

(defn change-type [e new-type]
  (assoc e :type new-type))

(defn handle-click-tree [state entity]
  (if (= :tree (:type entity))
    (-> state
        (update-selected-entities change-type :chopper))
    state))

(defn action-performed-on-selection [state]
  (if (empty? (:picked state))
    (handle-click-ground state)
    (let [picked-entity (get-entity state (first (:picked state)))]
      (-> state
          (handle-click-tree picked-entity)))))

(defn handle-user-commands [state]
  (if (and (mouse-was-pressed? state) (not (empty? (:selected state))))
    (action-performed-on-selection state)
    state))

(defn- update-entity [e]
  (if (:behavior e)
    (update-entity-with-behavior e (:behavior e))
    e))

(defn update-entities [entities]
  (into [] (map update-entity entities)))

(defn- entity-can-see? [e]
  (contains? #{:farmer :chopper} (:type e)))

(defn- update-entity-line-of-sight [state i e]
  (if (entity-can-see? e)
    (assoc e :los
      (filter (fn [x] (not= x i))
              (filter-entities-indexed state (partial entity-in-circle? (:x e) (:y e) 30))))
    e))

(defn update-line-of-sight [state]
  (assoc
    state :entities
    (into [] (map-indexed (partial update-entity-line-of-sight state) (:entities state)))))

(defn update-game [state]
  (-> state
      (update-keyboard [\a \s])
      update-mouse
      update-flags
      update-mouse-selection
      update-line-of-sight
      handle-user-commands
      (update :entities update-entities)
      ))


; Draw

(def state-for-repl (atom nil))

(defn draw-entity [e]
  ((:draw e) e))

(defn draw-selected [e]
    (q/no-fill)
    (q/stroke 255 0 0)
    (q/ellipse (int (:x e)) (int (:y e)) 5 5))

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


