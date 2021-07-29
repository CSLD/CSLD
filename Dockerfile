FROM ubuntu:20.04

ENV GIT_USER="${GIT_USER:-360acc8bab3c872b517216a51c1e34cdb40731ce}"
ENV JAVA_OPTS="${JAVA_OPTS:--server -Dfile.encoding=UTF-8 -Dprops.path=/opt/csld/www/ROOT/WEB-INF/classes/ -Xms1024m -Xmx4096m -XX:PermSize=128m -XX:MaxPermSize=512m -XX:+UseConcMarkSweepGC -XX:NewSize=1G -XX:+UseParNewGC}"
ENV NODE_VERSION=12.16.1
ENV NODE_PATH=$NVM_DIR/versions/node/v$NODE_VERSION/lib/node_modules
ENV PATH=$PATH:$NVM_DIR/versions/node/v$NODE_VERSION/bin
ENV GEOSERVER_DATA_DIR=/mnt/volume-panther/geoserver-data
ENV CATALINA_BASE=/var/lib/tomcat7
ENV CATALINA_HOME=/usr/share/tomcat7
ENV DEBIAN_FRONTEND=noninteractive 
ENV DEBCONF_NONINTERACTIVE_SEEN=true

# Add repositories.
RUN apt-get update \
    && apt-get install -y wget software-properties-common

## Postgresql repository.
RUN wget --quiet --output-document=- https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - \
    && add-apt-repository "deb http://apt.postgresql.org/pub/repos/apt/ $(lsb_release -cs)-pgdg main" 

# Upgrade the system and install common packages needed for this build.
RUN apt-get update --yes \
    && apt-get upgrade --yes \
    && apt-get install -y bzip2 unzip zip patch git-core

# Postgresql with PostGIS.
RUN apt-get install -y postgresql-10 postgresql-10-postgis-2.4

COPY docker/pg_hba.patch /etc/postgresql/10/main/
RUN cd /etc/postgresql/10/main/ \
    && patch pg_hba.conf pg_hba.patch

# Apache.
RUN apt-get install -y apache2 libapache2-mod-jk

COPY docker/jk.conf /etc/apache2/mods-available/jk.conf
RUN a2enmod jk
COPY docker/000-default.conf /etc/apache2/sites-available/000-default.conf
COPY docker/workers.properties /etc/apache2/workers.properties
RUN a2enmod proxy_http \
    && a2enmod ssl \
    && a2enmod deflate \
    && a2enmod headers \
    && a2enmod cgi \
    && a2enmod rewrite


# Node.
RUN wget -qO- https://raw.githubusercontent.com/nvm-sh/nvm/v0.36.0/install.sh | bash \
    && export NVM_DIR="$HOME/.nvm" \
    && [ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  \
    && [ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"  \
    && nvm install v12.18.4 \
    && nvm use v12.18.4 \
    && npm install --unsafe-perm -g forever

# Tomcat
RUN apt-get install -y openjdk-14-jdk tomcat9 maven

COPY docker/server.xml /var/lib/tomcat9/conf/server.xml
COPY docker/setenv.sh /usr/share/tomcat9/bin/setenv.sh

# Adminstrators add-ons.
COPY ./docker/_* /usr/local/bin/
RUN chmod a+x /usr/local/bin/_*

RUN apt-get install -y sudo gdal-bin nano vim net-tools