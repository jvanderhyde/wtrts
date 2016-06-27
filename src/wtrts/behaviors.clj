(ns wtrts.behaviors
  (:require [wtrts.engine.flags :refer :all]))

(defn set-behavior [e behavior]
  (-> e
      (assoc :behavior behavior)
      (assoc :state (:initial behavior))))

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

(defn- update-entity [e ups]
  (if (empty? ups)
    e
    ((:update (first ups)) e)))

(defn update-entity-with-behavior [e b]
  (let [trans-e (state-transition e (filter :condition? (get (:states b) (:state e))))]
    (update-entity trans-e (filter :update (get (:states b) (:state trans-e))))))

(def chop-time 30)

'(defbehavior chopper-behavior
  [[(state=? :ready-to-chop) (val= :wood-carrying :wood-capacity)]
   [(state-transition :walking-to-base)]
   [(state=? :ready-to-chop) (val< :wood-carrying :wood-capacity)]
   [(state-transition :chopping) (add-timed-flag :chopping chop-time)]])

(defn chopper-behavior-fn [e]
  (let [state=? (fn [key] (= (get e :state) key))
        val= (fn [key1 key2] (= (get e key1) (get e key2)))
        val< (fn [key1 key2] (< (get e key1) (get e key2)))]
    (cond (and (state=? :ready-to-chop)
               (val= :wood-carrying :wood-capacity))
          (-> e (assoc :state :walking-to-base))
          (and (state=? :ready-to-chop)
               (val< :wood-carrying :wood-capacity))
          (-> e
              (assoc :state :chopping)
              (add-timed-flag :chopping chop-time)))))

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

(defn- set-destination [e]
  (-> e
      (assoc :destination-x (:mouse-pick-x e))
      (assoc :destination-y (:mouse-pick-y e))))

(defn- sqr [x] (* x x))

(defn- reached-destination? [e]
  (let [entity-radius 3
        dx (- (:destination-x e) (:x e))
        dy (- (:destination-y e) (:y e))]
    (< (+ (sqr dx) (sqr dy)) (sqr entity-radius))))

(defn- clear-destination [e]
  (dissoc e :destination-x :destination-y))

(defn- vector2-normalize [dx dy]
  (let [norm (Math/sqrt (+ (* dx dx) (* dy dy)))]
    {:x (/ dx norm), :y (/ dy norm)}))

(defn- update-walk [e]
  (if (reached-destination? e) e
    (let [walk-speed 1
          dx (- (:destination-x e) (:x e))
          dy (- (:destination-y e) (:y e))
          heading (vector2-normalize dx dy)]
        (-> e
            (update :x (fn [x] (+ x (* walk-speed (:x heading)))))
            (update :y (fn [y] (+ y (* walk-speed (:y heading)))))))))

(def walker-behavior
  {:states
   {:walking
    [{:condition? reached-destination?
      :transition :idle
      :e-effect clear-destination}
     {:update update-walk}]
    :idle
    [{:condition? (fn [e] (:mouse-pick-x e))
      :transition :walking
      :e-effect (fn [e] (-> e
                            (set-destination)
                            (dissoc :mouse-pick-x :mouse-pick-y)))}]}
   :initial :idle})


(def ent1 (set-behavior {:type :farmer, :x 40, :y 40, :mouse-pick-x 0, :mouse-pick-y 0} walker-behavior))
ent1
(def ent2 (update-entity-with-behavior ent1 (:behavior ent1)))
ent2
(update-entity-with-behavior ent2 (:behavior ent2))

(def ent3 (set-behavior {:type :farmer, :x 40, :y 40, :mouse-pick-x 40, :mouse-pick-y 40} walker-behavior))
ent3
(def ent4 (update-entity-with-behavior ent3 (:behavior ent3)))
ent4
(update-entity-with-behavior ent4 (:behavior ent4))