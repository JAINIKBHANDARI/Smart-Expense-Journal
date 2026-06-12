@echo off
title Smart Expense Journal
cls
echo Compiling Smart Expense Journal...
javac *.java
if errorlevel 1 (
    echo.
    echo Compilation failed. Please check the errors above.
    pause
    exit /b 1
)
cls
java Main
echo.
pause
