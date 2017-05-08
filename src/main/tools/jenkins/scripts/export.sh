#!/bin/bash
SCRIPT_DIR="${0%\/*}";
DATA_FOLDER='../data'
USER_NAME=
TOKEN=
URL=
OVERRIDE=
CRUMB=
CRUMB_FIELD=

function exportJob(){
    name="$1";
    
    jobExists=false
    jobs="$("${SCRIPT_DIR}/utils/readJobs.sh" "${USER_NAME}" "${TOKEN}" "${URL}")"
    
    if [[ ${#jobs} -gt 0 ]]
    then 
        found="$(echo "${jobs}" | while read job
	    do
	        jobName=${job/:[^:]*}
	        jobUrl=$(echo "${job}" | sed -e 's/^[^:.]*://')
	        
	        if [ "${jobName}" = "${name}" ]
	        then
	            echo 'true'
	            break
	        fi
	    done)"
	    
	    if [ "${found}" = 'true' ]
	    then
	        jobExists=true
	    fi
	fi
    
	if [ "${jobExists}" = 'false' ]
	then
	    echo "Creating ${name}"
        curl -s -X POST \
            -H "${CRUMB_FIELD}:${CRUMB} " \
            -H "Content-Type: application/xml " \
            --user "${USER_NAME}:${TOKEN}" \
            --data-binary "@${SCRIPT_DIR}/${DATA_FOLDER}/${name}.xml" \
            "${URL}/createItem?name=${name/ /%20}"
	fi
	
	if [ "${jobExists}" = 'false' ] || [ "${jobExists}" = 'true' ] && [ "${OVERRIDE}" = 'true' ]
    then
	    echo "Updating ${name}"
        curl -s -X POST \
            -H "${CRUMB_FIELD}:${CRUMB}" \
            --user "${USER_NAME}:${TOKEN}" \
            --data-binary "@${SCRIPT_DIR}/${DATA_FOLDER}/${name}.xml" \
            "${URL}/job/${name/ /%20}/config.xml"
    fi
    
    if [ "${jobExists}" = 'true' ] && [ "${OVERRIDE}" = 'false' ]
    then
        echo "Ignoring ${name}"
    fi
}

function exportAllJobs(){
    jobs="$("${SCRIPT_DIR}"/utils/readJobs.sh "${USER_NAME}" "${TOKEN}" "${URL}")"
    
    for job in "${SCRIPT_DIR}"/${DATA_FOLDER}/*
	do
	    name=${job##*/}
	    name=${name%.*}
	    
	    echo "Processing ${name}"
	    exportJob "${name}"
	    echo ''
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
        echo -n "Enter the jenkins url > "
        read URL
    else
        URL=$3
    fi
    
    if [[ $# -lt 4 ]]
    then
        echo -n "Would you like to override existing jobs? > "
        read OVERRIDE
    else
        OVERRIDE=$4
    fi
    
    while  [ "${OVERRIDE}" != 'true' ] && [ "${OVERRIDE}" != 'false' ] && 
        ! [[ "${OVERRIDE}" =~ ^[yY].* ]] && ! [[ "${OVERRIDE}" =~ ^[nN].* ]]
    do
        echo "Invalid override: ${OVERRIDE}"
        echo -n "Would you like to override existing jobs? > "
        read OVERRIDE
    done
    
    if [[ $# -lt 4 ]]
    then
        echo "You can also execute this script as following:"
        echo "$0 '${USER_NAME}' '$(echo ${TOKEN} | sed s/./?/g)' '${URL}' '${OVERRIDE}'"
        echo ''
    fi
    
    if [[ "${OVERRIDE}" =~ ^[yY].* ]]
    then
        OVERRIDE=true
    elif [[ "${OVERRIDE}" =~ ^[nN].* ]]
    then
        OVERRIDE=false
    fi
    
    local response=$(curl -s -X GET "${URL}/crumbIssuer/api/json?xpath=concat(//crumbRequestField,":",//crumb)" \
        --user ${USER_NAME}:${TOKEN})
    
    CRUMB=$(echo ${response} | grep -P '(?<=crumb\":\")([0-9a-z]*)' -o)
    CRUMB_FIELD=$(echo ${response} | grep -P '(?<=crumbRequestField\":\")([0-9a-zA-Z\-\.]*)' -o)
    
    if [ ! -d "${SCRIPT_DIR}/${DATA_FOLDER}" ]
    then
        mkdir "${SCRIPT_DIR}/${DATA_FOLDER}"
    fi
}

setup $@
exportAllJobs
echo "Finished exporting all jobs to ${SCRIPT_DIR}/${DATA_FOLDER}"