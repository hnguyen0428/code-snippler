#!/bin/bash

if [ -z $1 ]; then
	echo 'Usage: "./dockerbuild.sh <image tag>"'
	exit 1
fi

docker build -t $1 \
	--build-arg ADMIN_KEY_ENC="${ADMIN_KEY_ENC}" \
	--build-arg CLIENT_KEY_ENC="${CLIENT_KEY_ENC}" \
	--build-arg MONGODB_DATABASE="${MONGODB_DATABASE}" \
	--build-arg MONGODB_URI="${MONGODB_URI}" \
	--build-arg MONGODB_USERNAME="${MONGODB_USERNAME}" \
	--build-arg MONGODB_PASSWORD="${MONGODB_PASSWORD}" .
