FROM gradle:6.5.1-jdk11 as build

WORKDIR /stub-idp
USER root
ENV GRADLE_USER_HOME ~/.gradle

COPY build.gradle build.gradle
COPY stub-idp/build.gradle stub-idp/build.gradle
COPY stub-idp-saml/build.gradle stub-idp-saml/build.gradle
COPY shared.gradle shared.gradle
COPY settings.gradle settings.gradle
COPY inttest.gradle inttest.gradle
# There is an issue running idea.gradle in the container
# So just make this an empty file
RUN touch idea.gradle
RUN gradle :stub-idp:install


COPY stub-idp/src stub-idp/src
COPY stub-idp-saml/src stub-idp-saml/src
COPY configuration/ configuration/

RUN gradle :stub-idp:installDist

ENTRYPOINT ["gradle"]
CMD ["tasks"]

FROM openjdk:11.0.6-jre

WORKDIR /stub-idp

COPY configuration/local/stub-idp.yml stub-idp.yml
COPY --from=build /stub-idp/stub-idp/build/install/stub-idp .

CMD bin/stub-idp server stub-idp.yml
