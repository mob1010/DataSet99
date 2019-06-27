package de.unijena.bioinf.ChemistryBase.ms;

public class SimplePeak implements Peak {
    protected static final double DELTA = 1e-8;

    protected double mass;
    protected double intensity;

    public SimplePeak(Peak x) {
        this(x.getMass(), x.getIntensity());
    }

    public SimplePeak(double mass, double intensity) {
        super();
        this.mass = mass;
        this.intensity = intensity;
    }

    public double getMass() {
        return mass;
    }

    public double getIntensity() {
        return intensity;
    }

    @Override
    public int compareTo(Peak o) {
        return Double.compare(mass, o.getMass());
    }

    @Override
    public Peak clone() {
        try {
            return (Peak) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof SimplePeak) {
            Peak p = (Peak) obj;
            return Math.abs(mass - p.getMass()) < DELTA && Math.abs(intensity - p.getIntensity()) < DELTA;
        }
        return false;
    }

    @Override
    public int hashCode() {
        long mbits = Double.doubleToLongBits(mass);
        long ibits = Double.doubleToLongBits(intensity);
        return (int) (((mbits ^ (mbits >>> 32)) >> 13) ^ (ibits ^ (ibits >>> 32)));
    }

    @Override
    public String toString() {
        return "[" + mass + "," + intensity + "]";
    }
}