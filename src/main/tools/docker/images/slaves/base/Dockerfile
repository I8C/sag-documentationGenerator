# Data container for Jenkins
FROM debian:jessie
MAINTAINER I8C

# Install the remaining dependancies
RUN apt-get update -y && \
	apt-get install -y \
		git \
		wget \
		ca-certificates \
		curl \
		openssh-server \
		sudo \
		nano && \
    apt-get clean all

# Set SSH Configuration to allow remote logins without /proc write access
RUN sed -ri 's/^session\s+required\s+pam_loginuid.so$/session optional pam_loginuid.so/' /etc/pam.d/sshd

# Set the password of user root
ARG user=root
ARG password=wQua3cdgBXWrx8b4gaNxkT9q
RUN echo "${user}:${password}" | chpasswd

# Allow root login
RUN sed -i 's/^PermitRootLogin.*/PermitRootLogin yes/' /etc/ssh/sshd_config

# Set Name Servers
COPY /files/resolv.conf /etc/resolv.conf

# Create the directory for the SSHD
RUN mkdir /var/run/sshd

# Expose SSH port and run SSHD
EXPOSE 22
CMD ["/usr/sbin/sshd","-D"]