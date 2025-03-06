import java.util.Arrays;
import java.util.Random;

public class minErrorStaging {
    /**
     *
     */
    int[] sameSolutions;
    /**
     *
     */
    int[][] localMaxSolution;
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
     * Hamming change in codewords
     */
    int totHammingChange;
    /**
     * Total number of errors in all solutions found
     */
    int totLocalErrors;
    /**
     * Minimum solutions stored as a Wolfram code, with the address being the integer value of the binary
     */
    int[][] minSolutionsAsWolfram;
    int[][] maxSolutionsAsWolfram;
    /**
     * Cumulative distribution of codeword solutions over all inputs
     */
    int[] minSolutionDistro;
    /**
     * Cumulative distribution of codeword solutions over all inputs, maximized error codewords rather than minimized error codewords
     */
    int[] maxSolutionDistro;
    int[][] field;
    int[] minErrorBuckets;
    int[] maxErrorBuckets;
    int[][][] solutions;
    int[] numberBoards;
    int[] errorChange;
    int[] hammingChange;
    double[] averageChange;
    int[] solutionBucket;
    double[] changeChange;
    int[] changeScore;
    int[][] errorMap;
    int start;
    int stop;
    int totMaxErrors = 0;
    int totMinErrors = 0;
    int[][] minVote;
    int[][] maxVote;
    int[][] maxTemp;
    int[][] minTemp;
    double[] minErrorPerArray;
    double[] maxErrorPerArray;
    double[] minErrorPerBit;
    double[] maxErrorPerBit;
    int[] minSorted;
    int[] maxSorted;
    int[] minNumberOfSameSolutions;
    int[] maxNumberOfSameSolutions;

    BasicECA basicECA = new BasicECA();
    CustomArray customArray = new CustomArray();
    Random rand = new Random();

    /**
     * Finds the minimum-discrepancy initial neighborhood output of the ECA n vs the input
     *
     * @param n  ECA rule 0-255
     * @param in binary input array
     * @return the array with ECA n and initial neighborhood mimimum output
     */
    public int[][] checkPascalErrorCorrectionAll(int n, int[][] in, int[] wolfram, int integerWolfram) {
        sameSolutions = new int[256];
        int size = in.length;
        localMaxSolution = new int[size][size];
        int[][] trialField = new int[size][size];
        int[] errorScore = new int[sameErrorMin.length];
        //Declaring these here instead of inline in the loops significantly speeds it up
        int row = 0;
        int column = 0;
        int a = 0;
        int b = 0;
        int c = 0;
        int maxNeighborhood = (int) Math.pow(2, size);
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
                    //errorScore[correction] +=  ((row*row))*(trialField[row][column] ^ in[row][column]);
                    //errorScore[correction] += row*row*(trialField[row][column] ^ in[row][column]);
                    //errorScore[correction] += column*column *(in[row][column] ^  trialField[row][column]);
                    //errorScore[correction] += column* (in[row][column] ^  trialField[row][column]);
                    //errorScore[correction] += row* (in[row][column] ^  trialField[row][column]);
                    //errorScore[correction] += (in[row][column] ^ trialField[row][column]);
                    //errorScore[correction] += (int)Math.pow(2, column)*(in[row][column] ^ trialField[row][column]);
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
                maxSpot = neighborhood;
            }
        }
        int minErrors = Integer.MAX_VALUE;
        int minSpot = 0;
        for (int neighborhood = 0; neighborhood < maxNeighborhood; neighborhood++) {
            if (errorScore[neighborhood] < minErrors) {
                minErrors = errorScore[neighborhood];
                minSpot = neighborhood;
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
        sameMinimums = Arrays.copyOfRange(sameMinimums, 0, minIndex);
        sameMaximums = Arrays.copyOfRange(sameMaximums, 0, maxIndex);
        //sameErrorMin[integerWolfram][numberOfSameMinimum]++;
        //sameErrorMax[integerWolfram][numberOfSameMaximum]++;
        minNumberOfSameSolutions[integerWolfram] = numSameMinimum;
        maxNumberOfSameSolutions[integerWolfram] = numSameMaximum;
        if (numSameMinimum > sameSolutions[n]) {
            sameSolutions[n] = numSameMinimum;
        }
        //System.out.println("sameNumErrors = " + sameNumErrors);
        //System.out.println("sameNumErrorsMax = " + sameNumMax);
        //System.out.println("sameNumErrors " + sameNumErrors);
        //
        //
        //Run the Wolfram code on the minimum neighborhood codeword for function return purposes
        trialField = new int[size + 1][size];
        for (column = 0; column < size; column++) {
            trialField[0][column] = ((minSpot / (int) Math.pow(2, column)) % 2);
            localMaxSolution[0][column] = ((maxSpot / (int) Math.pow(2, column)) % 2);
            localMinSolution[0][column] = ((minSpot / (int) Math.pow(2, column)) % 2);
        }
        minSolutionDistro[minIndex]++;
        maxSolutionDistro[maxIndex]++;
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
        return trialField;
    }

    public void doAllRules(int size, boolean doChangeScore, int changeScoreTrials, boolean doRandom, int numTrials, boolean doVoting) {
        for (int rule = 0; rule < 256; rule++) {
            individualRuleMinimizer(rule, size, doChangeScore, changeScoreTrials, doRandom, numTrials, doVoting);
        }
        minSorted = new int[256];
        maxSorted = new int[256];
        for (int row = 0; row < 256; row++) {
            minSorted[row] = row;
            maxSorted[row] = row;
        }
        for (int row = 0; row < 256; row++) {
            for (int column = 0; column < 256; column++) {
                if (minErrorBuckets[minSorted[row]] < minErrorBuckets[minSorted[column]]) {
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
        double numArrays = Math.pow(2,size*size);
        if (doRandom) numArrays = numTrials;
        double numBits = numArrays * size * size;
        for (int row = 0; row < 256; row++) {
            minErrorPerArray[row] = minErrorBuckets[row]/numArrays;
            maxErrorPerArray[row] = maxErrorBuckets[row]/numArrays;
            minErrorPerBit[row] = minErrorBuckets[row]/numBits;
            maxErrorPerBit[row] = maxErrorBuckets[row]/numBits;
        }
        //Sort
        //All of these min/max
        //
        //Error rate
        //heat map
        //solution distro
        //Pi/Phi stuff
    }

    public void individualRuleDisplay(int specificRule, boolean doChangeScore, int changeScoreTrials, int size) {
        //All of these min/max
        //
        //Error rate
        //heat map
        //solution distro
        //Pi/Phi stuff
        System.out.println("Rule specific: " + specificRule);
        System.out.println("Min errors: " + minErrorBuckets[specificRule]);
        System.out.println("Max errors: " + maxErrorBuckets[specificRule]);
        if (doChangeScore) {
            System.out.println("Change score: " + changeScoreTrials);
        }
        customArray.plusArrayDisplay(minErrorMap[specificRule], false, false, "minErrorMap");
        customArray.plusArrayDisplay(maxErrorMap[specificRule], false, false, "maxErrorMap");
        System.out.println("minSolutionDistro[] " + Arrays.toString(minSolutionDistro));
        System.out.println("maxSolutionDistro[] " + Arrays.toString(maxSolutionDistro));
        System.out.println("sameErrorMin[] " + Arrays.toString(sameErrorMin[specificRule]));
        System.out.println("sameErrorMax[] " + Arrays.toString(sameErrorMax[specificRule]));
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
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f", minProportions[row][column]) + " ");
            }
        }
        double[] maxRowTots = new double[size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                maxRowTots[row] += (double) minErrorMap[specificRule][row][column];
            }
        }
        double[][] maxProportions = new double[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                maxProportions[row][column] = maxRowTots[row] / maxRowTots[column];
            }
        }
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f", maxProportions[row][column]) + " ");
            }
        }
//        //The rest is results management & display
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //Sorts and averages the best performing ECA rules
//        System.out.println("solutionBucket[] " + solutionBucket);
//        System.out.println("pre division");
//        System.out.println(Arrays.toString(minErrorBuckets));
//        System.out.println("hbLength[]");
//        System.out.println(Arrays.toString(numberBoards));
//        int[] hashbucketRate = new int[256];
//        hashbucketRate[row] = minErrorBuckets[row] / numberBoards[row];
//        if (doChangeScore) averageChange[row] = (double) hammingChange[row] / (double) errorChange[row];
//        System.out.println("post division");
//        System.out.println(Arrays.toString(hashbucketRate));
//        // System.out.println(Arrays.toString(hashbucket));
//        //
//        //
//        //Output
//        System.out.println("Here");
//        for (int index = start; index < stop; index++) {
//            System.out.println("index " + sorted[index] + " " + basicECA.ruleClasses[sorted[index]] + " " + hashbucketRate[index] + " " + minErrorBuckets[index] + " " + numberBoards[index] + " ");
//        }
//        System.out.println(Arrays.toString(sameSolutions));
//        for (row = start; row < stop; row++) {
//            if (sameSolutions[sorted[row]] == 1) {
//                System.out.println("Rule has a unique solution " + sorted[row]);
//            } else {
//                System.out.println("sameSolutions[" + sorted[row] + "] " + sameSolutions[sorted[row]]);
//            }
//        }
//        if (doChangeScore) {
//            System.out.println("hammingChange per changeScore");
//            for (row = start; row < stop; row++) {
//                System.out.println(sorted[row] + " " + changeChange[sorted[row]] + " " + errorChange[sorted[row]]);
//            }
//        }
//        System.out.println("min error/grid " + ((double) minErrorBuckets[specificRule] / (double) numBoards));
//        System.out.println("min error/bit " + ((double) minErrorBuckets[specificRule]) / numBoards / size / size);
//        System.out.println("min bits/error " + (double) (numBoards * size * size) / (double) minErrorBuckets[specificRule]);
//        System.out.println("sameError " + Arrays.toString(sameErrorMin));
//        System.out.println("sameErrorMax " + Arrays.toString(sameErrorMax));
//        System.out.println("max error/grid " + ((double) maxErrorBuckets[specificRule] / (double) numBoards));
//        System.out.println("max error/bit " + ((double) maxErrorBuckets[specificRule]) / numBoards / size / size);
//        System.out.println("max bits/error " + (double) (numBoards * size * size) / (double) maxErrorBuckets[specificRule]);
//        System.out.println("\n");
//        double max = ((double) maxErrorBuckets[specificRule]) / numBoards / size / size;
//        double min = ((double) minErrorBuckets[specificRule]) / numBoards / size / size;
//        double ebDiff = ((double) maxErrorBuckets[specificRule] / numBoards / size / size) - ((double) minErrorBuckets[specificRule] / numBoards / size / size);
//        double beDiff = ((double) (numTrials * size * size) / (double) maxErrorBuckets[specificRule]) - ((double) (numTrials * size * size) / (double) minErrorBuckets[specificRule]);
//        System.out.println("ebDiff " + ebDiff);
//        System.out.println("beDiff " + beDiff);
//        System.out.println("max * min = " + (max * min));
//        double[][] sameMin = new double[size][size];
//        double[][] sameMax = new double[size][size];
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                sameMin[row][column] = (double) sameErrorMin[row + 1] / (double) sameErrorMin[column + 1];
//                sameMax[row][column] = (double) sameErrorMax[2 * row + 2] / (double) sameErrorMax[2 * column + 2];
//            }
//        }
//        System.out.println("sameMaxRowComparisons ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", sameMax[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("sameMinRowComparisons ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", sameMin[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        for (row = 1; row < 4096 && row < sameErrorMin.length; row *= 2) {
//            System.out.print("row " + row + " " + sameErrorMin[row] + "\n");
//        }
//        int maxErrorTot = 0;
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                maxErrorTot += minErrorMap[row][column];
//            }
//        }
//        CustomArray.plusArrayDisplay(minErrorMap, false, false, "Min Error Map");
//        CustomArray.plusArrayDisplay(maxErrorMap, false, false, "Max Error Map");
//        double[] rowTots = new double[size];
//        double phi = (1 + Math.sqrt(5)) / 2;
//        int[] maxRowTots = new int[size];
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                rowTots[row] += minErrorMap[row][column];
//                maxRowTots[row] += maxErrorMap[row][column];
//            }
//        }
//        double[][] maxRowComparisons = new double[size][size];
//        double[][] minRowComparisons = new double[size][size];
//        double[][] maxMapRate = new double[size][size];
//        double[][] minMapRate = new double[size][size];
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                maxRowComparisons[row][column] = (double) maxRowTots[row] / (double) maxRowTots[column];
//                minRowComparisons[row][column] = (double) rowTots[row] / (double) rowTots[column];
//                maxMapRate[row][column] = (double) maxErrorMap[row][column] / (double) numBoards / 8.0;
//                minMapRate[row][column] = (double) minErrorMap[row][column] / (double) numBoards / 8.0;
//            }
//        }
//        System.out.println("maxRowComparisons ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", maxRowComparisons[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("maxMapRate[][] ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", maxMapRate[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("minRowComparisons ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", minRowComparisons[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("maxMapRate[][] ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", minMapRate[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("(1,0) - PI/3" + ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0));
//        System.out.println("(3,2) - PI " + ((double) rowTots[2] / (double) rowTots[3] - Math.PI));
//        double diff = ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0);
//        diff = Math.log(diff) / Math.log(2);
//        System.out.println("diff " + diff);
//        diff = ((double) rowTots[2] / (double) rowTots[3] - Math.PI);
//        diff = Math.log(diff) / Math.log(2);
//        System.out.println("diff " + diff);
//        int upper = 0;
//        int lower = 0;
//        for (row = 0; row < 2; row++) {
//            for (column = 0; column < size; column++) {
//                upper += minErrorMap[row][column];
//                lower += minErrorMap[row + 2][column];
//            }
//        }
//        double div = (double) upper / (double) lower;
//        System.out.println("upper/lower " + div + " " + 1.0 / div + " " + div * Math.PI + " " + Math.PI / div + " " + div / Math.PI);
//        System.out.println("upper/lower - phi*phi " + (div - phi * phi));
//        double[] diffs = new double[]{((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0), ((double) rowTots[2] / (double) rowTots[3] - Math.PI), (div - phi * phi)};
//        for (int d = 0; d < 3; d++) {
//            diffs[d] = Math.abs(diffs[d]);
//            System.out.println("diff " + diffs[d] + " " + (Math.log(diffs[d]) / Math.log(2)));
//        }
//        for (int power = 2; power > -20; power--) {
//            System.out.print((int) (Math.PI / Math.pow(2, power)) % 2 + " ");
//        }
//        System.out.println();
//        for (int power = 2; power > -20; power--) {
//            System.out.print((int) (diffs[1] / Math.pow(2, power)) % 2 + " ");
//        }
//        System.out.println();
//        for (int power = 2; power > -24; power--) {
//            int a = (int) ((diffs[1] / Math.pow(2, power)) % 2);
//            int b = (int) ((Math.PI / Math.pow(2, power)) % 2);
//            System.out.print((a ^ b) + " ");
//        }
//        System.out.println();
//        double[][] errors = new double[size][size];
//        double[] byRow = new double[size];
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                errors[row][column] = (double) minErrorMap[row][column] / (double) (Math.pow(2, size * size) * 8);
//                byRow[row] += errors[row][column];
//            }
//            byRow[row] /= 4;
//            byRow[row] *= (440 * 8);
//        }
//        System.out.println("errors ");
//        for (row = 0; row < size; row++) {
//            System.out.println(Arrays.toString(errors[row]));
//        }
//        System.out.println("byRow " + Arrays.toString(byRow));
//        //doWallisProduct(50);
//        //doTriangles(50);
//        System.out.println("solutionDistro: " + Arrays.toString(minSolutionDistro));
//        System.out.println("maxSolutionDistro: " + Arrays.toString(maxSolutionDistro));
//        System.out.println("\n\n\n\n\n\n\n");
//        for (int spot = 0; spot < 16; spot++) {
//            System.out.print((double) maxSolutionDistro[spot] / 65536.0 + " ");
//        }
//        System.out.println();
//        for (int spot = 0; spot < 16; spot++) {
//            System.out.print(65536.0 / (double) maxSolutionDistro[spot] + " ");
//        }
    }

    public void individualRuleMinimizer(int rule, int size, boolean doChangeScore, int changeScoreTrials, boolean doRandom, int numTrials, boolean doVoting) {
        int trial;
        int row;
        int column;
        int numBoards = (int) Math.pow(2, size * size);
        if (doRandom) numBoards = numTrials;
        int numVotes = 1;
        if (doVoting) numVotes = 8;
        System.out.println("rule " + rule);
        for (trial = 0; trial < numBoards; trial++) {
            if (trial % 1000 == 0) System.out.println("trial " + trial);
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
            //CustomArray.plusArrayDisplay(field, false, true, "Field");
            minVote = new int[size][size];
            maxVote = new int[size][size];
            //This loop reflects, rotates, and transposes the input data array
            for (int rotation = 0; rotation < numVotes; rotation++) {
                minTemp = new int[size][size];
                for (row = 0; row < size; row++) {
                    for (column = 0; column < size; column++) {
                        minTemp[row][column] = field[row][column];
                    }
                }
                minTemp = customArray.reflectRotateTranspose(minTemp, rotation);
                minTemp = checkPascalErrorCorrectionAll(rule, minTemp, basicECA.ecaWolframCodes[rule], rule);
                minTemp = customArray.reflectRotateTranspose(minTemp, rotation);
                maxTemp = localMaxSolution;
                for (row = 0; row < size; row++) {
                    for (column = 0; column < size; column++) {
                        minVote[row][column] += minTemp[row][column];
                        maxVote[row][column] += maxTemp[row][column];
                    }
                }
            }
            for (column = 0; column < size && !doRandom; column++) {
                minSolutionsAsWolfram[rule][trial] += (int) Math.pow(2, column) * minTemp[0][column];
                maxSolutionsAsWolfram[rule][trial] += (int) Math.pow(2, column) * maxTemp[0][column];
            }
            totMinErrors = 0;
            totMaxErrors = 0;
            for (row = 0; row < size; row++) {
                for (column = 0; column < size; column++) {
                    if (minVote[row][column] > 3) minVote[row][column] = 1;
                    else minVote[row][column] = 0;
                    totMinErrors += (minVote[row][column] ^ field[row][column]);
                    minErrorMap[rule][row][column] += (minVote[row][column] ^ field[row][column]);
                    if (maxVote[row][column] > 3) maxVote[row][column] = 1;
                    else maxVote[row][column] = 0;
                    totMaxErrors += (maxVote[row][column] ^ field[row][column]);
                    maxErrorMap[rule][row][column] += (maxVote[row][column] ^ field[row][column]);
                }
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
     * Manager function for checkPascalErrorCorrection()
     *
     * @param specificRule ECA 0-255 rule
     * @param size         width of input array, size of solution neighborhood
     * @return ???
     */
    public int[][] errorMinimizationManager(int specificRule, int size, boolean doChangeScore, int changeScoreTrials, boolean doAllRules, boolean doRandom, int numTrials, boolean doVoting) {
        //
        //
        //
        //
        //
        //
        //
        //
        //Initialization
        CustomArray customArray = new CustomArray();
        maxSolutionDistro = new int[(int) Math.pow(2, size)];
        minSolutionDistro = new int[(int) Math.pow(2, size)];
        int numBoards = (int) Math.pow(2, size * size);
        if (doRandom) numBoards = numTrials;
        //Turns solutions into a Wolfram code, with the address being the input array (trial)
        //Currently only for one ECA rule, and does not work with doRandom, and does not work for size > 4 because the Wolfram code becomes too long (4x4 array = 2^16 length)
        minSolutionsAsWolfram = new int[256][numBoards];
        minErrorMap = new int[256][size][size];
        maxErrorMap = new int[256][size][size];
        sameSolutions = new int[256];
        sameErrorMin = new int[256][(int) Math.pow(2, size) + 1];
        sameErrorMax = new int[256][sameErrorMin.length];
        field = new int[size][size];
        minErrorBuckets = new int[256];
        maxErrorBuckets = new int[256];
        solutions = new int[8][size][size];
        numberBoards = new int[256];
        errorChange = new int[256];
        hammingChange = new int[256];
        averageChange = new double[256];
        solutionBucket = new int[256];
        changeChange = new double[256];
        changeScore = new int[256];
        maxVote = new int[size][size];
        minVote = new int[size][size];
        maxTemp = new int[size][size];
        minTemp = new int[size][size];
        int start = 0;
        if (!doAllRules) start = specificRule;
        int stop = 255;
        if (!doAllRules) stop = specificRule + 1;
        int numVotes = 1;
        if (doVoting) numVotes = 8;
        Random rand = new Random();
        int trial;
        int row;
        int column;
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //Main loop
        for (int rule = start; rule < stop; rule++) {
            System.out.println("rule " + rule);
            for (trial = 0; trial < numBoards; trial++) {
                if (trial % 1000 == 0) System.out.println("trial " + trial);
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
                //CustomArray.plusArrayDisplay(field, false, true, "Field");
                minVote = new int[size][size];
                maxVote = new int[size][size];
                //This loop reflects, rotates, and transposes the input data array
                for (int rotation = 0; rotation < numVotes; rotation++) {
                    minTemp = new int[size][size];
                    for (row = 0; row < size; row++) {
                        for (column = 0; column < size; column++) {
                            minTemp[row][column] = field[row][column];
                        }
                    }
                    minTemp = customArray.reflectRotateTranspose(minTemp, rotation);
                    minTemp = checkPascalErrorCorrectionAll(rule, minTemp, basicECA.ecaWolframCodes[rule], rule);
                    minTemp = customArray.reflectRotateTranspose(minTemp, rotation);
                    maxTemp = localMaxSolution;
                    for (row = 0; row < size; row++) {
                        for (column = 0; column < size; column++) {
                            minVote[row][column] += minTemp[row][column];
                            maxVote[row][column] += maxTemp[row][column];
                        }
                    }
                }
                for (column = 0; column < size && !doRandom; column++) {
                    minSolutionsAsWolfram[rule][trial] += (int) Math.pow(2, column) * minTemp[0][column];
                    maxSolutionsAsWolfram[rule][trial] += (int) Math.pow(2, column) * maxTemp[0][column];
                }
                totMinErrors = 0;
                totMaxErrors = 0;
                for (row = 0; row < size; row++) {
                    for (column = 0; column < size; column++) {
                        if (minVote[row][column] > 3) minVote[row][column] = 1;
                        else minVote[row][column] = 0;
                        totMinErrors += (minVote[row][column] ^ field[row][column]);
                        minErrorMap[rule][row][column] += (minVote[row][column] ^ field[row][column]);
                        if (maxVote[row][column] > 3) maxVote[row][column] = 1;
                        else maxVote[row][column] = 0;
                        totMaxErrors += (maxVote[row][column] ^ field[row][column]);
                        maxErrorMap[rule][row][column] += (maxVote[row][column] ^ field[row][column]);
                    }
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
            //CustomArray.plusArrayDisplay(errorMap,false,false,"ECA rule " + rule + " error map");
        }
//        //The rest is results management & display
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //Sorts and averages the best performing ECA rules
//        System.out.println("solutionBucket[] " + solutionBucket);
//        System.out.println("pre division");
//        System.out.println(Arrays.toString(minErrorBuckets));
//        System.out.println("hbLength[]");
//        System.out.println(Arrays.toString(numberBoards));
//        int[] hashbucketRate = new int[256];
//        for (row = start; row < stop; row++) {
//            hashbucketRate[row] = minErrorBuckets[row] / numberBoards[row];
//            if (doChangeScore) averageChange[row] = (double) hammingChange[row] / (double) errorChange[row];
//        }
//        System.out.println("post division");
//        System.out.println(Arrays.toString(hashbucketRate));
//        int[] sorted = new int[256];
//        for (row = 0; row < 256; row++) {
//            sorted[row] = row;
//        }
//        if (doAllRules) {
//            for (row = 0; row < 256; row++) {
//                for (column = 0; column < 256; column++) {
//                    if (hashbucketRate[row] < hashbucketRate[column]) {
//                        int temp = minErrorBuckets[row];
//                        minErrorBuckets[row] = minErrorBuckets[column];
//                        minErrorBuckets[column] = temp;
//                        temp = sorted[row];
//                        sorted[row] = sorted[column];
//                        sorted[column] = temp;
//                        temp = numberBoards[row];
//                        numberBoards[row] = numberBoards[column];
//                        numberBoards[column] = temp;
//                        temp = hashbucketRate[row];
//                        hashbucketRate[row] = hashbucketRate[column];
//                        hashbucketRate[column] = temp;
//                    }
//                }
//            }
//        }
//        // System.out.println(Arrays.toString(hashbucket));
//        //
//        //
//        //Output
//        System.out.println("Here");
//        for (int index = start; index < stop; index++) {
//            System.out.println("index " + sorted[index] + " " + basicECA.ruleClasses[sorted[index]] + " " + hashbucketRate[index] + " " + minErrorBuckets[index] + " " + numberBoards[index] + " ");
//        }
//        System.out.println(Arrays.toString(sameSolutions));
//        for (row = start; row < stop; row++) {
//            if (sameSolutions[sorted[row]] == 1) {
//                System.out.println("Rule has a unique solution " + sorted[row]);
//            } else {
//                System.out.println("sameSolutions[" + sorted[row] + "] " + sameSolutions[sorted[row]]);
//            }
//        }
//        if (doChangeScore) {
//            System.out.println("hammingChange per changeScore");
//            for (row = start; row < stop; row++) {
//                System.out.println(sorted[row] + " " + changeChange[sorted[row]] + " " + errorChange[sorted[row]]);
//            }
//        }
//        System.out.println("min error/grid " + ((double) minErrorBuckets[specificRule] / (double) numBoards));
//        System.out.println("min error/bit " + ((double) minErrorBuckets[specificRule]) / numBoards / size / size);
//        System.out.println("min bits/error " + (double) (numBoards * size * size) / (double) minErrorBuckets[specificRule]);
//        System.out.println("sameError " + Arrays.toString(sameErrorMin));
//        System.out.println("sameErrorMax " + Arrays.toString(sameErrorMax));
//        System.out.println("max error/grid " + ((double) maxErrorBuckets[specificRule] / (double) numBoards));
//        System.out.println("max error/bit " + ((double) maxErrorBuckets[specificRule]) / numBoards / size / size);
//        System.out.println("max bits/error " + (double) (numBoards * size * size) / (double) maxErrorBuckets[specificRule]);
//        System.out.println("\n");
//        double max = ((double) maxErrorBuckets[specificRule]) / numBoards / size / size;
//        double min = ((double) minErrorBuckets[specificRule]) / numBoards / size / size;
//        double ebDiff = ((double) maxErrorBuckets[specificRule] / numBoards / size / size) - ((double) minErrorBuckets[specificRule] / numBoards / size / size);
//        double beDiff = ((double) (numTrials * size * size) / (double) maxErrorBuckets[specificRule]) - ((double) (numTrials * size * size) / (double) minErrorBuckets[specificRule]);
//        System.out.println("ebDiff " + ebDiff);
//        System.out.println("beDiff " + beDiff);
//        System.out.println("max * min = " + (max * min));
//        double[][] sameMin = new double[size][size];
//        double[][] sameMax = new double[size][size];
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                sameMin[row][column] = (double) sameErrorMin[row + 1] / (double) sameErrorMin[column + 1];
//                sameMax[row][column] = (double) sameErrorMax[2 * row + 2] / (double) sameErrorMax[2 * column + 2];
//            }
//        }
//        System.out.println("sameMaxRowComparisons ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", sameMax[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("sameMinRowComparisons ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", sameMin[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        for (row = 1; row < 4096 && row < sameErrorMin.length; row *= 2) {
//            System.out.print("row " + row + " " + sameErrorMin[row] + "\n");
//        }
//        int maxErrorTot = 0;
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                maxErrorTot += minErrorMap[row][column];
//            }
//        }
//        CustomArray.plusArrayDisplay(minErrorMap, false, false, "Min Error Map");
//        CustomArray.plusArrayDisplay(maxErrorMap, false, false, "Max Error Map");
//        double[] rowTots = new double[size];
//        double phi = (1 + Math.sqrt(5)) / 2;
//        int[] maxRowTots = new int[size];
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                rowTots[row] += minErrorMap[row][column];
//                maxRowTots[row] += maxErrorMap[row][column];
//            }
//        }
//        double[][] maxRowComparisons = new double[size][size];
//        double[][] minRowComparisons = new double[size][size];
//        double[][] maxMapRate = new double[size][size];
//        double[][] minMapRate = new double[size][size];
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                maxRowComparisons[row][column] = (double) maxRowTots[row] / (double) maxRowTots[column];
//                minRowComparisons[row][column] = (double) rowTots[row] / (double) rowTots[column];
//                maxMapRate[row][column] = (double) maxErrorMap[row][column] / (double) numBoards / 8.0;
//                minMapRate[row][column] = (double) minErrorMap[row][column] / (double) numBoards / 8.0;
//            }
//        }
//        System.out.println("maxRowComparisons ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", maxRowComparisons[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("maxMapRate[][] ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", maxMapRate[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("minRowComparisons ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", minRowComparisons[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("maxMapRate[][] ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", minMapRate[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("(1,0) - PI/3" + ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0));
//        System.out.println("(3,2) - PI " + ((double) rowTots[2] / (double) rowTots[3] - Math.PI));
//        double diff = ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0);
//        diff = Math.log(diff) / Math.log(2);
//        System.out.println("diff " + diff);
//        diff = ((double) rowTots[2] / (double) rowTots[3] - Math.PI);
//        diff = Math.log(diff) / Math.log(2);
//        System.out.println("diff " + diff);
//        int upper = 0;
//        int lower = 0;
//        for (row = 0; row < 2; row++) {
//            for (column = 0; column < size; column++) {
//                upper += minErrorMap[row][column];
//                lower += minErrorMap[row + 2][column];
//            }
//        }
//        double div = (double) upper / (double) lower;
//        System.out.println("upper/lower " + div + " " + 1.0 / div + " " + div * Math.PI + " " + Math.PI / div + " " + div / Math.PI);
//        System.out.println("upper/lower - phi*phi " + (div - phi * phi));
//        double[] diffs = new double[]{((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0), ((double) rowTots[2] / (double) rowTots[3] - Math.PI), (div - phi * phi)};
//        for (int d = 0; d < 3; d++) {
//            diffs[d] = Math.abs(diffs[d]);
//            System.out.println("diff " + diffs[d] + " " + (Math.log(diffs[d]) / Math.log(2)));
//        }
//        for (int power = 2; power > -20; power--) {
//            System.out.print((int) (Math.PI / Math.pow(2, power)) % 2 + " ");
//        }
//        System.out.println();
//        for (int power = 2; power > -20; power--) {
//            System.out.print((int) (diffs[1] / Math.pow(2, power)) % 2 + " ");
//        }
//        System.out.println();
//        for (int power = 2; power > -24; power--) {
//            int a = (int) ((diffs[1] / Math.pow(2, power)) % 2);
//            int b = (int) ((Math.PI / Math.pow(2, power)) % 2);
//            System.out.print((a ^ b) + " ");
//        }
//        System.out.println();
//        double[][] errors = new double[size][size];
//        double[] byRow = new double[size];
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                errors[row][column] = (double) minErrorMap[row][column] / (double) (Math.pow(2, size * size) * 8);
//                byRow[row] += errors[row][column];
//            }
//            byRow[row] /= 4;
//            byRow[row] *= (440 * 8);
//        }
//        System.out.println("errors ");
//        for (row = 0; row < size; row++) {
//            System.out.println(Arrays.toString(errors[row]));
//        }
//        System.out.println("byRow " + Arrays.toString(byRow));
//        //doWallisProduct(50);
//        //doTriangles(50);
//        System.out.println("solutionDistro: " + Arrays.toString(minSolutionDistro));
//        System.out.println("maxSolutionDistro: " + Arrays.toString(maxSolutionDistro));
//        System.out.println("\n\n\n\n\n\n\n");
//        for (int spot = 0; spot < 16; spot++) {
//            System.out.print((double) maxSolutionDistro[spot] / 65536.0 + " ");
//        }
//        System.out.println();
//        for (int spot = 0; spot < 16; spot++) {
//            System.out.print(65536.0 / (double) maxSolutionDistro[spot] + " ");
//        }
        return solutions[0];
    }

    public void displayResults(boolean doChangeScore) {
//        int row;
//        int column;
//        //The rest is results management & display
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //Sorts and averages the best performing ECA rules
//        System.out.println("solutionBucket[] " + solutionBucket);
//        System.out.println("pre division");
//        System.out.println(Arrays.toString(minErrorBuckets));
//        System.out.println("hbLength[]");
//        System.out.println(Arrays.toString(numberBoards));
//        int[] hashbucketRate = new int[256];
//        for (row = start; row < stop; row++) {
//            hashbucketRate[row] = minErrorBuckets[row] / numberBoards[row];
//            if (doChangeScore) averageChange[row] = (double) hammingChange[row] / (double) errorChange[row];
//        }
//        System.out.println("post division");
//        System.out.println(Arrays.toString(hashbucketRate));
//        int[] sorted   //The rest is results management & display
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //Sorts and averages the best performing ECA rules
//        System.out.println("solutionBucket[] " + solutionBucket);
//        System.out.println("pre division");
//        System.out.println(Arrays.toString(minErrorBuckets));
//        System.out.println("hbLength[]");
//        System.out.println(Arrays.toString(numberBoards));
//        int[] hashbucketRate = new int[256];
//        for (row = start; row < stop; row++) {
//            hashbucketRate[row] = minErrorBuckets[row] / numberBoards[row];
//            if (doChangeScore) averageChange[row] = (double) hammingChange[row] / (double) errorChange[row];
//        }
//        System.out.println("post division");
//        System.out.println(Arrays.toString(hashbucketRate));
//        int[] sorted = new int[256];
//        for (row = 0; row < 256; row++) {
//            sorted[row] = row;
//        }
//        if (doAllRules) {
//            for (row = 0; row < 256; row++) {
//                for (column = 0; column < 256; column++) {
//                    if (hashbucketRate[row] < hashbucketRate[column]) {
//                        int temp = minErrorBuckets[row];
//                        minErrorBuckets[row] = minErrorBuckets[column];
//                        minErrorBuckets[column] = temp;
//                        temp = sorted[row];
//                        sorted[row] = sorted[column];
//                        sorted[column] = temp;
//                        temp = numberBoards[row];
//                        numberBoards[row] = numberBoards[column];
//                        numberBoards[column] = temp;
//                        temp = hashbucketRate[row];
//                        hashbucketRate[row] = hashbucketRate[column];
//                        hashbucketRate[column] = temp;
//                    }
//                }
//            }
//        }
//        // System.out.println(Arrays.toString(hashbucket));
//        //
//        //
//        //Output
//        System.out.println("Here");
//        for (int index = start; index < stop; index++) {
//            System.out.println("index " + sorted[index] + " " + basicECA.ruleClasses[sorted[index]] + " " + hashbucketRate[index] + " " + minErrorBuckets[index] + " " + numberBoards[index] + " ");
//        }
//        System.out.println(Arrays.toString(sameSolutions));
//        for (row = start; row < stop; row++) {
//            if (sameSolutions[sorted[row]] == 1) {
//                System.out.println("Rule has a unique solution " + sorted[row]);
//            } else {
//                System.out.println("sameSolutions[" + sorted[row] + "] " + sameSolutions[sorted[row]]);
//            }
//        }
//        if (doChangeScore) {
//            System.out.println("hammingChange per changeScore");
//            for (row = start; row < stop; row++) {
//                System.out.println(sorted[row] + " " + changeChange[sorted[row]] + " " + errorChange[sorted[row]]);
//            }
//        }
//        System.out.println("min error/grid " + ((double) minErrorBuckets[specificRule] / (double) numBoards));
//        System.out.println("min error/bit " + ((double) minErrorBuckets[specificRule]) / numBoards / size / size);
//        System.out.println("min bits/error " + (double) (numBoards * size * size) / (double) minErrorBuckets[specificRule]);
//        System.out.println("sameError " + Arrays.toString(sameErrorMin));
//        System.out.println("sameErrorMax " + Arrays.toString(sameErrorMax));
//        System.out.println("max error/grid " + ((double) maxErrorBuckets[specificRule] / (double) numBoards));
//        System.out.println("max error/bit " + ((double) maxErrorBuckets[specificRule]) / numBoards / size / size);
//        System.out.println("max bits/error " + (double) (numBoards * size * size) / (double) maxErrorBuckets[specificRule]);
//        System.out.println("\n");
//        double max = ((double) maxErrorBuckets[specificRule]) / numBoards / size / size;
//        double min = ((double) minErrorBuckets[specificRule]) / numBoards / size / size;
//        double ebDiff = ((double) maxErrorBuckets[specificRule] / numBoards / size / size) - ((double) minErrorBuckets[specificRule] / numBoards / size / size);
//        double beDiff = ((double) (numTrials * size * size) / (double) maxErrorBuckets[specificRule]) - ((double) (numTrials * size * size) / (double) minErrorBuckets[specificRule]);
//        System.out.println("ebDiff " + ebDiff);
//        System.out.println("beDiff " + beDiff);
//        System.out.println("max * min = " + (max * min));
//        double[][] sameMin = new double[size][size];
//        double[][] sameMax = new double[size][size];
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                sameMin[row][column] = (double) sameErrorMin[row + 1] / (double) sameErrorMin[column + 1];
//                sameMax[row][column] = (double) sameErrorMax[2 * row + 2] / (double) sameErrorMax[2 * column + 2];
//            }
//        }
//        System.out.println("sameMaxRowComparisons ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", sameMax[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("sameMinRowComparisons ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", sameMin[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        for (row = 1; row < 4096 && row < sameErrorMin.length; row *= 2) {
//            System.out.print("row " + row + " " + sameErrorMin[row] + "\n");
//        }
//        int maxErrorTot = 0;
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                maxErrorTot += minErrorMap[row][column];
//            }
//        }
//        CustomArray.plusArrayDisplay(minErrorMap, false, false, "Min Error Map");
//        CustomArray.plusArrayDisplay(maxErrorMap, false, false, "Max Error Map");
//        double[] rowTots = new double[size];
//        double phi = (1 + Math.sqrt(5)) / 2;
//        int[] maxRowTots = new int[size];
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                rowTots[row] += minErrorMap[row][column];
//                maxRowTots[row] += maxErrorMap[row][column];
//            }
//        }
//        double[][] maxRowComparisons = new double[size][size];
//        double[][] minRowComparisons = new double[size][size];
//        double[][] maxMapRate = new double[size][size];
//        double[][] minMapRate = new double[size][size];
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                maxRowComparisons[row][column] = (double) maxRowTots[row] / (double) maxRowTots[column];
//                minRowComparisons[row][column] = (double) rowTots[row] / (double) rowTots[column];
//                maxMapRate[row][column] = (double) maxErrorMap[row][column] / (double) numBoards / 8.0;
//                minMapRate[row][column] = (double) minErrorMap[row][column] / (double) numBoards / 8.0;
//            }
//        }
//        System.out.println("maxRowComparisons ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", maxRowComparisons[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("maxMapRate[][] ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", maxMapRate[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("minRowComparisons ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", minRowComparisons[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("maxMapRate[][] ");
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", minMapRate[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("(1,0) - PI/3" + ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0));
//        System.out.println("(3,2) - PI " + ((double) rowTots[2] / (double) rowTots[3] - Math.PI));
//        double diff = ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0);
//        diff = Math.log(diff) / Math.log(2);
//        System.out.println("diff " + diff);
//        diff = ((double) rowTots[2] / (double) rowTots[3] - Math.PI);
//        diff = Math.log(diff) / Math.log(2);
//        System.out.println("diff " + diff);
//        int upper = 0;
//        int lower = 0;
//        for (row = 0; row < 2; row++) {
//            for (column = 0; column < size; column++) {
//                upper += minErrorMap[row][column];
//                lower += minErrorMap[row + 2][column];
//            }
//        }
//        double div = (double) upper / (double) lower;
//        System.out.println("upper/lower " + div + " " + 1.0 / div + " " + div * Math.PI + " " + Math.PI / div + " " + div / Math.PI);
//        System.out.println("upper/lower - phi*phi " + (div - phi * phi));
//        double[] diffs = new double[]{((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0), ((double) rowTots[2] / (double) rowTots[3] - Math.PI), (div - phi * phi)};
//        for (int d = 0; d < 3; d++) {
//            diffs[d] = Math.abs(diffs[d]);
//            System.out.println("diff " + diffs[d] + " " + (Math.log(diffs[d]) / Math.log(2)));
//        }
//        for (int power = 2; power > -20; power--) {
//            System.out.print((int) (Math.PI / Math.pow(2, power)) % 2 + " ");
//        }
//        System.out.println();
//        for (int power = 2; power > -20; power--) {
//            System.out.print((int) (diffs[1] / Math.pow(2, power)) % 2 + " ");
//        }
//        System.out.println();
//        for (int power = 2; power > -24; power--) {
//            int a = (int) ((diffs[1] / Math.pow(2, power)) % 2);
//            int b = (int) ((Math.PI / Math.pow(2, power)) % 2);
//            System.out.print((a ^ b) + " ");
//        }
//        System.out.println();
//        double[][] errors = new double[size][size];
//        double[] byRow = new double[size];
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                errors[row][column] = (double) minErrorMap[row][column] / (double) (Math.pow(2, size * size) * 8);
//                byRow[row] += errors[row][column];
//            }
//            byRow[row] /= 4;
//            byRow[row] *= (440 * 8);
//        }
//        System.out.println("errors ");
//        for (row = 0; row < size; row++) {
//            System.out.println(Arrays.toString(errors[row]));
//        }
//        System.out.println("byRow " + Arrays.toString(byRow));
//        //doWallisProduct(50);
//        //doTriangles(50);
//        System.out.println("solutionDistro: " + Arrays.toString(minSolutionDistro));
//        System.out.println("maxSolutionDistro: " + Arrays.toString(maxSolutionDistro));
//        System.out.println("\n\n\n\n\n\n\n");
//        for (int spot = 0; spot < 16; spot++) {
//            System.out.print((double) maxSolutionDistro[spot] / 65536.0 + " ");
//        }
//        System.out.println();
//        for (int spot = 0; spot < 16; spot++) {
//            System.out.print(65536.0 / (double) maxSolutionDistro[spot] + " ");
//        }
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
            fitted = checkPascalErrorCorrectionAll(rule, changed, basicECA.ecaWolframCodes[rule], rule);
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
}
