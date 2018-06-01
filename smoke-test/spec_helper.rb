require 'capybara/rspec'
require 'selenium-webdriver'

Capybara.javascript_driver = :selenium_remote_firefox
Capybara.register_driver "selenium_remote_firefox".to_sym do |app|
  Capybara::Selenium::Driver.new(app, browser: :remote, url: "http://selenium-hub:4444/wd/hub", desired_capabilities: :firefox)
end

Capybara.default_max_wait_time = 15
Capybara.ignore_hidden_elements = false
