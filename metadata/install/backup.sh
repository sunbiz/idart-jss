#!/bin/bash

# customise these variables to suit your setup
userName="postgres"
dbName="pharm"
backupPath="/home/idart/backup"

##############################
# do not edit below this line
##############################

if [ -d backupPath ]; then
	mkdir -p "$backupPath"
	if [ $? -ne 0 ]; then
        echo "$0: Can't create backup folder, exiting..."
		exit 1
    fi
fi

day=`date +%d`
backupName="$backupPath/idart-$day.backup"

# dump file as custom, compress = 7 to the backupName filename
pg_dump -F c -Z 7 -U $userName -f $backupName $dbName