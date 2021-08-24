
ARG CREATED=test
ARG REVISION=latest

FROM debian:buster-slim as builder

ARG JANUS_VERSION=0.5.3
ARG YQ_VERSION=4.9.3
ENV JANUS_VERSION=${JANUS_VERSION}
ENV JANUS_HOME=/opt/janusgraph

WORKDIR /opt

ARG DEBIAN_FRONTEND=noninteractive
RUN /usr/bin/apt-get update -y
RUN /usr/bin/apt-get install -y gpg unzip curl dos2unix
RUN /usr/bin/curl -fSL https://github.com/mikefarah/yq/releases/download/v${YQ_VERSION}/yq_linux_amd64 -o yq
RUN /usr/bin/curl -fSL https://repo1.maven.org/maven2/io/codearte/props2yaml/props2yaml/0.5/props2yaml-0.5-jar-with-dependencies.jar -o props2yaml.jar
RUN /usr/bin/curl -fSL https://github.com/JanusGraph/janusgraph/releases/download/v${JANUS_VERSION}/janusgraph-${JANUS_VERSION}.zip -o janusgraph.zip
RUN /usr/bin/curl -fSL https://github.com/JanusGraph/janusgraph/releases/download/v${JANUS_VERSION}/janusgraph-${JANUS_VERSION}.zip.asc -o janusgraph.zip.asc
RUN /usr/bin/curl -fSL https://github.com/JanusGraph/janusgraph/releases/download/v${JANUS_VERSION}/KEYS -o KEYS
RUN /usr/bin/gpg --import KEYS
RUN /usr/bin/gpg --batch --verify janusgraph.zip.asc janusgraph.zip
RUN /usr/bin/unzip janusgraph.zip
RUN /bin/mv janusgraph-${JANUS_VERSION} ${JANUS_HOME}
RUN find ${JANUS_HOME} -name 'janusgraph-*.properties' -z \
    | xargs --null sh -c 'for arg; do java props2yaml.jar "$arg" > "${arg%.properties}.yaml"; done'

# Clean up
RUN /bin/rm -rf ${JANUS_HOME}/elasticsearch
RUN /bin/rm -rf ${JANUS_HOME}/javadocs
RUN /bin/rm -rf ${JANUS_HOME}/log
RUN /bin/rm -rf ${JANUS_HOME}/examples
RUN /usr/bin/apt-get --purge remove -y gpg unzip curl
RUN /bin/rm -rf /var/lib/apt/lists/*

COPY conf ${JANUS_HOME}/conf/custom-server/
COPY scripts/remote-connect.gremlin ${JANUS_HOME}/scripts/

# Next build stage
FROM openjdk:8-jre-slim-buster as image
ARG CREATED
ARG REVISION

ENV JANUS_VERSION=${JANUS_VERSION}
ENV JANUS_HOME=/opt/janusgraph
ENV JANUS_CONFIG_DIR=/etc/opt/janusgraph
ENV JANUS_DATA_DIR=/var/lib/janusgraph
ENV JANUS_SERVER_TIMEOUT=30
ENV JANUS_STORAGE_TIMEOUT=60
ENV JANUS_PROPS_TEMPLATE=cql-es
ENV JANUS_INIT_DB_DIR=/docker-entrypoint-init-db.d
ENV GREMLIN__00graphProperties='.graphs.graph = "/etc/opt/janusgraph/janusgraph.properties"'
ENV GREMLIN__00threadPoolWorker='.threadPoolWorker = 1'
ENV GREMLIN__00gremlinPool='.gremlinPool = 8'

RUN /usr/sbin/groupadd -r janusgraph --gid=999
RUN /usr/sbin/useradd -r -g janusgraph --uid=999 -d ${JANUS_DATA_DIR} janusgraph
RUN /usr/bin/apt-get update -y
RUN /usr/bin/apt-get install -y --no-install-recommends krb5-user
RUN /usr/bin/apt-get install -y dos2unix

COPY --from=builder /opt/janusgraph/ /opt/janusgraph/
COPY --from=builder /opt/yq /usr/bin/yq
COPY scripts/docker-entrypoint.sh /usr/local/bin/
COPY scripts/load-init-db.sh /usr/local/bin/

RUN /usr/bin/dos2unix /usr/local/bin/docker-entrypoint.sh
RUN /bin/chmod 755 /usr/local/bin/load-init-db.sh
RUN /usr/bin/dos2unix /usr/local/bin/load-init-db.sh
RUN /bin/chmod 755 /usr/bin/yq
RUN /bin/mkdir -p ${JANUS_INIT_DB_DIR} ${JANUS_CONFIG_DIR} ${JANUS_DATA_DIR}
RUN /bin/chown -R janusgraph:janusgraph ${JANUS_HOME} ${JANUS_INIT_DB_DIR} ${JANUS_CONFIG_DIR} ${JANUS_DATA_DIR}

# Clean up
RUN /usr/bin/apt-get --purge remove -y dos2unix
RUN /bin/rm -rf /var/lib/apt/lists/*

EXPOSE 8182

WORKDIR ${JANUS_HOME}
USER janusgraph

ENTRYPOINT [ "/bin/bash", "/usr/local/bin/docker-entrypoint.sh" ]
CMD [ "janusgraph" ]

LABEL org.opencontainers.image.title="JanusGraph Docker Image" \
      org.opencontainers.image.description="UnOfficial JanusGraph Docker image" \
      org.opencontainers.image.url="https://janusgraph.org/" \
      org.opencontainers.image.documentation="https://docs.janusgraph.org/v0.5/" \
      org.opencontainers.image.revision="${REVISION}" \
      org.opencontainers.image.source="https://github.com/JanusGraph/janusgraph-docker/" \
      org.opencontainers.image.vendor="JanusGraph" \
      org.opencontainers.image.version="${JANUS_VERSION}" \
      org.opencontainers.image.created="${CREATED}" \
      org.opencontainers.image.license="Apache-2.0"