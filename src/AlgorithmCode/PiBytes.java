package AlgorithmCode;

/**
 * Various Pi related functions, most relevant to a 1-2% error rate in reconstructing the lossy compression
 * hash minimums
 */
public class PiBytes {


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

    /**
     * Place binary Pi, Phi, and Sqrt(2) next to each other, find the Hamming codeword of them at each point
     * then find the hash's minimizing codeword at each point and see if you can reconstruct Pi, Phi, Sqrt(2)
     * from hash codewords
     * @return
     */
    public int[][] doArray() {
        errorMinimizationHash m = new errorMinimizationHash();
        int length = 256;
        int[] Pi = new int[length];
        int[] E = new int[length];
        int[] Phi = new int[length];
        int[] sqrtTwo = new int[length];
        int[] hamming = new int[length];
        int[][] in = new int[256][8];
        int[][] out = new int[256 + 8][8];
        int[][][] outVotes = new int[256][8][8];
        int[] outDec = new int[length];
        int[][] cell = new int[8][8];
        for (int l = 0; l < length; l++) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    int spot = l - 8;
                    if (spot < 0) {
                        cell[row][col] = 0;
                    } else {
                        cell[row][col] = in[l][row];
                    }
                }
            }
            int[] codeword = m.findMinimizingCodeword(204,cell)[0];
            for (int row = 0; row < 8; row++){
                out[l][row] = codeword[row];
            }
        }
        return out;
    }

    /**
     * If you have {0,1,2,3,4} start at 0 and go backwards by 2 you get {3,1,4,2,0} and this
     * function checks to see the difference between 3.1420 and Pi
     * @return 3.1420 - Pi
     */
    public double checkStar(){
        double difference = 3.1420-Math.PI;
        System.out.println("difference = " + difference);
        for (int power = 1; power < 12; power++) {
            int a = (int)(Math.PI/Math.pow(2,-power)%2);
            int b = (int)(3.1420/Math.pow(2,-power)%2);
            if (a != b){
                System.out.println("breaks down at power " + power);
                break;
            }
        }
        return difference;
    }
}
