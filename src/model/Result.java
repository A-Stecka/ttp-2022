package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;

public class Result {
    private final TTP problem;
    private final boolean turnOffBackpack;
    private int[] visitOrder;
    private final int[] chosenItems;
    private double fitness;
    private int currentWeight;

////KONSTRUKTORY////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Result(TTP problem, boolean turnOffBackpack) {
        this.problem = problem;
        this.turnOffBackpack = turnOffBackpack;
        visitOrder = new int[problem.getNoOfCities()];
        chosenItems = new int[problem.getNoOfItems()];
        currentWeight = 0;
        fitness = 0.0;
        Arrays.fill(chosenItems, 0);
        Arrays.fill(visitOrder, -1);
    }

    public Result(Result other) {
        this.problem = other.problem;
        this.turnOffBackpack = other.turnOffBackpack;
        visitOrder = Arrays.copyOf(other.visitOrder, other.visitOrder.length);
        chosenItems = new int[problem.getNoOfItems()];
        currentWeight = 0;
        fitness = 0.0;
        Arrays.fill(chosenItems, 0);
    }

    public void reset() {
        Arrays.fill(visitOrder, -1);
        Arrays.fill(chosenItems, 0);
        currentWeight = 0;
        fitness = 0.0;
    }

////"ALGORYTM" LOSOWY///////////////////////////////////////////////////////////////////////////////////////////////////

    public void initRandom() {
        Random random = new Random();
        ArrayList<Integer> available = new ArrayList<>();
        int currentWeight = 0;
        for (int i = 0; i < visitOrder.length; i++)
            available.add(i);
        int j = 0;
        while (available.size() > 0) {
            int index = random.nextInt(available.size());
            visitOrder[j] = available.get(index);
            available.remove(index);
            j++;
        }
        for (int i = 0; i < chosenItems.length; i++)
            available.add(i);
        while (available.size() > 0) {
            int index = random.nextInt(available.size());
            int item = available.get(index);
            if (currentWeight + problem.getItemWeight(item) <= problem.getKnapsackCapacity()) {
                chosenItems[item] = 1;
                currentWeight += problem.getItemWeight(item);
            }
            available.remove(index);
        }
    }

    public void initRandomBackpack() {
        Random random = new Random();
        for (int i = 0; i < chosenItems.length; i++) {
            if (currentWeight + problem.getItemWeight(i) <= problem.getKnapsackCapacity() && Math.random() > 0.5) {
                chosenItems[i] = 1;
                currentWeight += problem.getItemWeight(i);
            }
        }
    }

////"ALGORYTM" ZACHLANNY////////////////////////////////////////////////////////////////////////////////////////////////

    public void initGreedy(int startCity) {
        visitOrder[0] = startCity;
        int currentWeight = 0;
        if (problem.getGreedyMode() == 2) {
            for (int i = 1; i < visitOrder.length; i++)
                visitOrder[i] = problem.getClosestNotVisitedCity(visitOrder[i - 1], visitOrder);
            for (int i = 1; i < visitOrder.length; i++) {
                int[] itemsInCities = problem.getItemsInCitiesByBest(visitOrder[i], visitOrder);
                for (int k = itemsInCities.length - 1; k > -1; k--) {
                    if (!problem.ifCityHasItem(visitOrder[i], itemsInCities[k]))
                        break;
                    if (currentWeight + problem.getItemWeight(itemsInCities[k]) <= problem.getKnapsackCapacity()) {
                        chosenItems[itemsInCities[k]] = 1;
                        currentWeight += problem.getItemWeight(itemsInCities[k]);
                    }
                }
            }
        } else {
            for (int i = 1; i < visitOrder.length; i++)
                visitOrder[i] = problem.getClosestNotVisitedCity(visitOrder[i - 1], visitOrder);
            for (int i = 1; i < visitOrder.length; i++) {
                int[] itemsInCity = problem.getItemsInCityByBest(visitOrder[i]);
                for (int k = itemsInCity.length - 1; k > -1; k--) {
                    if (currentWeight + problem.getItemWeight(itemsInCity[k]) <= problem.getKnapsackCapacity()) {
                        chosenItems[itemsInCity[k]] = 1;
                        currentWeight += problem.getItemWeight(itemsInCity[k]);
                    }
                }
            }
        }
    }

    public void initGreedyBackpack() {
        Arrays.fill(chosenItems, 0);
        int currentWeight = 0;
        if (problem.getGreedyMode() == 2) {
            for (int city : visitOrder) {
                int[] itemsInCities = problem.getItemsInCitiesByBest(visitOrder[city], visitOrder);
                for (int k = itemsInCities.length - 1; k > -1; k--) {
                    if (!problem.ifCityHasItem(visitOrder[city], itemsInCities[k]))
                        break;
                    if (currentWeight + problem.getItemWeight(itemsInCities[k]) <= problem.getKnapsackCapacity()) {
                        chosenItems[itemsInCities[k]] = 1;
                        currentWeight += problem.getItemWeight(itemsInCities[k]);
                    }
                }
            }
        } else {
            for (int city : visitOrder) {
                int[] itemsInCity = problem.getItemsInCityByBest(city);
                for (int k = itemsInCity.length - 1; k > -1; k--) {
                    if (currentWeight + problem.getItemWeight(itemsInCity[k]) <= problem.getKnapsackCapacity()) {
                        chosenItems[itemsInCity[k]] = 1;
                        currentWeight += problem.getItemWeight(itemsInCity[k]);
                    }
                }
            }
        }
    }

////FITNESS/////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void evaluate() {
        this.currentWeight = 0;
        if (turnOffBackpack)
            this.fitness = this.evaluateTSP();
        else
            this.fitness = this.getKnapsackValue() - this.getRouteTime();
    }

    private double evaluateTSP() {
        double distance = 0.0;
        for (int i = 0; i < visitOrder.length - 1; i++)
            distance += problem.getDistanceBetween(visitOrder[i], visitOrder[i + 1]);
        distance += problem.getDistanceBetween(visitOrder[visitOrder.length - 1], visitOrder[0]);
        return distance;
    }

    private double getKnapsackValue() {
        double value = 0.0;
        for (int i = 0; i < chosenItems.length; i++)
            if (chosenItems[i] == 1)
                value += problem.getItemValue(i);
        return value;
    }

    private double getRouteTime() {
        double time = 0.0;
        for (int i = 0; i < visitOrder.length - 1; i++)
            time += this.getRoutePartTime(visitOrder[i], visitOrder[i + 1]);
        time += this.getRoutePartTime(visitOrder[visitOrder.length - 1], visitOrder[0]);
        return time;
    }

    private double getRoutePartTime(int routeStart, int routeEnd) {
        return problem.getDistanceBetween(routeStart, routeEnd) / this.getCurrentSpeed(routeStart);
    }

    private double getCurrentSpeed(int city) {
        this.updateCurrentWeight(city);
        return problem.getMaxSpeed() - this.currentWeight * (problem.getMaxSpeed() - problem.getMinSpeed()) / problem.getKnapsackCapacity();
    }

    private void updateCurrentWeight(int city) {
        double weightInCity = 0.0;
        for (int i = 0; i < chosenItems.length; i++)
            if (chosenItems[i] == 1 && problem.ifCityHasItem(city, i))
                weightInCity += problem.getItemWeight(i);
        this.currentWeight += weightInCity;
    }

    ////KRZYZOWANIE OX//////////////////////////////////////////////////////////////////////////////////////////////////////

    public Result[] crossoverOrdered(Result secondParent) {
        int firstId = (int) (Math.random() * problem.getNoOfCities()),
                secondId = (int) (Math.random() * problem.getNoOfCities());
        while (firstId == secondId)
            secondId = (int) (Math.random() * problem.getNoOfCities());
        if (firstId > secondId) {
            int tmp = firstId;
            firstId = secondId;
            secondId = tmp;
        }
        return new Result[]{this.crossoverOrderedSingleChild(secondParent, firstId, secondId), secondParent.crossoverOrderedSingleChild(this, firstId, secondId)};
    }

    public Result crossoverOrderedSingleChild(Result secondParent, int firstId, int secondId) {
        int[] childGenotype = new int[problem.getNoOfCities()];
        Arrays.fill(childGenotype, -1);
        if (secondId + 1 - firstId >= 0)
            System.arraycopy(visitOrder, firstId, childGenotype, firstId, secondId + 1 - firstId);

        int index = 0;
        for (int i = 0; i < problem.getNoOfCities(); i++) {
            int city = secondParent.visitOrder[i];
            if (Arrays.stream(childGenotype).allMatch(j -> j != city)) {
                while (childGenotype[index] != -1)
                    index++;
                childGenotype[index] = city;
            }
        }

        Result child = new Result(problem, turnOffBackpack);
        child.visitOrder = childGenotype;
        return child;
    }

////KRZYZOWANIE PMX/////////////////////////////////////////////////////////////////////////////////////////////////////

    public Result[] crossoverPartiallyMatched(Result secondParent) {
        int[] firstChildGenotype = new int[problem.getNoOfCities()],
                secondChildGenotype = new int[problem.getNoOfCities()];
        Arrays.fill(firstChildGenotype, -1);
        Arrays.fill(secondChildGenotype, -1);
        int firstId = (int) (Math.random() * problem.getNoOfCities()),
                secondId = (int) (Math.random() * problem.getNoOfCities());
        while (firstId == secondId)
            secondId = (int) (Math.random() * problem.getNoOfCities());
        if (firstId > secondId) {
            int tmp = firstId;
            firstId = secondId;
            secondId = tmp;
        }

        TreeMap<Integer, Integer> firstMapping = new TreeMap<>();
        TreeMap<Integer, Integer> secondMapping = new TreeMap<>();
        for (int i = firstId; i < secondId + 1; i++) {
            firstChildGenotype[i] = secondParent.visitOrder[i];
            secondChildGenotype[i] = visitOrder[i];
            firstMapping.put(firstChildGenotype[i], secondChildGenotype[i]);
            secondMapping.put(secondChildGenotype[i], firstChildGenotype[i]);
        }

        for (int i = 0; i < problem.getNoOfCities(); i++)
            if (i < firstId || i > secondId) {
                int firstValue = firstMapping.getOrDefault(visitOrder[i], visitOrder[i]);
                while (firstMapping.containsKey(firstValue))
                    firstValue = firstMapping.get(firstValue);
                firstChildGenotype[i] = firstValue;

                int secondValue = secondMapping.getOrDefault(secondParent.visitOrder[i], secondParent.visitOrder[i]);
                while (secondMapping.containsKey(secondValue))
                    secondValue = secondMapping.get(secondValue);
                secondChildGenotype[i] = secondValue;
            }

        Result firstChild = new Result(problem, turnOffBackpack);
        firstChild.visitOrder = firstChildGenotype;
        Result secondChild = new Result(problem, turnOffBackpack);
        secondChild.visitOrder = secondChildGenotype;
        return new Result[]{firstChild, secondChild};
    }

////MUTACJA SWAP////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void mutationSwap(double mutationProbability) {
        for (int i = 0; i < problem.getNoOfCities(); i++)
            if (Math.random() < mutationProbability) {
                int secondId = (int) (Math.random() * problem.getNoOfCities());
                while (i == secondId)
                    secondId = (int) (Math.random() * problem.getNoOfCities());
                int tmp = visitOrder[i];
                visitOrder[i] = visitOrder[secondId];
                visitOrder[secondId] = tmp;
            }
    }

////MUTACJA INWERSJA////////////////////////////////////////////////////////////////////////////////////////////////////

    public void mutationInversion(double mutationProbability) {
        if (Math.random() < mutationProbability) {
            int firstId = (int) (Math.random() * problem.getNoOfCities()),
                    secondId = (int) (Math.random() * problem.getNoOfCities());
            while (firstId == secondId)
                secondId = (int) (Math.random() * problem.getNoOfCities());
            if (firstId > secondId) {
                int tmp = firstId;
                firstId = secondId;
                secondId = tmp;
            }
            for (int i = 0; i < (secondId - firstId) / 2; i++) {
                int tmp = visitOrder[firstId + i];
                visitOrder[firstId + i] = visitOrder[secondId - i];
                visitOrder[secondId - i] = tmp;
            }
        }
    }

////WYBOR SASIADOW//////////////////////////////////////////////////////////////////////////////////////////////////////

    public Result[] getNeighbours(int mode, int noOfNeighbours) {
        Result[] neighbours = new Result[noOfNeighbours];
        for (int i = 0; i < noOfNeighbours; i++) {
            if (mode == 0)
                neighbours[i] = getNeighbourSwap();
            else
                neighbours[i] = getNeighbourInversion();
            neighbours[i].initGreedyBackpack();
            neighbours[i].evaluate();
        }
        return neighbours;
    }

////SASIEDZTWO SWAP/////////////////////////////////////////////////////////////////////////////////////////////////////

    public Result getNeighbourSwap() {
        int firstId = (int) (Math.random() * problem.getNoOfCities()),
                secondId = (int) (Math.random() * problem.getNoOfCities());
        while (firstId == secondId)
            secondId = (int) (Math.random() * problem.getNoOfCities());

        Result neighbour = new Result(this);
        int tmp = neighbour.visitOrder[firstId];
        neighbour.visitOrder[firstId] = neighbour.visitOrder[secondId];
        neighbour.visitOrder[secondId] = tmp;
        return neighbour;
    }

////MUTACJA INWERSJA////////////////////////////////////////////////////////////////////////////////////////////////////

    public Result getNeighbourInversion() {
        int firstId = (int) (Math.random() * problem.getNoOfCities()),
                secondId = (int) (Math.random() * problem.getNoOfCities());
        while (firstId == secondId)
            secondId = (int) (Math.random() * problem.getNoOfCities());
        if (firstId > secondId) {
            int tmp = firstId;
            firstId = secondId;
            secondId = tmp;
        }
        Result neighbour = new Result(this);
        for (int i = 0; i < (secondId - firstId) / 2; i++) {
            int tmp = neighbour.visitOrder[firstId + i];
            neighbour.visitOrder[firstId + i] = neighbour.visitOrder[secondId - i];
            neighbour.visitOrder[secondId - i] = tmp;
        }
        return neighbour;
    }

////GETTERY I SETTERY///////////////////////////////////////////////////////////////////////////////////////////////////

    public double getFitness() {
        return fitness;
    }

////TO STRING, EQUALS///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "model.Result { \n" +
                "\tvisitOrder=" + Arrays.toString(visitOrder) + "\n" +
                "\tchosenItems=" + Arrays.toString(chosenItems) + "\n" +
                "\tfitness=" + this.fitness + "\n" +
                "\tnoOfItemsInBackpack=" + Arrays.stream(chosenItems).sum() + "\n" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Result result = (Result) o;
        return Arrays.equals(visitOrder, result.visitOrder);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(visitOrder);
    }
}
