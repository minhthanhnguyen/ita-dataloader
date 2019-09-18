FROM openjdk

COPY ["susa-stats-dataloader-0.0.1-SNAPSHOT.jar", "/temp/"]

EXPOSE 80

CMD java -jar /temp/susa-stats-dataloader-0.0.1-SNAPSHOT.jar --spring.profiles.active=staging --server.port=80