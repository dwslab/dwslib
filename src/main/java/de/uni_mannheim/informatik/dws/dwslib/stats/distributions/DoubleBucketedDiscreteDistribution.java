package de.uni_mannheim.informatik.dws.dwslib.stats.distributions;

import java.util.*;

/**
 * A bucketed discrete distribution containing double values.
 *
 * @author Daniel Fleischhacker
 */
public class DoubleBucketedDiscreteDistribution extends DiscreteDistribution<Double>{
    private TreeMap<Double,Integer> buckets;
    private double bucketSize;
    private int totalNum = 0;

    private DoubleBucketedDiscreteDistribution() { }

    /**
     * Creates a distribution from the given list of samples by sorting the samples into the given number of
     * equally sized buckets. The given number of buckets is created in the range between the lowest sample
     * value and the greatest sample value (both inclusive).
     *
     * @param samples            samples to create distribution from
     * @param numberOfBuckets    number of buckets to create
     */
    public DoubleBucketedDiscreteDistribution(List<Double> samples, int numberOfBuckets) throws Exception {
        if (samples.size() == 0) {
            throw new Exception("Unable to create distribution from empty sample list");
        }
        TreeMap<Double, Integer> map = new TreeMap<Double, Integer>();

        for (Double s : samples) {
            if (!map.containsKey(s)) {
                map.put(s, 1);
            }
            else {
                map.put(s, map.get(s) + 1);
            }
        }

        Double startValue = map.firstKey();
        Double endValue = map.lastKey();
        bucketSize = (endValue - startValue) / numberOfBuckets;

        buckets = new TreeMap<Double, Integer>();

        for (int i = 0; i < numberOfBuckets; i++) {
            double curBucketStart = startValue + i * bucketSize;
            double curBucketEnd = startValue + (i + 1) * bucketSize;
            int totalNumCurrentBucket = 0;
            Map<Double, Integer> subMap = map.subMap(curBucketStart, true, curBucketEnd, i == numberOfBuckets - 1);
            for (Integer v : subMap.values()) {
                totalNumCurrentBucket += v;
                totalNum += v;
            }
            buckets.put(curBucketStart, totalNumCurrentBucket);
//            System.out.println(String.format("[%f, %f) ==> %d", curBucketStart, curBucketEnd, totalNumCurrentBucket));
        }
    }

    @Override
    public double getProbability(Double value) {
        // search for matching bucket
        if (value < buckets.firstKey()) {
            return 0;
        }
        if (value == buckets.lastKey() + bucketSize) {
            return buckets.lastEntry().getValue() / (double) totalNum;
        }
        if (value > buckets.lastKey() + bucketSize) {
            return 0;
        }
        for (Double current : buckets.navigableKeySet()) {
            if (value < current + bucketSize) {
                return buckets.get(current) / (double)totalNum;
            }
        }

        return 0;
    }

    @Override
    public double getProbability(Double a, Double b) {
        // use floorKey/ceilingKey
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Set<Double> getValueRange() {
        return buckets.navigableKeySet();
    }

    @Override
    public double getSmoothedProbability(Double value, double alpha) {
        if (value < buckets.firstKey()) {
            return 1 / ((double)totalNum + buckets.size());
        }
        if (value == buckets.lastKey() + bucketSize) {
            return (1 + buckets.lastEntry().getValue()) / ((double) totalNum + buckets.size());
        }
        if (value > buckets.lastKey() + bucketSize) {
            return 0;
        }
        for (Double current : buckets.navigableKeySet()) {
            if (value < current + bucketSize) {
                return buckets.get(current) / ((double) totalNum + buckets.size());
            }
        }

        return 0;
    }

    /**
     * Returns this distribution but scaled to the given bounds.
     *
     * @param lower lower bound for new distribution
     * @param upper upper bound for new distribution
     * @return an (independent) version of this distribution scaled to the given range
     */
    public DoubleBucketedDiscreteDistribution getScaledDistribution(Double lower, Double upper) {
        double curLower = buckets.firstKey();
        double curUpper = buckets.lastKey() + bucketSize;
        double curSpan = curUpper - curLower;
        double desiredSpan = upper - lower;

        double translation = lower - curLower;
        double scale = desiredSpan / curSpan;

        DoubleBucketedDiscreteDistribution newDistribution = new DoubleBucketedDiscreteDistribution();
        newDistribution.totalNum = totalNum;
        newDistribution.buckets = new TreeMap<Double, Integer>();

        for (Map.Entry<Double, Integer> e : buckets.entrySet()) {
            Double newKey = ((e.getKey() - curLower) * scale) + translation;
            newDistribution.buckets.put(newKey, e.getValue());
        }

        newDistribution.bucketSize = bucketSize * curSpan / desiredSpan;

        return newDistribution;
    }

    public static void main(String[] args) throws Exception {
        List<Double> values = new ArrayList<Double>(
                Arrays.<Double>asList(new Double[]{1d, 0.5, 7d, 6.5, 3.9, 1d, 0d, 10d, 9.8, 7.1}));
        Collections.sort(values);

        System.out.println(values);

        DoubleBucketedDiscreteDistribution d = new DoubleBucketedDiscreteDistribution(values, 10);
        System.out.println("1.0 " + d.getProbability(1.0));
        System.out.println("2.0 " + d.getProbability(2.0));
        System.out.println("0.0 " + d.getProbability(0.0));
        System.out.println("71.0 " + d.getProbability(71.0));
        System.out.println("3.5 " + d.getProbability(3.5));
        System.out.println("9.8 " + d.getProbability(9.8));
        System.out.println("-1.0 " + d.getProbability(-1.0));
        System.out.println("10 " + d.getProbability(10d));
        d.getScaledDistribution(0d, 1d);
        System.out.println("1.0 " + d.getProbability(1.0));
    }
}
