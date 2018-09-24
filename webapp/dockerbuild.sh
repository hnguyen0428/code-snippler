#!/bin/bash

if [ -z $1 ]; then
	echo 'Usage: "./dockerbuild.sh <image tag>"'
	exit 1
fi

docker build -t $1 \
	--build-arg CODE_SNIPPLER_BASEURL="${CODE_SNIPPLER_BASEURL}" \
	--build-arg CLIENT_KEY="${CLIENT_KEY}" .
