package de.uni_mannheim.informatik.dws.dwslib.stats;

import de.uni_mannheim.informatik.dws.dwslib.stats.distributions.DiscreteDistribution;
import de.uni_mannheim.informatik.dws.dwslib.stats.distributions.DiscreteSampleDistribution;

import java.util.Arrays;
import java.util.TreeSet;

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
    public static <T extends Comparable<T>> double getKLDivergence(DiscreteDistribution<T> p,
                                                                   DiscreteDistribution<T> q) {

        TreeSet<T> joinedRanges = new TreeSet<T>(p.getValueRange());
        joinedRanges.addAll(q.getValueRange());

        double sum = 0;
        for (T val : joinedRanges) {
            double probP = p.getSmoothedProbability(val, 1);
            double probQ = q.getSmoothedProbability(val, 1);

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
    public static <T extends Comparable<T>> double getSymmetricKLDivergence(DiscreteDistribution<T> p,
                                                                            DiscreteDistribution<T> q) {
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

    public static void main(String[] args) {
        DiscreteSampleDistribution<Integer> d1 = new DiscreteSampleDistribution<Integer>(
                Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        DiscreteSampleDistribution<Integer> d2 = new DiscreteSampleDistribution<Integer>(
                Arrays.asList(99999999));
        System.out.println(getKLDivergence(d1, d2));

    }
}

