#!/bin/bash

#chcp.com 65001

#curl -X POST \
#  http://localhost:9000/movie \
#  -H 'cache-control: no-cache' \
#  -H 'content-type: application/x-www-form-urlencoded' \
#  -d title=Jest%20k%C5%82%C3%B3dka3

#curl -X POST \
#  http://localhost:9000/movie \
#  -H 'cache-control: no-cache' \
#  -H 'content-type: application/x-www-form-urlencoded' \
#  --data-urlencode "title=Kłódeczka kochana 8"

curl -X POST \
  http://localhost:9000/movie \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/x-www-form-urlencoded; charset=ISO-8859-1' \
  --data-ascii "title=Kłódeczka kochana 9" \
  --data-ascii "screeningDateTime=2019-01-30 11:35:00"
