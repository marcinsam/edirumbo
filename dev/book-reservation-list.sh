#!/bin/bash

curl -X POST \
  http://localhost:9000/reservations-group \
  -H 'content-type: application/json; charset=UTF-8' \
  --data '[{"movieRoomId":1,"roomRowSeatId":13,"name":"Boguś","surname":"Linda","ticketPriceId":2},{"movieRoomId":1,"roomRowSeatId":14,"name":"Mela","surname":"Chomacka","ticketPriceId":3}]'