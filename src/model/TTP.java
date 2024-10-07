package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class TTP {
    private String problemName, knapsackDataType, edgeWeightType;
    private int noOfCities, noOfItems, knapsackCapacity, greedyMode;
    private double minSpeed, maxSpeed;
    private double[][] cities;
    private int[][] items, itemsInCities, itemsInCitiesByBest;

////KONSTRUKTORY////////////////////////////////////////////////////////////////////////////////////////////////////////

    public TTP() {
        problemName = "";
        knapsackDataType = "";
        edgeWeightType = "";
        noOfCities = 0;
        noOfItems = 0;
        knapsackCapacity = 0;
        greedyMode = 0;
        minSpeed = 0.0;
        cities = null;
        items = null;
        itemsInCities = null;
        itemsInCitiesByBest = null;
    }

////POBRANIE WARTOSCI, WAGI PRZEDMIOTU//////////////////////////////////////////////////////////////////////////////////

    public int getItemValue(int item) {
        return items[item][0];
    }

    public int getItemWeight(int item) {
        return items[item][1];
    }

    public double getItemValueToWeightRatio(int item) {
        return ((double) items[item][0]) / ((double) items[item][1]);
    }

////POBRANIE ODLEGLOSCI MIEDZY DWOMA MIASTAMI///////////////////////////////////////////////////////////////////////////

    public double getDistanceBetween(int firstCity, int secondCity) {
        return cities[firstCity][secondCity];
    }

////POBRANIE NAJBLIZSZEGO MIASTA KTORE NIE ZOSTALO ODWIEDZONE///////////////////////////////////////////////////////////

    public int getClosestNotVisitedCity(int city, int[] visited) {
        double minDistance = Integer.MAX_VALUE;
        int closestCity = -1;
        for (int i = 0; i < cities[city].length; i++) {
            int checkedCity = i;
            if (cities[city][i] < minDistance && i != city && Arrays.stream(visited).allMatch(item -> item != checkedCity)) {
                closestCity = i;
                minDistance = cities[city][i];
            }
        }
        return closestCity;
    }

////WSZYSTKIE PRZEDMIOTY Z MIASTA///////////////////////////////////////////////////////////////////////////////////////

    public void calculateItemsInCityByBest() {
        itemsInCitiesByBest = new int[noOfCities][noOfItems];
        for (int i = 0; i < noOfCities; i++) {
            ArrayList<Integer> items = new ArrayList<>();
            for (int j = 0; j < itemsInCities[i].length; j++)
                if (itemsInCities[i][j] != 0)
                    items.add(j);
            if (greedyMode == 0)
                items.sort(Comparator.comparing(this::getItemValue));
            else
                items.sort(Comparator.comparing(this::getItemValueToWeightRatio));
            itemsInCitiesByBest[i] = items.stream().mapToInt(k -> k).toArray();
        }
    }

    public int[] getItemsInCityByBest(int city) {
        return itemsInCitiesByBest[city];
    }

    public int[] getItemsInCitiesByBest(int city, int[] visitOrder) {
        int distance = 0, i = 0;
        while (visitOrder[i] != city)
            i++;
        for (int j = i; j < visitOrder.length - 1; j++)
            distance += getDistanceBetween(visitOrder[j], visitOrder[j + 1]);
        distance += getDistanceBetween(visitOrder[visitOrder.length - 1], visitOrder[0]);

        ArrayList<Integer> items = new ArrayList<>();
        for (int j = i; j < visitOrder.length; j++) {
            for (int k = 0; k < itemsInCities[j].length; k++)
                if (itemsInCities[visitOrder[j]][k] != 0)
                    items.add(k);
        }
        int finalDistance = distance;
        items.sort(Comparator.comparing(this::getItemValueToWeightRatio, Comparator.comparingDouble(r -> (r / finalDistance))));
        return items.stream().mapToInt(k -> k).toArray();
    }

////CZY PRZEDMIOT JEST W MIESCIE////////////////////////////////////////////////////////////////////////////////////////

    public boolean ifCityHasItem(int city, int item) {
        return itemsInCities[city][item] == 1;
    }

////TO STRING///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("model.TTP { \n" + "\tproblemName='" + problemName + "'\n" +
                "\tknapsackDataType='" + knapsackDataType + "'\n" +
                "\tedgeWeightType='" + edgeWeightType + "'\n" +
                "\tnoOfCities=" + noOfCities + "\n" +
                "\tnoOfItems=" + noOfItems + "\n" +
                "\tknapsackCapacity=" + knapsackCapacity + "\n" +
                "\tminSpeed=" + minSpeed + "\n" +
                "\tmaxSpeed=" + maxSpeed + "\n" +
                "\tcities=[");
        for (double[] city : cities) {
            result.append("\n\t\t");
            for (double distance : city)
                result.append(String.format("%-10.2f", distance));
        }
        result.append("\n\t]\n");
        result.append("\titems=[");
        for (int i = 0; i < items.length; i++) {
            result.append("\n\t\t");
            result.append(String.format("%-6d", i));
            for (int value : items[i])
                result.append(String.format("%-6d", value));
        }
        result.append("\n\t]\n");
        result.append("\titemsInCities=[");
        for (int[] items : itemsInCities) {
            result.append("\n\t\t");
            for (int isInCity : items)
                result.append(String.format("%-6d", isInCity));
        }
        result.append("\n\t]\n");
        result.append("}");
        return result.toString();
    }

////GETTERY I SETTERY///////////////////////////////////////////////////////////////////////////////////////////////////

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public void setKnapsackDataType(String knapsackDataType) {
        this.knapsackDataType = knapsackDataType;
    }

    public void setEdgeWeightType(String edgeWeightType) {
        this.edgeWeightType = edgeWeightType;
    }

    public int getNoOfCities() {
        return noOfCities;
    }

    public void setNoOfCities(int noOfCities) {
        this.noOfCities = noOfCities;
    }

    public int getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(int noOfItems) {
        this.noOfItems = noOfItems;
    }

    public int getKnapsackCapacity() {
        return knapsackCapacity;
    }

    public void setKnapsackCapacity(int knapsackCapacity) {
        this.knapsackCapacity = knapsackCapacity;
    }

    public int getGreedyMode() {
        return greedyMode;
    }

    public void setGreedyMode(int greedyMode) {
        this.greedyMode = greedyMode;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setCities(double[][] cities) {
        this.cities = cities;
    }

    public void setItems(int[][] items) {
        this.items = items;
    }

    public void setItemsInCities(int[][] items) {
        this.itemsInCities = items;
    }

}
