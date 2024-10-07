package main;

import model.Result;
import model.TTP;

import java.util.LinkedList;

public class TS extends Algorithm {
    private final LinkedList<Result> tabu;
    private final int noOfNeighbours, noOfIterations, tabuSize;
    private final double [] currents;
    private final boolean aspirationCriteria;

////KONSTRUKTORY////////////////////////////////////////////////////////////////////////////////////////////////////////

    public TS(boolean debug, TTP problem, int noOfNeighbours, int noOfIterations, int tabuSize,
              boolean aspirationCriteria, boolean turnOffBackpack) {
        super(debug, problem, turnOffBackpack);
        this.noOfNeighbours = noOfNeighbours;
        this.noOfIterations = noOfIterations;
        this.tabuSize = tabuSize;
        this.aspirationCriteria = aspirationCriteria;
        tabu = new LinkedList<>();
        currents = new double[noOfIterations];
        bests = new double[noOfIterations];
        worsts = new double[noOfIterations];
        avgs = new double[noOfIterations];
    }

////METAHEURYSTYKA TABU SEARCH//////////////////////////////////////////////////////////////////////////////////////////

    //mode = 0 ----> sasiedztwo swap
    //mode = 1 ----> sasiedztwo inversion
    public void run(int mode) {
        Result current = new Result(problem, turnOffBackpack);
        current.initRandom();
        current.initGreedyBackpack();
        current.evaluate();
        Result best = current;
        tabu.add(current);
        for (int i = 0; i < noOfIterations; i++) {
            double worstFitness = Integer.MAX_VALUE;
            double fitnessSum = 0.0;
            boolean found = false;

            int[] neighboursInTabu = new int[noOfNeighbours];
            Result[] neighbours = current.getNeighbours(mode, noOfNeighbours);
            Result bestNeighbour = current;

            for (int j = 0; j < noOfNeighbours; j++)
                if (tabu.contains(neighbours[j]))
                    neighboursInTabu[j] = 1;
                else
                    neighboursInTabu[j] = 0;

            for (int j = 0; j < noOfNeighbours; j++)  {
                if (neighboursInTabu[j] == 0 && !found) {
                    current = neighbours[j];
                    found = true;
                }
                if (neighbours[j].getFitness() > bestNeighbour.getFitness())
                    bestNeighbour = neighbours[j];
            }

            if (aspirationCriteria && !found)
                current = bestNeighbour;
            else
                for (int j = 0; j < noOfNeighbours; j++) {
                    double neighbourFitness = neighbours[j].getFitness();

                    if (neighbourFitness < worstFitness)
                        worstFitness = neighbourFitness;
                    fitnessSum += neighbourFitness;

                    if (neighboursInTabu[j] == 0 && neighbourFitness > current.getFitness())
                        current = neighbours[j];
                }

            if (current.getFitness() > best.getFitness())
                best = current;

            tabu.add(current);
            if (tabu.size() > tabuSize)
                tabu.remove(0);

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
                System.out.println();
            }
        }
    }

////GETTERY I SETTERY///////////////////////////////////////////////////////////////////////////////////////////////////

    public double[] getCurrents() {
        return currents;
    }

}
