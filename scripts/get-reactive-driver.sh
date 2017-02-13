#!/bin/bash

set -e # exit with nonzero exit code if anything fails

cd "$( cd "$( dirname "$0" )" && pwd )/../.."

git clone https://github.com/szarnyasg/neo4j-reactive-driver || true
cd neo4j-reactive-driver
git fetch
./gradlew publishToMavenLocal
