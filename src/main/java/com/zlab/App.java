package com.zlab;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;

public class App {

    public static SlotMachineConfig parseJson(String jsonFilePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        // System.out.println("Working Directory = " + System.getProperty("user.dir"));

        try {
            // Read JSON file and parse it into SlotMachineConfiguration class
            return objectMapper.readValue(new File(jsonFilePath), SlotMachineConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar <path to matrixGame jar-file> <path to json config file> <bet>");
            System.exit(1);
        }

        String jsonFilePath = args[0];
        double bet = Double.parseDouble(args[1]);

        SlotMachineConfig cfg = getCfg(jsonFilePath, bet);

        generateMatrix(cfg);

        //calculate prize

    }

    private static void generateMatrix(SlotMachineConfig cfg) {
        //generating matrix according to wierd probabilities map
        Random random = new Random();

        String[][] matrix = new String[cfg.columns][cfg.rows];
        int bonusTotal = cfg.probabilities.getOrDefault("bonus_symbols", List.of())
                .stream()
                .mapToInt(probability -> probability.symbols.values().stream().mapToInt(d->d).sum())
                .sum();
        for (int i = 0; i < cfg.columns; i++) {
            for (int j = 0; j < cfg.rows; j++) {
                final int fi = i;
                final int fj = j;
                double symbolsTotal = cfg.probabilities.getOrDefault("standard_symbols", List.of())
                        .stream()
                        .filter(probability -> probability.column == fi && probability.row == fj)
                        .mapToInt(probability -> probability.symbols.values().stream().mapToInt(d->d).sum())
                        .sum();

                double probTotal = bonusTotal + symbolsTotal;

                double randomValue = random.nextDouble() * probTotal;

                Map<String,Integer> bonusProbs = cfg.probabilities.getOrDefault("bonus_symbols", List.of())
                .stream()
                .findAny()
                .map( probability -> probability.symbols)
                .get();

                Map<String,Integer> standardProbs = cfg.probabilities.getOrDefault("standard_symbols", List.of())
                .stream()
                .filter(probability -> probability.column == fi && probability.row == fj)
                .findAny()
                .map( probability -> probability.symbols)
                .get();

                Map<String,Integer> combinedProbs = new HashMap<>(bonusProbs);
                combinedProbs.putAll(standardProbs);

                System.out.println("Combined Map: " + combinedProbs);

                double checkProb = combinedProbs.values().stream().mapToDouble(d-> d).sum();
                System.out.println("Check Prob: " + checkProb);

                String key = "";
                // Iterate through probabilities until the randomValue is less than or equal to 0
                for (Map.Entry<String,Integer> entry : combinedProbs.entrySet()) {
                    randomValue -= entry.getValue();
                    if (randomValue <= 0) {
                        key =  entry.getKey();
                        break;
                    }
                }
                matrix[i][j] = key;
            }
        }

        System.out.println("matrix: " + matrix);
        for(String[] row : matrix)
            System.out.println(String.join(", ", row));
    }

    private static SlotMachineConfig getCfg(String jsonFilePath, double bet) {
        // Parse JSON and get SlotMachineConfiguration object
        SlotMachineConfig cfg = parseJson(jsonFilePath);

        // Check if parsing was successful
        if (cfg != null) {
            // Now you can use the populated SlotMachineConfiguration object
            System.out.println("Columns: " + cfg.columns);
            System.out.println("Rows: " + cfg.rows);
            System.out.println("Symbols: " + cfg.symbols);
            System.out.println("Probabilities: " + cfg.probabilities);
            System.out.println("Win Combinations: " + cfg.win_combinations);
            System.out.println("Bet: " + bet);
        } else {
            System.out.println("Failed to parse JSON.");
        }
        return cfg;
    }
}

