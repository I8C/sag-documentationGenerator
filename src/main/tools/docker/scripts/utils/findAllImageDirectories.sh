#!/bin/bash
function findAllImageDirectories(){
    dir="$1";
    foundDirs=();
    
    # If the directory itself has a docker file
    if [[ -f ${dir}/Dockerfile ]]
    then
        foundDirs+=("${dir}")
    fi
    
    # Loop though all the entries in the directory
    for entry in ${dir}/*
	do
	    # If the entry is a directory
		if [[ -d ${entry} ]]
		then
            foundDirs+=("$(findAllImageDirectories "${entry}")");
        fi
	done
	
    for dir in "${foundDirs[@]}"
	do
		echo "${dir}"
	done
}

findAllImageDirectories $@