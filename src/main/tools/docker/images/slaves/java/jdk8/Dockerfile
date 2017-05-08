# Data container for Jenkins
FROM i8c/jenkins-base-slave:1.0.0
MAINTAINER I8C

# Install java 8
RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" > /etc/apt/sources.list.d/java-8-debian.list && \
	apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886 && \
	apt-get update && \
	apt-get install -y debconf-utils && \
	echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | debconf-set-selections && \
	apt-get install -y oracle-java8-installer && \
	apt-get remove -y debconf-utils