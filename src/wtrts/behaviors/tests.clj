(ns wtrts.behaviors.tests
  (:require [wtrts.behaviors :refer :all]
            [wtrts.behaviors.walker :refer :all]))

; Walker tests

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
