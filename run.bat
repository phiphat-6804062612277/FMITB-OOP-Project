@echo off
echo ===================================================
echo   Building and Running Five Minutes In The Bathroom
echo ===================================================

if not exist bin mkdir bin

dir /s /b src\*.java > sources.txt
echo Compiling Java source files...
javac -encoding UTF-8 -d bin @sources.txt
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    del sources.txt
    pause
    exit /b %ERRORLEVEL%
)
del sources.txt

echo Starting game...
java -cp bin Main
pause
