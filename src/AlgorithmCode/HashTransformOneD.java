package AlgorithmCode;

import java.util.Arrays;
import java.util.Random;

/**
 * One D version of the hash algorithm
 */
public class HashTransformOneD {
    /**
     * Algorithmic manager function
     */
    Hash hash;

    /**
     * Sets the algorithm parent
     *
     * @param inHash Owner of this class
     */
    public HashTransformOneD(Hash inHash) {
        hash = inHash;
    }

    /**
     * 1D version of the hash algorithm using all 32 codeword sets, hash-in-place
     *
     * @param input a 1D array of data to be hashed
     * @param depth the iterative depth defining the transformation steps
     * @return a new 1D array containing the transformed and rehashed data
     */
    public int[][][] hashArray(int[][][] input, int depth) {
        int[][][] out = new int[4][8][input.length];
        for (int layer = 0; layer < 4; layer++) {
            boolean minimize = (layer % 2) == 0 ? true : false;
            boolean rowError = (layer / 2) % 2 == 0 ? true : false;
            for (int t = 0; t < 8; t++) {
                out[layer][t] = hashArray(input[layer][t], hash.bothLists[layer][t], depth, minimize, rowError)[depth];
            }
        }
        return out;
    }

    /**
     * 1D version of the hash algorithm using all 32 codeword sets, compression version
     *
     * @param input a 1D array of data to be hashed
     * @param depth iterative depth of hashing
     * @return input data compressively hashed to iteration depth
     */
    public int[][][] hashCompression(int[][][] input, int depth) {
        int[][][] out = new int[4][8][input.length];
        for (int layer = 0; layer < 4; layer++) {
            boolean minimize = layer % 2 == 0 ? true : false;
            boolean rowError = (layer / 2) % 2 == 0 ? true : false;
            for (int t = 0; t < 8; t++) {
                out[layer][t] = hashArrayCompression(input[layer][t], hash.bothLists[layer][t], depth, minimize, rowError);
            }
        }
        return out;
    }

    /**
     * 1D version of the hash algorithm using only a single codeword set, hash-in-place version
     *
     * @param input    a 1D array of hashed data
     * @param rule     a 0-255 ECA rule, typically within the two standard sets
     * @param depth    how many iterations of hashing to do
     * @param minimize if true uses the minimizing codeword set of the rule, if false uses the maximizing set
     * @param rowError if true uses the row-weighted errorScore set, if false uses the column-weighted errorScore set
     * @return the input data hashed depth times
     */
    public int[][] hashArray(int[] input, int rule, int depth, boolean minimize, boolean rowError) {
        //initWolframs();
        int rows = input.length;
        int[][] output = new int[depth + 1][rows];
        //initialize layer 0 to the input
        int layer = ((minimize) ? 0 : 1) + 2 * (rowError ? 0 : 1);
        for (int row = 0; row < rows; row++) {
            output[0][row] = input[row];
        }
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //for every (row,column) location in the image
            for (int row = 0; row < rows; row++) {
                //gets its neighborhood
                int cell = 0;
                int phasePower = (int) Math.pow(2, d - 1);
                for (int r = 0; r < 4; r++) {
                    cell += (int) Math.pow(16, r) * output[d - 1][(row + phasePower * r) % rows];
                }
                //stores the neighborhood's codeword
                output[d][row] = (hash.allTables[layer][rule][cell]);
            }
        }
        return output;
    }

    /**
     * 1D version of the hash algorithm, single codeword set and compression version
     *
     * @param input    a 1D array of input data to be hashed
     * @param rule     a 0-255 ECA rule
     * @param depth    iterative depth, how many times to hash the data
     * @param minimize if true uses the minimizing codeword set, if false uses the maximizing set
     * @param rowError if true uses the row-weighted truth tables, if false uses the column-weighted set
     * @return the input data, hashed depth times
     */
    public int[] hashArrayCompression(int[] input, int rule, int depth, boolean minimize, boolean rowError) {
        //initWolframs();
        int rows = input.length;
        int[][] output = new int[depth + 1][rows];
        //initialize layer 0 to the input
        int layer = ((minimize) ? 0 : 1) + 2 * (rowError ? 0 : 1);
        for (int row = 0; row < rows; row++) {
            output[0][row] = input[row];
        }
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //for every (row,column) location in the image
            for (int row = 0; row < rows / (1 << (depth - 1)); row++) {
                //gets its neighborhood
                int cell = 0;
                int phasePower = (int) Math.pow(2, d - 1);
                for (int r = 0; r < 4; r++) {
                    cell += (int) Math.pow(16, r) * output[d - 1][(row + phasePower * r) % rows];
                }
                //stores the neighborhood's codeword
                output[d][row] = (hash.allTables[layer][rule][cell]);
            }
        }
        int[] out = new int[rows / (1 << (depth - 1))];
        for (int row = 0; row < (1 << (depth - 1)); row++) {
            out[row] = output[depth][row];
        }
        return out;
    }

    /**
     * 1D version of the hash inversion, all 32 codeword sets
     *
     * @param input input data
     * @param depth depth of iteration, how many times to hash and rehash
     * @return inverted input[][], lossy
     */
    public int[] invert(int[][] input, int depth) {
        int neighborDistance = 1 << (depth - 1);
        int[][] votes = new int[input.length][4];
        for (int row = 0; row < input.length; row++) {
            for (int listLayer = 0; listLayer < 4; listLayer++) {
                for (int t = 0; t < 8; t++) {
                    //apply its vote to every location that it influences
                    //including itself
                    int[][] generatedGuess = hash.hashRowsColumns[listLayer / 2].generateCodewordTile(input[row], hash.bothLists[listLayer][t]);
                    for (int r = 0; r < 4 && listLayer / 2 % 2 == 0; r++) {
                        for (int c = 0; c < 4; c++) {
                            if (generatedGuess[r][c] == listLayer % 2) {
                                votes[(row + neighborDistance * (4 * r + c)) % input.length][c] += (1 << r);
                            } else {
                                votes[(row + neighborDistance * (4 * r + c)) % input.length][c] -= (1 << r);
                            }
                        }
                    }
                    for (int r = 0; r < 4 && listLayer / 2 % 2 == 1; r++) {
                        for (int c = 0; c < 4; c++) {
                            if (generatedGuess[r][c] == listLayer % 2) {
                                votes[(row + neighborDistance * (4 * r + c)) % input.length][c] += (1 << c);
                            } else {
                                votes[(row + neighborDistance * (4 * r + c)) % input.length][c] -= (1 << c);
                            }
                        }
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        int[] outResult = new int[input.length];
        int[] outCompare = new int[input.length];
        int totDifferent = 0;
        for (int row = 0; row < input.length; row++) {
            for (int power = 0; power < 4; power++) {
                if (votes[row][power] >= 0) {
                    outResult[row] += 0;
                } else {
                    outResult[row] += (1 << power);
                }
            }
            //outCompare[row] = outResult[row] ^ input[row];
            //totDifferent += outCompare[row];
        }
        //System.out.println("totDifferent: " + totDifferent);
        //System.out.println("totLength: " + (input.length ));
        //System.out.println("different/Area=errors/bit= " + ((double) totDifferent / (double) (input.length )));
        return outResult;
    }

    /**
     * 1D version of the hash inverse operation, single codeword set
     *
     * @param input    hashed input data to be inverted
     * @param rule     0-255 ECA rule used to hash the data
     * @param depth    depth of iteration of the hashed data
     * @param minimize if true uses the minimizing codeword set, if false uses the maximizing set
     * @param rowError if true uses the row-weighted truth tables, if false uses the column-weighted set
     * @return inverted input[][]
     */
    public int[] invert(int[] input, int rule, int depth, boolean minimize, boolean rowError) {
        int neighborDistance = 1 << (depth - 1);
        int[][] votes = new int[input.length][4];
        int listLayer = rowError ? 0 : 1;
        int posNeg = minimize ? 0 : 1;
        for (int row = 0; row < input.length; row++) {
            //apply its vote to every location that it influences
            //including itself
            int[][] generatedGuess = hash.hashRowsColumns[listLayer].generateCodewordTile(input[row], rule);
            for (int r = 0; r < 4 && rowError; r++) {
                for (int c = 0; c < 4; c++) {
                    if (generatedGuess[r][c] == posNeg) {
                        votes[(row + neighborDistance * (r)) % input.length][c] += (1 << r);
                    } else {
                        votes[(row + neighborDistance * (r)) % input.length][c] -= (1 << r);
                    }
                }
            }
            for (int r = 0; r < 4 && !rowError; r++) {
                for (int c = 0; c < 4; c++) {
                    if (generatedGuess[r][c] == posNeg) {
                        votes[(row + neighborDistance * (r)) % input.length][c] += (1 << c);
                    } else {
                        votes[(row + neighborDistance * (r)) % input.length][c] -= (1 << c);
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        int[] outResult = new int[input.length];
        int[] outCompare = new int[input.length];
        int totDifferent = 0;
        for (int row = 0; row < input.length; row++) {
            for (int power = 0; power < 4; power++) {
                if (votes[row][power] >= 0) {
                    outResult[row] += 0;
                } else {
                    outResult[row] += (1 << power);
                }
            }
            outCompare[row] = outResult[row] ^ input[row];
            totDifferent += outCompare[row];
        }
        //System.out.println("totDifferent: " + totDifferent);
        //System.out.println("totLength: " + (input.length ));
        //System.out.println("different/Area=errors/bit= " + ((double) totDifferent / (double) (input.length )));
        return outResult;
    }

    /**
     * Tests these 1D hash versions with random data
     */
    public void testOneD() {
        //hash.initWolframs();
        int size = 100;
        int depth = 1;
        int rule = 2;
        int layer = 0;
        int[] input = new int[size];
        Random rand = new Random();
        for (int index = 0; index < size; index++) {
            input[index] = rand.nextInt(0, 16);
        }
        int[] out = hashArray(input, hash.bothLists[layer][rule], 1, true, true)[1];
        System.out.println(Arrays.toString(input));
        System.out.println(Arrays.toString(out));
        int[] inversion = invert(out, hash.bothLists[layer][rule], 1, true, true);
        int same = 0;
        int diff = 0;
        for (int index = 0; index < size; index++) {
            for (int power = 0; power < 4; power++) {
                if (((input[index] >> power) % 2) == ((inversion[index] >> power) % 2)) {
                    same++;
                } else {
                    diff++;
                }
            }
        }
        System.out.println(Arrays.toString(inversion));
        System.out.println("same: " + same);
        System.out.println("diff: " + diff);
        int[] compressed = hashArrayCompression(input, hash.bothLists[layer][rule], 1, true, true);
        System.out.println(Arrays.toString(compressed));
    }
}
