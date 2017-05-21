FROM docker

RUN apk add --no-cache curl ca-certificates bash sudo

RUN curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get | bash
RUN curl https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl > /usr/local/bin/kubectl

ENV HOME=/config

RUN set -x && \
    chmod +x /usr/local/bin/kubectl && \
    kubectl version --client


RUN apk add --no-cache git
ADD entrypoint /entrypoint
RUN chmod u+x /entrypoint

ENV DOCKER_API_VERSION 1.23
ENTRYPOINT ["/entrypoint"]