#\!/bin/sh
cd `dirname $0`
java -Xms64m -Xmx512m -classpath ../dist/IceNLPServer.jar is.iclt.icenlp.server.runner.Runner --config=../configs/server.apertium.conf
		
