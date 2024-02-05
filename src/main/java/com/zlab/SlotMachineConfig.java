package com.zlab;

import java.util.List;
import java.util.Map;

public class SlotMachineConfig {

    public enum SymbolType {
        standard,
        bonus
    }

    public static class Symbol {
        public double reward_multiplier;
        public SymbolType type;
        public String impact; // Only applicable for bonus symbols
        public Integer extra; // Only applicable for bonus symbols
    }

    public static class Probability {
        public int column;
        public int row;
        public Map<String, Integer> symbols;
    }

    public static class WinCombination {
        public double reward_multiplier;
        public String when;
        public int count;
        public String group;
        public List<List<String>> covered_areas; // Only applicable for some win combinations
    }

    public int columns;
    public int rows;
    public Map<String, Symbol> symbols;
    public Map<String, List<Probability>> probabilities;
    public Map<String, WinCombination> win_combinations;
}
