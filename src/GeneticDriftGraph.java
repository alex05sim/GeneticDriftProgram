import org.jfree.data.xy.XYSeries;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
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
        series.add(0, dominantFreq); //  Always add initial point
        try (PrintWriter writer = new PrintWriter(new FileWriter("simulation_log.txt", false))) {
            writer.println("Generation\tDominant Frequency (A)\tRecessive Frequency (a)");
            for (int gen = 1; gen < generations; gen++) {
                int dominantCount = 0;

                for (int i = 0; i < populationSize; i++) {
                    if (random.nextDouble() < dominantFreq) {
                        dominantCount++;
                    }
                }

                dominantFreq = (double) dominantCount / populationSize;
                series.add(gen, dominantFreq); // Ensure data is added
                writer.printf("%d\t%.4f\t%.4f%n", gen, dominantFreq, 1 - dominantFreq);


                if (dominantFreq == 1.0 || dominantFreq == 0.0) {
                    writer.printf("Fixation occurred at Generation %d! Allele %s fixed.%n",
                            gen, dominantFreq == 1.0 ? "A" : "a");
                    return gen; // Return number of generations until fixation
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
        return generations; // If no fixation, return max generations
    }

}