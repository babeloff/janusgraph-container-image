
ARG CREATED=test
ARG REVISION=latest

FROM openjdk:8-jre-slim-buster as builder

ARG JG_VERSION=0.6.0
ARG YQ_VERSION=4.13.2

ENV JG_VERSION=${JG_VERSION}
ENV JG_HOME=/opt/janusgraph

WORKDIR /opt

ARG DEBIAN_FRONTEND=noninteractive
# Prepare to get components
RUN /usr/bin/apt-get update -y
RUN /usr/bin/apt-get install -y gpg unzip curl dos2unix
# Get YQ
RUN /usr/bin/curl -fSL https://github.com/mikefarah/yq/releases/download/v${YQ_VERSION}/yq_linux_amd64 -o yq
RUN /bin/chmod 755 /opt/yq
# Get Props2Yaml
RUN /usr/bin/curl -fSL https://repo1.maven.org/maven2/io/codearte/props2yaml/props2yaml/0.5/props2yaml-0.5-jar-with-dependencies.jar -o props2yaml.jar

# Get Janusgraph
RUN /usr/bin/curl -fSL https://github.com/JanusGraph/janusgraph/releases/download/v${JG_VERSION}/janusgraph-${JG_VERSION}.zip -o janusgraph.zip
RUN /usr/bin/curl -fSL https://github.com/JanusGraph/janusgraph/releases/download/v${JG_VERSION}/janusgraph-${JG_VERSION}.zip.asc -o janusgraph.zip.asc
RUN /usr/bin/curl -fSL https://github.com/JanusGraph/janusgraph/releases/download/v${JG_VERSION}/KEYS -o KEYS
RUN /usr/bin/gpg --import KEYS
RUN /usr/bin/gpg --batch --verify janusgraph.zip.asc janusgraph.zip
RUN /usr/bin/unzip janusgraph.zip
RUN /bin/mv janusgraph-${JG_VERSION} ${JG_HOME}

# Clean up Janusgraph
RUN /bin/rm -rf ${JG_HOME}/elasticsearch
RUN /bin/rm -rf ${JG_HOME}/javadocs
RUN /bin/rm -rf ${JG_HOME}/log
RUN /bin/rm -rf ${JG_HOME}/examples
RUN /bin/rm -rf ${JANUS_HOME}/conf/janusgraph-*.properties
RUN /bin/rm -rf ${JANUS_HOME}/conf/gremlin-server

# Clean up Miscellaneous
RUN /usr/bin/apt-get --purge remove -y gpg unzip curl

COPY conf/ ${JG_HOME}/conf/
COPY scripts/remote-connect.gremlin ${JG_HOME}/scripts/

RUN find ${JG_HOME} -name '*.yaml' -print0  \
  | xargs --null sh -c 'for arg; do /opt/yq eval --prettyPrint --inplace "${arg}"; done'

RUN find ${JG_HOME} -name 'janusgraph-*.properties' -print0  \
  | xargs --null sh -c 'for arg; do java -jar /opt/props2yaml.jar "$arg" > "${arg%.properties}.yaml"; /bin/rm "$arg"; done'

# Next build stage
FROM openjdk:8-jre-slim-buster as image

ARG CREATED=test
ARG REVISION=test

ENV JG_VERSION=${JG_VERSION}
ENV JG_HOME=/opt/janusgraph
ENV JG_CONFIG_DIR=/etc/opt/janusgraph
ENV JG_DATA_DIR=/var/lib/janusgraph
ENV JG_SVC_TIMEOUT=30
ENV JG_STORAGE_TIMEOUT=60
ENV JG_SVC_TEMPLATE=janusgraph-server
ENV JG_GRAPH_TEMPLATE=janusgraph-cql-es-graph
ENV JG_INIT_DB_DIR=/docker-entrypoint-init-db.d
ENV JG_SVC__00graphProperties='.graphs.graph = "/etc/opt/janusgraph/janusgraph.properties"'
ENV JG_SVC__00threadPoolWorker='.threadPoolWorker = 1'
ENV JG_SVC__00gremlinPool='.gremlinPool = 8'

RUN /usr/sbin/groupadd -r janusgraph --gid=999
RUN /usr/sbin/useradd -r -g janusgraph --uid=999 -d ${JG_DATA_DIR} janusgraph
RUN /usr/bin/apt-get update -y
RUN DEBIAN_FRONTEND=noninteractive /usr/bin/apt-get install -y --no-install-recommends krb5-user
RUN DEBIAN_FRONTEND=noninteractive /usr/bin/apt-get install -y dos2unix
RUN /bin/rm -rf /var/lib/apt/lists/*

COPY --from=builder /opt/janusgraph/ /opt/janusgraph/
COPY --from=builder /opt/yq /usr/bin/yq
#COPY --from=builder /opt/props2yaml.jar /usr/bin/props2yaml.jar
COPY scripts/docker-entrypoint.sh /usr/local/bin/
COPY scripts/load-init-db.sh /usr/local/bin/

RUN /usr/bin/dos2unix /usr/local/bin/docker-entrypoint.sh
RUN /usr/bin/dos2unix /usr/local/bin/load-init-db.sh

RUN /bin/chmod 755 /usr/local/bin/docker-entrypoint.sh
RUN /bin/chmod 755 /usr/local/bin/load-init-db.sh
RUN /bin/chmod 755 /usr/bin/yq
RUN /bin/mkdir -p ${JG_INIT_DB_DIR} ${JG_CONFIG_DIR} ${JG_DATA_DIR}
RUN /bin/chown -R janusgraph:janusgraph ${JG_HOME} ${JG_INIT_DB_DIR} ${JG_CONFIG_DIR} ${JG_DATA_DIR}

# Clean up
RUN /usr/bin/apt-get --purge remove -y dos2unix
RUN /bin/rm -rf /var/lib/apt/lists/*

EXPOSE 8182

WORKDIR ${JG_HOME}
USER janusgraph

ENTRYPOINT [ "/bin/bash", "/usr/local/bin/docker-entrypoint.sh" ]
CMD [ "janusgraph" ]

LABEL org.opencontainers.image.title="JanusGraph Docker Image" \
      org.opencontainers.image.description="UnOfficial JanusGraph Docker image" \
      org.opencontainers.image.url="https://janusgraph.org/" \
      org.opencontainers.image.documentation="https://docs.janusgraph.org/v0.6/" \
      org.opencontainers.image.revision="${REVISION}" \
      org.opencontainers.image.source="https://github.com/JanusGraph/janusgraph-docker/" \
      org.opencontainers.image.vendor="JanusGraph" \
      org.opencontainers.image.version="${JG_VERSION}" \
      org.opencontainers.image.created="${CREATED}" \
      org.opencontainers.image.license="Apache-2.0"