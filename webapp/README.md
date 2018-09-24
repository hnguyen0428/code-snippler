# CodeSnippler Webapp

The user interface of code snippler.


## Configurations

The application uses Code Snippler's backend, so the base url and client 
key must be available in the environment variables.

CLIENT_KEY: Used to authenticate users for other types of requests 
other than GET.

CODE_SNIPPLER_BASEURL: The code snippler's backend url.


## Docker

Dockerfile and docker run and build scripts are provided in the repo for
containerizing this application. The run and build scripts are provided
in order to propagate environment variables into the Docker build and run
environment.

To build the container, be in the same directory as the dockerbuild.sh 
script and run:

    ./dockerbuild.sh <image tag>
    
To run the container, be in the same directory as the dockerrun.sh script
and run:

    ./dockerrun.sh -p <port to be run on>:5000 <other docker flags> <image id>

