#!/bin/bash

curl -X GET 'http://localhost:9000/reservations?movieRoomId='''$1'' \
-H 'content-type: application/x-www-form-urlencoded; charset=utf-8' | json_pp