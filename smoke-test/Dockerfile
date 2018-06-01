FROM ruby:2.4.2

ADD Gemfile Gemfile

RUN bundle install

ADD . /smoke-test

WORKDIR /smoke-test

ENTRYPOINT ["bundle", "exec", "rspec"]
