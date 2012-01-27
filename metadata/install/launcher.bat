:: This batch file is used to launch iDART related application.
:: It can be used by passing the Java class that needs to be run
::	along with any other command line arguments.
:: e.g. .\lanucher.bat org.celllife.idart.start.PharmacyApplication
::
:: linux equivalent = launcher.sh

:: The ROOT directory (in which iDART is installed) is assumed
:: 	to be the same directory that this script is in.
set ROOT="%~dp0"

set TMP_CLASSPATH=%CLASSPATH%

:: Add all jars to the classpath

for %%i in (".\lib\*.jar") do call ".\cpappend.bat" %%i
for %%i in (".\bin\*.jar") do call ".\cpappend.bat" %%i

set PHARM_CLASSPATH=%CLASSPATH%;.
set CLASSPATH=%TMP_CLASSPATH%

set JAVA_HOME=$JAVA_HOME
set JAVA_CMD=%JAVA_HOME%\bin\javaw.exe

:: %ROOT~0,-2% = extract all but the last 2 characters from the ROOT variable (in this case '\"')
start  "iDART" "%JAVA_CMD%" -cp "%PHARM_CLASSPATH%" -Djava.library.path=%ROOT:~0,-2%" -Xms24m -Xmx512m %*