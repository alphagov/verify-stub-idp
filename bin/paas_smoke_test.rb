require 'rspec'
require_relative 'spec_helper'

describe 'User verifies with Stub IDP on PaaS', type: :feature do
  it 'should end up on the logged in test-rp page', js: true do
    rp_url = ENV.fetch('RP_URL')
    idp_name = ENV.fetch('IDP_NAME')
    idp_username = ENV.fetch('IDP_USERNAME')
    idp_password = ENV.fetch('IDP_PASSWORD')

    visit rp_url
    click_button 'Start'
    choose 'start_form_selection_false'
    click_button 'Continue'
    find("button[name*=#{idp_name}]").click
    fill_in 'username', with: idp_username
    fill_in 'password', with: idp_password
    click_button 'SignIn'
    click_button 'I Agree'
    expect(page).to have_content('Your identity has been confirmed')
  end
end
