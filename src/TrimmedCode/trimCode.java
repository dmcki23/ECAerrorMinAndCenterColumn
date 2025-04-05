package TrimmedCode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class trimCode {


    public int[] binaryParityRowEchelon(int[][] in){
        int size = in.length;
        int[] out = new int[size];
        int[][] active = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                active[row][col] = in[row][col];
            }
        }
        for (int row = 0; row < size; row++){
            for (int col = 0; col < size; col++){
                out[row] ^= active[row][col];
            }
        }
        //customArray.plusArrayDisplay(active,false,false,"initial");

        int activeRow = 0;
        int temp;
        boolean columnActive;
        for (int col = 0; col < size; col++){
            columnActive = false;
            for (int row = activeRow; row < size; row++){
                if (active[row][col] == 1){
                    for (int column = 0; column < size; column++){
                        temp = active[activeRow][column];
                        active[activeRow][column] = active[row][column];
                        active[row][column] = temp;

                    }
                    temp = out[activeRow];
                    out[activeRow] = out[row];
                    out[row] = temp;
                    columnActive = true;
                    break;
                }
            }
            if (columnActive){
                for (int row = 0; row < size; row++){
                    if (row == activeRow || active[row][col] == 0) continue;
                    for (int column = 0; column < size; column++){
                        active[row][column] = (active[row][column] + active[activeRow][column])%2;
                    }
                    out[row] = (out[row]+out[activeRow])%2;
                }
                activeRow++;
            }
            //customArray.plusArrayDisplay(active,false,false,"col: "+col);
            //System.out.println(Arrays.toString(out));

        }
        //customArray.plusArrayDisplay(active,false,false,"final");

        //System.out.println(Arrays.toString(out));
        //System.out.println("\n\n");

        return out;
    }
    public void test(int size){
        int[][] field = new int[size][size];
        for (int trial = 0; trial < 1; trial++){
            for (int row = 0; row < size; row++){
                for (int col = 0; col < size; col++){
                    field[row][col] = rand.nextInt(0,2);
                }
            }
            binaryParityRowEchelon(field);
        }
    }
    public void testAll(int size){
        int numBoards = (int)Math.pow(2,size*size);
        int[][] field = new int[size][size];
        int[] codeword = new int[size];
        int decCodeword = 0;
        int[] codewordDistro = new int[(int)Math.pow(2,size)];
        for (int trial = 0; trial < numBoards; trial++){
            if (trial % 1000 == 0) System.out.println(trial);
            for (int row = 0; row < size; row++){
                for (int col = 0; col < size; col++){
                    field[row][col] = ((trial/(int)Math.pow(2,size*row+col))%2);
                }
            }
            codeword = binaryParityRowEchelon(field);
            decCodeword = 0;
            for (int spot = 0; spot < size; spot++){
                decCodeword += (int)Math.pow(2,spot)*codeword[spot];
            }
            codewordDistro[decCodeword]++;
        }
        System.out.println("codewordDistro[] " + Arrays.toString(codewordDistro));
    }
    public void doAllRulesCoords(int size, boolean doChangeScore, int changeScoreTrials, boolean doRandom, int numTrials, boolean doVoting, int[][] ruleList) {
        int[] list = new int[8];
        for (int spot = 0; spot < 4; spot++) {
            for (int lr = 0; lr < 2; lr++){
                list[2*spot+lr] = ruleList[spot][lr];
                //individualRule(ruleList[spot][lr], size, doChangeScore, changeScoreTrials, doRandom, numTrials, doVoting);
            }
        }
        int listLength = 8;
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
        list = new int[8];
        for (int spot = 0; spot < 4; spot++) {
            for (int lr = 0; lr < 2; lr++){
                //list[2*spot+lr] = ruleList[spot][lr];
                individualRule(ruleList[spot][lr], size, doChangeScore, changeScoreTrials, doRandom, numTrials, doVoting);
            }
        }
        boolean isOnList = false;

        for (int rule = 0; rule < 256; rule++) {
            isOnList = false;
            for (int l = 0; l < 8; l++){
                if (list[l] == rule){
                    isOnList = true;
                }
            }
            if (isOnList == false){
                continue;
            }
            //individualRule(rule, size, doChangeScore, changeScoreTrials, doRandom, numTrials, doVoting);
        }
        minSorted = new int[256];
        maxSorted = new int[256];
        for (int row = 0; row < 256; row++) {
            isOnList = false;
            for (int l = 0; l < 8; l++){
                if (list[l] == row){
                    isOnList = true;
                }
            }
            if (isOnList == false){
                continue;
            }
            minSorted[row] = row;
            maxSorted[row] = row;

        }
        for (int row = 0; row < 256; row++) {
            isOnList = false;
            for (int l = 0; l < 8; l++){
                if (list[l] == row){
                    isOnList = true;
                }
            }
            if (isOnList == false){
                continue;
            }
            for (int column = 0; column < 256; column++) {
                isOnList = false;
                for (int l = 0; l < 8; l++){
                    if (list[l] == column){
                        isOnList = true;
                    }
                }
                if (isOnList == false){
                    continue;
                }
                if (minErrorBuckets[minSorted[row]] > minErrorBuckets[minSorted[column]]) {
                    int temp = minSorted[row];
                    minSorted[row] = minSorted[column];
                    minSorted[column] = temp;
                }
            }
        }
        for (int row = 0; row < 256; row++) {
            isOnList = false;
            for (int l = 0; l < 8; l++){
                if (list[l] == row){
                    isOnList = true;
                }
            }
            if (isOnList == false){
                continue;
            }
            for (int column = 0; column < 256; column++) {
                isOnList = false;
                for (int l = 0; l < 8; l++){
                    if (list[l] == column){
                        isOnList = true;
                    }
                }
                if (isOnList == false){
                    continue;
                }
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
            isOnList = false;
            for (int l = 0; l < 8; l++){
                if (list[l] == row){
                    isOnList = true;
                }
            }
            if (isOnList == false){
                continue;
            }
            minErrorPerArray[row] = minErrorBuckets[row] / numArrays;
            maxErrorPerArray[row] = maxErrorBuckets[row] / numArrays;
            minErrorPerBit[row] = minErrorBuckets[row] / numBits;
            maxErrorPerBit[row] = maxErrorBuckets[row] / numBits;
        }
        System.out.println("minErrorPerArray, sorted");
        System.out.println(Arrays.toString(minSorted));
        System.out.println("maxErrorPerArray, sorted");
        System.out.println(Arrays.toString(maxSorted));
        for (int n = 0; n < 256; n++){
            isOnList = false;
            for (int l = 0; l < 8; l++){
                if (list[l] == n){
                    isOnList = true;
                }
            }
            if (isOnList == false){
                continue;
            }
            System.out.println("n: " + n + " " + minNumberOfSameSolutions[n] + " " + maxNumberOfSameSolutions[n] + " " + Arrays.toString(minSolutionDistro[n]) + " " + Arrays.toString(maxSolutionDistro[n]));
        }
        System.out.println();
        for (int n = 0; n < 256; n++){
            isOnList = false;
            for (int l = 0; l < 8; l++){
                if (list[l] == n){
                    isOnList = true;
                }
            }
            if (isOnList == false){
                continue;
            }
            if (minNumberOfSameSolutions[n] == 1 || maxNumberOfSameSolutions[n] == 1){
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

    public void allSymmetriesOfMinTransform(int[][] in) {
        int[][][] transformedArray = new int[4][in.length][in[0].length];
        CustomArray ca = new CustomArray();
        for (int mirror = 0; mirror < 8; mirror++) {
            int[][] mirrorArr = ca.reflectRotateTranspose(in, mirror);
            for (int posNeg = 0; posNeg < 2; posNeg++) {
                for (int spot = 0; spot < 4; spot++) {
                    for (int lr = 0; lr < 2; lr++) {
                        transformedArray = initializeDepthZero(in, new int[]{posNeg, spot, lr});
                    }
                }
            }
        }
    }
    public void oneHammingChange(int numChanges) {
        initWolframs();
        generateContiguous();
        Random rand = new Random();
        int totExactlyEqual = 0;
        for (int address = 0; address < 65536; address++) {
            if (address % 256 == 0) {
                System.out.println("address: " + address);
            }
            for (int shift = 0; shift < 512; shift++) {
                int[][][] tuple = new int[16][2][2];
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        for (int contig = 0; contig < 2; contig++) {
                            for (int cg = 0; cg < 2; cg++) {
                                tuple[posNeg * 8 + t][contig][cg] = flatWolframs[posNeg][t][contiguous[address][shift][contig][cg]];
                            }
                        }
                    }
                }
                changeLoop:
                for (int trial = 0; trial < numChanges; trial++) {
                    int altered = address ^ (int) Math.pow(2, rand.nextInt(0, 16));
                    for (int change = 1; change < numChanges; change++) {
                        altered = altered ^ (int) Math.pow(2, rand.nextInt(0, 16));
                    }
                    int[][][] tuplee = new int[16][2][2];
                    for (int posNegInner = 0; posNegInner < 2; posNegInner++) {
                        for (int t = 0; t < 8; t++) {
                            for (int contigInner = 0; contigInner < 2; contigInner++) {
                                for (int cgInner = 0; cgInner < 2; cgInner++) {
                                    tuplee[posNegInner * 8 + t][contigInner][cgInner] = flatWolframs[posNegInner][t][contiguous[altered][shift][contigInner][cgInner]];
                                    if (tuplee[posNegInner * 8 + t][contigInner][cgInner] != tuple[posNegInner * 8 + t][contigInner][cgInner]) {
                                        continue changeLoop;
                                    }
                                }
                            }
                        }
                    }
                    System.out.println("address: " + address + " shift: " + shift + " altered: " + altered);
                }
            }
        }
        System.out.println("totExactlyEqual: " + totExactlyEqual);
    }

    public void cnw() {
        generateContiguous();
        initWolframs();
        for (int address = 0; address < 65536; address++) {
            if (address % 256 == 0) System.out.println("address: " + address);
            for (int shift = 0; shift < 512; shift++) {
                int[][][] rtuple = new int[16][2][2];
                for (int r = 0; r < 2; r++) {
                    for (int c = 0; c < 2; c++) {
                        for (int posNeg = 0; posNeg < 2; posNeg++) {
                            for (int spot = 0; spot < 8; spot++) {
                                rtuple[8 * posNeg + spot][r][c] = flatWolframs[posNeg][spot][contiguous[address][shift][r][c]];
                            }
                        }
                    }
                }
                for (int add = 0; add < 65536; add++) {
                    for (int s = 0; s < 512; s++) {
                        if (add == address && s == shift) continue;
                        int[][][] ctuple = new int[16][2][2];
                        for (int r = 0; r < 2; r++) {
                            for (int c = 0; c < 2; c++) {
                                for (int posNeg = 0; posNeg < 2; posNeg++) {
                                    for (int spot = 0; spot < 8; spot++) {
                                        ctuple[8 * posNeg + spot][r][c] = flatWolframs[posNeg][spot][contiguous[add][s][r][c]];
                                    }
                                }
                            }
                        }
                        if (Arrays.deepEquals(rtuple, ctuple)) {
                            System.out.println("isEqual " + add);
                        }
                    }
                }
            }
        }
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
            int randAddress = rand.nextInt(0, 65536);
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
                int randNeighborhood = rand.nextInt(0, 512);
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
    public void generateContiguous() {
        initWolframs();
        buckets = new int[2][8][16][65536 / 16];
        int[][][] bucketsIndices = new int[2][8][16];
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                for (int address = 0; address < 65536; address++) {
                    int a = flatWolframs[posNeg][t][address];
                    buckets[posNeg][t][a][bucketsIndices[posNeg][t][a]] = address;
                    bucketsIndices[posNeg][t][a]++;
                }
            }
        }
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                System.out.println("posNeg: " + posNeg + " t: " + t + " buckets: " + Arrays.toString(bucketsIndices[posNeg][t]));
            }
        }
        //int[][][][] flatWolframWindowMin = new int[8][65536][512][4];
        //int[][][][] flatWolframWindowMax = new int[8][65536][512][4];
        contiguous = new int[65536][512][2][2];
        int[] transpose = new int[65536];
        for (int address = 0; address < 65536; address++) {
            int out = 0;
            for (int row = 0; row < 4; row++) {
                for (int column = 0; column < 4; column++) {
                    out += (int) Math.pow(2, 4 * column + row) * ((address / (int) Math.pow(2, 4 * row + column)) % 2);
                }
            }
            transpose[address] = out;
        }
        for (int address = 0; address < 65536; address++) {
            //if (address % 256 == 0) System.out.println("address: " + address);
            for (int shift = 0; shift < 512; shift++) {
                contiguous[address][shift][0][0] = address;
                contiguous[address][shift][1][0] = (address / 16) + 16 * 16 * 16 * (shift % 16);
                int trans = transpose[address];
                int to = (trans / 16) + 16 * 16 * 16 * ((shift / 16) % 16);
                contiguous[address][shift][0][1] = transpose[to];
                trans = transpose[contiguous[address][shift][1][0]];
                to = (trans / 16) + 16 * 16 * 16 * ((shift / 32) % 16);
                contiguous[address][shift][1][1] = transpose[to];
            }
        }
        System.out.println("has been allocated");
    }
    public void cw() {
        initWolframs();
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                for (int input = 0; input < 65536; input++) {
                    //int[][][][][] next = new int[16][16][16][4][4];
                    for (int nextPossible = 0; nextPossible < 16; nextPossible++) {
                        int[][] next = new int[4][4];
                        for (int row = 0; row < 3; row++) {
                            for (int col = 0; col < 4; col++) {
                                next[row][col] = ((input / (int) Math.pow(2, 4 * (row + 1) + col)) % 2);
                            }
                        }
                        for (int column = 0; column < 4; column++) {
                            next[4][column] = ((nextPossible / (int) Math.pow(2, column)) % 2);
                        }
                        int nextAddress = 0;
                        for (int row = 0; row < 4; row++) {
                            for (int col = 0; col < 4; col++) {
                                nextAddress += (int) Math.pow(2, 4 * row + col);
                            }
                        }
                        if ((flatWolframs[posNeg][t][nextAddress] / 2 % 8) * 2 + flatWolframs[posNeg][t][input] != flatWolframs[posNeg][t][input]) {
                        }
                    }
                }
            }
        }
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
                        xyHeatMap[0][a / 256]++;
                        xyHeatMap[1][b / 256]++;
                        xyHeatMap[2][b % 256]++;
                        xyHeatMap[3][a % 256]++;
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

    public void checkWolframsbyCheckWolframs() {
        int[][] xyHeatMap = new int[4][256];
        initWolframs();
        System.out.println(Arrays.toString(Arrays.copyOfRange(flatWolframs[0][0], 0, 64)));
        int[] map = new int[8355840];
        int index = 0;
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
                        xyHeatMap[0][a / 256]++;
                        xyHeatMap[1][b / 256]++;
                        xyHeatMap[2][b % 256]++;
                        xyHeatMap[3][a % 256]++;
                        int address = 256 * 256 * 256 * a + b;
                        map[index] = address;
                        index++;
                        //map[a][b] = 1;
                        //map[b][a] = 1;
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
        long sameSame = 0;
        int abb;
        int b;
        int d;
        int posNeg;
        int f;
        int add;
        Random rand = new Random();
//        for (int add = 0; add < 8355840; add++) {
//            for (abb = 0; abb < 8355840; abb++) {
//                b = map[abb] % (65536);
//                d = (map[add] / (65536) ) % 65536;
        for (int sample = 0; sample < 10000; sample++) {
            b = rand.nextInt(0, index);
            d = rand.nextInt(0, index);
            for (posNeg = 0; posNeg < 2; posNeg++) {
                for (f = 0; f < 8; f++) {
                    if (flatWolframs[posNeg][f][map[b] % 65536] == flatWolframs[posNeg][f][(map[d] / 65536) % 65536]) {
                        sameSame++;
                    }
                }
            }
        }
        System.out.println("sameSame: " + sameSame);
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
                votes[level] = m.generateWrappedECAsquare(neighborhood, unpackedList[level]);
                votes[level + 8] = m.generateWrappedECAsquare(neighborhood, unpackedList[level]);
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
    public int[][][][] minTransformTwoPathsTwo(int[][] input, int[] wolframTuple) {
        initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        //mirrors, xy, phase, depth, (cell mirrors, minMax tree)
        solutions = new int[8][2][4][5][256 * 256];
        int[][] out = new int[rows][cols];
        int totLength = 256 * 256;
        int[][][] deepInput = new int[4][rows][cols];
        int[][][] init = initializeDepthZero(input, wolframTuple);
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
        int[][][] init = initializeDepthZero(input, wolframTuple);
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
                                    voteLogDepth = m.generateWrappedECAsquare(deepInput[depth - 1][row + phasePower * r][col + phasePower * r], 204);
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

    public void check() throws IOException {
        String filepath = "lion.bmp";
        File file = new File(filepath);
        BufferedImage inImage = ImageIO.read(file);
        int[] inRaster = ((DataBufferInt) inImage.getRaster().getDataBuffer()).getData();
        int[][] binaryArray = new int[inImage.getHeight()][inImage.getWidth() * 32];
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                for (int b = 0; b < 3; b++) {
                    int rgb = (inRaster[inImage.getWidth() * row + column] >> (8 * b)) % 256;
                    if (rgb < 0) rgb = -rgb;
                    for (int bb = 0; bb < 8; bb++) {
                        binaryArray[row][column + 8 * b + bb] = (rgb >> bb) % 2;
                    }
                }
            }
        }
        CustomArray.plusArrayDisplay(binaryArray, true, true, "binaryArray");
        checkInverse(binaryArray);
    }

    public void reconstructFromPrimitives() {
        initWolframs();
        int totEqual = 0;
        int totHamming = 0;
        for (int address = 0; address < 65536; address++) {
            int tot = 0;
            int[][] vote = new int[4][4];
            for (int posNeg = 0; posNeg < 1; posNeg++) {
                for (int t = 0; t < 8; t++) {
                    int[][] guess = m.generateWrappedECAsquare(flatWolframs[posNeg][t][address], unpackedList[t]);
                    for (int r = 0; r < 4; r++) {
                        for (int c = 0; c < 4; c++) {
                            if (guess[r][c] == posNeg) {
                                vote[r][c]+=(int)Math.pow(2,r);
                            } else {
                                vote[r][c]-= (int)Math.pow(2,r);
                            }
                        }
                    }
                }
            }
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    if (vote[r][c] >= 0) {
                        vote[r][c] = 0;
                    } else {
                        vote[r][c] = 1;
                    }
                    tot += (int)Math.pow(2, 4 * r + c) * (vote[r][c] );
                    if (vote[r][c] != ((address/(int)Math.pow(2,4*r+c))%2)){
                        totHamming++;
                    }

                }
            }
            if (tot == address){
                totEqual++;
            }
        }
        System.out.println("totEqual: " + totEqual);
        System.out.println("totHamming: " + totHamming);
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
        int[][][] init = initializeDepthZero(input, wolframTuple);
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
    public void checkInverse(int[][] in) {
        initWolframs();
        int[][][][] depthChart = new int[2][8][in.length][in[0].length];
        for (int posNeg = 0; posNeg < 1; posNeg++) {
            for (int t = 0; t < 8; t++) {
                System.out.println("posNeg: " + posNeg + " t: " + t);
                depthChart[posNeg][t] = initializeDepthZero(in, new int[]{posNeg, t / 2, t % 2})[1];
            }
        }
        int[][] outVotes = new int[in.length][in[0].length];
        int r;
        int c;
        int t;
        int posNeg;
        for (int row = 0; row < in.length; row++) {
            System.out.println("row: " + row + " out of " + in.length);
            for (int column = 0; column < in[0].length; column++) {
                for (posNeg = 0; posNeg < 2; posNeg++) {
                    for (t = 0; t < 8; t++) {
                        int[][] generatedGuess = m.generateWrappedECAsquare(depthChart[posNeg][t][row][column], unpackedList[t]);
                        for (r = 0; r < 4; r++) {
                            for (c = 0; c < 4; c++) {
                                //int a = (generatedGuess[r][c] );
                                if (generatedGuess[r][c] != posNeg) {
                                    outVotes[(row + r) % in.length][(column + c) % in[0].length] += (int) Math.pow(2, r);
                                } else {
                                    outVotes[(row + r) % in.length][(column + c) % in[0].length] -= (int) Math.pow(2, r);
                                }
                            }
                        }
                    }
                }
            }
        }
        int[][] outResult = new int[in.length][in[0].length];
        int[][] outCompare = new int[in.length][in[0].length];
        int totDifferent = 0;
        for (int row = 0; row < in.length; row++) {
            for (int column = 0; column < in[0].length; column++) {
                if (outVotes[row][column] >= 0) {
                    outResult[row][column] = 0;
                } else {
                    outResult[row][column] = 1;
                }
                outCompare[row][column] = outResult[row][column] ^ in[row][column];
                totDifferent += outCompare[row][column];
            }
        }
        System.out.println("totDifferent: " + totDifferent);
        System.out.println("totArea: " + (in.length * in[0].length));
        System.out.println("different/Area=errors/bit= " + ((double) totDifferent / (double) (in.length * in[0].length)));
    }
    public int[][][] minTransformTwoOneByOne(int[][] input, int[] wolframTuple, int depth) {
        initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        //mirrors, xy, phase, depth, (cell mirrors, minMax tree)
        solutions = new int[8][2][4][5][256 * 256];
        int[][] out = new int[rows][cols];
        int totLength = 256 * 256;
        int[][][] deepInput = new int[depth + 1][rows][cols];
        int[][][] init;// = minTransform(input, wolframTuple);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        for (int d = 1; d <= depth; d++) {
            System.out.println("frame " + d + " frames left " + (depth - d));
            for (int row = 0; row < rows; row++) {
                //System.out.print(row + " ");
                //if (row % 32 == 0)System.out.print("\n " + (rows-row) + " ");
                for (int col = 0; col < cols; col++) {
                    int cell = 0;
                    int phasePower = (int) Math.pow(2, 0);
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
    public void checkWolframCollisionLinear() {
        fmt.initWolframs();
        int[][] fourCombs = new int[][]{{0, 1, 2, 3}};
        int[][] oneCombs = PermutationsFactoradic.combinations(4, 3);
        int[][] twoCombs = PermutationsFactoradic.combinations(4, 2);
        int[][] zeroCombs = PermutationsFactoradic.combinations(4, 1);
        int[] combsTally = new int[4];
        int[][][] allCombs = new int[][][]{zeroCombs, oneCombs, twoCombs, fourCombs};
        int errors = 0;
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                for (int column = 0; column < 65536; column++) {
                    for (int next = 0; next < 16; next++) {
                        int address = column;
                        for (int changes = 0; changes < 4; changes++) {
                            for (int row = 0; row < allCombs[changes].length; row++) {
                                for (int index = 0; index < allCombs[changes][row].length; index++) {
                                    address = column ^ (1 << allCombs[changes][row][index]);
                                }
                                if (fmt.flatWolframs[posNeg][t][column] == fmt.flatWolframs[posNeg][t][address]) {
                                    errors++;
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("errors: " + errors);
        errors = 0;
        int[] tuple = new int[16];
        int[] innerTuple = new int[16];
        for (int column = 0; column < 65536; column++) {
            int address = column;
            for (int posNeg = 0; posNeg < 2; posNeg++) {
                for (int t = 0; t < 8; t++) {
                    tuple[posNeg * 8 + t] = fmt.flatWolframs[posNeg][t][address];
                }
            }
            for (int changes = 0; changes < 4; changes++) {
                for (int row = 0; row < allCombs[changes].length; row++) {
                    address = column;
                    for (int index = 0; index < allCombs[changes][row].length; index++) {
                        address ^= (1 << (12 + allCombs[changes][row][index]));
                    }
                    for (int posNeg = 0; posNeg < 2; posNeg++) {
                        for (int t = 0; t < 8; t++) {
                            innerTuple[posNeg * 8 + t] = fmt.flatWolframs[posNeg][t][address];
                        }
                    }
                    if (Arrays.equals(innerTuple, tuple)) {
                        errors++;
                        combsTally[changes]++;
                    }
                }
            }
        }
        System.out.println("errors: " + errors);
        System.out.println("combsTally: " + Arrays.toString(combsTally));
        int[] nextTuple = new int[16];
        int[] nextInnerTuple = new int[16];
        combsTally = new int[4];
        errors = 0;
        for (int column = 0; column < 65536; column++) {
            for (int next = 0; next < 16; next++) {
                int address = column;
                int nextAddress = 0;
                for (int row = 0; row < 4; row++) {
                    for (int col = 1; col < 4; col++) {
                        nextAddress += (1 << (4 * row + col - 1)) * ((column / (1 << (4 * row + col)) % 2));
                    }
                }
                for (int row = 0; row < 4; row++) {
                    nextAddress += (1 << (4 * row + 3)) * ((next / (1 << row)) % 2);
                }
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        tuple[posNeg * 8 + t] = fmt.flatWolframs[posNeg][t][address];
                        nextTuple[posNeg * 8 + t] = fmt.flatWolframs[posNeg][t][nextAddress];
                    }
                }
                for (int changes = 0; changes < 4; changes++) {
                    for (int row = 0; row < allCombs[changes].length; row++) {
                        address = column;
                        int innerNext = nextAddress;
                        for (int index = 0; index < allCombs[changes][row].length; index++) {
                            address ^= (1 << (12 + allCombs[changes][row][index]));
                            if (allCombs[changes][row][index] > 0) {
                                innerNext ^= (1 << (12 + allCombs[changes][row][index] - 1));
                            }
                        }
                        for (int posNeg = 0; posNeg < 2; posNeg++) {
                            for (int t = 0; t < 8; t++) {
                                innerTuple[posNeg * 8 + t] = fmt.flatWolframs[posNeg][t][address];
                                nextInnerTuple[posNeg * 8 + t] = fmt.flatWolframs[posNeg][t][innerNext];
                            }
                        }
                        if (Arrays.equals(innerTuple, tuple)) {
                            //errors++;
                            //combsTally[changes]++;
                        }
                        if (Arrays.equals(nextInnerTuple, nextTuple)) {
                            if (Arrays.equals(innerTuple, tuple)) {
                                errors++;
                                combsTally[changes]++;
                            }
                        }
                    }
                }
            }
        }
        System.out.println("errors: " + errors);
        System.out.println("combsTally: " + Arrays.toString(combsTally));
        System.out.println("errors: " + errors);
        System.out.println("combsTally: " + Arrays.toString(combsTally));
        nextTuple = new int[16];
        nextInnerTuple = new int[16];
        combsTally = new int[4];
        errors = 0;
        allCombs = new int[4][1][1];
        for (int changes = 1; changes < 4; changes++) {
            allCombs[changes] = PermutationsFactoradic.combinations(8, changes);
        }
        int[] slidingCodewords = new int[8];
        int[][] slidingTuples = new int[8][16];
        int[][] changedSlidingTuples = new int[8][16];
        int[][] grid;
        int row;
        int column;
        int phase;
        int posNeg;
        int changes;
        int t;
        int next;
        int[] changedCodewords;
        for (int first = 0; first < 65536; first++) {
            if (first % 256 == 0) System.out.println("first: " + (first / 256));
            for (next = 0; next < 65536; next++) {
                grid = new int[8][4];
                slidingCodewords = new int[8];
                slidingTuples = new int[8][16];
                for (row = 0; row < 4; row++) {
                    for (column = 0; column < 4; column++) {
                        grid[row][column] = ((first >> (4 * row + column)) % 2);
                    }
                }
                for (phase = 0; phase < 8; phase++) {
                    for (row = 0; row < 4; row++) {
                        for (column = 0; column < 4; column++) {
                            slidingCodewords[phase] += (1 << (4 * row + column)) * grid[(row + phase) % 8][column];
                        }
                    }
                }
                for (phase = 0; phase < 8; phase++) {
                    for (posNeg = 0; posNeg < 2; posNeg++) {
                        for (t = 0; t < 8; t++) {
                            slidingTuples[phase][posNeg * 8 + t] = fmt.flatWolframs[posNeg][t][slidingCodewords[phase]];
                        }
                    }
                }
                for (changes = 1; changes < 2; changes++) {
                    for (row = 0; row < allCombs[changes].length; row++) {
                        changedCodewords = new int[8];
                        for (int index = 0; index < allCombs[changes][row].length; index++) {
                            for (phase = 0; phase < 8; phase++) {
                                changedCodewords[phase] = slidingCodewords[phase] ^ (1 << (((12 + allCombs[changes][row][index]) - phase + 8) % 8));
                            }
                        }
                        for (phase = 0; phase < 8; phase++) {
                            for (posNeg = 0; posNeg < 2; posNeg++) {
                                for (t = 0; t < 8; t++) {
                                    changedSlidingTuples[phase][8 * posNeg + t] = fmt.flatWolframs[posNeg][t][changedCodewords[phase]];
                                }
                            }
                        }
                        if (Arrays.equals(changedSlidingTuples, slidingTuples)) {
                            errors++;
                            combsTally[changes]++;
                            System.out.println("error: " + Arrays.toString(combsTally));
                        }
                    }
                }
            }
        }
        System.out.println("errors: " + errors);
        System.out.println("combsTally: " + Arrays.toString(combsTally));
    }
    public void checkOneTileSlideNextDoor() {
        int[][] slidingAddresses = new int[65536][4];
        int[][][] slidingTuples = new int[65536][4][16];
        fmt.initWolframs();
        for (int address = 0; address < 65536; address++) {
            int[][] grid = gridOfInt(4, address);
            for (int r = 0; r < 2; r++) {
                for (int c = 0; c < 2; c++) {
                    int tot = 0;
                    for (int row = 0; row < 4; row++) {
                        for (int col = 0; col < 4; col++) {
                            tot += (1 << (4 * row + col)) * grid[(row + r) % 4][(col + c) % 4];
                        }
                    }
                    slidingAddresses[address][2 * r + c] = tot;
                    for (int posNeg = 0; posNeg < 2; posNeg++) {
                        for (int t = 0; t < 8; t++) {
                            slidingTuples[address][2 * r + c][8 * posNeg + t] = fmt.flatWolframs[posNeg][t][slidingAddresses[address][2 * r + c]];
                        }
                    }
                }
            }
        }
        Random rand = new Random();
        int numTrials = 500;
        int[][] changedSlidingTuples = new int[25][16];
        int[][] otherTuple = new int[25][16];
        int[] changedSlidingAddresses = new int[25];
        int numErrors = 0;
        int trial;
        int[][] ingrid;
        int[][] grid;
        int power;
        int r;
        int c;
        int row;
        int col;
        int posNeg;
        int t;
        int tot;
        int tr;
        int add;
        double errorRate = 0;
        int[] otherAddress = new int[25];
        for (int address = 0; address < 65536; address++) {
            if (address % 256 * 16 == 0) System.out.println("address: " + address / 256);
            //for (int numChanges = 1; numChanges < 16; numChanges++) {
            for (trial = 0; trial < 512; trial++) {
                //ingrid = gridOfInt(4, address);
                grid = new int[5][5];
                for (row = 0; row < 4; row++) {
                    for (col = 0; col < 4; col++) {
                        grid[row][col] = (address >> (4 * row + col)) % 2;
                    }
                }
                for (power = 0; power < 5; power++) {
                    grid[4][power] = ((trial / (1 << power)) % 2);
                }
                for (power = 0; power < 4; power++) {
                    grid[3 - power][4] = ((trial / (1 << (power + 5))) % 2);
                }
                //if (address == trial) continue;
                //  int[][] grid = gridOfInt(4,address);
                //for (int change = 0; change < numChanges; change++) {
                //  grid[rand.nextInt(0, 4)][rand.nextInt(0, 4)] ^= 1;
                //}
                for (r = 0; r < 5; r++) {
                    for (c = 0; c < 5; c++) {
                        tot = 0;
                        for (row = 0; row < 4; row++) {
                            for (col = 0; col < 4; col++) {
                                tot += (1 << (4 * row + col)) * grid[(row + r) % 5][(col + c) % 5];
                            }
                        }
                        changedSlidingAddresses[5 * r + c] = tot;
                        for (posNeg = 0; posNeg < 2; posNeg++) {
                            for (t = 0; t < 8; t++) {
                                changedSlidingTuples[5 * r + c][8 * posNeg + t] = fmt.flatWolframs[posNeg][t][changedSlidingAddresses[5 * r + c]];
                            }
                        }
                    }
                }
                for (add = 0; add < 65536; add++) {
                    for (tr = 0; tr < 512; tr++) {
                        if (add == address && trial == tr) continue;
                        //ingrid = gridOfInt(4, add);
                        grid = new int[5][5];
                        for (row = 0; row < 4; row++) {
                            for (col = 0; col < 4; col++) {
                                grid[row][col] = (add >> (4 * row + col)) % 2;
                            }
                        }
                        for (power = 0; power < 5; power++) {
                            grid[4][power] = ((tr / (1 << power)) % 2);
                        }
                        for (power = 0; power < 4; power++) {
                            grid[3 - power][4] = ((tr / (1 << (power + 5))) % 2);
                        }
                        //if (address == trial) continue;
                        //  int[][] grid = gridOfInt(4,address);
                        //for (int change = 0; change < numChanges; change++) {
                        //  grid[rand.nextInt(0, 4)][rand.nextInt(0, 4)] ^= 1;
                        //}
                        for (r = 0; r < 5; r++) {
                            for (c = 0; c < 5; c++) {
                                tot = 0;
                                for (row = 0; row < 4; row++) {
                                    for (col = 0; col < 4; col++) {
                                        tot += (1 << (4 * row + col)) * grid[(row + r) % 5][(col + c) % 5];
                                    }
                                }
                                otherAddress[5 * r + c] = tot;
                                for (posNeg = 0; posNeg < 2; posNeg++) {
                                    for (t = 0; t < 8; t++) {
                                        otherTuple[5 * r + c][8 * posNeg + t] = fmt.flatWolframs[posNeg][t][otherAddress[5 * r + c]];
                                    }
                                }
                            }
                        }
                        if (Arrays.deepEquals(changedSlidingTuples, otherTuple)) {
                            numErrors++;
                            double aLoops = 512 * address + trial;
                            double bLoops = 512 * add + tr;
                            errorRate = numErrors / (aLoops * (65536 * 512) + bLoops);
                            System.out.println("error: " + numErrors + " errors/attempt = " + errorRate);
                            System.out.println("attempts/error = " + (1 / errorRate));
                        }
                    }
                }
            }
        }
    }
}
