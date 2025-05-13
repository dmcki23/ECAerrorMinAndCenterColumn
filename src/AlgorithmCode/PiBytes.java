package AlgorithmCode;

/**
 * Various Pi related functions, most relevant to a 1-2% error rate in reconstructing the lossy compression
 * hash minimums
 */
public class PiBytes {
    int[][] constants = new int[4][512];
    int[] hammingCodewordsConstants = new int[512];

    /**
     * Calculates PI via the Wallis product
     *
     * @param cycles how many iterations to use
     * @return PI via Wallis product
     */
    public double doWallisProduct(int cycles) {
        double out = 2.0 * 2.0 / 1.0 / 3.0 * 2.0;
        for (int cycle = 1; cycle < cycles; cycle++) {
            System.out.println("cycle " + cycle);
            double a = (2 * (cycle + 1)) * (2 * (cycle + 1));
            double b = (2 * (cycle + 1) - 1) * (2 * (cycle + 1) + 1);
            out = out * a / b;
            for (int power = 2; power > -20; power--) {
                System.out.print((int) (Math.PI / Math.pow(2, power)) % 2 + " ");
            }
            System.out.println();
            for (int power = 2; power > -20; power--) {
                System.out.print((int) (out / Math.pow(2, power)) % 2 + " ");
            }
            int diffIndex = -1;
            System.out.println();
            for (int power = 2; power > -24; power--) {
                int c = (int) ((out / Math.pow(2, power)) % 2);
                int d = (int) ((Math.PI / Math.pow(2, power)) % 2);
                System.out.print((c ^ d) + " ");
                if ((c ^ d) == 1 && diffIndex == -1) {
                    diffIndex = power;
                }
            }
            System.out.println();
            System.out.println("diffIndex " + (2 - diffIndex));
            System.out.println("PI - Wallis = " + (Math.PI - out));
            double l = Math.log(Math.PI - out) / Math.log(2);
            System.out.println("log(PI-Wallis)= " + l);
        }
        return out;
    }

    /**
     * Calculates PI by evenly dividing a circle of radius one into 2^n triangles and adding the outer edges, so square, then 8 triangles, then 16, then 32 etc...
     *
     * @param cycles how many triangles to divide the circle into, minimum 2
     * @return PI calculated via triangle
     */
    public double doTriangles(int cycles) {
        double out = 2.0 * 2.0 / 1.0 / 3.0 * 2.0;
        for (int cycle = 2; cycle < cycles; cycle++) {
            System.out.println("cycle " + (cycle - 2));
            double xzero = 1;
            double yzero = 0;
            double xone = Math.cos(Math.PI / Math.pow(2, cycle - 1));
            double yone = Math.sin(Math.PI / Math.pow(2, cycle - 1));
            double base = Math.sqrt((xone - xzero) * (xone - xzero) + (yone - yzero) * (yone - yzero));
            double height = 1;
            double numTriangles = Math.pow(2, cycle);
            out = numTriangles * base * height / 2;
            for (int power = 2; power > -20; power--) {
                System.out.print((int) (Math.PI / Math.pow(2, power)) % 2 + " ");
            }
            System.out.println();
            for (int power = 2; power > -20; power--) {
                System.out.print((int) (out / Math.pow(2, power)) % 2 + " ");
            }
            int diffIndex = -1;
            System.out.println();
            for (int power = 2; power > -24; power--) {
                int c = (int) ((out / Math.pow(2, power)) % 2);
                int d = (int) ((Math.PI / Math.pow(2, power)) % 2);
                System.out.print((c ^ d) + " ");
                if ((c ^ d) == 1 && diffIndex == -1) {
                    diffIndex = power;
                }
            }
            System.out.println();
            System.out.println("diffIndex " + (2 - diffIndex));
            System.out.println("PI - triangles = " + (Math.PI - out));
            double l = Math.log(Math.PI - out) / Math.log(2);
            System.out.println("log(PI-triangles)= " + l);
        }
        return out;
    }

    public void initializeBinaryConstants() {
        String piString =
                "110010010000111111011010101000100010000101101000110000100011010011000100" +
                        "1100011001100010100010111000000011011100000111001101000100101001000000" +
                        "1001001110000010001000101001100111110011000111010000000010000010111011" +
                        "1110101001100011101100010011100110110010001001010001010010100000100001" +
                        "11100110001110001101000000010011011101111011111001";
        for (int digit = 0; digit < piString.length(); digit++) {
            constants[0][digit] = piString.charAt(digit);
        }
        String eString =
                "101011011111100001010100010110001010001010111011010010101001101010101" +
                        "1111101110001010110001000000010011100111101001111001111000111011000101" +
                        "1100111000101100000111100111000101101001101101001010110101001111000010" +
                        "0110110010000010001010001100100001100111111101111001100100100111001110" +
                        "11100111000100100100110110011111011111001011111010011";
        for (int digit = 0; digit < eString.length(); digit++) {
            constants[1][digit] = eString.charAt(digit);
        }
        String sqrtTwoString =
                "0101101010000010011110011001100111111100111011110011001001000010001011" +
                        "0010111110110001001101100110111010101001010101111101001111100011101011" +
                        "0111101100000101110101000100100111011101010000100110011101101000101111" +
                        "0101100100001011000001100110011100110010001010101001010111111001000001" +
                        "10000010000111010101110001010001011000011101010001011";
        for (int digit = 0; digit < sqrtTwoString.length(); digit++) {
            constants[2][digit] = sqrtTwoString.charAt(digit);
        }
        String phiSquared =
                "111001111000110111011110011011100101111111010010100111110000010101111100" +
                        "1110011100110000000110000001011100111011011100100000110100000100001000" +
                        "0010001001110110101111110011101000100111001001010001111110000110110001" +
                        "1010100001000111010000110000011000111010010101001001110110011111110000" +
                        "101100010101001111010010011110110111111100000011010";
        String phi =
                "011001111000110111011110011011100101111111010010100111110000010101111100" +
                        "1110011100110000000110000001011100111011011100100000110100000100001000" +
                        "0010001001110110101111110011101000100111001001010001111110000110110001" +
                        "1010100001000111010000110000011000111010010101001001110110011111110000" +
                        "101100010101001111010010011110110111111100000011010";
        String piOverThree = "010000110000010101001000111000001011010111001101100101100001000110010110" +
                "1110110011001011100000111101010110011110101101000100010110111000010101" +
                "1000011010000000101101100011001101010001000010011010101011010110010011" +
                "1111100011001011111001011011110111100110000011000101110000110101100000" +
                "1010001000010010111100000000011001111101001111110111";
        for (int digit = 0; digit < phiSquared.length(); digit++) {
            constants[3][digit] = phiSquared.charAt(digit);
        }
        hammingCodewordsConstants = generateHammingCodewords(constants);
    }

    public int[] generateHammingCodewords(int[][] in) {
        int[] out = new int[in[0].length];
        for (int column = 0; column < in[0].length; column++) {
            int ninety = in[0][column] ^ in[2][column];
            int oneOhTwo = in[2][column] ^ in[3][column];
            int oneFiftyThree = in[0][column] ^ in[1][column];
            int oneFifty = in[0][column] ^ in[1][column] ^ in[2][column];
            out[0] = ninety;
            out[1] = oneOhTwo;
            out[2] = oneFiftyThree;
            out[3] = oneFifty;
        }
        return out;
    }

    /**
     * Place binary Pi, Phi, and Sqrt(2) next to each other, find the Hamming codeword of them at each point
     * then find the hash's minimizing codeword at each point and see if you can reconstruct Pi, Phi, Sqrt(2)
     * from hash codewords
     *
     * @return
     */
    public int[][] doConstants() {
        int length = 100;
        int[][] out = new int[4][100];
        Hash hash = new Hash();
        initializeBinaryConstants();
        hash.initWolframs();
        int[] tuple = new int[16];
        for (int spot = 0; spot < length; spot++) {
            int activePower = -spot + 4;
            int tot = 0;
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    tot += (1 << (4 * row + col)) * constants[row][activePower];
                }
            }
            for (int t = 0; t < 8; t++) {
                tuple[t] = hash.flatWolframs[0][t][tot];
                tuple[t + 8] = hash.flatWolframs[1][t][tot];
            }
        }
        return out;
    }

    /**
     * If you have {0,1,2,3,4} start at 0 and go backwards by 2 you get {3,1,4,2,0} and this
     * function checks to see the difference between 3.1420 and Pi
     *
     * @return 3.1420 - Pi
     */
    public double checkStar() {
        double difference = 3.1420 - Math.PI;
        System.out.println("difference = " + difference);
        for (int power = 1; power < 12; power++) {
            int a = (int) (Math.PI / Math.pow(2, -power) % 2);
            int b = (int) (3.1420 / Math.pow(2, -power) % 2);
            if (a != b) {
                System.out.println("breaks down at power " + power);
                break;
            }
        }
        return difference;
    }
}
