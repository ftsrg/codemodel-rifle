#!/bin/bash
#set DEBUG
#SILENT=${DEBUG--s}
#REDIRECT=${DEBUG-/dev/null}

function coldStart {
  rm -rm ./database &> /dev/null
  nohup java -d64 -Xms4g -Xmx12g -jar ./rifle.jar &
  pid=$!

  sleep 10
}

function killServer {
  kill -2 $pid
}

function nowMilli {
  # http://stackoverflow.com/questions/16548528/linux-command-to-get-time-in-milliseconds
  echo $(($(date +%s%N)/1000000))
}

function timeMilli {
  local from="$(nowMilli)"
  ${@}
  echo $(($(nowMilli)-$from))
}

function initDb {
  echo $(curl -s -X POST -d 'MATCH (a) RETURN count(a)' "http://localhost:8080/codemodel/run?" | tail -n 3 | head -n 1)
}

function timeBatch {
  for i in `seq 1 $1`; do
    coldStart
    initDb &> /dev/null

    timeMilli $2

    killServer
  done
}

function importFile {
  cat $1 | curl -s --data-binary "@-" "http://localhost:8080/codemodel/handle?path=$1"
}

function removeFile {
  curl -s -X DELETE "http://localhost:8080/codemodel/handle?path=$1"
}

function buildCfg {
  curl -s -X GET "http://localhost:8080/codemodel/buildcfg"
}

function importExport {
  curl -s -X GET "http://localhost:8080/codemodel/importexport"
}

function searchDeadcode {
  curl -s -X GET "http://localhost:8080/codemodel/unusedfunctions"
}

function importWebclientFull {
  pushd ./sources/webclient-babel
  for filePath in $(find . -name '*.js'); do
    echo $filePath  $(cat $filePath | wc -l)  $(timeMilli importFile $filePath) $(initDb)
  done
  popd
}

function buildCfgWebclient {
  pushd ./sources/webclient-babel
  for filePath in $(find . -name '*.js'); do
    importFile $filePath
    echo $filePath $(timeMilli buildCfg) $(initDb)
    removeFile $filePath
  done
  popd
}

function importexportWebclientFull {
  coldStart
  importWebclientFull
  timeMilli importExport
}

function searchDeadcodeWebclient {
  pushd ./sources/webclient-babel
  for filePath in $(find . -name '*.js'); do
    importFile $filePath
    echo $filePath $(timeMilli searchDeadcode) $(initDb)
    removeFile $filePath
  done
  popd
}

function timeStartup {
  for i in `seq 1 10`; do
    coldStart

    timeMilli initDb
    cat performance-metrics.txt

    killServer
  done
}

timeBatch 1 importexportWebclientFull
