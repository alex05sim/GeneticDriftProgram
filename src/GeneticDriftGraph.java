import org.jfree.data.xy.XYSeries;

import java.util.Random;

public class GeneticDriftGraph {
    private int populationSize;
    private double initialDominantFreq;
    private int generations;
    private Random random;

    public GeneticDriftGraph(int populationSize, double initialDominantFreq, int generations) {
        this.populationSize = populationSize;
        this.initialDominantFreq = initialDominantFreq;
        this.generations = generations;
        this.random = new Random();
    }

    public int runSingleSimulation(XYSeries series) {
        double dominantFreq = initialDominantFreq;
        series.add(0, dominantFreq); // ✅ Always add initial point

        for (int gen = 1; gen < generations; gen++) {
            int dominantCount = 0;
            for (int i = 0; i < populationSize; i++) {
                if (random.nextDouble() < dominantFreq) {
                    dominantCount++;
                }
            }

            dominantFreq = (double) dominantCount / populationSize;
            series.add(gen, dominantFreq); // ✅ Ensure data is added

            if (dominantFreq == 1.0 || dominantFreq == 0.0) {
                return gen; // Return number of generations until fixation
            }
        }

        return generations; // If no fixation, return max generations
    }
}