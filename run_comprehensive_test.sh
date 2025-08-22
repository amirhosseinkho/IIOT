#!/bin/bash

echo "========================================"
echo "Comprehensive IIoT Scheduler Test Runner"
echo "========================================"
echo

echo "Step 1: Compiling project..."
javac -cp "libs/*:src" src/algorithms/*.java src/core/*.java src/utils/*.java src/simulation/*.java src/evaluation/*.java test/*.java

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

echo "✅ Compilation successful!"
echo

echo "Step 2: Running comprehensive tests..."
echo "----------------------------------------"
java -cp "libs/*:src" test.ComprehensiveTestRunner

if [ $? -ne 0 ]; then
    echo "❌ Test execution failed!"
    exit 1
fi

echo
echo "✅ Tests completed successfully!"
echo

echo "Step 3: Installing Python dependencies..."
echo "-----------------------------------------"
pip3 install -r requirements.txt

if [ $? -ne 0 ]; then
    echo "⚠️  Python dependencies installation failed, trying to continue..."
fi

echo
echo "Step 4: Running Python analysis..."
echo "-----------------------------------"
python3 analyze_results.py

if [ $? -ne 0 ]; then
    echo "❌ Python analysis failed!"
    exit 1
fi

echo
echo "🎉 All steps completed successfully!"
echo
echo "Summary:"
echo "- ✅ Java compilation"
echo "- ✅ Comprehensive testing"
echo "- ✅ Results export to CSV"
echo "- ✅ Python analysis and visualization"
echo
echo "📁 Results saved to:"
echo "  - evaluation_results/ (CSV files)"
echo "  - analysis_plots/ (Visualizations)"
echo
