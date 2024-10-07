package main;

import model.TTP;

public abstract class Algorithm {
    protected final boolean debug;
    protected TTP problem;
    protected boolean turnOffBackpack;
    protected double [] bests, worsts, avgs;

////KONSTRUKTORY////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Algorithm(boolean debug, TTP problem, boolean turnOffBackpack) {
        this.debug = debug;
        this.problem = problem;
        this.turnOffBackpack = turnOffBackpack;
    }

////GETTERY I SETTERY///////////////////////////////////////////////////////////////////////////////////////////////////

    public double[] getBests() {
        return bests;
    }

    public double getBest() {
        double best = Integer.MIN_VALUE;
        for (double value : bests)
            if (value > best)
                best = value;
        return best;
    }

    public double[] getWorsts() {
        return worsts;
    }

    public double getWorst() {
        double worst = Integer.MAX_VALUE;
        for (double value : worsts)
            if (value < worst)
                worst = value;
        return worst;
    }

    public double[] getAvgs() {
        return avgs;
    }

}
