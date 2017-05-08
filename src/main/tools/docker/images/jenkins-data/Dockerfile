# Data container for Jenkins
FROM debian:jessie
MAINTAINER I8C

# Create the same user as the Cloudbees Jenkins image
ARG user=jenkins
ARG group=jenkins
ARG uid=1000
ARG gid=1000

RUN groupadd -g ${gid} ${group} \
	&& useradd -d "/var/jenkins_home" -u ${uid} -g ${gid} -m -s /bin/bash ${user}

# Create the same log dir as our jenkins-master
RUN mkdir -p /var/log/jenkins
RUN chown -R jenkins:jenkins /var/log/jenkins

# Add a volume for the log dir and the jenkins home
VOLUME ["/var/log/jenkins", "/var/jenkins_home"]

# Switch to the jenkins user
USER jenkins