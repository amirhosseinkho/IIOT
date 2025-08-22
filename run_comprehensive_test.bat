@echo off
echo ========================================
echo Comprehensive IIoT Scheduler Test Runner
echo ========================================
echo.

echo Step 1: Compiling project...
javac -cp "libs/*;src" src/algorithms/*.java src/core/*.java src/utils/*.java src/simulation/*.java src/evaluation/*.java test/*.java

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.

echo Step 2: Running comprehensive tests...
echo ----------------------------------------
java -cp "libs/*;src" test.ComprehensiveTestRunner

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Test execution failed!
    pause
    exit /b 1
)

echo.
echo ✅ Tests completed successfully!
echo.

echo Step 3: Installing Python dependencies...
echo -----------------------------------------
pip install -r requirements.txt

if %ERRORLEVEL% NEQ 0 (
    echo ⚠️  Python dependencies installation failed, trying to continue...
)

echo.
echo Step 4: Running Python analysis...
echo -----------------------------------
python analyze_results.py

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Python analysis failed!
    pause
    exit /b 1
)

echo.
echo 🎉 All steps completed successfully!
echo.
echo Summary:
echo - ✅ Java compilation
echo - ✅ Comprehensive testing
echo - ✅ Results export to CSV
echo - ✅ Python analysis and visualization
echo.
echo 📁 Results saved to:
echo   - quick_results/ (CSV files)
echo   - analysis_plots/ (Visualizations)
echo.
pause
