#!/bin/bash

# this script uses the ag, the silver searcher (https://geoff.greer.fm/ag/)
# parameters passed to this script will be passed to ag,
# so you can specify -l (only print filenames), -i (ignore case)

AG_PARAMS=$@

declare -a FEATURES=("\*]" "\*\d" "shortestpath" "\w+\s*=\s*\(" "count" "collect" "UNWIND" "apoc" "all\s*\(" "exists\s*\(" "nodes\s*\(")

declare -a DML_FEATURES=("CREATE" "MERGE" "DELETE" "SET")

for FEATURE in "${FEATURES[@]}"; do
    echo \#\#\#\#\# $FEATURE
    ag "$FEATURE" -G ".*cypher" $AG_PARAMS
    echo
done
