BankBox
=======

Poalim CTO BankBox




A running Service provider login - http://ec2-54-82-116-231.compute-1.amazonaws.com:8080/bankbox/test - currently for webfederation only with ssocircle... if you don't have a user create at
http://www.ssocircle.com/en/ 



1) BankboxProtocolLayer folder - contains standalone REST spring controller and client API implementatin. 
it can be bootstrapped from gradlew. just go to command line in BankboxProtocolLayer\
and run:  gradlew build
after it will bootstrap run gradlew eclipse to build a working eclipse project, then you can export project from eclipse.
run the Application.main it will bootstrap spring + tomcat and will run the restcontroler on 8080
client API will implement android UI Client lib. 







