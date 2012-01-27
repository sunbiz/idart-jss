#!/bin/bash
# This batch file is used to launch iDART related application.
# It can be used by passing the Java class that needs to be run
#	along with any other command line arguments.
# e.g. .\lanucher.sh org.celllife.idart.start.PharmacyApplication
#
# windows equivalent = launcher.bat

LSOF=$(lsof -p $$ | grep -E "/"$(basename $0)"$")
FPATH=$(echo $LSOF | sed -r s/'^([^\/]+)\/'/'\/'/1 2>/dev/null)
ROOT=$(dirname $FPATH)
PWD=`pwd`

if [ $ROOT != $PWD ]
then
    echo "This script must be run from the same directory as it is in ($ROOT)."
    exit 1
fi

# build the classpath
export TMP_CLASSPATH=$CLASSPATH
export CLASSPATH=$CLASSPATH:/usr/lib/

for jarfile in $PWD/lib/*.jar
do
	CLASSPATH=$CLASSPATH:$jarfile
done

export PHARM_CLASSPATH=$CLASSPATH
export CLASSPATH=$TMP_CLASSPATH

exec  %JAVA_HOME/bin/java  -cp $PHARM_CLASSPATH:bin/Pharmacy.jar -Djava.library.path=$PWD -Xms24m -Xmx512m $*