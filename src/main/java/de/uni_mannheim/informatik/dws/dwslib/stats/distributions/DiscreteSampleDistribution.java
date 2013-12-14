package de.uni_mannheim.informatik.dws.dwslib.stats.distributions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

/**
 * Implements a distribution generated from sampled values.
 *
 * @author Daniel Fleischhacker
 */
public class DiscreteSampleDistribution<T extends Comparable<T>> extends DiscreteDistribution<T> {
    public static final Logger log = LoggerFactory.getLogger(DiscreteSampleDistribution.class);
    private TreeMap<T, Integer> frequencies;
    private int totalFrequency;

    /**
     * Creates a discrete sample distribution from the given set of samples without bucketing the samples.
     *
     * @param samples samples to create distribution from
     */
    public DiscreteSampleDistribution(Collection<T> samples) {
        frequencies = new TreeMap<T, Integer>();
        totalFrequency = 0;
        for (T val : samples) {
            totalFrequency++;
            if (frequencies.containsKey(val)) {
                frequencies.put(val, frequencies.get(val) + 1);
            }
            else {
                frequencies.put(val, 1);
            }
        }
        log.debug("Initialized new distribution with {} samples", totalFrequency);
    }

    /**
     * Creates a discrete sample distribution from the given set of samples by building the given number of equal-sized
     * buckets and assigning each sample to one of these buckets.
     *
     * @param value
     * @return
     */

    @Override
    public double getProbability(T value) {
        if (frequencies.containsKey(value)) {
            return frequencies.get(value) / (double) totalFrequency;
        }
        return 0d;
    }

    @Override
    public double getProbability(T a, T b) {
        int valuesInInterval = 0;
        for (Integer val : frequencies.subMap(a, true, b, true).values()) {
            valuesInInterval += val;
        }
        return valuesInInterval / (double) totalFrequency;
    }

    @Override
    public Set<T> getValueRange() {
        return frequencies.navigableKeySet();
    }

    @Override
    public double getSmoothedProbability(T value, double alpha) {
        if (frequencies.containsKey(value)) {
            return (1d + frequencies.get(value)) / (frequencies.size() * alpha + (double) totalFrequency);
        }
        return 1d/(frequencies.size() * alpha + (double) totalFrequency);
    }
}
