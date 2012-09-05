
MAVEN SETTINGS 
==============
Basic settings:
http://powerplant.nature.com/wiki/display/SFD/Starting+a+web+project+in+NPG+environment

RUN IT BY USING THE CAMEL MAVEN PLUGIN
======================================
jpa-tutorial $ mvn clean camel:run
url: http://localhost:1234/myapp/person/1

RUN IT BY USING SERVICEMIX
==========================
jpa-tutorial $ mvn clean install
[start serviceMix] /usr/local/apache-servicemix-4.3.0 $ sudo ./bin/servicemix
[install bundler] karaf@root> install -s file:/Users/a.zhou/.m2/repository/org/meri/org.meri.jpa.tutorial/0.0.1-SNAPSHOT/org.meri.jpa.tutorial-0.0.1-SNAPSHOT.jar
OR [update bundler] karaf@root> update 251 file:/Users/a.zhou/.m2/repository/org/meri/org.meri.jpa.tutorial/0.0.1-SNAPSHOT/org.meri.jpa.tutorial-0.0.1-SNAPSHOT.jar
url: http://localhost:1234/myapp/person/1
[stop serviceMix from console] karaf@root> shutdown
[emptying the component cache] apache-servicemix-4.3.0 $ rm -rf data/cache/*

REFERENCE
=========
http://meri-stuff.blogspot.com/2012/03/jpa-tutorial.html