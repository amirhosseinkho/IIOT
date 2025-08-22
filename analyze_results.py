#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
IIoT Scheduler Results Analysis and Visualization
Analyzes CSV results and creates comprehensive charts
"""

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
from pathlib import Path
import warnings
warnings.filterwarnings('ignore')

# Set style for better looking plots
plt.style.use('seaborn-v0_8')
sns.set_palette("husl")

class IIoTSchedulerAnalyzer:
    def __init__(self, results_dir="quick_results"):
        self.results_dir = Path(results_dir)
        self.results = {}
        self.load_data()
        
    def load_data(self):
        """Load all CSV files from results directory"""
        print("📊 Loading results data...")
        
        csv_files = list(self.results_dir.glob("*.csv"))
        if not csv_files:
            print("❌ No CSV files found in", self.results_dir)
            return
            
        for csv_file in csv_files:
            try:
                df = pd.read_csv(csv_file)
                metric_name = csv_file.stem
                self.results[metric_name] = df
                print(f"  ✅ Loaded {metric_name}: {len(df)} records")
            except Exception as e:
                print(f"  ❌ Error loading {csv_file}: {e}")
    
    def create_comprehensive_analysis(self):
        """Create comprehensive analysis and visualizations"""
        if not self.results:
            print("❌ No data to analyze")
            return
            
        print("\n🎨 Creating comprehensive analysis...")
        
        # Create output directory for plots
        plots_dir = Path("analysis_plots")
        plots_dir.mkdir(exist_ok=True)
        
        # 1. Algorithm Performance Comparison
        self.plot_algorithm_comparison(plots_dir)
        
        # 2. Scenario Analysis
        self.plot_scenario_analysis(plots_dir)
        
        # 3. Scalability Analysis
        self.plot_scalability_analysis(plots_dir)
        
        # 4. Correlation Analysis
        self.plot_correlation_analysis(plots_dir)
        
        # 5. Performance Heatmaps
        self.plot_performance_heatmaps(plots_dir)
        
        # 6. Statistical Summary
        self.generate_statistical_summary(plots_dir)
        
        print(f"\n✅ Analysis completed! Plots saved to: {plots_dir}")
    
    def plot_algorithm_comparison(self, plots_dir):
        """Compare performance of different algorithms"""
        print("  📈 Creating algorithm comparison plots...")
        
        if 'comprehensive_results' not in self.results:
            return
            
        df = self.results['comprehensive_results']
        
        # Create subplots for different metrics
        fig, axes = plt.subplots(2, 3, figsize=(18, 12))
        fig.suptitle('IIoT Scheduler Algorithm Performance Comparison', fontsize=16, fontweight='bold')
        
        metrics = ['TotalCost', 'Makespan', 'DeadlineHitRate', 'ExecutionTime', 'EnergyConsumption', 'FogUtilization']
        titles = ['Total Cost ($)', 'Makespan (s)', 'Deadline Hit Rate', 'Execution Time (ms)', 'Energy (Wh)', 'Fog Utilization']
        
        for i, (metric, title) in enumerate(zip(metrics, titles)):
            row, col = i // 3, i % 3
            ax = axes[row, col]
            
            # Box plot for each algorithm
            sns.boxplot(data=df, x='Algorithm', y=metric, ax=ax)
            ax.set_title(title, fontweight='bold')
            ax.set_xticklabels(ax.get_xticklabels(), rotation=45, ha='right')
            ax.grid(True, alpha=0.3)
        
        plt.tight_layout()
        plt.savefig(plots_dir / 'algorithm_comparison.png', dpi=300, bbox_inches='tight')
        plt.close()
        
        # Bar chart for average performance
        fig, ax = plt.subplots(figsize=(14, 8))
        
        avg_performance = df.groupby('Algorithm')[['TotalCost', 'Makespan', 'DeadlineHitRate']].mean()
        
        x = np.arange(len(avg_performance.index))
        width = 0.25
        
        ax.bar(x - width, avg_performance['TotalCost'], width, label='Total Cost', alpha=0.8)
        ax.bar(x, avg_performance['Makespan'], width, label='Makespan', alpha=0.8)
        ax.bar(x + width, avg_performance['DeadlineHitRate'], width, label='Deadline Hit Rate', alpha=0.8)
        
        ax.set_xlabel('Algorithm', fontweight='bold')
        ax.set_ylabel('Normalized Performance', fontweight='bold')
        ax.set_title('Average Algorithm Performance (Normalized)', fontweight='bold')
        ax.set_xticks(x)
        ax.set_xticklabels(avg_performance.index, rotation=45, ha='right')
        ax.legend()
        ax.grid(True, alpha=0.3)
        
        plt.tight_layout()
        plt.savefig(plots_dir / 'average_performance.png', dpi=300, bbox_inches='tight')
        plt.close()
    
    def plot_scenario_analysis(self, plots_dir):
        """Analyze performance across different scenarios"""
        print("  🌍 Creating scenario analysis plots...")
        
        if 'comprehensive_results' not in self.results:
            return
            
        df = self.results['comprehensive_results']
        
        # Scenario performance comparison
        fig, axes = plt.subplots(2, 2, figsize=(16, 12))
        fig.suptitle('Performance Analysis Across Scenarios', fontsize=16, fontweight='bold')
        
        # 1. Total Cost by Scenario
        sns.boxplot(data=df, x='Scenario', y='TotalCost', ax=axes[0, 0])
        axes[0, 0].set_title('Total Cost by Scenario', fontweight='bold')
        axes[0, 0].set_xticklabels(axes[0, 0].get_xticklabels(), rotation=45, ha='right')
        
        # 2. Makespan by Scenario
        sns.boxplot(data=df, x='Scenario', y='Makespan', ax=axes[0, 1])
        axes[0, 1].set_title('Makespan by Scenario', fontweight='bold')
        axes[0, 1].set_xticklabels(axes[0, 1].get_xticklabels(), rotation=45, ha='right')
        
        # 3. Deadline Hit Rate by Scenario
        sns.boxplot(data=df, x='Scenario', y='DeadlineHitRate', ax=axes[1, 0])
        axes[1, 0].set_title('Deadline Hit Rate by Scenario', fontweight='bold')
        axes[1, 0].set_xticklabels(axes[1, 0].get_xticklabels(), rotation=45, ha='right')
        
        # 4. Algorithm vs Scenario heatmap
        pivot_table = df.pivot_table(values='TotalCost', index='Algorithm', columns='Scenario', aggfunc='mean')
        sns.heatmap(pivot_table, annot=True, fmt='.3f', cmap='YlOrRd', ax=axes[1, 1])
        axes[1, 1].set_title('Total Cost: Algorithm vs Scenario', fontweight='bold')
        
        plt.tight_layout()
        plt.savefig(plots_dir / 'scenario_analysis.png', dpi=300, bbox_inches='tight')
        plt.close()
    
    def plot_scalability_analysis(self, plots_dir):
        """Analyze scalability performance"""
        print("  📊 Creating scalability analysis plots...")
        
        if 'comprehensive_results' not in self.results:
            return
            
        df = self.results['comprehensive_results']
        scalability_df = df[df['Scenario'] == 'Scalability']
        
        if len(scalability_df) == 0:
            print("    ⚠️  No scalability data found")
            return
        
        fig, axes = plt.subplots(2, 2, figsize=(16, 12))
        fig.suptitle('Scalability Analysis', fontsize=16, fontweight='bold')
        
        # 1. Performance vs Task Count
        for algorithm in scalability_df['Algorithm'].unique():
            alg_data = scalability_df[scalability_df['Algorithm'] == algorithm]
            axes[0, 0].plot(alg_data['TaskCount'], alg_data['TotalCost'], 
                           marker='o', label=algorithm, linewidth=2)
        
        axes[0, 0].set_xlabel('Number of Tasks', fontweight='bold')
        axes[0, 0].set_ylabel('Total Cost ($)', fontweight='bold')
        axes[0, 0].set_title('Cost vs Task Count', fontweight='bold')
        axes[0, 0].legend()
        axes[0, 0].grid(True, alpha=0.3)
        
        # 2. Performance vs Node Count
        for algorithm in scalability_df['Algorithm'].unique():
            alg_data = scalability_df[scalability_df['Algorithm'] == algorithm]
            axes[0, 1].plot(alg_data['NodeCount'], alg_data['TotalCost'], 
                           marker='s', label=algorithm, linewidth=2)
        
        axes[0, 1].set_xlabel('Number of Nodes', fontweight='bold')
        axes[0, 1].set_ylabel('Total Cost ($)', fontweight='bold')
        axes[0, 1].set_title('Cost vs Node Count', fontweight='bold')
        axes[0, 1].legend()
        axes[0, 1].grid(True, alpha=0.3)
        
        # 3. Execution Time vs Task Count
        for algorithm in scalability_df['Algorithm'].unique():
            alg_data = scalability_df[scalability_df['Algorithm'] == algorithm]
            axes[1, 0].plot(alg_data['TaskCount'], alg_data['ExecutionTime'], 
                           marker='^', label=algorithm, linewidth=2)
        
        axes[1, 0].set_xlabel('Number of Tasks', fontweight='bold')
        axes[1, 0].set_ylabel('Execution Time (ms)', fontweight='bold')
        axes[1, 0].set_title('Execution Time vs Task Count', fontweight='bold')
        axes[1, 0].legend()
        axes[1, 0].grid(True, alpha=0.3)
        
        # 4. 3D scatter plot: Tasks vs Nodes vs Cost
        ax3d = fig.add_subplot(2, 2, 4, projection='3d')
        for algorithm in scalability_df['Algorithm'].unique():
            alg_data = scalability_df[scalability_df['Algorithm'] == algorithm]
            ax3d.scatter(alg_data['TaskCount'], alg_data['NodeCount'], alg_data['TotalCost'], 
                        label=algorithm, s=50)
        
        ax3d.set_xlabel('Tasks', fontweight='bold')
        ax3d.set_ylabel('Nodes', fontweight='bold')
        ax3d.set_zlabel('Cost ($)', fontweight='bold')
        ax3d.set_title('3D: Tasks vs Nodes vs Cost', fontweight='bold')
        ax3d.legend()
        
        plt.tight_layout()
        plt.savefig(plots_dir / 'scalability_analysis.png', dpi=300, bbox_inches='tight')
        plt.close()
    
    def plot_correlation_analysis(self, plots_dir):
        """Analyze correlations between different metrics"""
        print("  🔗 Creating correlation analysis plots...")
        
        if 'comprehensive_results' not in self.results:
            return
            
        df = self.results['comprehensive_results']
        
        # Select numeric columns for correlation
        numeric_cols = ['TaskCount', 'NodeCount', 'TotalCost', 'Makespan', 
                       'DeadlineHitRate', 'ExecutionTime', 'EnergyConsumption', 
                       'FogUtilization', 'CloudUtilization']
        
        correlation_df = df[numeric_cols].corr()
        
        # Correlation heatmap
        plt.figure(figsize=(12, 10))
        sns.heatmap(correlation_df, annot=True, cmap='coolwarm', center=0, 
                   square=True, fmt='.3f', cbar_kws={'shrink': 0.8})
        plt.title('Metric Correlation Matrix', fontsize=16, fontweight='bold')
        plt.tight_layout()
        plt.savefig(plots_dir / 'correlation_matrix.png', dpi=300, bbox_inches='tight')
        plt.close()
        
        # Scatter plot matrix for key metrics
        key_metrics = ['TotalCost', 'Makespan', 'DeadlineHitRate', 'ExecutionTime']
        scatter_df = df[key_metrics].dropna()
        
        if len(scatter_df) > 0:
            sns.pairplot(scatter_df, diag_kind='kde', plot_kws={'alpha': 0.6})
            plt.suptitle('Key Metrics Scatter Plot Matrix', y=1.02, fontsize=16, fontweight='bold')
            plt.savefig(plots_dir / 'scatter_matrix.png', dpi=300, bbox_inches='tight')
            plt.close()
    
    def plot_performance_heatmaps(self, plots_dir):
        """Create performance heatmaps"""
        print("  🗺️  Creating performance heatmaps...")
        
        if 'comprehensive_results' not in self.results:
            return
            
        df = self.results['comprehensive_results']
        
        # Create pivot tables for different metrics
        metrics = ['TotalCost', 'Makespan', 'DeadlineHitRate', 'ExecutionTime']
        titles = ['Total Cost ($)', 'Makespan (s)', 'Deadline Hit Rate', 'Execution Time (ms)']
        
        fig, axes = plt.subplots(2, 2, figsize=(16, 12))
        fig.suptitle('Performance Heatmaps: Algorithm vs Scenario', fontsize=16, fontweight='bold')
        
        for i, (metric, title) in enumerate(zip(metrics, titles)):
            row, col = i // 2, i % 2
            ax = axes[row, col]
            
            pivot_table = df.pivot_table(values=metric, index='Algorithm', 
                                       columns='Scenario', aggfunc='mean')
            
            sns.heatmap(pivot_table, annot=True, fmt='.3f', cmap='YlOrRd', 
                       ax=ax, cbar_kws={'shrink': 0.8})
            ax.set_title(title, fontweight='bold')
        
        plt.tight_layout()
        plt.savefig(plots_dir / 'performance_heatmaps.png', dpi=300, bbox_inches='tight')
        plt.close()
    
    def generate_statistical_summary(self, plots_dir):
        """Generate comprehensive statistical summary"""
        print("  📋 Generating statistical summary...")
        
        if 'comprehensive_results' not in self.results:
            return
            
        df = self.results['comprehensive_results']
        
        # Create summary statistics
        summary_stats = df.groupby('Algorithm').agg({
            'TotalCost': ['mean', 'std', 'min', 'max'],
            'Makespan': ['mean', 'std', 'min', 'max'],
            'DeadlineHitRate': ['mean', 'std', 'min', 'max'],
            'ExecutionTime': ['mean', 'std', 'min', 'max']
        }).round(4)
        
        # Save summary to CSV
        summary_file = plots_dir / 'statistical_summary.csv'
        summary_stats.to_csv(summary_file)
        print(f"    📄 Statistical summary saved to: {summary_file}")
        
        # Create summary visualization
        fig, axes = plt.subplots(2, 2, figsize=(16, 12))
        fig.suptitle('Statistical Summary by Algorithm', fontsize=16, fontweight='bold')
        
        metrics = ['TotalCost', 'Makespan', 'DeadlineHitRate', 'ExecutionTime']
        titles = ['Total Cost ($)', 'Makespan (s)', 'Deadline Hit Rate', 'Execution Time (ms)']
        
        for i, (metric, title) in enumerate(zip(metrics, titles)):
            row, col = i // 2, i % 2
            ax = axes[row, col]
            
            # Box plot with individual points
            sns.boxplot(data=df, x='Algorithm', y=metric, ax=ax)
            sns.stripplot(data=df, x='Algorithm', y=metric, color='red', 
                         alpha=0.3, size=3, ax=ax)
            
            ax.set_title(title, fontweight='bold')
            ax.set_xticklabels(ax.get_xticklabels(), rotation=45, ha='right')
            ax.grid(True, alpha=0.3)
        
        plt.tight_layout()
        plt.savefig(plots_dir / 'statistical_summary.png', dpi=300, bbox_inches='tight')
        plt.close()
        
        # Print summary to console
        print("\n📊 STATISTICAL SUMMARY:")
        print("=" * 50)
        print(summary_stats)
        
        # Best performing algorithms
        print("\n🏆 BEST PERFORMING ALGORITHMS:")
        print("=" * 50)
        
        best_cost = df.groupby('Algorithm')['TotalCost'].mean().idxmin()
        best_makespan = df.groupby('Algorithm')['Makespan'].mean().idxmin()
        best_deadline = df.groupby('Algorithm')['DeadlineHitRate'].mean().idxmax()
        best_time = df.groupby('Algorithm')['ExecutionTime'].mean().idxmin()
        
        print(f"Lowest Cost: {best_cost}")
        print(f"Lowest Makespan: {best_makespan}")
        print(f"Highest Deadline Hit Rate: {best_deadline}")
        print(f"Fastest Execution: {best_time}")

def main():
    """Main function to run the analysis"""
    print("🚀 IIoT Scheduler Results Analyzer")
    print("=" * 40)
    
    # Create analyzer instance
    analyzer = IIoTSchedulerAnalyzer()
    
    # Run comprehensive analysis
    analyzer.create_comprehensive_analysis()
    
    print("\n🎉 Analysis completed successfully!")
    print("📁 Check the 'analysis_plots' directory for all visualizations")

if __name__ == "__main__":
    main()
