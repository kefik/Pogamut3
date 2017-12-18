#!/bin/bash

# Reset getopts
OPTIND=1

DIR="acc_navMesh"
while getopts "o" opt; do
    case $opt in
        o)
            DIR="acc_oldNavMesh"
            ;;
    esac
done

watch -n 5 "find ./results/$DIR -name data.aggregate.csv -exec cat {} \; | sort -r -u | column -t -s';'"
