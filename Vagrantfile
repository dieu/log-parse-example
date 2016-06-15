# -*- mode: ruby -*-
# vi: set ft=ruby :

required_plugins = %w( vagrant-hostmanager )
plugins_to_install = required_plugins.select { |plugin| not Vagrant.has_plugin? plugin }
if not plugins_to_install.empty?
  puts "Installing plugins: #{plugins_to_install.join(' ')}"
  if system "vagrant plugin install #{plugins_to_install.join(' ')}"
    exec "vagrant #{ARGV.join(' ')}"
  else
    abort "Installation of one or more plugins has failed. Aborting."
  end
end

cluster_prefix = "spark"
memory_size =  ENV.has_key?('MEM') ? ENV['MEM'].to_i : 1024
number_cpus =  ENV.has_key?('CPU') ? ENV['CPU'].to_i : 1
dataFolder = ENV.has_key?('DATA_FOLDER') ? ENV['DATA_FOLDER'] : "#{File.dirname(__FILE__)}/src/main/resources/"

Vagrant.configure(2) do |config|
    config.hostmanager.enabled = true
    config.hostmanager.manage_host = true
    config.hostmanager.manage_guest = true
    config.hostmanager.ignore_private_ip = false
    config.hostmanager.include_offline = true

    nodename = "#{cluster_prefix}-master"

    config.vm.define "#{nodename}" do |node|
        node.vm.box = "boxcutter/centos71"
        node_hostname=nodename
        node_fqdn="spark1.vagrant"
        node_ip="10.10.10.11"
        node.vm.provider :virtualbox do |vbox|
            vbox.customize ["modifyvm", :id, "--memory", memory_size.to_s]
        	vbox.customize ['modifyvm', :id, '--cpus', number_cpus.to_s]
            vbox.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
            vbox.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
        end
        node.vm.synced_folder dataFolder, dataFolder
        node.vm.network :private_network, ip: node_ip
        node.vm.hostname = node_fqdn
        node.vm.provision :hostmanager
        node.vm.provision "shell", inline: <<-SHELL
            mkdir -p ~/.ssh
            wget https://raw.githubusercontent.com/mitchellh/vagrant/master/keys/vagrant -O ~/.ssh/id_rsa >/dev/null 2>&1
            chmod 600 ~/.ssh/id_rsa
            wget https://raw.githubusercontent.com/mitchellh/vagrant/master/keys/vagrant.pub -O ~/.ssh/authorized_keys >/dev/null 2>&1
            sed -ie 's/PasswordAuthentication yes/PasswordAuthentication no/g' /etc/ssh/sshd_config
            sed -ie 's/.*127.0.0.1.*/127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4/g' /etc/hosts
            systemctl restart sshd
            sudo yum install -y java-1.8.0-openjdk
            if [ ! -f spark-1.6.1-bin-hadoop2.6.tgz ]; then
                wget –quiet http://d3kbcqa49mib13.cloudfront.net/spark-1.6.1-bin-hadoop2.6.tgz >/dev/null 2>&1
            fi
            mkdir -p /opt/spark/
            tar -xzf spark-1.6.1-bin-hadoop2.6.tgz -C /opt/spark
        SHELL
        node.vm.provision "shell", inline: <<-SHELL
            /opt/spark/spark-1.6.1-bin-hadoop2.6/sbin/start-all.sh
        SHELL
    end
end
