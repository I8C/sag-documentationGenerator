#!/bin/bash
MASTER_IMAGE='i8c/jenkins-master'
MASTER_NAME='jenkins-master'
DATA_IMAGE='i8c/jenkins-data'
DATA_NAME='jenkins-data'
SCRIPT_DIR="${0%\/*}";

function createDataContainer(){
    local FOUND_CONTAINERS="$(docker ps -q -f NAME=${DATA_NAME} 2> /dev/null -a | grep -c '')";
    if [[ FOUND_CONTAINERS -lt 1 ]]
    then
    	echo 'Creating a new data container.'
    	docker run --name=${DATA_NAME} ${DATA_IMAGE}
    else
    	echo 'Using the already existing data container.'
    fi
}

function runMaster(){
    local host_jenkins_home=$1;
    local FOUND_CONTAINERS="$(docker ps -q -f NAME=${MASTER_NAME} -a 2> /dev/null | grep -c '')";
    if [[ FOUND_CONTAINERS -lt 1 ]]
    then
    	# If the container doesn't exist, create a new one
    	if [[ ${#host_jenkins_home} -gt 0 ]]
    	then
    	    echo "Starting a new jenkins master with persistent storage located @ ${host_jenkins_home}"
    	    docker run -p 8080:8080 -p 50000:50000 --name=${MASTER_NAME} -v "$host_jenkins_home:/var/jenkins_home" -d ${MASTER_IMAGE}
    	else
    	    createDataContainer
    	    ${SCRIPT_DIR}/utils/printTitle.sh '[WARNING]' 'No real data persistence used';
            echo "The ${MASTER_NAME} will use virtual persistent storage to store the jenkins data. When the container ${DATA_NAME} is removed, so will all the data."
            echo 'To write the jenkins data to the host hard drive, add a single argument with the path to the host directory where you want to store everything.'
            echo 'Example: ./run.sh /var/jenkins/'
            echo ''
    	    echo 'Starting a new jenkins master with virtual persistent storage'
    	    docker run -p 8080:8080 -p 50000:50000 --name=${MASTER_NAME} --volumes-from=jenkins-data -d ${MASTER_IMAGE}
    	fi
    else
    	FOUND_CONTAINERS="$(docker ps -q -f NAME=${MASTER_NAME} 2> /dev/null | grep -c '')";
    	if [[ FOUND_CONTAINERS -lt 1 ]]
    	then
    		# If the container isn't running but has not been removed, start it again
    		echo 'Starting the last jenkins master and its settings.'
    		echo 'To start a new master, stop and remove the container.'
    		docker start ${MASTER_NAME}
    	else
    		# If the container is already running, notify
    		echo 'The jenkins master is already running'
    	fi
    fi
}

runMaster $1