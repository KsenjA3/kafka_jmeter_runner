@echo off

REM Жёстко задаём имя сценария
set "SCENARIO=kafka-sender33"

REM Метка времени для уникальных файлов (формат: YYYY-MM-DD_HH-MM-SS)
set "TS=%DATE:~6,4%-%DATE:~3,2%-%DATE:~0,2%_%TIME:~0,2%-%TIME:~3,2%-%TIME:~6,2%"
set "TS=%TS: =0%"

REM Пути к тест-плану и папкам для отчётов
set "PROJECT_DIR=%~dp0"
if "%PROJECT_DIR:~-1%"=="\\" set "PROJECT_DIR=%PROJECT_DIR:~0,-1%"
set "JMETER_DIR=%PROJECT_DIR%jmeter_tests"
set "TEST_PLAN=%JMETER_DIR%\%SCENARIO%.jmx"
set "RESULTS_FILE=%JMETER_DIR%\%SCENARIO%-results-%TS%.jtl"
set "HTML_REPORT=%JMETER_DIR%\%SCENARIO%-html-report-%TS%"
set "JMETER_LOG=%JMETER_DIR%\jmeter-%SCENARIO%-%TS%.log"

REM Удалить старые папки отчётов для этого сценария (если есть)
for /d %%D in ("%JMETER_DIR%\%SCENARIO%-html-report*") do rd /s /q "%%D"

if not exist "%HTML_REPORT%" mkdir "%HTML_REPORT%"

echo ========================================
echo JMeter Kafka Test Scenario: %SCENARIO%
echo ========================================
echo.
echo Running JMeter test...
echo Test Plan: %TEST_PLAN%"
echo Results: %RESULTS_FILE%"
echo HTML Report: %HTML_REPORT%"
echo.
cd /d C:\Users\User\apache-jmeter-5.6.3
bin\jmeter.bat -n -t "%TEST_PLAN%" -l "%RESULTS_FILE%" -e -o "%HTML_REPORT%" -j "%JMETER_LOG%"

cd /d "%PROJECT_DIR%"
echo.
echo Test completed!
echo Check the HTML report:
echo   %HTML_REPORT%\index.html

echo.
pause 

