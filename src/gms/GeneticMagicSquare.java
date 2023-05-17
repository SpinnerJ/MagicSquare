package gms;

import java.util.Random;

public class GeneticMagicSquare {
    private static final int POPULATION_SIZE = 250;
    private static final int MAX_GENERATIONS = 10000;

    private int[][] population;
    private int targetSum;
    private Random random;

    public GeneticMagicSquare(int size) {
        population = new int[POPULATION_SIZE][size * size];
        targetSum = size * (size * size + 1) / 2;
        random = new Random();

        initializePopulation(size);
    }

    private void initializePopulation(int size) {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            int[] square = generateRandomSquare(size);
            population[i] = square;
        }
    }

    private int[] generateRandomSquare(int size) {
        int[] square = new int[size * size];
        for (int i = 0; i < square.length; i++) {
            square[i] = i + 1;
        }

        for (int i = square.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = square[i];
            square[i] = square[j];
            square[j] = temp;
        }

        return square;
    }

    private int fitness(int[] square) {
        int n = (int) Math.sqrt(square.length);
        int sum = 0;

        // Calculate row sums
        for (int i = 0; i < square.length; i += n) {
            int rowSum = 0;
            for (int j = 0; j < n; j++) {
                rowSum += square[i + j];
            }
            sum += Math.abs(rowSum - targetSum);
        }

        // Calculate column sums
        for (int i = 0; i < n; i++) {
            int colSum = 0;
            for (int j = i; j < square.length; j += n) {
                colSum += square[j];
            }
            sum += Math.abs(colSum - targetSum);
        }

        // Calculate diagonal sums
        int diagSum1 = 0;
        int diagSum2 = 0;
        for (int i = 0; i < square.length; i += (n + 1)) {
            diagSum1 += square[i];
        }
        for (int i = n - 1; i < square.length - 1; i += (n - 1)) {
            diagSum2 += square[i];
        }
        sum += Math.abs(diagSum1 - targetSum);
        sum += Math.abs(diagSum2 - targetSum);

        return sum;
    }

    private void evolve() {
        int[] fitnessScores = new int[POPULATION_SIZE];
        int totalFitness = 0;

        for (int i = 0; i < POPULATION_SIZE; i++) {
            int fitness = fitness(population[i]);
            fitnessScores[i] = fitness;
            totalFitness += fitness;
        }

        int[][] newPopulation = new int[POPULATION_SIZE][population[0].length];

        // Elitism: keep the best individual from previous generation
        int bestIndex = getBestIndex(fitnessScores);
        newPopulation[0] = population[bestIndex];

        for (int i = 1; i < POPULATION_SIZE; i++) {
            int[] parent1 = selectParent(fitnessScores, totalFitness);
            int[] parent2 = selectParent(fitnessScores, totalFitness);

            int[] child = crossover(parent1, parent2);
            mutate(child);

            newPopulation[i] = child;
        }

        population = newPopulation;
    }

    private int getBestIndex(int[] fitnessScores) {
        int bestIndex = 0;
        int minFitness = fitnessScores[0];

        for (int i = 1; i < fitnessScores.length; i++) {
            if (fitnessScores[i] < minFitness) {
                bestIndex = i;
                minFitness = fitnessScores[i];
            }
        }

        return bestIndex;
    }

    private int[] selectParent(int[] fitnessScores, int totalFitness) {
        int index = 0;
        int runningSum = 0;
        int randomValue = random.nextInt(totalFitness);

        while (runningSum < randomValue && index < fitnessScores.length - 1) {
            runningSum += fitnessScores[index];
            index++;
        }

        return population[index];
    }




    private int[] crossover(int[] parent1, int[] parent2) {
        int crossoverPoint = random.nextInt(parent1.length);

        int[] child = new int[parent1.length];

        for (int i = 0; i < crossoverPoint; i++) {
            child[i] = parent1[i];
        }

        for (int i = crossoverPoint; i < parent2.length; i++) {
            child[i] = parent2[i];
        }

        return child;
    }

    private void mutate(int[] individual) {
        int mutationPoint = random.nextInt(individual.length);

        int newValue = random.nextInt(individual.length) + 1;
        individual[mutationPoint] = newValue;
    }

    private int[] getBestSolution() {
        int[] fitnessScores = new int[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            fitnessScores[i] = fitness(population[i]);
        }
        int bestIndex = getBestIndex(fitnessScores);
        return population[bestIndex];
    }


    public void run(int size) {
        for (int i = 0; i < MAX_GENERATIONS; i++) {
            evolve();
        }

        int[] bestSolution = getBestSolution();
        printSquare(bestSolution, size);
    }

    private void printSquare(int[] square, int size) {
        for (int i = 0; i < square.length; i++) {
            System.out.print(square[i] + " ");
            if ((i + 1) % size == 0) {
                System.out.println();
            }
        }
    }

    public static void main(String[] args) {
        int size = 3; // Change this value to the desired size of the magic square
        GeneticMagicSquare geneticMagicSquare = new GeneticMagicSquare(size);
        geneticMagicSquare.run(size);
    }
}
