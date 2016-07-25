(ns wtrts.behaviors.util)

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


