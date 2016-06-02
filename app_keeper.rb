#!/usr/bin/env ruby
# encoding: utf-8
#
# 需要调整事项:
# 1. 应用图标
# 2. 应用名称XML档
# 3. Gradle应用ID
# 4. AndroidManifest 友盟、蒲公英配置
# 5. PrivateURLs 服务器域名
#

require 'erb'
require 'settingslogic'
require 'active_support/core_ext/string'

bundle_display_hash = {
  yonghui: '永辉生意人',
  shengyiplus: '生意+',
  qiyoutong: '企邮通'
}
bundle_display_names = bundle_display_hash.keys.map(&:to_s)

current_app = ARGV.shift || 'null' # File.read('.current-app').strip.freeze
unless bundle_display_names.include?(current_app)
  puts %(appname should in #{bundle_display_names}, but #{current_app})
  exit
end

if IO.read('.current-app').strip == current_app
  puts %(current app already: #{current_app})
  exit
end

current_app_name = bundle_display_hash.fetch(current_app.to_sym)

`echo '#{current_app}' > .current-app`
puts %(#{'-' * 25}\ncurrent app: #{current_app}\n#{'-' * 25}\n\n)

NAME_SPACE = current_app # TODO: namespace(variable_instance)
class Settings < Settingslogic
  source 'config/config.yaml'
  namespace NAME_SPACE
end

#
# reset app/build.gradle
#
gradle_path = 'app/build.gradle'
gradle_text = IO.read(gradle_path)
gradle_lines = gradle_text.split(/\n/)
application_id_line = gradle_lines.find { |line| line.include?('applicationId') }
application_id = application_id_line.strip.scan(/applicationId\s+'com\.intfocus\.(.*?)'/).flatten[0]
new_application_id_line = application_id_line.sub(application_id, current_app)

puts %(done - applicationId: #{application_id})
File.open(gradle_path, 'w:utf-8') do |file|
  file.puts gradle_text.sub(application_id_line, new_application_id_line)
end

#
# reset mipmap and loading.zip
#
puts %(done - launcher@mipmap)
`rm -fr app/src/main/res/mipmap-* && cp -fr config/Assets/mipmap-#{current_app}/mipmap-* app/src/main/res/`
puts %(done - loading zip)
`cp -f config/Assets/loading-#{current_app}.zip app/src/main/assets/loading.zip`
puts %(done - banner_logo)
`cp -f config/Assets/banner-logo-#{current_app}.png app/src/main/res/drawable/banner_logo.png`

#
# reset app/src/main/AndroidManifest.xml
# 
android_manifest_erb_path = 'config/AndroidManifest.xml.erb'
android_manifest_xml_path = 'app/src/main/AndroidManifest.xml'
puts %(done - umeng/pgyer configuration)
File.open(android_manifest_xml_path, 'w:utf-8') do |file|
  file.puts ERB.new(IO.read(android_manifest_erb_path)).result
end

#
# reset res/strings.xml
# 
strings_erb_path = 'config/strings.xml.erb'
strings_xml_path = 'app/src/main/res/values/strings.xml'
puts %(done - app name: #{current_app_name})
File.open(strings_xml_path, 'w:utf-8') do |file|
  file.puts ERB.new(IO.read(strings_erb_path)).result
end

puts %(done - PrivateURLs java class)
File.open('app/src/main/java/com/intfocus/yh_android/util/PrivateURLs.java', 'w:utf-8') do |file|
  file.puts <<-EOF.strip_heredoc
    //  PrivateURLs.java
    //
    //  `bundle install`
    //  `bundle exec ruby app_kepper.rb`
    //
    //  Created by lijunjie on 16/06/02.
    //  Copyright © 2016年 com.intfocus. All rights reserved.
    //

    // current app: [#{current_app}]
    // automatic generated by app_keeper.rb
    package com.intfocus.yh_android.util;

    public class PrivateURLs {
      public final static String HOST = "#{Settings.server}";
      public final static String HOST1 = "http://10.0.3.2:4567";
    }

    EOF
end

