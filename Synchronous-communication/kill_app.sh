# Filename: start_app.sh
# This file should be sourced

#! usr/bin/bash
echo "killing app"

chmod u+x kill_app.sh

#cd idea-IU-231.8109.175/bin

sudo kill -9 $(sudo lsof -t -i:7000)
sudo kill -9 $(sudo lsof -t -i:7001)
sudo kill -9 $(sudo lsof -t -i:7002)
sudo kill -9 $(sudo lsof -t -i:7003)
clear
sudo lsof -i -P -n | grep LISTEN


pwd
echo $$

#cd


