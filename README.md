# CassandraCurator
A tool for helping manage your Cassandra cluster.

##
Status: In development.

## Building
mvn clean install

## Pre-Reqs (for testing)
* Install Docker
* Add this line to your /etc/default/docker file:
DOCKER_OPTS="-H tcp://127.0.0.1:2375 -H unix:///var/run/docker.sock"
* Restart Docker
* Ensure you can run this: docker -H tcp://127.0.0.1:2375 version
* Build the docker test box: 
```
docker build -f ./src/test/resources/docker/cassandra2.1.0 -t cassandra2.1.0 . 
```

If something gets weird, try restarting your docker service.

(Apologies for poor formatting; will clean up later.)
