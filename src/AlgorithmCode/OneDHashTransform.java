package AlgorithmCode;

public class OneDHashTransform {
    HashTransform hash;

    public OneDHashTransform(HashTransform inHashTransform) {
        hash = inHashTransform;
    }

    /**
     * Takes raw binary data and does the initial conversion to one codeword per point covering its
     * area of influence, before comparing them with neighbors in ecaMinMaxTransform()
     *
     * @param input a 2D binary array
     * @param rule  an ECA rule
     * @return a set of 2D arrays with input in layer 0, and layer 1 is the codeword-ified input,
     * the rest is empty
     */
    public int[][] initializeDepthZero(int[] input, int rule) {
        int rows = input.length;
        int[][] deepInput = new int[4][rows];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            deepInput[0][row] = input[row];
        }
        //for every location in the bitmap
        for (int row = 0; row < rows; row++) {
            //gets its neighborhood
            int cell = 0;
            for (int r = 0; r < 16; r++) {
                cell += (int) Math.pow(2, r) * deepInput[0][(row + r) % rows];
            }
            //finds the neighborhood's codeword
            deepInput[1][row] = hash.m.minSolutionsAsWolfram[rule][cell];
        }
        return deepInput;
    }

    /**
     * Takes in a 2D array of hashed data in codeword form, then rehashes sets of codewords increasingly far apart in steps of powers of 2, 1 apart 2 apart 4 apart ... 2^n apart
     *
     * @param input a 2D array of hashed data
     * @param rule  one of {0,15,51,85,170,204,240,255}
     * @param depth iterative depth, also the power of how far away its neighbors are
     * @return the input data, rehashed with neighbors 2^depth apart
     */
    public int[][] ecaMinTransform(int[] input, int rule, int depth) {
        //initWolframs();
        int rows = input.length;
        int[][] deepInput = new int[depth + 1][rows];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            deepInput[0][row] = input[row];
        }
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //for every (row,column) location in the image
            for (int row = 0; row < rows; row++) {
                //gets its neighborhood
                int cell = 0;
                int phasePower = (int) Math.pow(2, d - 1);
                for (int r = 0; r < 4; r++) {
                    cell += (int) Math.pow(16, r) * deepInput[d - 1][(row + phasePower * r) % rows];
                }
                //stores the neighborhood's codeword
                deepInput[d][row] = (hash.m.minSolutionsAsWolfram[rule][cell]);
            }
        }
        return deepInput;
    }

    /**
     * Takes in a 2D array of hashed data in codeword form, then rehashes sets of codewords increasingly far apart in steps of powers of 2, 1 apart 2 apart 4 apart ... 2^n apart
     *
     * @param input a 2D array of hashed data
     * @param rule  one of {0,15,51,85,170,204,240,255}
     * @param depth iterative depth, also the power of how far away its neighbors are
     * @return the input data, rehashed with neighbors 2^depth apart
     */
    public int[][] ecaMaxTransform(int[] input, int rule, int depth) {
        //initWolframs();
        int rows = input.length;
        int[][] deepInput = new int[depth + 1][rows];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            deepInput[0][row] = input[row];
        }
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //for every (row,column) location in the image
            for (int row = 0; row < rows; row++) {
                //gets its neighborhood
                int cell = 0;
                int phasePower = (int) Math.pow(2, d - 1);
                for (int r = 0; r < 4; r++) {
                    cell += (int) Math.pow(16, r) * deepInput[d - 1][(row + phasePower * r) % rows];
                }
                //stores the neighborhood's codeword
                deepInput[d][row] = (hash.m.maxSolutionsAsWolfram[rule][cell]);
            }
        }
        return deepInput;
    }

    /**
     * Does the legwork of reconstituting input from sets of codewords
     * The commented out code that includes a and generatedGuess() is the original voting mechanism
     * where a codeword generates a square neighborhood to decompress the data back to original
     * What's here at the moment is the Hadamard parity, which results in the same thing with an
     * almost identical error rate. The Hadamard parity is count the number of 1s in the bits of
     * the binary codeword and take that mod 2. For some reason the Hadamard parity can be substituted
     * for the codeword's generate neighborhood. This was found by experimentation after it was discovered
     * that codeword addition results in the non-reduced ROW AND COLUMN matrix that produces the boolean
     * Hadamard matrix.
     *
     * @param in 2D codeword input array
     */
    public void checkInverse(int[] in, int depth) {
        //load the minMax 8 tuple subset Wolfram codes
        hash.initWolframs();
        //Operating set
        int[][][] depthChart = new int[2][8][in.length];
        //puts the input data as layer 0 of the output data
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                System.out.println("posNeg: " + posNeg + " t: " + t);
                depthChart[posNeg][t] = initializeDepthZero(in, hash.unpackedList[t])[1];
            }
        }
        //this array is the vote tally, location is influenced by 16 neighborhoods within a distance of 4
        //each of these neighborhoods has 16 terms in the min max codeword set of the 8 tuple
        //every term of every vote is weighted by 2^RelativeRow
        int[] outVotes = new int[in.length];
        int r;
        int c;
        int t;
        int posNeg;
        int hadamardValue;
        int power;
        int row;
        int column;
        //for every location in the transformed bitmap data
        for (row = 0; row < in.length; row++) {
            System.out.println("row: " + row + " out of " + in.length);
            //for every term in its min max codeword set
            for (posNeg = 0; posNeg < 2; posNeg++) {
                for (t = 0; t < 8; t++) {
                    //apply its vote to every location that it influences
                    //including itself
                    //int[][] generatedGuess = m.generateGuess(depthChart[posNeg][t][row][column], fmt.unpackedList[t]);
                    hadamardValue = 0;
                    for (power = 0; power < 4; power++) {
                        //hadamardValue += ((depthChart[posNeg][t][row] >> power) % 2);
                        hadamardValue += ((in[row]>>power)%2);
                    }
                    hadamardValue %= 2;
                    for (r = 0; r < 16; r++) {
                        //int a = (generatedGuess[r][c] );
                        //if (generatedGuess[r][c] == posNeg) {
                        if (hadamardValue == posNeg) {
                            outVotes[(row + r) % in.length] += (1 << r);
                        } else {
                            outVotes[(row + r) % in.length] -= (1 << r);
                        }
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        int[] outResult = new int[in.length];
        int[] outCompare = new int[in.length];
        int totDifferent = 0;
        for (row = 0; row < in.length; row++) {
            if (outVotes[row] >= 0) {
                outResult[row] = 0;
            } else {
                outResult[row] = 1;
            }
            outCompare[row] = outResult[row] ^ in[row];
            totDifferent += outCompare[row];
        }
        System.out.println("totDifferent: " + totDifferent);
        System.out.println("totLength: " + (in.length));
        System.out.println("different/Area=errors/bit= " + ((double) totDifferent / (double) (in.length)));
    }
}
