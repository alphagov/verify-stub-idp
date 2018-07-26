FROM govukverify/java8

WORKDIR /app

ADD configuration/stub-idp.yml stub-idp.yml
ADD build/distributions/verify-stub-idp-local.zip verify-stub-idp.zip

RUN unzip verify-stub-idp.zip

CMD verify-stub-idp/bin/verify-stub-idp server stub-idp.yml 
