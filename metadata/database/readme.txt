scripts folder
==============

Contains database scripts

idart_x.x.0_schema.sql		Script to drop/generate the iDART database (with NO data)
idart_x.x.0_coredata.sql	Script to populate tables with REQUIRED data only
idart_x.x.0_testdata.sql	Script to populate tables with test data
gernerateReleaseScripts.sh	Script to generate the above files as well as release scripts for distribution


These files are created as follows:

idart_x.x.0_schema.sql
==========================
1. Run the following pg_dump command
	pg_dump -h <host> -U <user> -F p -s -D -v -O -x -f "idart_x.x.0_schema.sql" idart
2. delete all DROP statments at beginning of file

idart_x.x.0_coredata.sql
============================
1. Use the following pg_dump command ((and add any additional tables containing default data that have been added since the previous release)
	pg_dump -h <host> -U <user> -f "idart_x.x.0_coredata.sql" -d -a --disable-triggers -t chemicalcompound -t chemicaldrugstrength -t clinic -t drug -t form -t pharmacy -t regimen -t regimendrugs -t simpledomain -t users idart

idart_x.x.0_testdata.sql
========================
1. run the following commands on a database with real data:
	update patient set firstnames = 'first'|| id;
	update patient set lastname = 'last'|| id;
	update patient set patientid = 'FN' || id;
2. use the following pg_dump command (and add any additional tables containing test data that have been added since the previous release)
	pg_dump -h <host> -U <user> -f "idart_x.x.0_testdata.sql" -d -a --disable-triggers -t accumulateddrugs -t adherencerecordtmp -t alternatepatientidentifier -t appointment -t doctor -t episode -t logging -t package -t packageddrugs -t patient -t pillcount -t pregnancy -t prescribeddrugs -t prescription -t stock -t stockadjustment -t stocktake idart




