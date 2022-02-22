package lab1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class DecryptTask3 {

    private static Random random = new Random();
    private static String alphabetLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void decryptTask3(String task3) {
        GACreature.init();

        int populationSize = 100;
        int populationSizeAfterSelection = (int)((double)populationSize * 0.6);
        int cycles = 100;
        System.out.println("Init");

        String initKey = "EKMFLGDQVJNTOWYHXUSPAIBRCZ";

        ArrayList<Integer> fixedIndexes = new ArrayList<Integer>();
        fixedIndexes.add(initKey.indexOf('P'));
        fixedIndexes.add(initKey.indexOf('Q'));
        fixedIndexes.add(initKey.indexOf('L'));

        fixedIndexes.add(initKey.indexOf('S'));
        fixedIndexes.add(initKey.indexOf('E'));
        fixedIndexes.add(initKey.indexOf('V'));
        fixedIndexes.add(initKey.indexOf('M'));
        fixedIndexes.add(initKey.indexOf('B'));

        fixedIndexes.add(initKey.indexOf('W'));
        fixedIndexes.add(initKey.indexOf('R'));

        fixedIndexes.add(initKey.indexOf('H'));
        fixedIndexes.add(initKey.indexOf('U'));

        fixedIndexes.add(initKey.indexOf('C'));
        fixedIndexes.add(initKey.indexOf('Y'));
        fixedIndexes.add(initKey.indexOf('G'));
        fixedIndexes.add(initKey.indexOf('A'));
        fixedIndexes.add(initKey.indexOf('O'));
        fixedIndexes.add(initKey.indexOf('K'));

        fixedIndexes.add(initKey.indexOf('F'));
        fixedIndexes.add(initKey.indexOf('T'));
        fixedIndexes.add(initKey.indexOf('N'));

        fixedIndexes.add(initKey.indexOf('I'));
        fixedIndexes.add(initKey.indexOf('D'));
        System.out.println("Fixed indexes = " + fixedIndexes);
        GACreature.setFixedKeyCharIndexes(fixedIndexes);

        ArrayList<GACreature> creatures = initialization(populationSize, initKey);

        for (int i = 0; i < cycles; i++) {

            for (GACreature c : creatures) {
                if (c.plainText != null || c.getFitnesValue() < 0.0) {
                    decryptSubstitutionCipher(task3, c);
                    c.calculateFintess();
                }
            }

            Collections.sort(creatures, (a, b) -> b.compateTo(a));
            while (creatures.size() > populationSizeAfterSelection) {
                creatures.remove(creatures.size() - 1);
            }
            crossoverAndMutation(creatures);
        }


        for (int i = 0; i < 10; i++) {
            var c = creatures.get(i);
            System.out.println("rank " + (i + 1));
            System.out.println(c.key);
            System.out.println(c.plainText);
            System.out.println(c.getFitnesValue());
            c.printTopNgrams();
            System.out.println();
        }
    }

    private static ArrayList<GACreature> initialization(int amount, String initKey) {
        var creatures = new ArrayList<GACreature>();

        int maxPerm = 26;
        int minPerm = 5;
        for (int i = 0; i < amount - 1; i++) {
            int numberPermutations = random.nextInt((maxPerm - minPerm) + 1) + minPerm;
            char[] chars = initKey.toCharArray();

            for (int j = 0; j < numberPermutations; j++) {
                int index1 = -1;
                while (index1 < 0 || GACreature.isIndexFixed(index1)) {
                    index1 = random.nextInt(chars.length);
                }
                int index2 = -1;
                while (index2 < 0 || GACreature.isIndexFixed(index2)) {
                    index2 = random.nextInt(chars.length);
                }
                char tmp = chars[index1];
                chars[index1] = chars[index2];
                chars[index2] = tmp;
            }
            var creature = new GACreature(new String(chars));
            creatures.add(creature);
        }

        var creature = new GACreature(new String(initKey));
        creatures.add(creature);
        return creatures;
    }


    private static void decryptSubstitutionCipher(String cipherText, GACreature creature) {

        HashMap<Character, Character> decryptTable = new HashMap<Character, Character>();

        assert creature.key.length() == alphabetLetters.length();
        for (int i = 0; i < creature.key.length(); i++) {
            decryptTable.put(creature.key.charAt(i), alphabetLetters.charAt(i));
        }

        StringBuilder plainTextBuilder = new StringBuilder();
        for (char c : cipherText.toCharArray()) {
            char ptChar = decryptTable.get(c);
            plainTextBuilder.append(ptChar);
        }

        creature.plainText = plainTextBuilder.toString();
    }

    private static void crossoverAndMutation(ArrayList<GACreature> creatures) {
        int originalSize = creatures.size();

        for (int i = 0; i < originalSize; i++) {
            for (int child = 0; child < 2; child++) {
                char[] chars = creatures.get(i).key.toCharArray();
                int numberPermutations = random.nextInt(3 + child) + 1;

                for (int j = 0; j < numberPermutations; j++) {
                    int index1 = -1;
                    while (index1 < 0 || GACreature.isIndexFixed(index1)) {
                        index1 = random.nextInt(chars.length);
                    }
                    int index2 = -1;
                    while (index2 < 0 || GACreature.isIndexFixed(index2)) {
                        index2 = random.nextInt(chars.length);
                    }
                    char tmp = chars[index1];
                    chars[index1] = chars[index2];
                    chars[index2] = tmp;
                }
                var creature = new GACreature(new String(chars));
                creatures.add(creature);
            }
        }
    }
}
