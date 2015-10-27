# CassandraCurator
A tool for helping manage your Cassandra cluster.

## Building
mvn clean install

## Pre-Reqs (for testing)
* Install Docker
* Add this line to your /etc/default/docker file:
DOCKER_OPTS="-H tcp://127.0.0.1:2375 -H unix:///var/run/docker.sock"
* Restart Docker
* Ensure you can run this: docker -H tcp://127.0.0.1:2375 version

(Apologies for poor formatting; will clean up later.)
