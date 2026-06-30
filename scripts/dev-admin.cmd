@echo off
setlocal

set "PORTAL=%~1"
if "%PORTAL%"=="" set "PORTAL=ygb"
if /I not "%PORTAL%"=="ygb" if /I not "%PORTAL%"=="azb" (
  echo Usage: dev-admin.cmd [ygb^|azb]
  exit /b 1
)

cd /d "%~dp0..\yuegongbao-ui"
npm.cmd run dev:%PORTAL%
