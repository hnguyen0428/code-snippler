#!/bin/bash

if [ -z $1 ]; then
	echo 'Usage: "./dockerrun.sh <docker run flags> <image id>"'
	exit 1
fi

docker run \
	-e ADMIN_KEY_ENC="${ADMIN_KEY_ENC}" \
	-e CLIENT_KEY_ENC="${CLIENT_KEY_ENC}" \
	-e MONGODB_DATABASE="${MONGODB_DATABASE}" \
	-e MONGODB_URI="${MONGODB_URI}" \
	-e MONGODB_USERNAME="${MONGODB_USERNAME}" \
	-e MONGODB_PASSWORD="${MONGODB_PASSWORD}" \
	"$@"
