package lab1;

import java.util.ArrayList;
import java.util.HashMap;
import static java.util.Map.entry;
import java.util.Map;

public class GACreature {
    public String key;
    public String plainText = null;
    private double fitnessValue = -1;

    public GACreature (String keyValue)  {
        key = keyValue;
    }

    public int compateTo (GACreature other) {
        if (fitnessValue == other.fitnessValue) {
            return 0;
        } else if (fitnessValue > other.fitnessValue) {
            return 1;
        }
        return -1;
    }

    public double getFitnesValue() {
        return fitnessValue;
    }

    public static ArrayList<Integer> fixedKeyCharIndexes;
    public static void setFixedKeyCharIndexes(ArrayList<Integer> newFixedIndexes) {
        fixedKeyCharIndexes = newFixedIndexes;
    }
    public static boolean isIndexFixed(int index) {
        for (int i : fixedKeyCharIndexes) {
            if (i == index) return true;
        }
        return false;
    }

    private static double englishMonogramsFreqSumTotal = 0.0;
    private static Map<Character, Double> englishMonogramsFreqMap = Map.ofEntries(
            entry('e', 13.0),
            entry('t', 9.1),
            entry('a', 8.2),
            entry('o', 7.5),
            entry('i', 7.0),
            entry('n', 6.7),
            entry('s', 6.3),
            entry('h', 6.1),
            entry('r', 6.0),
            entry('d', 4.3),
            entry('l', 4.0)
    );

    private static double englishBigramsFreqSumTotal = 0.0;
    private static Map<String, Double> englishBigramsFreqMap = Map.ofEntries(
            entry("TH", 1.52),
            entry("HE", 1.28),
            entry("IN", 0.94),
            entry("ER", 0.94),
            entry("AN", 0.82),
            entry("RE", 0.68),
            entry("ND", 0.63),
            entry("AT", 0.59),
            entry("ON", 0.57),
            entry("NT", 0.56),
            entry("HA", 0.56),
            entry("ES", 0.56),
            entry("ST", 0.55),
            entry("EN", 0.55),
            entry("ED", 0.53),
            entry("TO", 0.52),
            entry("IT", 0.50),
            entry("OU", 0.50),
            entry("EA", 0.47),
            entry("HI", 0.46),
            entry("IS", 0.46),
            entry("OR", 0.43),
            entry("TI", 0.34),
            entry("AS", 0.33),
            entry("TE", 0.27),
            entry("ET", 0.19),
            entry("NG", 0.18),
            entry("OF", 0.16)
    );

    private ArrayList<Character> topMonograms;
    private ArrayList<String> topBigrams;

    public static void init() {

        for (double v: englishMonogramsFreqMap.values()) {
            englishMonogramsFreqSumTotal += v;
        }

        for (double v: englishBigramsFreqMap.values()) {
            englishBigramsFreqSumTotal += v;
        }
        System.out.println("englishMonogramsFreqSumTotal = " + englishMonogramsFreqSumTotal);
        System.out.println("englishBigramsFreqSumTotal = " + englishBigramsFreqSumTotal);
    }

    public void printTopNgrams() {
        System.out.println("Top monograms: " + topMonograms);
        System.out.println("Top bigrams: " + topBigrams);
    }

    public void calculateFintess () {
        assert englishBigramsFreqSumTotal > 0;
        assert englishMonogramsFreqSumTotal > 0;

        topMonograms = getTopMonograms(10);
        topBigrams = getTopBigrams(15);

        double mFitnessValue = 0.0;
        for (char m : topMonograms) {
            Double freq = englishMonogramsFreqMap.get(m);
            if (freq != null) {
                mFitnessValue += freq;
            }
        }
        mFitnessValue = mFitnessValue / englishMonogramsFreqSumTotal;

        double bFitnessValue = 0.0;
        for (String b : topBigrams) {
            Double freq = englishBigramsFreqMap.get(b);
            if (freq != null) {
                bFitnessValue += freq;
            }
        }
        bFitnessValue = bFitnessValue / englishBigramsFreqSumTotal;

        fitnessValue = (0.2 * mFitnessValue + 0.8 * bFitnessValue) * 100;
    }

    private ArrayList<Character> getTopMonograms(int count) {
        HashMap<Character, Integer> ptMonograms = new HashMap<Character, Integer>();
        for (char c : plainText.toCharArray()) {
            Integer number = ptMonograms.get(c);
            if (number == null) {
                number = 0;
            }
            ptMonograms.put(c, ++number);
        }

        var topMonograms = new ArrayList<Character>();

        for (int i = 0; i < count; i++) {
            int maxFreq = 0;
            char maxChar = '\0';
            for (char m : ptMonograms.keySet()) {
                int number = ptMonograms.get(m);
                if (number > maxFreq) {
                    maxFreq = number;
                    maxChar = m;
                }
            }
            topMonograms.add(maxChar);
            ptMonograms.put(maxChar, -1);
        }
        return topMonograms;
    }

    private ArrayList<String> getTopBigrams(int count) {
        HashMap<String, Integer> ptBigrams = new HashMap<String, Integer>();
        int index = 0;
        while (index + 2 <= plainText.length()) {
            String ptBigram = plainText.substring(index, index + 2);
            index++;

            Integer number = ptBigrams.get(ptBigram);
            if (number == null) {
                number = 0;
            }
            ptBigrams.put(ptBigram, ++number);
        }

        var topBigrams = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            int maxFreq = 0;
            String maxBigram = "";
            for (String bigram : ptBigrams.keySet()) {
                int freq = ptBigrams.get(bigram);
                if (freq > maxFreq) {
                    maxFreq = freq;
                    maxBigram = bigram;
                }
            }
            topBigrams.add(maxBigram);
            ptBigrams.put(maxBigram, -1);
        }
        return topBigrams;
    }
}
