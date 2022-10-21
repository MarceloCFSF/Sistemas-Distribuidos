#!/bin/zsh

cmd="cd /home/marcelocfsf/Documents/UFABC/Sistemas-Distribuidos/EP1 ; /usr/bin/env /usr/lib/jvm/java-8-openjdk-amd64/bin/java -cp /tmp/cp_916k23y2mc8mmumnretfu4dsk.jar peers.Peer" 

tmux new-session -d -s peers
tmux send-keys -t peers $cmd Enter 
tmux split-window -h -t peers 
tmux send-keys -t peers $cmd Enter

init_peer() {
  tmux select-pane -t peers:$1
  tmux send-keys -t peers "1" Enter
  tmux send-keys -t peers "127.0.0.1:${2}" Enter
  tmux send-keys -t peers $3 Enter
  tmux send-keys -t peers "127.0.0.1:${4}" Enter
  tmux send-keys -t peers "127.0.0.1:${5}" Enter
}

base="peers"

init_peer "0.0" "8040" "${base}/1" "8041" "8041" 
init_peer "0.1" "8041" "${base}/2" "8040" "8040" 

tmux a -t peers
