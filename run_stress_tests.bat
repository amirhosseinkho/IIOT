@echo off
echo ========================================================
echo    🔥🔥🔥 COMPREHENSIVE STRESS TESTING SUITE 🔥🔥🔥
echo ========================================================
echo Running all stress tests and benchmarks for Enhanced EPO-CEIS
echo.

REM Create results directories
if not exist "stress_test_results" mkdir stress_test_results
if not exist "benchmark_results" mkdir benchmark_results

echo 📂 Results directories created
echo.

REM Compile all test files
echo 🔨 Compiling test suite...
javac -cp "libs/*;src" src/test/*.java src/algorithms/*.java src/core/*.java src/simulation/*.java src/utils/*.java src/evaluation/*.java

if %ERRORLEVEL% neq 0 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.

REM Run Advanced Scenario Tester
echo 🚀 Running Advanced Scenario Stress Tests...
echo ============================================
java -cp "libs/*;src" test.AdvancedScenarioTester
echo.

REM Run Real-World Scenario Tester  
echo 🏭 Running Real-World Scenario Tests...
echo =====================================
java -cp "libs/*;src" test.RealWorldScenarioTester
echo.

REM Run Performance Benchmark
echo ⚡ Running Performance Benchmarks...
echo ==================================
java -cp "libs/*;src" test.PerformanceBenchmark
echo.

REM Run Quick System Test for comparison
echo 🔍 Running Quick System Test for validation...
echo ============================================
java -cp "libs/*;src" test.QuickSystemTest
echo.

echo 🎉 ALL STRESS TESTS COMPLETED!
echo ==============================
echo.
echo 📊 Results saved in:
echo   - stress_test_results/
echo   - benchmark_results/
echo.
echo Check the output above for detailed performance analysis.
pause


