import java.math.BigInteger;
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









}
