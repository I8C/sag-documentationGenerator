FROM jenkins
MAINTAINER I8C

# Install default plugins
COPY plugins.txt /tmp/plugins.txt
RUN /usr/local/bin/plugins.sh /tmp/plugins.txt

# Create a log folder
USER root
RUN mkdir /var/log/jenkins
RUN mkdir /var/cache/jenkins
RUN chown -R  jenkins:jenkins /var/log/jenkins
RUN chown -R  jenkins:jenkins /var/cache/jenkins
USER jenkins

# Environment
ENV JAVA_OPTS="-Xmx2048m"
# Set the log location and where the jenkins war file is stored
# We store the war in a non-persistent location for easier transition of jenkins versions
ENV JENKINS_OPTS="--logfile=/var/log/jenkins/jenkins.log --webroot=/var/cache/jenkins/war"
