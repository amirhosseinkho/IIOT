#!/bin/bash

echo "========================================"
echo "Testing Fixed IIoT Scheduler Algorithms"
echo "========================================"
echo

echo "Compiling project..."
javac -cp "libs/*:src" src/algorithms/*.java src/core/*.java src/utils/*.java src/simulation/*.java src/evaluation/*.java test/*.java

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

echo "✅ Compilation successful!"
echo

echo "🧪 Testing Fixed Baseline Algorithms..."
echo "----------------------------------------"
java -cp "libs/*:src" test.TestFixedAlgorithms

echo
echo "🧪 Testing CloudSimWrapper..."
echo "-----------------------------"
java -cp "libs/*:src" test.TestCloudSimWrapper

echo
echo "✅ All tests completed!"
echo
echo "Summary of fixes applied:"
echo "- Fixed PSO infinite cost issue"
echo "- Fixed NSGA-II crowding distance calculation"
echo "- Added proper error handling and validation"
echo "- Added CloudSim integration wrapper"
echo "- Added fallback mechanisms for failed algorithms"
echo
