##!/bin/bash
# This script file launches the StableUpdate application.
#
# windows version = update.bat

LSOF=$(lsof -p $$ | grep -E "/"$(basename $0)"$")
FPATH=$(echo $LSOF | sed -r s/'^([^\/]+)\/'/'\/'/1 2>/dev/null)
ROOT=$(dirname $FPATH)

cd $ROOT

URL="http://update.cell-life.org/idart/updates.xml"
NAME="iDART"
./launcher.sh org.gnu.amSpacks.app.update.Updater $ROOT $URL $NAME


