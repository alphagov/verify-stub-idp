FROM ghcr.io/alphagov/verify/ruby:2.6.6

ADD Gemfile Gemfile

RUN bundle install

ADD . /smoke-test

WORKDIR /smoke-test

ENTRYPOINT ["bundle", "exec", "rspec"]
