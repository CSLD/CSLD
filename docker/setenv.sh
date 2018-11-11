#!/bin/sh

JAVA_OPTS="-Dfile.encoding=UTF-8 -Dprops.path=/opt/csld/www/ROOT/WEB-INF/classes -Xms1024m -Xmx4096m -XX:PermSize=128m -XX:MaxPermSize=512m -XX:+UseConcMarkSweepGC -XX:NewSize=1G -XX:+UseParNewGC"