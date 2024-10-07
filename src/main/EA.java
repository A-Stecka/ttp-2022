package main;

import model.Result;
import model.TTP;

import java.util.ArrayList;
import java.util.TreeMap;

public class EA extends Algorithm {
    private final int noOfGenerations, populationSize, roulettePressureModifier;
    private final double tournamentSizePercent, crossoverProbability, mutationProbability;
    private TreeMap<Double, Result> probabilities;


////KONSTRUKTORY////////////////////////////////////////////////////////////////////////////////////////////////////////

    public EA(boolean debug, TTP problem, int noOfGenerations, int populationSize, double tournamentSizePercent,
              int roulettePressureModifier, double crossoverProbability, double mutationProbability, boolean turnOffBackpack) {
        super(debug, problem, turnOffBackpack);
        this.noOfGenerations = noOfGenerations;
        this.populationSize = populationSize;
        this.tournamentSizePercent = tournamentSizePercent;
        this.roulettePressureModifier = roulettePressureModifier;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
        bests = new double[noOfGenerations];
        worsts = new double[noOfGenerations];
        avgs = new double[noOfGenerations];
    }

////ALGORYTM EWOLUCYJNY/////////////////////////////////////////////////////////////////////////////////////////////////

    //selectionMode = 0 ----> selekcja turniejowa
    //selectionMode = 1 ----> selekcja ruletkowa
    //crossoverMode = 0 ----> krzyzowanie OX
    //crossoverMode = 1 ----> krzyzowanie PMX
    //mutationMode = 0 -----> mutacja swap
    //mutationMode = 0 -----> mutacja inversion
    public void run(int selectionMode, int crossoverMode, int mutationMode) {
        ArrayList<Result> previousPopulation = new ArrayList<>();
        this.initRandom(previousPopulation);
        int currentGeneration = 0;

        while (currentGeneration < noOfGenerations) {
            double currentBest = Integer.MIN_VALUE;
            double currentWorst = Integer.MAX_VALUE;
            double currentFitnessSum = 0.0;
            this.setProbabilities(previousPopulation);

            ArrayList<Result> newPopulation = new ArrayList<>();
            while (newPopulation.size() < populationSize) {
                Result selectedIndividual;
                if (selectionMode == 0)
                    selectedIndividual = tournamentSelect(previousPopulation);
                else
                    selectedIndividual = rouletteSelect();
                if (Math.random() < crossoverProbability) {
                    Result secondParent;
                    if (selectionMode == 0)
                        secondParent = tournamentSelect(previousPopulation);
                    else
                        secondParent = rouletteSelect();
                    Result[] children;
                    if (crossoverMode == 0)
                        children = selectedIndividual.crossoverOrdered(secondParent);
                    else
                        children = selectedIndividual.crossoverPartiallyMatched(secondParent);
                    if (mutationMode == 0) {
                        children[0].mutationSwap(mutationProbability);
                        children[1].mutationSwap(mutationProbability);
                    } else {
                        children[0].mutationInversion(mutationProbability);
                        children[1].mutationInversion(mutationProbability);
                    }
                    children[0].initGreedyBackpack();
                    children[1].initGreedyBackpack();
                    children[0].evaluate();
                    children[1].evaluate();

                    Result[] childrenRandomBackpack = new Result[2];
                    childrenRandomBackpack[0] = new Result(children[0]);
                    childrenRandomBackpack[1] = new Result(children[1]);
                    childrenRandomBackpack[0].initRandomBackpack();
                    childrenRandomBackpack[1].initRandomBackpack();
                    childrenRandomBackpack[0].evaluate();
                    childrenRandomBackpack[1].evaluate();

                    Result[] finalChildren = new Result[2];
                    if (children[0].getFitness() > childrenRandomBackpack[0].getFitness())
                        finalChildren[0] = children[0];
                    else
                        finalChildren[0] = childrenRandomBackpack[0];
                    if (children[1].getFitness() > childrenRandomBackpack[1].getFitness())
                        finalChildren[1] = children[1];
                    else
                        finalChildren[1] = childrenRandomBackpack[1];

                    if (currentBest < Math.max(finalChildren[0].getFitness(), finalChildren[1].getFitness()))
                        currentBest = Math.max(finalChildren[0].getFitness(), finalChildren[1].getFitness());
                    if (currentWorst > Math.min(finalChildren[0].getFitness(), finalChildren[1].getFitness()))
                        currentWorst = Math.min(finalChildren[0].getFitness(), finalChildren[1].getFitness());
                    currentFitnessSum += finalChildren[0].getFitness() + finalChildren[1].getFitness();

                    newPopulation.add(finalChildren[0]);
                    newPopulation.add(finalChildren[1]);
                } else {
                    Result child = new Result(selectedIndividual);
                    if (mutationMode == 0)
                        child.mutationSwap(mutationProbability);
                    else
                        child.mutationInversion(mutationProbability);
                    child.initGreedyBackpack();
                    child.evaluate();

                    Result childRandomBackpack = new Result(child);
                    childRandomBackpack.initRandomBackpack();
                    childRandomBackpack.evaluate();

                    Result finalChild;
                    if (child.getFitness() > childRandomBackpack.getFitness())
                        finalChild = child;
                    else
                        finalChild = childRandomBackpack;

                    if (currentBest < finalChild.getFitness())
                        currentBest = finalChild.getFitness();
                    if (currentWorst > finalChild.getFitness())
                        currentWorst = finalChild.getFitness();
                    currentFitnessSum += finalChild.getFitness();

                    newPopulation.add(finalChild);
                }
            }
            bests[currentGeneration] = currentBest;
            worsts[currentGeneration] = currentWorst;
            avgs[currentGeneration] = currentFitnessSum / populationSize;

            if (debug) {
                System.out.printf("%-15s", "gen " + currentGeneration + ":");
                System.out.printf("%-15.2f", currentBest);
                System.out.printf("%-15.2f", currentWorst);
                System.out.printf("%-15.2f", currentFitnessSum / populationSize);
                System.out.println();
            }

            previousPopulation = newPopulation;
            currentGeneration++;
        }
    }

////SELEKCJA TURNIEJOWA/////////////////////////////////////////////////////////////////////////////////////////////////

    public Result tournamentSelect(ArrayList<Result> population) {
        Result bestIndividual = null;
        for (int i = 0; i < tournamentSizePercent * populationSize; i++) {
            int index = (int) (Math.random() * populationSize);
            if (bestIndividual == null || bestIndividual.getFitness() < population.get(index).getFitness())
                bestIndividual = population.get(index);
        }
        return bestIndividual;
    }

////SELEKCJA RULETKOWA//////////////////////////////////////////////////////////////////////////////////////////////////

    public Result rouletteSelect() {
        return probabilities.ceilingEntry(Math.random()).getValue();
    }

    public void setProbabilities(ArrayList<Result> population) {
        double worstFitness = Double.MAX_VALUE;
        for (Result individual : population)
            if (individual.getFitness() < worstFitness)
                worstFitness = individual.getFitness();
        worstFitness = Math.abs(worstFitness);

        double populationFitness = 0.0;
        for (Result individual : population)
            populationFitness += Math.pow(individual.getFitness() + worstFitness, roulettePressureModifier);

        probabilities = new TreeMap<>();
        for (Result individual : population) {
            double probability = Math.pow(individual.getFitness() + worstFitness, roulettePressureModifier)
                    / populationFitness;
            if (!probabilities.isEmpty())
                probability += probabilities.lastKey();
            probabilities.put(probability, individual);
        }
    }

////LOSOWA INICJALIZACJA POPULACJI//////////////////////////////////////////////////////////////////////////////////////

    public void initRandom(ArrayList<Result> population) {
        for (int i = 0; i < populationSize; i++) {
            Result individual = new Result(problem, turnOffBackpack);
            individual.initRandom();
            individual.evaluate();
            population.add(individual);
        }
    }

}
