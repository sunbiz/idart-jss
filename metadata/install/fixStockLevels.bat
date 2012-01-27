@echo off
set TMP_CLASSPATH=%CLASSPATH%

set CLASSPATH=.\target\
rem Add all jars....

for %%i in (".\lib\*.jar") do call ".\cpappend.bat" %%i
for %%i in (".\bin\*.jar") do call ".\cpappend.bat" %%i

set PHARM_CLASSPATH=%CLASSPATH%;.
set CLASSPATH=%TMP_CLASSPATH%

set JAVA_HOME=$JAVA_HOME
set JAVA_CMD=%JAVA_HOME%\bin\javaw.exe

start  "iDART" "%JAVA_CMD%" -cp "%PHARM_CLASSPATH%" -Djava.library.path="$INSTALL_PATH" -Xms24m -Xmx512m org.celllife.idart.start.FixStockLevels %*



