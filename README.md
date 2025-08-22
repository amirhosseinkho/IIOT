# IIoT Task Scheduling System

A comprehensive framework for optimizing task scheduling in Industrial Internet of Things (IIoT) environments using advanced evolutionary algorithms.

## Overview

This project implements multiple scheduling algorithms including Enhanced EPO-CEIS (Enhanced Evolutionary Programming with Constraint-based Evolutionary Iterative Search), Genetic Algorithm, Particle Swarm Optimization, Min-Min heuristic, and First-Fit heuristic.

## Key Features

- **Multi-algorithm scheduling framework** with comprehensive evaluation
- **Enhanced EPO-CEIS algorithm** for optimal task scheduling
- **Fog-cloud computing support** with resource optimization
- **Automated performance analysis** and visualization
- **Scalable architecture** for large-scale IIoT deployments

## Performance Results

The Enhanced EPO-CEIS algorithm demonstrates superior performance:

- **20.8% lower cost** compared to Genetic Algorithm
- **44.5% lower cost** compared to First-Fit heuristic
- **90.55% deadline hit rate** (36% improvement over baselines)
- **Consistent performance** across varying workload sizes

## Quick Start

### Prerequisites
- Java 8+
- Python 3.8+
- 4GB RAM minimum

### Installation
```bash
# Clone the repository
git clone https://github.com/yourusername/IIoT-Scheduler.git
cd IIoT-Scheduler

# Install Python dependencies
pip install -r requirements.txt
```

### Running the System
```bash
# Compile Java code
javac -cp "libs/*;src" src/evaluation/MainEvaluation.java

# Run evaluation
java -cp "libs/*;src" evaluation.MainEvaluation

# Generate visualizations
python analyze_results.py
```

## Project Structure

```
IIoT-Scheduler/
├── src/
│   ├── algorithms/          # Scheduling algorithms
│   ├── core/               # Core data structures
│   ├── evaluation/         # Evaluation framework
│   ├── simulation/         # Simulation components
│   └── utils/              # Utility functions
├── data/                   # Test scenarios and datasets
├── quick_results/          # Evaluation results
├── analysis_plots/         # Generated visualizations
└── test/                   # Test suites
```

## Algorithms

1. **Enhanced EPO-CEIS**: Primary algorithm with multi-objective optimization
2. **Genetic Algorithm**: Population-based evolutionary approach
3. **Particle Swarm Optimization**: Swarm intelligence method
4. **Min-Min Heuristic**: Greedy scheduling approach
5. **First-Fit Heuristic**: Simple resource allocation

## Results

The system generates comprehensive performance analysis including:
- Algorithm comparison charts
- Performance heatmaps
- Correlation analysis
- Statistical summaries
- Scalability analysis

All visualizations are saved in high-resolution (300 DPI) PNG format.

## Documentation

For detailed information, see [PROJECT_REPORT.md](PROJECT_REPORT.md) which includes:
- Complete system architecture
- Implementation details
- Performance analysis
- Technical specifications
- Future enhancements

## Citation

If you use this project in your research, please cite:

```bibtex
@software{iiot_scheduler,
  title={IIoT Task Scheduling System},
  author={Your Name},
  year={2024},
  url={https://github.com/yourusername/IIoT-Scheduler}
}
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Contact

For questions and support, please open an issue on GitHub.