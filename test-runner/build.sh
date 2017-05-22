#!/usr/bin/env bash
set -e
set -o xtrace

docker build -t aidbox/zero-ci-base:0.0.1 .
docker push aidbox/zero-ci-base:0.0.1
