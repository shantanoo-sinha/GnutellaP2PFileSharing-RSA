set path = "C:\Program Files\Java\jdk1.8.0_151\bin"
timeout /t 5
java -classpath "F:\Workspace\GnutellaP2PFileSharing-RSA\target\GnutellaP2PFileSharing-RSA\lib\*" -Dlog4j.configurationFile=file:///f:/Workspace/GnutellaP2PFileSharing-RSA/target/GnutellaP2PFileSharing-RSA/log4j2.properties -jar F:\Workspace\GnutellaP2PFileSharing-RSA\target\GnutellaP2PFileSharing-RSA\GnutellaP2PFileSharing-RSA.jar F:\Workspace\GnutellaP2PFileSharing-RSA\target\GnutellaP2PFileSharing-RSA\topologies\all-to-all.txt 6 F:\Workspace\GnutellaP2PFileSharing-RSA\target\GnutellaP2PFileSharing-RSA\Clients\Client6\files\master PUSH
timeout /t 50