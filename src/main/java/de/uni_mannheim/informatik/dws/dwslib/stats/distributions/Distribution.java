package de.uni_mannheim.informatik.dws.dwslib.stats.distributions;

/**
 * Common interface for statistical distributions.
 *
 * @author Daniel Fleischhacker
 */
public interface Distribution<T extends Comparable<T>> {
    /**
     * Returns the probability of a random variable having this distribution to produce the given value. Note, that
     * this method will always return 0 for continuous distributions for which you should resort to the alternative
     * method {@link #getProbability(Comparable, Comparable)}.
     *
     * @param value value to get this distribution's probability for
     * @return probability the given value is produced by a random variable having this distribution
     */
    public double getProbability(T value);

    /**
     * Returns the probability of a random variable having this distribution to produce a value in the interval defined
     * by the the lower bound {@code a} and the upper bound {@code b} (both inclusive).
     *
     * @param a lower bound of the interval to get probability for (inclusive)
     * @param b uppoer bound of the interval to get probability for (inclusive)
     * @return probability of a random variable having this distribution to produce a value in this interval
     */
    public double getProbability(T a, T b);
}
