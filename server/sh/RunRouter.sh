#\!/bin/sh
cd `dirname $0`
java -Xms64m -Xmx512m -Dfile.encoding=UTF-8 -classpath ../dist/IceNLPServer.jar is.iclt.icenlp.router.runner.Runner -h=$1
		
