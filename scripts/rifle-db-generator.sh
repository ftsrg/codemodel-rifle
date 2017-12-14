#!/bin/bash

# INTRODUCTION
# Codemodel-Rifle database generator from CSVs
#
# As this method is much more faster than importing with queries via
# the Bolt protocol, this is used for initial repository graph
# imports.
#
# The location of the Rifle-generated CSV files (containing the
# nodes' and relationships' data) can be configured by editing
# the SynchronizeRepository.csvFolderPath static final string.
#
#
# USAGE
# ./rifle-db-generator.sh \
# 	outputDatabaseFolderName \
# 	inputCsvFilePathForNodes \
# 	inputCsvFilePathForRelationships
#
# - outputDatabaseFolderName:
# 	An arbitrarily chosen name for the generated database's
# 	folder's nam to be created in the current working directory.
#
#  - inputCsvFilePathForNodes:
# 	The path (relative or absolute) of the CSV file containing
# 	the nodes' data.
#
# - inputCsvFilePathForRelationships:
# 	The path (relative or absolute) of the CSV file containing
# 	the relationships' data.


if [[ $# -ne 3 ]]; then
	echo "Usage: ./rifle-db-generator.sh outputDatabaseFolderName inputCsvFilePathForNodes inputCsvFilePathForRelationships"; exit 1
fi

set -e # exit with nonzero exit code if anything fails

export NEO4J_HOME=

$NEO4J_HOME/bin/neo4j-admin import --database=$1 --nodes=$2 --relationships=$3