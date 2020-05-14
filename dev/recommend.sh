#!/bin/bash

curl -X POST 'http://localhost:9000/recommend?movieRoomId='''$1'' \
-H 'content-type: application/x-www-form-urlencoded; charset=utf-8'