#\!/bin/sh
cd `dirname $0`
java -Xms64m -Xmx512m -classpath ../dist/IceNLPServer.jar is.ru.iclt.icenlp.server.runner.Runner --config=../configs/server.conf
		
