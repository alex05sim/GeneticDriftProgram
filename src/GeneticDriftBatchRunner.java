import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class GeneticDriftBatchRunner {
    private int populationSize;
    private double initialDominantFreq;
    private int generations;
    private int numSimulations;
    private XYSeriesCollection dataset;
    private Random random = new Random();

    // Fixation statistics
    private int fixationA = 0;
    private int fixationa = 0;
    private int[] fixationTimes;

    public GeneticDriftBatchRunner(int populationSize, double initialDominantFreq, int generations, int numSimulations) {
        this.populationSize = populationSize;
        this.initialDominantFreq = initialDominantFreq;
        this.generations = generations;
        this.numSimulations = numSimulations;
        this.dataset = new XYSeriesCollection();
        this.fixationTimes = new int[numSimulations];

        // Run all simulations first, then graph ONCE
        runSimulations();
        printStatistics();
        SwingUtilities.invokeLater(this::createGraph);
    }

    public void runSimulations() {
        for (int i = 0; i < numSimulations; i++) {
            System.out.println("Running Simulation #" + (i + 1));

            XYSeries series = new XYSeries("Sim " + (i + 1)); // Each simulation has its own series
            GeneticDriftGraph simulation = new GeneticDriftGraph(populationSize, initialDominantFreq, generations);

            int fixationTime = (int) simulation.runSingleSimulation(series);
            fixationTimes[i] = fixationTime;

            if (series.getItemCount() > 0) { //Ensure series is not empty before checking last item
                if (series.getY(series.getItemCount() - 1).doubleValue() == 1.0) {
                    fixationA++;
                } else {
                    fixationa++;
                }
            } else {
                System.err.println("Warning: No data points in series for simulation " + (i + 1));
            }

            dataset.addSeries(series);
        }
    }


    private void createGraph() {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Genetic Drift - Multiple Simulations",
                "Generations",
                "Frequency of A",
                dataset
        );

        XYPlot plot = chart.getXYPlot();
        XYItemRenderer renderer = plot.getRenderer(); // Renderer to modify line colors
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            Color lineColor = getUniqueColor(i, dataset.getSeriesCount());
            renderer.setSeriesPaint(i, lineColor);
        }
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setRange(0, generations);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0.0, 1.0);
        JFrame frame = new JFrame("Genetic Drift - Multiple Simulations");
        frame.setContentPane(new ChartPanel(chart));
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        saveGraphAsImage(chart);
    }
    private void saveGraphAsImage(JFreeChart chart) {
        try {
            File file = new File("genetic_drift_graph.png");
            ChartUtilities.saveChartAsPNG(file, chart, 800, 600);
            System.out.println("Graph saved as genetic_drift_graph.png");
        } catch (IOException e) {
            System.err.println("Error saving graph: " + e.getMessage());
        }
    }

    private void printStatistics() {
        double avgFixationTime = calculateMean(fixationTimes);
        double stdDeviation = calculateStandardDeviation(fixationTimes, avgFixationTime);

        System.out.println("\n=== Simulation Statistics ===");
        System.out.println("Total Simulations: " + numSimulations);
        System.out.println("Fixation of A: " + fixationA + " times (" + (fixationA * 100.0 / numSimulations) + "%)");
        System.out.println("Fixation of a: " + fixationa + " times (" + (fixationa * 100.0 / numSimulations) + "%)");
        System.out.println("Average Time to Fixation: " + avgFixationTime + " generations");
        System.out.println("Standard Deviation of Fixation Time: " + stdDeviation);
    }

    private double calculateMean(int[] data) {
        int sum = 0, count = 0;
        for (int val : data) {
            if (val > 0) {
                sum += val;
                count++;
            }
        }
        return count > 0 ? (double) sum / count : 0;
    }

    private double calculateStandardDeviation(int[] data, double mean) {
        double sum = 0;
        int count = 0;
        for (int val : data) {
            if (val > 0) {
                sum += Math.pow(val - mean, 2);
                count++;
            }
        }
        return count > 0 ? Math.sqrt(sum / count) : 0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter population size: ");
        int populationSize = scanner.nextInt();

        System.out.print("Enter initial dominant allele frequency (0 to 1): ");
        double initialDominantFreq = scanner.nextDouble();

        System.out.print("Enter number of generations to simulate: ");
        int generations = scanner.nextInt();

        System.out.print("Enter number of simulations to run: ");
        int numSimulations = scanner.nextInt();

        scanner.close();

        new GeneticDriftBatchRunner(populationSize, initialDominantFreq, generations, numSimulations);
    }
    private Color getUniqueColor(int index, int totalSimulations) {
        // Generate colors using HSB color space for uniqueness
        float hue = (float) index / totalSimulations; // Spread colors evenly
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }
}
