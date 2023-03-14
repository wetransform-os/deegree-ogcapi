FROM eclipse-temurin:11 as builder
# Multi stage build - https://docs.docker.com/engine/userguide/eng-image/multistage-build/

# install maven and unzip
RUN apt-get update && apt-get install -y --no-install-recommends maven unzip

# prepare folders
RUN mkdir /build && mkdir /target
COPY ./ /build/

# build and extract deegree
RUN cd /build/ && \
  mvn clean install -B -DskipTests && \
  cp /build/deegree-ogcapi-webapp/deegree-ogcapi-webapp-postgres/target/deegree-ogcapi-webapp-postgres-*.war /build/deegree-ogcapi.war && \
  unzip -o /build/deegree-ogcapi.war -d /target

# add to image...
FROM tomcat:9.0-jdk11-temurin-jammy

ENV LANG en_US.UTF-8

# add build info - see hooks/build and https://github.com/opencontainers/image-spec/blob/master/annotations.md
ARG BUILD_DATE
ARG VCS_REF
ARG VCS_URL
LABEL org.opencontainers.image.created=$BUILD_DATE \
  org.opencontainers.image.source=$VCS_URL \
  org.opencontainers.image.revision=$VCS_REF

# tomcat port
EXPOSE 8080

# folder for workspace root
ENV DEEGREE_WORKSPACE_ROOT=/workspaces

# get dataset list as health check
HEALTHCHECK \
    --interval=60s \
    --timeout=15s \
    --start-period=2m \
    --retries=3 \
    CMD curl --fail http://localhost:8080/datasets || exit 1

RUN mkdir $DEEGREE_WORKSPACE_ROOT && \
  (rm -r /usr/local/tomcat/webapps/ROOT || true)

COPY --from=builder /target /usr/local/tomcat/webapps/ROOT

VOLUME $DEEGREE_WORKSPACE_ROOT
