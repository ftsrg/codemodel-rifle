#!/bin/bash

set -e # exit with nonzero exit code if anything fails

cd "$( cd "$( dirname "$0" )" && pwd )/../.."

git clone https://github.com/szarnyasg/neo4j-drivers || true
cd neo4j-drivers
git fetch
./gradlew publishToMavenLocal
