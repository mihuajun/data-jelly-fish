#!/bin/bash

echo "########### Loading data to Mongo DB ###########"
mongoimport -u root -p root --authenticationDatabase admin -c=topic -d=data-jelly-fish --file /tmp/data/topic.json
mongoimport -u root -p root --authenticationDatabase admin -c=consumer -d=data-jelly-fish --file /tmp/data/consumer.json