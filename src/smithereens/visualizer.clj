(ns smithereens.visualizer
  (:use quil.core
        smithereens.sound)
  (:require [overtone.core :as ovt]))

(def midi-state
  "Captures all the state of midi input events in a hash"
  (atom {}))

(defn setup
  "Setup for the Processing sketch. Establishes the initial world."
  []
  (no-stroke)
  (smooth)
  (frame-rate 60))

(defn parse-midi-notes
  "Takes an event hash e and parses note values into atom state"
  [state name e]
  (let [note (:note e)
        data {:state name :velocity (:velocity e)}]
    (println note data)
    (swap! state assoc note data)))

(defn register-midi-handlers
  "Register midi handlers for Overtone events"
  []
  (doseq [note-event [:note-on :note-off]]
    (ovt/on-event [:midi note-event]
                  #(parse-midi-notes midi-state note-event %)
                  (keyword (str "midi-" note-event)))))

(defonce register-handlers (register-midi-handlers))

(defn visualize
  "Loop through all the keys in state and visualize notes that are on"
  [midi]
  ;; TODO map the color to the velocity
  ;; TODO map the position to the note in a scale
  (doseq [k @midi]
    (let [[pos-y data] k
          vel (:velocity data)
          scaled-vel (+ 128 vel)
          active (= (:state data) :note-off)]
      (fill scaled-vel (* 100 0) 0 255)
      (when active
        (rect (random vel)
              (random vel)
              (+ pos-y (/ (width) 2))
              (+ pos-y (/ (height) 2)))))))

(defn clear-frame [] (background 255))

(defn draw-loop []
  (clear-frame)
  (visualize midi-state))

(defsketch visualizer
  :title "Smithereens"
  :renderer :java2d
  :setup setup
  :draw draw-loop
  :size [400 300])
