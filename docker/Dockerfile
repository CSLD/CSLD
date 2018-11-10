FROM phusion/baseimage

MAINTAINER Jakub Balhar

ENV GIT_USER="${GIT_USER:-360acc8bab3c872b517216a51c1e34cdb40731ce}"
ENV JAVA_OPTS="${JAVA_OPTS:--server -Dfile.encoding=UTF-8 -Xms1024m -Xmx4096m -XX:PermSize=128m -XX:MaxPermSize=512m -XX:+UseConcMarkSweepGC -XX:NewSize=1G -XX:+UseParNewGC}"
ENV NVM_DIR=/usr/local/nvm
ENV NODE_VERSION=8.10.0
ENV NODE_PATH=$NVM_DIR/versions/node/v$NODE_VERSION/lib/node_modules
ENV PATH=$PATH:$NVM_DIR/versions/node/v$NODE_VERSION/bin
ENV GEOSERVER_DATA_DIR=/mnt/volume-panther/geoserver-data
ENV CATALINA_BASE=/var/lib/tomcat8
ENV CATALINA_HOME=/usr/share/tomcat8

# Add repositories.
RUN install_clean wget software-properties-common

## Postgresql repository.
RUN add-apt-repository "deb http://apt.postgresql.org/pub/repos/apt/ $(lsb_release -cs)-pgdg main" \
    && wget --quiet --output-document=- https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add -

# Upgrade the system and install common packages needed for this build.
RUN apt-get update --yes \
    && apt-get upgrade --yes \
    && install_clean bzip2 unzip zip patch git-core

# Postgresql with PostGIS.
RUN install_clean postgresql-10-postgis-2.4 postgresql-10-postgis-2.4-scripts

COPY ./pg_hba.patch /etc/postgresql/10/main/
RUN cd /etc/postgresql/10/main/ \
    && patch pg_hba.conf pg_hba.patch

# Apache.
RUN install_clean apache2 libapache2-mod-jk

COPY ./jk.conf /etc/apache2/mods-available/jk.conf
RUN a2enmod jk
COPY ./000-default.conf /etc/apache2/sites-available/000-default.conf
COPY ./workers.properties /etc/apache2/workers.properties
RUN a2enmod proxy_http \
    && a2enmod ssl \
    && a2enmod deflate \
    && a2enmod headers \
    && a2enmod cgi \
    && a2enmod rewrite


# Node.
RUN wget --quiet --output-document=- https://raw.githubusercontent.com/creationix/nvm/v0.33.5/install.sh | bash \
    && . $NVM_DIR/nvm.sh \
    && nvm install $NODE_VERSION \
    && nvm alias default $NODE_VERSION \
    && nvm use default

RUN npm install --unsafe-perm -g forever

# Tomcat
RUN install_clean openjdk-8-jdk tomcat8

COPY ./server.xml /var/lib/tomcat8/conf/server.xml

# Adminstrators add-ons.
COPY ./_* /usr/local/bin/
RUN chmod a+x /usr/local/bin/_*

RUN install_clean sudo gdal-bin nano vim net-tools

# Other.
RUN export TERM=xterm

ENTRYPOINT ["/sbin/my_init"]


