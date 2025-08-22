#!/bin/bash

echo "========================================================"
echo "   🔥🔥🔥 COMPREHENSIVE STRESS TESTING SUITE 🔥🔥🔥"
echo "========================================================"
echo "Running all stress tests and benchmarks for Enhanced EPO-CEIS"
echo

# Create results directories
mkdir -p stress_test_results
mkdir -p benchmark_results

echo "📂 Results directories created"
echo

# Compile all test files
echo "🔨 Compiling test suite..."
javac -cp "libs/*:src" src/test/*.java src/algorithms/*.java src/core/*.java src/simulation/*.java src/utils/*.java src/evaluation/*.java

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

echo "✅ Compilation successful!"
echo

# Run Advanced Scenario Tester
echo "🚀 Running Advanced Scenario Stress Tests..."
echo "============================================"
java -cp "libs/*:src" test.AdvancedScenarioTester
echo

# Run Real-World Scenario Tester  
echo "🏭 Running Real-World Scenario Tests..."
echo "====================================="
java -cp "libs/*:src" test.RealWorldScenarioTester
echo

# Run Performance Benchmark
echo "⚡ Running Performance Benchmarks..."
echo "=================================="
java -cp "libs/*:src" test.PerformanceBenchmark
echo

# Run Quick System Test for comparison
echo "🔍 Running Quick System Test for validation..."
echo "============================================"
java -cp "libs/*:src" test.QuickSystemTest
echo

echo "🎉 ALL STRESS TESTS COMPLETED!"
echo "=============================="
echo
echo "📊 Results saved in:"
echo "  - stress_test_results/"
echo "  - benchmark_results/"
echo
echo "Check the output above for detailed performance analysis."


