#!/bin/bash

set -e

echo "== stop finlink compose =="

docker compose \
  -p finlink \
  -f ./app/docker-compose-mid.yml \
  down -v --rmi all --remove-orphans

echo "== clean system cache =="

docker system prune -a -f --volumes

echo "== DONE =="