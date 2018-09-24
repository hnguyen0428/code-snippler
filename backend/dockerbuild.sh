#!/bin/bash

if [ -z $1 ]; then
	TAG=codesnippler_backend
else
	TAG=$1
fi


docker build -t $TAG \
	--build-arg ADMIN_KEY_ENC="${ADMIN_KEY_ENC}" \
	--build-arg CLIENT_KEY_ENC="${CLIENT_KEY_ENC}" \
	--build-arg MONGODB_DATABASE="${MONGODB_DATABASE}" \
	--build-arg MONGODB_URI="${MONGODB_URI}" \
	--build-arg MONGODB_USERNAME="${MONGODB_USERNAME}" \
	--build-arg MONGODB_PASSWORD="${MONGODB_PASSWORD}" .
