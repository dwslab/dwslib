package de.uni_mannheim.informatik.dws.dwslib.stats.distributions;

/**
 * Defines an interface for implementing distance metrics.
 *
 * @author Daniel Fleischhacker
 */
public abstract class DistanceMetric<T> {
    /**
     * Returns the distance between points a and b.
     *
     * @param a the starting point for the distance
     * @param b the ending point for the distance
     * @return distance between a and b
     */
    public abstract double getDistance(T a, T b);

    public static class OneDimensionalNumericDistance<T extends Number> extends DistanceMetric<T> {

        @Override
        public double getDistance(T a, T b) {
            if (a instanceof Integer && b instanceof Integer) {
                return Math.abs((Integer) a - (Integer) b);
            }
            if (a instanceof Double && b instanceof Double) {
                return Math.abs((Double) a - (Double) b);
            }
            if (a instanceof Float && b instanceof Float) {
                return Math.abs((Float) a - (Float) b);
            }
            if (a instanceof Long && b instanceof Long) {
                return Math.abs((Long) a - (Long) b);
            }

            throw new IllegalArgumentException("Distance matric not implemented for type " + a.getClass().getName());
        }
    }

}
