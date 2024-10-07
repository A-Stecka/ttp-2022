public class main {

    @SuppressWarnings("unused")
    public static void evolutionaryAlgorithm(Research research, boolean debug, boolean turnOffBackpack) {
        //research.calibrateEA(debug);
        //research.getStatsEA(debug, 0, 0, 1, 100, 100, 0.1, 0, 0.7, 0.3, turnOffBackpack);

        research.loadProblems(2);
        //research.fullCalibrateEA(debug);

        research.fullRunEA(true);
    }

    @SuppressWarnings("unused")
    public static void tabuSearch(Research research, boolean debug, boolean turnOffBackpack) {
        //research.calibrateTS(debug);
        //research.getStatsTS(debug, 0, 10, 1000, 100, true, turnOffBackpack);

        research.fullCalibrateTS(debug);
        research.fullRunTS(debug);
    }

    @SuppressWarnings("unused")
    public static void simulatedAnnealing(Research research, boolean debug, boolean turnOffBackPack) {
        //research.calibrateSA(debug);
        //research.getStatsSA(debug, 1, 10, 5000, 1000, 0.001, 0.9995, turnOffBackPack);

        research.fullCalibrateSA(debug);
        research.fullRunSA(debug);
    }

    public static void main(String[] args) {
        @SuppressWarnings("unused")
        String[] instances = new String[]{"easy_0", "easy_1", "medium_0", "medium_1", "medium_2", "hard_0", "hard_1"};
        @SuppressWarnings("unused")
        Research research = new Research();
        @SuppressWarnings("unused")
        boolean debug = true;
        @SuppressWarnings("unused")
        boolean turnOffBackpack = false;
        @SuppressWarnings("unused")
        boolean aspirationCriteria = true;

        //research.getStatsRandom(debug, turnOffBackpack);
        //research.getStatsRandomGreedyBackpack(debug);
        //research.getStatsGreedy(debug);

        evolutionaryAlgorithm(research, debug, turnOffBackpack);
        //tabuSearch(research, debug, turnOffBackpack);
        //simulatedAnnealing(research, debug, turnOffBackpack);

        //research.runOnceEA(debug, "ea_profiler_test.csv", instances[2], 0, 0, 1, 100, 100, 0.1, 0, 0.7, 0.3, turnOffBackpack);
        //research.runOnceTS(debug, "ts_profiler_test.csv", instances[2], 1, 100, 2500, 100, aspirationCriteria, turnOffBackpack);
        //research.runOnceSA(debug, "sa_profiler_test.csv", instances[2], 1, 50, 5000, 1000, 0.001, 0.9995, turnOffBackpack);
    }
}
