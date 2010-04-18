#\!/bin/sh
cd `dirname $0`
java -classpath ../dist/IceNLPServer.jar is.iclt.icenlp.client.runner.Runner --port=1234 --host=localhost
		
