import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class FastMinTransformApril {
    FastMinTransform fmt = new FastMinTransform();

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

    public void checkInverse(int[][] in) {
        fmt.initWolframs();
        int[][][][] depthChart = new int[2][8][in.length][in[0].length];
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                System.out.println("posNeg: " + posNeg + " t: " + t);
                depthChart[posNeg][t] = fmt.minTransform(in, new int[]{posNeg, t / 2, t % 2})[1];
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
                        //int[][] generatedGuess = m.generateGuess(depthChart[posNeg][t][row][column], fmt.unpackedList[t]);
                        int hadamardValue = 0;
                        for (int power = 0; power < 4; power++) {
                            hadamardValue += ((depthChart[posNeg][t][row][column] / (int) Math.pow(2, power)) % 2);
                        }
                        hadamardValue %= 2;
                        for (r = 0; r < 4; r++) {
                            for (c = 0; c < 4; c++) {
                                //int a = (generatedGuess[r][c] );
                                //if (generatedGuess[r][c] == posNeg) {
                                if (hadamardValue == posNeg) {
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

    public int[][][] lrbwFourTemplate() {
        int[][][] out = new int[16][4][4];
        for (int row = 0; row < 16; row++) {
            for (int power = 0; power < 4; power++) {
                out[row][0][power] = row ;
            }
            for (int lr = 0; lr < 2; lr++) {
                for (int power = 0; power < 4 && lr == 1; power++) {
                    int a = power % 2;
                    int b = (power / 2) % 2;
                    int c = 2 * a + b;
                    out[row][1][lr] = c;
                    out[row][3][lr] = c;
                }
                for (int bw = 0; bw < 2; bw++) {
                    int[] temp = new int[4];
                    for (int power = 0; power < 4 && bw == 1; power++) {
                        temp[power] = out[row][lr][3 - power];
                    }
                    for (int power = 0; power < 4 && bw == 1; power++) {
                        out[row][2 * bw + lr][power] = (temp[power] + 1) % 2;
                    }
                }
            }
        }
        return out;
    }

    public int[][] lrbwCodewordTemplate(){
        int[][][] in = lrbwFourTemplate();
        int[][] out = new int[in.length][in[0].length];
        for (int row = 0; row < in.length; row++) {
            for (int lr = 0; lr < 2; lr++) {
                for (int bw = 0; bw < 2; bw++) {
                    for (int power = 0; power < 4; power++) {
                        out[row][2*lr+bw] += (1<<in[row][2*lr+bw][power])*((row>>power)%2);
                    }
                }
            }
        }
        return out;
    }

    public void checkErrorWeights() {
        fmt.initWolframs();
        int totDifferent = 0;
        for (int posNeg = 0; posNeg < 1; posNeg++) {
            for (int t = 0; t < 8; t++) {
                for (int input = 0; input < 16; input++) {
                    int[][] cell = fmt.m.generateGuess(input, fmt.unpackedList[t]);
                    int error = 0;
                    for (int row = 0; row < 4; row++) {
                        for (int column = 0; column < 4; column++) {
                            if (cell[row][column] == 0) {
                                error += (1 << row);
                            } else {
                                error -= (1 << row);
                            }
                        }
                    }
                    int totInInput = 0;
                    for (int power = 0; power < 4; power++) {
                        totInInput += ((input / (int) Math.pow(2, power)) % 2);
                    }
                    totInInput %= 2;
                    System.out.println("totInInput: " + totInInput);
                    System.out.println("error: " + error);
                    int vote = 0;
                    if (error >= 0) vote = 0;
                    else vote = 1;
                    totDifferent += (vote ^ totInInput);
                    System.out.println("vote: " + vote);
                }
            }
        }
        System.out.println("totDifferent: " + totDifferent);
    }

    public void checkCollisions() {
        int[] numCollisions = new int[16];
        int[][] field = new int[5][5];
        int[][] changedField = new int[5][5];
        int[] address = new int[4];
        int[][] codewords = new int[16][4];
        int[] innerAddress = new int[4];
        int[][] innerCodewords = new int[16][4];
        int numTrials = 5000;
        Random rand = new Random();
        fmt.initWolframs();
        for (int trial = 0; trial < numTrials; trial++) {
            for (int row = 0; row < 5; row++) {
                for (int column = 0; column < 5; column++) {
                    field[row][column] = rand.nextInt(0, 2);
                }
            }
            address = new int[4];
            for (int r = 0; r < 2; r++) {
                for (int c = 0; c < 2; c++) {
                    for (int row = 0; row < 4; row++) {
                        for (int column = 0; column < 4; column++) {
                            address[2 * r + c] += (1 << (4 * row + column)) * field[row + r][column + c];
                        }
                    }
                }
            }
            codewords = new int[16][4];
            for (int word = 0; word < 4; word++) {
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        codewords[8 * posNeg + t][word] = fmt.flatWolframs[posNeg][t][address[word]];
                    }
                }
            }
            for (int numChanges = 1; numChanges < 16; numChanges++) {
                for (int tr = 0; tr < numTrials; tr++) {
                    for (int row = 0; row < 5; row++) {
                        for (int column = 0; column < 5; column++) {
                            changedField[row][column] = field[row][column];
                        }
                    }
                    for (int change = 0; change < numChanges; change++) {
                        changedField[rand.nextInt(0, field.length)][rand.nextInt(0, field[0].length)] ^= 1;
                    }
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            for (int row = 0; row < 4; row++) {
                                for (int column = 0; column < 4; column++) {
                                    innerAddress[2 * r + c] += (1 << (4 * row + column)) * field[row + r][column + c];
                                }
                            }
                        }
                    }
                    for (int word = 0; word < 4; word++) {
                        for (int posNeg = 0; posNeg < 2; posNeg++) {
                            for (int t = 0; t < 8; t++) {
                                innerCodewords[8 * posNeg + t][word] = fmt.flatWolframs[posNeg][t][address[word]];
                            }
                        }
                    }
                    if (Arrays.equals(innerCodewords, codewords)) {
                        numCollisions[numChanges]++;
                    }
                }
            }
        }
        System.out.println("numCollisions: " + Arrays.toString(numCollisions));
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

    public int[][] gridOfInt(int size, int inInt) {
        int[][] out = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                out[row][column] = ((inInt >> (4 * row + column)) % 2);
            }
        }
        return out;
    }

    public void checkOneTileSlide() {
        int[][] slidingAddresses = new int[65536][16];
        int[][][] slidingTuples = new int[65536][16][16];
        fmt.initWolframs();
        for (int address = 0; address < 65536; address++) {
            int[][] grid = gridOfInt(4, address);
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    int tot = 0;
                    for (int row = 0; row < 4; row++) {
                        for (int col = 0; col < 4; col++) {
                            tot += (1 << (4 * row + col)) * grid[(row + r) % 4][(col + c) % 4];
                        }
                    }
                    slidingAddresses[address][4 * r + c] = tot;
                    for (int posNeg = 0; posNeg < 2; posNeg++) {
                        for (int t = 0; t < 8; t++) {
                            slidingTuples[address][4 * r + c][8 * posNeg + t] = fmt.flatWolframs[posNeg][t][slidingAddresses[address][4 * r + c]];
                        }
                    }
                }
            }
        }
        Random rand = new Random();
        int numTrials = 500;
        int[][][] changedSlidingTuples = new int[65536][4][16];
        int[][] changedSlidingAddresses = new int[65536][4];
        int numErrors = 0;
        for (int address = 0; address < 65536; address++) {
            if (address % 256 * 16 == 0) System.out.println("address: " + address / 256);
            //for (int numChanges = 1; numChanges < 16; numChanges++) {
            for (int trial = 0; trial < address; trial++) {
                if (address == trial) continue;
//                    int[][] grid = gridOfInt(4,address);
//                    for (int change = 0; change < numChanges; change++) {
//                        grid[rand.nextInt(0, 4)][rand.nextInt(0, 4)] ^= 1;
//                    }
//                    for (int r = 0; r < 2; r++){
//                        for (int c = 0; c < 2; c++){
//                            int tot = 0;
//                            for (int row = 0; row < 4; row++){
//                                for (int col = 0; col < 4; col++){
//                                    tot += (1<<(4*row+col))*grid[(row+r)%4][(col+c)%4];
//                                }
//                            }
//                            changedSlidingAddresses[address][2*r+c] = tot;
//                            for (int posNeg = 0; posNeg < 2; posNeg++){
//                                for (int t = 0; t < 8; t++){
//                                    changedSlidingTuples[address][2*r+c][8*posNeg+t] = fmt.flatWolframs[posNeg][t][slidingAddresses[address][2*r+c]];
//                                }
//                            }
//                        }
//                    }
                if (Arrays.deepEquals(slidingTuples[trial], slidingTuples[address])) {
                    numErrors++;
                    System.out.println("error: " + numErrors);
                }
            }
        }
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

    public void checkOneTileSlideNextDoorRandomized() {
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
        int address;
        int[] otherAddress = new int[25];
        for (int addressCounter = 0; addressCounter < 65536; addressCounter++) {
            if (addressCounter % 256 * 16 == 0) System.out.println("address: " + addressCounter);
            //for (int numChanges = 1; numChanges < 16; numChanges++) {
            address = rand.nextInt(0, 65536);
            for (trial = 0; trial < 512; trial++) {
                trial = rand.nextInt(0, 512);
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
                    add = rand.nextInt(0, 65536);
                    for (tr = 0; tr < 512; tr++) {
                        tr = rand.nextInt(0, 65536);
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
        System.out.println("numErrors = " + numErrors);
    }
}

