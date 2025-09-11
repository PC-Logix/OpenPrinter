@echo off
setlocal
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-8.0.422.5-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
call "%~dp0gradlew.bat" %*
endlocal