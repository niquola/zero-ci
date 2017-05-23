#!/usr/bin/env bash
set -e
set -o xtrace

lein uberjar
docker build -t aidbox/zero-ci-hook:0.0.4 .
docker push aidbox/zero-ci-hook:0.0.4
