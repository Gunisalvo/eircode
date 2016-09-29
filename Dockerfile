FROM java:8

MAINTAINER Rodrigo Gunisalvo Leite

RUN mkdir -p /home/eircode/config && \
groupadd -r eircode -g 710 && \
useradd -u 710 -r -g eircode -d /home/eircode -s /sbin/nologin -c "Eircode app user" eircode && \
chown -R eircode:eircode /home/eircode

WORKDIR /home/eircode/

USER eircode

ADD target/eircode-1.1.3.jar eircode.jar

EXPOSE 8080

CMD java -jar eircode.jar --spring.config.location=file:config/application-alliescomputing.properties --spring.profiles.active=alliescomputing

