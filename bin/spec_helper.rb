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

# Ensure that the tests fail if they encounter any invalid or self signed certificates:
Capybara.register_driver :selenium do |app|
  profile = Selenium::WebDriver::Firefox::Profile.new
  profile.secure_ssl = true
  Selenium::WebDriver::Firefox::Binary.path = ENV['FIREFOX_PATH'] || Selenium::WebDriver::Firefox::Binary.path
  Capybara::Selenium::Driver.new(app, :browser => :firefox, profile: profile)
end

Capybara.default_max_wait_time = 15
Capybara.ignore_hidden_elements = false
