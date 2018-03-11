#!/bin/bash

set -e  # exit immediately if a command exits with a non-zero status
set -x  # print all executed commands on terminal

PROJECT_DIR=$1
MODULE_NAME=$2
VERSION=$3

java -jar $PROJECT_DIR/"$MODULE_NAME"-assembly-"$VERSION".jar;
