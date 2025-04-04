import java.util.Arrays;

public class PiBytes {
    public void testFiveStar() {
        double input = 3.142;
        int[] inputBinary = new int[32];
        int[] PiBinary = new int[32];
        int[] comp = new int[32];
        for (int power = -1; power < 16; power++) {
            inputBinary[power + 1] = (int) (input / Math.pow(2, -power)) % 2;
            PiBinary[power + 1] = (int) (Math.PI / Math.pow(2, -power)) % 2;
            comp[power + 1] = inputBinary[power + 1] ^ PiBinary[power + 1];
        }
        System.out.println(Arrays.toString(inputBinary));
        System.out.println(Arrays.toString(PiBinary));
        System.out.println(Arrays.toString(comp));
    }

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
     * Calculates PI by evenly dividing a circle of radius one into 2^n angles and adding the outer edges, so square, then 8 triangles, then 16, then 32 etc...
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

    public int[][] doArray() {
        minErrorStaging m = new minErrorStaging();
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
            int[] codeword = m.findMinimizingCodeword(204,cell,new int[]{0,0,0,0,0,0,0,0})[0];
            for (int row = 0; row < 8; row++){
                out[l][row] = codeword[row];
            }
        }
        return out;
    }
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
