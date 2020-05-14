#!/bin/bash

curl -X POST \
  http://localhost:9000/reservations \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/x-www-form-urlencoded; charset=ISO-8859-1' \
  --data-ascii "movieRoomId=$1" \
  --data-ascii "roomRowSeatId=$2" \
  --data-ascii "name=$3" \
  --data-ascii "surname=$4" \
  --data-ascii "ticketPriceId=$5" \
