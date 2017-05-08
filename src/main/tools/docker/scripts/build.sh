#!/bin/bash
SCRIPT_DIR="${0%\/*}";

function buildImage(){
	imageLocation=$1;
	imageName=$(grep -i 'name: *' ${imageLocation}/properties | sed -e 's/name: *//');
	tags=$(grep -i 'version: *' ${imageLocation}/properties | sed -e 's/version: *// ' | tr ';' '\t');
	
	if [[ -z ${tags} ]]
	then
	    # If no version was found, notify the user
		"${SCRIPT_DIR}/utils/printTitle.sh" "No version found @ $imageLocation/properties";
	else
	    tagNames="";
	    dockerTags="";
	    
	    for tag in ${tags}
	    do
	        tagNames="$tagNames $imageName:$tag"
	        dockerTags="$dockerTags -t $imageName:$tag"
	    done
	    
		"${SCRIPT_DIR}/utils/printTitle.sh" "Building"${tagNames};
		docker build${dockerTags} ${imageLocation}
	fi
}

function buildImages(){
    if [[ $# -gt 0 ]]
    then
        images=$(${SCRIPT_DIR}/utils/findAllImageDirectories.sh $1)
    else
        images=$(${SCRIPT_DIR}/utils/findAllImageDirectories.sh "${SCRIPT_DIR}/../images")
    fi
    for image in ${images}
	do
	    buildImage "${image}"
	done
	echo ''
	echo "Build done"
}

buildImages $@