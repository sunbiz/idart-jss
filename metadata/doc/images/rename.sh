#! /bin/csh
foreach FILE ( `ls *.png| awk -F"." '{print $1}'` )
        mv $FILE.png $FILE.PNG
