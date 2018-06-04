require 'capybara/rspec'
require 'selenium-webdriver'

if ENV['HEADLESS'] == 'true'
  require 'headless'
  headless = Headless.new
  headless.start
  at_exit do
    exit_status = $!.status if $!.is_a?(SystemExit)
    headless.destroy
    exit exit_status if exit_status
  end
end

if ENV['DOCKER'] == 'true'
  # To use docker, run
  # $ export DOCKER=true && docker run -d -p 4444:4444 selenium/standalone-firefox
  Capybara.javascript_driver = :selenium_remote_firefox
  Capybara.register_driver "selenium_remote_firefox".to_sym do |app|
    Capybara::Selenium::Driver.new(app, browser: :remote, url: "http://localhost:4444/wd/hub", desired_capabilities: :firefox)
  end
else
  Capybara.register_driver :selenium do |app|
    profile = Selenium::WebDriver::Firefox::Profile.new
    # Ensure that the tests fail if they encounter any invalid or self signed certificates:
    profile.secure_ssl = true
    Selenium::WebDriver::Firefox::Binary.path = ENV['FIREFOX_PATH'] || Selenium::WebDriver::Firefox::Binary.path
    Capybara::Selenium::Driver.new(app, :browser => :firefox, profile: profile)
  end
end

Capybara.default_max_wait_time = 15
Capybara.ignore_hidden_elements = false
