package de.uni_mannheim.informatik.dws.dwslib.stats;

import de.uni_mannheim.informatik.dws.dwslib.stats.distributions.DoubleBucketedDiscreteDistribution;

import java.util.Arrays;

/**
 * Implements the computation of divergence measures like the Kullback Leibler divergence.
 *
 * @author Daniel Fleischhacker
 */
public class Divergence {
    private final static double VALUE_LOG2 = Math.log(2);

    /**
     * Computes the Kullback Leibler divergence between two discrete distributions in bits/Shannon (i.e., using the
     * logarithm with base 2).
     * <p/>
     * Implemented according to the definition on
     * <a href="http://en.wikipedia.org/wiki/Kullback%E2%80%93Leibler_divergence">Wikipedia</a>.
     *
     * @param p the first distribution
     * @param q the other distribution
     * @return Kullback Leibler divergence between the two given distributions in Bit resp. Shannon
     */
    public static <T extends Comparable<T>> double getKLDivergence(DoubleBucketedDiscreteDistribution p,
                                                                   DoubleBucketedDiscreteDistribution q) {
        if (p.getNumberOfBuckets() != q.getNumberOfBuckets()) {
            throw new IllegalArgumentException("Given distributions must both have the same number of buckets");
        }
        double sum = 0;
        for (int i = 0; i < p.getNumberOfBuckets(); i++) {
            double probP = p.getSmoothedProbability(i);
            double probQ = q.getSmoothedProbability(i);

            sum += log2(probP / probQ) * probP;
        }

        return sum;
    }

    /**
     * Returns the value of the symmetric KL divergence. This is the sum of the values of the KL divergence in either
     * direction.
     *
     * @
     */
    public static <T extends Comparable<T>> double getSymmetricKLDivergence(DoubleBucketedDiscreteDistribution p,
                                                                            DoubleBucketedDiscreteDistribution q) {
        return getKLDivergence(p, q) + getKLDivergence(q, p);
    }


    /**
     * Computes the logarithm to base 2 of x.
     *
     * @param x value to compute log 2 for
     * @return logarithm of 2 for base 2
     */
    private static double log2(double x) {
        return Math.log(x) / VALUE_LOG2;
    }

    public static void main(String[] args) throws Exception {
        DoubleBucketedDiscreteDistribution d1 = new DoubleBucketedDiscreteDistribution(
                Arrays.asList(1d, 2d, 3d, 4d, 5d, 6d, 7d), 10);
        DoubleBucketedDiscreteDistribution d2 = new DoubleBucketedDiscreteDistribution(
                Arrays.asList(99999999d), 10);
        System.out.println(getKLDivergence(d1, d2));

    }
}

