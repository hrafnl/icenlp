#\!/bin/sh
cd `dirname $0`
java -Xms64m -Xmx512m -Dfile.encoding=UTF-8 -classpath ../dist/IceNLPServer.jar is.ru.iclt.icenlp.slave.runner.Runner -h=$1 -sh=/usr/local/applications/apertium/apertium-is-en/icenlpApertium.sh -p=2525
		
