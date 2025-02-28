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
    /**
     * Within a single solution, if multiple codewords have the same weighted error score, each codeword is placed here
     */
    int[] sameError;
    /**
     * Within a single maximization solution, if multiple codewords have the same weighted error score, each is placed here
     */
    int[] sameErrorMax;
    /**
     * Cumulative heat map of errors, maximization version
     */
    int[][] maxErrorMap;
    /**
     * Cumulative heat map of errors
     */
    int[][] minErrorMap;
    /**
     *
     */
    int[] gateDistro;
    /**
     *
     */
    int[][] maxDistro;
    /**
     * Weighted change score of an input, scored the same way as the errors
     */
    int changeScoreInt;
    /**
     * Hamming change in codewords
     */
    int totHammingChange;
    /**
     * Total number of errors in all solutions found
     */
    int totLocalErrors;
    /**
     * ??
     */
    int[] fbfWolfram;
    /**
     * Cumulative distribution of codeword solutions over all inputs
     */
    int[] solutionDistro;
    /**
     * Cumulative distribution of codeword solutions over all inputs, maximized error codewords rather than minimized error codewords
     */
    int[] maxSolutionDistro;

    /**
     * Finds the minimum-discrepancy initial neighborhood output of the ECA n vs the input
     *
     * @param n  ECA rule 0-255
     * @param in binary input array
     * @return the array with ECA n and initial neighborhood mimimum output
     */
    public int[][] checkPascalErrorCorrectionAll(int n, int[][] in) {

        int[] wolfram = new int[8];
        for (int power = 0; power < 8; power++) {
            wolfram[power] = ((n / (int) Math.pow(2, power)) % 2);
        }
        sameSolutions = new int[256];
        int size = in.length;
        localMaxSolution = new int[size][size];
        int[][] trialField = new int[size][size];
        int[] errorScore = new int[sameError.length];
        int row = 0;
        int column = 0;
        int a = 0;
        int b = 0;
        int c = 0;

        //
        //
        //Check every possible input neighborhood of length size
        for (int correction = 0; correction < (int) Math.pow(2, size); correction++) {
            //Initialize trial neighborhood
            for (column = 0; column < size; column++) {
                trialField[0][column] = ((correction / (int) Math.pow(2, column)) % 2);
            }
            //Run Wolfram code on trial neighborhood to row size
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
                    errorScore[correction] += ((int) Math.pow(2, row) * (trialField[row][column] ^ in[row][column]));
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
        for (int correction = 0; correction < (int) Math.pow(2, size); correction++) {
            if (errorScore[correction] > maxErrors) {
                maxErrors = errorScore[correction];
                maxSpot = correction;
            }
        }
        int sameNumMax = 0;
        for (int correction = 0; correction < (int) Math.pow(2, size); correction++) {
            if (errorScore[correction] == maxErrors) {
                sameNumMax++;
            }
        }
        int minErrors = Integer.MAX_VALUE;
        int minSpot = 0;
        for (int correction = 0; correction < (int) Math.pow(2, size); correction++) {
            if (errorScore[correction] < minErrors) {
                minErrors = errorScore[correction];
                minSpot = correction;
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
        int sameNumErrors = 0;
        int[] esPrintList = new int[errorScore.length];
        int[] esMaxPL = new int[errorScore.length];
        Arrays.fill(esMaxPL, -1);
        Arrays.fill(esPrintList, -1);
        int index = 0;
        int maxIndex = 0;
        int firstMinSpot = -1;
        for (int correction = 0; correction < (int) Math.pow(2, size); correction++) {
            if (errorScore[correction] == minErrors) {
                sameNumErrors++;
                esPrintList[index] = correction;
                index++;
                if (firstMinSpot == -1) {
                    firstMinSpot = correction;
                }
            }
            if (errorScore[correction] == maxErrors) {
                sameNumMax++;
                esMaxPL[maxIndex] = correction;
                maxIndex++;

            }
        }
        esPrintList = Arrays.copyOfRange(esPrintList, 0, index);
        esMaxPL = Arrays.copyOfRange(esMaxPL, 0, index);
        sameError[sameNumErrors]++;
        sameErrorMax[sameNumMax]++;
        //System.out.print("index " + index);
        //System.out.print("\n\n");
        if (sameNumErrors > sameSolutions[n]) {
            sameSolutions[n] = sameNumErrors;
        }
        //System.out.println("sameNumErrors = " + sameNumErrors);
        //System.out.println("sameNumErrorsMax = " + sameNumMax);
        //System.out.println("sameNumErrors " + sameNumErrors);
        //
        //
        //Run the Wolfram code on the minimum neighborhood codeword for function return purposes
        trialField = new int[size + 1][size];
        int neighborhoodInt = 0;
        for (column = 0; column < size; column++) {
            //System.out.println("column " + column);
            trialField[0][column] = ((minSpot / (int) Math.pow(2, column)) % 2);
            neighborhoodInt += (int) Math.pow(2, column)*trialField[0][column];

            localMaxSolution[0][column] = ((maxSpot / (int) Math.pow(2, column)) % 2);
        }
        solutionDistro[neighborhoodInt]++;
        //columnOut[0] = field[0][size/2];
        //calculate neighborhood
        //System.out.println("size/2 " + (size/2));
        //System.out.println("size/2 - input.length/2 " + (size/2-input.length/2));
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

        int[][] out = new int[size][size];
        for (row = 0; row < size; row++) {
            for (column = 0; column < size; column++) {
                out[row][column] = trialField[row][column];
                //to get pi in the ratio, this localMaxSolution was trialField[]
                //maxErrorMap[row][column] += (trialField[row][column] ^ in[row][column]);
                //maxErrorMap[row][column] += (localMaxSolution[row][column]^in[row][column]);
            }
        }
        //
         //
         //Some experimental voting procedures
        int[][][] subVote = new int[10][size][size];
        for (int answer = 0; answer < 1; answer++) {
            trialField = new int[size][size];
            for (column = 0; column < size; column++) {
                //System.out.println("column " + column);
                subVote[answer][0][column] = ((esPrintList[esPrintList.length / 2] / (int) Math.pow(2, column)) % 2);
                //subVote[answer][0][column] = ((esPrintList[rand.nextInt(0,esPrintList.length)]/ (int) Math.pow(2, column)) % 2);
                subVote[answer][0][column] = ((minSpot / (int) Math.pow(2, column)) % 2);
                //subVote[answer][0][column] = ((firstMinSpot / (int) Math.pow(2, column)) % 2);
                //localMaxSolution[0][column] = ((maxSpot / (int) Math.pow(2, column)) % 2);
                //trialField[0][column] = (((minSpot+maxSpot)/2)/(int)Math.pow(2, column))%2;
                //trialField[0][column] = (((minSpot+firstMinSpot)/2)/(int)Math.pow(2, column))%2;
            }
            //columnOut[0] = field[0][size/2];
            //calculate neighborhood
            //System.out.println("size/2 " + (size/2));
            //System.out.println("size/2 - input.length/2 " + (size/2-input.length/2));
            for (row = 1; row < size; row++) {
                for (column = 0; column < size; column++) {
                    a = ((column - 1) + size) % size;
                    b = column;
                    c = ((column + 1)) % size;
                    subVote[answer][row][column] = subVote[answer][row - 1][a] + 2 * subVote[answer][row - 1][b] + 4 * subVote[answer][row - 1][c];
                    subVote[answer][row][column] = wolfram[subVote[answer][row][column]];
                    subVote[9][row][column] += subVote[answer][row][column];
                    ////localMaxSolution[row][column] = localMaxSolution[row - 1][a] + 2 * trialField[row - 1][b] + 4 * trialField[row - 1][c];
                    //localMaxSolution[row][column] = wolfram[localMaxSolution[row][column]];
                }
            }
            for (row = 0; row < size; row++) {
                for (column = 0; column < size; column++) {
                    //to get pi in the ratio, this localMaxSolution was trialField[]
                    minErrorMap[row][column] += (subVote[answer][row][column] ^ in[row][column]);
                    //maxErrorMap[row][column] += (localMaxSolution[row][column] ^ in[row][column]);
                }
            }
        }
        for (row = 0; row < size; row++) {
            for (column = 0; column < size; column++) {
                if (subVote[sameNumErrors][row][column] > (sameNumErrors / 2)) {
                    subVote[sameNumErrors][row][column] = 1;
                } else {
                    subVote[sameNumErrors][row][column] = 0;
                }
                //to get pi in the ratio, this localMaxSolution was trialField[]
                //maxErrorMap[row][column] += (subVote[sameNumErrors][row][column] ^ in[row][column]);
                //maxErrorMap[row][column] += (localMaxSolution[row][column]^in[row][column]);
            }
        }
        int[][][] subVoteMax = new int[10][size][size];
        for (int answer = 0; answer < 1; answer++) {
            trialField = new int[size][size];
            neighborhoodInt = 0;
            for (column = 0; column < size; column++) {
                //System.out.println("column " + column);
                //subVote[answer][0][column] = ((esPrintList[esPrintList.length / 2] / (int) Math.pow(2, column)) % 2);
                //subVote[answer][0][column] = ((esPrintList[rand.nextInt(0,esPrintList.length)]/ (int) Math.pow(2, column)) % 2);
                subVoteMax[answer][0][column] = ((maxSpot / (int) Math.pow(2, column)) % 2);
                //subVote[answer][0][column] = ((firstMinSpot / (int) Math.pow(2, column)) % 2);
                //localMaxSolution[0][column] = ((maxSpot / (int) Math.pow(2, column)) % 2);
                //trialField[0][column] = (((minSpot+maxSpot)/2)/(int)Math.pow(2, column))%2;
                //trialField[0][column] = (((minSpot+firstMinSpot)/2)/(int)Math.pow(2, column))%2;
                neighborhoodInt += (int) Math.pow(2, column)*subVoteMax[answer][0][column];
            }
            maxSolutionDistro[neighborhoodInt]++;
            //columnOut[0] = field[0][size/2];
            //calculate neighborhood
            //System.out.println("size/2 " + (size/2));
            //System.out.println("size/2 - input.length/2 " + (size/2-input.length/2));
            for (row = 1; row < size; row++) {
                for (column = 0; column < size; column++) {
                    a = ((column - 1) + size) % size;
                    b = column;
                    c = ((column + 1)) % size;
                    subVoteMax[answer][row][column] = subVoteMax[answer][row - 1][a] + 2 * subVoteMax[answer][row - 1][b] + 4 * subVoteMax[answer][row - 1][c];
                    subVoteMax[answer][row][column] = wolfram[subVoteMax[answer][row][column]];
                    subVoteMax[9][row][column] += subVoteMax[answer][row][column];
                    ////localMaxSolution[row][column] = localMaxSolution[row - 1][a] + 2 * trialField[row - 1][b] + 4 * trialField[row - 1][c];
                    //localMaxSolution[row][column] = wolfram[localMaxSolution[row][column]];
                }
            }
            for (row = 0; row < size; row++) {
                for (column = 0; column < size; column++) {
                    //to get pi in the ratio, this localMaxSolution was trialField[]
                    maxErrorMap[row][column] += (subVoteMax[answer][row][column] ^ in[row][column]);
                    //maxErrorMap[row][column] += (localMaxSolution[row][column] ^ in[row][column]);
                }
            }
        }
        for (row = 0; row < size; row++) {
            for (column = 0; column < size; column++) {
                if (subVoteMax[sameNumErrors][row][column] > (sameNumErrors / 2)) {
                    subVoteMax[sameNumErrors][row][column] = 1;
                } else {
                    subVoteMax[sameNumErrors][row][column] = 0;
                }
                //to get pi in the ratio, this localMaxSolution was trialField[]
                //maxErrorMap[row][column] += (subVote[sameNumErrors][row][column] ^ in[row][column]);
                //maxErrorMap[row][column] += (localMaxSolution[row][column]^in[row][column]);
            }
        }
        return out;
    }

    /**
     * Manager function for checkPascalErrorCorrection()
     *
     * @param specificRule ECA 0-255 rule
     * @param size         width of input array, size of solution neighborhood
     * @return ???
     */
    public int[][] ecaBestFitHashCollisionExhuastive(int specificRule, int size, boolean doChangeScore, boolean doAllRules, int numTrials) {
        //
        //
        //Initialization
        maxErrorMap = new int[size][size];
        maxSolutionDistro = new int[(int) Math.pow(2, size)];
        solutionDistro = new int[(int) Math.pow(2, size)];
        int numBoards = (int) Math.pow(2, size * size);
        fbfWolfram = new int[numBoards];
        minErrorMap = new int[size][size];

        sameSolutions = new int[256];
        //coefficients = coefficients(size);
        sameError = new int[(int) Math.pow(2, size) + 1];
        sameErrorMax = new int[sameError.length];
        int[][] field = new int[size][size];
        int[] hashbucket = new int[256];
        int[] hashbucketMax = new int[256];
        int[][][] solutions = new int[8][size][size];
        int[] hbLength = new int[256];
        BasicECA basicECA = new BasicECA();
        int[] errorChange = new int[256];
        int[] hammingChange = new int[256];
        double[] averageChange = new double[256];
        int[] solutionBucket = new int[256];
        double[] changeChange = new double[256];
        int[] changeScore = new int[256];
        int[][] errorMap = new int[size][size];
        int start = 0;
        if (!doAllRules) start = specificRule;
        int stop = 255;
        if (!doAllRules) stop = specificRule + 1;
        int totMaxErrors = 0;
        int[][] vote = new int[size][size];
        int[][] maxVote = new int[size][size];
        int[][] maxTemp = new int[size][size];
        //In previous versions this ran for all 0-255 ECA rules
        for (int rule = start; rule < stop; rule++) {
            totMaxErrors = 0;
            errorMap = new int[size][size];
            System.out.println("rule " + rule);
            for (int trial = 0; trial < numBoards; trial++) {
                if (trial % 100 == 0) System.out.println("trial " + trial);
                //Creates a random binary array
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        field[row][column] = ((trial / (int) Math.pow(2, size * column + row)) % 2);
                    }
                }
                //CustomArray.plusArrayDisplay(field, false, true, "Field");
                int[][] solutionsTemp = new int[size][size];
                vote = new int[size][size];
                maxVote = new int[size][size];
                //This loop reflects, rotates, and transposes the input data array
                for (int rotation = 0; rotation < 8; rotation++) {
                    int[][] temp = new int[size][size];
                    for (int row = 0; row < size; row++) {
                        for (int column = 0; column < size; column++) {
                            temp[row][column] = field[row][column];
                        }
                    }
                    temp = reflectRotateTranspose(temp, rotation);
                    temp = checkPascalErrorCorrectionAll(rule, temp);
                    temp = reflectRotateTranspose(temp, rotation);
                    maxTemp = localMaxSolution;
                    //unreflects, unrotates, untransposes the solution
                    int totErrors = 0;
                    for (int row = 0; row < size; row++) {
                        for (int column = 0; column < size; column++) {
                            solutions[rotation][row][column] = temp[row][column];
                            vote[row][column] += solutions[rotation][row][column];
                            maxVote[row][column] += maxTemp[row][column];
                        }
                    }

                }
                for (int column = 0; column < size; column++) {
                    fbfWolfram[trial] += (int) Math.pow(2, column) * solutions[0][0][column];
                }
                int totErrors = 0;
                //totMaxErrors = 0;
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        if (vote[row][column] > 3) vote[row][column] = 1;
                        else vote[row][column] = 0;
                        totErrors += (vote[row][column] ^ field[row][column]);
                        errorMap[row][column] += (vote[row][column] ^ field[row][column]);
                        if (maxVote[row][column] > 3) maxVote[row][column] = 1;
                        else maxVote[row][column] = 0;
                        totMaxErrors += (maxVote[row][column] ^ field[row][column]);
                    }
                }
                //solutionBucket[index]++;
                hashbucket[rule] += totErrors;
                hashbucketMax[rule] += totMaxErrors;
                totMaxErrors = 0;
                hbLength[rule]++;
                //
                //
                //Severely impacts runtime
                //Finds the mean distance between codewords per change in the random input data
                //the changeScore is scored with the same exponent as the errorScore
                if (doChangeScore) {
                    changeChange[rule] = changeChange(rule, vote, totErrors, numTrials);
                    errorChange[rule] += totErrors;
                    hammingChange[rule] += totHammingChange;
                    changeScore[rule] += changeScoreInt;
                }

            }
            if (doChangeScore) changeChange[rule] = (double) hammingChange[rule] / (double) changeScore[rule];
            //CustomArray.plusArrayDisplay(errorMap,false,false,"ECA rule " + rule + " error map");
        }
        //
        //
        //Sorts and averages the best performing ECA rules
        System.out.println("solutionBucket[] " + solutionBucket);
        System.out.println("pre division");
        System.out.println(Arrays.toString(hashbucket));
        System.out.println("hbLength[]");
        System.out.println(Arrays.toString(hbLength));
        int[] hashbucketRate = new int[256];
        for (int row = start; row < stop; row++) {
            hashbucketRate[row] = hashbucket[row] / hbLength[row];
            if (doChangeScore) averageChange[row] = (double) hammingChange[row] / (double) errorChange[row];
        }
        System.out.println("post division");
        System.out.println(Arrays.toString(hashbucketRate));
        int[] sorted = new int[256];
        for (int row = 0; row < 256; row++) {
            sorted[row] = row;
        }
        if (doAllRules) {
            for (int row = 0; row < 256; row++) {
                for (int column = 0; column < 256; column++) {
                    if (hashbucketRate[row] < hashbucketRate[column]) {
                        int temp = hashbucket[row];
                        hashbucket[row] = hashbucket[column];
                        hashbucket[column] = temp;
                        temp = sorted[row];
                        sorted[row] = sorted[column];
                        sorted[column] = temp;
                        temp = hbLength[row];
                        hbLength[row] = hbLength[column];
                        hbLength[column] = temp;
                        temp = hashbucketRate[row];
                        hashbucketRate[row] = hashbucketRate[column];
                        hashbucketRate[column] = temp;
                    }
                }
            }
        }
        // System.out.println(Arrays.toString(hashbucket));
        //
        //
        //Output
        System.out.println("Here");
        for (int index = start; index < stop; index++) {
            System.out.println("index " + sorted[index] + " " + basicECA.ruleClasses[sorted[index]] + " " + hashbucketRate[index] + " " + hashbucket[index] + " " + hbLength[index] + " ");
        }
        System.out.println(Arrays.toString(sameSolutions));
        for (int row = start; row < stop; row++) {
            if (sameSolutions[sorted[row]] == 1) {
                System.out.println("Rule has a unique solution " + sorted[row]);
            } else {
                System.out.println("sameSolutions[" + sorted[row] + "] " + sameSolutions[sorted[row]]);
            }
        }
        if (doChangeScore) {
            System.out.println("hammingChange per changeScore");
            for (int row = start; row < stop; row++) {
                System.out.println(sorted[row] + " " + changeChange[sorted[row]] + " " + errorChange[sorted[row]]);
            }
        }
        System.out.println("min error/grid " + ((double)hashbucket[specificRule]/(double)numBoards));
        System.out.println("min error/bit " + ((double) hashbucket[specificRule]) / numBoards / size / size);
        System.out.println("min bits/error " + (double) (numBoards * size * size) / (double) hashbucket[specificRule]);
        System.out.println("sameError " + Arrays.toString(sameError));
        System.out.println("sameErrorMax " + Arrays.toString(sameErrorMax));

        System.out.println("max error/grid " + ((double)hashbucketMax[specificRule]/(double)numBoards));
        System.out.println("max error/bit " + ((double) hashbucketMax[specificRule]) / numBoards / size / size);
        System.out.println("max bits/error " + (double) (numBoards * size * size) / (double) hashbucketMax[specificRule]);
        System.out.println("\n");
        double max = ((double) hashbucketMax[specificRule]) / numBoards / size / size;
        double min = ((double) hashbucket[specificRule]) / numBoards / size / size;
        double ebDiff = ((double) hashbucketMax[specificRule] / numBoards / size / size) - ((double) hashbucket[specificRule] / numBoards / size / size);
        double beDiff = ((double) (numTrials * size * size) / (double) hashbucketMax[specificRule]) - ((double) (numTrials * size * size) / (double) hashbucket[specificRule]);
        System.out.println("ebDiff " + ebDiff);
        System.out.println("beDiff " + beDiff);
        System.out.println("max * min = " + (max * min));
        double[][] sameMin = new double[size][size];
        double[][] sameMax = new double[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                sameMin[row][column] = (double)sameError[row+1]/(double)sameError[column+1];
                sameMax[row][column] = (double)sameErrorMax[2*row+2]/(double)sameErrorMax[2*column+2];
            }
        }
        System.out.println("sameMaxRowComparisons " );
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f",sameMax[row][column]) + " ");
            }
            System.out.print("\n");
        }
        System.out.println("sameMinRowComparisons " );
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f",sameMin[row][column]) + " ");
            }
            System.out.print("\n");
        }

        for (int row = 1; row < 4096 && row < sameError.length; row *= 2) {
            System.out.print("row " + row + " " + sameError[row] + "\n");
        }
        System.out.println("gateDistro[] " + Arrays.toString(gateDistro));
        int maxErrorTot = 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                maxErrorTot += minErrorMap[row][column];
            }
        }
        CustomArray.plusArrayDisplay(minErrorMap, false, false, "Min Error Map");
        CustomArray.plusArrayDisplay(maxErrorMap, false, false, "Max Error Map");
        double[] rowTots = new double[size];
        double phi = (1 + Math.sqrt(5)) / 2;
        int[] maxRowTots = new int[size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                rowTots[row] += minErrorMap[row][column];
                maxRowTots[row] += maxErrorMap[row][column];
            }
        }
        double[][] maxRowComparisons = new double[size][size];
        double[][] minRowComparisons = new double[size][size];
        double[][] maxMapRate = new double[size][size];
        double[][] minMapRate = new double[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                maxRowComparisons[row][column] = (double)maxRowTots[row]/(double)maxRowTots[column];
                minRowComparisons[row][column] = (double)rowTots[row]/(double)rowTots[column];
                maxMapRate[row][column] = (double)maxErrorMap[row][column]/(double)numBoards/8.0;
                minMapRate[row][column] = (double)minErrorMap[row][column]/(double)numBoards/8.0;
            }
        }
        System.out.println("maxRowComparisons " );
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f",maxRowComparisons[row][column]) + " ");
            }
            System.out.print("\n");
        }
        System.out.println("maxMapRate[][] ");
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f",maxMapRate[row][column]) + " ");
            }
            System.out.print("\n");
        }
        System.out.println("minRowComparisons " );
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f",minRowComparisons[row][column]) + " ");
            }
            System.out.print("\n");
        }
        System.out.println("maxMapRate[][] ");
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f",minMapRate[row][column]) + " ");
            }
            System.out.print("\n");
        }


        System.out.println("(1,0) - PI/3" + ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0));
        System.out.println("(3,2) - PI " + ((double) rowTots[2] / (double) rowTots[3] - Math.PI));
        double diff = ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0);
        diff = Math.log(diff) / Math.log(2);
        System.out.println("diff " + diff);
        diff = ((double) rowTots[2] / (double) rowTots[3] - Math.PI);
        diff = Math.log(diff) / Math.log(2);
        System.out.println("diff " + diff);
        int upper = 0;
        int lower = 0;
        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < size; column++) {
                upper += minErrorMap[row][column];
                lower += minErrorMap[row + 2][column];
            }
        }
        double div = (double) upper / (double) lower;
        System.out.println("upper/lower " + div + " " + 1.0 / div + " " + div * Math.PI + " " + Math.PI / div + " " + div / Math.PI);
        System.out.println("upper/lower - phi*phi " + (div - phi * phi));
        double[] diffs = new double[]{((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0), ((double) rowTots[2] / (double) rowTots[3] - Math.PI), (div - phi * phi)};
        for (int d = 0; d < 3; d++) {
            diffs[d] = Math.abs(diffs[d]);
            System.out.println("diff " + diffs[d] + " " + (Math.log(diffs[d]) / Math.log(2)));
        }
        for (int power = 2; power > -20; power--) {
            System.out.print((int) (Math.PI / Math.pow(2, power)) % 2 + " ");

        }
        System.out.println();
        for (int power = 2; power > -20; power--) {
            System.out.print((int) (diffs[1] / Math.pow(2, power)) % 2 + " ");
        }
        System.out.println();
        for (int power = 2; power > -24; power--) {
            int a = (int) ((diffs[1] / Math.pow(2, power)) % 2);
            int b = (int) ((Math.PI / Math.pow(2, power)) % 2);
            System.out.print((a ^ b) + " ");
        }
        System.out.println();
        double[][] errors = new double[size][size];
        double[] byRow = new double[size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                errors[row][column] = (double) minErrorMap[row][column] / (double) (Math.pow(2, size * size) * 8);
                byRow[row] += errors[row][column];
            }
            byRow[row] /= 4;
            byRow[row] *= (440 * 8);

        }
        System.out.println("errors ");
        for (int row = 0; row < size; row++) {
            System.out.println(Arrays.toString(errors[row]));
        }
        System.out.println("byRow " + Arrays.toString(byRow));
        //doWallisProduct(50);
        //doTriangles(50);
        System.out.println("solutionDistro: " + Arrays.toString(solutionDistro));
        System.out.println("maxSolutionDistro: " + Arrays.toString(maxSolutionDistro));

        System.out.println("\n\n\n\n\n\n\n");
        for (int spot = 0; spot < 16; spot++) {
            System.out.print((double)maxSolutionDistro[spot]/65536.0 + " ");
        }
        System.out.println();
        for (int spot = 0; spot < 16; spot++) {
            System.out.print(65536.0/(double)maxSolutionDistro[spot] + " ");
        }

        return solutions[0];
    }

    /**
     * ???
     * @param rule
     * @param maxLength
     * @param numTrials
     */

    public void manageLengthsMinimizations(int rule, int maxLength, int numTrials) {
        int columnsOutput = 24;
        int[][] allData = new int[maxLength][(int) Math.pow(2, maxLength)];
        maxDistro = new int[maxLength][(int) Math.pow(2, maxLength)];
        for (int lengths = 4; lengths < maxLength; lengths++) {
            sameError = new int[(int) Math.pow(2, maxLength) + 1];
            sameErrorMax = new int[(int) Math.pow(2, maxLength) + 1];
            ecaBestFitHashCollisionExhuastive(rule, lengths, false, false, numTrials);
            for (int spot = 0; spot < (int) Math.pow(2, lengths); spot++) {
                allData[lengths][spot] = sameError[spot];
                maxDistro[lengths][spot] = sameErrorMax[spot];
            }
        }

        System.out.println("same number of errors minimum ");
        System.out.print("\n");

        for (int lengths = 4; lengths < maxLength; lengths += 2) {
            for (int spot = 0; spot < columnsOutput; spot++) {
                System.out.print(allData[lengths][spot]);
                if (spot != columnsOutput - 1) System.out.print(",");
            }
            if (lengths != maxLength - 2) System.out.print(";");
            //System.out.println();
        }
        System.out.println("}");
        System.out.print("\n{");

        for (int lengths = 5; lengths < maxLength; lengths += 2) {
            for (int spot = 0; spot < columnsOutput; spot++) {
                System.out.print(allData[lengths][spot]);
                if (spot != columnsOutput - 1) System.out.print(",");
            }
            if (lengths != maxLength - 1) System.out.print(";");
            //System.out.println();
        }
        System.out.println("}");
        System.out.println("same number of errors maximum ");
        System.out.print("\n{");

        for (int lengths = 4; lengths < maxLength; lengths += 2) {
            for (int spot = 0; spot < columnsOutput; spot++) {
                System.out.print(maxDistro[lengths][spot]);
                if (spot != columnsOutput - 1) System.out.print(",");
            }
            if (lengths != maxLength - 1) System.out.print(";");
            //System.out.println();
        }
        System.out.println("}");
        System.out.print("\n{");

        for (int lengths = 5; lengths < maxLength; lengths += 2) {
            for (int spot = 0; spot < columnsOutput; spot++) {
                System.out.print(maxDistro[lengths][spot]);
                if (spot != columnsOutput - 1) System.out.print(",");
            }
            if (lengths != maxLength - 1) System.out.print(";");
            //System.out.println();
        }
        System.out.println("}");

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
        changeScoreInt = 0;
        int[][] changed = new int[size][size];
        //Main loop
        for (int trial = 0; trial < numTrials; trial++) {
            //Initializes and does the changes on the input data
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    changed[row][column] = in[row][column];
                }
            }
            int numChanges = rand.nextInt(0, 2 * size);
            for (int change = 0; change < numChanges; change++) {
                int r = rand.nextInt(0, size);
                int c = rand.nextInt(0, size);
                changed[r][c] ^= 1;
                changeScoreInt += (int) Math.pow(2, r);
            }
            //Finds the new minimum neighborhood
            int[][] fitted = checkPascalErrorCorrectionAll(rule, changed);
            //Scores the new neighborhood
            int changes = 0;
            for (int power = 0; power < size; power++) {
                changes += (in[0][power] ^ fitted[0][power]);
            }
            int errorScore = 0;
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
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
     * Reflects and transposes the input array
     * @param in input array
     * @param rotation the one's place reflects across the x axis, the 2's place reflects across the y axis, and the 4's place is transpose
     * @return returns the input array reflected and transposed according to the rotation parameter
     */

    public int[][] reflectRotateTranspose(int[][] in, int rotation) {
        int size = in.length;
        int[][] out = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                out[row][column] = in[row][column];
            }
        }
        if (rotation % 2 == 1) {
            int[][] nextTemp = new int[size][size];
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    nextTemp[row][column] = out[size - 1 - row][column];
                }
            }
            out = nextTemp;
        }
        if (((rotation / 2) % 2) == 1) {
            int[][] nextTemp = new int[size][size];
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    nextTemp[row][column] = out[row][size - 1 - column];
                }
            }
            out = nextTemp;
        }
        if (((rotation / 4) % 2) == 1) {
            int[][] nextTemp = new int[size][size];
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    nextTemp[row][column] = out[column][row];
                }
            }
            out = nextTemp;
        }
        return out;
    }

    /**
     * ??
     * @param size
     * @return
     */

    public int[][] doWolfram(int size) {
        ecaBestFitHashCollisionExhuastive(150, 4, false, false, 1);
        int fieldSize = 150;
        int[][] field = new int[fieldSize][fieldSize];
        int[] primeStart = new int[]{10, 0, 4, 8};
        for (int column = 0; column < 4; column++) {
            //field[0][column + fieldSize / 2 - 2] = primeStart[column];
        }
        Random rand = new Random();
        for (int column = 0; column < fieldSize / 2; column++) {
            field[0][column + fieldSize / 2 - 2] = rand.nextInt(0, 16);
        }
        for (int row = 1; row < fieldSize; row++) {
            for (int column = row + 1; column < fieldSize - row - 2; column++) {
//                field[row][column] = field[row-1][column-1] + 16*field[row-1][column] + 256*field[row-1][column+1] + 256*16*field[row-1][column+2];
                field[row][column] = field[row - 1][column - 2] + 16 * field[row - 1][column - 1] + 256 * field[row - 1][column] + 256 * 16 * field[row - 1][column + 1];

                field[row][column] = fbfWolfram[field[row][column]];
            }
        }
        System.out.println("error min wolfram on prime ca initial value");
        for (int row = 0; row < fieldSize; row++) {
            for (int column = 0; column < fieldSize - row; column++) {
                if (column >= row) System.out.print(String.format("%2d", field[row][column]));
                else System.out.print("  ");
            }
            System.out.print("\n");
        }
        return field;
    }

    /**
     * ??
     * @return
     */
    public int[][] checkCodesXor(){
        int[] codes = new int[]{9,18,27,21};
        int[][] table = new int[4][4];
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                table[row][column] = codes[row] ^ codes[column];
            }
        }
        CustomArray.plusArrayDisplay(table,false,false,"row xor column");
        return table;
    }
    /**
     * A non-exhaustive version of ecaBestFitHashCollisionExhaustive
     *
     * @param specificRule ECA 0-255 rule
     * @param size         width of input array, size of solution neighborhood
     * @return ???
     */
    public int[][] ecaBestFitHashCollision(int specificRule, int size, boolean doChangeScore, boolean doAllRules, int numTrials) {
        //
        //
        //Initialization
        maxErrorMap = new int[size][size];
        maxSolutionDistro = new int[(int) Math.pow(2, size)];
        solutionDistro = new int[(int) Math.pow(2, size)];
        int numBoards = (int) Math.pow(2, size * size);
        fbfWolfram = new int[numBoards];
        minErrorMap = new int[size][size];

        sameSolutions = new int[256];
        //coefficients = coefficients(size);
        sameError = new int[(int) Math.pow(2, size) + 1];
        sameErrorMax = new int[sameError.length];
        int[][] field = new int[size][size];
        int[] hashbucket = new int[256];
        int[] hashbucketMax = new int[256];
        int[][][] solutions = new int[8][size][size];
        int[] hbLength = new int[256];
        BasicECA basicECA = new BasicECA();
        int[] errorChange = new int[256];
        int[] hammingChange = new int[256];
        double[] averageChange = new double[256];
        int[] solutionBucket = new int[256];
        double[] changeChange = new double[256];
        int[] changeScore = new int[256];
        int[][] errorMap = new int[size][size];
        int start = 0;
        if (!doAllRules) start = specificRule;
        int stop = 255;
        if (!doAllRules) stop = specificRule + 1;
        int totMaxErrors = 0;
        int[][] vote = new int[size][size];
        int[][] maxVote = new int[size][size];
        int[][] maxTemp = new int[size][size];
        Random rand  = new Random();
        //In previous versions this ran for all 0-255 ECA rules
        for (int rule = start; rule < stop; rule++) {
            totMaxErrors = 0;
            errorMap = new int[size][size];
            System.out.println("rule " + rule);
            for (int trial = 0; trial < numTrials; trial++) {
                if (trial % 100 == 0) System.out.println("trial " + trial);
                //Creates a random binary array
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        //field[row][column] = ((trial / (int) Math.pow(2, size * column + row)) % 2);
                        field[row][column] = rand.nextInt(0,2);
                    }
                }
                //CustomArray.plusArrayDisplay(field, false, true, "Field");
                int[][] solutionsTemp = new int[size][size];
                vote = new int[size][size];
                maxVote = new int[size][size];
                //This loop reflects, rotates, and transposes the input data array
                for (int rotation = 0; rotation < 8; rotation++) {
                    int[][] temp = new int[size][size];
                    for (int row = 0; row < size; row++) {
                        for (int column = 0; column < size; column++) {
                            temp[row][column] = field[row][column];
                        }
                    }
                    temp = reflectRotateTranspose(temp, rotation);
                    temp = checkPascalErrorCorrectionAll(rule, temp);
                    temp = reflectRotateTranspose(temp, rotation);
                    maxTemp = localMaxSolution;
                    //unreflects, unrotates, untransposes the solution
                    int totErrors = 0;
                    for (int row = 0; row < size; row++) {
                        for (int column = 0; column < size; column++) {
                            solutions[rotation][row][column] = temp[row][column];
                            vote[row][column] += solutions[rotation][row][column];
                            maxVote[row][column] += maxTemp[row][column];
                        }
                    }

                }
                for (int column = 0; column < size; column++) {
                    fbfWolfram[trial] += (int) Math.pow(2, column) * solutions[0][0][column];
                }
                int totErrors = 0;
                //totMaxErrors = 0;
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        if (vote[row][column] > 3) vote[row][column] = 1;
                        else vote[row][column] = 0;
                        totErrors += (vote[row][column] ^ field[row][column]);
                        errorMap[row][column] += (vote[row][column] ^ field[row][column]);
                        if (maxVote[row][column] > 3) maxVote[row][column] = 1;
                        else maxVote[row][column] = 0;
                        totMaxErrors += (maxVote[row][column] ^ field[row][column]);
                    }
                }
                //solutionBucket[index]++;
                hashbucket[rule] += totErrors;
                hashbucketMax[rule] += totMaxErrors;
                totMaxErrors = 0;
                hbLength[rule]++;
                //
                //
                //Severely impacts runtime
                //Finds the mean distance between codewords per change in the random input data
                //the changeScore is scored with the same exponent as the errorScore
                if (doChangeScore) {
                    changeChange[rule] = changeChange(rule, vote, totErrors, numTrials);
                    errorChange[rule] += totErrors;
                    hammingChange[rule] += totHammingChange;
                    changeScore[rule] += changeScoreInt;
                }

            }
            if (doChangeScore) changeChange[rule] = (double) hammingChange[rule] / (double) changeScore[rule];
            //CustomArray.plusArrayDisplay(errorMap,false,false,"ECA rule " + rule + " error map");
        }
        //
        //
        //Sorts and averages the best performing ECA rules
        System.out.println("solutionBucket[] " + solutionBucket);
        System.out.println("pre division");
        System.out.println(Arrays.toString(hashbucket));
        System.out.println("hbLength[]");
        System.out.println(Arrays.toString(hbLength));
        int[] hashbucketRate = new int[256];
        for (int row = start; row < stop; row++) {
            hashbucketRate[row] = hashbucket[row] / hbLength[row];
            if (doChangeScore) averageChange[row] = (double) hammingChange[row] / (double) errorChange[row];
        }
        System.out.println("post division");
        System.out.println(Arrays.toString(hashbucketRate));
        int[] sorted = new int[256];
        for (int row = 0; row < 256; row++) {
            sorted[row] = row;
        }
        if (doAllRules) {
            for (int row = 0; row < 256; row++) {
                for (int column = 0; column < 256; column++) {
                    if (hashbucketRate[row] < hashbucketRate[column]) {
                        int temp = hashbucket[row];
                        hashbucket[row] = hashbucket[column];
                        hashbucket[column] = temp;
                        temp = sorted[row];
                        sorted[row] = sorted[column];
                        sorted[column] = temp;
                        temp = hbLength[row];
                        hbLength[row] = hbLength[column];
                        hbLength[column] = temp;
                        temp = hashbucketRate[row];
                        hashbucketRate[row] = hashbucketRate[column];
                        hashbucketRate[column] = temp;
                    }
                }
            }
        }
        // System.out.println(Arrays.toString(hashbucket));
        //
        //
        //Output
        System.out.println("Here");
        for (int index = start; index < stop; index++) {
            System.out.println("index " + sorted[index] + " " + basicECA.ruleClasses[sorted[index]] + " " + hashbucketRate[index] + " " + hashbucket[index] + " " + hbLength[index] + " ");
        }
        System.out.println(Arrays.toString(sameSolutions));
        for (int row = start; row < stop; row++) {
            if (sameSolutions[sorted[row]] == 1) {
                System.out.println("Rule has a unique solution " + sorted[row]);
            } else {
                System.out.println("sameSolutions[" + sorted[row] + "] " + sameSolutions[sorted[row]]);
            }
        }
        if (doChangeScore) {
            System.out.println("hammingChange per changeScore");
            for (int row = start; row < stop; row++) {
                System.out.println(sorted[row] + " " + changeChange[sorted[row]] + " " + errorChange[sorted[row]]);
            }
        }
        System.out.println("min error/grid " + ((double) hashbucket[specificRule] / (double) numBoards));
        System.out.println("min error/bit " + ((double) hashbucket[specificRule]) / numBoards / size / size);
        System.out.println("min bits/error " + (double) (numBoards * size * size) / (double) hashbucket[specificRule]);
        System.out.println("sameError " + Arrays.toString(sameError));
        System.out.println("sameErrorMax " + Arrays.toString(sameErrorMax));

        System.out.println("max error/grid " + ((double) hashbucketMax[specificRule] / (double) numBoards));
        System.out.println("max error/bit " + ((double) hashbucketMax[specificRule]) / numBoards / size / size);
        System.out.println("max bits/error " + (double) (numBoards * size * size) / (double) hashbucketMax[specificRule]);
        System.out.println("\n");
        double max = ((double) hashbucketMax[specificRule]) / numBoards / size / size;
        double min = ((double) hashbucket[specificRule]) / numBoards / size / size;
        double ebDiff = ((double) hashbucketMax[specificRule] / numBoards / size / size) - ((double) hashbucket[specificRule] / numBoards / size / size);
        double beDiff = ((double) (numTrials * size * size) / (double) hashbucketMax[specificRule]) - ((double) (numTrials * size * size) / (double) hashbucket[specificRule]);
        System.out.println("ebDiff " + ebDiff);
        System.out.println("beDiff " + beDiff);
        System.out.println("max * min = " + (max * min));
        double[][] sameMin = new double[size][size];
        double[][] sameMax = new double[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                sameMin[row][column] = (double) sameError[row + 1] / (double) sameError[column + 1];
                sameMax[row][column] = (double) sameErrorMax[2 * row + 2] / (double) sameErrorMax[2 * column + 2];
            }
        }
        System.out.println("sameMaxRowComparisons ");
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f", sameMax[row][column]) + " ");
            }
            System.out.print("\n");
        }
        System.out.println("sameMinRowComparisons ");
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f", sameMin[row][column]) + " ");
            }
            System.out.print("\n");
        }

        for (int row = 1; row < 4096 && row < sameError.length; row *= 2) {
            System.out.print("row " + row + " " + sameError[row] + "\n");
        }
        System.out.println("gateDistro[] " + Arrays.toString(gateDistro));
        int maxErrorTot = 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                maxErrorTot += minErrorMap[row][column];
            }
        }
        CustomArray.plusArrayDisplay(minErrorMap, false, false, "Min Error Map");
        CustomArray.plusArrayDisplay(maxErrorMap, false, false, "Max Error Map");
        double[] rowTots = new double[size];
        double phi = (1 + Math.sqrt(5)) / 2;
        int[] maxRowTots = new int[size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                rowTots[row] += minErrorMap[row][column];
                maxRowTots[row] += maxErrorMap[row][column];
            }
        }
        double[][] maxRowComparisons = new double[size][size];
        double[][] minRowComparisons = new double[size][size];
        double[][] maxMapRate = new double[size][size];
        double[][] minMapRate = new double[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                maxRowComparisons[row][column] = (double) maxRowTots[row] / (double) maxRowTots[column];
                minRowComparisons[row][column] = (double) rowTots[row] / (double) rowTots[column];
                maxMapRate[row][column] = (double) maxErrorMap[row][column] / (double) numBoards / 8.0;
                minMapRate[row][column] = (double) minErrorMap[row][column] / (double) numBoards / 8.0;
            }
        }
        System.out.println("maxRowComparisons ");
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f", maxRowComparisons[row][column]) + " ");
            }
            System.out.print("\n");
        }
        System.out.println("maxMapRate[][] ");
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f", maxMapRate[row][column]) + " ");
            }
            System.out.print("\n");
        }
        System.out.println("minRowComparisons ");
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f", minRowComparisons[row][column]) + " ");
            }
            System.out.print("\n");
        }
        System.out.println("maxMapRate[][] ");
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(String.format("%.4f", minMapRate[row][column]) + " ");
            }
            System.out.print("\n");
        }

        System.out.println("(1,0) - PI/3" + ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0));
        System.out.println("(3,2) - PI " + ((double) rowTots[2] / (double) rowTots[3] - Math.PI));
        double diff = ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0);
        diff = Math.log(diff) / Math.log(2);
        System.out.println("diff " + diff);
        diff = ((double) rowTots[2] / (double) rowTots[3] - Math.PI);
        diff = Math.log(diff) / Math.log(2);
        System.out.println("diff " + diff);
        int upper = 0;
        int lower = 0;
        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < size; column++) {
                upper += minErrorMap[row][column];
                lower += minErrorMap[row + 2][column];
            }
        }
        double div = (double) upper / (double) lower;
        System.out.println("upper/lower " + div + " " + 1.0 / div + " " + div * Math.PI + " " + Math.PI / div + " " + div / Math.PI);
        System.out.println("upper/lower - phi*phi " + (div - phi * phi));
        double[] diffs = new double[]{((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0), ((double) rowTots[2] / (double) rowTots[3] - Math.PI), (div - phi * phi)};
        for (int d = 0; d < 3; d++) {
            diffs[d] = Math.abs(diffs[d]);
            System.out.println("diff " + diffs[d] + " " + (Math.log(diffs[d]) / Math.log(2)));
        }
        for (int power = 2; power > -20; power--) {
            System.out.print((int) (Math.PI / Math.pow(2, power)) % 2 + " ");

        }
        System.out.println();
        for (int power = 2; power > -20; power--) {
            System.out.print((int) (diffs[1] / Math.pow(2, power)) % 2 + " ");
        }
        System.out.println();
        for (int power = 2; power > -24; power--) {
            int a = (int) ((diffs[1] / Math.pow(2, power)) % 2);
            int b = (int) ((Math.PI / Math.pow(2, power)) % 2);
            System.out.print((a ^ b) + " ");
        }
        System.out.println();
        double[][] errors = new double[size][size];
        double[] byRow = new double[size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                errors[row][column] = (double) minErrorMap[row][column] / (double) (Math.pow(2, size * size) * 8);
                byRow[row] += errors[row][column];
            }
            byRow[row] /= 4;
            byRow[row] *= (440 * 8);

        }
        System.out.println("errors ");
        for (int row = 0; row < size; row++) {
            System.out.println(Arrays.toString(errors[row]));
        }
        System.out.println("byRow " + Arrays.toString(byRow));
        //doWallisProduct(50);
        //doTriangles(50);
        System.out.println("solutionDistro: " + Arrays.toString(solutionDistro));
        System.out.println("maxSolutionDistro: " + Arrays.toString(maxSolutionDistro));

        System.out.println("\n\n\n\n\n\n\n");
        for (int spot = 0; spot < 16; spot++) {
            System.out.print((double) maxSolutionDistro[spot] / 65536.0 + " ");
        }
        System.out.println();
        for (int spot = 0; spot < 16; spot++) {
            System.out.print(65536.0 / (double) maxSolutionDistro[spot] + " ");
        }

        return solutions[0];
    }
}
