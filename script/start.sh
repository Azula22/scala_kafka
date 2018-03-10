#!/bin/bash

set -e
set -x

PROJECT_DIR=$1
MODULE_NAME=$2
VERSION=$3

zookeeper:2554 -t 30 -- echo "Zookeeper started"
kafka:9092 -t 30 -- echo "Kafka started"

java -jar $PROJECT_DIR/"$MODULE_NAME"-assembly-"$VERSION".jar;
