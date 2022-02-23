package lab3;

public class MT19937Generator {
    //http://www.java2s.com/Code/Java/Development-Class/AJavaimplementationoftheMT19937MersenneTwisterpseudorandomnumbergeneratoralgorithm.htm

    // Constants used in the original C implementation
    private final static int UPPER_MASK    = 0x80000000;
    private final static int LOWER_MASK    = 0x7fffffff;
    private final static int N             = 624;
    private final static int M             = 397;
    private final static int[] MAGIC       = { 0x0, 0x9908b0df };
    private final static int MAGIC_FACTOR1 = 1812433253;
    private final static int MAGIC_MASK1   = 0x9d2c5680;
    private final static int MAGIC_MASK2   = 0xefc60000;

    // Internal state
    private int[] mt;
    private int mti;

    public MT19937Generator(int seed)
    {
        mt = new int[N];
        setSeed(seed);
    }

    private void setSeed(int seed)
    {
        // ---- Begin Mersenne Twister Algorithm ----
        mt[0] = seed;
        for (mti = 1; mti < N; mti++)
        {
            mt[mti] = (MAGIC_FACTOR1 * (mt[mti - 1] ^ (mt[mti - 1] >>> 30)) + mti);
        }
        // ---- End Mersenne Twister Algorithm ----
    }

    public final long next()
    {
        // ---- Begin Mersenne Twister Algorithm ----
        int y, kk;
        if (mti >= N)
        { // generate N words at one time

            for (kk = 0; kk < N - M; kk++)
            {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + M] ^ (y >>> 1) ^ MAGIC[y & 0x1];
            }
            for (; kk < N - 1; kk++)
            {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ MAGIC[y & 0x1];
            }
            y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ MAGIC[y & 0x1];

            mti = 0;
        }

        y = mt[mti++];

        // Tempering
        y ^= (y >>> 11);
        y ^= (y << 7) & MAGIC_MASK1;
        y ^= (y << 15) & MAGIC_MASK2;
        y ^= (y >>> 18);
        // ---- End Mersenne Twister Algorithm ----
        long ret = y;
        ret &= 0xFFFFFFFFL;
        return ret;
    }

}
