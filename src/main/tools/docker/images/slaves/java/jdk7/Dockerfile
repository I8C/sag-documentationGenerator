# Data container for Jenkins
FROM i8c/jenkins-base-slave:1.0.0
MAINTAINER I8C

# add a simple script that can auto-detect the appropriate JAVA_HOME value
# based on whether the JDK or only the JRE is installed
# Source: https://github.com/docker-library/openjdk/blob/054cea7585e6c0e4e98d133378ea38061a2ae3ac/7-jdk/Dockerfile
RUN { \
		echo '#!/bin/sh'; \
		echo 'set -e'; \
		echo; \
		echo 'dirname "$(dirname "$(readlink -f "$(which javac || which java)")")"'; \
	} > /usr/local/bin/docker-java-home && \
    chmod +x /usr/local/bin/docker-java-home

ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64

# Install java 7
RUN apt-get install -y openjdk-7-jdk && \
	rm -rf /var/lib/apt/lists/* && \
	[ "$JAVA_HOME" = "$(docker-java-home)" ]