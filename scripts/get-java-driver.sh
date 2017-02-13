#!/bin/bash

set -e # exit with nonzero exit code if anything fails

cd "$( cd "$( dirname "$0" )" && pwd )/../.."

git clone https://github.com/neo4j/neo4j-java-driver || true
cd neo4j-java-driver
git fetch
echo "Building neo4j-java-driver in quiet mode (only prints to console in case of errors)"
mvn clean install -q -DskipTests
