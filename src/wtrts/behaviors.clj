(ns wtrts.behaviors
  (:require [wtrts.engine.flags :refer :all]))

(defn set-behavior [e behavior]
  (-> e
      (assoc :behavior behavior)
      (assoc :state (:initial behavior))))

(defn add-behavior-action [e action]
  (if (:actions e)
    (update e :actions conj action)
    (assoc e :actions [action])))

(defn- state-transition [e trs]
  (if (empty? trs)
    e
    (let [t (first trs)]
      (if ((:condition? t) e)
        (let [trans-e (assoc e :state (:transition t))]
          (if (:e-effect t)
            ((:e-effect t) trans-e)
            trans-e))
        (recur e (rest trs))))
    ))

(defn- update-entity [e up]
  (if up (up e) e))

(defn update-entity-with-behavior [e b]
  (state-transition e (get (:states b) (:state e))))

(defn update-entity-with-actions [e actions]
  (if (empty? actions) e
    (recur (update-entity e (get (first actions) (:state e))) (rest actions))))

(def chop-time 30)

(def chopper-behavior
  {:ready-to-chop
   [{:condition? (fn [e] (= (:wood-carrying e) (:wood-capacity e)))
     :transition :walking-to-base}
    {:condition? (fn [e] (< (:wood-carrying e) (:wood-capacity e)))
     :transition :chopping
     :e-effect (fn [e] (add-timed-flag e :chopping chop-time))}]
   :walking-to-base []
   :chopping []
   :walking-to-chop []
   :walking-up-to-tree []
   :idle []})


;Walker example behavior

(defn- set-destination [e]
  (-> e
      (assoc :destination-x (:mouse-pick-x e))
      (assoc :destination-y (:mouse-pick-y e))))

(defn- sqr [x] (* x x))

(defn- within-radius? [radius x1 y1 x2 y2]
  (let [dx (- x2 x1)
        dy (- y2 y1)]
    (< (+ (sqr dx) (sqr dy)) (sqr radius))))

(defn- entity-near-point? [e x y]
  (let [entity-radius 6]
    (within-radius? entity-radius (:x e) (:y e) x y)))

(defn- entity-near-entity? [e e2]
  (let [entity-radius 6]
    (within-radius? (* 2 entity-radius) (:x e) (:y e) (:x e2) (:y e2))))

(defn- reached-destination? [e]
    (entity-near-point? e (:destination-x e) (:destination-y e)))

(defn- clear-destination [e]
  (dissoc e :destination-x :destination-y))

(defn- vector2-normalize [dx dy]
  (let [norm (Math/sqrt (+ (* dx dx) (* dy dy)))]
    {:x (/ dx norm), :y (/ dy norm)}))

(defn- vector2-turn-right [x y]
  {:x y, :y (- x)})

(defn- update-walk [e]
  (if (reached-destination? e) e
    (let [walk-speed 1
          dx (- (:destination-x e) (:x e))
          dy (- (:destination-y e) (:y e))
          heading (vector2-normalize dx dy)]
        (-> e
            (update :x (fn [x] (+ x (* walk-speed (:x heading)))))
            (update :y (fn [y] (+ y (* walk-speed (:y heading)))))))))

(defn- intersects-obstacle? [origin direction e]
  (some (fn [loc] (entity-near-entity? e loc))
        (map (fn [n] {:x (+ (:x origin) (* n (:x direction))),
                      :y (+ (:y origin) (* n (:y direction)))})
             [6])))

(defn- obstacle-present? [e heading]
    (some (partial intersects-obstacle? e heading) (:los-entities e)))

(some (constantly true) [])

(defn- update-walk-avoid-obstacles [e]
  (if (reached-destination? e) e
    (let [walk-speed 1
          dx (- (:destination-x e) (:x e))
          dy (- (:destination-y e) (:y e))
          heading (vector2-normalize dx dy)
          adjusted-heading (if (obstacle-present? e heading) (vector2-turn-right (:x heading) (:y heading)) heading)]
        (-> e
            (update :x (fn [x] (+ x (* walk-speed (:x adjusted-heading)))))
            (update :y (fn [y] (+ y (* walk-speed (:y adjusted-heading)))))))))

(def walker-behavior
  {:states
   {:walking
    [{:condition? reached-destination?
      :transition :idle
      :e-effect clear-destination}]
    :idle
    [{:condition? (fn [e] (:mouse-pick-x e))
      :transition :walking
      :e-effect (fn [e] (-> e
                            (set-destination)
                            (dissoc :mouse-pick-x :mouse-pick-y)))}]}
   :initial :idle})

(def walker-behavior-action
  {:walking update-walk-avoid-obstacles})

; Tests

(def ent1a (set-behavior {:type :farmer, :x 40, :y 40, :mouse-pick-x 0, :mouse-pick-y 0} walker-behavior))
(def ent1 (add-behavior-action ent1a walker-behavior-action))
ent1
(def ent2a (update-entity-with-behavior ent1 (:behavior ent1)))
(def ent2 (update-entity-with-actions ent2a (:actions ent2a)))
ent2
(update-entity-with-behavior ent2 (:behavior ent2))

(def ent3 (set-behavior {:type :farmer, :x 40, :y 40, :mouse-pick-x 40, :mouse-pick-y 40} walker-behavior))
ent3
(def ent4 (update-entity-with-behavior ent3 (:behavior ent3)))
ent4
(update-entity-with-behavior ent4 (:behavior ent4))
