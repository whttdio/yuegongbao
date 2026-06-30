@echo off
setlocal

if not "%YGB_JAVA_HOME%"=="" (
  set "JAVA_HOME=%YGB_JAVA_HOME%"
)
if "%JAVA_HOME%"=="" if exist "D:\JAVA\JDK21\jdk-21\bin\java.exe" (
  set "JAVA_HOME=D:\JAVA\JDK21\jdk-21"
)
if "%JAVA_HOME%"=="" (
  echo JAVA_HOME is not set. Configure JAVA_HOME or YGB_JAVA_HOME to JDK 17+.
  exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"
echo Using JAVA_HOME=%JAVA_HOME%

mvn -pl yuegongbao-admin -am test %*
