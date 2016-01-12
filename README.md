# CassandraCurator
A tool for helping manage your Cassandra cluster.

##
Status: In development.

## Building
mvn clean install

## Pre-Reqs for testing
* A fairly powerful computer; the tests are extremely expensive.
* Install Docker
* Add this line to your /etc/default/docker file:
DOCKER_OPTS="-H tcp://127.0.0.1:2375 -H unix:///var/run/docker.sock"
* Restart Docker
* Ensure you can run this: docker -H tcp://127.0.0.1:2375 version
* Build the docker test boxes: 
```
cd /src/test/resources/docker
./buildCassandraDockerInstances.sh
```

If something gets weird (memory, lag, etc), try restarting your docker service.

(Apologies for poor formatting; will clean up later.)
