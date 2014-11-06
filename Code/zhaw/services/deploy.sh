#!/bin/sh

rm idemix*

echo "Starting apacheds default"
sudo /etc/init.d/apacheds-2.0.0-M17-default start

echo "Building components"
cd ../components
mvn clean install
cd ../services

echo "Building Services"
mvn clean
mvn -P ldap-ui-service install
~/web/web1/bin/shutdown.sh
rm -rf ~/web/web1/webapps/zhaw-p2abc-webservices
rm -f ~/web/web1/webapps/*.war
cp -f ./target/zhaw-p2abc-webservices.war ~/web/web1/webapps/zhaw-p2abc-webservices.war
#cp -rf ./target/zhaw-p2abc-webservices ~/web/web1/webapps/
cp -f ./src-web/ldapui.html ~/web/web1/webapps/ROOT/ldapui.html
sudo cp -f ldapServiceConfig.xml /etc/abc4trust/ldapServiceConfig.xml
~/web/web1/bin/startup.sh