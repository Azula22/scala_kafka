#!/bin/bash


#create database with name test
export PGPASSWORD='postgres' &&
(psql -h 127.0.0.1 -p 5432 -U postgres -c "SELECT 1 FROM pg_database WHERE datname = 'test'" | grep -q 1 ||
 psql -h 127.0.0.1 -p 5432 -U postgres -c "CREATE DATABASE test") &&
export PGPASSWORD=''

#run tests
sbt -Dconfig.file=./tests/src/main/resources/application.conf tests/test
