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
    (swap! state assoc note data)))

(defn register-midi-handlers
  "Register midi handlers for Overtone events"
  []
  (doseq [i [:note-on :note-off]]
    (ovt/on-event [:midi i]
                  #(parse-midi-notes midi-state i %)
                  (keyword (str "midi-" i)))))

(defonce register-handlers (register-midi-handlers))

(defn visualize
  "Loop through all the keys in state and visualize notes that are on"
  [midi]
  (fill (random 100) (random 100) (random 200) (random 200))
  (doseq [k @midi]
    (let [[pos-y data] k
          dimension (:velocity data)
          active (= (:state data) :note-on)]
      (when active
        (rect (random dimension) (random dimension) (+ pos-y (/ (width) 2)) (+ pos-y (/ (height) 2)))))))

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
