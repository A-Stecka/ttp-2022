package main;

import model.Result;
import model.TTP;

public class SA extends Algorithm {
    private final int noOfNeighbours, noOfIterations;
    private final double startTemp, endTemp, coolingSchema;
    private final double [] currents;

////KONSTRUKTORY////////////////////////////////////////////////////////////////////////////////////////////////////////

    public SA(boolean debug, TTP problem, int noOfNeighbours, int noOfIterations, double startTemp, double endTemp,
              double coolingSchema, boolean turnOffBackpack) {
        super(debug, problem, turnOffBackpack);
        this.noOfNeighbours = noOfNeighbours;
        this.noOfIterations = noOfIterations;
        this.startTemp = startTemp;
        this.endTemp = endTemp;
        this.coolingSchema = coolingSchema;
        currents = new double[noOfIterations];
        bests = new double[noOfIterations];
        worsts = new double[noOfIterations];
        avgs = new double[noOfIterations];
    }

////METAHEURYSTYKA SIMULATED ANNEALING//////////////////////////////////////////////////////////////////////////////////

    //mode = 0 ----> sasiedztwo swap
    //mode = 1 ----> sasiedztwo inversion
    public void run(int mode) {
        Result current = new Result(problem, turnOffBackpack);
        current.initRandom();
        current.initGreedyBackpack();
        current.evaluate();
        Result best = current;
        double currentTemperature = startTemp;
        for (int i = 0; i < noOfIterations; i++) {
            double worstFitness = Integer.MAX_VALUE;
            double fitnessSum = 0.0;

            Result[] neighbours = current.getNeighbours(mode, noOfNeighbours);
            Result bestNeighbour = neighbours[0];

            for (int j = 0; j < noOfNeighbours; j++) {
                double neighbourFitness = neighbours[j].getFitness();

                if (neighbourFitness > best.getFitness())
                    bestNeighbour = neighbours[j];

                if (neighbourFitness < worstFitness)
                    worstFitness = neighbourFitness;

                fitnessSum += neighbourFitness;
            }

            if (bestNeighbour.getFitness() > current.getFitness())
                current = bestNeighbour;
            else {
                if (Math.random() < Math.exp((bestNeighbour.getFitness() - current.getFitness()) / currentTemperature))
                    current = bestNeighbour;
            }

            if (currentTemperature > endTemp)
                currentTemperature = currentTemperature * coolingSchema;

            if (current.getFitness() > best.getFitness())
                best = current;

            currents[i] = current.getFitness();
            bests[i] = best.getFitness();
            worsts[i] = worstFitness;
            avgs[i] = fitnessSum / ((double) noOfNeighbours);

            if (debug) {
                System.out.printf("%-20s", "iteration " + i + ":");
                System.out.printf("%-15.2f", currents[i]);
                System.out.printf("%-15.2f", bests[i]);
                System.out.printf("%-15.2f", worsts[i]);
                System.out.printf("%-15.2f", avgs[i]);
                System.out.printf("%-15.2f", currentTemperature);
                System.out.println();
            }
        }
    }

////GETTERY I SETTERY///////////////////////////////////////////////////////////////////////////////////////////////////

    public double[] getCurrents() {
        return currents;
    }
}
