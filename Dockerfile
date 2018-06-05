FROM gradle:4.7.0-jdk8 as build

WORKDIR /stub-idp
USER root
ENV GRADLE_USER_HOME ~/.gradle

COPY build.gradle build.gradle
# There is an issue running idea.gradle in the container
# So just make this an empty file
RUN touch idea.gradle
RUN gradle install

COPY src/ src/
COPY configuration/ configuration/

RUN gradle installDist

ENTRYPOINT ["gradle"]
CMD ["tasks"]

FROM openjdk:8-jre

WORKDIR /stub-idp

COPY configuration/stub-idp.yml stub-idp.yml
COPY --from=build /stub-idp/build/install/stub-idp .

CMD bin/stub-idp server stub-idp.yml
