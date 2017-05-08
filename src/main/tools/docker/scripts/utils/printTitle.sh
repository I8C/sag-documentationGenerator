#!/bin/bash
defaultInnerLength=40; # Amount of characters between each left and right border
minimunMargin=2; # Minimum 2 character space between the boarder and the text
borderCharacter='#' # The character that represents the border

function printTitle () {
	# Find the the longest argument length
	maxLength=0;
	for arg in "$@"
	do
		if [[ $maxLength -lt ${#arg} ]]
		then
			maxLength=${#arg};
		fi
	done
	
	# Define the inner length
	if [[ $defaultInnerLength -lt $maxLength ]]
	then
		innerLength=$maxLength;
	else
		innerLength=$defaultInnerLength;
	fi
	
	# Check if the inner length needs to be expanded
	# For the margin
	if [[ $(($maxLength+$minimunMargin*2)) -gt $innerLength ]]
	then
		innerLength=$(($maxLength+$minimunMargin*2));
	fi
	
	j=0;
	for arg in "$@"
	do
		textLength=${#arg}; # How many characters the argument has
		title=$borderCharacter; # The text that will be displayed in the middle
		margin=innerLength-textLength; # The mount of spaced left and right from the text
		
		# Add the left side spaces
		i=0;
		while [[ "$i" -lt margin/2 ]]
		do 
			i=$(($i + 1)); 
			title="$title ";
		done
		
		# Add the title text
		title="$title$arg";
		
		# Add the right side spaces
		while [[ "$i" -lt margin ]]
		do 
			i=$(($i + 1)); 
			title="$title ";
		done
		title="$title$borderCharacter";
		
		titles[j]=$title
		j+=1;
	done
		
	# Create the outer and inner boarders
	outerBorder=$borderCharacter;
	innerBorder=$borderCharacter;
	i=0;
	while [[ "$i" -lt innerLength ]]
	do 
		i=$(($i + 1)); 
		outerBorder="$outerBorder$borderCharacter";
		innerBorder="$innerBorder ";
	done
	outerBorder="$outerBorder$borderCharacter";
	innerBorder="$innerBorder$borderCharacter";
	
	# Print the title
	_print "$outerBorder" "$innerBorder" "${titles[@]}"
}

function _print(){
	outerBorder=$1;
	innerBorder=$2;
	shift 2;
	
	# Upper half
	echo '';
	echo '';
	echo "$outerBorder";
	echo "$innerBorder";
	
	# Middle text
	for var in "$@"
	do
		echo "$var"
	done
	
	#Bottom half
	echo "$innerBorder";
	echo "$outerBorder";
	echo '';
}

printTitle "$@"