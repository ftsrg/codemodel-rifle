#!/bin/bash

set -e # exit with nonzero exit code if anything fails

NEO4J_VERSION=neo4j-community-3.1.1

curl https://neo4j.com/artifact.php?name=$NEO4J_VERSION-unix.tar.gz | tar xz
mv $NEO4J_VERSION neo4j-db
neo4j-db/bin/neo4j start
