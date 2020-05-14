#!/bin/bash

#curl -X GET 'http://localhost:9000/movies?after=2019-01-09T11:35:00&before=2020-12-10T11:35:00' \
#-H 'content-type: application/x-www-form-urlencoded; charset=utf-8'
curl -X GET 'http://localhost:9000/movies?after='''$1'''&before='''$2'' \
-H 'content-type: application/x-www-form-urlencoded; charset=utf-8' \
| json_pp