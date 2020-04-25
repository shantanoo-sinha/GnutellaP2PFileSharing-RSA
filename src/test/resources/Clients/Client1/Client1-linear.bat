set path = "C:\Program Files\Java\jdk1.8.0_151\bin"
timeout /t 5
java -classpath "F:\Workspace\GnutellaP2PFileSharing-RSA\target\GnutellaP2PFileSharing-RSA\lib\*" -Dlog4j.configurationFile=log4j2.properties -jar F:\Workspace\GnutellaP2PFileSharing-RSA\target\GnutellaP2PFileSharing-RSA\GnutellaP2PFileSharing-RSA.jar F:\Workspace\GnutellaP2PFileSharing-RSA\target\GnutellaP2PFileSharing-RSA\topologies\linear.txt 1 F:\Workspace\GnutellaP2PFileSharing-RSA\target\GnutellaP2PFileSharing-RSA\Clients\Client1\files\master PUSH
timeout /t 50