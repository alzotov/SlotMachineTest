package com.zlab;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Random;

import javax.swing.RowFilter.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zlab.SlotMachineConfig.WinCombination;

public class App {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar <path to matrixGame jar-file> <path to json config file> <bet>");
            System.exit(1);
        }

        String jsonFilePath = args[0];
        double bet = Double.parseDouble(args[1]);

        SlotMachineConfig cfg = getCfg(jsonFilePath, bet);

        String[][] matrix = generateMatrix(cfg);

        calculatePrize(matrix, cfg, bet);

    }

    //, java.util.Map.Entry<String, WinCombination> entry
    private static void calculatePrize(String[][] matrix, SlotMachineConfig cfg, double bet) {
        //System.out.print("occurrencesMap:", occurrencesMap);
        double reward = 0;

        List<Map.Entry<String,WinCombination>> sameSymbolWinsSorted = cfg.win_combinations.entrySet()
        .stream()
        .filter(c -> c.getValue().when == SlotMachineConfig.WinType.valueOf("same_symbols"))
        .sorted((a,b) -> b.getValue().count - a.getValue().count)
        .collect(Collectors.toList());

        if(sameSymbolWinsSorted.size() != 0){
            Map<String, Integer> occurrencesMap = countOccurrences(matrix);
            for(Map.Entry<String,Integer> occurrence: occurrencesMap.entrySet()){
                //occurrencesMap.values().stream().sorted().filter(e -> e < sameSymbolWin.getValue().count )
                System.out.println(occurrence);
            }
            
            List<Map.Entry<String,Integer>> occurrencesSorted = occurrencesMap.entrySet()
            .stream()
            .sorted((a,b) -> b.getValue() - a.getValue())
            .collect(Collectors.toList());

            System.out.println("occurrencesSorted:" +occurrencesSorted);
            
            Iterator<Map.Entry<String,Integer>> iOccurrencesSorted = occurrencesSorted.iterator();
            for(Map.Entry<String,WinCombination> sameSymbolWin: sameSymbolWinsSorted){
                //occurrencesMap.values().stream().sorted().filter(e -> e < sameSymbolWin.getValue().count )
                System.out.println(sameSymbolWin);
                if(iOccurrencesSorted.hasNext())
                {
                    java.util.Map.Entry<String, Integer> next = iOccurrencesSorted.next();
                    if(next.getValue() >= sameSymbolWin.getValue().count){
                        String symbol = next.getKey();
                        double symbolMultiplier = cfg.symbols.get(symbol).reward_multiplier;
                        reward += sameSymbolWin.getValue().reward_multiplier * symbolMultiplier * bet;
                    }
                }
            }
            System.out.println("reward: "+reward);
        }
    }

    private static Map<String, Integer> countOccurrences(String[][] matrix) {
        Map<String, Integer> occurrencesMap = new HashMap<>();

        for (String[] row : matrix) {
            for (String element : row) {
                occurrencesMap.put(element, occurrencesMap.getOrDefault(element, 0) + 1);
            }
        }

        return occurrencesMap;
    }

    private static String[][] generateMatrix(SlotMachineConfig cfg) {
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
        return matrix;
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
}

