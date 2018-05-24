(ns wtrts.ecs)

(defn create-game []
  {:entities {}})

(defn create-entity []
  [(java.util.UUID/randomUUID)
   {}])

(defn add-entity [game entity]
  (update-in game [:entities] conj entity))

(defn add-component [game entity com data]
  (assoc-in game [:entities (first entity) com] data))


;;example
(let [e1 (create-entity)]
  (->
    (create-game)
    (add-entity e1)
    (add-component e1 :position [3 4])
    (add-component e1 :name "ball")))

