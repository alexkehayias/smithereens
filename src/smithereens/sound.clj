(ns smithereens.sound
  (:use overtone.core))

(defonce sc-server (boot-server))

(definst steel-drum [note 60 amp 0.8]
  (let [freq (midicps note)]
    (* amp
       (env-gen (perc 0.01 2.0) 1 1 0 1 :action FREE)
       (+ (sin-osc (/ freq 6))
          (rlpf (saw freq) (* 1.1 freq) 0.4)))))

(definst saw-wave [freq 440 attack 0.01 sustain 0.4 release 0.1 vol 0.4]
  (* (env-gen (lin-env attack sustain release) 1 1 0 1 FREE)
     (saw freq)
          vol))

;; Adding an instrument to midi player
(def player (midi-poly-player steel-drum))
;; Play some notes
;; (midi-player-stop)

;; DEBUG

(defonce debug-mid
  (do
    (on-event [:midi :note-on]
              (fn [e]
                (println {:state "on" :note (:note e) :velocity (:velocity e)}))
              :log-midi-note-on)
    
    (on-event [:midi :note-off]
              (fn [e]
                (println {:state "off" :note (:note e) :velocity (:velocity e)}))
              :log-midi-note-off)))
