(ns wtrts.ui.input
  (:require [quil.core :as q]))


; Keyboard input

(defn setup-keyboard [state]
  (merge state
         {:keyboard
          { :pressed {}
            :first-press {}}}))

(defn- register-key [state key-code]
  (if (= key-code (q/raw-key))
    (-> state
        (assoc-in [:keyboard :first-press key-code] (not (get-in state [:keyboard :pressed key-code])))
        (assoc-in [:keyboard :pressed key-code] (q/key-pressed?)))
    state))

(defn update-keyboard [state key-list]
  (reduce register-key state key-list))

(defn key-is-down? [state key-code]
  (get-in state [:keyboard :pressed key-code]))

(defn key-was-pressed? [state key-code]
  (and
    (get-in state [:keyboard :pressed key-code])
    (get-in state [:keyboard :first-press key-code])))


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


