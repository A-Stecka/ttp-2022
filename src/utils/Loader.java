package utils;

import model.TTP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Loader {
    private ArrayList<String[]> content;

////KONSTRUKTORY////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Loader() {
        this.content = null;
    }

////CZYTANIE Z PLIKU////////////////////////////////////////////////////////////////////////////////////////////////////

    public void readFromFile(String filename) {
        content = new ArrayList<>();
        try (BufferedReader TSVReader = new BufferedReader(new FileReader(filename))) {
            String singleLine;
            while ((singleLine = TSVReader.readLine()) != null) {
                String[] lineItems = singleLine.split("\t");
                content.add(lineItems);
            }
        } catch (Exception e) {
            System.out.println("Cannot open file");
        }
    }

////LADOWANIE DO INSTANCJI PROBLEMU/////////////////////////////////////////////////////////////////////////////////////

    public void loadIntoTTP(TTP problem, int greedyMode) {
        problem.setProblemName(content.get(0)[1]);
        problem.setKnapsackDataType(content.get(1)[0].substring(20));
        problem.setEdgeWeightType(content.get(8)[1]);
        problem.setNoOfCities(Integer.parseInt(content.get(2)[1]));
        problem.setNoOfItems(Integer.parseInt(content.get(3)[1]));
        problem.setKnapsackCapacity(Integer.parseInt(content.get(4)[1]));
        problem.setMinSpeed(Double.parseDouble(content.get(5)[1]));
        problem.setMaxSpeed(Double.parseDouble(content.get(6)[1]));

        double[] citiesX = new double[problem.getNoOfCities()];
        double[] citiesY = new double[problem.getNoOfCities()];
        for (int i = 0; i < problem.getNoOfCities(); i++) {
            citiesX[i] = Double.parseDouble(content.get(i + 10)[1]);
            citiesY[i] = Double.parseDouble(content.get(i + 10)[2]);
        }

        double[][] cities = new double[problem.getNoOfCities()][problem.getNoOfCities()];
        for (int i = 0; i < cities.length; i++)
            for (int j = 0; j < cities[i].length; j++)
                cities[i][j] = Math.sqrt(Math.pow(citiesX[i] - citiesX[j], 2) + Math.pow(citiesY[i] - citiesY[j], 2));
        problem.setCities(cities);

        int[][] itemsInCities = new int[problem.getNoOfCities()][problem.getNoOfItems()];
        for (int[] items : itemsInCities)
            Arrays.fill(items, 0);

        int[][] items = new int[problem.getNoOfItems()][2];
        for (int i = 0; i < problem.getNoOfItems(); i++) {
            items[i][0] = Integer.parseInt(content.get(i + 11 + problem.getNoOfCities())[1]);
            items[i][1] = Integer.parseInt(content.get(i + 11 + problem.getNoOfCities())[2]);
            itemsInCities[Integer.parseInt(content.get(i + 11 + problem.getNoOfCities())[3]) - 1][i] = 1;
        }
        problem.setItems(items);
        problem.setItemsInCities(itemsInCities);
        problem.setGreedyMode(greedyMode);
        problem.calculateItemsInCityByBest();
    }
}
