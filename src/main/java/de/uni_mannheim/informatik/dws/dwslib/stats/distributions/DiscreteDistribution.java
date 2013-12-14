package de.uni_mannheim.informatik.dws.dwslib.stats.distributions;

import java.util.Set;

/**
 * Represents a discrete distribution. This class is abstract since the actual functionality is provided by its sub
 * classes like {@link DiscreteSampleDistribution}.
 *
 * @author Daniel Fleischhacker
 */
public abstract class DiscreteDistribution<T extends Comparable<T>> implements Distribution<T> {
    @Override
    public abstract double getProbability(T value);

    @Override
    public abstract double getProbability(T a, T b);

    /**
     * Returns the set of all values a random variable with this distribution might have. This means all values
     * with a non-zero probability.
     *
     * @return set of all values having a non-zero probability according to this distribution
     */
    public abstract Set<T> getValueRange();

    /**
     * Returns the additive smoothed probability of a random variable with this distribution to produce the given value.
     *
     * @param value value to get this distribution's additive smoothed probability for
     * @param alpha the alpha parameter for smoothing
     */
    public abstract double getSmoothedProbability(T value, double alpha);
}
