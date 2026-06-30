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
if "%YGB_DB_URL%"=="" set "YGB_DB_URL=jdbc:mysql://127.0.0.1:3306/yuegongbao?useUnicode=true^&characterEncoding=utf8^&zeroDateTimeBehavior=convertToNull^&useSSL=false^&allowPublicKeyRetrieval=true^&serverTimezone=Asia/Shanghai"
if "%YGB_DB_USERNAME%"=="" set "YGB_DB_USERNAME=root"
if "%YGB_DB_PASSWORD%"=="" set "YGB_DB_PASSWORD=root123456"
if "%YGB_REDIS_HOST%"=="" set "YGB_REDIS_HOST=127.0.0.1"
if "%YGB_REDIS_PORT%"=="" set "YGB_REDIS_PORT=6379"
if "%YGB_REDIS_DATABASE%"=="" set "YGB_REDIS_DATABASE=0"
if "%YGB_UPLOAD_DIR%"=="" set "YGB_UPLOAD_DIR=D:\yuegongbao\uploadPath"
if "%YGB_INTEGRATION_MODE%"=="" set "YGB_INTEGRATION_MODE=stub"

echo Using JAVA_HOME=%JAVA_HOME%
echo Using YGB_DB_URL=%YGB_DB_URL%
echo Using YGB_REDIS_HOST=%YGB_REDIS_HOST%

mvn -pl yuegongbao-admin -am -f yuegongbao-admin\pom.xml spring-boot:run
