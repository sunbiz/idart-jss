:: --------------------------------------------------------------
:: dump.bat
::
:: This file connects to the database and dumps it to a file.
::
:: Written by Nico Gevers
:: Cell-Life
:: September 2006
::
:: last modified : October 2008 by Simon Kelly
:: --------------------------------------------------------------

:: customise these variables to suit your setup
set postgresDir="c:\Program Files\PostgreSQL\8.2\bin"
set userName="postgres"
set dbName="pharm"

@ECHO OFF
BREAK=OFF

FOR /F "TOKENS=1* DELIMS= " %%A IN ('DATE/T') DO SET CDATE=%%B
For /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set date=%%b%%c)

set backupName="c:\backup\iDART%date%.backup"

:: dump file as custom, compress = 9 to the backupName filename
%postgresDir%\pg_dump -F c -Z 7 -U %userName% -f %backupName% %dbName%

BREAK=ON
:: deinitialise variables
set postgresDir=
set userName=
set dbName=
set backupName=


