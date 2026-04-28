@echo off
title Skyways Airport Dispatch Tycoon - Build
echo ============================================
echo  Skyways Airport Dispatch Tycoon
echo  Building and launching...
echo ============================================
echo.

:: Auto-detect JAVA_HOME if not already set
if not defined JAVA_HOME (
    :: Try common install locations
    for /d %%G in (
        "C:\Program Files\Eclipse Adoptium\jdk-*"
        "C:\Program Files\Java\jdk-*"
        "C:\Program Files\Microsoft\jdk-*"
        "C:\Program Files\Zulu\zulu-*"
    ) do (
        if exist "%%G\bin\java.exe" (
            set "JAVA_HOME=%%G"
            goto :found
        )
    )
    :: Fall back to java on PATH
    where java >nul 2>nul
    if not errorlevel 1 (
        for /f "delims=" %%J in ('where java') do (
            for %%P in ("%%~dpJ..") do set "JAVA_HOME=%%~fP"
            goto :found
        )
    )
    echo ERROR: Java not found. Please install Java 21 or set JAVA_HOME.
    pause
    exit /b 1
)
:found
echo Using JAVA_HOME: %JAVA_HOME%
echo.

call mvnw.cmd javafx:run

if errorlevel 1 (
    echo.
    echo BUILD FAILED. Check errors above.
    pause
)
