package AlgorithmCode;

/**
 *
 */
public class HashTransform {
    /**
     * Hash subroutines
     */
    errorMinimizationHash m = new errorMinimizationHash();
    /**
     * The entire set of min and max codewords of [0,15,51,85,170,204,240,255]
     */
    int[][][] flatWolframs = new int[2][8][256 * 256];
    /**
     * The 8 rules referred to in the paper that have an even distribution of codewords
     * and unique codewords for every input
     */
    int[] unpackedList = new int[]{0, 15, 51, 85, 170, 204, 240, 255};

    /**
     * Does the Hash transform on 1D input
     *
     * @param input binary array
     * @return
     */
    public int[] oneD(int[] input) {
        int[] out = new int[input.length];
//        int[][] intermediate = new int[input.length][input.length];
//        for (int row = 0; row < input.length; row++) {
//            for (int col = 0; col < input.length; col++) {
//                intermediate[row][col] = input[col];
//            }
//        }
//        int[][][][] transformed = minTransformTwoPaths(intermediate, wolframTuple);
//        int[][][] reconstructed = reconstruct(vectorField, 4, 0, new int[]{0, 0, 0});
//        for (int row = 0; row < input.length; row++) {
//            out[row] = reconstructed[4][row][0];
//        }
        return out;
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
    public int[][][] initializeDepthZero(int[][] input, int rule) {
        //initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        //mirrors, xy, phase, depth, (cell mirrors, minMax tree)
        //solutions = new int[8][2][4][5][256 * 256];
        int[][] out = new int[rows][cols];
        int totLength = 256 * 256;
        int[][][] deepInput = new int[4][rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int cell = 0;
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        cell += (int) Math.pow(2, 4 * r + c) * deepInput[0][(row + r) % rows][(col + c) % cols];
                    }
                }
                deepInput[1][row][col] = m.minSolutionsAsWolfram[rule][cell];
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
    public int[][][] ecaMinTransform(int[][] input, int rule, int depth) {
        initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        //mirrors, xy, phase, depth, (cell mirrors, minMax tree)
        //solutions = new int[8][2][4][5][256 * 256];
        int[][] out = new int[rows][cols];
        int totLength = 256 * 256;
        int[][][] deepInput = new int[depth + 1][rows][cols];
        int[][][] init;// = minTransform(input, wolframTuple);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        for (int d = 2; d <= depth; d++) {
            for (int row = 0; row < rows; row++) {
                System.out.print(row + " ");
                if (row % 32 == 0) System.out.print("\n " + (rows - row) + " ");
                for (int col = 0; col < cols; col++) {
                    int cell = 0;
                    int phasePower = (int) Math.pow(2, d - 1);
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            cell += (int) Math.pow(4, 2 * r + c) * deepInput[depth - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                        }
                    }
                    deepInput[depth][row][col] = (m.minSolutionsAsWolfram[rule][cell]);
                }
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
    public int[][][] ecaMaxTransform(int[][] input, int rule, int depth) {
        initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        //mirrors, xy, phase, depth, (cell mirrors, minMax tree)
        //solutions = new int[8][2][4][5][256 * 256];
        int[][] out = new int[rows][cols];
        int totLength = 256 * 256;
        int[][][] deepInput = new int[depth + 1][rows][cols];
        int[][][] init;// = minTransform(input, wolframTuple);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        for (int d = 2; d <= depth; d++) {
            for (int row = 0; row < rows; row++) {
                System.out.print(row + " ");
                if (row % 32 == 0) System.out.print("\n " + (rows - row) + " ");
                for (int col = 0; col < cols; col++) {
                    int cell = 0;
                    int phasePower = (int) Math.pow(2, d - 1);
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            cell += (int) Math.pow(4, 2 * r + c) * deepInput[depth - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                        }
                    }
                    deepInput[depth][row][col] = (m.maxSolutionsAsWolfram[rule][cell]);
                }
            }
        }
        return deepInput;
    }

    /**
     * Initializes the set of hash truth tables for [0,15,51,85,170,204,240,255]
     */
    public void initWolframs() {
        //int[][] ruleList = packedList;
        int[][][] wolframIn = new int[4][2][256 * 256];
        int[][][] maxWolframIn = new int[4][2][256 * 256];
        //m.doAllRulesCoords(4, false, 0, false, 0, false, ruleList);
        for (int spot = 0; spot < 4; spot++) {
            for (int lr = 0; lr < 2; lr++) {
                //m.individualRule(ruleList[spot][lr],4,false,0,false,0,false);
                //wolframIn[spot][lr] = m.minSolutionsAsWolfram[ruleList[spot][lr]];
                //maxWolframIn[spot][lr] = m.maxSolutionsAsWolfram[ruleList[spot][lr]];
            }
        }
        //int[][] wolfram = m.minSolutionsAsWolfram;
        //int[][] maxWolfram = m.maxSolutionsAsWolfram;
        //minMax,group,leftright
        //wolframs = new int[2][4][2][256 * 256];
        for (int spot = 0; spot < 4; spot++) {
            for (int lr = 0; lr < 2; lr++) {
                for (int column = 0; column < 256 * 256; column++) {
                    //wolframs[0][spot][lr][column] = wolframIn[spot][lr][column];
                    //wolframs[1][spot][lr][column] = maxWolframIn[spot][lr][column];
                    //flatWolframs[0][2 * spot + lr][column] = wolframIn[spot][lr][column];
                    //flatWolframs[1][2 * spot + lr][column] = maxWolframIn[spot][lr][column];
                    flatWolframs[0][2 * spot + lr][column] = m.minSolutionsAsWolfram[unpackedList[2 * spot + lr]][column];
                    flatWolframs[1][2 * spot + lr][column] = m.maxSolutionsAsWolfram[unpackedList[2 * spot + lr]][column];
                }
            }
        }
    }
}



