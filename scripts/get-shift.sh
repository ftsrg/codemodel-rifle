#!/bin/bash

set -e # exit with nonzero exit code if anything fails

cd "$( cd "$( dirname "$0" )" && pwd )/../../"

git clone https://github.com/steindani/shift-java.git || true
cd shift-java
echo "Building shift-java in quiet mode (only prints to console in case of errors)"
time mvn clean install -q -DskipTests
