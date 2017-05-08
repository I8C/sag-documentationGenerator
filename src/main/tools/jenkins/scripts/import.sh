#!/bin/bash
SCRIPT_DIR="${0%\/*}";
DATA_FOLDER='../data'
USER_NAME=
TOKEN=
URL=

function importJob(){
    name="$1";
    base_url="$2";
    
    curl -s -X GET ${base_url}/config.xml \
        --user ${USER_NAME}:${TOKEN} > "${SCRIPT_DIR}/${DATA_FOLDER}/${name}.xml"
}

function importAllJobs(){
    jobs="$("${SCRIPT_DIR}"/utils/readJobs.sh "${USER_NAME}" "${TOKEN}" "${URL}")"
    
    echo "$jobs" | while read job
	do
	    name=${job/:[^:]*}
	    url=$(echo "${job}" | sed -e 's/^[^:.]*://')
	    
	    echo "Processing ${name}"
	    importJob "${name}" "${url}"
	done
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
        echo "Enter the user token (or password) > "
        read -s TOKEN
    else
        TOKEN=$2
    fi
    
    if [[ $# -lt 3 ]]
    then
        echo -n "Enter jenkins url > "
        read URL
    else
        URL=$3
    fi
    
    if [ ! -d "${SCRIPT_DIR}/${DATA_FOLDER}" ]
    then
        mkdir "${SCRIPT_DIR}/${DATA_FOLDER}"
    fi
}

setup $@
importAllJobs
echo "Finished importing all jobs to ${SCRIPT_DIR}/${DATA_FOLDER}"