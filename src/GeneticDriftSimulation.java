import java.util.Random;
import java.util.Scanner;

public class GeneticDriftSimulation {
    private static final char DOMINANT_ALLELE = 'A';
    private static final char RECESSIVE_ALLELE = 'a';
    private int populationSize;
    private double initialDominantFreq;
    private int generations;
    private Random random;

    public GeneticDriftSimulation(int populationSize, double initialDominantFreq, int generations) {
        this.populationSize = populationSize;
        this.initialDominantFreq = initialDominantFreq;
        this.generations = generations;
        this.random = new Random();

    }


    public void simulate() {
        double dominantFreq = initialDominantFreq;
        double recessiveFreq = 1.0 - dominantFreq;

        System.out.println("Generation\tDominant Frequency (A)\tRecessive Frequency (a)");

        for (int gen = 0; gen < generations; gen++) {
            System.out.printf("%d\t\t%.4f\t\t\t%.4f%n", gen, dominantFreq, recessiveFreq);

            int dominantCount = 0;
            for (int i = 0; i < populationSize; i++) {
                // Simulate reproduction: randomly select an allele from the previous generation's pool
                if (random.nextDouble() < dominantFreq) {
                    dominantCount++;
                }
            }

            // Update frequencies based on new offspring count
            dominantFreq = (double) dominantCount / populationSize;
            recessiveFreq = 1.0 - dominantFreq;

            // If one allele becomes fixed (dominant = 1 or 0), stop simulation
            if (dominantFreq == 1.0 || dominantFreq == 0.0) {
                System.out.printf("Fixation occurred at Generation %d! (Allele %c fixed)%n", gen,
                        dominantFreq == 1.0 ? DOMINANT_ALLELE : RECESSIVE_ALLELE);
                break;
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter population size: ");
        int populationSize = scanner.nextInt();

        System.out.print("Enter initial dominant allele frequency (0 to 1): ");
        double initialFreq = scanner.nextDouble();

        System.out.print("Enter number of generations to simulate: ");
        int generations = scanner.nextInt();

        GeneticDriftSimulation sim = new GeneticDriftSimulation(populationSize, initialFreq, generations);
        sim.simulate();


    }
}
