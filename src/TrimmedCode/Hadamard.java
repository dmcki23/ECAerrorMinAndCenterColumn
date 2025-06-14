package AlgorithmCode;

import CustomLibrary.CustomArray;

import java.util.Arrays;

/**
 * Hadamard matrix
 */
public class Hadamard {
    /**
     * The `hadamardParity` array represents the parity of a Hadamard matrix.
     * This field is used to store parity values, which are typically utilized
     * in the context of Hadamard matrix operations to determine or validate
     * the parity of data or structures.
     *
     * The array values are computed or modified based on certain operations
     * involving Hadamard matrices, allowing*/
    public int[] hadamardParity;

    /**
     * Constructs a Hadamard object and initializes the `hadamardParity` array.
     *
     * The `hadamardParity` array contains the parity information for all integers
     * from 0 to 4095 (inclusive). The parity of each number is determined by the number
     * of set bits in its binary representation, where an even count results in 0
     * and an odd count results in 1.
     *
     * The process iterates over all 12-bit numbers, counts the number of set bits
     * in each number, and calculates the parity (even or odd). The calculated parity
     * is then stored in the corresponding index of the `hadamardParity` array.
     */
    public Hadamard() {
        hadamardParity = new int[4096];
        for (int n = 0; n < 4096; n++) {
            int tot = 0;
            for (int power = 0; power < 12; power++) {
                tot += ((n >> power) % 2);
            }
            hadamardParity[n] = tot % 2;
        }
    }

    /**
     * boolean Hadamard matrix
     *
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
     *
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
     *
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
     *
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
     *
     * @param size size of matrices to be tested
     * @return Hadamard matrix
     */
    public int[][] test(int size) {
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
        for (int in = 0; in < (int) Math.pow(2, size); in++) {
            System.out.println("in = " + in);
            for (int power = 0; power < size; power++) {
                input[power] = ((in / (int) Math.pow(2, power)) % 2);
            }
            int[] test = matrixMultiply(H, input);
            int[] inverse = matrixMultiply(Hnegated, test);
            for (int spot = 0; spot < size; spot++) {
                inverse[spot] /= size;
            }
            for (int spot = 0; spot < size; spot++) {
                if (inverse[spot] == -1) {
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
     * Generates a non-reduced Hadamard matrix using bitwise AND operation.
     * The matrix values are computed based on the bitwise AND of the row and column indices.
     *
     * @param size the size of the square matrix to generate
     * @return a non-reduced Hadamard matrix of the specified size
     */
    public int[][] nonReducedHadamard(int size) {
        int[][] out = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                out[row][col] = row & col;
            }
        }
        return out;
    }
    int[][][] allH;

    /**
     * Generates all possible Hadamard matrices based on the given logarithmic size.
     * Populates an internal structure with the computed Hadamard matrices, which are later
     * displayed using the CustomArray utility.
     *
     * @param logSize the logarithmic scale of the size of the matrices to generate.
     *                The total size will be 2^logSize.
     */
    public void allHadamardish(int logSize) {
        int size = (1<<logSize);
        allH = new int[16][size][size];
        for (int gate = 0; gate < 16; gate++){
            int[][] a = new int[2][2];
            for (int r = 0; r < 2; r++){
                for (int c = 0; c < 2; c++){
                    a[r][c] = (gate>>(2*r+c))%2;
                    allH[gate][r][c] = a[r][c];
                }
            }
            for (int iter = 2; iter <= logSize; iter++) {
                int localSize = (1<<iter);
                int[][] temp = new int[localSize][localSize];

                for (int row = 0; row < localSize/2; row++) {
                    for (int col = 0; col < localSize/2; col++) {
                        for (int r = 0; r < 2; r++){
                            for (int c = 0; c < 2; c++){
                                temp[row+r*localSize/2][col+c*localSize/2] = allH[gate][row][col];
                            }
                        }
                    }
                }
                for (int r = 0; r < 2; r++){
                    for (int c = 0; c < 2; c++){
                        for (int row = 0; row < localSize/2; row++) {
                            for (int col = 0; col < localSize/2; col++) {
                                temp[row+r*localSize/2][col+c*localSize/2] = temp[row][col] ^ a[r][c];
                            }
                        }
                    }
                }
                for (int row = 0; row < localSize; row++) {
                    for (int col = 0; col < localSize; col++) {
                        allH[gate][row][col] = temp[row][col];
                    }
                }
            }

        }
        for (int gate = 0; gate < 16; gate++){
            CustomArray.plusArrayDisplay(allH[gate],false,false,"gate: " + gate);
        }
    }
}
