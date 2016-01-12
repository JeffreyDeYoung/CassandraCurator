#!/bin/bash

#Author: Jeffrey DeYoung
#Create Date: 11 Jan 16
#Purpose: Testing helper; should only have to be run when the docker files change:
#Run docker build on all of our cassandra docker files in order to prep them for use;
#needs to be run from the folder containing the docker files.

files=`ls | egrep "\<cassandra" | grep -v "~"`
for i in $files
 do
    docker build -f $i -t $i .
 done
