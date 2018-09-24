#!/bin/bash

if [ -z $1 ]; then
	echo 'Usage: "./dockerrun.sh <docker run flags> <image id>"'
	exit 1
fi

echo $CODE_SNIPPLER_BASEURL

docker run \
	-e CODE_SNIPPLER_BASEURL="${CODE_SNIPPLER_BASEURL}" \
	-e CLIENT_KEY="${CLIENT_KEY}" \
	"$@"
