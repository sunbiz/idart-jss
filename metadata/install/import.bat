%@echo off
set FILENAME=Patients.xls
set SHEETNAME=import

.\go.bat import %FILENAME% %SHEETNAME%

set FILENAME=
set SHEETNAME=