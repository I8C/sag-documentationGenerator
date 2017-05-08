#!/bin/bash
SCRIPT_DIR="${0%\/*}";
USER_NAME=
TOKEN=
URL=

function getJobs(){
    curl -s -X GET ${URL}/api/json \
        --user ${USER_NAME}:${TOKEN}|
    python "${SCRIPT_DIR}/readJobsFromJSON.py"
}

function setup(){
    if [[ $# -lt 1 ]]
    then
        echo -n "Enter the user name > "
        read USER_NAME
    else
        USER_NAME=$1
    fi
    
    if [[ $# -lt 2 ]]
    then
        echo -n "Enter the user token (or password) > "
        read -s TOKEN
    else
        TOKEN=$2
    fi
    
    if [[ $# -lt 3 ]]
    then
        echo -n "Enter the jenkins url > "
        read URL
    else
        URL=$3
    fi
}

setup $@
getJobs