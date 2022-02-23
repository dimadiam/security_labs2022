package lab3;

import java.math.BigInteger;
import java.util.ArrayList;

public class LcgCalculate {

    public static long[] getAandC(ArrayList<Long> realNumbers) {
        assert realNumbers.size() == 3;
        final long m = ((long)1) << 32;

        long X0 = realNumbers.get(0);
        long X1 = realNumbers.get(1);
        long X2 = realNumbers.get(2);

        long diff10 = X1 - X0;
        long inverseDiff10 = BigInteger.valueOf(diff10).modInverse(BigInteger.valueOf(m)).longValue();

        long a = (X2 - X1) * inverseDiff10;
        while (a < 0) {
            a += m;
        }

        long c = X1 - a * X0;
        while (c < 0) {
            c += m;
        }

        long[] ac = new long[2];
        ac[0] = a % m;
        ac[1] = c % m;
        return ac;
    }

    public static int getNextNumber(long a, long c, int prevNumber) {
        long nextNumber = a * prevNumber + c;
        return (int)nextNumber;
    }
}
