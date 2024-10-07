import model.*;
import utils.*;
import main.*;
import java.util.Objects;

public class Research {
    private final String[] filenames, outputs, instances;
    private final Logger logger;
    private final TTP[] problems;
    private int greedyMode;

////KONSTRUKTORY////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Research() {
        this.logger = new Logger();
        this.filenames = new String[]{"..\\student\\easy_0.ttp", "..\\student\\easy_1.ttp", "..\\student\\medium_0.ttp",
                "..\\student\\medium_1.ttp", "..\\student\\medium_2.ttp", "..\\student\\hard_0.ttp", "..\\student\\hard_1.ttp"};
        this.outputs = new String[]{"results\\all_raw\\random.csv", "results\\all_raw\\random_greedy.csv",
                "results\\all_raw\\greedy.csv", "results\\all_raw\\ea.csv", "results\\all_raw\\ts.csv", "results\\all_raw\\sa.csv"};
        this.instances = new String[]{"easy_0", "easy_1", "medium_0", "medium_1", "medium_2", "hard_0", "hard_1"};
        this.problems = new TTP[filenames.length];
        this.greedyMode = -1;
    }

////LADOWANIE DANYCH Z PLIKU////////////////////////////////////////////////////////////////////////////////////////////

    //mode = 0 ----> porownuje wartosc przedmiotow
    //mode = 1 ----> porownuje stosunek wartosci do wagi przedmiotow
    //mode = 2 ----> porownuje stosunek wartosci do wagi przedmiotow biorac pod uwage dystans ktory zostal do przejscia
    public void loadProblems(int greedyMode) {
        this.greedyMode = greedyMode;
        Loader loader = new Loader();
        for (int i = 0; i < filenames.length; i++) {
            problems[i] = new TTP();
            loader.readFromFile(filenames[i]);
            loader.loadIntoTTP(problems[i], greedyMode);
        }
    }

////ALGORYTM LOSOWY/////////////////////////////////////////////////////////////////////////////////////////////////////

    public void runRandom(boolean debug, String filename, String params, TTP problem, int noOfRepeats, boolean turnOffBackpack) {
        long startTime = System.currentTimeMillis();
        double[] bests = new double[noOfRepeats];
        double best = Integer.MIN_VALUE;
        double worst = Integer.MAX_VALUE;
        double avg = 0.0;
        double std = 0.0;
        Result result = new Result(problem, turnOffBackpack);
        for (int i = 0; i < noOfRepeats; i++) {
            result.reset();
            result.initRandom();
            result.evaluate();
            double current = result.getFitness();
            bests[i] = current;
            if (current > best)
                best = current;
            if (current < worst)
                worst = current;
            avg += current;
        }
        avg = avg / ((double) noOfRepeats);
        for (double value : bests)
            std += Math.pow(avg - value, 2);
        std = std / ((double) noOfRepeats);
        std = Math.sqrt(std);
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (debug) {
            System.out.printf("%-15.2f", best);
            System.out.printf("%-15.2f", worst);
            System.out.printf("%-15.2f", avg);
            System.out.printf("%-15.2f", std);
            System.out.printf("%-15d", elapsedTime);
            System.out.println();
        }
        logger.loadIntoFile(filename, params, best, worst, avg, std, elapsedTime);
    }

    @SuppressWarnings("unused")
    public void getStatsRandom(boolean debug, boolean turnOffBackpack) {
        for (int i = 0; i < filenames.length; i++) {
            if (debug)
                System.out.println("------------------------------------------------ " + outputs[0] + " " + instances[i]);
            runRandom(debug, outputs[0], instances[i], problems[i], 10000, turnOffBackpack);
        }
    }

////ALGORYTM LOSOWY Z ZACHLANNYM PLECAKIEM//////////////////////////////////////////////////////////////////////////////

    public void runRandomGreedyBackpack(boolean debug, String filename, String params, TTP problem, int noOfRepeats) {
        long startTime = System.currentTimeMillis();
        double[] bests = new double[noOfRepeats];
        double best = Integer.MIN_VALUE;
        double worst = Integer.MAX_VALUE;
        double avg = 0.0;
        double std = 0.0;
        Result result = new Result(problem, false);
        for (int i = 0; i < noOfRepeats; i++) {
            result.reset();
            result.initRandom();
            result.initGreedyBackpack();
            result.evaluate();
            double current = result.getFitness();
            bests[i] = current;
            if (current > best)
                best = current;
            if (current < worst)
                worst = current;
            avg += current;
        }
        avg = avg / ((double) noOfRepeats);
        for (double value : bests)
            std += Math.pow(avg - value, 2);
        std = std / ((double) noOfRepeats);
        std = Math.sqrt(std);
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (debug) {
            System.out.printf("%-15.2f", best);
            System.out.printf("%-15.2f", worst);
            System.out.printf("%-15.2f", avg);
            System.out.printf("%-15.2f", std);
            System.out.printf("%-15d", elapsedTime);
            System.out.println();
        }
        logger.loadIntoFile(filename, params, best, worst, avg, std, elapsedTime);
    }

    @SuppressWarnings("unused")
    public void getStatsRandomGreedyBackpack(boolean debug) {
        for (int i = 0; i < filenames.length; i++) {
            if (debug)
                System.out.println("------------------------------------------------ " + outputs[1] + " " + instances[i]);
            runRandomGreedyBackpack(debug, outputs[1], instances[i], problems[i], 10000);
        }
    }

////ALGORYTM ZACHLANNY//////////////////////////////////////////////////////////////////////////////////////////////////

    public void runGreedy(boolean debug, String filename, String params, TTP problem) {
        long startTime = System.currentTimeMillis();
        int noOfCities = problem.getNoOfCities();
        double[] bests = new double[noOfCities];
        double best = Integer.MIN_VALUE;
        double worst = Integer.MAX_VALUE;
        double avg = 0.0;
        double std = 0.0;
        Result result = new Result(problem, false);
        for (int i = 0; i < noOfCities; i++) {
            result.reset();
            result.initGreedy(i);
            result.evaluate();
            double current = result.getFitness();
            bests[i] = current;
            if (current > best)
                best = current;
            if (current < worst)
                worst = current;
            avg += current;
        }
        avg = avg / ((double) noOfCities);
        for (double value : bests)
            std += Math.pow(avg - value, 2);
        std = std / ((double) noOfCities);
        std = Math.sqrt(std);
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (debug) {
            System.out.printf("%-15.2f", best);
            System.out.printf("%-15.2f", worst);
            System.out.printf("%-15.2f", avg);
            System.out.printf("%-15.2f", std);
            System.out.printf("%-15d", elapsedTime);
            System.out.println();
        }
        logger.loadIntoFile(filename, params, best, worst, avg, std, elapsedTime);
    }

    @SuppressWarnings("unused")
    public void getStatsGreedy(boolean debug) {
        for (int i = 0; i < filenames.length; i++) {
            if (debug)
                System.out.println("------------------------------------------------ " + outputs[2] + " " + instances[i]);
            runGreedy(debug, outputs[2], instances[i], problems[i]);
        }
    }

////ALGORYTM EWOLUCYJNY/////////////////////////////////////////////////////////////////////////////////////////////////

    private void runEA(boolean debug, String filename, String params, int selectionMode, int crossoverMode, int mutationMode,
                       TTP problem, int noOfGenerations, int populationSize, double tournamentSizePercent, int roulettePressureModifier,
                       double crossoverProbability, double mutationProbability, boolean turnOffBackpack) {
        long startTime = System.currentTimeMillis();
        double[] bests = new double[10];
        double best = Integer.MIN_VALUE;
        double worst = Integer.MAX_VALUE;
        double avg = 0.0;
        double std = 0.0;
        EA ea = new EA(true, problem, noOfGenerations, populationSize, tournamentSizePercent, roulettePressureModifier,
                crossoverProbability, mutationProbability, turnOffBackpack);
        for (int i = 0; i < 10; i++) {
            if (debug)
                System.out.println("------------------------ RUN" + i);
            ea.run(selectionMode, crossoverMode, mutationMode);
            double currentBest = ea.getBest(), currentWorst = ea.getWorst();
            bests[i] = currentBest;
            if (currentBest > best)
                best = currentBest;
            if (currentWorst < worst)
                worst = currentWorst;
            avg += currentBest;
        }
        avg = avg / ((double) 10);
        for (double value : bests)
            std += Math.pow(avg - value, 2);
        std = std / ((double) 10);
        std = Math.sqrt(std);
        long elapsedTime = System.currentTimeMillis() - startTime;
        logger.loadIntoFile(filename, params, best, worst, avg, std, ((double) elapsedTime) / 10.0);
    }

    @SuppressWarnings("unused")
    public void getStatsEA(boolean debug, int selectionMode, int crossoverMode, int mutationMode, int noOfGenerations,
                           int populationSize, double tournamentSizePercent, int roulettePressureModifier,
                           double crossoverProbability, double mutationProbability, boolean turnOffBackpack) {
        for (int i = 0; i < filenames.length; i++) {
            if (debug)
                System.out.println("------------------------------------------------ " + outputs[2] + " " + instances[i]);
            runEA(debug, outputs[3], instances[i] + "_greedy_" + greedyMode, selectionMode, crossoverMode, mutationMode,
                    problems[i], noOfGenerations, populationSize, tournamentSizePercent, roulettePressureModifier,
                    crossoverProbability, mutationProbability, turnOffBackpack);
        }
    }

    @SuppressWarnings("unused")
    public void runOnceEA(boolean debug, String filename, String instance, int selectionMode, int crossoverMode, int mutationMode,
                          int noOfGenerations, int populationSize, double tournamentSizePercent, int roulettePressureModifier,
                          double crossoverProbability, double mutationProbability, boolean turnOffBackpack) {
        int index = -1;
        for (int i = 0; i < instances.length; i++)
            if (Objects.equals(instances[i], instance))
                index = i;
        EA ea = new EA(debug, problems[index], noOfGenerations, populationSize, tournamentSizePercent, roulettePressureModifier,
                crossoverProbability, mutationProbability, turnOffBackpack);
        ea.run(selectionMode, crossoverMode, mutationMode);
        logger.loadIntoFile(filename, ea.getBests(), ea.getWorsts(), ea.getAvgs());
    }

////STROJENIE ALGORYTMU EWOLUCYJNEGO////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    public void fullRunEA(boolean debug) {
        // instance easy_0
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[3] + " " + instances[0]);
        runEA(debug, outputs[3], instances[0], 0, 0, 1, problems[0], 1000, 100, 0.05, 7, 0.6, 0.1, false);

        // instance easy_1
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[3] + " " + instances[1]);
        runEA(debug, outputs[3], instances[1], 0, 0, 1, problems[1], 1000, 100, 0.05, 7, 0.7, 0.1, false);

        // instance medium_0
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[3] + " " + instances[2]);
        runEA(debug, outputs[3], instances[2], 1, 0, 1, problems[2], 1000, 100, 0.1, 9, 0.6, 0.3, false);

        // instance medium_1
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[3] + " " + instances[3]);
        runEA(debug, outputs[3], instances[3], 0, 0, 1, problems[3], 1000, 100, 0.01, 7, 0.7, 0.3, false);

        // instance medium_2
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[3] + " " + instances[4]);
        runEA(debug, outputs[3], instances[4], 0, 0, 1, problems[4], 1000, 100, 0.01, 7, 0.6, 0.3, false);

        if (greedyMode != 2) {
            // instance hard_0
            if (debug)
                System.out.println("------------------------------------------------ " + outputs[3] + " " + instances[5]);
            runEA(debug, outputs[3], instances[5], 0, 0, 0, problems[5], 1000, 100, 0.05, 7, 0.8, 0.05, false);

            // instance hard_1
            if (debug)
                System.out.println("------------------------------------------------ " + outputs[3] + " " + instances[6]);
            runEA(debug, outputs[3], instances[6], 0, 0, 0, problems[6], 1000, 100, 0.05, 7, 0.8, 0.05, false);
        }

        //runOnceEA(false, "results\\ea_raw\\graph_easy_0.csv", instances[0], 0, 0, 0,2500, 500, 0.05, 7, 0.8, 0.05, false);
        //runOnceEA(false, "results\\ea_raw\\graph_easy_1.csv", instances[1], 0, 0, 0, 2500, 500, 0.1, 7, 0.8, 0.05, false);
        //runOnceEA(false, "results\\ea_raw\\graph_medium_0.csv", instances[2], 1, 0, 1, 2500, 500, 0.1, 9, 0.6, 0.3, false);
        //runOnceEA(false, "results\\ea_raw\\graph_medium_1.csv", instances[3], 0, 0, 1,2500, 500, 0.01, 7, 0.7, 0.3, false);
        //runOnceEA(false, "results\\ea_raw\\graph_medium_2.csv", instances[4], 0, 0, 1, 2500, 500, 0.01, 7, 0.6, 0.3, false);
        //runOnceEA(false, "results\\ea_raw\\graph_hard_0.csv", instances[5], 0, 0, 0, 2500, 100, 0.05, 7, 0.8, 0.05, false);
        //runOnceEA(false, "results\\ea_raw\\graph_hard_1.csv", instances[6], 0, 0, 0, 2500, 100, 0.05, 7, 0.8, 0.05, false);
    }

    @SuppressWarnings("unused")
    public void fullCalibrateEA(boolean debug) {
        //fullCalibrateEAIterPopSize(debug);
        //fullCalibrateEACrossoverMutation(debug);
        fullCalibrateEASelection(debug);
    }

    @SuppressWarnings("unused")
    public void fullCalibrateEAIterPopSize(boolean debug) {
        double[] easyMutation = {0.05, 0.1};
        double[] easyCrossover = {0.9, 0.5};
        double[] mediumMutation = {0.01, 0.3};
        double[] mediumCrossover = {0.7, 0.5};

        // instance easy_0
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                for (int k = 0; k < 2; k++)
                    testEvolutionaryAlgorithmIterPopSize(debug, problems[0], instances[0], i, j, k, 0.05, easyCrossover[j], easyMutation[k]);

        // instance easy_1
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                for (int k = 0; k < 2; k++)
                    testEvolutionaryAlgorithmIterPopSize(debug, problems[1], instances[1], i, j, k, 0.05, easyCrossover[j], easyMutation[k]);

        if (greedyMode != 2) {
            // instance medium_0
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 2; j++)
                    for (int k = 0; k < 2; k++)
                        testEvolutionaryAlgorithmIterPopSize(debug, problems[2], instances[2], i, j, k, 0.1, mediumCrossover[j], mediumMutation[k]);

            // instance medium_1
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 2; j++)
                    for (int k = 0; k < 2; k++)
                        testEvolutionaryAlgorithmIterPopSize(debug, problems[3], instances[3], i, j, k, 0.1, mediumCrossover[j], mediumMutation[k]);

            // instance medium_2
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 2; j++)
                    for (int k = 0; k < 2; k++)
                        testEvolutionaryAlgorithmIterPopSize(debug, problems[4], instances[4], i, j, k, 0.1, mediumCrossover[j], mediumMutation[k]);
        }
    }

    @SuppressWarnings("unused")
    public void fullCalibrateEACrossoverMutation(boolean debug) {
        // instance easy_0
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                for (int k = 0; k < 2; k++)
                    testEvolutionaryAlgorithmCrossoverMutation(debug, problems[0], instances[0], i, j, k, 0.05);

        // instance easy_1
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                for (int k = 0; k < 2; k++)
                    testEvolutionaryAlgorithmCrossoverMutation(debug, problems[1], instances[1], i, j, k, 0.05);

        if (greedyMode != 2) {
            // instance medium_0
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 2; j++)
                    for (int k = 0; k < 2; k++)
                        testEvolutionaryAlgorithmCrossoverMutation(debug, problems[2], instances[2], i, j, k, 0.1);

            // instance medium_1
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 2; j++)
                    for (int k = 0; k < 2; k++)
                        testEvolutionaryAlgorithmCrossoverMutation(debug, problems[3], instances[3], i, j, k, 0.1);

            // instance medium_2
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 2; j++)
                    for (int k = 0; k < 2; k++)
                        testEvolutionaryAlgorithmCrossoverMutation(debug, problems[4], instances[4], i, j, k, 0.1);
        }
    }

    @SuppressWarnings("unused")
    public void fullCalibrateEASelection(boolean debug) {
        // instance easy_0
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                for (int k = 0; k < 2; k++)
                    testEvolutionaryAlgorithmSelection(debug, problems[0], instances[0], i, j, k);

        // instance easy_1
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                for (int k = 0; k < 2; k++)
                    testEvolutionaryAlgorithmSelection(debug, problems[1], instances[1], i, j, k);

        if (greedyMode != 2) {
            // instance medium_0
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 2; j++)
                    for (int k = 0; k < 2; k++)
                        testEvolutionaryAlgorithmSelection(debug, problems[2], instances[2], i, j, k);

            // instance medium_1
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 2; j++)
                    for (int k = 0; k < 2; k++)
                        testEvolutionaryAlgorithmSelection(debug, problems[3], instances[3], i, j, k);

            // instance medium_2
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 2; j++)
                    for (int k = 0; k < 2; k++)
                        testEvolutionaryAlgorithmSelection(debug, problems[4], instances[4], i, j, k);
        }
    }

    private void testEvolutionaryAlgorithmIterPopSize(boolean debug, TTP problem, String modifier,
                                                      int selectionMode, int crossoverMode, int mutationMode,
                                                      double tournamentSize, double cxProbability, double mxProbability) {
        int[] noOfIterations = {1000, 1500, 2000};
        int[] populationSizes = {50, 100, 250};
        if (greedyMode == 2) {
            noOfIterations = new int[]{250, 500, 1000};
            populationSizes = new int[]{10, 50, 100};
        }

        for (int iter : noOfIterations)
            for (int populationSize : populationSizes){
                String selection, crossover, mutation;
                if (selectionMode == 0) selection = "TOURNAMENT"; else selection = "ROULETTE";
                if (crossoverMode == 0) crossover = "OX"; else crossover = "PMX";
                if (mutationMode == 0) mutation = "SWAP"; else mutation = "INVERSION";
                if (debug)
                    System.out.println("------------------------------------------------ " + selection + " "  + crossover + " " + mutation + " " + iter + " " + populationSize + " " + modifier);
                runEA(debug, "results\\ea_raw\\greedy_" + greedyMode + "\\" + modifier + "_ITER_POP_SIZE.csv",
                        iter + ";" + populationSize + ";"  + selection + ";" + crossover + ";" + mutation,
                        selectionMode, crossoverMode, mutationMode, problem, iter, populationSize,
                        tournamentSize, 7, cxProbability, mxProbability, false);
            }
    }

    private void testEvolutionaryAlgorithmCrossoverMutation(boolean debug, TTP problem, String modifier,
                                                            int selectionMode, int crossoverMode, int mutationMode,
                                                            double tournamentSize) {
        double[] cxProbabilities = {0.6, 0.7, 0.8};
        double[] swapProbabilities = {0.01, 0.02, 0.05};
        double[] inversionProbabilities = {0.1, 0.2, 0.3};

        int noOfGenerations = 2000, populationSize = 250;
        switch (greedyMode) {
            case 0:
                switch (modifier) {
                    case "easy_0":
                    case "easy_1":
                    case "medium_1":
                    case "medium_2":
                        break;
                    case "medium_0":
                        populationSize = 100;
                        break;
                }
                break;
            case 1:
                switch (modifier) {
                    case "easy_0":
                    case "easy_1":
                    case "medium_0":
                    case "medium_1":
                    case "medium_2":
                        break;
                }
                break;
            case 2:
                noOfGenerations = 1000;
                populationSize = 100;
        }

        for (double cx : cxProbabilities)
            for (int i = 0; i < swapProbabilities.length; i++){
                String selection, crossover, mutation;
                if (selectionMode == 0) selection = "TOURNAMENT"; else selection = "ROULETTE";
                if (crossoverMode == 0) crossover = "OX"; else crossover = "PMX";
                if (mutationMode == 0) {
                    mutation = "SWAP";
                    if (debug)
                        System.out.println("------------------------------------------------ " + selection + " "  + crossover + " " + mutation + " " + cx + " " + swapProbabilities[i] + " " + modifier);
                    runEA(debug, "results\\ea_raw\\greedy_" + greedyMode + "\\" + modifier + "_CX_MX.csv",
                            cx + ";" + swapProbabilities[i] + ";"  + selection + ";" + crossover + ";" + mutation,
                            selectionMode, crossoverMode, mutationMode, problem, noOfGenerations, populationSize,
                            tournamentSize, 7, cx, swapProbabilities[i], false);
                } else {
                    mutation = "INVERSION";
                    if (debug)
                        System.out.println("------------------------------------------------ " + selection + " "  + crossover + " " + mutation + " " + cx + " " + inversionProbabilities[i] + " " + modifier);
                    runEA(debug, "results\\ea_raw\\greedy_" + greedyMode + "\\" + modifier + "_CX_MX.csv",
                            cx + ";" + inversionProbabilities[i] + ";"  + selection + ";" + crossover + ";" + mutation,
                            selectionMode, crossoverMode, mutationMode, problem, noOfGenerations, populationSize,
                            tournamentSize, 7, cx, inversionProbabilities[i], false);
                }
            }
    }

    private void testEvolutionaryAlgorithmSelection(boolean debug, TTP problem, String modifier, int selectionMode,
                                                    int crossoverMode, int mutationMode) {
        double[] tournamentSizes = {0.01, 0.05, 0.1};
        int[] roulettePressures = {5, 7, 9};

        int noOfGenerations = 2000, populationSize = 250;
        double cxProbability = 0.8, mxProbability = 0.02;
        switch (greedyMode) {
            case 0:
                switch (modifier) {
                    case "easy_0":
                        mxProbability = 0.2;
                        break;
                    case "easy_1":
                    case "medium_2":
                        cxProbability = 0.6;
                        mxProbability = 0.3;
                        break;
                    case "medium_0":
                        cxProbability = 0.6;
                        mxProbability = 0.3;
                        populationSize = 100;
                        break;
                    case "medium_1":
                        mxProbability = 0.3;
                        break;
                }
                break;
            case 1:
                switch (modifier) {
                    case "easy_0":
                    case "medium_0":
                    case "medium_1":
                        cxProbability = 0.6;
                        mxProbability = 0.3;
                        break;
                    case "easy_1":
                        mxProbability = 0.3;
                        break;
                    case "medium_2":
                        cxProbability = 0.6;
                        mxProbability = 0.2;
                        break;
                }
                break;
            case 2:
                noOfGenerations = 1000;
                populationSize = 100;
                switch (modifier) {
                    case "easy_0":
                        mxProbability = 0.1;
                        break;
                    case "easy_1":
                        cxProbability = 0.7;
                        mxProbability = 0.2;
                        break;
                }
                break;
        }

        for (int i = 0; i < tournamentSizes.length; i++){
            String selection, crossover, mutation;
            if (crossoverMode == 0) crossover = "OX"; else crossover = "PMX";
            if (mutationMode == 0) mutation = "SWAP"; else mutation = "INVERSION";
            if (selectionMode == 0) {
                selection = "TOURNAMENT";
                if (debug)
                    System.out.println("------------------------------------------------ " + selection + " "  + crossover + " " + mutation + " " + tournamentSizes[i] + " " + modifier);
                runEA(debug, "results\\ea_raw\\greedy_" + greedyMode + "\\" + modifier + "_SELECTION.csv",
                        tournamentSizes[i] + ";"  + selection + ";" + crossover + ";" + mutation,
                        selectionMode, crossoverMode, mutationMode, problem, noOfGenerations, populationSize,
                        tournamentSizes[i], 7, cxProbability, mxProbability, false);
            } else {
                selection = "ROULETTE";
                if (debug)
                    System.out.println("------------------------------------------------ " + selection + " "  + crossover + " " + mutation + " " + roulettePressures[i] + " " + modifier);
                runEA(debug, "results\\ea_raw\\greedy_" + greedyMode + "\\" + modifier + "_SELECTION.csv",
                        roulettePressures[i] + ";"  + selection + ";" + crossover + ";" + mutation,
                        selectionMode, crossoverMode, mutationMode, problem, noOfGenerations, populationSize,
                        roulettePressures[i], 7, cxProbability, mxProbability, false);
            }
        }
    }

    @SuppressWarnings("unused")
    public void calibrateEA(boolean debug) {
        EA_testPopulationSizeNoOfGenerations(debug, problems[0], "_easy_0");
        EA_testPopulationSizeNoOfGenerations(debug, problems[1], "_medium_0");
        EA_testCrossoverProbability(debug, problems[0], 500, "_easy_0");
        EA_testCrossoverProbability(debug, problems[1], 100, "_medium_0");
        EA_testMutationProbability(debug, problems[0], 500, 0.9, "_easy_0");
        EA_testMutationProbability(debug, problems[1], 100, 0.7, "_medium_0");
        EA_testTournamentSize(debug, problems[0], 500, 0.9, 0.1, "_easy_0");
        EA_testTournamentSize(debug, problems[1], 100, 0.7, 0.3, "_medium_0");
        EA_testRoulettePressure(debug, problems[0], 500, 0.9, 0.1, "_easy_0");
        EA_testRoulettePressure(debug, problems[1], 100, 0.7, 0.3, "_medium_0");
        EA_testTournamentVsRoulette(debug, "results\\ea_raw\\T_easy_0.csv", "results\\ea_raw\\R_easy_0.csv", problems[0], 500, 0.05, 0.9, 0.1);
        EA_testTournamentVsRoulette(debug, "results\\ea_raw\\T_medium_0.csv", "results\\ea_raw\\R_medium_0.csv", problems[1], 100, 0.1, 0.7, 0.3);
    }

    private void EA_testPopulationSizeNoOfGenerations(boolean debug, TTP problem, String modifier) {
        int[] populationSize = {10, 100, 500};
        int[] noOfGenerations = {10, 100, 500};

        for (int popSize : populationSize)
            for (int gen : noOfGenerations) {
                if (debug)
                    System.out.println("--------------------------------------------------- TOURNAMENT, ORDERED, SWAP " + modifier);
                runEA(debug, "results\\ea_raw\\pop_size_gen_T_OX_S" + modifier + ".csv", popSize + ";" + gen,
                        0, 0, 0, problem, gen, popSize,
                        0.05, 5, 0.7, 0.01, false);
            }
    }

    private void EA_testCrossoverProbability(boolean debug, TTP problem, int populationSize, String modifier) {
        double[] crossoverProbability = new double[]{0.1, 0.3, 0.5, 0.7, 0.9};

        for (double probability : crossoverProbability) {
            if (debug)
                System.out.println("--------------------------------------------------- TOURNAMENT, ORDERED, SWAP " + modifier);
            runEA(debug, "results\\ea_raw\\cx_T_OX_S" + modifier + ".csv", Double.toString(probability),
                    0, 0, 0, problem, 500, populationSize,
                    0.05, 5, probability, 0.01, false);
            if (debug)
                System.out.println("--------------------------------------------------- TOURNAMENT, PARTIALLY MATCHED, SWAP");
            runEA(debug, "results\\ea_raw\\cx_T_PMX_S" + modifier + ".csv", Double.toString(probability),
                    0, 1, 0, problem, 500, populationSize,
                    0.05, 5, probability, 0.01, false);
        }
    }

    private void EA_testMutationProbability(boolean debug, TTP problem, int populationSize, double crossoverProbability, String modifier) {
        double[] swapProbability = new double[]{0.001, 0.01, 0.05, 0.1, 0.2};
        double[] inversionProbability = new double[]{0.01, 0.05, 0.1, 0.3, 0.5};

        for (int i = 0; i < swapProbability.length; i++) {
            if (debug)
                System.out.println("--------------------------------------------------- TOURNAMENT, ORDERED, SWAP " + modifier);
            runEA(debug, "results\\ea_raw\\mx_T_OX_S" + modifier + ".csv", Double.toString(swapProbability[i]),
                    0, 0, 0, problem, 500, populationSize,
                    0.05, 5, crossoverProbability, swapProbability[i], false);
            System.out.println("--------------------------------------------------- TOURNAMENT, ORDERED, INVERSION " + modifier);
            runEA(debug, "results\\ea_raw\\mx_T_OX_I" + modifier + ".csv", Double.toString(inversionProbability[i]),
                    0, 0, 1, problem, 500, populationSize,
                    0.05, 5, crossoverProbability, inversionProbability[i], false);
        }
    }

    private void EA_testTournamentSize(boolean debug, TTP problem, int populationSize, double crossoverProbability, double mutationProbability, String modifier) {
        double [] tournamentSize;
        if (populationSize == 100)
            tournamentSize = new double[]{0.01, 0.05, 0.1, 0.5, 1};
        else
            tournamentSize = new double[]{0.002, 0.05, 0.1, 0.5, 1};

        for (double size : tournamentSize) {
            if (debug)
                System.out.println("--------------------------------------------------- TOURNAMENT, ORDERED, INVERSION " + modifier);
            runEA(debug, "results\\ea_raw\\tournament_size_T_OX_I" + modifier + ".csv", Double.toString(size),
                    0, 0, 1, problem, 500, populationSize,
                    size, 5, crossoverProbability, mutationProbability, false);
        }
    }

    private void EA_testRoulettePressure(boolean debug, TTP problem, int populationSize, double crossoverProbability, double mutationProbability, String modifier) {
        int[] roulettePressure = {1, 3, 5, 7, 9};

        for (int pressure : roulettePressure) {
            if (debug)
                System.out.println("--------------------------------------------------- ROULETTE, ORDERED, INVERSION " + modifier);
            runEA(debug, "results\\ea_raw\\roulette_pressure_R_OX_I" + modifier + ".csv", Double.toString(pressure),
                    1, 0, 1, problem, 500, populationSize,
                    0.05, pressure, crossoverProbability, mutationProbability, false);
        }
    }

    private void EA_testTournamentVsRoulette(boolean debug, String firstFile, String secondFile, TTP problem,
                                             int populationSize, double tournamentSizePercent,
                                             double crossoverProbability, double mutationProbability) {
        if (debug)
            System.out.println("--------------------------------------------------------------------------------------------------------------------- GRAPHS " + firstFile + ", " + secondFile);
        EA ea = new EA(debug, problem, 500, populationSize, tournamentSizePercent, 7,
                crossoverProbability, mutationProbability, false);
        if (debug)
            System.out.println("--------------------------------------------------- TOURNAMENT, ORDERED, INVERSION");
        ea.run(0, 0, 1);
        logger.loadIntoFile(firstFile, ea.getBests(), ea.getWorsts(), ea.getAvgs());
        if (debug)
            System.out.println("--------------------------------------------------- ROULETTE, ORDERED, INVERSION");
        ea.run(1, 0, 1);
        logger.loadIntoFile(secondFile, ea.getBests(), ea.getWorsts(), ea.getAvgs());
    }

////METAHEURYSTYKA TABU SEARCH//////////////////////////////////////////////////////////////////////////////////////////

    private void runTS(boolean debug, String filename, String params, int mode, TTP problem,
                       int noOfNeighbours, int noOfIterations, int tabuSize, boolean aspirationCriteria,
                       boolean turnOffBackpack) {
        long startTime = System.currentTimeMillis();
        double[] bests = new double[10];
        double best = Integer.MIN_VALUE;
        double worst = Integer.MAX_VALUE;
        double avg = 0.0;
        double std = 0.0;
        TS ts = new TS(false, problem, noOfNeighbours, noOfIterations, tabuSize, aspirationCriteria, turnOffBackpack);
        for (int i = 0; i < 10; i++) {
            if (debug)
                System.out.println("------------------------ RUN" + i);
            ts.run(mode);
            double currentBest = ts.getBest(), currentWorst = ts.getWorst();
            bests[i] = currentBest;
            if (currentBest > best)
                best = currentBest;
            if (currentWorst < worst)
                worst = currentWorst;
            avg += currentBest;
        }
        avg = avg / ((double) 10);
        for (double value : bests)
            std += Math.pow(avg - value, 2);
        std = std / ((double) 10);
        std = Math.sqrt(std);
        long elapsedTime = System.currentTimeMillis() - startTime;
        logger.loadIntoFile(filename, params, best, worst, avg, std, ((double) elapsedTime) / 10.0);
    }

    @SuppressWarnings("unused")
    public void getStatsTS(boolean debug, int mode, int noOfNeighbours, int noOfIterations, int tabuSize,
                           boolean aspirationCriteria, boolean turnOffBackpack) {
        for (int i = 0; i < filenames.length; i++) {
            if (debug)
                System.out.println("------------------------------------------------ " + outputs[2] + " " + instances[i]);
            runTS(debug, outputs[4], instances[i], mode, problems[i], noOfNeighbours, noOfIterations, tabuSize,
                    aspirationCriteria, turnOffBackpack);
        }
    }

    public void runOnceTS(boolean debug, String filename, String instance, int mode,
                          int noOfNeighbours, int noOfIterations, int tabuSize, boolean aspirationCriteria,
                          boolean turnOffBackpack) {
        int index = -1;
        for (int i = 0; i < instances.length; i++)
            if (Objects.equals(instances[i], instance))
                index = i;
        TS ts = new TS(debug, problems[index], noOfNeighbours, noOfIterations, tabuSize, aspirationCriteria, turnOffBackpack);
        ts.run(mode);
        logger.loadIntoFile(filename, ts.getCurrents(), ts.getBests(), ts.getWorsts(), ts.getAvgs());
    }

////STROJENIE METAHEURYSTYKI TS/////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    public void fullRunTS(boolean debug) {
        // instance easy_0
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[4] + " " + instances[0]);
        runTS(debug, outputs[4], instances[0], 0, problems[0], 50, 2500, 200, true, false);
        runOnceTS(false, "results\\ts_raw\\graph_easy_0.csv", instances[0], 0, 50, 2500, 200, true, false);

        // instance easy_1
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[4] + " " + instances[1]);
        runTS(debug, outputs[4], instances[1], 1, problems[1], 100, 2500, 150, false, false);
        runOnceTS(false, "results\\ts_raw\\graph_easy_1.csv", instances[1], 1, 100, 2500, 150, false, false);

        // instance medium_0
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[4] + " " + instances[2]);
        runTS(debug, outputs[4], instances[2], 1, problems[2], 200, 2500, 100, true, false);
        runOnceTS(false, "results\\ts_raw\\graph_medium_0.csv", instances[2], 1, 200, 2500, 100, true, false);

        // instance medium_1
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[4] + " " + instances[3]);
        runTS(debug, outputs[4], instances[3], 1, problems[3], 200, 2500, 50, false, false);
        runOnceTS(false, "results\\ts_raw\\graph_medium_1.csv", instances[3], 1, 200, 2500, 50, false, false);

        // instance medium_2
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[4] + " " + instances[4]);
        runTS(debug, outputs[4], instances[4], 1, problems[4], 100, 2500, 100, false, false);
        runOnceTS(false, "results\\ts_raw\\graph_medium_2.csv", instances[4], 1, 100, 2500, 100, false, false);

        // instance hard_0
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[4] + " " + instances[5]);
        runTS(debug, outputs[4], instances[5], 0, problems[5], 10, 2500, 100, false, false);
        runOnceTS(false, "results\\ts_raw\\graph_hard_0.csv", instances[5], 0, 10, 2500, 100, false, false);

        // instance hard_1
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[4] + " " + instances[6]);
        runTS(debug, outputs[4], instances[6], 0, problems[6], 10, 2500, 100, false, false);
        runOnceTS(false, "results\\ts_raw\\graph_hard_1.csv", instances[6], 0, 10, 2500, 100, false, false);
    }

    @SuppressWarnings("unused")
    public void fullCalibrateTS(boolean debug) {
        // instance easy_0
        testTabuSearch(debug, problems[0], instances[0], 0, false);
        testTabuSearch(debug, problems[0], instances[0], 1, false);
        testTabuSearch(debug, problems[0], instances[0], 0, true);
        testTabuSearch(debug, problems[0], instances[0], 1, true);

        // instance easy_1
        testTabuSearch(debug, problems[1], instances[1], 0, false);
        testTabuSearch(debug, problems[1], instances[1], 1, false);
        testTabuSearch(debug, problems[1], instances[1], 0, true);
        testTabuSearch(debug, problems[1], instances[1], 1, true);

        // instance medium_0
        testTabuSearch(debug, problems[2], instances[2], 0, false);
        testTabuSearch(debug, problems[2], instances[2], 1, false);
        testTabuSearch(debug, problems[2], instances[2], 0, true);
        testTabuSearch(debug, problems[2], instances[2], 1, true);

        // instance medium_1
        testTabuSearch(debug, problems[3], instances[3], 0, false);
        testTabuSearch(debug, problems[3], instances[3], 1, false);
        testTabuSearch(debug, problems[3], instances[3], 0, true);
        testTabuSearch(debug, problems[3], instances[3], 1, true);

        // instance medium_2
        testTabuSearch(debug, problems[4], instances[4], 0, false);
        testTabuSearch(debug, problems[4], instances[4], 1, false);
        testTabuSearch(debug, problems[4], instances[4], 0, true);
        testTabuSearch(debug, problems[4], instances[4], 1, true);
    }

    private void testTabuSearch(boolean debug, TTP problem, String modifier, int mode, boolean aspirationCriteria) {
        int[] noOfIterations = {1000, 1500, 2000, 2500};
        int[] noOfNeighbours = {50, 100, 150, 200};
        int[] tabuSizes = {50, 100, 150, 200};

        for (int iter : noOfIterations)
            for (int neighbours : noOfNeighbours)
                for (int tabu: tabuSizes) {
                    String modeParam, aspirationParam;
                    if (mode == 0) modeParam = "SWAP"; else modeParam = "INVERSION";
                    if (aspirationCriteria) aspirationParam = "YES"; else aspirationParam = "NO";
                    if (debug)
                        System.out.println("------------------------------------------------ " + modeParam + " " + iter + " " + neighbours + " " + tabu + " " + aspirationParam + " " + modifier);
                    runTS(debug, "results\\ts_raw\\" + modifier + ".csv",
                            iter + ";" + neighbours + ";" + tabu + ";" + modeParam + ";" + aspirationParam,
                            mode, problem, neighbours, iter, tabu, aspirationCriteria, false);
                }
    }

    @SuppressWarnings("unused")
    public void calibrateTS(boolean debug) {
        TS_testNoOfIterationsNoOfNeighbours(debug, problems[0], "_easy_0");
        TS_testNoOfIterationsNoOfNeighbours(debug, problems[1], "_medium_0");
        TS_testTabuSize(debug, problems[0], "_easy_0");
        TS_testTabuSize(debug, problems[1], "_medium_0");
        TS_testAspirationCriteriaVsNone(debug);
        TS_graphAspirationCriteriaVsNone(debug, "results\\ts_raw\\I_AC_easy_0.csv", "results\\ts_raw\\I_NAC_easy_0.csv", "easy_0");
        TS_graphAspirationCriteriaVsNone(debug, "results\\ts_raw\\I_AC_medium_0.csv", "results\\ts_raw\\I_NAC_medium_0.csv", "medium_0");
        TS_graphNeighbourhoodMethods(debug, "results\\ts_raw\\SvsI_S_easy_0.csv", "results\\ts_raw\\SvsI_I_easy_0.csv", "easy_0", 500);
        TS_graphNeighbourhoodMethods(debug, "results\\ts_raw\\SvsI_S_medium_0.csv", "results\\ts_raw\\SvsI_I_medium_0.csv", "medium_0", 100);
    }

    private void TS_testNoOfIterationsNoOfNeighbours(boolean debug, TTP problem, String modifier) {
        int[] noOfIterations = {10, 100, 1000, 2500};
        int[] noOfNeighbours = {1, 10, 100, 250};

        for (int iter : noOfIterations)
            for (int nSize : noOfNeighbours) {
                if (debug)
                    System.out.println("--------------------------------------------------- SWAP, WITH ASPIRATION CRITERIA " + modifier);
                runTS(debug, "results\\ts_raw\\iter_n_size_S_AC" + modifier + ".csv", iter + ";" + nSize,
                        0, problem, nSize, iter, 100, true, false);
            }
    }

    private void TS_testTabuSize(boolean debug, TTP problem, String modifier) {
        int[] tabuSizes = {0, 1, 100, 500, 1000};

        for (int tabuSize : tabuSizes) {
            if (debug)
                System.out.println("--------------------------------------------------- SWAP, WITH ASPIRATION CRITERIA " + modifier);
            runTS(debug, "results\\ts_raw\\tabu_size_S_AC" + modifier + ".csv", tabuSize + "",
                    1, problem, 2500, 100, tabuSize, true, false);
            if (debug)
                System.out.println("--------------------------------------------------- INVERSION, WITH ASPIRATION CRITERIA " + modifier);
            runTS(debug, "results\\ts_raw\\tabu_size_I_AC" + modifier + ".csv", tabuSize + "",
                    1, problem, 2500, 100, tabuSize, true, false);
        }
    }

    private void TS_testAspirationCriteriaVsNone(boolean debug) {
        if (debug)
            System.out.println("--------------------------------------------------- ASPIRATION CRITERIA _easy_0");
        runTS(debug, "results\\ts_raw\\aspiration_criteria.csv", "easy_0 AC", 1, problems[0],
                100, 2500, 100, true, false);
        if (debug)
            System.out.println("--------------------------------------------------- NO ASPIRATION CRITERIA _easy_0");
        runTS(debug, "results\\ts_raw\\aspiration_criteria.csv", "easy_0 no AC", 1, problems[0],
                100, 2500, 100, false, false);
        if (debug)
            System.out.println("--------------------------------------------------- ASPIRATION CRITERIA _medium_0");
        runTS(debug, "results\\ts_raw\\aspiration_criteria.csv", "medium_0 AC", 1, problems[1],
                100, 2500, 100, true, false);
        if (debug)
            System.out.println("--------------------------------------------------- NO ASPIRATION CRITERIA _medium_0");
        runTS(debug, "results\\ts_raw\\aspiration_criteria.csv", "medium_0 no AC", 1, problems[1],
                100, 2500, 100, false, false);

    }

    private void TS_graphAspirationCriteriaVsNone(boolean debug, String firstFile, String secondFile, String instance) {
        if (debug)
            System.out.println("--------------------------------------------------------------------------------------------------------------------- GRAPHS " + firstFile + ", " + secondFile);
        if (debug)
            System.out.println("--------------------------------------------------- ASPIRATION CRITERIA");
        runOnceTS(debug, firstFile, instance, 1, 100, 2500, 100, true, false);
        if (debug)
            System.out.println("--------------------------------------------------- NO ASPIRATION CRITERIA");
        runOnceTS(debug, firstFile, instance, 1, 100, 2500, 100, false, false);
    }

    private void TS_graphNeighbourhoodMethods(boolean debug, String firstFile, String secondFile, String instance, int swapTabuSize) {
        if (debug)
            System.out.println("--------------------------------------------------------------------------------------------------------------------- GRAPHS " + firstFile + ", " + secondFile);
        if (debug)
            System.out.println("--------------------------------------------------- SWAP");
        runOnceTS(debug, firstFile, instance, 0, 100, 2500, swapTabuSize, false, false);
        if (debug)
            System.out.println("--------------------------------------------------- INVERSION");
        runOnceTS(debug, firstFile, instance, 1, 100, 2500, 100, false, false);
    }

////METAHEURYSTYKA SIMULATED ANNEALING//////////////////////////////////////////////////////////////////////////////////

    private void runSA(boolean debug, String filename, String params, int mode, TTP problem,
                       int noOfNeighbours, int noOfIterations, double startTemp, double endTemp, double coolingSchema,
                       boolean turnOffBackpack) {
        long startTime = System.currentTimeMillis();
        double[] bests = new double[10];
        double best = Integer.MIN_VALUE;
        double worst = Integer.MAX_VALUE;
        double avg = 0.0;
        double std = 0.0;
        SA sa = new SA(false, problem, noOfNeighbours, noOfIterations, startTemp, endTemp, coolingSchema, turnOffBackpack);
        for (int i = 0; i < 10; i++) {
            if (debug)
                System.out.println("------------------------ RUN" + i);
            sa.run(mode);
            double currentBest = sa.getBest(), currentWorst = sa.getWorst();
            bests[i] = currentBest;
            if (currentBest > best)
                best = currentBest;
            if (currentWorst < worst)
                worst = currentWorst;
            avg += currentBest;
        }
        avg = avg / ((double) 10);
        for (double value : bests)
            std += Math.pow(avg - value, 2);
        std = std / ((double) 10);
        std = Math.sqrt(std);
        long elapsedTime = System.currentTimeMillis() - startTime;
        logger.loadIntoFile(filename, params, best, worst, avg, std, ((double) elapsedTime) / 10.0);
    }

    @SuppressWarnings("unused")
    public void getStatsSA(boolean debug, int mode, int noOfNeighbours, int noOfIterations, double startTemp,
                           double endTemp, double coolingSchema, boolean turnOffBackpack) {
        for (int i = 0; i < filenames.length; i++) {
            if (debug)
                System.out.println("------------------------------------------------ " + outputs[2] + " " + instances[i]);
            runSA(debug, outputs[5], filenames[i], mode, problems[i], noOfNeighbours, noOfIterations,
                    startTemp, endTemp, coolingSchema, turnOffBackpack);
        }
    }


    public void runOnceSA(boolean debug, String filename, String instance, int mode, int noOfNeighbours, int noOfIterations,
                          double startTemp, double endTemp, double coolingSchema, boolean turnOffBackpack) {
        int index = -1;
        for (int i = 0; i < instances.length; i++)
            if (Objects.equals(instances[i], instance))
                index = i;
        SA sa = new SA(debug, problems[index], noOfNeighbours, noOfIterations, startTemp, endTemp, coolingSchema, turnOffBackpack);
        sa.run(mode);
        logger.loadIntoFile(filename, sa.getCurrents(), sa.getBests(), sa.getWorsts(), sa.getAvgs());
    }


////STROJENIE METAHEURYSTYKI SA/////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    public void fullRunSA(boolean debug) {
        // instance easy_0
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[5] + " " + instances[0]);
        runSA(debug, outputs[5], instances[0], 1, problems[0], 100, 2500, 1250, 0.05, 0.9995, false);
        runOnceSA(false, "results\\sa_raw\\graph_easy_0.csv", instances[0], 1, 100, 2500, 1250, 0.05, 0.9995, false);

        // instance easy_1
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[5] + " " + instances[1]);
        runSA(debug, outputs[5], instances[1], 1, problems[1], 200, 2500, 1500, 0.05, 0.999, false);
        runOnceSA(false, "results\\sa_raw\\graph_easy_1.csv", instances[1], 1, 200, 2500, 1500, 0.05, 0.999, false);

        // instance medium_0
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[5] + " " + instances[2]);
        runSA(debug, outputs[5], instances[2], 1, problems[2], 150, 2500, 750, 0.001, 0.999, false);
        runOnceSA(false, "results\\sa_raw\\graph_medium_0.csv", instances[2], 1, 150, 2500, 750, 0.001, 0.999, false);

        // instance medium_1
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[4] + " " + instances[3]);
        runSA(debug, outputs[5], instances[3], 0, problems[3], 100, 2500, 750, 0.01, 0.995, false);
        runOnceSA(false, "results\\sa_raw\\graph_medium_1.csv", instances[3], 0, 100, 2500, 750, 0.01, 0.995, false);

        // instance medium_2
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[5] + " " + instances[4]);
        runSA(debug, outputs[5], instances[4], 1, problems[4], 100, 2500, 1000, 0.05, 0.995, false);
        runOnceSA(false, "results\\sa_raw\\graph_medium_2.csv", instances[4], 1, 100, 2500, 1000, 0.05, 0.995, false);

        // instance hard_0
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[5] + " " + instances[5]);
        runSA(debug, outputs[5], instances[5], 0, problems[5], 10, 2500, 1000, 0.05, 0.995, false);
        runOnceSA(false, "results\\sa_raw\\graph_hard_0.csv", instances[5], 1, 10, 2500, 1000, 0.05, 0.995, false);

        // instance hard_1
        if (debug)
            System.out.println("------------------------------------------------ " + outputs[5] + " " + instances[6]);
        runSA(debug, outputs[5], instances[6], 0, problems[6], 10, 2500, 1000, 0.05, 0.995, false);
        runOnceSA(false, "results\\sa_raw\\graph_hard_1.csv", instances[6], 1, 10, 2500, 1000, 0.05, 0.995, false);
    }

    @SuppressWarnings("unused")
    public void fullCalibrateSA(boolean debug) {
        fullCalibrateSAIterNeighbours(debug);
        fullCalibrateSATemperature(debug);
    }

    public void fullCalibrateSAIterNeighbours(boolean debug) {
        // instance easy_0
        testSimulatedAnnealingIterNeighbours(debug, problems[0], instances[0], 0, 0.01);
        testSimulatedAnnealingIterNeighbours(debug, problems[0], instances[0], 1, 0.01);

        // instance easy_1
        testSimulatedAnnealingIterNeighbours(debug, problems[1], instances[1], 0, 0.01);
        testSimulatedAnnealingIterNeighbours(debug, problems[1], instances[1], 1, 0.01);

        // instance medium_0
        testSimulatedAnnealingIterNeighbours(debug, problems[2], instances[2], 0, 0.001);
        testSimulatedAnnealingIterNeighbours(debug, problems[2], instances[2], 1, 0.001);

        // instance medium_1
        testSimulatedAnnealingIterNeighbours(debug, problems[3], instances[3], 0, 0.001);
        testSimulatedAnnealingIterNeighbours(debug, problems[3], instances[3], 1, 0.001);

        // instance medium_2
        testSimulatedAnnealingIterNeighbours(debug, problems[4], instances[4], 0, 0.001);
        testSimulatedAnnealingIterNeighbours(debug, problems[4], instances[4], 1, 0.001);
    }

    public void fullCalibrateSATemperature(boolean debug) {
        // instance easy_0
        testSimulatedAnnealingTemperature(debug, problems[0], instances[0], 0, 100);
        testSimulatedAnnealingTemperature(debug, problems[0], instances[0], 1, 100);

        // instance easy_1
        testSimulatedAnnealingTemperature(debug, problems[1], instances[1], 0, 150);
        testSimulatedAnnealingTemperature(debug, problems[1], instances[1], 1, 200);

        // instance medium_0
        testSimulatedAnnealingTemperature(debug, problems[2], instances[2], 0, 200);
        testSimulatedAnnealingTemperature(debug, problems[2], instances[2], 1, 150);

        // instance medium_1
        testSimulatedAnnealingTemperature(debug, problems[3], instances[3], 0, 100);
        testSimulatedAnnealingTemperature(debug, problems[3], instances[3], 1, 150);

        // instance medium_2
        testSimulatedAnnealingTemperature(debug, problems[4], instances[4], 0, 200);
        testSimulatedAnnealingTemperature(debug, problems[4], instances[4], 1, 100);
    }

    private void testSimulatedAnnealingIterNeighbours(boolean debug, TTP problem, String modifier, int mode, double tempMin) {
        int[] noOfIterations = {1000, 1500, 2000, 2500};
        int[] noOfNeighbours = {50, 100, 150, 200};

        for (int iter : noOfIterations)
            for (int neighbours : noOfNeighbours){
                String modeParam;
                if (mode == 0) modeParam = "SWAP"; else modeParam = "INVERSION";
                if (debug)
                    System.out.println("------------------------------------------------ " + modeParam + " " + iter + " " + neighbours + " " + modifier);
                runSA(debug, "results\\sa_raw\\" + modifier + "_ITER_NEIGHBOURS.csv",
                        iter + ";" + neighbours + ";"  + modeParam, mode, problem, neighbours, iter,
                        1000.0, tempMin, 0.9995, false);
            }
    }

    private void testSimulatedAnnealingTemperature(boolean debug, TTP problem, String modifier, int mode, int noOfNeighbours) {
        double[] tempsMax = {750.0, 1000.0, 1250.0, 1500.0};
        double[] tempsMin = {0.01, 0.05, 0.005, 0.001};
        double[] coolingSchemas = {0.995, 0.999, 0.9995, 0.9999};

        for (double tempMax: tempsMax)
            for (double tempMin: tempsMin)
                for (double schema: coolingSchemas) {
                    String modeParam;
                    if (mode == 0) modeParam = "SWAP"; else modeParam = "INVERSION";
                    if (debug)
                        System.out.println("------------------------------------------------ " + modeParam + " " + tempMax + " " + tempMin + " "  + schema + " " + modifier);
                    runSA(debug, "results\\sa_raw\\" + modifier + "_TEMPERATURE.csv",
                            tempMax + ";" + tempMin + ";" + schema + ";" + modeParam, mode, problem, noOfNeighbours,
                            2500, tempMax, tempMin, schema, false);
                }
    }

    @SuppressWarnings("unused")
    public void calibrateSA(boolean debug) {
        SA_testNoOfIterationsNoOfNeighbours(debug, problems[0], "_easy_0");
        SA_testNoOfIterationsNoOfNeighbours(debug, problems[1], "_medium_0");
        SA_testTemperature(debug, problems[0], "_easy_0");
        SA_testTemperature(debug, problems[1], "_medium_0");
        SA_graphTemperature(debug, "easy_0", "_easy_0", 0.01);
        SA_graphTemperature(debug, "medium_0", "_medium_0", 0.001);
        SA_testNeighbourhoodMethods(debug, problems[0], "_easy_0", 0.01);
        SA_testNeighbourhoodMethods(debug, problems[1], "_medium_0", 0.001);
        SA_graphNeighbourhoodMethods(debug, "easy_0", "_easy_0", 0.01);
        SA_graphNeighbourhoodMethods(debug, "medium_0", "_medium_0", 0.001);
    }

    private void SA_testNoOfIterationsNoOfNeighbours(boolean debug, TTP problem, String modifier) {
        int[] noOfIterations = {100, 1000, 2500, 5000};
        int[] noOfNeighbours = {1, 10, 50, 100};

        for (int iter : noOfIterations)
            for (int nSize : noOfNeighbours) {
                if (debug)
                    System.out.println("--------------------------------------------------- SWAP " + iter + " " + nSize + " " + modifier);
                runSA(debug, "results\\sa_raw\\iter_n_size_S" + modifier + ".csv", iter + ";" + nSize,
                        0, problem, nSize, iter, 1000, 0.0001, 0.9995, false);
            }
    }

    private void SA_testTemperature(boolean debug, TTP problem, String modifier) {
        int[] maxTemp = {100, 1000, 5000};
        double[] minTemp = {0.0001, 0.001, 0.01};
        double[] coolingSchema = {0.9995, 0.995, 0.95};

        for (int max : maxTemp)
            for (double min : minTemp)
                for (double schema: coolingSchema) {
                    if (debug)
                        System.out.println("--------------------------------------------------- SWAP " + max + " " + min + " " + schema + " " + modifier);
                    runSA(debug, "results\\sa_raw\\temp_S" + modifier + ".csv", max + ";" + min + ";" + schema,
                            0, problem, 50, 5000, max, min, schema, false);
                }
    }

    private void SA_graphTemperature(boolean debug, String instance, String modifier, double endTemp) {
        if (debug)
            System.out.println("--------------------------------------------------------------------------------------------------------------------- GRAPHS BACKPACK ON");
        runOnceSA(debug, "results\\sa_raw\\sa_graph_bp" + modifier + ".csv", instance, 0, 50,
                10000, 1000, endTemp, 0.9995, false);
        if (debug)
            System.out.println("--------------------------------------------------------------------------------------------------------------------- GRAPHS BACKPACK OFF");
        runOnceSA(debug, "results\\sa_raw\\sa_graph_no_bp" + modifier + ".csv", instance, 0, 50,
                10000, 1000, endTemp, 0.9995, true);
    }

    private void SA_testNeighbourhoodMethods(boolean debug, TTP problem, String modifier, double endTemp) {
        if (debug)
            System.out.println("--------------------------------------------------- SWAP " + modifier);
        runSA(debug, "results\\sa_raw\\neighbours" + modifier + ".csv", "SWAP", 0, problem, 10,
                5000, 1000, endTemp, 0.9995, false);
        if (debug)
            System.out.println("--------------------------------------------------- INVERSION " + modifier);
        runSA(debug, "results\\sa_raw\\neighbours" + modifier + ".csv", "INVERSION", 1, problem, 10,
                5000, 1000, endTemp, 0.9995, false);
    }

    private void SA_graphNeighbourhoodMethods(boolean debug, String instance, String modifier, double endTemp) {
        if (debug)
            System.out.println("--------------------------------------------------- SWAP BACKPACK ON " + modifier);
        runOnceSA(debug, "results\\sa_raw\\sa_graph_S_bp" + modifier + ".csv", instance, 0, 10,
                10000, 1000, endTemp, 0.9995, false);
        if (debug)
            System.out.println("--------------------------------------------------- SWAP BACKPACK OFF " + modifier);
        runOnceSA(debug, "results\\sa_raw\\sa_graph_S_no_bp" + modifier + ".csv", instance, 0, 10,
                10000, 1000, endTemp, 0.9995, true);
        if (debug)
            System.out.println("--------------------------------------------------- INVERSION BACKPACK ON " + modifier);
        runOnceSA(debug, "results\\sa_raw\\sa_graph_I_bp" + modifier + ".csv", instance, 1, 10,
                10000, 1000, endTemp, 0.9995, false);
        if (debug)
            System.out.println("--------------------------------------------------- INVERSION BACKPACK OFF " + modifier);
        runOnceSA(debug, "results\\sa_raw\\sa_graph_I_no_bp" + modifier + ".csv", instance, 1, 10,
                10000, 1000, endTemp, 0.9995, true);
    }

}
