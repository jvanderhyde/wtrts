(ns wtrts.engine.flags)


; Flags

(defn setup-flags [state]
  (merge state
         {:flags
          { :flags {}
            :timed {}}}))

(defn add-flag [state key]
  (assoc-in state [:flags :flags key] true))

(defn clear-flag [state key]
  (assoc-in state [:flags :flags key] false))

(defn toggle-flag [state key]
  (assoc-in state [:flags :flags key] (not (get-in state [:flags :flags key]))))

(defn add-timed-flag [state key time]
  (assoc-in state [:flags :timed key] time))

(defn flag? [state key]
  (or (get-in state [:flags :flags key])
      (get-in state [:flags :timed key])))

(defn- update-timed-flag [state key]
  (let [remaining-time (get-in state [:flags :timed key])]
    (if (> remaining-time 0)
      (update-in state [:flags :timed key] dec)
      (update-in state [:flags :timed] dissoc key))))

(defn update-flags [state]
  (reduce update-timed-flag state (keys (get-in state [:flags :timed]))))


