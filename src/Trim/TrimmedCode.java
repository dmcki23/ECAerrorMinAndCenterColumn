package Trim;

import java.util.Arrays;
import java.util.Random;

public class TrimmedCode {
//    /**
//     * ??
//     * @param size
//     * @return
//     */
//
//    public int[][] doWolfram(int size) {
//        ecaBestFitHashCollisionExhuastive(150, 4, false, false, 1);
//        int fieldSize = 150;
//        int[][] field = new int[fieldSize][fieldSize];
//        int[] primeStart = new int[]{10, 0, 4, 8};
//        for (int column = 0; column < 4; column++) {
//            //field[0][column + fieldSize / 2 - 2] = primeStart[column];
//        }
//        Random rand = new Random();
//        for (int column = 0; column < fieldSize / 2; column++) {
//            field[0][column + fieldSize / 2 - 2] = rand.nextInt(0, 16);
//        }
//        for (int row = 1; row < fieldSize; row++) {
//            for (int column = row + 1; column < fieldSize - row - 2; column++) {
////                field[row][column] = field[row-1][column-1] + 16*field[row-1][column] + 256*field[row-1][column+1] + 256*16*field[row-1][column+2];
//                field[row][column] = field[row - 1][column - 2] + 16 * field[row - 1][column - 1] + 256 * field[row - 1][column] + 256 * 16 * field[row - 1][column + 1];
//
//                field[row][column] = fbfWolfram[field[row][column]];
//            }
//        }
//        System.out.println("error min wolfram on prime ca initial value");
//        for (int row = 0; row < fieldSize; row++) {
//            for (int column = 0; column < fieldSize - row; column++) {
//                if (column >= row) System.out.print(String.format("%2d", field[row][column]));
//                else System.out.print("  ");
//            }
//            System.out.print("\n");
//        }
//        return field;
//    }
//
//    /**
//     * ??
//     * @return
//     */
//    public int[][] checkCodesXor(){
//        int[] codes = new int[]{9,18,27,21};
//        int[][] table = new int[4][4];
//        for (int row = 0; row < 4; row++) {
//            for (int column = 0; column < 4; column++) {
//                table[row][column] = codes[row] ^ codes[column];
//            }
//        }
//        CustomArray.plusArrayDisplay(table,false,false,"row xor column");
//        return table;
//    }





//
//
//Some experimental voting procedures
//int[][][] subVote = new int[10][size][size];
//        for (int answer = 0; answer < 1; answer++) {
//        trialField = new int[size][size];
//        for (column = 0; column < size; column++) {
//            //System.out.println("column " + column);
//            subVote[answer][0][column] = ((esPrintList[esPrintList.length / 2] / (int) Math.pow(2, column)) % 2);
//            //subVote[answer][0][column] = ((esPrintList[rand.nextInt(0,esPrintList.length)]/ (int) Math.pow(2, column)) % 2);
//            subVote[answer][0][column] = ((minSpot / (int) Math.pow(2, column)) % 2);
//            //subVote[answer][0][column] = ((firstMinSpot / (int) Math.pow(2, column)) % 2);
//            //localMaxSolution[0][column] = ((maxSpot / (int) Math.pow(2, column)) % 2);
//            //trialField[0][column] = (((minSpot+maxSpot)/2)/(int)Math.pow(2, column))%2;
//            //trialField[0][column] = (((minSpot+firstMinSpot)/2)/(int)Math.pow(2, column))%2;
//        }
//        //columnOut[0] = field[0][size/2];
//        //calculate neighborhood
//        //System.out.println("size/2 " + (size/2));
//        //System.out.println("size/2 - input.length/2 " + (size/2-input.length/2));
//        for (row = 1; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                a = ((column - 1) + size) % size;
//                b = column;
//                c = ((column + 1)) % size;
//                subVote[answer][row][column] = subVote[answer][row - 1][a] + 2 * subVote[answer][row - 1][b] + 4 * subVote[answer][row - 1][c];
//                subVote[answer][row][column] = wolfram[subVote[answer][row][column]];
//                subVote[9][row][column] += subVote[answer][row][column];
//                ////localMaxSolution[row][column] = localMaxSolution[row - 1][a] + 2 * trialField[row - 1][b] + 4 * trialField[row - 1][c];
//                //localMaxSolution[row][column] = wolfram[localMaxSolution[row][column]];
//            }
//        }
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                //to get pi in the ratio, this localMaxSolution was trialField[]
//                minErrorMap[row][column] += (subVote[answer][row][column] ^ in[row][column]);
//                //maxErrorMap[row][column] += (localMaxSolution[row][column] ^ in[row][column]);
//            }
//        }
//    }
//        for (row = 0; row < size; row++) {
//        for (column = 0; column < size; column++) {
//            if (subVote[sameNumErrors][row][column] > (sameNumErrors / 2)) {
//                subVote[sameNumErrors][row][column] = 1;
//            } else {
//                subVote[sameNumErrors][row][column] = 0;
//            }
//            //to get pi in the ratio, this localMaxSolution was trialField[]
//            //maxErrorMap[row][column] += (subVote[sameNumErrors][row][column] ^ in[row][column]);
//            //maxErrorMap[row][column] += (localMaxSolution[row][column]^in[row][column]);
//        }
//    }
//    int[][][] subVoteMax = new int[10][size][size];
//        for (int answer = 0; answer < 1; answer++) {
//        trialField = new int[size][size];
//        neighborhoodInt = 0;
//        for (column = 0; column < size; column++) {
//            //System.out.println("column " + column);
//            //subVote[answer][0][column] = ((esPrintList[esPrintList.length / 2] / (int) Math.pow(2, column)) % 2);
//            //subVote[answer][0][column] = ((esPrintList[rand.nextInt(0,esPrintList.length)]/ (int) Math.pow(2, column)) % 2);
//            subVoteMax[answer][0][column] = ((maxSpot / (int) Math.pow(2, column)) % 2);
//            //subVote[answer][0][column] = ((firstMinSpot / (int) Math.pow(2, column)) % 2);
//            //localMaxSolution[0][column] = ((maxSpot / (int) Math.pow(2, column)) % 2);
//            //trialField[0][column] = (((minSpot+maxSpot)/2)/(int)Math.pow(2, column))%2;
//            //trialField[0][column] = (((minSpot+firstMinSpot)/2)/(int)Math.pow(2, column))%2;
//            neighborhoodInt += (int) Math.pow(2, column)*subVoteMax[answer][0][column];
//        }
//        maxSolutionDistro[neighborhoodInt]++;
//        //columnOut[0] = field[0][size/2];
//        //calculate neighborhood
//        //System.out.println("size/2 " + (size/2));
//        //System.out.println("size/2 - input.length/2 " + (size/2-input.length/2));
//        for (row = 1; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                a = ((column - 1) + size) % size;
//                b = column;
//                c = ((column + 1)) % size;
//                subVoteMax[answer][row][column] = subVoteMax[answer][row - 1][a] + 2 * subVoteMax[answer][row - 1][b] + 4 * subVoteMax[answer][row - 1][c];
//                subVoteMax[answer][row][column] = wolfram[subVoteMax[answer][row][column]];
//                subVoteMax[9][row][column] += subVoteMax[answer][row][column];
//                ////localMaxSolution[row][column] = localMaxSolution[row - 1][a] + 2 * trialField[row - 1][b] + 4 * trialField[row - 1][c];
//                //localMaxSolution[row][column] = wolfram[localMaxSolution[row][column]];
//            }
//        }
//        for (row = 0; row < size; row++) {
//            for (column = 0; column < size; column++) {
//                //to get pi in the ratio, this localMaxSolution was trialField[]
//                maxErrorMap[row][column] += (subVoteMax[answer][row][column] ^ in[row][column]);
//                //maxErrorMap[row][column] += (localMaxSolution[row][column] ^ in[row][column]);
//            }
//        }
//    }
//        for (row = 0; row < size; row++) {
//        for (column = 0; column < size; column++) {
//            if (subVoteMax[sameNumErrors][row][column] > (sameNumErrors / 2)) {
//                subVoteMax[sameNumErrors][row][column] = 1;
//            } else {
//                subVoteMax[sameNumErrors][row][column] = 0;
//            }
//            //to get pi in the ratio, this localMaxSolution was trialField[]
//            //maxErrorMap[row][column] += (subVote[sameNumErrors][row][column] ^ in[row][column]);
//            //maxErrorMap[row][column] += (localMaxSolution[row][column]^in[row][column]);
//        }
//    }



//
//    /**
//     * ???
//     *
//     * @param n       ECA 0-255 rule
//     * @param numRows number of rows
//     * @return ???
//     */
//
//    public int[] backwardsColumn(int n, int numRows) {
//        int[] out = new int[numRows];
//        int[] multiplier = new int[]{1, 2, 4};
//        int[][] partial = new int[3][3];
//        int[] next = new int[3];
//        int[] in = new int[]{7, 3, 1};
//        int tot = 0;
//        for (int row = numRows - 1; row >= 0; row--) {
//            next = new int[3];
//            for (int value = 0; value < 3; value++) {
//                for (int mult = 0; mult < 3; mult++) {
//                    partial[value][mult] = in[value] / multiplier[mult];
//                    next[value] += (int) Math.pow(2, mult) * (((n / (int) Math.pow(2, partial[value][mult])) % 2));
//                }
//                //next[value] = ((n/(int)Math.pow(2,next[value]))%2);
//                in[value] = next[value];
//                next[value] = ((n / (int) Math.pow(2, next[value])) % 2);
//                out[row] += (int) Math.pow(2, value) * next[value];
//            }
//            out[row] = ((n / (int) Math.pow(2, out[row])) % 2);
//        }
//        return out;
//    }
//
//    /**
//     * ???
//     *
//     * @param n       ECA 0-255 rule
//     * @param numRows number of rows
//     */
//    public void checkColumns(int n, int numRows) {
//        int[][][] passes = doLogReductionTwo(n, 1, 2, 4);
//        int[] input = Arrays.copyOfRange(passes[passRepeat][5], 0, 9);
//        System.out.println(Arrays.toString(input));
//        int[] a = ruleStretchTemplate.traditionalCenterColumn(n, 512, input, 1024);
//        int[] b = ruleStretchTemplate.traditionalCenterColumn(n, 512, new int[]{1}, 1024);
//        for (int spot = 0; spot < 128; spot++) {
//            if (Arrays.equals(Arrays.copyOfRange(b, spot, spot + 30), Arrays.copyOfRange(a, 1, 31))) {
//                System.out.println("spot " + spot);
//            }
//        }
//        System.out.println(Arrays.toString(a));
//        System.out.println(Arrays.toString(b));
//    }







//
//    /**
//     * ???
//     * @param rule
//     * @param maxLength
//     * @param numTrials
//     */
//
//    public void manageLengthsMinimizations(int rule, int maxLength, int numTrials) {
//        int columnsOutput = 24;
//        int[][] allData = new int[maxLength][(int) Math.pow(2, maxLength)];
//        maxDistro = new int[maxLength][(int) Math.pow(2, maxLength)];
//        for (int lengths = 4; lengths < maxLength; lengths++) {
//            sameError = new int[(int) Math.pow(2, maxLength) + 1];
//            sameErrorMax = new int[(int) Math.pow(2, maxLength) + 1];
//            ecaBestFitHashCollisionExhuastive(rule, lengths, false, false, numTrials);
//            for (int spot = 0; spot < (int) Math.pow(2, lengths); spot++) {
//                allData[lengths][spot] = sameError[spot];
//                maxDistro[lengths][spot] = sameErrorMax[spot];
//            }
//        }
//
//        System.out.println("same number of errors minimum ");
//        System.out.print("\n");
//
//        for (int lengths = 4; lengths < maxLength; lengths += 2) {
//            for (int spot = 0; spot < columnsOutput; spot++) {
//                System.out.print(allData[lengths][spot]);
//                if (spot != columnsOutput - 1) System.out.print(",");
//            }
//            if (lengths != maxLength - 2) System.out.print(";");
//            //System.out.println();
//        }
//        System.out.println("}");
//        System.out.print("\n{");
//
//        for (int lengths = 5; lengths < maxLength; lengths += 2) {
//            for (int spot = 0; spot < columnsOutput; spot++) {
//                System.out.print(allData[lengths][spot]);
//                if (spot != columnsOutput - 1) System.out.print(",");
//            }
//            if (lengths != maxLength - 1) System.out.print(";");
//            //System.out.println();
//        }
//        System.out.println("}");
//        System.out.println("same number of errors maximum ");
//        System.out.print("\n{");
//
//        for (int lengths = 4; lengths < maxLength; lengths += 2) {
//            for (int spot = 0; spot < columnsOutput; spot++) {
//                System.out.print(maxDistro[lengths][spot]);
//                if (spot != columnsOutput - 1) System.out.print(",");
//            }
//            if (lengths != maxLength - 1) System.out.print(";");
//            //System.out.println();
//        }
//        System.out.println("}");
//        System.out.print("\n{");
//
//        for (int lengths = 5; lengths < maxLength; lengths += 2) {
//            for (int spot = 0; spot < columnsOutput; spot++) {
//                System.out.print(maxDistro[lengths][spot]);
//                if (spot != columnsOutput - 1) System.out.print(",");
//            }
//            if (lengths != maxLength - 1) System.out.print(";");
//            //System.out.println();
//        }
//        System.out.println("}");
//
//    }









//
//
//    /**
//     * Finds the center column of an input neighborhood
//     *
//     * @param in      primeCA Wolfram code
//     * @param inputIn input neighborhood
//     * @param numRows number of rows to return
//     * @param size    size of output space, must be > 2*numRows
//     * @return the center column of inputIn[]'s output
//     */
//    public int[] traditionalCenterColumnLong(long[] in, long[] inputIn, int numRows, int size) {
//        //
//        //
//        //Initialization
////        int[] n = new int[in.length];
////        for (int spot = 0; spot < in.length; spot++) {
////            n[spot] = (int) in[spot];
////        }
////        int[] input = new int[inputIn.length];
////        for (int spot = 0; spot < inputIn.length; spot++) {
////            input[spot] = (int) inputIn[spot];
////        }
//        int[] columnOut = new int[numRows];
//        //code = basicECA.ruleExtension(30);
//        //System.out.println(Arrays.toString(code[5]));
//        //code[3] = new int[]{0,1,1,1,1,0,0,0};
//        long[][] field = new long[size][size];
//        //field[0][128] = 1;
//        for (int column = size / 2 - inputIn.length / 2; column <= size / 2 + inputIn.length / 2; column++) {
//            //System.out.println("column " + column);
//            field[0][column] = inputIn[column + inputIn.length / 2 - size / 2];
//        }
//        //System.out.println(Arrays.toString(field[0]));
//        columnOut[0] = (int) field[0][size / 2];
//        //calculate neighborhood
//        //System.out.println("size/2 " + (size/2));
//        //System.out.println("size/2 - input.length/2 " + (size/2-input.length/2));
//        //Run Wolfram code on input
//        int column = 0;
//        for (int row = 1; row < numRows; row++) {
//            for (column = 1; column < size - 1; column++) {
////                for (int bit = 0; bit < 3; bit++) {
////                    field[row][ccolumn] += (int) Math.pow(16, bit) * field[row - 1][ccolumn - 1 + bit];
////                }
//                field[row][column] = field[row - 1][column - 1] + 16 * field[row - 1][column] + 16 * 16 * field[row - 1][column + 1];
//                field[row][column] = in[(int) field[row][column]];
//            }
//            columnOut[row] = (int) field[row][size / 2];
//        }
//        for (int row = 0; row < 10; row++) {
//            //System.out.println(Arrays.toString(Arrays.copyOfRange(field[row],size/2-10,size/2+10)));
//        }
////        System.out.print("\n");
////        for (int row = 0; row < numRows; row++) {
////            for (int ccolumn = size/2-numRows; ccolumn < size/2+numRows; ccolumn++) {
////                System.out.print(field[row][ccolumn]);
////            }
////            System.out.print("\n");
////        }
////        System.out.print("\n");
//        return columnOut;
//    }

//    /**
//     * Manager function for checkPascalErrorCorrection()
//     *
//     * @param specificRule ECA 0-255 rule
//     * @param size         width of input array, size of solution neighborhood
//     * @return ???
//     */
//    public int[][] ecaBestFitHashCollisionExhuastive(int specificRule, int size, boolean doChangeScore, boolean doAllRules, int numTrials) {
//        //
//        //
//        //Initialization
//        CustomArray customArray = new CustomArray();
//        maxErrorMap = new int[size][size];
//        maxSolutionDistro = new int[(int) Math.pow(2, size)];
//        solutionDistro = new int[(int) Math.pow(2, size)];
//        int numBoards = (int) Math.pow(2, size * size);
//        fbfWolfram = new int[numBoards];
//        minErrorMap = new int[size][size];
//        sameSolutions = new int[256];
//        //coefficients = coefficients(size);
//        sameErrorMin = new int[(int) Math.pow(2, size) + 1];
//        sameErrorMax = new int[sameErrorMin.length];
//        int[][] field = new int[size][size];
//        int[] minErrorBuckets = new int[256];
//        int[] maxErrorBuckets = new int[256];
//        int[][][] solutions = new int[8][size][size];
//        int[] hbLength = new int[256];
//        BasicECA basicECA = new BasicECA();
//        int[] errorChange = new int[256];
//        int[] hammingChange = new int[256];
//        double[] averageChange = new double[256];
//        int[] solutionBucket = new int[256];
//        double[] changeChange = new double[256];
//        int[] changeScore = new int[256];
//        int[][] errorMap = new int[size][size];
//        int start = 0;
//        if (!doAllRules) start = specificRule;
//        int stop = 255;
//        if (!doAllRules) stop = specificRule + 1;
//        int totMaxErrors = 0;
//        int[][] vote = new int[size][size];
//        int[][] maxVote = new int[size][size];
//        int[][] maxTemp = new int[size][size];
//        //In previous versions this ran for all 0-255 ECA rules
//        for (int rule = start; rule < stop; rule++) {
//            totMaxErrors = 0;
//            errorMap = new int[size][size];
//            System.out.println("rule " + rule);
//            for (int trial = 0; trial < numBoards; trial++) {
//                if (trial % 1000 == 0) System.out.println("trial " + trial);
//                for (int row = 0; row < size; row++) {
//                    for (int column = 0; column < size; column++) {
//                        field[row][column] = ((trial / (int) Math.pow(2, size * column + row)) % 2);
//                    }
//                }
//                //CustomArray.plusArrayDisplay(field, false, true, "Field");
//                int[][] solutionsTemp = new int[size][size];
//                vote = new int[size][size];
//                maxVote = new int[size][size];
//                //This loop reflects, rotates, and transposes the input data array
//                for (int rotation = 0; rotation < 8; rotation++) {
//                    int[][] temp = new int[size][size];
//                    for (int row = 0; row < size; row++) {
//                        for (int column = 0; column < size; column++) {
//                            temp[row][column] = field[row][column];
//                        }
//                    }
//                    temp = customArray.reflectRotateTranspose(temp, rotation);
//                    temp = checkPascalErrorCorrectionAll(rule, temp);
//                    temp = customArray.reflectRotateTranspose(temp, rotation);
//                    maxTemp = localMaxSolution;
//                    //unreflects, unrotates, untransposes the solution
//                    int totErrors = 0;
//                    for (int row = 0; row < size; row++) {
//                        for (int column = 0; column < size; column++) {
//                            solutions[rotation][row][column] = temp[row][column];
//                            vote[row][column] += solutions[rotation][row][column];
//                            maxVote[row][column] += maxTemp[row][column];
//                        }
//                    }
//                }
//                for (int column = 0; column < size; column++) {
//                    fbfWolfram[trial] += (int) Math.pow(2, column) * solutions[0][0][column];
//                }
//                int totErrors = 0;
//                //totMaxErrors = 0;
//                for (int row = 0; row < size; row++) {
//                    for (int column = 0; column < size; column++) {
//                        if (vote[row][column] > 3) vote[row][column] = 1;
//                        else vote[row][column] = 0;
//                        totErrors += (vote[row][column] ^ field[row][column]);
//                        errorMap[row][column] += (vote[row][column] ^ field[row][column]);
//                        if (maxVote[row][column] > 3) maxVote[row][column] = 1;
//                        else maxVote[row][column] = 0;
//                        totMaxErrors += (maxVote[row][column] ^ field[row][column]);
//                    }
//                }
//                //solutionBucket[index]++;
//                minErrorBuckets[rule] += totErrors;
//                maxErrorBuckets[rule] += totMaxErrors;
//                totMaxErrors = 0;
//                hbLength[rule]++;
//                //
//                //
//                //Severely impacts runtime
//                //Finds the mean distance between codewords per change in the random input data
//                //the changeScore is scored with the same exponent as the errorScore
//                if (doChangeScore) {
//                    changeChange[rule] = changeChange(rule, vote, totErrors, numTrials);
//                    errorChange[rule] += totErrors;
//                    hammingChange[rule] += totHammingChange;
//                    changeScore[rule] += weightedChangeScore;
//                }
//            }
//            if (doChangeScore) changeChange[rule] = (double) hammingChange[rule] / (double) changeScore[rule];
//            //CustomArray.plusArrayDisplay(errorMap,false,false,"ECA rule " + rule + " error map");
//        }
//        //
//        //
//        //Sorts and averages the best performing ECA rules
//        System.out.println("solutionBucket[] " + solutionBucket);
//        System.out.println("pre division");
//        System.out.println(Arrays.toString(minErrorBuckets));
//        System.out.println("hbLength[]");
//        System.out.println(Arrays.toString(hbLength));
//        int[] hashbucketRate = new int[256];
//        for (int row = start; row < stop; row++) {
//            hashbucketRate[row] = minErrorBuckets[row] / hbLength[row];
//            if (doChangeScore) averageChange[row] = (double) hammingChange[row] / (double) errorChange[row];
//        }
//        System.out.println("post division");
//        System.out.println(Arrays.toString(hashbucketRate));
//        int[] sorted = new int[256];
//        for (int row = 0; row < 256; row++) {
//            sorted[row] = row;
//        }
//        if (doAllRules) {
//            for (int row = 0; row < 256; row++) {
//                for (int column = 0; column < 256; column++) {
//                    if (hashbucketRate[row] < hashbucketRate[column]) {
//                        int temp = minErrorBuckets[row];
//                        minErrorBuckets[row] = minErrorBuckets[column];
//                        minErrorBuckets[column] = temp;
//                        temp = sorted[row];
//                        sorted[row] = sorted[column];
//                        sorted[column] = temp;
//                        temp = hbLength[row];
//                        hbLength[row] = hbLength[column];
//                        hbLength[column] = temp;
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
//            System.out.println("index " + sorted[index] + " " + basicECA.ruleClasses[sorted[index]] + " " + hashbucketRate[index] + " " + minErrorBuckets[index] + " " + hbLength[index] + " ");
//        }
//        System.out.println(Arrays.toString(sameSolutions));
//        for (int row = start; row < stop; row++) {
//            if (sameSolutions[sorted[row]] == 1) {
//                System.out.println("Rule has a unique solution " + sorted[row]);
//            } else {
//                System.out.println("sameSolutions[" + sorted[row] + "] " + sameSolutions[sorted[row]]);
//            }
//        }
//        if (doChangeScore) {
//            System.out.println("hammingChange per changeScore");
//            for (int row = start; row < stop; row++) {
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
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                sameMin[row][column] = (double) sameErrorMin[row + 1] / (double) sameErrorMin[column + 1];
//                sameMax[row][column] = (double) sameErrorMax[2 * row + 2] / (double) sameErrorMax[2 * column + 2];
//            }
//        }
//        System.out.println("sameMaxRowComparisons ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", sameMax[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("sameMinRowComparisons ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", sameMin[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        for (int row = 1; row < 4096 && row < sameErrorMin.length; row *= 2) {
//            System.out.print("row " + row + " " + sameErrorMin[row] + "\n");
//        }
//        System.out.println("gateDistro[] " + Arrays.toString(gateDistro));
//        int maxErrorTot = 0;
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                maxErrorTot += minErrorMap[row][column];
//            }
//        }
//        CustomArray.plusArrayDisplay(minErrorMap, false, false, "Min Error Map");
//        CustomArray.plusArrayDisplay(maxErrorMap, false, false, "Max Error Map");
//        double[] rowTots = new double[size];
//        double phi = (1 + Math.sqrt(5)) / 2;
//        int[] maxRowTots = new int[size];
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                rowTots[row] += minErrorMap[row][column];
//                maxRowTots[row] += maxErrorMap[row][column];
//            }
//        }
//        double[][] maxRowComparisons = new double[size][size];
//        double[][] minRowComparisons = new double[size][size];
//        double[][] maxMapRate = new double[size][size];
//        double[][] minMapRate = new double[size][size];
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                maxRowComparisons[row][column] = (double) maxRowTots[row] / (double) maxRowTots[column];
//                minRowComparisons[row][column] = (double) rowTots[row] / (double) rowTots[column];
//                maxMapRate[row][column] = (double) maxErrorMap[row][column] / (double) numBoards / 8.0;
//                minMapRate[row][column] = (double) minErrorMap[row][column] / (double) numBoards / 8.0;
//            }
//        }
//        System.out.println("maxRowComparisons ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", maxRowComparisons[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("maxMapRate[][] ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", maxMapRate[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("minRowComparisons ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", minRowComparisons[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("maxMapRate[][] ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
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
//        for (int row = 0; row < 2; row++) {
//            for (int column = 0; column < size; column++) {
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
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                errors[row][column] = (double) minErrorMap[row][column] / (double) (Math.pow(2, size * size) * 8);
//                byRow[row] += errors[row][column];
//            }
//            byRow[row] /= 4;
//            byRow[row] *= (440 * 8);
//        }
//        System.out.println("errors ");
//        for (int row = 0; row < size; row++) {
//            System.out.println(Arrays.toString(errors[row]));
//        }
//        System.out.println("byRow " + Arrays.toString(byRow));
//        //doWallisProduct(50);
//        //doTriangles(50);
//        System.out.println("solutionDistro: " + Arrays.toString(solutionDistro));
//        System.out.println("maxSolutionDistro: " + Arrays.toString(maxSolutionDistro));
//        System.out.println("\n\n\n\n\n\n\n");
//        for (int spot = 0; spot < 16; spot++) {
//            System.out.print((double) maxSolutionDistro[spot] / 65536.0 + " ");
//        }
//        System.out.println();
//        for (int spot = 0; spot < 16; spot++) {
//            System.out.print(65536.0 / (double) maxSolutionDistro[spot] + " ");
//        }
//        return solutions[0];
//    }




//    /**
//     * A non-exhaustive version of ecaBestFitHashCollisionExhaustive
//     *
//     * @param specificRule ECA 0-255 rule
//     * @param size         width of input array, size of solution neighborhood
//     * @return ???
//     */
//    public int[][] ecaBestFitHashCollision(int specificRule, int size, boolean doChangeScore, boolean doAllRules, int numTrials) {
//        //
//        //
//        //Initialization
//        CustomArray customArray = new CustomArray();
//        maxErrorMap = new int[size][size];
//        maxSolutionDistro = new int[(int) Math.pow(2, size)];
//        solutionDistro = new int[(int) Math.pow(2, size)];
//        int numBoards = (int) Math.pow(2, size * size);
//        fbfWolfram = new int[numBoards];
//        minErrorMap = new int[size][size];
//        sameSolutions = new int[256];
//        //coefficients = coefficients(size);
//        sameErrorMin = new int[(int) Math.pow(2, size) + 1];
//        sameErrorMax = new int[sameErrorMin.length];
//        int[][] field = new int[size][size];
//        int[] hashbucket = new int[256];
//        int[] hashbucketMax = new int[256];
//        int[][][] solutions = new int[8][size][size];
//        int[] hbLength = new int[256];
//        BasicECA basicECA = new BasicECA();
//        int[] errorChange = new int[256];
//        int[] hammingChange = new int[256];
//        double[] averageChange = new double[256];
//        int[] solutionBucket = new int[256];
//        double[] changeChange = new double[256];
//        int[] changeScore = new int[256];
//        int[][] errorMap = new int[size][size];
//        int start = 0;
//        if (!doAllRules) start = specificRule;
//        int stop = 255;
//        if (!doAllRules) stop = specificRule + 1;
//        int totMaxErrors = 0;
//        int[][] vote = new int[size][size];
//        int[][] maxVote = new int[size][size];
//        int[][] maxTemp = new int[size][size];
//        Random rand = new Random();
//        //In previous versions this ran for all 0-255 ECA rules
//        for (int rule = start; rule < stop; rule++) {
//            totMaxErrors = 0;
//            errorMap = new int[size][size];
//            System.out.println("rule " + rule);
//            for (int trial = 0; trial < numTrials; trial++) {
//                if (trial % 100 == 0) System.out.println("trial " + trial);
//                //Creates a random binary array
//                for (int row = 0; row < size; row++) {
//                    for (int column = 0; column < size; column++) {
//                        //field[row][column] = ((trial / (int) Math.pow(2, size * column + row)) % 2);
//                        field[row][column] = rand.nextInt(0, 2);
//                    }
//                }
//                //CustomArray.plusArrayDisplay(field, false, true, "Field");
//                int[][] solutionsTemp = new int[size][size];
//                vote = new int[size][size];
//                maxVote = new int[size][size];
//                //This loop reflects, rotates, and transposes the input data array
//                for (int rotation = 0; rotation < 8; rotation++) {
//                    int[][] temp = new int[size][size];
//                    for (int row = 0; row < size; row++) {
//                        for (int column = 0; column < size; column++) {
//                            temp[row][column] = field[row][column];
//                        }
//                    }
//                    temp = customArray.reflectRotateTranspose(temp, rotation);
//                    temp = checkPascalErrorCorrectionAll(rule, temp);
//                    temp = customArray.reflectRotateTranspose(temp, rotation);
//                    maxTemp = localMaxSolution;
//                    //unreflects, unrotates, untransposes the solution
//                    int totErrors = 0;
//                    for (int row = 0; row < size; row++) {
//                        for (int column = 0; column < size; column++) {
//                            solutions[rotation][row][column] = temp[row][column];
//                            vote[row][column] += solutions[rotation][row][column];
//                            maxVote[row][column] += maxTemp[row][column];
//                        }
//                    }
//                }
//                for (int column = 0; column < size; column++) {
//                    fbfWolfram[trial] += (int) Math.pow(2, column) * solutions[0][0][column];
//                }
//                int totErrors = 0;
//                //totMaxErrors = 0;
//                for (int row = 0; row < size; row++) {
//                    for (int column = 0; column < size; column++) {
//                        if (vote[row][column] > 3) vote[row][column] = 1;
//                        else vote[row][column] = 0;
//                        totErrors += (vote[row][column] ^ field[row][column]);
//                        errorMap[row][column] += (vote[row][column] ^ field[row][column]);
//                        if (maxVote[row][column] > 3) maxVote[row][column] = 1;
//                        else maxVote[row][column] = 0;
//                        totMaxErrors += (maxVote[row][column] ^ field[row][column]);
//                    }
//                }
//                //solutionBucket[index]++;
//                hashbucket[rule] += totErrors;
//                hashbucketMax[rule] += totMaxErrors;
//                totMaxErrors = 0;
//                hbLength[rule]++;
//                //
//                //
//                //Severely impacts runtime
//                //Finds the mean distance between codewords per change in the random input data
//                //the changeScore is scored with the same exponent as the errorScore
//                if (doChangeScore) {
//                    changeChange[rule] = changeChange(rule, vote, totErrors, numTrials);
//                    errorChange[rule] += totErrors;
//                    hammingChange[rule] += totHammingChange;
//                    changeScore[rule] += weightedChangeScore;
//                }
//            }
//            if (doChangeScore) changeChange[rule] = (double) hammingChange[rule] / (double) changeScore[rule];
//            //CustomArray.plusArrayDisplay(errorMap,false,false,"ECA rule " + rule + " error map");
//        }
//        //
//        //
//        //Sorts and averages the best performing ECA rules
//        System.out.println("solutionBucket[] " + solutionBucket);
//        System.out.println("pre division");
//        System.out.println(Arrays.toString(hashbucket));
//        System.out.println("hbLength[]");
//        System.out.println(Arrays.toString(hbLength));
//        int[] hashbucketRate = new int[256];
//        for (int row = start; row < stop; row++) {
//            hashbucketRate[row] = hashbucket[row] / hbLength[row];
//            if (doChangeScore) averageChange[row] = (double) hammingChange[row] / (double) errorChange[row];
//        }
//        System.out.println("post division");
//        System.out.println(Arrays.toString(hashbucketRate));
//        int[] sorted = new int[256];
//        for (int row = 0; row < 256; row++) {
//            sorted[row] = row;
//        }
//        if (doAllRules) {
//            for (int row = 0; row < 256; row++) {
//                for (int column = 0; column < 256; column++) {
//                    if (hashbucketRate[row] < hashbucketRate[column]) {
//                        int temp = hashbucket[row];
//                        hashbucket[row] = hashbucket[column];
//                        hashbucket[column] = temp;
//                        temp = sorted[row];
//                        sorted[row] = sorted[column];
//                        sorted[column] = temp;
//                        temp = hbLength[row];
//                        hbLength[row] = hbLength[column];
//                        hbLength[column] = temp;
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
//            System.out.println("index " + sorted[index] + " " + basicECA.ruleClasses[sorted[index]] + " " + hashbucketRate[index] + " " + hashbucket[index] + " " + hbLength[index] + " ");
//        }
//        System.out.println(Arrays.toString(sameSolutions));
//        for (int row = start; row < stop; row++) {
//            if (sameSolutions[sorted[row]] == 1) {
//                System.out.println("Rule has a unique solution " + sorted[row]);
//            } else {
//                System.out.println("sameSolutions[" + sorted[row] + "] " + sameSolutions[sorted[row]]);
//            }
//        }
//        if (doChangeScore) {
//            System.out.println("hammingChange per changeScore");
//            for (int row = start; row < stop; row++) {
//                System.out.println(sorted[row] + " " + changeChange[sorted[row]] + " " + errorChange[sorted[row]]);
//            }
//        }
//        System.out.println("min error/grid " + ((double) hashbucket[specificRule] / (double) numBoards));
//        System.out.println("min error/bit " + ((double) hashbucket[specificRule]) / numBoards / size / size);
//        System.out.println("min bits/error " + (double) (numBoards * size * size) / (double) hashbucket[specificRule]);
//        System.out.println("sameError " + Arrays.toString(sameErrorMin));
//        System.out.println("sameErrorMax " + Arrays.toString(sameErrorMax));
//        System.out.println("max error/grid " + ((double) hashbucketMax[specificRule] / (double) numBoards));
//        System.out.println("max error/bit " + ((double) hashbucketMax[specificRule]) / numBoards / size / size);
//        System.out.println("max bits/error " + (double) (numBoards * size * size) / (double) hashbucketMax[specificRule]);
//        System.out.println("\n");
//        double max = ((double) hashbucketMax[specificRule]) / numBoards / size / size;
//        double min = ((double) hashbucket[specificRule]) / numBoards / size / size;
//        double ebDiff = ((double) hashbucketMax[specificRule] / numBoards / size / size) - ((double) hashbucket[specificRule] / numBoards / size / size);
//        double beDiff = ((double) (numTrials * size * size) / (double) hashbucketMax[specificRule]) - ((double) (numTrials * size * size) / (double) hashbucket[specificRule]);
//        System.out.println("ebDiff " + ebDiff);
//        System.out.println("beDiff " + beDiff);
//        System.out.println("max * min = " + (max * min));
//        double[][] sameMin = new double[size][size];
//        double[][] sameMax = new double[size][size];
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                sameMin[row][column] = (double) sameErrorMin[row + 1] / (double) sameErrorMin[column + 1];
//                sameMax[row][column] = (double) sameErrorMax[2 * row + 2] / (double) sameErrorMax[2 * column + 2];
//            }
//        }
//        System.out.println("sameMaxRowComparisons ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", sameMax[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("sameMinRowComparisons ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", sameMin[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        for (int row = 1; row < 4096 && row < sameErrorMin.length; row *= 2) {
//            System.out.print("row " + row + " " + sameErrorMin[row] + "\n");
//        }
//        System.out.println("gateDistro[] " + Arrays.toString(gateDistro));
//        int maxErrorTot = 0;
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                maxErrorTot += minErrorMap[row][column];
//            }
//        }
//        CustomArray.plusArrayDisplay(minErrorMap, false, false, "Min Error Map");
//        CustomArray.plusArrayDisplay(maxErrorMap, false, false, "Max Error Map");
//        double[] rowTots = new double[size];
//        double phi = (1 + Math.sqrt(5)) / 2;
//        int[] maxRowTots = new int[size];
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                rowTots[row] += minErrorMap[row][column];
//                maxRowTots[row] += maxErrorMap[row][column];
//            }
//        }
//        double[][] maxRowComparisons = new double[size][size];
//        double[][] minRowComparisons = new double[size][size];
//        double[][] maxMapRate = new double[size][size];
//        double[][] minMapRate = new double[size][size];
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                maxRowComparisons[row][column] = (double) maxRowTots[row] / (double) maxRowTots[column];
//                minRowComparisons[row][column] = (double) rowTots[row] / (double) rowTots[column];
//                maxMapRate[row][column] = (double) maxErrorMap[row][column] / (double) numBoards / 8.0;
//                minMapRate[row][column] = (double) minErrorMap[row][column] / (double) numBoards / 8.0;
//            }
//        }
//        System.out.println("maxRowComparisons ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", maxRowComparisons[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("maxMapRate[][] ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", maxMapRate[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("minRowComparisons ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", minRowComparisons[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("maxMapRate[][] ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
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
//        for (int row = 0; row < 2; row++) {
//            for (int column = 0; column < size; column++) {
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
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                errors[row][column] = (double) minErrorMap[row][column] / (double) (Math.pow(2, size * size) * 8);
//                byRow[row] += errors[row][column];
//            }
//            byRow[row] /= 4;
//            byRow[row] *= (440 * 8);
//        }
//        System.out.println("errors ");
//        for (int row = 0; row < size; row++) {
//            System.out.println(Arrays.toString(errors[row]));
//        }
//        System.out.println("byRow " + Arrays.toString(byRow));
//        //doWallisProduct(50);
//        //doTriangles(50);
//        System.out.println("solutionDistro: " + Arrays.toString(solutionDistro));
//        System.out.println("maxSolutionDistro: " + Arrays.toString(maxSolutionDistro));
//        System.out.println("\n\n\n\n\n\n\n");
//        for (int spot = 0; spot < 16; spot++) {
//            System.out.print((double) maxSolutionDistro[spot] / 65536.0 + " ");
//        }
//        System.out.println();
//        for (int spot = 0; spot < 16; spot++) {
//            System.out.print(65536.0 / (double) maxSolutionDistro[spot] + " ");
//        }
//        return solutions[0];
//    }


//
//    /**
//     * Manager function for checkPascalErrorCorrection()
//     *
//     * @param specificRule ECA 0-255 rule
//     * @param size         width of input array, size of solution neighborhood
//     * @return ???
//     */
//    public int[][] ecaBestFitHashCollisionExhuastiveSizeTwo(int specificRule, int size, boolean doChangeScore, boolean doAllRules, int numTrials) {
//        //
//        //
//        //Initialization
//        CustomArray customArray = new CustomArray();
//        maxErrorMap = new int[size][size];
//        maxSolutionDistro = new int[(int) Math.pow(2, size)];
//        solutionDistro = new int[(int) Math.pow(2, size)];
//        int numBoards = (int) Math.pow(2, size * size);
//        fbfWolfram = new int[numBoards];
//        minErrorMap = new int[size][size];
//        sameSolutions = new int[256];
//        //coefficients = coefficients(size);
//        sameErrorMin = new int[(int) Math.pow(2, size) + 1];
//        sameErrorMax = new int[sameErrorMin.length];
//        int[][] field = new int[size][size];
//        int[] hashbucket = new int[256];
//        int[] hashbucketMax = new int[256];
//        int[][][] solutions = new int[8][size][size];
//        int[] hbLength = new int[256];
//        BasicECA basicECA = new BasicECA();
//        int[] errorChange = new int[256];
//        int[] hammingChange = new int[256];
//        double[] averageChange = new double[256];
//        int[] solutionBucket = new int[256];
//        double[] changeChange = new double[256];
//        int[] changeScore = new int[256];
//        int[][] errorMap = new int[size][size];
//        int start = 0;
//        if (!doAllRules) start = specificRule;
//        int stop = 255;
//        if (!doAllRules) stop = specificRule + 1;
//        int totMaxErrors = 0;
//        int[][] vote = new int[size][size];
//        int[][] maxVote = new int[size][size];
//        int[][] maxTemp = new int[size][size];
//        //In previous versions this ran for all 0-255 ECA rules
//        for (int rule = start; rule < stop; rule++) {
//            totMaxErrors = 0;
//            errorMap = new int[size][size];
//            System.out.println("rule " + rule);
//            for (int trial = 0; trial < numBoards; trial++) {
//                if (trial % 100 == 0) System.out.println("trial " + trial);
//                //Creates a random binary array
//                for (int row = 0; row < size; row++) {
//                    for (int column = 0; column < size; column++) {
//                        field[row][column] = ((trial / (int) Math.pow(2, size * column + row)) % 2);
//                    }
//                }
//                //CustomArray.plusArrayDisplay(field, false, true, "Field");
//                int[][] solutionsTemp = new int[size][size];
//                vote = new int[size][size];
//                maxVote = new int[size][size];
//                //This loop reflects, rotates, and transposes the input data array
//                for (int rotation = 0; rotation < 8; rotation++) {
//                    int[][] temp = new int[size][size];
//                    for (int row = 0; row < size; row++) {
//                        for (int column = 0; column < size; column++) {
//                            temp[row][column] = field[row][column];
//                        }
//                    }
//                    temp = customArray.reflectRotateTranspose(temp, rotation);
//                    temp = checkPascalErrorCorrectionAll(rule, temp);
//                    temp = customArray.reflectRotateTranspose(temp, rotation);
//                    maxTemp = localMaxSolution;
//                    //unreflects, unrotates, untransposes the solution
//                    int totErrors = 0;
//                    for (int row = 0; row < size; row++) {
//                        for (int column = 0; column < size; column++) {
//                            solutions[rotation][row][column] = temp[row][column];
//                            vote[row][column] += solutions[rotation][row][column];
//                            maxVote[row][column] += maxTemp[row][column];
//                        }
//                    }
//                }
//                for (int column = 0; column < size; column++) {
//                    fbfWolfram[trial] += (int) Math.pow(2, column) * solutions[0][0][column];
//                }
//                int totErrors = 0;
//                //totMaxErrors = 0;
//                for (int row = 0; row < size; row++) {
//                    for (int column = 0; column < size; column++) {
//                        if (vote[row][column] > 3) vote[row][column] = 1;
//                        else vote[row][column] = 0;
//                        totErrors += (vote[row][column] ^ field[row][column]);
//                        errorMap[row][column] += (vote[row][column] ^ field[row][column]);
//                        if (maxVote[row][column] > 3) maxVote[row][column] = 1;
//                        else maxVote[row][column] = 0;
//                        totMaxErrors += (maxVote[row][column] ^ field[row][column]);
//                    }
//                }
//                //solutionBucket[index]++;
//                hashbucket[rule] += totErrors;
//                hashbucketMax[rule] += totMaxErrors;
//                totMaxErrors = 0;
//                hbLength[rule]++;
//                //
//                //
//                //Severely impacts runtime
//                //Finds the mean distance between codewords per change in the random input data
//                //the changeScore is scored with the same exponent as the errorScore
//                if (doChangeScore) {
//                    changeChange[rule] = changeChange(rule, vote, totErrors, numTrials);
//                    errorChange[rule] += totErrors;
//                    hammingChange[rule] += totHammingChange;
//                    changeScore[rule] += weightedChangeScore;
//                }
//            }
//            if (doChangeScore) changeChange[rule] = (double) hammingChange[rule] / (double) changeScore[rule];
//            //CustomArray.plusArrayDisplay(errorMap,false,false,"ECA rule " + rule + " error map");
//        }
//        //
//        //
//        //Sorts and averages the best performing ECA rules
//        System.out.println("solutionBucket[] " + solutionBucket);
//        System.out.println("pre division");
//        System.out.println(Arrays.toString(hashbucket));
//        System.out.println("hbLength[]");
//        System.out.println(Arrays.toString(hbLength));
//        int[] hashbucketRate = new int[256];
//        for (int row = start; row < stop; row++) {
//            hashbucketRate[row] = hashbucket[row] / hbLength[row];
//            if (doChangeScore) averageChange[row] = (double) hammingChange[row] / (double) errorChange[row];
//        }
//        System.out.println("post division");
//        System.out.println(Arrays.toString(hashbucketRate));
//        int[] sorted = new int[256];
//        for (int row = 0; row < 256; row++) {
//            sorted[row] = row;
//        }
//        if (doAllRules) {
//            for (int row = 0; row < 256; row++) {
//                for (int column = 0; column < 256; column++) {
//                    if (hashbucketRate[row] < hashbucketRate[column]) {
//                        int temp = hashbucket[row];
//                        hashbucket[row] = hashbucket[column];
//                        hashbucket[column] = temp;
//                        temp = sorted[row];
//                        sorted[row] = sorted[column];
//                        sorted[column] = temp;
//                        temp = hbLength[row];
//                        hbLength[row] = hbLength[column];
//                        hbLength[column] = temp;
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
//            System.out.println("index " + sorted[index] + " " + basicECA.ruleClasses[sorted[index]] + " " + hashbucketRate[index] + " " + hashbucket[index] + " " + hbLength[index] + " ");
//        }
//        System.out.println(Arrays.toString(sameSolutions));
//        for (int row = start; row < stop; row++) {
//            if (sameSolutions[sorted[row]] == 1) {
//                System.out.println("Rule has a unique solution " + sorted[row]);
//            } else {
//                System.out.println("sameSolutions[" + sorted[row] + "] " + sameSolutions[sorted[row]]);
//            }
//        }
//        if (doChangeScore) {
//            System.out.println("hammingChange per changeScore");
//            for (int row = start; row < stop; row++) {
//                System.out.println(sorted[row] + " " + changeChange[sorted[row]] + " " + errorChange[sorted[row]]);
//            }
//        }
//        System.out.println("min error/grid " + ((double) hashbucket[specificRule] / (double) numBoards));
//        System.out.println("min error/bit " + ((double) hashbucket[specificRule]) / numBoards / size / size);
//        System.out.println("min bits/error " + (double) (numBoards * size * size) / (double) hashbucket[specificRule]);
//        System.out.println("sameError " + Arrays.toString(sameErrorMin));
//        System.out.println("sameErrorMax " + Arrays.toString(sameErrorMax));
//        System.out.println("max error/grid " + ((double) hashbucketMax[specificRule] / (double) numBoards));
//        System.out.println("max error/bit " + ((double) hashbucketMax[specificRule]) / numBoards / size / size);
//        System.out.println("max bits/error " + (double) (numBoards * size * size) / (double) hashbucketMax[specificRule]);
//        System.out.println("\n");
//        double max = ((double) hashbucketMax[specificRule]) / numBoards / size / size;
//        double min = ((double) hashbucket[specificRule]) / numBoards / size / size;
//        double ebDiff = ((double) hashbucketMax[specificRule] / numBoards / size / size) - ((double) hashbucket[specificRule] / numBoards / size / size);
//        double beDiff = ((double) (numTrials * size * size) / (double) hashbucketMax[specificRule]) - ((double) (numTrials * size * size) / (double) hashbucket[specificRule]);
//        System.out.println("ebDiff " + ebDiff);
//        System.out.println("beDiff " + beDiff);
//        System.out.println("max * min = " + (max * min));
//        double[][] sameMin = new double[size][size];
//        double[][] sameMax = new double[size][size];
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                sameMin[row][column] = (double) sameErrorMin[row + 1] / (double) sameErrorMin[column + 1];
//                sameMax[row][column] = (double) sameErrorMax[2 * row + 2] / (double) sameErrorMax[2 * column + 2];
//            }
//        }
//        System.out.println("sameMaxRowComparisons ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", sameMax[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("sameMinRowComparisons ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", sameMin[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        for (int row = 1; row < 4096 && row < sameErrorMin.length; row *= 2) {
//            System.out.print("row " + row + " " + sameErrorMin[row] + "\n");
//        }
//        System.out.println("gateDistro[] " + Arrays.toString(gateDistro));
//        int maxErrorTot = 0;
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                maxErrorTot += minErrorMap[row][column];
//            }
//        }
//        CustomArray.plusArrayDisplay(minErrorMap, false, false, "Min Error Map");
//        CustomArray.plusArrayDisplay(maxErrorMap, false, false, "Max Error Map");
//        double[] rowTots = new double[size];
//        double phi = (1 + Math.sqrt(5)) / 2;
//        int[] maxRowTots = new int[size];
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                rowTots[row] += minErrorMap[row][column];
//                maxRowTots[row] += maxErrorMap[row][column];
//            }
//        }
//        double[][] maxRowComparisons = new double[size][size];
//        double[][] minRowComparisons = new double[size][size];
//        double[][] maxMapRate = new double[size][size];
//        double[][] minMapRate = new double[size][size];
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                maxRowComparisons[row][column] = (double) maxRowTots[row] / (double) maxRowTots[column];
//                minRowComparisons[row][column] = (double) rowTots[row] / (double) rowTots[column];
//                maxMapRate[row][column] = (double) maxErrorMap[row][column] / (double) numBoards / 8.0;
//                minMapRate[row][column] = (double) minErrorMap[row][column] / (double) numBoards / 8.0;
//            }
//        }
//        System.out.println("maxRowComparisons ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", maxRowComparisons[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("maxMapRate[][] ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", maxMapRate[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("minRowComparisons ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", minRowComparisons[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("maxMapRate[][] ");
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                System.out.print(String.format("%.4f", minMapRate[row][column]) + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("(1,0) - PI/3" + ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0));
//        //System.out.println("(3,2) - PI " + ((double) rowTots[2] / (double) rowTots[3] - Math.PI));
//        double diff = ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0);
//        diff = Math.log(diff) / Math.log(2);
//        System.out.println("diff " + diff);
//        //diff = ((double) rowTots[2] / (double) rowTots[3] - Math.PI);
//        //diff = Math.log(diff) / Math.log(2);
//        System.out.println("diff " + diff);
//        int upper = 0;
//        int lower = 0;
//        for (int row = 0; row < 2; row++) {
//            for (int column = 0; column < size; column++) {
//                upper += minErrorMap[row][column];
////                lower += minErrorMap[row + 2][column];
//            }
//        }
//        double div = (double) upper / (double) lower;
//        System.out.println("upper/lower " + div + " " + 1.0 / div + " " + div * Math.PI + " " + Math.PI / div + " " + div / Math.PI);
//        System.out.println("upper/lower - phi*phi " + (div - phi * phi));
//        double[] diffs = new double[]{((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0), 0, (div - phi * phi)};
////        double[] diffs = new double[]{((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0), ((double) rowTots[2] / (double) rowTots[3] - Math.PI), (div - phi * phi)};
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
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                errors[row][column] = (double) minErrorMap[row][column] / (double) (Math.pow(2, size * size) * 8);
//                byRow[row] += errors[row][column];
//            }
//            byRow[row] /= 4;
//            byRow[row] *= (440 * 8);
//        }
//        System.out.println("errors ");
//        for (int row = 0; row < size; row++) {
//            System.out.println(Arrays.toString(errors[row]));
//        }
//        System.out.println("byRow " + Arrays.toString(byRow));
//        //doWallisProduct(50);
//        //doTriangles(50);
//        System.out.println("solutionDistro: " + Arrays.toString(solutionDistro));
//        System.out.println("maxSolutionDistro: " + Arrays.toString(maxSolutionDistro));
//        System.out.println("\n\n\n\n\n\n\n");
//        for (int spot = 0; spot < 16; spot++) {
//            System.out.print((double) maxSolutionDistro[spot] / 65536.0 + " ");
//        }
//        System.out.println();
//        for (int spot = 0; spot < 16; spot++) {
//            System.out.print(65536.0 / (double) maxSolutionDistro[spot] + " ");
//        }
//        return solutions[0];
//    }










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



//    /**
//     * Manager function for checkPascalErrorCorrection()
//     *
//     * @param specificRule ECA 0-255 rule
//     * @param size         width of input array, size of solution neighborhood
//     * @return ???
//     */
//    public int[][] errorMinimizationManager(int specificRule, int size, boolean doChangeScore, int changeScoreTrials, boolean doAllRules, boolean doRandom, int numTrials, boolean doVoting) {
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //Initialization
//        CustomArray customArray = new CustomArray();
//        maxSolutionDistro = new int[(int) Math.pow(2, size)];
//        minSolutionDistro = new int[(int) Math.pow(2, size)];
//        int numBoards = (int) Math.pow(2, size * size);
//        if (doRandom) numBoards = numTrials;
//        //Turns solutions into a Wolfram code, with the address being the input array (trial)
//        //Currently only for one ECA rule, and does not work with doRandom, and does not work for size > 4 because the Wolfram code becomes too long (4x4 array = 2^16 length)
//        minSolutionsAsWolfram = new int[256][numBoards];
//        minErrorMap = new int[256][size][size];
//        maxErrorMap = new int[256][size][size];
//        sameSolutions = new int[256];
//        sameErrorMin = new int[256][(int) Math.pow(2, size) + 1];
//        sameErrorMax = new int[256][sameErrorMin.length];
//        field = new int[size][size];
//        minErrorBuckets = new int[256];
//        maxErrorBuckets = new int[256];
//        solutions = new int[8][size][size];
//        numberBoards = new int[256];
//        errorChange = new int[256];
//        hammingChange = new int[256];
//        averageChange = new double[256];
//        solutionBucket = new int[256];
//        changeChange = new double[256];
//        changeScore = new int[256];
//        maxVote = new int[size][size];
//        minVote = new int[size][size];
//        maxTemp = new int[size][size];
//        minTemp = new int[size][size];
//        int start = 0;
//        if (!doAllRules) start = specificRule;
//        int stop = 255;
//        if (!doAllRules) stop = specificRule + 1;
//        int numVotes = 1;
//        if (doVoting) numVotes = 8;
//        Random rand = new Random();
//        int trial;
//        int row;
//        int column;
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //Main loop
//        for (int rule = start; rule < stop; rule++) {
//            System.out.println("rule " + rule);
//            for (trial = 0; trial < numBoards; trial++) {
//                if (trial % 1000 == 0) System.out.println("trial " + trial);
//                if (!doRandom) {
//                    for (row = 0; row < size; row++) {
//                        for (column = 0; column < size; column++) {
//                            field[row][column] = ((trial / (int) Math.pow(2, size * column + row)) % 2);
//                        }
//                    }
//                } else {
//                    for (row = 0; row < size; row++) {
//                        for (column = 0; column < size; column++) {
//                            field[row][column] = rand.nextInt(0, 2);
//                        }
//                    }
//                }
//                //CustomArray.plusArrayDisplay(field, false, true, "Field");
//                minVote = new int[size][size];
//                maxVote = new int[size][size];
//                //This loop reflects, rotates, and transposes the input data array
//                for (int rotation = 0; rotation < numVotes; rotation++) {
//                    minTemp = new int[size][size];
//                    for (row = 0; row < size; row++) {
//                        for (column = 0; column < size; column++) {
//                            minTemp[row][column] = field[row][column];
//                        }
//                    }
//                    minTemp = customArray.reflectRotateTranspose(minTemp, rotation);
//                    minTemp = checkPascalErrorCorrectionAll(rule, minTemp, basicECA.ecaWolframCodes[rule]);
//                    minTemp = customArray.reflectRotateTranspose(minTemp, rotation);
//                    maxTemp = localMaxSolution;
//                    for (row = 0; row < size; row++) {
//                        for (column = 0; column < size; column++) {
//                            minVote[row][column] += minTemp[row][column];
//                            maxVote[row][column] += maxTemp[row][column];
//                        }
//                    }
//                }
//                for (column = 0; column < size && !doRandom; column++) {
//                    minSolutionsAsWolfram[rule][trial] += (int) Math.pow(2, column) * minTemp[0][column];
//                    maxSolutionsAsWolfram[rule][trial] += (int) Math.pow(2, column) * maxTemp[0][column];
//                }
//                totMinErrors = 0;
//                totMaxErrors = 0;
//                for (row = 0; row < size; row++) {
//                    for (column = 0; column < size; column++) {
//                        if (minVote[row][column] > 3) minVote[row][column] = 1;
//                        else minVote[row][column] = 0;
//                        totMinErrors += (minVote[row][column] ^ field[row][column]);
//                        minErrorMap[rule][row][column] += (minVote[row][column] ^ field[row][column]);
//                        if (maxVote[row][column] > 3) maxVote[row][column] = 1;
//                        else maxVote[row][column] = 0;
//                        totMaxErrors += (maxVote[row][column] ^ field[row][column]);
//                        maxErrorMap[rule][row][column] += (maxVote[row][column] ^ field[row][column]);
//                    }
//                }
//                minErrorBuckets[rule] += totMinErrors;
//                maxErrorBuckets[rule] += totMaxErrors;
//                numberBoards[rule]++;
//                //
//                //
//                //Severely impacts runtime
//                //Finds the mean distance between codewords per change in the random input data
//                //the changeScore is scored with the same exponent as the errorScore
//                if (doChangeScore) {
//                    changeChange[rule] = changeChange(rule, minVote, totMinErrors, changeScoreTrials);
//                    errorChange[rule] += totMinErrors;
//                    hammingChange[rule] += totHammingChange;
//                    changeScore[rule] += weightedChangeScore;
//                }
//            }
//            if (doChangeScore) changeChange[rule] = (double) hammingChange[rule] / (double) changeScore[rule];
//            //CustomArray.plusArrayDisplay(errorMap,false,false,"ECA rule " + rule + " error map");
//        }
////        //The rest is results management & display
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //Sorts and averages the best performing ECA rules
////        System.out.println("solutionBucket[] " + solutionBucket);
////        System.out.println("pre division");
////        System.out.println(Arrays.toString(minErrorBuckets));
////        System.out.println("hbLength[]");
////        System.out.println(Arrays.toString(numberBoards));
////        int[] hashbucketRate = new int[256];
////        for (row = start; row < stop; row++) {
////            hashbucketRate[row] = minErrorBuckets[row] / numberBoards[row];
////            if (doChangeScore) averageChange[row] = (double) hammingChange[row] / (double) errorChange[row];
////        }
////        System.out.println("post division");
////        System.out.println(Arrays.toString(hashbucketRate));
////        int[] sorted = new int[256];
////        for (row = 0; row < 256; row++) {
////            sorted[row] = row;
////        }
////        if (doAllRules) {
////            for (row = 0; row < 256; row++) {
////                for (column = 0; column < 256; column++) {
////                    if (hashbucketRate[row] < hashbucketRate[column]) {
////                        int temp = minErrorBuckets[row];
////                        minErrorBuckets[row] = minErrorBuckets[column];
////                        minErrorBuckets[column] = temp;
////                        temp = sorted[row];
////                        sorted[row] = sorted[column];
////                        sorted[column] = temp;
////                        temp = numberBoards[row];
////                        numberBoards[row] = numberBoards[column];
////                        numberBoards[column] = temp;
////                        temp = hashbucketRate[row];
////                        hashbucketRate[row] = hashbucketRate[column];
////                        hashbucketRate[column] = temp;
////                    }
////                }
////            }
////        }
////        // System.out.println(Arrays.toString(hashbucket));
////        //
////        //
////        //Output
////        System.out.println("Here");
////        for (int index = start; index < stop; index++) {
////            System.out.println("index " + sorted[index] + " " + basicECA.ruleClasses[sorted[index]] + " " + hashbucketRate[index] + " " + minErrorBuckets[index] + " " + numberBoards[index] + " ");
////        }
////        System.out.println(Arrays.toString(sameSolutions));
////        for (row = start; row < stop; row++) {
////            if (sameSolutions[sorted[row]] == 1) {
////                System.out.println("Rule has a unique solution " + sorted[row]);
////            } else {
////                System.out.println("sameSolutions[" + sorted[row] + "] " + sameSolutions[sorted[row]]);
////            }
////        }
////        if (doChangeScore) {
////            System.out.println("hammingChange per changeScore");
////            for (row = start; row < stop; row++) {
////                System.out.println(sorted[row] + " " + changeChange[sorted[row]] + " " + errorChange[sorted[row]]);
////            }
////        }
////        System.out.println("min error/grid " + ((double) minErrorBuckets[specificRule] / (double) numBoards));
////        System.out.println("min error/bit " + ((double) minErrorBuckets[specificRule]) / numBoards / size / size);
////        System.out.println("min bits/error " + (double) (numBoards * size * size) / (double) minErrorBuckets[specificRule]);
////        System.out.println("sameError " + Arrays.toString(sameErrorMin));
////        System.out.println("sameErrorMax " + Arrays.toString(sameErrorMax));
////        System.out.println("max error/grid " + ((double) maxErrorBuckets[specificRule] / (double) numBoards));
////        System.out.println("max error/bit " + ((double) maxErrorBuckets[specificRule]) / numBoards / size / size);
////        System.out.println("max bits/error " + (double) (numBoards * size * size) / (double) maxErrorBuckets[specificRule]);
////        System.out.println("\n");
////        double max = ((double) maxErrorBuckets[specificRule]) / numBoards / size / size;
////        double min = ((double) minErrorBuckets[specificRule]) / numBoards / size / size;
////        double ebDiff = ((double) maxErrorBuckets[specificRule] / numBoards / size / size) - ((double) minErrorBuckets[specificRule] / numBoards / size / size);
////        double beDiff = ((double) (numTrials * size * size) / (double) maxErrorBuckets[specificRule]) - ((double) (numTrials * size * size) / (double) minErrorBuckets[specificRule]);
////        System.out.println("ebDiff " + ebDiff);
////        System.out.println("beDiff " + beDiff);
////        System.out.println("max * min = " + (max * min));
////        double[][] sameMin = new double[size][size];
////        double[][] sameMax = new double[size][size];
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                sameMin[row][column] = (double) sameErrorMin[row + 1] / (double) sameErrorMin[column + 1];
////                sameMax[row][column] = (double) sameErrorMax[2 * row + 2] / (double) sameErrorMax[2 * column + 2];
////            }
////        }
////        System.out.println("sameMaxRowComparisons ");
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                System.out.print(String.format("%.4f", sameMax[row][column]) + " ");
////            }
////            System.out.print("\n");
////        }
////        System.out.println("sameMinRowComparisons ");
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                System.out.print(String.format("%.4f", sameMin[row][column]) + " ");
////            }
////            System.out.print("\n");
////        }
////        for (row = 1; row < 4096 && row < sameErrorMin.length; row *= 2) {
////            System.out.print("row " + row + " " + sameErrorMin[row] + "\n");
////        }
////        int maxErrorTot = 0;
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                maxErrorTot += minErrorMap[row][column];
////            }
////        }
////        CustomArray.plusArrayDisplay(minErrorMap, false, false, "Min Error Map");
////        CustomArray.plusArrayDisplay(maxErrorMap, false, false, "Max Error Map");
////        double[] rowTots = new double[size];
////        double phi = (1 + Math.sqrt(5)) / 2;
////        int[] maxRowTots = new int[size];
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                rowTots[row] += minErrorMap[row][column];
////                maxRowTots[row] += maxErrorMap[row][column];
////            }
////        }
////        double[][] maxRowComparisons = new double[size][size];
////        double[][] minRowComparisons = new double[size][size];
////        double[][] maxMapRate = new double[size][size];
////        double[][] minMapRate = new double[size][size];
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                maxRowComparisons[row][column] = (double) maxRowTots[row] / (double) maxRowTots[column];
////                minRowComparisons[row][column] = (double) rowTots[row] / (double) rowTots[column];
////                maxMapRate[row][column] = (double) maxErrorMap[row][column] / (double) numBoards / 8.0;
////                minMapRate[row][column] = (double) minErrorMap[row][column] / (double) numBoards / 8.0;
////            }
////        }
////        System.out.println("maxRowComparisons ");
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                System.out.print(String.format("%.4f", maxRowComparisons[row][column]) + " ");
////            }
////            System.out.print("\n");
////        }
////        System.out.println("maxMapRate[][] ");
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                System.out.print(String.format("%.4f", maxMapRate[row][column]) + " ");
////            }
////            System.out.print("\n");
////        }
////        System.out.println("minRowComparisons ");
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                System.out.print(String.format("%.4f", minRowComparisons[row][column]) + " ");
////            }
////            System.out.print("\n");
////        }
////        System.out.println("maxMapRate[][] ");
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                System.out.print(String.format("%.4f", minMapRate[row][column]) + " ");
////            }
////            System.out.print("\n");
////        }
////        System.out.println("(1,0) - PI/3" + ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0));
////        System.out.println("(3,2) - PI " + ((double) rowTots[2] / (double) rowTots[3] - Math.PI));
////        double diff = ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0);
////        diff = Math.log(diff) / Math.log(2);
////        System.out.println("diff " + diff);
////        diff = ((double) rowTots[2] / (double) rowTots[3] - Math.PI);
////        diff = Math.log(diff) / Math.log(2);
////        System.out.println("diff " + diff);
////        int upper = 0;
////        int lower = 0;
////        for (row = 0; row < 2; row++) {
////            for (column = 0; column < size; column++) {
////                upper += minErrorMap[row][column];
////                lower += minErrorMap[row + 2][column];
////            }
////        }
////        double div = (double) upper / (double) lower;
////        System.out.println("upper/lower " + div + " " + 1.0 / div + " " + div * Math.PI + " " + Math.PI / div + " " + div / Math.PI);
////        System.out.println("upper/lower - phi*phi " + (div - phi * phi));
////        double[] diffs = new double[]{((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0), ((double) rowTots[2] / (double) rowTots[3] - Math.PI), (div - phi * phi)};
////        for (int d = 0; d < 3; d++) {
////            diffs[d] = Math.abs(diffs[d]);
////            System.out.println("diff " + diffs[d] + " " + (Math.log(diffs[d]) / Math.log(2)));
////        }
////        for (int power = 2; power > -20; power--) {
////            System.out.print((int) (Math.PI / Math.pow(2, power)) % 2 + " ");
////        }
////        System.out.println();
////        for (int power = 2; power > -20; power--) {
////            System.out.print((int) (diffs[1] / Math.pow(2, power)) % 2 + " ");
////        }
////        System.out.println();
////        for (int power = 2; power > -24; power--) {
////            int a = (int) ((diffs[1] / Math.pow(2, power)) % 2);
////            int b = (int) ((Math.PI / Math.pow(2, power)) % 2);
////            System.out.print((a ^ b) + " ");
////        }
////        System.out.println();
////        double[][] errors = new double[size][size];
////        double[] byRow = new double[size];
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                errors[row][column] = (double) minErrorMap[row][column] / (double) (Math.pow(2, size * size) * 8);
////                byRow[row] += errors[row][column];
////            }
////            byRow[row] /= 4;
////            byRow[row] *= (440 * 8);
////        }
////        System.out.println("errors ");
////        for (row = 0; row < size; row++) {
////            System.out.println(Arrays.toString(errors[row]));
////        }
////        System.out.println("byRow " + Arrays.toString(byRow));
////        //doWallisProduct(50);
////        //doTriangles(50);
////        System.out.println("solutionDistro: " + Arrays.toString(minSolutionDistro));
////        System.out.println("maxSolutionDistro: " + Arrays.toString(maxSolutionDistro));
////        System.out.println("\n\n\n\n\n\n\n");
////        for (int spot = 0; spot < 16; spot++) {
////            System.out.print((double) maxSolutionDistro[spot] / 65536.0 + " ");
////        }
////        System.out.println();
////        for (int spot = 0; spot < 16; spot++) {
////            System.out.print(65536.0 / (double) maxSolutionDistro[spot] + " ");
////        }
//        return solutions[0];
//    }
//
//    public void displayResults(boolean doChangeScore) {
////        int row;
////        int column;
////        //The rest is results management & display
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //Sorts and averages the best performing ECA rules
////        System.out.println("solutionBucket[] " + solutionBucket);
////        System.out.println("pre division");
////        System.out.println(Arrays.toString(minErrorBuckets));
////        System.out.println("hbLength[]");
////        System.out.println(Arrays.toString(numberBoards));
////        int[] hashbucketRate = new int[256];
////        for (row = start; row < stop; row++) {
////            hashbucketRate[row] = minErrorBuckets[row] / numberBoards[row];
////            if (doChangeScore) averageChange[row] = (double) hammingChange[row] / (double) errorChange[row];
////        }
////        System.out.println("post division");
////        System.out.println(Arrays.toString(hashbucketRate));
////        int[] sorted   //The rest is results management & display
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //
////        //Sorts and averages the best performing ECA rules
////        System.out.println("solutionBucket[] " + solutionBucket);
////        System.out.println("pre division");
////        System.out.println(Arrays.toString(minErrorBuckets));
////        System.out.println("hbLength[]");
////        System.out.println(Arrays.toString(numberBoards));
////        int[] hashbucketRate = new int[256];
////        for (row = start; row < stop; row++) {
////            hashbucketRate[row] = minErrorBuckets[row] / numberBoards[row];
////            if (doChangeScore) averageChange[row] = (double) hammingChange[row] / (double) errorChange[row];
////        }
////        System.out.println("post division");
////        System.out.println(Arrays.toString(hashbucketRate));
////        int[] sorted = new int[256];
////        for (row = 0; row < 256; row++) {
////            sorted[row] = row;
////        }
////        if (doAllRules) {
////            for (row = 0; row < 256; row++) {
////                for (column = 0; column < 256; column++) {
////                    if (hashbucketRate[row] < hashbucketRate[column]) {
////                        int temp = minErrorBuckets[row];
////                        minErrorBuckets[row] = minErrorBuckets[column];
////                        minErrorBuckets[column] = temp;
////                        temp = sorted[row];
////                        sorted[row] = sorted[column];
////                        sorted[column] = temp;
////                        temp = numberBoards[row];
////                        numberBoards[row] = numberBoards[column];
////                        numberBoards[column] = temp;
////                        temp = hashbucketRate[row];
////                        hashbucketRate[row] = hashbucketRate[column];
////                        hashbucketRate[column] = temp;
////                    }
////                }
////            }
////        }
////        // System.out.println(Arrays.toString(hashbucket));
////        //
////        //
////        //Output
////        System.out.println("Here");
////        for (int index = start; index < stop; index++) {
////            System.out.println("index " + sorted[index] + " " + basicECA.ruleClasses[sorted[index]] + " " + hashbucketRate[index] + " " + minErrorBuckets[index] + " " + numberBoards[index] + " ");
////        }
////        System.out.println(Arrays.toString(sameSolutions));
////        for (row = start; row < stop; row++) {
////            if (sameSolutions[sorted[row]] == 1) {
////                System.out.println("Rule has a unique solution " + sorted[row]);
////            } else {
////                System.out.println("sameSolutions[" + sorted[row] + "] " + sameSolutions[sorted[row]]);
////            }
////        }
////        if (doChangeScore) {
////            System.out.println("hammingChange per changeScore");
////            for (row = start; row < stop; row++) {
////                System.out.println(sorted[row] + " " + changeChange[sorted[row]] + " " + errorChange[sorted[row]]);
////            }
////        }
////        System.out.println("min error/grid " + ((double) minErrorBuckets[specificRule] / (double) numBoards));
////        System.out.println("min error/bit " + ((double) minErrorBuckets[specificRule]) / numBoards / size / size);
////        System.out.println("min bits/error " + (double) (numBoards * size * size) / (double) minErrorBuckets[specificRule]);
////        System.out.println("sameError " + Arrays.toString(sameErrorMin));
////        System.out.println("sameErrorMax " + Arrays.toString(sameErrorMax));
////        System.out.println("max error/grid " + ((double) maxErrorBuckets[specificRule] / (double) numBoards));
////        System.out.println("max error/bit " + ((double) maxErrorBuckets[specificRule]) / numBoards / size / size);
////        System.out.println("max bits/error " + (double) (numBoards * size * size) / (double) maxErrorBuckets[specificRule]);
////        System.out.println("\n");
////        double max = ((double) maxErrorBuckets[specificRule]) / numBoards / size / size;
////        double min = ((double) minErrorBuckets[specificRule]) / numBoards / size / size;
////        double ebDiff = ((double) maxErrorBuckets[specificRule] / numBoards / size / size) - ((double) minErrorBuckets[specificRule] / numBoards / size / size);
////        double beDiff = ((double) (numTrials * size * size) / (double) maxErrorBuckets[specificRule]) - ((double) (numTrials * size * size) / (double) minErrorBuckets[specificRule]);
////        System.out.println("ebDiff " + ebDiff);
////        System.out.println("beDiff " + beDiff);
////        System.out.println("max * min = " + (max * min));
////        double[][] sameMin = new double[size][size];
////        double[][] sameMax = new double[size][size];
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                sameMin[row][column] = (double) sameErrorMin[row + 1] / (double) sameErrorMin[column + 1];
////                sameMax[row][column] = (double) sameErrorMax[2 * row + 2] / (double) sameErrorMax[2 * column + 2];
////            }
////        }
////        System.out.println("sameMaxRowComparisons ");
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                System.out.print(String.format("%.4f", sameMax[row][column]) + " ");
////            }
////            System.out.print("\n");
////        }
////        System.out.println("sameMinRowComparisons ");
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                System.out.print(String.format("%.4f", sameMin[row][column]) + " ");
////            }
////            System.out.print("\n");
////        }
////        for (row = 1; row < 4096 && row < sameErrorMin.length; row *= 2) {
////            System.out.print("row " + row + " " + sameErrorMin[row] + "\n");
////        }
////        int maxErrorTot = 0;
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                maxErrorTot += minErrorMap[row][column];
////            }
////        }
////        CustomArray.plusArrayDisplay(minErrorMap, false, false, "Min Error Map");
////        CustomArray.plusArrayDisplay(maxErrorMap, false, false, "Max Error Map");
////        double[] rowTots = new double[size];
////        double phi = (1 + Math.sqrt(5)) / 2;
////        int[] maxRowTots = new int[size];
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                rowTots[row] += minErrorMap[row][column];
////                maxRowTots[row] += maxErrorMap[row][column];
////            }
////        }
////        double[][] maxRowComparisons = new double[size][size];
////        double[][] minRowComparisons = new double[size][size];
////        double[][] maxMapRate = new double[size][size];
////        double[][] minMapRate = new double[size][size];
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                maxRowComparisons[row][column] = (double) maxRowTots[row] / (double) maxRowTots[column];
////                minRowComparisons[row][column] = (double) rowTots[row] / (double) rowTots[column];
////                maxMapRate[row][column] = (double) maxErrorMap[row][column] / (double) numBoards / 8.0;
////                minMapRate[row][column] = (double) minErrorMap[row][column] / (double) numBoards / 8.0;
////            }
////        }
////        System.out.println("maxRowComparisons ");
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                System.out.print(String.format("%.4f", maxRowComparisons[row][column]) + " ");
////            }
////            System.out.print("\n");
////        }
////        System.out.println("maxMapRate[][] ");
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                System.out.print(String.format("%.4f", maxMapRate[row][column]) + " ");
////            }
////            System.out.print("\n");
////        }
////        System.out.println("minRowComparisons ");
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                System.out.print(String.format("%.4f", minRowComparisons[row][column]) + " ");
////            }
////            System.out.print("\n");
////        }
////        System.out.println("maxMapRate[][] ");
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                System.out.print(String.format("%.4f", minMapRate[row][column]) + " ");
////            }
////            System.out.print("\n");
////        }
////        System.out.println("(1,0) - PI/3" + ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0));
////        System.out.println("(3,2) - PI " + ((double) rowTots[2] / (double) rowTots[3] - Math.PI));
////        double diff = ((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0);
////        diff = Math.log(diff) / Math.log(2);
////        System.out.println("diff " + diff);
////        diff = ((double) rowTots[2] / (double) rowTots[3] - Math.PI);
////        diff = Math.log(diff) / Math.log(2);
////        System.out.println("diff " + diff);
////        int upper = 0;
////        int lower = 0;
////        for (row = 0; row < 2; row++) {
////            for (column = 0; column < size; column++) {
////                upper += minErrorMap[row][column];
////                lower += minErrorMap[row + 2][column];
////            }
////        }
////        double div = (double) upper / (double) lower;
////        System.out.println("upper/lower " + div + " " + 1.0 / div + " " + div * Math.PI + " " + Math.PI / div + " " + div / Math.PI);
////        System.out.println("upper/lower - phi*phi " + (div - phi * phi));
////        double[] diffs = new double[]{((double) rowTots[1] / (double) rowTots[0] - Math.PI / 3.0), ((double) rowTots[2] / (double) rowTots[3] - Math.PI), (div - phi * phi)};
////        for (int d = 0; d < 3; d++) {
////            diffs[d] = Math.abs(diffs[d]);
////            System.out.println("diff " + diffs[d] + " " + (Math.log(diffs[d]) / Math.log(2)));
////        }
////        for (int power = 2; power > -20; power--) {
////            System.out.print((int) (Math.PI / Math.pow(2, power)) % 2 + " ");
////        }
////        System.out.println();
////        for (int power = 2; power > -20; power--) {
////            System.out.print((int) (diffs[1] / Math.pow(2, power)) % 2 + " ");
////        }
////        System.out.println();
////        for (int power = 2; power > -24; power--) {
////            int a = (int) ((diffs[1] / Math.pow(2, power)) % 2);
////            int b = (int) ((Math.PI / Math.pow(2, power)) % 2);
////            System.out.print((a ^ b) + " ");
////        }
////        System.out.println();
////        double[][] errors = new double[size][size];
////        double[] byRow = new double[size];
////        for (row = 0; row < size; row++) {
////            for (column = 0; column < size; column++) {
////                errors[row][column] = (double) minErrorMap[row][column] / (double) (Math.pow(2, size * size) * 8);
////                byRow[row] += errors[row][column];
////            }
////            byRow[row] /= 4;
////            byRow[row] *= (440 * 8);
////        }
////        System.out.println("errors ");
////        for (row = 0; row < size; row++) {
////            System.out.println(Arrays.toString(errors[row]));
////        }
////        System.out.println("byRow " + Arrays.toString(byRow));
////        //doWallisProduct(50);
////        //doTriangles(50);
////        System.out.println("solutionDistro: " + Arrays.toString(minSolutionDistro));
////        System.out.println("maxSolutionDistro: " + Arrays.toString(maxSolutionDistro));
////        System.out.println("\n\n\n\n\n\n\n");
////        for (int spot = 0; spot < 16; spot++) {
////            System.out.print((double) maxSolutionDistro[spot] / 65536.0 + " ");
////        }
////        System.out.println();
////        for (int spot = 0; spot < 16; spot++) {
////            System.out.print(65536.0 / (double) maxSolutionDistro[spot] + " ");
////        }
//    }
}
