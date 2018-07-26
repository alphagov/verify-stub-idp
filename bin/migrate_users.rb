#! /usr/bin/env ruby

require 'yaml'
require 'net/http'
require 'io/console'

UK_CLOUD_HOSTNAME="https://idp-stub-integration.ida.digital.cabinet-office.gov.uk"
PAAS_HOSTNAME="https://ida-stub-idp.cloudapps.digital"

puts "Password for ida admin user: "
password = STDIN.noecho(&:gets).chomp

idps = YAML.load_file('../verify-stub-idp-federation-config/configuration/integration/stub-idps.yml')['stubIdps'].map { |idp| idp['friendlyId'] }

idps.each do |idp|
  uri = URI("#{UK_CLOUD_HOSTNAME}/#{idp}/users")
  req = Net::HTTP::Get.new(uri)
  req.basic_auth 'ida', password
  res = Net::HTTP.start(uri.hostname, uri.port, :use_ssl => true) {|http|
    http.request(req)
  }
  uri = URI("#{PAAS_HOSTNAME}/#{idp}/users")
  req = Net::HTTP::Post.new(uri, 'Content-Type' => ' application/json')
  req.basic_auth 'ida', password
  req.body = res.body
  Net::HTTP.start(uri.hostname, uri.port, :use_ssl => true) { |http| http.request(req) }
  puts "Migrated users for IDP: #{idp}"
end

