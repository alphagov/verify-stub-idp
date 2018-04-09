#!/usr/bin/env ruby

PROJECT_ROOT = File.expand_path(File.dirname(__FILE__) + '/..')

require 'yaml'

if __FILE__ == $0
  system "#{PROJECT_ROOT}/bin/build.rb --upload-to-packages"
  exit($?.exitstatus)
end
