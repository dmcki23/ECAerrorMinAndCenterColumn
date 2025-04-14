package AlgorithmCode;

import CustomLibrary.CustomArray;

import java.util.Arrays;

/**
 * Hadamard matrix
 */
public class Hadamard {
    public int[] hadamardParity;
    public Hadamard(){
        hadamardParity = new int[4096];
        for (int n = 0; n < 4096; n++) {
            int tot = 0;
            for (int power = 0; power < 12; power++) {
                tot += ((n>>power)%2);
            }
            hadamardParity[n] = tot%2;
        }
    }
    /**
     * boolean Hadamard matrix
     * @param size size of the array desired
     * @return a boolean Hadamard matrix
     */
    public int[][] generateHadamardBoolean(int size) {
        int[][] out = new int[size][size];
        int log = (int) (Math.log(size) / Math.log(2));
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                out[row][col] = (row & col);
                int quantity = 0;
                for (int power = 0; power < (log); power++) {
                    quantity += (out[row][col] / (int) Math.pow(2, power)) % 2;
                }
                out[row][col] = quantity % 2;
            }
        }
        //CustomArray.plusArrayDisplay(out, false, false, "generateByRCmult");
        return out;
    }

    /**
     * Hadamard matrix
     * @param size size of array desired
     * @return Hadamard matrix
     */
    public int[][] generateHadamard(int size) {
        int[][] out = new int[size][size];
        int log = (int) (Math.log(size) / Math.log(2));
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                out[row][col] = (row & col);
                int quantity = 0;
                for (int power = 0; power < (log); power++) {
                    quantity += (out[row][col] / (int) Math.pow(2, power)) % 2;
                }
                out[row][col] = quantity % 2;
                if (out[row][col] == 0) {
                    out[row][col] = 1;
                } else {
                    out[row][col] = -1;
                }
                //out[row][col] = (out[row][col] == 0) ? -1 : 1;
            }
        }
        //CustomArray.plusArrayDisplay(out, false, false, "generateByRCmult");
        return out;
    }

    /**
     * Multiplies a * b
     * @param a a matrix
     * @param b a vector
     * @return a * b
     */
    public int[] matrixMultiply(int[][] a, int[] b) {
        int[] out = new int[b.length];
        for (int row = 0; row < a.length; row++) {
            for (int col = 0; col < a[0].length; col++) {
                out[col] += (a[col][row] * b[row]);
            }
        }
        return out;
    }

    /**
     * Multiplies a * b
     * @param a a matrix
     * @param b a matrix
     * @return a * b
     */
    public int[][] matrixMultiply(int[][] a, int[][] b) {
        int[][] out = new int[a.length][a[0].length];
        for (int row = 0; row < a[0].length; row++) {
            for (int col = 0; col < a[row].length; col++) {
                for (int zee = 0; zee < a[row].length; zee++) {
                    out[row][col] += a[row][zee] * b[zee][col];
                }
            }
        }
        return out;
    }

    /**
     * Tests the Hadamard and inverse Hadamard matrices. Does the Hadamard-Walsh transform on all 2^size
     * possible binary input arrays of size size. It then does the inverse and if the inverse is not the original,
     * it flags the whole operation as incorrect.
     * @param size size of matrices to be tested
     * @return Hadamard matrix
     */
    public int[][] test(int size){
        int[][] H = generateHadamard(size);
        int[][] Hnegated = generateHadamard(size);
        for (int row = 0; row < H.length; row++) {
            for (int col = 0; col < H[0].length; col++) {
                Hnegated[row][col] = -H[row][col];
            }
        }
        int[] input = new int[size];
        int log = (int) (Math.log(size) / Math.log(2));
        System.out.println("log = " + log);
        boolean allMatch = true;
        for (int in = 0; in < (int)Math.pow(2,size); in++) {
            System.out.println("in = " + in);
            for (int power = 0; power < size; power++) {
                input[power] = ((in/(int)Math.pow(2, power)) % 2);
            }
            int[] test = matrixMultiply(H, input);
            int[] inverse = matrixMultiply(Hnegated, test);
            for (int spot = 0; spot < size; spot++) {
                inverse[spot] /= size;
            }
            for (int spot = 0; spot < size; spot++) {
                if (inverse[spot] == -1){
                    inverse[spot] = 1;
                } else {
                    inverse[spot] = 0;
                }
            }
            if (!Arrays.equals(inverse, input)) {
                allMatch = false;
            }
            System.out.println("input: " + Arrays.toString(input));
            System.out.println("result: " + Arrays.toString(test));
            System.out.println("inverse: " + Arrays.toString(inverse));
            System.out.println();
        }
        System.out.println("allMatch = " + allMatch);
        return H;
    }

    /**
     * Sierpinski gasket in a square matrix
     * @param size size of array desired
     * @return Pascal triangle square matrix of size size
     */
    public int[][] pascalDiag(int size){
        int[][] out = new int[size][size];
        out[0][0] = 1;
        for (int row = 0; row < size; row++) {
            out[0][row] = 1;
            out[row][0] = 1;
        }

        for (int row = 2; row < size; row++) {
            for (int col = 1; col < row; col++) {
                int a = row - col;
                int b = col;
                out[a][b] = (out[a - 1][b] + out[a][b - 1])%2;

            }
        }
        for (int row = 0; row < size; row++) {
            for (int col = 0; col <= row; col++) {
                out[size-1-row][size-1-col] = out[row][col];
            }
        }
        CustomArray.plusArrayDisplay(out, false, false, "pascal");
        return out;
    }

    /**
     * Sierpinski gasket on the diagonal in a square matrix
     * @param size
     * @return
     */
    public int[][] pascalLR(int size){
        int[][] out = new int[size][size];
        out[0][0] = 1;
        for (int row = 0; row < size; row++) {
            out[row][0] = 1;
            out[row][row] = 1;
            out[0][row] = 1;
        }

        for (int row = 2; row < size; row++) {
            for (int col = 1; col < row; col++) {
                int a = row;
                int b = col;
                out[a][b] = (out[a - 1][b] + out[a][b - 1])%2;
                out[b][a] = out[a][b];
            }
        }
        CustomArray.plusArrayDisplay(out, false, false, "pascal");
        return out;
    }

    /**
     * Sierpinksi gasket XOR Hadmard matrix
     * @param size length of array
     * @return Sierpinski XOR Hadmard
     */
    public int[][] pascalXORhadamard(int size){
        int[][] H = generateHadamardBoolean(size);
        int[][] pascal;
        pascal = pascalLR(size);
        int[][] pascalDiag = pascalDiag(size);
        int[][] out = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                out[row][col] = H[row][col] ^ pascal[col][row];
                pascalDiag[row][col] = (pascalDiag[row][col] ^ H[row][col]);
            }
        }
        CustomArray.plusArrayDisplay(out, false, false, "pascal");
        CustomArray.plusArrayDisplay(out, false, false, "pascalDiag");
        return out;
    }
    public double[][][] dftOfHadamard(int size){
        double[][][] out = new double[size][size][2];
        int[][] H = generateHadamardBoolean(size);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                out[row][col][0] = H[row][col];
            }
        }
        out = dft(out);
        return out;
    }
    public double[][] dft(double[][] in) {
        double[][] out = new double[in.length][2];
        double coefficient = -2 * Math.PI;
        for (int row = 0; row < in.length; row++) {
            for (int column = 0; column < in.length; column++) {
                double r = in[column][0] * Math.cos( (coefficient * row * column) / (double) in.length) - in[column][1] * Math.sin( (coefficient * row * column) / (double) in.length);
                double c = in[column][0] * Math.sin( (coefficient * row * column) / (double) in.length) + in[column][1] * Math.cos( (coefficient * row * column) / (double) in.length);
                out[row][0] += r;
                out[row][1] += c;
            }
        }

        return out;
    }
    public double[][][] dft(double[][][] in) {
        double[][][] out = new double[in.length][in[0].length][2];
        double coefficient = -2 * Math.PI;
        for (int row = 0; row < in.length; row++) {
            for (int column = 0; column < in.length; column++) {
                double[] innerSum = new double[2];
                double[] outerSum = new double[2];
                for (int rr = 0; rr < in.length; rr++) {
                    innerSum = new double[2];
                    for (int cc = 0; cc < in[row].length; cc++) {
                        double r = in[row][column][0] * Math.cos((coefficient * cc * rr) / (double) in.length) - in[row][column][1] * Math.sin((coefficient * cc * rr) / (double) in.length);
                        double c = in[row][column][0] * Math.sin((coefficient * cc * rr) / (double) in.length) + in[row][column][1] * Math.cos((coefficient * cc * rr) / (double) in.length);
                        innerSum[0] += r;
                        innerSum[1] += c;
                    }
                    innerSum[0] = innerSum[0] * Math.cos((coefficient * row * column) / (double) in.length) - innerSum[1] * Math.sin((coefficient * row * column) / (double) in.length);
                    innerSum[1] = innerSum[1] * Math.sin((coefficient * row * column) / (double) in.length) + innerSum[1] * Math.cos((coefficient * row * column) / (double) in.length);
                    outerSum[0] += innerSum[0];
                    outerSum[1] += innerSum[1];
                }
                out[row][column][0] = outerSum[0];
                out[row][column][1] = outerSum[1];
            }
        }

        for (int row = 0; row < in.length; row++){
            for (int column = 0; column < in.length; column++){
                System.out.print(Math.round(out[row][column][0]) + " ");
            }
            System.out.print("\n");
        }
        return out;
    }
    public int[][] nonReducedHadamard(int size){
        int[][] out = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                out[row][col] = row & col;
            }
        }
        return out;
    }

}
