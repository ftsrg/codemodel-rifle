#!/bin/bash

set -e # exit with nonzero exit code if anything fails

cd "$( cd "$( dirname "$0" )" && pwd )/../"

NEO4J_VERSION=neo4j-community-3.1.1
NEO4J_DIR=neo4j-db

echo Downloading and initializing a Neo4j server instance
echo Warning: authentication is turned off for the server

curl https://neo4j.com/artifact.php?name=$NEO4J_VERSION-unix.tar.gz | tar xz
mv $NEO4J_VERSION $NEO4J_DIR
sed -i.bak "s/#dbms.security.auth_enabled=false/dbms.security.auth_enabled=false/g" $NEO4J_DIR/conf/neo4j.conf
$NEO4J_DIR/bin/neo4j start
