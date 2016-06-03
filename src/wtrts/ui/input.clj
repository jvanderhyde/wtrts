(ns wtrts.ui.input
  (:require [quil.core :as q]))


; Keyboard input

(defn setup-keys [state]
  (merge state
         {:keyboard {}
          :key-first-press {}}))

(defn- register-key [state key-code]
  (if (= key-code (q/raw-key))
    (-> state
        (assoc-in [:key-first-press key-code] (not (get-in state [:keyboard key-code])))
        (assoc-in [:keyboard key-code] (q/key-pressed?)))
    state))

(defn update-keys [state key-list]
  (reduce register-key state key-list))

(defn key-is-down? [state key-code]
  ((:keyboard state) key-code))

(defn key-was-pressed? [state key-code]
  (and
    ((:keyboard state) key-code)
    ((:key-first-press state) key-code)))


; Mouse input

(defn setup-mouse [state]
  (merge state {:mouse { :pressed false :first-press true }}))

(defn update-mouse [state]
  (-> state
      (assoc-in [:mouse :first-press] (not (get-in state [:mouse :pressed])))
      (assoc-in [:mouse :pressed] (q/mouse-pressed?))))

(defn mouse-is-down? [state]
  ((:mouse state) :pressed))

(defn mouse-was-pressed? [state key-code]
  (and
    ((:mouse state) :pressed)
    ((:mouse state) :first-press)))


