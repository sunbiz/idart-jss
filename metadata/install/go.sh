#!/bin/bash
# Calls go.sh, which Launches the Cell-Life Pharmacy Application.
# necessary for correct working directory when running the application

LSOF=$(lsof -p $$ | grep -E "/"$(basename $0)"$")
FPATH=$(echo $LSOF | sed -r s/'^([^\/]+)\/'/'\/'/1 2>/dev/null)
ROOT=$(dirname $FPATH)

cd $ROOT
./launcher.sh org.celllife.idart.start.PharmacyApplication $*