#!/bin/sh

echo 'Downloading AttributeInfoCollection'
curl --user both:tomcat -X GET 'http://localhost:8888/zhaw-p2abc-webservices/issuance/protected/attributeInfoCollection/userdata' > ./gen/attrInfoCol.xml

echo 'Generating CredentialSpecification'
curl --user both:tomcat -X POST --header 'Content-Type: application/xml' -d @./gen/attrInfoCol.xml 'http://localhost:8888/zhaw-p2abc-webservices/issuance/protected/genCredSpec/' > ./gen/credSpec.xml

# urn:abc4trust:credspec:ldap:abcPerson == urn%3Aabc4trust%3Acredspec%3Aldap%3AabcPerson

echo 'Storing CredentialSpecification at Issuer'

curl --user both:tomcat -X PUT --header 'Content-Type: application/xml' -d @./gen/credSpec.xml 'http://localhost:8888/zhaw-p2abc-webservices/issuance/protected/storeCredentialSpecification/urn%3Afiware%3Aprivacy%3Acredspec%3Auserdata'

echo 'Store QueryRule at Issuer'

curl --user both:tomcat -X PUT --header 'Content-Type: application/xml' -d @queryRule.xml 'http://localhost:8888/zhaw-p2abc-webservices/issuance/protected/storeQueryRule/urn%3Afiware%3Aprivacy%3Acredspec%3Auserdata'

echo 'Store IssuancePolicy at Issuer'

curl --user both:tomcat -X PUT --header 'Content-Type: application/xml' -d @issuancePolicy.xml 'http://localhost:8888/zhaw-p2abc-webservices/issuance/protected/storeIssuancePolicy/urn%3Afiware%3Aprivacy%3Acredspec%3Auserdata'
