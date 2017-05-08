#!/bin/bash
SCRIPT_DIR="${0%\/*}";

function stopAll(){
	imageLocation=$1;
	# Get all images located in the giver directory
    images=$(${SCRIPT_DIR}/utils/findAllImageDirectories.sh ${imageLocation})
    
    # Loop though all the image directories
    for image in ${images}
	do
	    # Get the name of the image
	    imageName=$(grep -i 'name: *' ${image}/properties | sed -e 's/name: *//');
	    # Get all the container ids that run that image
	    local PS_OUTPUT=$(docker ps -q -f ancestor=${imageName} 2> /dev/null);
	    
	    echo "--${imageName}--"
	    
        if [[ ${#PS_OUTPUT} -gt 0 ]]
        then
            # If at least one image is found, loop through all of them
            echo "$PS_OUTPUT" | while read line
            do
                # Stop the container
                echo "Stopping ${line}"
                docker stop ${line} > /dev/null
            done
        else
            # If no images are found
            echo 'No containers found';
        fi
        echo '';
	done
}

function stopIfRunning(){
    local containerName=$1;
    
    # Returning 1 line would mean the container is running
    local FOUND_CONTAINERS="$(docker ps -q -f NAME=${containerName} 2> /dev/null | grep -c '')";
    if [[ FOUND_CONTAINERS -eq 1 ]]
    then
    	docker stop ${containerName}
    	return 1;
    else
    	return 0;
    fi
}

function stopMasterAndData(){

    stopIfRunning 'jenkins-master'
    if [[ $? -eq 1 ]]
    then
        echo "Stopped the jenkins master container"
    fi
    stopIfRunning 'jenkins-data'
    if [[ $? -eq 1 ]]
    then
        echo "Stopped the jenkins data container"
    fi
}

stopAll "${SCRIPT_DIR}/../images"
stopMasterAndData