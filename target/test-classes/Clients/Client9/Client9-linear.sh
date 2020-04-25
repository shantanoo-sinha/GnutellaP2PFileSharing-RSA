cd ./../../lib/classes
export CLASSPATH="$CLASSPATH:./../../lib/classes:./../../lib/*"
echo $CLASSPATH
sleep 5
java -classpath $CLASSPATH -Dlog4j.configurationFile="file:./../../lib/classes/log4j2.properties" -Djava.rmi.server.codebase=file:./../../lib/classes/ -Djava.security.policy=file:./../../lib/classes/security.policy client.Client linear Client9 $1 $2
sleep 50