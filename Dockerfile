FROM java:8

MAINTAINER Rodrigo Gunisalvo Leite

RUN mkdir -p /home/eircode/conf && \
groupadd -r eircode -g 710 && \
useradd -u 710 -r -g eircode -d /home/eircode -s /sbin/nologin -c "Eircode app user" eircode && \
chown -R eircode:eircode /home/eircode

WORKDIR /home/eircode/

USER eircode

ADD target/eircode-1.1.1.jar eircode.jar

ADD src/main/resources/application-alliescomputing.properties conf/application.properties

CMD java -jar eircode.jar --spring.config.location=file:conf/application.properties --spring.profiles.active=alliescomputing

