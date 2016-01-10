#!/bin/bash

ip=`ip route get 8.8.8.8 | awk '{print $NF; exit}'`
echo "Setting Cassandra listen addresses to: $ip on `date`" >> /var/log/cassandra_setup.log
sed -i "s/localhost/$ip/g" /etc/cassandra/cassandra.yaml
sed -i "s/127.0.0.1/$ip/g" /etc/cassandra/cassandra.yaml
