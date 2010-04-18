#\!/bin/sh
cd `dirname $0`
apertium_script_location="CHANGE-THIS"
java -Xms64m -Xmx512m -Dfile.encoding=UTF-8 -classpath ../dist/IceNLPServer.jar is.iclt.icenlp.slave.runner.Runner -h=$1 -sh=$apertium_script_location -p=2525
		
