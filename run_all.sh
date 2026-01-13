#!/bin/bash
make clean && make

run(){
    java -cp bin Main Lab1.map "$1" "$2" "$3"
}
run 15 1 1 &
run 10 1 1 &
run 1 15 1 &
run 10 4 1 &
wait
