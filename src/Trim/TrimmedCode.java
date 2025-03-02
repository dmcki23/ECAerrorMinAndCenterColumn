package Trim;

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
}
