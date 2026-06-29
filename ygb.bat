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
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
set "JPS_EXE=%JAVA_HOME%\bin\jps.exe"
if not exist "%JAVA_EXE%" (
    echo Java executable not found: %JAVA_EXE%
    exit /b 1
)

set AppName=yuegongbao-admin.jar
set JVM_OPTS="-Dname=%AppName% -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:NewRatio=1 -XX:SurvivorRatio=30 -XX:+UseParallelGC -XX:+UseParallelOldGC"

echo.
echo [1] Start %AppName%
echo [2] Stop %AppName%
echo [3] Restart %AppName%
echo [4] Status %AppName%
echo [5] Exit
echo.

echo Please select an action:
set /p ID=

if "%ID%"=="1" goto start
if "%ID%"=="2" goto stop
if "%ID%"=="3" goto restart
if "%ID%"=="4" goto status
if "%ID%"=="5" exit
pause

:start
for /f "usebackq tokens=1-2" %%a in (`"%JPS_EXE%" -l ^| findstr %AppName%`) do (
    set pid=%%a
    set image_name=%%b
)
if defined pid (
    echo %AppName% is already running
    pause
    goto:eof
)

start "" "%JAVA_EXE%" %JVM_OPTS% -jar %AppName%
echo Start %AppName% success...
goto:eof

:stop
for /f "usebackq tokens=1-2" %%a in (`"%JPS_EXE%" -l ^| findstr %AppName%`) do (
    set pid=%%a
    set image_name=%%b
)
if not defined pid (
    echo Process %AppName% does not exist
) else (
    echo Stopping %image_name%
    taskkill /f /pid %pid%
)
goto:eof

:restart
call :stop
call :start
goto:eof

:status
for /f "usebackq tokens=1-2" %%a in (`"%JPS_EXE%" -l ^| findstr %AppName%`) do (
    set pid=%%a
    set image_name=%%b
)
if not defined pid (
    echo Process %AppName% is not running
) else (
    echo %image_name% is running
)
goto:eof
