#\!/bin/sh
cd `dirname $0`
apertium_script_location="/home/hs/temp/apertium-is-en/icenlpApertium.sh"
java -Xms64m -Xmx512m -Dfile.encoding=UTF-8 -classpath ../dist/IceNLPServer.jar is.iclt.icenlp.slave.runner.Runner -h=$1 -sh=/home/hs/temp/apertium-is-en/icenlpApertium.sh -p=2525
		
