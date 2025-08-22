#!/bin/bash

echo "==============================================="
echo "   Enhanced EPO-CEIS Evaluation Runner"
echo "==============================================="
echo ""

# Create results directory if it doesn't exist
mkdir -p results

echo "🔧 Compiling Java source files..."
javac -cp "libs/*:src" src/algorithms/*.java src/core/*.java src/simulation/*.java src/utils/*.java src/evaluation/*.java 2>compile_errors.txt

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed! Check compile_errors.txt for details"
    cat compile_errors.txt
    read -p "Press Enter to continue..."
    exit 1
fi

echo "✅ Compilation successful!"
echo ""

echo "🚀 Running comprehensive evaluation..."
echo "This may take several minutes..."
echo ""

java -cp "libs/*:src" evaluation.MainEvaluation results/

if [ $? -ne 0 ]; then
    echo "❌ Evaluation failed!"
    read -p "Press Enter to continue..."
    exit 1
fi

echo ""
echo "✅ Evaluation completed successfully!"
echo "📊 Results are available in the 'results' directory"
echo ""

# Clean up compilation errors file if everything worked
rm -f compile_errors.txt

echo "Results saved to: $(pwd)/results/"
ls -la results/

echo ""
echo "🎉 Enhanced EPO-CEIS evaluation completed!"
read -p "Press Enter to finish..."
