@echo off
echo ===============================================
echo   Enhanced EPO-CEIS Evaluation Runner
echo ===============================================
echo.

REM Create results directory if it doesn't exist
if not exist "results" mkdir results

echo 🔧 Compiling Java source files...
javac -cp "libs/*;src" src/algorithms/*.java src/core/*.java src/simulation/*.java src/utils/*.java src/evaluation/*.java 2>compile_errors.txt

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Compilation failed! Check compile_errors.txt for details
    type compile_errors.txt
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.

echo 🚀 Running comprehensive evaluation...
echo This may take several minutes...
echo.

java -cp "libs/*;src" evaluation.MainEvaluation results/

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Evaluation failed!
    pause
    exit /b 1
)

echo.
echo ✅ Evaluation completed successfully!
echo 📊 Results are available in the 'results' directory
echo.

REM Clean up compilation errors file if everything worked
if exist "compile_errors.txt" del compile_errors.txt

echo Press any key to view results directory...
pause >nul
explorer results\

echo.
echo 🎉 Enhanced EPO-CEIS evaluation completed!
pause
