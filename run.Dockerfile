FROM govukverify/java8

WORKDIR /app

ADD configuration/local/stub-idp.yml stub-idp.yml
ADD build/distributions/ida-stub-idp-local.zip ida-stub-idp.zip

RUN unzip ida-stub-idp.zip

CMD ida-stub-idp-local/bin/ida-stub-idp server stub-idp.yml 
