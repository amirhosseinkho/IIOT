package evaluation;

import java.util.*;

/**
 * Statistical Analysis Tools for Algorithm Performance Comparison
 * Implements t-tests, ANOVA, and other statistical methods for significance testing
 */
public class StatisticalAnalysis {
    
    private static final double DEFAULT_ALPHA = 0.05;
    
    /**
     * Perform two-sample t-test (Welch's t-test for unequal variances)
     * H0: μ1 = μ2 (means are equal)
     * H1: μ1 ≠ μ2 (means are different)
     * 
     * @param sample1 First sample
     * @param sample2 Second sample
     * @return p-value
     */
    public double performTTest(List<Double> sample1, List<Double> sample2) {
        if (sample1.size() < 2 || sample2.size() < 2) {
            throw new IllegalArgumentException("Samples must have at least 2 observations");
        }
        
        double mean1 = calculateMean(sample1);
        double mean2 = calculateMean(sample2);
        double var1 = calculateVariance(sample1, mean1);
        double var2 = calculateVariance(sample2, mean2);
        
        int n1 = sample1.size();
        int n2 = sample2.size();
        
        // Welch's t-statistic
        double tStatistic = (mean1 - mean2) / Math.sqrt(var1/n1 + var2/n2);
        
        // Degrees of freedom (Welch-Satterthwaite equation)
        double df = Math.pow(var1/n1 + var2/n2, 2) / 
                   (Math.pow(var1/n1, 2)/(n1-1) + Math.pow(var2/n2, 2)/(n2-1));
        
        // Calculate p-value (two-tailed)
        return 2 * (1 - studentTCDF(Math.abs(tStatistic), df));
    }
    
    /**
     * Perform one-way ANOVA
     * H0: All group means are equal
     * H1: At least one group mean is different
     * 
     * @param groups List of groups (each group is a list of observations)
     * @return ANOVA result containing F-statistic and p-value
     */
    public ANOVAResult performOneWayANOVA(List<List<Double>> groups) {
        if (groups.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 groups for ANOVA");
        }
        
        int k = groups.size(); // number of groups
        int n = groups.stream().mapToInt(List::size).sum(); // total observations
        
        if (n < k + 1) {
            throw new IllegalArgumentException("Not enough observations for ANOVA");
        }
        
        // Calculate group means and overall mean
        List<Double> groupMeans = new ArrayList<>();
        List<Integer> groupSizes = new ArrayList<>();
        double overallSum = 0.0;
        
        for (List<Double> group : groups) {
            double groupMean = calculateMean(group);
            groupMeans.add(groupMean);
            groupSizes.add(group.size());
            overallSum += group.stream().mapToDouble(Double::doubleValue).sum();
        }
        
        double overallMean = overallSum / n;
        
        // Calculate Sum of Squares Between (SSB) and Within (SSW)
        double ssb = 0.0;
        double ssw = 0.0;
        
        for (int i = 0; i < k; i++) {
            List<Double> group = groups.get(i);
            double groupMean = groupMeans.get(i);
            int groupSize = groupSizes.get(i);
            
            // Between-group sum of squares
            ssb += groupSize * Math.pow(groupMean - overallMean, 2);
            
            // Within-group sum of squares
            for (double value : group) {
                ssw += Math.pow(value - groupMean, 2);
            }
        }
        
        // Calculate Mean Squares
        double msb = ssb / (k - 1);
        double msw = ssw / (n - k);
        
        // F-statistic
        double fStatistic = msb / msw;
        
        // p-value using F-distribution
        double pValue = 1 - fCDF(fStatistic, k - 1, n - k);
        
        return new ANOVAResult(fStatistic, pValue, k - 1, n - k, ssb, ssw);
    }
    
    /**
     * Perform pairwise comparisons with Bonferroni correction
     */
    public PairwiseComparisonResult performPairwiseComparisons(List<List<Double>> groups, 
                                                              List<String> groupNames) {
        if (groups.size() != groupNames.size()) {
            throw new IllegalArgumentException("Number of groups must match number of group names");
        }
        
        int numGroups = groups.size();
        int numComparisons = numGroups * (numGroups - 1) / 2;
        double bonferroniAlpha = DEFAULT_ALPHA / numComparisons;
        
        List<ComparisonResult> comparisons = new ArrayList<>();
        
        for (int i = 0; i < numGroups - 1; i++) {
            for (int j = i + 1; j < numGroups; j++) {
                double pValue = performTTest(groups.get(i), groups.get(j));
                boolean significant = pValue < bonferroniAlpha;
                
                comparisons.add(new ComparisonResult(
                    groupNames.get(i), 
                    groupNames.get(j), 
                    pValue, 
                    significant,
                    calculateMean(groups.get(i)),
                    calculateMean(groups.get(j))
                ));
            }
        }
        
        return new PairwiseComparisonResult(comparisons, bonferroniAlpha);
    }
    
    /**
     * Calculate effect size (Cohen's d) between two groups
     */
    public double calculateCohenD(List<Double> group1, List<Double> group2) {
        double mean1 = calculateMean(group1);
        double mean2 = calculateMean(group2);
        
        double var1 = calculateVariance(group1, mean1);
        double var2 = calculateVariance(group2, mean2);
        
        // Pooled standard deviation
        double pooledSD = Math.sqrt(((group1.size() - 1) * var1 + (group2.size() - 1) * var2) / 
                                   (group1.size() + group2.size() - 2));
        
        return (mean1 - mean2) / pooledSD;
    }
    
    /**
     * Calculate descriptive statistics for a sample
     */
    public DescriptiveStatistics calculateDescriptiveStats(List<Double> sample) {
        if (sample.isEmpty()) {
            throw new IllegalArgumentException("Sample cannot be empty");
        }
        
        double mean = calculateMean(sample);
        double variance = calculateVariance(sample, mean);
        double stdDev = Math.sqrt(variance);
        
        List<Double> sorted = new ArrayList<>(sample);
        Collections.sort(sorted);
        
        double median = calculateMedian(sorted);
        double min = sorted.get(0);
        double max = sorted.get(sorted.size() - 1);
        double q1 = calculateQuantile(sorted, 0.25);
        double q3 = calculateQuantile(sorted, 0.75);
        
        return new DescriptiveStatistics(mean, median, stdDev, variance, min, max, q1, q3, sample.size());
    }
    
    // Helper methods
    
    private double calculateMean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    private double calculateVariance(List<Double> values, double mean) {
        double sumSquaredDiff = values.stream()
            .mapToDouble(x -> Math.pow(x - mean, 2))
            .sum();
        return sumSquaredDiff / (values.size() - 1); // Sample variance
    }
    
    private double calculateMedian(List<Double> sortedValues) {
        int n = sortedValues.size();
        if (n % 2 == 0) {
            return (sortedValues.get(n/2 - 1) + sortedValues.get(n/2)) / 2.0;
        } else {
            return sortedValues.get(n/2);
        }
    }
    
    private double calculateQuantile(List<Double> sortedValues, double quantile) {
        if (quantile < 0 || quantile > 1) {
            throw new IllegalArgumentException("Quantile must be between 0 and 1");
        }
        
        int n = sortedValues.size();
        double index = quantile * (n - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);
        
        if (lowerIndex == upperIndex) {
            return sortedValues.get(lowerIndex);
        } else {
            double weight = index - lowerIndex;
            return sortedValues.get(lowerIndex) * (1 - weight) + sortedValues.get(upperIndex) * weight;
        }
    }
    
    /**
     * Approximate Student's t-distribution CDF using numerical integration
     */
    private double studentTCDF(double t, double df) {
        if (df <= 0) return 0.5;
        if (t == 0) return 0.5;
        if (Double.isInfinite(t)) return t > 0 ? 1.0 : 0.0;
        
        // For large df, approximate with normal distribution
        if (df >= 30) {
            return normalCDF(t);
        }
        
        // Simple approximation for small df
        // In practice, you would use a more accurate implementation
        double x = t / Math.sqrt(df);
        return 0.5 + 0.5 * Math.signum(t) * Math.min(0.5, Math.abs(x) / (1 + Math.abs(x)));
    }
    
    /**
     * Standard normal distribution CDF approximation
     */
    private double normalCDF(double z) {
        return 0.5 * (1 + erf(z / Math.sqrt(2)));
    }
    
    /**
     * Error function approximation
     */
    private double erf(double x) {
        // Abramowitz and Stegun approximation
        double a1 =  0.254829592;
        double a2 = -0.284496736;
        double a3 =  1.421413741;
        double a4 = -1.453152027;
        double a5 =  1.061405429;
        double p  =  0.3275911;
        
        int sign = x < 0 ? -1 : 1;
        x = Math.abs(x);
        
        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
        
        return sign * y;
    }
    
    /**
     * F-distribution CDF approximation
     */
    private double fCDF(double f, int df1, int df2) {
        if (f <= 0) return 0.0;
        if (Double.isInfinite(f)) return 1.0;
        
        // Simple approximation - in practice use more accurate implementation
        double x = (df1 * f) / (df1 * f + df2);
        return incompleteBeta(x, df1/2.0, df2/2.0);
    }
    
    /**
     * Incomplete beta function approximation
     */
    private double incompleteBeta(double x, double a, double b) {
        if (x <= 0) return 0.0;
        if (x >= 1) return 1.0;
        
        // Simple approximation
        return Math.pow(x, a) * Math.pow(1-x, b) / (a * beta(a, b));
    }
    
    /**
     * Beta function approximation
     */
    private double beta(double a, double b) {
        return Math.exp(logGamma(a) + logGamma(b) - logGamma(a + b));
    }
    
    /**
     * Log gamma function approximation
     */
    private double logGamma(double x) {
        // Stirling's approximation
        if (x < 1) return logGamma(x + 1) - Math.log(x);
        return (x - 0.5) * Math.log(x) - x + 0.5 * Math.log(2 * Math.PI);
    }
    
    // Result classes
    
    public static class ANOVAResult {
        public final double fStatistic;
        public final double pValue;
        public final int dfBetween;
        public final int dfWithin;
        public final double ssBetween;
        public final double ssWithin;
        
        public ANOVAResult(double fStatistic, double pValue, int dfBetween, int dfWithin, 
                          double ssBetween, double ssWithin) {
            this.fStatistic = fStatistic;
            this.pValue = pValue;
            this.dfBetween = dfBetween;
            this.dfWithin = dfWithin;
            this.ssBetween = ssBetween;
            this.ssWithin = ssWithin;
        }
        
        public boolean isSignificant(double alpha) {
            return pValue < alpha;
        }
    }
    
    public static class ComparisonResult {
        public final String group1;
        public final String group2;
        public final double pValue;
        public final boolean significant;
        public final double mean1;
        public final double mean2;
        
        public ComparisonResult(String group1, String group2, double pValue, boolean significant,
                               double mean1, double mean2) {
            this.group1 = group1;
            this.group2 = group2;
            this.pValue = pValue;
            this.significant = significant;
            this.mean1 = mean1;
            this.mean2 = mean2;
        }
    }
    
    public static class PairwiseComparisonResult {
        public final List<ComparisonResult> comparisons;
        public final double adjustedAlpha;
        
        public PairwiseComparisonResult(List<ComparisonResult> comparisons, double adjustedAlpha) {
            this.comparisons = comparisons;
            this.adjustedAlpha = adjustedAlpha;
        }
    }
    
    public static class DescriptiveStatistics {
        public final double mean;
        public final double median;
        public final double standardDeviation;
        public final double variance;
        public final double minimum;
        public final double maximum;
        public final double q1;
        public final double q3;
        public final int sampleSize;
        
        public DescriptiveStatistics(double mean, double median, double standardDeviation,
                                   double variance, double minimum, double maximum,
                                   double q1, double q3, int sampleSize) {
            this.mean = mean;
            this.median = median;
            this.standardDeviation = standardDeviation;
            this.variance = variance;
            this.minimum = minimum;
            this.maximum = maximum;
            this.q1 = q1;
            this.q3 = q3;
            this.sampleSize = sampleSize;
        }
        
        @Override
        public String toString() {
            return String.format("Mean: %.3f, Median: %.3f, SD: %.3f, Min: %.3f, Max: %.3f, N: %d",
                mean, median, standardDeviation, minimum, maximum, sampleSize);
        }
    }
}
