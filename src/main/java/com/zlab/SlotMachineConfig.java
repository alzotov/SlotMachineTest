package com.zlab;

import java.util.List;
import java.util.Map;

public class SlotMachineConfig {

    public enum SymbolType {
        standard,
        bonus
    }

    public enum WinGroup {
        same_symbols,
        horizontally_linear_symbols,
        vertically_linear_symbols,
        ltr_diagonally_linear_symbols,
        rtl_diagonally_linear_symbols
    }

    public enum WinType {
        same_symbols,
        linear_symbols,
        same_symbols_diagonally_right_to_left,
        same_symbols_diagonally_left_to_right
    }

    public static class Symbol {
        public double reward_multiplier;
        public SymbolType type;
        public String impact; // Only applicable for bonus symbols
        public Integer extra; // Only applicable for bonus symbols
        @Override
        public String toString() {
            return "Symbol [reward_multiplier=" + reward_multiplier + ", type=" + type + ", impact=" + impact
                    + ", extra=" + extra + "]";
        }
    }

    public static class Probability {
        public int column;
        public int row;
        public Map<String, Integer> symbols;
        @Override
        public String toString() {
            return "Probability [column=" + column + ", row=" + row + ", symbols=" + symbols + "]";
        }
    }

    public static class WinCombination {
        public double reward_multiplier;
        public WinType when;
        public int count;
        public WinGroup group;
        public List<List<String>> covered_areas; // Only applicable for some win combinations
        @Override
        public String toString() {
            return "WinCombination [reward_multiplier=" + reward_multiplier + ", when=" + when + ", count=" + count
                    + ", group=" + group + ", covered_areas=" + covered_areas + "]";
        }
    }

    public int columns;
    public int rows;
    public Map<String, Symbol> symbols;
    public Map<String, List<Probability>> probabilities;
    public Map<String, WinCombination> win_combinations;
    @Override
    public String toString() {
        return "SlotMachineConfig [columns=" + columns + ", rows=" + rows + ", symbols=" + symbols + ", probabilities="
                + probabilities + ", win_combinations=" + win_combinations + "]";
    }
}
