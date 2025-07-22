@echo off

REM Метка времени для уникальных папок (формат: YYYY-MM-DD_HH-MM-SS)
set "TS=%DATE:~6,4%-%DATE:~3,2%-%DATE:~0,2%_%TIME:~0,2%-%TIME:~3,2%-%TIME:~6,2%"
set "TS=%TS: =0%"

REM Получить абсолютный путь к корню проекта (где лежит этот батник)
set "PROJECT_DIR=%~dp0"
if "%PROJECT_DIR:~-1%"=="\" set "PROJECT_DIR=%PROJECT_DIR:~0,-1%"

REM Пути к тест-плану и папкам для отчётов
set "JMETER_DIR=%PROJECT_DIR%\jmeter_tests"

set "DB_RESULTS_FILE=%JMETER_DIR%\database-query-results-influxdb-%TS%.jtl"
set "DB_HTML_REPORT=%JMETER_DIR%\database-query-html-report-influxdb-%TS%"
set "DB_TEST_PLAN=%JMETER_DIR%\JDBC-Requests-influxdb.jmx"
set "JMETER_JDBC_LOG=%JMETER_DIR%\jmeter-jdbc-influxdb-%TS%.log"


REM Удалить старые папки отчетов (если есть)

for /d %%D in ("%JMETER_DIR%\database-query-html-report-influxdb*") do rd /s /q "%%D"


if not exist "%DB_HTML_REPORT%" mkdir "%DB_HTML_REPORT%"





echo ========================================
echo JMeter End-to-End Kafka Test (JDBC Scenario)
echo ========================================
echo.

echo.
echo Running JMeter test with end-to-end timing...
echo Test Plan: %DB_TEST_PLAN%"
echo Results: %DB_RESULTS_FILE%"
echo HTML Report: %DB_HTML_REPORT%"
echo.
cd /d c:\Users\User\apache-jmeter-5.6.3
bin\jmeter.bat -n -t %DB_TEST_PLAN% -l %DB_RESULTS_FILE% -e -o %DB_HTML_REPORT% -j %JMETER_JDBC_LOG%"

cd /d "%PROJECT_DIR%"
echo.
echo Test completed!
echo Check the HTML reports for detailed results:
echo   %DB_HTML_REPORT%\index.html

pause 