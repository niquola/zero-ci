## CI using Kubernetes and Google Cloud 


```sh
# run in cluster
helm install ci/test-runner/test-runner --name run-2

# run local
docker run --rm \ 
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v ~/.kube:/root/.kube \
  -e REPO=<your-repo> \
  -e DOCKER_KEY="$(cat <service_account_json_key.json>)" \
  -it zero-runner
```
