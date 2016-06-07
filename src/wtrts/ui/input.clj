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
  (merge state {:mouse { :pressed false :first-press true :x 0 :y 0 }}))

(defn update-mouse [state]
  (-> state
      (assoc-in [:mouse :first-press] (not (get-in state [:mouse :pressed])))
      (assoc-in [:mouse :pressed] (q/mouse-pressed?))
      (assoc-in [:mouse :x] (q/mouse-x))
      (assoc-in [:mouse :y] (q/mouse-y))))

(defn mouse-is-down? [state]
  ((:mouse state) :pressed))

(defn mouse-was-pressed? [state]
  (and
    ((:mouse state) :pressed)
    ((:mouse state) :first-press)))

(defn mouse-x [state]
  ((:mouse state) :x))

(defn mouse-y [state]
  ((:mouse state) :y))


