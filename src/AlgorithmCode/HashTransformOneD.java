package AlgorithmCode;

/**
 * One D version of the hash algorith
 */
public class HashTransformOneD {
    /**
     * Algorithmic manager function
     */
    Hash hash;

    /**
     * Sets the algorithm parent
     * @param inHash Owner of this class
     */
    public HashTransformOneD(Hash inHash) {
        hash = inHash;
    }



    /**
     * Takes in a 1D array of hashed data in codeword form, then rehashes sets of codewords increasingly far apart in steps of powers of 2, 1 apart 2 apart 4 apart ... 2^n apart
     *
     * @param input a 2D array of hashed data
     * @param rule  one of {0,15,51,85,170,204,240,255}
     * @param depth iterative depth, also the power of how far away its neighbors are
     * @return the input data, rehashed with neighbors 2^depth apart
     */
    public int[][] hashArray(int[] input, int rule, int depth, boolean minimize, boolean rowError) {
        //initWolframs();
        int rows = input.length;
        int[][] output = new int[depth + 1][rows];
        //initialize layer 0 to the input
        int layer = ((minimize) ? 0 : 1) + 2*(rowError ? 0 : 1);
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
     * Takes in a 1D array of hashed data in codeword form, then rehashes sets of codewords increasingly far apart in steps of powers of 2, 1 apart 2 apart 4 apart ... 2^n apart
     *
     * @param input a 2D array of hashed data
     * @param rule  one of {0,15,51,85,170,204,240,255}
     * @param depth iterative depth, also the power of how far away its neighbors are
     * @return the input data, rehashed with neighbors 2^depth apart
     */
    public int[] hashArrayCompression(int[] input, int rule, int depth, boolean minimize, boolean rowError) {
        //initWolframs();
        int rows = input.length;
        int[][] output = new int[depth + 1][rows];
        //initialize layer 0 to the input
        int layer = ((minimize) ? 0 : 1) + 2*(rowError ? 0 : 1);
        for (int row = 0; row < rows; row++) {
            output[0][row] = input[row];
        }
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //for every (row,column) location in the image
            for (int row = 0; row < rows/(1<<(depth-1)); row++) {
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
        int[] out = new int[rows/(1<<(depth-1))];
        for (int row = 0; row < (1<<(depth-1)); row++) {
            out[row] = output[depth][row];
        }
        return out;
    }

    /**
     * Inverse operation
     * @param input input data
     * @param depth iteration number
     * @return inverted input
     */
    public int[] invert(int[] input, int depth, boolean rowError) {
        int neighborDistance = 1 << (depth - 1);
        int[][] votes = new int[input.length][4];
        int listLayer = rowError ? 0 : 1;
        for (int row = 0; row < input.length; row++) {
            for (int posNeg = 0; posNeg < 2; posNeg++) {
                for (int t = 0; t < 8; t++) {
                    //apply its vote to every location that it influences
                    //including itself
                    int[][] generatedGuess = hash.hashRows.generateCodewordTile(input[row], hash.bothLists[listLayer][t]);
                    for (int r = 0; r < 4; r++) {
                        for (int c = 0; c < 4; c++) {
                            if (generatedGuess[r][c] == posNeg) {
                                votes[(row + neighborDistance * (4*r+c)) % input.length][c] += (1 << r);
                            } else {
                                votes[(row + neighborDistance * (4*r+c)) % input.length][c] -= (1 << r);
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
            outCompare[row] = outResult[row] ^ input[row];
            totDifferent += outCompare[row];
        }
        System.out.println("totDifferent: " + totDifferent);
        System.out.println("totLength: " + (input.length ));
        System.out.println("different/Area=errors/bit= " + ((double) totDifferent / (double) (input.length )));
        return outResult;
    }


}
