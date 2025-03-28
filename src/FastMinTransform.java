import java.util.Arrays;
import java.util.Random;

public class FastMinTransform {
    minErrorStaging m = new minErrorStaging();
    int[][][][] wolframs;
    int[][][][][] solutions;
    int[][] ruleList = new int[][]{{0, 255}, {15, 85}, {204, 204}, {170, 240}};
    int[][][] flatWolframs = new int[2][8][256 * 256];
    int[][][] reconstructed;
    int[][] vectorField;
    int[][] packedList = new int[][]{{0, 255}, {15, 240}, {51, 204}, {85, 170}};
    int[] unpackedList = new int[]{0, 15, 51, 85, 170, 204, 240, 255};

    public FastMinTransform() {
    }

    public int[] oneD(int[] input, int[] wolframTuple) {
        int[] out = new int[input.length];
        int[][] intermediate = new int[input.length][input.length];
        for (int row = 0; row < input.length; row++) {
            for (int col = 0; col < input.length; col++) {
                intermediate[row][col] = input[col];
            }
        }
        int[][][][] transformed = minTransformTwoPaths(intermediate, wolframTuple);
        int[][][] reconstructed = reconstruct(vectorField, 4, 0, new int[]{0, 0, 0});
        for (int row = 0; row < input.length; row++) {
            out[row] = reconstructed[4][row][0];
        }
        return out;
    }

    public int[][][] minTransform(int[][] input, int[] wolframTuple) {
        initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        //mirrors, xy, phase, depth, (cell mirrors, minMax tree)
        solutions = new int[8][2][4][5][256 * 256];
        int[][] out = new int[rows][cols];
        int totLength = 256 * 256;
        int[][][] deepInput = new int[4][rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        for (int depth = 1; depth < 2; depth++) {

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    int cell = 0;
                    for (int r = 0; r < 4; r++) {
                        for (int c = 0; c < 4; c++) {
                            cell += (int) Math.pow(2, 4 * r + c) * deepInput[depth - 1][(row + r) % rows][(col + c) % cols];
                        }
                    }

                    deepInput[depth][row][col] = wolframs[wolframTuple[0]][wolframTuple[1]][wolframTuple[2]][cell];
                }
            }
        }
        return deepInput;
    }

    public int[][][] minTransformTwo(int[][] input, int[] wolframTuple, int depth) {
        initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        //mirrors, xy, phase, depth, (cell mirrors, minMax tree)
        solutions = new int[8][2][4][5][256 * 256];
        int[][] out = new int[rows][cols];
        int totLength = 256 * 256;
        int[][][] deepInput = new int[depth+1][rows][cols];
        int[][][] init;// = minTransform(input, wolframTuple);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        for (int d = 1; d <= depth; d++) {
            for (int row = 0; row < rows; row++) {
                System.out.print(row + " ");
                if (row % 32 == 0)System.out.print("\n " + (rows-row) + " ");
                for (int col = 0; col < cols; col++) {
                    int cell = 0;
                    int phasePower = (int) Math.pow(2, d - 1);
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            cell += (int) Math.pow(4, 2 * r + c) * deepInput[depth - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                        }
                    }
                    deepInput[depth][row][col] = (wolframs[wolframTuple[0]][wolframTuple[1]][wolframTuple[2]][cell]);
                }
            }
        }
        return deepInput;
    }
    public int[][][] minTransformTwoOneByOne(int[][] input, int[] wolframTuple, int depth) {
        initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        //mirrors, xy, phase, depth, (cell mirrors, minMax tree)
        solutions = new int[8][2][4][5][256 * 256];
        int[][] out = new int[rows][cols];
        int totLength = 256 * 256;
        int[][][] deepInput = new int[depth+1][rows][cols];
        int[][][] init;// = minTransform(input, wolframTuple);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        for (int d = 1; d <= depth; d++) {
            for (int row = 0; row < rows; row++) {
                System.out.print(row + " ");
                if (row % 32 == 0)System.out.print("\n " + (rows-row) + " ");
                for (int col = 0; col < cols; col++) {
                    int cell = 0;
                    int phasePower = (int) Math.pow(2, d-1);
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            cell += (int) Math.pow(4, 2 * r + c) * deepInput[d - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                        }
                    }
                    deepInput[d][row][col] = (wolframs[wolframTuple[0]][wolframTuple[1]][wolframTuple[2]][cell]);
                }
            }
        }
        return deepInput;
    }
    public int[][][] minTransformTwoPreProcessed(int[][][] input, int[] wolframTuple) {
        initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        //mirrors, xy, phase, depth, (cell mirrors, minMax tree)
        solutions = new int[8][2][4][5][256 * 256];
        int[][] out = new int[rows][cols];
        int totLength = 256 * 256;
        int[][][] deepInput = new int[4][rows][cols];
        int[][][] init = input;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = init[0][row][col];
            }
        }
        for (int depth = 2; depth < 4; depth++) {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    int cell = 0;
                    int phasePower = (int) Math.pow(2, depth - 1);
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            cell += (int) Math.pow(4, 2 * r + c) * deepInput[depth - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                        }
                    }
                    deepInput[depth][row][col] = (wolframs[wolframTuple[0]][wolframTuple[1]][wolframTuple[2]][cell]);
                }
            }
        }
        return deepInput;
    }

    public int[][][][] minTransformTwoPaths(int[][] input, int[] wolframTuple) {
        initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        //mirrors, xy, phase, depth, (cell mirrors, minMax tree)
        solutions = new int[8][2][4][5][256 * 256];
        int[][] out = new int[rows][cols];
        int totLength = 256 * 256;
        int[][][] deepInput = new int[4][rows][cols];
        int[][][] init = minTransform(input, wolframTuple);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = init[1][row][col];
            }
        }
        int[][][][] output = new int[9][256][rows][cols];
        for (int depth = 1; depth < 4; depth++) {
            for (int path = 0; path < (int) Math.pow(2, depth); path++) {
                for (int spot = 0; spot < (int) Math.pow(2, depth); spot++) {
                    for (int row = 0; row < rows; row++) {
                        for (int col = 0; col < cols; col++) {
                            int cell = 0;
                            int phasePower = (int) Math.pow(2, depth - 1);
                            for (int r = 0; r < 2; r++) {
                                for (int c = 0; c < 2; c++) {
                                    cell += (int) Math.pow(4, 2 * r + c) * deepInput[depth - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                                }
                            }
                            output[depth][path][row][col] = (wolframs[((path / (int) Math.pow(2, spot)) % 2)][wolframTuple[1]][wolframTuple[2]][cell]);
                            vectorField[row][col] += (int) Math.pow(4, depth - 1) * output[depth][path][row][col];
                        }
                    }
                }
            }
        }
        return output;
    }

    public int[][][][] minTransformTwoPathsTwo(int[][] input, int[] wolframTuple) {
        initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        //mirrors, xy, phase, depth, (cell mirrors, minMax tree)
        solutions = new int[8][2][4][5][256 * 256];
        int[][] out = new int[rows][cols];
        int totLength = 256 * 256;
        int[][][] deepInput = new int[4][rows][cols];
        int[][][] init = minTransform(input, wolframTuple);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = init[1][row][col];
            }
        }
        int[][][][] output = new int[9][256][rows][cols];
        for (int depth = 1; depth < 4; depth++) {
            for (int path = 0; path < (int) Math.pow(2, depth); path++) {
                for (int spot = 0; spot < (int) Math.pow(2, depth); spot++) {
                    for (int row = 0; row < rows; row++) {
                        for (int col = 0; col < cols; col++) {
                            int cell = 0;
                            int phasePower = (int) Math.pow(2, depth - 1);
                            for (int r = 0; r < 2; r++) {
                                for (int c = 0; c < 2; c++) {
                                    cell += (int) Math.pow(4, 2 * r + c) * deepInput[depth - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                                }
                            }
                            output[depth][path][row][col] = (wolframs[((path / (int) Math.pow(2, spot)) % 2)][wolframTuple[1]][wolframTuple[2]][cell]);
                        }
                    }
                }
            }
        }
        return output;
    }

    public void analyzeTwo() {
        initWolframs();
        int[] tuple = new int[8];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 2; col++) {
                tuple[row * 2 + col] = ruleList[row][col];
            }
        }
        negLoop:
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int spot = 0; spot < 256 * 256; spot++) {
                for (int compare = 0; compare < 256 * 256; compare++) {
                    if (spot == compare) continue;
                    if (Arrays.equals(flatWolframs[posNeg][spot], flatWolframs[posNeg][compare])) {
                        System.out.println(posNeg + " " + spot + " " + compare);
                        break negLoop;
                    }
                }
            }
            System.out.println("posNeg " + posNeg + "WORKS!");
        }
    }

    public int[][][] vectorField(int[][][] input, int depth) {
        int[][][] out = new int[100][input[0].length][input[0].length];
        for (int dd = 1; dd < dd; depth++) {
            for (int diag = 0; diag < input[0].length; diag++) {
                out[0][diag][diag] = input[depth][diag][diag];
            }
            int depthPower = (int) Math.pow(2, depth);
            for (int frame = 0; frame < input[0].length; frame++) {
                for (int row = 0; row < input[0].length; row++) {
                    for (int col = 0; col < input[0].length; col++) {
                        int bit = input[depth][row][col] % 2;
                        int[] coords = new int[2];
                        if ((input[depth][row][col] / (int) Math.pow(2, 1) % 2) == 0) {
                            coords[0] = -1;
                        } else {
                            coords[0] = 1;
                        }
                        if ((input[depth][row][col] / (int) Math.pow(2, 2) % 2) == 0) {
                            coords[1] = -coords[0];
                        } else {
                            coords[1] = coords[0];
                        }
                        if ((input[depth][row][col] / (int) Math.pow(2, 3) % 2) == 0) {
                            coords[0] += 1;
                            coords[1] -= 1;
                        } else {
                            coords[0] -= 1;
                            coords[1] += 1;
                        }
                        out[frame][row + depthPower * coords[0]][col + depthPower * coords[1]] += (int) Math.pow(2, dd - 1) * bit;
                        //out[frame][row+depthPower*coords[1]][col+depthPower*coords[0]] %= 2;
                    }
                }
            }
        }
        return out;
    }

    public int[][][] reconstruct(int[][] input, int length, int specificPath, int[] wolframTuple) {
        int[][][][] transform = minTransformTwoPaths(input, wolframTuple);
        int rows = input.length;
        int cols = input[0].length;
        //mirrors, xy, phase, depth, (cell mirrors, minMax tree)
        solutions = new int[8][2][4][5][256 * 256];
        int[][] out = new int[rows][cols];
        int totLength = 256 * 256;
        int[][][] deepInput = new int[4][rows][cols];
        int[][][] init = minTransform(input, wolframTuple);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = transform[length][specificPath][row][col];
            }
        }
        int[][][][] output = new int[9][256][rows][cols];
        int[][] voteLogDepth = new int[4][4];
        int[][][] votes = new int[16][4][4];
        for (int depth = 1; depth < 4; depth++) {
            for (int path = specificPath; path < specificPath + 1; path++) {
                for (int spot = 0; spot < (int) Math.pow(2, depth); spot++) {
                    for (int row = 0; row < rows; row++) {
                        for (int col = 0; col < cols; col++) {
                            int cell = 0;
                            int phasePower = (int) Math.pow(2, depth - 1);
                            for (int r = 0; r < 2; r++) {
                                for (int c = 0; c < 2; c++) {
                                    voteLogDepth = m.generateGuess(deepInput[depth - 1][row + phasePower * r][col + phasePower * r], 204);
                                    for (int rr = 0; rr < 4; rr++) {
                                        for (int cc = 0; cc < 4; cc++) {
                                            votes[2 * rr + cc][(r + rr) % 4][(c + cc) % 4] = voteLogDepth[rr][cc];
                                        }
                                    }
                                    //cell += (int) Math.pow(4, 2 * r + c) * deepInput[depth][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                                }
                            }
                            int[][] votesTot = new int[4][4];
                            for (int r = 0; r < 4; r++) {
                                for (int c = 0; c < 4; c++) {
                                    for (int height = 0; height < 4; height++) {
                                        votesTot[r][c] += votes[height][r][c];
                                    }
                                    votesTot[r][c] = (votesTot[r][c] > 1) ? 1 : 0;
                                }
                            }
                            for (int r = 0; r < 2; r++) {
                                for (int c = 0; c < 2; c++) {
                                    int tot = 0;
                                    for (int rr = 0; rr < 2; rr++) {
                                        for (int cc = 0; cc < 2; cc++) {
                                            deepInput[depth][row + phasePower * r][col + phasePower * r] += (int) Math.pow(2, 2 * rr + cc) * votesTot[2 * r + rr][2 * c + cc];
                                        }
                                    }
                                }
                            }
                            //output[depth][path][row][col] = (wolframs[(((int)(Math.pow(2,depth)-1-path) / (int) Math.pow(2, spot)) % 2)][wolframTuple[1]][wolframTuple[2]][cell]);
                        }
                    }
                }
            }
        }
        reconstructed = deepInput;
        return deepInput;
    }

    public int[][] toRGB(int[][][] input) {
        int[][] rgbOut = new int[input.length][input[0].length];
        for (int row = 0; row < input.length; row++) {
            for (int col = 0; col < input[row].length; col++) {
                for (int depth = 1; depth < 4; depth++) {
                    rgbOut[row][col] += (int) Math.pow(4, depth - 1) * input[depth][row][col];
                }
            }
        }
        return rgbOut;
    }

    public void allSymmetriesOfMinTransform(int[][] in) {
        int[][][] transformedArray = new int[4][in.length][in[0].length];
        CustomArray ca = new CustomArray();
        for (int mirror = 0; mirror < 8; mirror++) {
            int[][] mirrorArr = ca.reflectRotateTranspose(in, mirror);
            for (int posNeg = 0; posNeg < 2; posNeg++) {
                for (int spot = 0; spot < 4; spot++) {
                    for (int lr = 0; lr < 2; lr++) {
                        transformedArray = minTransform(in, new int[]{posNeg, spot, lr});
                    }
                }
            }
        }
    }

    public int[][][][] initWolframs() {
        int[][] ruleList = packedList;
        int[][][] wolframIn = new int[4][2][256 * 256];
        int[][][] maxWolframIn = new int[4][2][256 * 256];
        m.doAllRulesCoords(4, false, 0, false, 0, false, ruleList);
        for (int spot = 0; spot < 4; spot++) {
            for (int lr = 0; lr < 2; lr++) {
                //m.individualRule(ruleList[spot][lr],4,false,0,false,0,false);
                wolframIn[spot][lr] = m.minSolutionsAsWolfram[ruleList[spot][lr]];
                maxWolframIn[spot][lr] = m.maxSolutionsAsWolfram[ruleList[spot][lr]];
            }
        }
        //int[][] wolfram = m.minSolutionsAsWolfram;
        //int[][] maxWolfram = m.maxSolutionsAsWolfram;
        //minMax,group,leftright
        wolframs = new int[2][4][2][256 * 256];
        for (int spot = 0; spot < 4; spot++) {
            for (int lr = 0; lr < 2; lr++) {
                for (int column = 0; column < 256 * 256; column++) {
                    wolframs[0][spot][lr][column] = wolframIn[spot][lr][column];
                    wolframs[1][spot][lr][column] = maxWolframIn[spot][lr][column];
                    flatWolframs[0][2 * spot + lr][column] = wolframIn[spot][lr][column];
                    flatWolframs[1][2 * spot + lr][column] = maxWolframIn[spot][lr][column];
                }
            }
        }
        return wolframs;
    }

    public void analyzeRelevantWolframs() {
        initWolframs();
        int[][][][][] lrbwWolframs = new int[4][wolframs.length][wolframs[0].length][wolframs[1].length][wolframs[2].length];
        for (int lrbw = 0; lrbw < 4; lrbw++) {
            for (int posNeg = 0; posNeg < 2; posNeg++) {
                for (int spot = 0; spot < 4; spot++) {
                    for (int lr = 0; lr < 2; lr++) {
                        for (int column = 0; column < 256 * 256; column++) {
                            int temp = 0;
                            int address = column;
                            int tot = 0;
                            if ((lrbw / 2) % 2 == 1) {
                                tot = 0;
                                for (int power = 0; power < 4; power++) {
                                    for (int pow = 0; pow < 4; pow++) {
                                        int coefficient = (int) Math.pow(2, 4 * power + pow);
                                        int negCoefficient = (int) Math.pow(2, 15 - 4 * power - pow);
                                        tot += negCoefficient * ((column / coefficient) % 2);
                                    }
                                }
                                address = tot;
                            }
                            lrbwWolframs[lrbw][posNeg][spot][lr][address] = wolframs[posNeg][spot][lr][column];
                            if (lrbw % 2 == 1) {
                                tot = 0;
                                for (int power = 0; power < 4; power++) {
                                    tot += (int) Math.pow(2, 3 - power) * ((wolframs[posNeg][spot][lr][column] / (int) Math.pow(2, power)) % 2);
                                }
                                temp = 15 - tot;
                                lrbwWolframs[lrbw][posNeg][spot][lr][address] = 15 - tot;
                            }
                        }
                    }
                }
            }
        }
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int spot = 0; spot < 4; spot++) {
                for (int lrs = 0; lrs < 2; lrs++) {
                    if (spot % 2 == 1) continue;
                    for (int lrbw = 0; lrbw < 4; lrbw++) {
                        for (int column = 0; column < 256; column++) {
                            System.out.print(String.format("% 4d", lrbwWolframs[lrbw][posNeg][spot][lrs][column]));
                        }
                    }
                }
            }
        }
    }

    public void checkWolframs() {
        int[][] xyHeatMap = new int[4][256];
        initWolframs();
        System.out.println(Arrays.toString(Arrays.copyOfRange(flatWolframs[0][0], 0, 64)));
        for (int posNeg = 0; posNeg < 1; posNeg++) {
            boolean isUnique = true;
            int totSame = 0;
            aLoop:
            for (int a = 0; a < 256 * 256; a++) {
                if (a % (256 * 16) == 0) {
                    System.out.println("a: " + a);
                }
                for (int b = 0; b < a; b++) {
                    if (a == b) continue;
                    int[] aa = new int[16];
                    int[] bb = new int[16];
                    int[] cc = new int[8];
                    int[] dd = new int[8];
                    for (int row = 0; row < 8; row++) {
                        aa[row] = flatWolframs[0][row][a];
                        aa[8 + row] = flatWolframs[1][row][a];
                        bb[row] = flatWolframs[0][row][b];
                        bb[8 + row] = flatWolframs[1][row][b];
                    }
                    if (Arrays.equals(aa, bb)) {
                        isUnique = false;
                        totSame++;
                        xyHeatMap[0][a/256]++;
                        xyHeatMap[1][b/256]++;
                        xyHeatMap[2][b%256]++;
                        xyHeatMap[3][a%256]++;
                        //continue aLoop;
                    }
                }
            }
            if (isUnique) {
                System.out.println("isUnique");
            } else {
                System.out.println("NOT isUnique");
            }
            System.out.println("totSame: " + totSame);
        }

        System.out.println(Arrays.toString(xyHeatMap[0]));
        System.out.println(Arrays.toString(xyHeatMap[1]));
        System.out.println(Arrays.toString(xyHeatMap[2]));
        System.out.println(Arrays.toString(xyHeatMap[3]));
    }

    public void checkWolframsForReversibility() {
        initWolframs();
        System.out.println(Arrays.toString(Arrays.copyOfRange(flatWolframs[0][0], 0, 64)));
        boolean isExact = true;
        int totSame = 0;
        int minExactBack = 0;
        int maxExactBack = 0;
        double totHammingDistanceMin = 0;
        double totHammingDistanceMax = 0;
        aLoop:
        for (int a = 0; a < 256 * 256; a++) {
            if (a % (256 * 16) == 0) {
                //System.out.println("a: " + a);
            }
            int[] aa = new int[16];
            int[][][] votes = new int[17][4][4];
            for (int row = 0; row < 8; row++) {
                aa[row] = flatWolframs[0][row][a];
                aa[8 + row] = flatWolframs[1][row][a];
            }
            for (int level = 1; level < 7; level++) {
                int[] neighborhood = new int[4];
                for (int power = 0; power < 4; power++) {
                    neighborhood[power] = ((aa[level] / (int) Math.pow(2, power)) % 2);
                }
                votes[level] = m.generateGuess(neighborhood, unpackedList[level]);
                votes[level + 8] = m.generateGuess(neighborhood, unpackedList[level]);
            }
            int[][] minVotes = new int[4][4];
            int[][] maxVotes = new int[4][4];
            for (int level = 1; level < 7; level++) {
                for (int row = 0; row < 4; row++) {
                    for (int column = 0; column < 4; column++) {
                        minVotes[row][column] += votes[level][row][column];
                        maxVotes[row][column] += votes[level + 8][row][column];
                    }
                }
            }
            int minTot = 0;
            int maxTot = 0;
            int[][] minVoteResult = new int[4][4];
            int[][] maxVoteResult = new int[4][4];
            Random rand = new Random();
            for (int row = 0; row < 4; row++) {
                for (int column = 0; column < 4; column++) {
                    if (minVotes[row][column] > 4) {
                        minVoteResult[row][column] = 1;
                        totHammingDistanceMin += (minVoteResult[row][column] ^ ((a / (int) Math.pow(2, 4 * row + column)) % 2));
                    } else if (minVotes[row][column] < 3) {
                        minVoteResult[row][column] = 0;
                        totHammingDistanceMin += (minVoteResult[row][column] ^ ((a / (int) Math.pow(2, 4 * row + column)) % 2));
                    } else {
                        minVoteResult[row][column] = rand.nextInt(0, 2);
                        totHammingDistanceMin += (minVoteResult[row][column] ^ ((a / (int) Math.pow(2, 4 * row + column)) % 2));
                    }
                    if (maxVotes[row][column] > 4) {
                        minVoteResult[row][column] = 1;
                        totHammingDistanceMax += (maxVoteResult[row][column] ^ ((a / (int) Math.pow(2, 4 * row + column)) % 2));
                    } else if (maxVotes[row][column] < 3) {
                        minVoteResult[row][column] = 0;
                        totHammingDistanceMax += (maxVoteResult[row][column] ^ ((a / (int) Math.pow(2, 4 * row + column)) % 2));
                    } else {
                        minVoteResult[row][column] = rand.nextInt(0, 2);
                        totHammingDistanceMax += (maxVoteResult[row][column] ^ ((a / (int) Math.pow(2, 4 * row + column)) % 2));
                    }
                    if (maxVotes[row][column] > 2) {
                        maxVoteResult[row][column] = 1;
                        totHammingDistanceMax += (maxVoteResult[row][column] ^ ((a / (int) Math.pow(2, 4 * row + column)) % 2));
                    }
                    if (minVotes[row][column] + maxVotes[row][column] > 7) {
                        //minVoteResult[row][column] = 1;
                        //maxVoteResult[row][column] = 1;
                    }
                    minTot += (int) Math.pow(2, 4 * row + column) * minVoteResult[row][column];
                    maxTot += (int) Math.pow(2, 4 * row + column) * maxVoteResult[row][column];
                }
            }
            // minExactBack += ((minTot==a) ? 0 : 1);
            //maxExactBack += (maxTot==a) ? 0 : 1;
            if (minTot != a) {
                System.out.println("minTot: " + minTot + " a: " + a);
            } else {
                minExactBack++;
            }
            if (maxTot != a) {
                System.out.println("maxTot: " + maxTot + " a: " + a);
            } else {
                maxExactBack++;
            }
        }
        System.out.println("minExactBack: " + minExactBack + " average Hamming distance: " + (totHammingDistanceMin / 65536));
        System.out.println("maxExactBack: " + maxExactBack + " average Hamming distance: " + (totHammingDistanceMax / 65536));
    }

    public void checkNeighborWindow() {
        initWolframs();
        int equalTuples = 0;
        for (int address = 0; address < 65536; address++) {
            int[][] table = new int[4][4];
            for (int row = 0; row < 4; row++) {
                for (int column = 0; column < 4; column++) {
                    table[row][column] = ((address / (int) Math.pow(2, 4 * row + column)) % 2);
                }
            }
            int[][] neighborTable = new int[4][4];
            int[] tuple = new int[8];
            for (int t = 0; t < 8; t++) {
                tuple[t] = m.minSolutionsAsWolfram[unpackedList[t]][address];
            }
            for (int nextTo = 0; nextTo < 16; nextTo++) {
                for (int row = 1; row < 4; row++) {
                    for (int column = 0; column < 4; column++) {
                        neighborTable[row - 1][column] = table[row][column];
                    }
                }
                for (int column = 0; column < 4; column++) {
                    neighborTable[3][column] = ((nextTo / (int) Math.pow(2, column)) % 2);
                }
                int tot = 0;
                for (int row = 0; row < 4; row++) {
                    for (int column = 0; column < 4; column++) {
                        tot += (int) Math.pow(2, 4 * row + column) * neighborTable[row][column];
                    }
                }
                int[] compTuple = new int[8];
                for (int comp = 0; comp < 8; comp++) {
                    compTuple[comp] = m.minSolutionsAsWolfram[unpackedList[comp]][tot];
                }
                if (Arrays.equals(compTuple, tuple)) {
                    equalTuples++;
                }
            }
        }
        System.out.println("equalTuples: " + equalTuples);
        equalTuples = 0;
        for (int address = 0; address < 65536; address++) {
            if (address % 256 == 0) {
                //System.out.print("address: " + address);
            }
            int[][] table = new int[4][4];
            for (int row = 0; row < 4; row++) {
                for (int column = 0; column < 4; column++) {
                    table[row][column] = ((address / (int) Math.pow(2, 4 * row + column)) % 2);
                }
            }
            int[][] neighborTable = new int[5][5];
            int[] tuple = new int[8];
            for (int t = 0; t < 8; t++) {
                tuple[t] = m.minSolutionsAsWolfram[unpackedList[t]][address];
            }
            tLoop:
            for (int nextTo = 0; nextTo < 512; nextTo++) {
                boolean allAllEqual = true;
                for (int t = 0; t < 8; t++) {
                    for (int row = 0; row < 4; row++) {
                        for (int column = 0; column < 4; column++) {
                            neighborTable[row][column] = table[row][column];
                        }
                    }
                    for (int column = 0; column < 5; column++) {
                        neighborTable[4][column] = ((nextTo / (int) Math.pow(2, column)) % 2);
                    }
                    for (int row = 3; row >= 0; row--) {
                        neighborTable[row][4] = ((nextTo / (int) Math.pow(2, 8 - row)) % 2);
                    }
                    int tot = 0;
                    int[][] abcd = new int[2][2];
                    for (int window = 0; window < 2; window++) {
                        for (int win = 0; win < 2; win++) {
                            tot = 0;
                            for (int row = 0; row < 4; row++) {
                                for (int column = 0; column < 4; column++) {
                                    tot += (int) Math.pow(2, 4 * row + column) * neighborTable[row + window][column + win];
                                }
                            }
                            abcd[window][win] = m.minSolutionsAsWolfram[unpackedList[t]][tot];
                        }
                    }
                    boolean allEqual = true;
                    windowComp:
                    for (int window = 0; window < 2; window++) {
                        for (int win = 0; win < 2; win++) {
                            for (int wi = 0; wi < 2; wi++) {
                                for (int w = 0; w < 2; w++) {
                                    if (abcd[window][win] != abcd[win][wi]) {
                                        allEqual = false;
                                        allAllEqual = false;
                                        break tLoop;
                                    }
                                }
                            }
                        }
                    }
                    if (allEqual) {
                        //equalTuples++;
                    }
                }
                if (allAllEqual) {
                    equalTuples++;
                }
            }
            if (address % 256 == 0) System.out.println("address:  " + address + " equalTuples: " + equalTuples);
        }
        System.out.println("equalTuples: " + equalTuples);
    }

    public void checkNeighborWindow(int numAttempts) {
        initWolframs();
        int equalTuples = 0;
//        for (int address = 0; address < 65536; address++) {
//            int[][] table = new int[4][4];
//            for (int row = 0; row < 4; row++) {
//                for (int column = 0; column < 4; column++) {
//                    table[row][column] = ((address / (int) Math.pow(2, 4 * row + column)) % 2);
//                }
//            }
//            int[][] neighborTable = new int[4][4];
//            int[] tuple = new int[8];
//            for (int t = 0; t < 8; t++) {
//                tuple[t] = m.minSolutionsAsWolfram[unpackedList[t]][address];
//            }
//            for (int nextTo = 0; nextTo < 16; nextTo++) {
//                for (int row = 1; row < 4; row++) {
//                    for (int column = 0; column < 4; column++) {
//                        neighborTable[row - 1][column] = table[row][column];
//                    }
//                }
//                for (int column = 0; column < 4; column++) {
//                    neighborTable[3][column] = ((nextTo / (int) Math.pow(2, column)) % 2);
//                }
//                int tot = 0;
//                for (int row = 0; row < 4; row++) {
//                    for (int column = 0; column < 4; column++) {
//                        tot += (int) Math.pow(2, 4 * row + column) * neighborTable[row][column];
//                    }
//                }
//                int[] compTuple = new int[8];
//                for (int comp = 0; comp < 8; comp++) {
//                    compTuple[comp] = m.minSolutionsAsWolfram[unpackedList[comp]][tot];
//                }
//                if (Arrays.equals(compTuple, tuple)) {
//                    equalTuples++;
//                }
//            }
//        }
        Random rand = new Random();
        System.out.println("equalTuples: " + equalTuples);
        equalTuples = 0;
        for (int address = 0; address < 4096; address++) {
            if (address % 256 == 0) {
                //System.out.print("address: " + address);
            }
            int randAddress = rand.nextInt(0,65536);
            int[][] table = new int[4][4];
            for (int row = 0; row < 4; row++) {
                for (int column = 0; column < 4; column++) {
                    table[row][column] = ((randAddress / (int) Math.pow(2, 4 * row + column)) % 2);
                }
            }
            int[][] neighborTable = new int[5][5];
            int[] tuple = new int[8];
            for (int t = 0; t < 8; t++) {
                tuple[t] = m.minSolutionsAsWolfram[unpackedList[t]][randAddress];
            }
            tLoop:
            for (int nextTo = 0; nextTo < 64; nextTo++) {
                boolean allAllEqual = true;
                int[][][] abcd = new int[16][2][2];
                int randNeighborhood = rand.nextInt(0,512);
                for (int t = 0; t < 8; t++) {
                    for (int row = 0; row < 4; row++) {
                        for (int column = 0; column < 4; column++) {
                            neighborTable[row][column] = table[row][column];
                        }
                    }
                    for (int column = 0; column < 5; column++) {
                        neighborTable[4][column] = ((randNeighborhood / (int) Math.pow(2, column)) % 2);
                    }
                    for (int row = 3; row >= 0; row--) {
                        neighborTable[row][4] = ((randNeighborhood / (int) Math.pow(2, 8 - row)) % 2);
                    }
                    int tot = 0;
                    abcd = new int[16][2][2];
                    for (int window = 0; window < 2; window++) {
                        for (int win = 0; win < 2; win++) {
                            tot = 0;
                            for (int row = 0; row < 4; row++) {
                                for (int column = 0; column < 4; column++) {
                                    tot += (int) Math.pow(2, 4 * row + column) * neighborTable[row + window][column + win];
                                }
                            }
                            abcd[t][window][win] = m.minSolutionsAsWolfram[unpackedList[t]][tot];
                        }
                    }
                    boolean allEqual = true;
                    windowComp:
                    if (allEqual) {
                        //equalTuples++;
                    }
                }
                for (int attempt = 0; attempt < numAttempts; attempt++) {
                    int[][] checkTable = new int[5][5];
                    int[][][] efgh = new int[16][2][2];
                    int nc = rand.nextInt(0, 50);
                    for (int numChanges = 0; numChanges < nc; numChanges++) {
                        checkTable[rand.nextInt(0, 5)][rand.nextInt(0, 5)] ^= rand.nextInt(0, 2);
                    }
                    for (int t = 0; t < 8; t++) {
                        for (int row = 0; row < 4; row++) {
                            for (int column = 0; column < 4; column++) {
                                checkTable[row][column] = table[row][column];
                            }
                        }
                        for (int column = 0; column < 5; column++) {
                            checkTable[4][column] = ((nextTo / (int) Math.pow(2, column)) % 2);
                        }
                        for (int row = 3; row >= 0; row--) {
                            checkTable[row][4] = ((nextTo / (int) Math.pow(2, 8 - row)) % 2);
                        }
                        int tot = 0;
                        abcd = new int[16][2][2];
                        for (int window = 0; window < 2; window++) {
                            for (int win = 0; win < 2; win++) {
                                tot = 0;
                                for (int row = 0; row < 4; row++) {
                                    for (int column = 0; column < 4; column++) {
                                        tot += (int) Math.pow(2, 4 * row + column) * checkTable[row + window][column + win];
                                    }
                                }
                                efgh[t][window][win] = m.minSolutionsAsWolfram[unpackedList[t]][tot];
                            }
                        }
                    }
                    boolean allEqual = true;
                    windowLoop:
                    for (int t = 0; t < 8; t++) {
                        for (int window = 0; window < 2; window++) {
                            for (int win = 0; win < 2; win++) {
                                for (int wi = 0; wi < 2; wi++) {
                                    for (int w = 0; w < 2; w++) {
                                        if (abcd[t][window][win] != efgh[t][win][wi]) {
                                            allEqual = false;
                                            break windowLoop;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (allEqual) {
                        equalTuples++;
                    }
                }

            }
            if (address % 256 == 0) System.out.println("address:  " + address + " equalTuples: " + equalTuples);
        }
        System.out.println("equalTuples: " + equalTuples);
    }
}



