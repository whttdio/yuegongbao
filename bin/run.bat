@echo off
echo.
echo [Info] Run the backend service from the packaged jar
echo.

cd %~dp0
cd ../yuegongbao-admin/target

set JAVA_OPTS=-Xms256m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m

java %JAVA_OPTS% -jar yuegongbao-admin.jar

cd ../../bin
pause
