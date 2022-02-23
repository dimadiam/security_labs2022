package lab3;

import java.time.Instant;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        CasinoGame game = new CasinoGame();
        boolean created = game.createAccount();
        while (created == false) {
            created = game.createAccount();
        }
        playCasinoInMtMode(game);
    }

    public static void playCasinoInLcgMode(CasinoGame game) {
        game.setMode(CasinoGame.MODE_LCG);
        System.out.println("Casino Royale in Lcg mode");
        int rounds = 1;
        System.out.println("Collecting numbers");
        ArrayList<Long> realNumbers = new ArrayList<Long>();
        for (int i = 0; i < 3; i++) {
            System.out.println("Round #" + rounds++ + " (Collecting numbers)");
            long realNumber = game.play(1, 1);
            System.out.println("Casino number is " + realNumber);
            realNumbers.add(realNumber);
        }

        long[] ac = LcgCalculate.getAandC(realNumbers);
        long a = ac[0];
        long c = ac[1];
        System.out.println("Lcg parameter a is " + a);
        System.out.println("Lcg parameter c is " + c);

        System.out.println("Winning the game");

        int prevNumber = realNumbers.get(2).intValue();
        while (game.getMoney() <= 1000000) {
            System.out.println("Round #" + rounds++ + " (winning the game)");
            int myNumber = LcgCalculate.getNextNumber(a, c, prevNumber);
            long realNumber = game.play(game.getMoney(), myNumber);
            if (myNumber == realNumber) {
                System.out.println("My number is correct");
            } else {
                System.out.println("My number is not correct");
            }
            prevNumber = ((Long)realNumber).intValue();

            System.out.println();
        }

        System.out.println("Won the game in Lcg mode! Amount of money is " + game.getMoney());
    }


    public static void playCasinoInMtMode(CasinoGame game) {
        game.setMode(CasinoGame.MODE_MT);
        System.out.println("Casino Royale in Mt mode");
        long realNumber = game.play(1, 1);
        long currentTime = Instant.now().getEpochSecond();

        System.out.println("The first value of casino is " + realNumber);
        System.out.println("Try to guess the seed... (current time is " + currentTime + ")");

        MT19937Generator mtGen = null;
        for (long i = currentTime - 10; i <= currentTime; i++) {
            System.out.println("Trying seed value " + i);
            // https://docs.microsoft.com/en-us/dotnet/api/system.datetimeoffset.tounixtimeseconds?view=net-6.0
            mtGen = new MT19937Generator(Long.valueOf(i).intValue());
            long myNumber = mtGen.next();
            System.out.println("Number from casino : " + realNumber);
            System.out.println("Number from MT19937: " + myNumber);
            if (myNumber == realNumber) {
                System.out.println("Found seed value = " + i);
                break;
            } else {
                mtGen = null;
            }
        }

        if (mtGen == null) {
            throw new RuntimeException("Error: Could not find seed for MT19937 generator");
        }

        System.out.println("Playing the game");
        int round = 1;
        while (game.getMoney() < 1000000) {
            System.out.println("Round #" + round++);
            long myNumber = mtGen.next();
            realNumber = game.play(game.getMoney(), myNumber);
            if (myNumber == realNumber) {
                System.out.println("My number is correct");
            } else {
                System.out.println("My number is not correct");
            }
        }

        System.out.println("Won the game in Mt mode! Amount of money is " + game.getMoney());
    }
}
