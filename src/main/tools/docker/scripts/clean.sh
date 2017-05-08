#!/bin/bash
SCRIPT_DIR="${0%\/*}";

function removeMaster(){
    # 'docker ps' returning 1 line would mean docker only printed the header
    # 'docker ps' returning 2 or more lines means it found 1 or more matching names
    local FOUND_CONTAINERS="$(docker ps -f NAME=jenkins-master 2> /dev/null | grep -c '')";
    if [[ FOUND_CONTAINERS -ge 2 ]]
    then
    	echo 'The jenkins master is still running, you need to stop it first.'
    	exit
    fi
    
    local FOUND_CONTAINERS="$(docker ps -f NAME=jenkins-master -a 2> /dev/null | grep -c '')";
    if [[ FOUND_CONTAINERS -ge 2 ]]
    then
    	echo 'Cleaning up the jenkins master'
    	docker rm jenkins-master
    fi
}

function removeData(){
    local FOUND_CONTAINERS="$(docker ps -f NAME=jenkins-data -a 2> /dev/null | grep -c '')";
    if [[ FOUND_CONTAINERS -ge 2 ]]
    then
    	echo 'Cleaning up the jenkins data'
    	docker rm -v jenkins-data
    fi
}

function removeAll(){
	imageLocation=$1;
	# Get all images located in the giver directory
    images=$(${SCRIPT_DIR}/utils/findAllImageDirectories.sh ${imageLocation})
    
    # Loop though all the image directories
    for image in ${images}
	do
	    # Get the name of the image
	    imageName=$(grep -i 'name: *' ${image}/properties | sed -e 's/name: *//');
	    # Get all the container ids that run that image
	    local PS_OUTPUT=$(docker ps -a -q -f ancestor=${imageName} 2> /dev/null);
	    
	    echo "--${imageName}--"
	    
        if [[ ${#PS_OUTPUT} -gt 0 ]]
        then
            # If at least one image is found, loop through all of them
            echo "$PS_OUTPUT" | while read line
            do
                # Check if the container is still running
                local running=$(docker ps -q -f id=${line} 2> /dev/null)
                if [[ ${#running} -gt 0 ]]
                then
                    # If it's running, give feedback
                    echo "${line} is still running"
                else
                    # If it's not running, remove the container
                    echo "Removing ${line}"
                    docker rm ${line} > /dev/null
                fi
                
            done
        else
            # If no images are found
            echo 'No containers found';
        fi
        echo '';
	done
}

# Remove the remaining containers
removeAll "${SCRIPT_DIR}/../images"