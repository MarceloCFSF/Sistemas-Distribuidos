#!/bin/zsh

cmd="cd /home/marcelocfsf/Documents/UFABC/Sistemas-Distribuidos/EP1 ; /usr/bin/env /home/marcelocfsf/.asdf/installs/java/adoptopenjdk-8.0.345+1/bin/java -cp /tmp/cp_3c8vbrpropzcle2lkwapeczs3.jar peers.Peer"

tmux new-session -d -s peers
tmux send-keys -t peers $cmd Enter 
tmux split-window -h -t peers 
tmux send-keys -t peers $cmd Enter
tmux split-window -v -t peers 
tmux send-keys -t peers $cmd Enter
tmux select-pane -t peers:0.0 
tmux split-window -v -t peers
tmux send-keys -t peers $cmd Enter

init_peer() {
  tmux select-pane -t peers:$1
  tmux send-keys -t peers "1" Enter
  tmux send-keys -t peers "127.0.0.1:${2}" Enter
  tmux send-keys -t peers $3 Enter
  tmux send-keys -t peers "127.0.0.1:${4}" Enter
  tmux send-keys -t peers "127.0.0.1:${5}" Enter
}

base="/home/marcelocfsf/Documents/UFABC/Sistemas-Distribuidos/EP1/peers"

init_peer "0.0" "8040" "${base}/1" "8041" "8042" 
init_peer "0.1" "8041" "${base}/2" "8042" "8043" 
init_peer "0.2" "8042" "${base}/3" "8040" "8041" 
init_peer "0.3" "8043" "${base}/4" "8042" "8040" 

tmux a -t peers
