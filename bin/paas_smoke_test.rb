require 'rspec'
require_relative 'spec_helper'

describe 'User verifies with Stub IDP on PaaS', type: :feature do
  it 'should end up on the logged in test-rp page', js: true do
    visit 'https://test-rp-stub-integration.ida.digital.cabinet-office.gov.uk/test-rp'
    click_button 'Start'
    choose 'start_form_selection_false'
    click_button 'Continue'
    click_button 'Select Stub Idp PaaS Demo'
    fill_in 'username', with: 'stub-idp-demo'
    fill_in 'password', with: 'bar'
    click_button 'SignIn'
    click_button 'I Agree'
    has_content?('Your identity has been confirmed')
  end
end