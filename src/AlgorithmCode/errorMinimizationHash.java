package AlgorithmCode;

import CustomLibrary.BasicECA;
import CustomLibrary.CustomArray;
import CustomLibrary.StringPrint;

import java.util.Arrays;
import java.util.Random;

/**
 * Generates, tests, and displays sets of truth tables, where the table is addressed by a binary input neighborhood and it's value is
 * a hexadecimal codeword ECA rule pair together that minimize the discrepancies between the input and the ECA output using the codeword as the
 * initial value
 */

public class errorMinimizationHash {

    /**
     * Last max codeword tile found
     */
    int[][] localMaxSolution;
    /**
     * Last min codeword tile found
     */
    int[][] localMinSolution;
    /**
     * Within a single solution, if multiple codewords have the same weighted error score, each codeword is placed here
     */
    int[][] sameErrorMin;
    /**
     * Within a single maximization solution, if multiple codewords have the same weighted error score, each is placed here
     */
    int[][] sameErrorMax;
    /**
     * Cumulative heat map of errors, maximization version
     */
    int[][][] maxErrorMap;
    /**
     * Cumulative heat map of errors
     */
    int[][][] minErrorMap;
    /**
     * Weighted change score of an input, scored the same way as the errors
     */
    int weightedChangeScore;
    /**
     * Cumulative Hamming change in codewords
     */
    int totHammingChange;
    /**
     * Total number of errors in all solutions found
     */
    int totLocalErrors;
    /**
     * Minimum solution codewords stored as a Wolfram code, with the address being the integer value of the binary
     */
    int[][] minSolutionsAsWolfram;
    /**
     * Minimum solution codewords stored as a Wolfram code, with the address being the integer value of the binary
     */
    int[][] maxSolutionsAsWolfram;
    /**
     * Cumulative distribution of codewords over all inputs
     */
    int[][] minSolutionDistro;
    /**
     * Cumulative distribution of codewords over all inputs, maximized error codewords rather than minimized error codewords
     */
    int[][] maxSolutionDistro;
    /**
     * Last min codeword found
     */
    int lastMinCodeword;
    /**
     * Last max codeword found
     */
    int lastMaxCodeword;
    /**
     * A size x size square used internally
     */
    int[][] field;
    /**
     * Total number of errors across all values of an ECA rule's min codeword truth table
     */
    int[] minErrorBuckets;
    /**
     * Total number of errors across all values of an ECA rule's max codeword truth table
     */
    int[] maxErrorBuckets;
    /**
     * Length of an ECA rule's codeword truth table
     */
    int[] numberBoards;
    /**
     * Part of changeChange(), it takes an address and a codeword, randomly changes the neighborhood,
     * then compares the neighborhood change to codeword change
     */
    int[] errorChange;
    /**
     * Part of changeChange(), this is the cumulative Hamming distance between all sets of codewords and the neighborhood-changed codewords
     */
    int[] hammingChange;
    /**
     * Part of changeChange(), this is how much the neighborhood changes
     */
    double[] changeChange;
    /**
     * Part of changeChange(), this is the overall change score ???
     */
    int[] changeScore;
    /**
     * Quantity of errors in reconstituting input from min codeword neighborhoods
     */
    int totMaxErrors;
    /**
     * Quantity of errors in reconstituting input from max codeword neighborhoods
     */
    int totMinErrors;
    /**
     * In the reconstitution process, the inverse of the hash transform, the output is voted on,
     * this is the tally of the min codeword votes
     */
    int[][] minVote;
    /**
     * In the reconstitution process, the inverse of the hash transform, the output is voted on,
     * this is the tally of the max codeword votes
     */
    int[][] maxVote;
    /**
     * Intermediate array used internally within the algorithm
     */
    int[][] maxTemp;
    /**
     * Intermediate array used internally within the algorithm
     */
    int[][] minTemp;
    /**
     * all 256 ECA rules' min codeword error/square
     */
    double[] minErrorPerArray;
    /**
     * All 256 ECA rules' max codeword error/square
     */
    double[] maxErrorPerArray;
    /**
     * All 256 ECA rules' min codeword error/bit
     */
    double[] minErrorPerBit;
    /**
     * All 256 ECA rules' max codeword error/bit
     */
    double[] maxErrorPerBit;
    /**
     * All 256 ECA rules sorted by error rate, min codeword
     */
    int[] minSorted;
    /**
     * All 256 ECA rules sorted by error rate, max codeword
     */
    int[] maxSorted;
    /**
     * All 256 ECA rules' number of ties in the min codeword error scores.
     * If an ECA rule has a 1, that means that every possible input
     * has a unique codeword. If it's greater than 1 that means more
     * than one codeword produces the same errorScore meaning there has
     * to be some kind of tiebreaker tbd.
     */
    int[] minNumberOfSameSolutions;
    /**
     * All 256 ECA rules' number of ties in the max codeword error scores.
     * If an ECA rule has a 1, that means that every possible input
     * has a unique codeword. If it's greater than 1 that means more
     * than one codeword produces the same errorScore meaning there has
     * to be some kind of tiebreaker tbd.
     */
    int[] maxNumberOfSameSolutions;
    /**
     * Custom ECA library
     */
    BasicECA basicECA = new BasicECA();
    /**
     * Custom array display functions
     */
    CustomArray customArray = new CustomArray();
    /**
     * Random number generator
     */
    Random rand = new Random();

    /**
     * Initializes everything to size 4, therefore truth tables are 65536 long.
     * If you want a different size run initialize(size)
     */

    public errorMinimizationHash() {
        int size = 4;
        sameErrorMin = new int[256][(int) Math.pow(2, size)];
        sameErrorMax = new int[256][(int) Math.pow(2, size)];
        minNumberOfSameSolutions = new int[256];
        maxNumberOfSameSolutions = new int[256];
        localMinSolution = new int[size][size];
        localMaxSolution = new int[size][size];
        minSolutionDistro = new int[256][(int) Math.pow(2, size)];
        maxSolutionDistro = new int[256][(int) Math.pow(2, size)];
        minErrorMap = new int[256][size][size];
        maxErrorMap = new int[256][size][size];
        minErrorBuckets = new int[256];
        maxErrorBuckets = new int[256];
        numberBoards = new int[256];
        minSolutionsAsWolfram = new int[256][(int) Math.pow(2, size * size)];
        maxSolutionsAsWolfram = new int[256][(int) Math.pow(2, size * size)];
    }

    /**
     * Initializes arrays to size. Sizes above five bust it
     *
     * @param size length and width of the input neighborhood
     */
    public void initialize(int size) {
        sameErrorMin = new int[256][(int) Math.pow(2, size)];
        sameErrorMax = new int[256][(int) Math.pow(2, size)];
        minNumberOfSameSolutions = new int[256];
        maxNumberOfSameSolutions = new int[256];
        localMinSolution = new int[size][size];
        localMaxSolution = new int[size][size];
        minSolutionDistro = new int[256][(int) Math.pow(2, size)];
        maxSolutionDistro = new int[256][(int) Math.pow(2, size)];
        minErrorMap = new int[256][size][size];
        maxErrorMap = new int[256][size][size];
        minErrorBuckets = new int[256];
        maxErrorBuckets = new int[256];
        numberBoards = new int[256];
        minSolutionsAsWolfram = new int[256][(int) Math.pow(2, size * size)];
        maxSolutionsAsWolfram = new int[256][(int) Math.pow(2, size * size)];
    }

    /**
     * Finds the minimum-discrepancy initial neighborhood output of the ECA n vs the input
     *
     * @param n  ECA rule 0-255
     * @param in binary input array
     * @return the array with ECA n and initial neighborhood mimimum output
     */
    public int[][] findMinimizingCodeword(int n, int[][] in, int[] wolfram) {
        int size = in.length;
        localMaxSolution = new int[size][size];
        localMinSolution = new int[size][size];
        //if (wolfram == null){
        wolfram = new int[8];
        for (int power = 0; power < 8; power++) {
            wolfram[power] = (n / (int) Math.pow(2, power)) % 2;
        }
        //}
        int[][] trialField = new int[size][size];
        //Declaring these here instead of inline in the loops significantly speeds it up
        int row = 0;
        int column = 0;
        int a = 0;
        int b = 0;
        int c = 0;
        int maxNeighborhood = (int) Math.pow(2, size);
        int[] errorScore = new int[maxNeighborhood];
        //
        //
        //Check every possible input neighborhood of length size
        for (int neighborhood = 0; neighborhood < maxNeighborhood; neighborhood++) {
            //Initialize trial neighborhood
            for (column = 0; column < size; column++) {
                trialField[0][column] = ((neighborhood / (int) Math.pow(2, column)) % 2);
            }
            //Run Wolfram code on array with row 0 input = correction
            for (row = 1; row < size; row++) {
                for (column = 0; column < size; column++) {
                    a = ((column - 1) + size) % size;
                    b = column;
                    c = ((column + 1)) % size;
                    trialField[row][column] = trialField[row - 1][a] + 2 * trialField[row - 1][b] + 4 * trialField[row - 1][c];
                    trialField[row][column] = wolfram[trialField[row][column]];
                }
            }
            //
            //
            //Score the error
            for (row = 0; row < size; row++) {
                for (column = 0; column < size; column++) {
                    //
                    //
                    //Various error-scoring weights tested
                    errorScore[neighborhood] += ((int) Math.pow(2, row) * (trialField[row][column] ^ in[row][column]));
                    //errorScore[neighborhood] +=  ((row*row))*(trialField[row][column] ^ in[row][column]);
                    //errorScore[correction] += row*row*(trialField[row][column] ^ in[row][column]);
                    //errorScore[correction] += column*column *(in[row][column] ^  trialField[row][column]);
                    //errorScore[correction] += column* (in[row][column] ^  trialField[row][column]);
                    //errorScore[neighborhood] += row* (in[row][column] ^  trialField[row][column]);
                    //errorScore[correction] += (in[row][column] ^ trialField[row][column]);
                    //errorScore[neighborhood] += (int)Math.pow(2, column)*(in[row][column] ^ trialField[row][column]);
                    //errorScore[correction] += coefficients[row] * trialField[row][column];
                }
            }
        }
        //
        //
        //Sort the errors to find the minimum error producing neighborhood
        //System.out.println(Arrays.toString(errorScore));
        int maxErrors = 0;
        int maxSpot = 0;
        for (int neighborhood = 0; neighborhood < maxNeighborhood; neighborhood++) {
            if (errorScore[neighborhood] > maxErrors) {
                maxErrors = errorScore[neighborhood];
                //maxSpot = neighborhood;
            }
        }
        int minErrors = Integer.MAX_VALUE;
        int minSpot = 0;
        for (int neighborhood = 0; neighborhood < maxNeighborhood; neighborhood++) {
            if (errorScore[neighborhood] < minErrors) {
                minErrors = errorScore[neighborhood];
                //minSpot = neighborhood;
            }
        }
//        System.out.println("minErrors " + minErrors);
//        System.out.println("maxErrors " + maxErrors);
//        System.out.println("minSpot " + minSpot);
//            int hammingDistance = 0;
//            for (int spot = 0; spot < size; spot++) {
//                if (((minSpot / (int) Math.pow(2, spot)) % 2) != ((decInput / (int) Math.pow(2, spot)) % 2)) {
//                    hammingDistance++;
//                }
//            }
        //
        //
        //
        //Checks for solution uniqueness
        int numSameMinimum = 0;
        int numSameMaximum = 0;
        int[] sameMinimums = new int[errorScore.length];
        int[] sameMaximums = new int[errorScore.length];
        Arrays.fill(sameMaximums, -1);
        Arrays.fill(sameMinimums, -1);
        int minIndex = 0;
        int maxIndex = 0;
        int firstMinSpot = -1;
        int firstMaxSpot = -1;
        for (int neighborhood = 0; neighborhood < (int) Math.pow(2, size); neighborhood++) {
            if (errorScore[neighborhood] == minErrors) {
                numSameMinimum++;
                sameMinimums[minIndex] = neighborhood;
                minIndex++;
                if (firstMinSpot == -1) {
                    firstMinSpot = neighborhood;
                }
            }
            if (errorScore[neighborhood] == maxErrors) {
                numSameMaximum++;
                sameMaximums[maxIndex] = neighborhood;
                maxIndex++;
                if (firstMaxSpot == -1) {
                    firstMaxSpot = neighborhood;
                }
            }
        }
        lastMinCodeword = firstMinSpot;
        lastMaxCodeword = firstMaxSpot;
        //sameMinimums = Arrays.copyOfRange(sameMinimums, 0, minIndex);
        // = Arrays.copyOfRange(sameMaximums, 0, maxIndex);
        //sameErrorMin[integerWolfram][numberOfSameMinimum]++;
        //sameErrorMax[integerWolfram][numberOfSameMaximum]++;
        if (numSameMinimum > minNumberOfSameSolutions[n]) minNumberOfSameSolutions[n] = numSameMinimum;
        if (numSameMaximum > maxNumberOfSameSolutions[n]) maxNumberOfSameSolutions[n] = numSameMaximum;
        //minNumberOfSameSolutions[n] += numSameMinimum;
        //maxNumberOfSameSolutions[n] += numSameMaximum;
        //System.out.println("sameNumErrors = " + sameNumErrors);
        //System.out.println("sameNumErrorsMax = " + sameNumMax);
        //System.out.println("sameNumErrors " + sameNumErrors);
        //
        //
        //Run the Wolfram code on the minimum neighborhood codeword for function return purposes
        trialField = new int[size][size];
        for (column = 0; column < size; column++) {
            trialField[0][column] = ((firstMinSpot / (int) Math.pow(2, column)) % 2);
            localMaxSolution[0][column] = ((firstMaxSpot / (int) Math.pow(2, column)) % 2);
            localMinSolution[0][column] = ((firstMinSpot / (int) Math.pow(2, column)) % 2);
        }
        minSolutionDistro[n][firstMinSpot]++;
        maxSolutionDistro[n][firstMaxSpot]++;
        for (row = 1; row < size; row++) {
            for (column = 0; column < size; column++) {
                a = ((column - 1) + size) % size;
                b = column;
                c = ((column + 1)) % size;
                trialField[row][column] = trialField[row - 1][a] + 2 * trialField[row - 1][b] + 4 * trialField[row - 1][c];
                trialField[row][column] = wolfram[trialField[row][column]];
                //localMaxSolution[row][column] = localMaxSolution[row-1][a]+2*trialField[row-1][b]+4*trialField[row-1][c];
                //localMaxSolution[row][column] = wolfram[localMaxSolution[row][column]];
            }
        }
        for (row = 1; row < size; row++) {
            for (column = 0; column < size; column++) {
                a = ((column - 1) + size) % size;
                b = column;
                c = ((column + 1)) % size;
                localMinSolution[row][column] = localMinSolution[row - 1][a] + 2 * localMinSolution[row - 1][b] + 4 * localMinSolution[row - 1][c];
                localMinSolution[row][column] = wolfram[localMinSolution[row][column]];
                //localMaxSolution[row][column] = localMaxSolution[row-1][a]+2*trialField[row-1][b]+4*trialField[row-1][c];
                //localMaxSolution[row][column] = wolfram[localMaxSolution[row][column]];
            }
        }
        for (row = 1; row < size; row++) {
            for (column = 0; column < size; column++) {
                a = ((column - 1) + size) % size;
                b = column;
                c = ((column + 1)) % size;
                localMaxSolution[row][column] = localMaxSolution[row - 1][a] + 2 * localMaxSolution[row - 1][b] + 4 * localMaxSolution[row - 1][c];
                localMaxSolution[row][column] = wolfram[localMaxSolution[row][column]];
                //localMaxSolution[row][column] = localMaxSolution[row-1][a]+2*trialField[row-1][b]+4*trialField[row-1][c];
                //localMaxSolution[row][column] = wolfram[localMaxSolution[row][column]];
            }
        }
        //if (numSameMinimum != 1) System.out.println(numSameMinimum);
        return trialField;
    }

    /**
     * Finds the minimum-discrepancy initial neighborhood output of the ECA n vs the input,
     * sans any reference of the entire Wolfram code
     *
     * @param n  ECA rule 0-255
     * @param in binary input array
     * @return the array with ECA n and initial neighborhood mimimum output
     */
    public void findMinimizingCodewordLarge(int n, int[][] in, int[] wolfram) {
        int size = in.length;
        localMaxSolution = new int[size][size];
        localMinSolution = new int[size][size];
        //if (wolfram == null){
        wolfram = new int[8];
        for (int power = 0; power < 8; power++) {
            wolfram[power] = (n / (int) Math.pow(2, power)) % 2;
        }
        //}
        int[][] trialField = new int[size][size];
        //Declaring these here instead of inline in the loops significantly speeds it up
        int row = 0;
        int column = 0;
        int a = 0;
        int b = 0;
        int c = 0;
        int maxNeighborhood = (int) Math.pow(2, size);
        int[] errorScore = new int[maxNeighborhood];
        //
        //
        //Check every possible input neighborhood of length size
        for (int neighborhood = 0; neighborhood < maxNeighborhood; neighborhood++) {
            //Initialize trial neighborhood
            for (column = 0; column < size; column++) {
                trialField[0][column] = ((neighborhood / (int) Math.pow(2, column)) % 2);
            }
            //Run Wolfram code on array with row 0 input = correction
            for (row = 1; row < size; row++) {
                for (column = 0; column < size; column++) {
                    a = ((column - 1) + size) % size;
                    b = column;
                    c = ((column + 1)) % size;
                    trialField[row][column] = trialField[row - 1][a] + 2 * trialField[row - 1][b] + 4 * trialField[row - 1][c];
                    trialField[row][column] = wolfram[trialField[row][column]];
                }
            }
            //
            //
            //Score the error
            for (row = 0; row < size; row++) {
                for (column = 0; column < size; column++) {
                    //
                    //
                    //Various error-scoring weights tested
                    errorScore[neighborhood] += ((int) Math.pow(2, row) * (trialField[row][column] ^ in[row][column]));
                    //errorScore[neighborhood] +=  ((row*row))*(trialField[row][column] ^ in[row][column]);
                    //errorScore[correction] += row*row*(trialField[row][column] ^ in[row][column]);
                    //errorScore[correction] += column*column *(in[row][column] ^  trialField[row][column]);
                    //errorScore[correction] += column* (in[row][column] ^  trialField[row][column]);
                    //errorScore[neighborhood] += row* (in[row][column] ^  trialField[row][column]);
                    //errorScore[correction] += (in[row][column] ^ trialField[row][column]);
                    //errorScore[neighborhood] += (int)Math.pow(2, column)*(in[row][column] ^ trialField[row][column]);
                    //errorScore[correction] += coefficients[row] * trialField[row][column];
                }
            }
        }
        //
        //
        //Sort the errors to find the minimum error producing neighborhood
        //System.out.println(Arrays.toString(errorScore));
        int maxErrors = 0;
        int maxSpot = 0;
        for (int neighborhood = 0; neighborhood < maxNeighborhood; neighborhood++) {
            if (errorScore[neighborhood] > maxErrors) {
                maxErrors = errorScore[neighborhood];
                //maxSpot = neighborhood;
            }
        }
        int minErrors = Integer.MAX_VALUE;
        int minSpot = 0;
        for (int neighborhood = 0; neighborhood < maxNeighborhood; neighborhood++) {
            if (errorScore[neighborhood] < minErrors) {
                minErrors = errorScore[neighborhood];
                //minSpot = neighborhood;
            }
        }
//        System.out.println("minErrors " + minErrors);
//        System.out.println("maxErrors " + maxErrors);
//        System.out.println("minSpot " + minSpot);
//            int hammingDistance = 0;
//            for (int spot = 0; spot < size; spot++) {
//                if (((minSpot / (int) Math.pow(2, spot)) % 2) != ((decInput / (int) Math.pow(2, spot)) % 2)) {
//                    hammingDistance++;
//                }
//            }
        //
        //
        //
        //Checks for solution uniqueness
        int numSameMinimum = 0;
        int numSameMaximum = 0;
        int[] sameMinimums = new int[errorScore.length];
        int[] sameMaximums = new int[errorScore.length];
        Arrays.fill(sameMaximums, -1);
        Arrays.fill(sameMinimums, -1);
        int minIndex = 0;
        int maxIndex = 0;
        int firstMinSpot = -1;
        int firstMaxSpot = -1;
        for (int neighborhood = 0; neighborhood < (int) Math.pow(2, size); neighborhood++) {
            if (errorScore[neighborhood] == minErrors) {
                numSameMinimum++;
                sameMinimums[minIndex] = neighborhood;
                minIndex++;
                if (firstMinSpot == -1) {
                    firstMinSpot = neighborhood;
                }
            }
            if (errorScore[neighborhood] == maxErrors) {
                numSameMaximum++;
                sameMaximums[maxIndex] = neighborhood;
                maxIndex++;
                if (firstMaxSpot == -1) {
                    firstMaxSpot = neighborhood;
                }
            }
        }
        lastMinCodeword = firstMinSpot;
        lastMaxCodeword = firstMaxSpot;
        //sameMinimums = Arrays.copyOfRange(sameMinimums, 0, minIndex);
        // = Arrays.copyOfRange(sameMaximums, 0, maxIndex);
        //sameErrorMin[integerWolfram][numberOfSameMinimum]++;
        //sameErrorMax[integerWolfram][numberOfSameMaximum]++;
//        if (numSameMinimum > minNumberOfSameSolutions[n]) minNumberOfSameSolutions[n] = numSameMinimum;
//        if (numSameMaximum > maxNumberOfSameSolutions[n]) maxNumberOfSameSolutions[n] = numSameMaximum;
//        //minNumberOfSameSolutions[n] += numSameMinimum;
//        //maxNumberOfSameSolutions[n] += numSameMaximum;
//
//        //System.out.println("sameNumErrors = " + sameNumErrors);
//        //System.out.println("sameNumErrorsMax = " + sameNumMax);
//        //System.out.println("sameNumErrors " + sameNumErrors);
//        //
//        //
//        //Run the Wolfram code on the minimum neighborhood codeword for function return purposes
//        trialField = new int[size][size];
//        for (column = 0; column < size; column++) {
//            trialField[0][column] = ((firstMinSpot / (int) Math.pow(2, column)) % 2);
//            localMaxSolution[0][column] = ((firstMaxSpot / (int) Math.pow(2, column)) % 2);
//            localMinSolution[0][column] = ((firstMinSpot / (int) Math.pow(2, column)) % 2);
//        }
//        minSolutionDistro[n][firstMinSpot]++;
//        maxSolutionDistro[n][firstMaxSpot]++;
//        for (row = 1; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                a = ((column - 1) + size) % size;
//                b = column;
//                c = ((column + 1)) % size;
//                trialField[row][column] = trialField[row - 1][a] + 2 * trialField[row - 1][b] + 4 * trialField[row - 1][c];
//                trialField[row][column] = wolfram[trialField[row][column]];
//                //localMaxSolution[row][column] = localMaxSolution[row-1][a]+2*trialField[row-1][b]+4*trialField[row-1][c];
//                //localMaxSolution[row][column] = wolfram[localMaxSolution[row][column]];
//            }
//        }
//        for (row = 1; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                a = ((column - 1) + size) % size;
//                b = column;
//                c = ((column + 1)) % size;
//                localMinSolution[row][column] = localMinSolution[row - 1][a] + 2 * localMinSolution[row - 1][b] + 4 * localMinSolution[row - 1][c];
//                localMinSolution[row][column] = wolfram[localMinSolution[row][column]];
//                //localMaxSolution[row][column] = localMaxSolution[row-1][a]+2*trialField[row-1][b]+4*trialField[row-1][c];
//                //localMaxSolution[row][column] = wolfram[localMaxSolution[row][column]];
//            }
//        }
//        for (row = 1; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                a = ((column - 1) + size) % size;
//                b = column;
//                c = ((column + 1)) % size;
//                localMaxSolution[row][column] = localMaxSolution[row - 1][a] + 2 * localMaxSolution[row - 1][b] + 4 * localMaxSolution[row - 1][c];
//                localMaxSolution[row][column] = wolfram[localMaxSolution[row][column]];
//                //localMaxSolution[row][column] = localMaxSolution[row-1][a]+2*trialField[row-1][b]+4*trialField[row-1][c];
//                //localMaxSolution[row][column] = wolfram[localMaxSolution[row][column]];
//            }
//        }
//        //if (numSameMinimum != 1) System.out.println(numSameMinimum);
//        return trialField;
    }

    /**
     * Outputs the square array, where row 0 is binary input and the rest of the rows are ECA output,
     * with wrapped columns so that it's cylindrical with the rows parallel to the circumfrence and
     * columns perpendicular. The basic unit of the hash algorithm
     * @param in input neighborhood in array form
     * @param rule ECA 0-255 rule
     * @return the square of the input and its ECA output
     */
    public int[][] generateWrappedECAsquare(int[] in, int rule) {
        int[][] out = new int[in.length][in.length];
        for (int row = 0; row < in.length; row++) {
            out[0][row] = in[row];
        }
        int row;
        int column;
        int a;
        int b;
        int c;
        int size = in.length;
        for (row = 1; row < size; row++) {
            for (column = 0; column < size; column++) {
                a = ((column - 1) + size) % size;
                b = column;
                c = ((column + 1)) % size;
                out[row][column] = out[row - 1][a] + 2 * out[row - 1][b] + 4 * out[row - 1][c];
                out[row][column] = ((rule / (int) Math.pow(2, out[row][column]) % 2));
                //localMaxSolution[row][column] = localMaxSolution[row-1][a]+2*trialField[row-1][b]+4*trialField[row-1][c];
                //localMaxSolution[row][column] = wolfram[localMaxSolution[row][column]];
            }
        }
        return out;
    }
    /**
     * Outputs the square array, where row 0 is binary input and the rest of the rows are ECA output,
     * with wrapped columns so that it's cylindrical with the rows parallel to the circumfrence and
     * columns perpendicular. The basic unit of the hash algorithm
     * @param inInt input neighborhood in integer form
     * @param rule ECA 0-255 rule
     * @return the square of the input and its ECA output
     */
    public int[][] generateWrappedECAsquare(int inInt, int rule) {
        int[] in = new int[4];
        for (int row = 0; row < in.length; row++) {
            in[row] = ((inInt) / (int) Math.pow(2, row) % 2);
        }
        int[][] out = new int[in.length][in.length];
        for (int row = 0; row < in.length; row++) {
            out[0][row] = in[row];
        }
        int row;
        int column;
        int a;
        int b;
        int c;
        int size = in.length;
        for (row = 1; row < size; row++) {
            for (column = 0; column < size; column++) {
                a = ((column - 1) + size) % size;
                b = column;
                c = ((column + 1)) % size;
                out[row][column] = out[row - 1][a] + 2 * out[row - 1][b] + 4 * out[row - 1][c];
                out[row][column] = ((rule / (int) Math.pow(2, out[row][column]) % 2));
                //localMaxSolution[row][column] = localMaxSolution[row-1][a]+2*trialField[row-1][b]+4*trialField[row-1][c];
                //localMaxSolution[row][column] = wolfram[localMaxSolution[row][column]];
            }
        }
        return out;
    }

    /**
     * Exhaustively generates all 0-255 ECA rule hash algorithm truth tables and minMax codeword sets;
     * then does some basic analysis on aggregate properties
     * @param size side length of the square you want to operate on
     * @param doChangeScore if this is set, it calls a function that randomly changes the address and sees how much the codeword changes, significantly affects runtime,
     *                      has not been tested in a while
     * @param changeScoreTrials if doChangeScore is true this is how may random attempts doChangeScore() makes on a given codeword, significantly affects runtime
     * @param doRandom size 4 is manageable computationally, larger than that you would want to set this true
     * @param numTrials if doRandom is set, this is how many random attempts it makes at analyzing a given rule
     * @param doVoting does voting on reconstruction, has not been tested in a while
     */

    public void doAllRules(int size, boolean doChangeScore, int changeScoreTrials, boolean doRandom, int numTrials, boolean doVoting) {
        sameErrorMin = new int[256][(int) Math.pow(2, size)];
        sameErrorMax = new int[256][(int) Math.pow(2, size)];
        minNumberOfSameSolutions = new int[256];
        maxNumberOfSameSolutions = new int[256];
        localMinSolution = new int[size][size];
        localMaxSolution = new int[size][size];
        minSolutionDistro = new int[256][(int) Math.pow(2, size)];
        maxSolutionDistro = new int[256][(int) Math.pow(2, size)];
        minErrorMap = new int[256][size][size];
        maxErrorMap = new int[256][size][size];
        minErrorBuckets = new int[256];
        maxErrorBuckets = new int[256];
        numberBoards = new int[256];
        minSolutionsAsWolfram = new int[256][(int) Math.pow(2, size * size)];
        maxSolutionsAsWolfram = new int[256][(int) Math.pow(2, size * size)];
        for (int rule = 0; rule < 256; rule++) {
            individualRule(rule, size, doChangeScore, changeScoreTrials, doRandom, numTrials, doVoting);
        }
        minSorted = new int[256];
        maxSorted = new int[256];
        for (int row = 0; row < 256; row++) {
            minSorted[row] = row;
            maxSorted[row] = row;
        }
        for (int row = 0; row < 256; row++) {
            for (int column = 0; column < 256; column++) {
                if (minErrorBuckets[minSorted[row]] > minErrorBuckets[minSorted[column]]) {
                    int temp = minSorted[row];
                    minSorted[row] = minSorted[column];
                    minSorted[column] = temp;
                }
            }
        }
        for (int row = 0; row < 256; row++) {
            for (int column = 0; column < 256; column++) {
                if (maxErrorBuckets[maxSorted[row]] < maxErrorBuckets[maxSorted[column]]) {
                    int temp = maxSorted[row];
                    maxSorted[row] = maxSorted[column];
                    maxSorted[column] = temp;
                }
            }
        }
        minErrorPerArray = new double[256];
        maxErrorPerArray = new double[256];
        minErrorPerBit = new double[256];
        maxErrorPerBit = new double[256];
        double numArrays = Math.pow(2, size * size);
        if (doRandom) numArrays = numTrials;
        double numBits = numArrays * size * size;
        for (int row = 0; row < 256; row++) {
            minErrorPerArray[row] = minErrorBuckets[row] / numArrays;
            maxErrorPerArray[row] = maxErrorBuckets[row] / numArrays;
            minErrorPerBit[row] = minErrorBuckets[row] / numBits;
            maxErrorPerBit[row] = maxErrorBuckets[row] / numBits;
        }
        System.out.println("minErrorPerArray, sorted");
        System.out.println(Arrays.toString(minSorted));
        System.out.println("maxErrorPerArray, sorted");
        System.out.println(Arrays.toString(maxSorted));
        for (int n = 0; n < 256; n++) {
            System.out.println("n: " + n + " " + minNumberOfSameSolutions[n] + " " + maxNumberOfSameSolutions[n] + " " + Arrays.toString(minSolutionDistro[n]) + " " + Arrays.toString(maxSolutionDistro[n]));
        }
        System.out.println();
        for (int n = 0; n < 256; n++) {
            if (minNumberOfSameSolutions[n] == 1 || maxNumberOfSameSolutions[n] == 1) {
                System.out.println("n: " + n + " " + minNumberOfSameSolutions[n] + " " + maxNumberOfSameSolutions[n] + " " + Arrays.toString(minSolutionDistro[n]) + " " + Arrays.toString(maxSolutionDistro[n]));
                System.out.println(minErrorPerArray[n] + " " + maxErrorPerArray[n]);
                System.out.println();
            }
        }
        //Sort
        //All of these min/max
        //
        //Error rate
        //heat map
        //solution distro
        //Pi/Phi stuff
    }

    /**
     * Displays some basic information about an ECA rule's hash performance statistics
     * @param specificRule 0-255 ECA rule
     * @param doChangeScore whether to check the codeword change / input change
     * @param changeScoreTrials how many random samples to take if doChangeScore is true
     * @param size size of the hash tile
     */
    public void individualRuleDisplay(int specificRule, boolean doChangeScore, int changeScoreTrials, int size) {
        //All of these min/max
        //
        //Error rate
        //heat map
        //solution distro
        //Pi/Phi stuff
        System.out.println("Specific: " + specificRule);
        System.out.println("numberBoards: " + numberBoards[specificRule]);
        System.out.println("Min errors: " + minErrorBuckets[specificRule] + " " + String.format("%.4f", (double) minErrorBuckets[specificRule] / (double) numberBoards[specificRule]));
        System.out.println("Max errors: " + maxErrorBuckets[specificRule] + " " + String.format("%.4f", (double) maxErrorBuckets[specificRule] / (double) numberBoards[specificRule]));
        if (doChangeScore) {
            System.out.println("Change score: " + changeScoreTrials);
        }
        System.out.println();
        customArray.plusArrayDisplay(minErrorMap[specificRule], false, false, "minErrorMap");
        customArray.plusArrayDisplay(maxErrorMap[specificRule], false, false, "maxErrorMap");
        System.out.println("minSolutionDistro[] " + Arrays.toString(minSolutionDistro[specificRule]));
        System.out.println("maxSolutionDistro[] " + Arrays.toString(maxSolutionDistro[specificRule]));
        System.out.println("sameErrorMin[] " + Arrays.toString(sameErrorMin[specificRule]) + " " + String.format("%.4f", (double) minNumberOfSameSolutions[specificRule] / (double) numberBoards[specificRule]));
        System.out.println("sameErrorMax[] " + Arrays.toString(sameErrorMax[specificRule]) + " " + String.format("%.4f", (double) maxNumberOfSameSolutions[specificRule] / (double) numberBoards[specificRule]));
        double[] minRowTots = new double[size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                minRowTots[row] += (double) minErrorMap[specificRule][row][column];
            }
        }
        double[][] minProportions = new double[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                minProportions[row][column] = minRowTots[row] / minRowTots[column];
            }
        }
        System.out.println("minProportions[][]");
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f", minProportions[row][column]) + " ");
            }
            System.out.print("\n");
        }
        System.out.println((minRowTots[0] + minRowTots[1]) / (minRowTots[2] + minRowTots[3]));
        double firstTwoOverSecondTwo = (minRowTots[0] + minRowTots[1]) / (minRowTots[2] + minRowTots[3]);
        double[] maxRowTots = new double[size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                maxRowTots[row] += (double) maxErrorMap[specificRule][row][column];
            }
        }
        double[][] maxProportions = new double[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                maxProportions[row][column] = maxRowTots[row] / maxRowTots[column];
            }
        }
        System.out.println("\nmaxProportions[][]");
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f", maxProportions[row][column]) + " ");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
        System.out.println("numberOfSameMinimum " + minNumberOfSameSolutions[specificRule]);
        System.out.println("numberOfSameMaximum " + maxNumberOfSameSolutions[specificRule]);
        StringPrint s = new StringPrint();
        double PiOverThree = Math.PI / 3;
        double PhiSquared = (Math.sqrt(5) + 1) / 2;
        PhiSquared *= PhiSquared;
        double[] relevantProportions = new double[]{minProportions[2][3], minProportions[1][0], firstTwoOverSecondTwo};
        int[] numberPlaces = new int[3];
        for (int power = 1; power < 12; power++) {
            int in = (int) ((minProportions[2][3] / Math.pow(2, -power)) % 2);
            int comp = (int) ((Math.PI / Math.pow(2, -power)) % 2);
            if (in != comp) {
                numberPlaces[0] = power;
                break;
            }
        }
        for (int power = 1; power < 12; power++) {
            int in = (int) ((minProportions[1][0] / Math.pow(2, -power)) % 2);
            int comp = (int) (((Math.PI / 3.0) / Math.pow(2, -power)) % 2);
            if (in != comp) {
                numberPlaces[1] = power;
                break;
            }
        }
        for (int power = 1; power < 12; power++) {
            int in = (int) ((firstTwoOverSecondTwo / Math.pow(2, -power)) % 2);
            int comp = (int) ((PhiSquared / Math.pow(2, -power)) % 2);
            if (in != comp) {
                numberPlaces[2] = power;
                break;
            }
        }
        s.println("the following only apply to rule 150, i don't know about elsewise");
        s.println();
        s.println("a = row2 / row 3 = " + minProportions[2][3]);
        s.println("a = (row2 / row 3) - PI = " + (minProportions[2][3] - Math.PI));
        s.println("accurate to the binary 2^-" + numberPlaces[0] + " place");
        double a = minProportions[2][3] - Math.PI;
        s.println();
        s.println("b = row1 / row 0 = " + minProportions[1][0]);
        s.println("b = (row1 / row 1) - (PI/3) = " + (minProportions[1][0] - Math.PI / 3));
        s.println("accurate to the binary 2^-" + numberPlaces[1] + " place");
        double b = minProportions[1][1] - Math.PI / 3;
        s.println();
        s.println("c = (row0+row1)/(row2+row3) = " + firstTwoOverSecondTwo);
        s.println("c = (row0+row1)/(row2+row3) - PhiSquared = " + (firstTwoOverSecondTwo - PhiSquared));
        s.println("accurate to the binary 2^-" + numberPlaces[2] + " place");
        double c = firstTwoOverSecondTwo - PhiSquared;
        s.println();
        s.println("As well as the 1's and 2's place makes for 7, 11, and 10 accurate digits");
    }

    /**
     * Displays some nifty information about rule 150's 4x4 hash error heat map
     * @param specificRule only written for rule 150
     * @param doChangeScore n/a, untested with True
     * @param changeScoreTrials n/a, untested with other than 0
     * @param size n/a, only written for size 4
     */
    public void oneFiftyDisplay(int specificRule, boolean doChangeScore, int changeScoreTrials, int size) {
        //All of these min/max
        //
        //Error rate
        //heat map
        //solution distro
        //Pi/Phi stuff
        System.out.println("Specific: " + specificRule);
        //System.out.println("numberBoards: " + numberBoards[specificRule]);
        //System.out.println("Min errors: " + minErrorBuckets[specificRule] + " " + String.format("%.4f", (double)minErrorBuckets[specificRule]/(double)numberBoards[specificRule]));
        //System.out.println("Max errors: " + maxErrorBuckets[specificRule] + " " + String.format("%.4f",(double)maxErrorBuckets[specificRule]/(double)numberBoards[specificRule]));
        if (doChangeScore) {
            System.out.println("Change score: " + changeScoreTrials);
        }
        System.out.println();
        customArray.plusArrayDisplay(minErrorMap[specificRule], false, false, "minErrorMap");
//        customArray.plusArrayDisplay(maxErrorMap[specificRule], false, false, "maxErrorMap");
//        System.out.println("minSolutionDistro[] " + Arrays.toString(minSolutionDistro));
//        System.out.println("maxSolutionDistro[] " + Arrays.toString(maxSolutionDistro));
//        System.out.println("sameErrorMin[] " + Arrays.toString(sameErrorMin[specificRule]) + " " + String.format("%.4f",(double)minNumberOfSameSolutions[specificRule]/(double)numberBoards[specificRule]));
//        System.out.println("sameErrorMax[] " + Arrays.toString(sameErrorMax[specificRule]) + " " + String.format("%.4f",(double)maxNumberOfSameSolutions[specificRule]/(double)numberBoards[specificRule]));
        double[] minRowTots = new double[size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                minRowTots[row] += (double) minErrorMap[specificRule][row][column];
            }
        }
        double[][] minProportions = new double[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                minProportions[row][column] = minRowTots[row] / minRowTots[column];
            }
        }
        System.out.println("minProportions[][]");
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f", minProportions[row][column]) + " ");
            }
            System.out.print("\n");
        }
        System.out.println((minRowTots[0] + minRowTots[1]) / (minRowTots[2] + minRowTots[3]));
        double firstTwoOverSecondTwo = (minRowTots[0] + minRowTots[1]) / (minRowTots[2] + minRowTots[3]);
        double[] maxRowTots = new double[size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                maxRowTots[row] += (double) maxErrorMap[specificRule][row][column];
            }
        }
        double[][] maxProportions = new double[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                maxProportions[row][column] = maxRowTots[row] / maxRowTots[column];
            }
        }
//        System.out.println("\nmaxProportions[][]");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", maxProportions[row][column]) + " ");
//
//            }
//            System.out.print("\n");
//        }
//        System.out.print("\n");
//        System.out.println("numberOfSameMinimum " + minNumberOfSameSolutions[specificRule]);
//        System.out.println("numberOfSameMaximum " + maxNumberOfSameSolutions[specificRule]);
        StringPrint s = new StringPrint();
        double PiOverThree = Math.PI / 3;
        double PhiSquared = (Math.sqrt(5) + 1) / 2;
        PhiSquared *= PhiSquared;
        double[] relevantProportions = new double[]{minProportions[2][3], minProportions[1][0], firstTwoOverSecondTwo};
        int[] numberPlaces = new int[3];
        for (int power = 1; power < 12; power++) {
            int in = (int) ((minProportions[2][3] / Math.pow(2, -power)) % 2);
            int comp = (int) ((Math.PI / Math.pow(2, -power)) % 2);
            if (in != comp) {
                numberPlaces[0] = power;
                break;
            }
        }
        for (int power = 1; power < 12; power++) {
            int in = (int) ((minProportions[1][0] / Math.pow(2, -power)) % 2);
            int comp = (int) (((Math.PI / 3.0) / Math.pow(2, -power)) % 2);
            if (in != comp) {
                numberPlaces[1] = power;
                break;
            }
        }
        for (int power = 1; power < 12; power++) {
            int in = (int) ((firstTwoOverSecondTwo / Math.pow(2, -power)) % 2);
            int comp = (int) ((PhiSquared / Math.pow(2, -power)) % 2);
            if (in != comp) {
                numberPlaces[2] = power;
                break;
            }
        }
        s.println();
        s.println("a = row2 / row 3 = " + minProportions[2][3]);
        s.println("a = (row2 / row 3) - PI = " + (minProportions[2][3] - Math.PI));
        s.println("accurate to the binary 2^-" + numberPlaces[0] + " place");
        double a = minProportions[2][3] - Math.PI;
        s.println();
        s.println("b = row1 / row 0 = " + minProportions[1][0]);
        s.println("b = (row1 / row 1) - (PI/3) = " + (minProportions[1][0] - Math.PI / 3));
        s.println("accurate to the binary 2^-" + numberPlaces[1] + " place");
        double b = minProportions[1][1] - Math.PI / 3;
        s.println();
        s.println("c = (row0+row1)/(row2+row3) = " + firstTwoOverSecondTwo);
        s.println("c = (row0+row1)/(row2+row3) - PhiSquared = " + (firstTwoOverSecondTwo - PhiSquared));
        s.println("accurate to the binary 2^-" + numberPlaces[2] + " place");
        double c = firstTwoOverSecondTwo - PhiSquared;
        s.println();
        s.println("As well as the 1's and 2's place makes for 7, 11, and 10 accurate digits");
    }

    /**
     * Outputs basic data about an individual rule
     * @param size side length of the square you want to operate on
     * @param doChangeScore if this is set, it calls a function that randomly changes the address and sees how much the codeword changes, significantly affects runtime,
     *                      has not been tested in a while
     * @param changeScoreTrials if doChangeScore is true this is how may random attempts doChangeScore() makes on a given codeword, significantly affects runtime
     * @param doRandom size 4 is manageable computationally, larger than that you would want to set this true
     * @param numTrials if doRandom is set, this is how many random attempts it makes at analyzing a given rule
     * @param doVoting does voting on reconstruction, has not been tested in a while
     */
    public void individualRuleManager(int rule, int size, boolean doChangeScore, int changeScoreTrials, boolean doRandom, int numTrials, boolean doVoting) {
        sameErrorMin = new int[256][(int) Math.pow(2, size)];
        sameErrorMax = new int[256][(int) Math.pow(2, size)];
        minNumberOfSameSolutions = new int[256];
        maxNumberOfSameSolutions = new int[256];
        localMinSolution = new int[size][size];
        localMaxSolution = new int[size][size];
        minSolutionDistro = new int[256][(int) Math.pow(2, size)];
        maxSolutionDistro = new int[256][(int) Math.pow(2, size)];
        minErrorMap = new int[256][size][size];
        maxErrorMap = new int[256][size][size];
        minErrorBuckets = new int[256];
        maxErrorBuckets = new int[256];
        numberBoards = new int[256];
        minSolutionsAsWolfram = new int[256][(int) Math.pow(2, size * size)];
        maxSolutionsAsWolfram = new int[256][(int) Math.pow(2, size * size)];
        individualRule(rule, size, doChangeScore, changeScoreTrials, doRandom, numTrials, doVoting);
        individualRuleDisplay(rule, doChangeScore, changeScoreTrials, size);
        //System.out.println(Arrays.toString(basicECA.ecaWolframCodes[rule]));
    }

    /**
     * Generates the entire truth table for a given size of a particular ECA rule
     * @param rule 0-255 ECA rule
     * @param size side length of the square you want to operate on
     * @param doChangeScore if this is set, it calls a function that randomly changes the address and sees how much the codeword changes, significantly affects runtime,
     *                      has not been tested in a while
     * @param changeScoreTrials if doChangeScore is true this is how may random attempts doChangeScore() makes on a given codeword, significantly affects runtime
     * @param doRandom size 4 is manageable computationally, larger than that you would want to set this true
     * @param numTrials if doRandom is set, this is how many random attempts it makes at analyzing a given rule
     * @param doVoting does voting on reconstruction, has not been tested in a while
     */
    public void individualRule(int rule, int size, boolean doChangeScore, int changeScoreTrials, boolean doRandom, int numTrials, boolean doVoting) {
        int trial;
        int row;
        int column;
        int numBoards = (int) Math.pow(2, size * size);
        if (doRandom) numBoards = numTrials;
        int numVotes = 1;
        if (doVoting) numVotes = 8;
        field = new int[size][size];
        int[][] temp = new int[size][size];
        System.out.println("rule " + rule);
        for (trial = 0; trial < numBoards; trial++) {
            //if (trial % 1000 == 0) System.out.println("trial " + trial);
            if (!doRandom) {
                for (row = 0; row < size; row++) {
                    for (column = 0; column < size; column++) {
                        field[row][column] = ((trial / (int) Math.pow(2, size * column + row)) % 2);
                    }
                }
            } else {
                for (row = 0; row < size; row++) {
                    for (column = 0; column < size; column++) {
                        field[row][column] = rand.nextInt(0, 2);
                    }
                }
            }
            //CustomLibrary.CustomArray.plusArrayDisplay(field, false, true, "Field");
            minVote = new int[size][size];
            maxVote = new int[size][size];
            //This loop reflects, rotates, and transposes the input data array
            for (int rotation = 0; rotation < numVotes; rotation++) {
                temp = new int[size][size];
                for (row = 0; row < size; row++) {
                    for (column = 0; column < size; column++) {
                        temp[row][column] = field[row][column];
                    }
                }
                temp = customArray.reflectRotateTranspose(temp, rotation);
                temp = findMinimizingCodeword(rule, temp, basicECA.ecaWolframCodes[rule]);
                minTemp = customArray.reflectRotateTranspose(localMinSolution, rotation);
                maxTemp = customArray.reflectRotateTranspose(localMaxSolution, rotation);
                for (row = 0; row < size; row++) {
                    for (column = 0; column < size; column++) {
                        minVote[row][column] += minTemp[row][column];
                        maxVote[row][column] += maxTemp[row][column];
                    }
                }
            }
            totMinErrors = 0;
            totMaxErrors = 0;
            for (row = 0; row < size && doVoting; row++) {
                for (column = 0; column < size; column++) {
                    if (minVote[row][column] > 3) {
                        minVote[row][column] = 1;
                    } else {
                        minVote[row][column] = 0;
                    }
                    totMinErrors += (minVote[row][column] ^ field[row][column]);
                    minErrorMap[rule][row][column] += (minVote[row][column] ^ field[row][column]);
                    if (maxVote[row][column] > 3) maxVote[row][column] = 1;
                    else maxVote[row][column] = 0;
                    totMaxErrors += (((maxVote[row][column] ^ field[row][column])));
                    maxErrorMap[rule][row][column] += (((maxVote[row][column] ^ field[row][column])));
                }
            }
            for (row = 0; row < size && !doVoting; row++) {
                for (column = 0; column < size; column++) {
                    totMinErrors += (minVote[row][column] ^ field[row][column]);
                    minErrorMap[rule][row][column] += (minVote[row][column] ^ field[row][column]);
                    totMaxErrors += (((maxVote[row][column] ^ field[row][column])));
                    maxErrorMap[rule][row][column] += (((maxVote[row][column] ^ field[row][column])));
                }
            }
            for (column = 0; column < size && !doRandom; column++) {
                minSolutionsAsWolfram[rule][trial] += (int) Math.pow(2, column) * minVote[0][column];
                maxSolutionsAsWolfram[rule][trial] += (int) Math.pow(2, column) * maxVote[0][column];
            }
            minErrorBuckets[rule] += totMinErrors;
            maxErrorBuckets[rule] += totMaxErrors;
            numberBoards[rule]++;
            //
            //
            //Severely impacts runtime
            //Finds the mean distance between codewords per change in the random input data
            //the changeScore is scored with the same exponent as the errorScore
            if (doChangeScore) {
                changeChange[rule] = changeChange(rule, minVote, totMinErrors, changeScoreTrials);
                errorChange[rule] += totMinErrors;
                hammingChange[rule] += totHammingChange;
                changeScore[rule] += weightedChangeScore;
            }
        }
        if (doChangeScore) changeChange[rule] = (double) hammingChange[rule] / (double) changeScore[rule];
    }

    /**
     * Checks the mean Hamming distance between minimized codewords when the input array is changed
     *
     * @param rule      ECA rule 0-255
     * @param in        random input data array
     * @param errorIn   errorScore of the calling function
     * @param numTrials number of trials to do for the mean (severely impacts runtime)
     * @return the mean Hamming distance between codewords per changeScore
     */
    public double changeChange(int rule, int[][] in, int errorIn, int numTrials) {
        //
        //
        //Initialization
        int size = in.length;
        Random rand = new Random();
        double out = 0;
        totLocalErrors = 0;
        totHammingChange = 0;
        weightedChangeScore = 0;
        int[][] changed = new int[size][size];
        int row;
        int column;
        int change;
        int[][] fitted;
        int changes;
        int power;
        int randRow;
        int randColumn;
        //Main loop
        for (int trial = 0; trial < numTrials; trial++) {
            //Initializes and does the changes on the input data
            for (row = 0; row < size; row++) {
                for (column = 0; column < size; column++) {
                    changed[row][column] = in[row][column];
                }
            }
            int numChanges = rand.nextInt(0, 2 * size);
            for (change = 0; change < numChanges; change++) {
                randRow = rand.nextInt(0, size);
                randColumn = rand.nextInt(0, size);
                changed[randRow][randColumn] ^= 1;
                weightedChangeScore += (int) Math.pow(2, randRow);
            }
            //Finds the new minimum neighborhood
            fitted = findMinimizingCodeword(rule, changed, basicECA.ecaWolframCodes[rule]);
            //Scores the new neighborhood
            changes = 0;
            for (power = 0; power < size; power++) {
                changes += (in[0][power] ^ fitted[0][power]);
            }
            int errorScore = 0;
            for (row = 0; row < size; row++) {
                for (column = 0; column < size; column++) {
                    errorScore += ((int) Math.pow(2, row) * (fitted[row][column] ^ in[row][column]));
                }
            }
            totHammingChange += changes;
            totLocalErrors += errorScore;
        }
        return (double) totHammingChange / (double) totLocalErrors;
    }
}
