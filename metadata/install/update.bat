:: This batch file launches the StableUpdate application.
::
:: Linux version = update.sh

%@echo off
set APPDIR=%~dp0
set URL=http://update.cell-life.org/idart/updates.xml
set NAME=iDART
.\launcher.bat org.gnu.amSpacks.app.update.Updater "%APPDIR:~0,-1%" %URL% %NAME%



