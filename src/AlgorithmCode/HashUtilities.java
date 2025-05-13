package AlgorithmCode;

import CustomLibrary.CustomArray;
import CustomLibrary.PermutationsFactoradic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Random;

public class HashUtilities {
    /**
     * Middle layer of transform code
     */
    public Hash hash = new Hash();
    int[] singles;
    int[][] doubles;
    int[] shiftedSingles;
    int[][] shiftedDoubles;
    int[][][] everything;
    int[][][] shiftedEverything;
    int[][][] trackedZero;
    /**
     * Every 0-65536 4x4 binary array's ecaHashTransform
     */
    int[] truthTableTransform;
    /**
     * First generate a 4x4 binary array based on a function such as row ^ column % 2, (row + column)%2
     * then rearrange the truth table such that the function is zero and the 0-65536 variable is relative
     * to XOR the function
     */
    int[] relativeTruthTable;

    public HashUtilities() {
        hash.hashUtilities = this;
    }

    /**
     * First generate a 4x4 binary array based on a function such as row ^ column % 2, (row + column)%2
     * then rearrange the truth table such that the function is zero and the 0-65536 variable is relative
     * to XOR the function
     *
     * @return the truth table with the function provided as zero
     */
    public int[] generateRelativeToFunctionTile() {
        int size = 4;
        relativeTruthTable = new int[65536];
        int[][] functionTile = generateFunctionTile(4);
        for (int address = 0; address < 65536; address++) {
            int[][] changeTile = hash.hashRows.addressToArray(4, address);
            int[][] changed = new int[size][size];
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    changed[row][col] = functionTile[row][col] ^ changeTile[row][col];
                }
            }
            relativeTruthTable[address] = truthTableTransform[hash.hashRows.addressTileToInteger(changed)];
        }
        return relativeTruthTable;
    }

    public void zerosRelativeTruthTable(boolean rowError) {
        generateAbsolutelyEverything(4, rowError);
        int[][] zeros = new int[256][256];
        generateRelativeToFunctionTile();
        for (int address = 0; address < 65536; address++) {
            if (relativeTruthTable[address] == 0) {
                zeros[address / 256][address % 256] = 1;
            }
        }
        CustomArray.plusArrayDisplay(zeros, true, true, "zeros relative truth table");
        zeros = PermutationsFactoradic.grayify(zeros);
        CustomArray.plusArrayDisplay(zeros, true, true, "Grayed - zeros relative truth table");
        int[][] h = hash.hashRows.hadamard.generateHadamardBoolean(256);
        int[][] temp = new int[256][256];
        for (int row = 0; row < 256; row++) {
            for (int col = 0; col < 256; col++) {
                temp[row][col] = h[row][col];
            }
        }
        int[][] hByzeros = new int[256][256];
        for (int row = 0; row < 256; row++) {
            for (int col = 0; col < 256; col++) {
                hByzeros[row][col] = temp[row][col] ^ zeros[row][col];
            }
        }
        CustomArray.plusArrayDisplay(hByzeros, true, true, "hByzeros relative truth table");
    }

    public int[][][] generateEveryLogicFunctionShifted(int size) {
        int logLength = (int) (Math.log(size) / Math.log(2));
        int[][][] out = new int[size * size * 16 * logLength][size][size];
        int[][][] temp = new int[16 * logLength][size][size];
        for (int gate = 0; gate < 16; gate++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    for (int place = 0; place < logLength; place++) {
                        temp[logLength * gate + place][row][col] = (2 * ((row >> place) % 2) + (col >> place) % 2);
                        temp[logLength * gate + place][row][col] = ((gate >> temp[gate][row][col])) % 2;
                    }
                }
            }
        }
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                for (int gate = 0; gate < 16; gate++) {
                    for (int row = 0; row < size; row++) {
                        for (int col = 0; col < size; col++) {
                            for (int place = 0; place < logLength; place++) {
                                out[(size * r + c) * logLength * gate + place][row][col] = temp[logLength * gate + place][(row + r) % size][(col + c) % size];
                            }
                        }
                    }
                }
            }
        }
        return out;
    }

    public int[][][][] generateEveryECAfunctionTable(int size) {
        int logLength = (int) (Math.log(size) / Math.log(2));
        int[][][][] out = new int[256 * logLength][size][size][size];
        for (int gate = 0; gate < 256; gate++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    for (int zee = 0; zee < size; zee++) {
                        for (int place = 0; place < logLength; place++) {
                            int tot = 4 * ((row >> place) % 2) + 2 * ((col >> place) % 2) + ((zee >> place) % 2);
                            out[logLength * gate + place][row][col][zee] = ((gate >> tot) % 2);
                        }
                    }
                }
            }
        }
        return out;
    }

    public int[][][] generateEveryLogicFunction(int size) {
        int logLength = (int) (Math.log(size) / Math.log(2));
        int[][][] out = new int[16 * logLength][size][size];
        for (int gate = 0; gate < 16; gate++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    for (int place = 0; place < logLength; place++) {
                        out[logLength * gate + place][row][col] = (2 * ((row >> place) % 2) + ((col >> place) % 2));
                        out[logLength * gate + place][row][col] = ((gate >> out[logLength * gate + place][row][col])) % 2;
                    }
                }
            }
        }
        int[][][] temp = new int[size * size * 16 * logLength][size][size];
        for (int index = 0; index < out.length; index++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    for (int r = 0; r < size; r++) {
                        for (int c = 0; c < size; c++) {
                            temp[index * size * size + size * row + col][r][c] = out[index][(row + r) % size][(col + c) % size];
                        }
                    }
                }
            }
        }
//        for (int gate = 0; gate < 16; gate++) {
//            for (int place = 0; place < logLength; place++) {
//                temp[logLength * gate + place] = out[logLength * gate + place];
//                for (int row = 0; row < size; row++) {
//                    for (int col = 0; col < size; col++) {
//                        temp[logLength * gate + place + logLength * 16][row][col] = (out[logLength * gate + place][row][col] + 1) % 2;
//                    }
//                }
//                //CustomArray.plusArrayDisplay(out[logLength * gate + place], true, false, "gate " + gate + " place " + place);
//            }
//        }
        return out;
    }

    public int[][] doTransform(int[][] input, int depth, int subsetIndex) {
        int rows = input.length;
        int cols = input[0].length;
        int[][] out = new int[rows][cols];
        int[][][] temp = new int[4][rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                //gets its neighborhood
                int cell = 0;
                int phasePower = (int) Math.pow(2, depth);
                for (int r = 0; r < 2; r++) {
                    for (int c = 0; c < 2; c++) {
                        cell += (int) Math.pow(16, 2 * r + c) * input[(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                    }
                }
                //stores the neighborhood's codeword
                out[row][col] = (truthTableTransform[cell] % 16);
                for (int r = 0; r < 2; r++) {
                    for (int c = 0; c < 2; c++) {
                        //out[(row + phasePower * r) % rows][(col + phasePower * c) % cols] = ((truthTableTransform[cell] / (int) Math.pow(16, 2 * r + c))) % 16;
                    }
                }
                out[row][col] = 15 - out[row][col];
            }
        }
//        for (int row = 0; row < rows; row++) {
//            for (int col = 0; col < cols; col++) {
//                int cell = 0;
//                for (int r = 0; r < 2; r++){
//                    for (int c = 0; c < 2; c++){
//                        cell += (int)Math.pow(16,2*r+c)*temp[2*r+c][row][col];
//                    }
//                }
//                int index = subsetIndex%8;
//                int negSign = subsetIndex/8;
//                //out[row][col] = hash.flatWolframs[negSign][index][cell];
//                //out[row][col] = truthTableTransform[cell]%16;
//            }
//        }
        return out;
    }

    public int[][] doTransformNormalizePixels(int[][] input, int depth) {
        int rows = input.length;
        int cols = input[0].length;
        depth = 1;
        int[][] out = new int[input.length][input[0].length];
        for (int iter = 0; iter < depth; iter++) {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    //gets its neighborhood
                    int cell = 0;
                    int phasePower = (1 << (iter));
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            cell += (int) Math.pow(16, 2 * r + c) * input[(row) % rows][(col + phasePower * (2 * r + c)) % cols];
                        }
                    }
                    //stores the neighborhood's codeword
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            out[(row) % rows][(col + phasePower * (2 * r + c)) % cols] = (truthTableTransform[cell] >> (4 * (2 * r + c))) % 16;
                        }
                    }
                    //out[row][col] = (truthTableTransform[cell] % 16);
                }
            }
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    input[row][col] = 15 - out[row][col];
                }
            }
        }
        return input;
    }



    public void testWriteToFile(boolean rowError) throws IOException {
        generateAbsolutelyEverything(4, rowError);
        writeToFile();
        int[][] comp = new int[2][65536];
        int[][][][] flatComp = new int[2][2][8][65536];
        for (int row = 0; row < 65536; row++) {
            comp[0][row] = truthTableTransform[row];
        }
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                for (int index = 0; index < 65536; index++) {
                    flatComp[0][posNeg][t][index] = hash.flatWolframs[posNeg][t][index];
                }
            }
        }
        System.out.println("doneWritingToFile");
        readFromFile();
        for (int row = 0; row < 65536; row++) {
            comp[1][row] = truthTableTransform[row];
        }
        int numDifferent = 0;
        for (int row = 0; row < 65536; row++) {
            if (comp[0][row] != comp[1][row]) {
                numDifferent++;
            }
        }
        System.out.println("numDifferent: " + numDifferent);
        numDifferent = 0;
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                for (int index = 0; index < 65536; index++) {
                    flatComp[1][posNeg][t][index] = hash.flatWolframs[posNeg][t][index];
                    if (flatComp[0][posNeg][t][index] != flatComp[1][posNeg][t][index]) {
                        numDifferent++;
                    }
                }
            }
        }
        System.out.println("numDifferent: " + numDifferent);
    }

    public void writeToFile() throws IOException {
        //generateAbsolutelyEverything(4);
        File file = new File("fourByfour.dat");
        FileOutputStream out = new FileOutputStream(file);
        ByteBuffer buffer = ByteBuffer.allocate(truthTableTransform.length * 4);
        for (int index = 0; index < truthTableTransform.length; index++) {
            buffer.putInt(truthTableTransform[index]);
        }
        out.write(buffer.array());
        out.close();
        file = new File("minMaxCodewords.dat");
        out = new FileOutputStream(file);
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                buffer = ByteBuffer.allocate(65536 * 4);
                for (int index = 0; index < 65536; index++) {
                    buffer.putInt(hash.flatWolframs[posNeg][t][index]);
                }
                out.write(buffer.array());
            }
        }
        out.close();
    }

    public void readFromFile() throws IOException {
        File file = new File("fourByfour.dat");
        FileInputStream in = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        in.read(data);
        truthTableTransform = new int[65536];
        IntBuffer intBuf =
                ByteBuffer.wrap(data)
                        .order(ByteOrder.BIG_ENDIAN)
                        .asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(truthTableTransform);
        in.close();
        file = new File("minMaxCodewords.dat");
        in = new FileInputStream(file);
        data = new byte[(int) file.length()];
        hash.flatWolframs = new int[2][8][65536];
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                for (int index = 0; index < 65536; index++) {
                    for (int place = 0; place < 4; place++) {
                        hash.flatWolframs[posNeg][t][index] += (int) Math.pow(4, place) * (int) data[4 * index + place];
                    }
                }
            }
        }
    }

    public int[][][] generateAddSubtractMultiplyFunction(int size) {
        int logLength = (int) (Math.log(size) / Math.log(2));
        int numKinds = 3;
        int[][][] out = new int[numKinds * size * size][size][size];
        for (int coeff = 0; coeff < size * size; coeff++) {
            int a = coeff / size;
            int b = coeff % size;
            a++;
            b++;
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    out[numKinds * coeff][row][col] = (a * row + b * col) % 2;
                }
            }
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    out[numKinds * coeff + 1][row][col] = (a * row - b * col) % 2;
                }
            }
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    out[numKinds * coeff + 2][row][col] = (a * row * b * col) % 2;
                }
            }
        }
        return out;
    }

    public int[][][] generateHadamardWaves(int size) {
        Hadamard hadamard = new Hadamard();
        int[][][] out = new int[size * size][size][size];
        int[][] h = hadamard.generateHadamardBoolean(size * size);
        for (int row = 0; row < size * size; row++) {
            for (int col = 0; col < size * size; col++) {
                out[row][col / size][col % size] = h[row][col];
            }
        }
        return out;
    }

    public int[][][] generateEveryHadamardishFunction(int size) {
        int[][][] out = new int[16][size][size];
        int logLength = (int) (Math.log(size) / Math.log(2));
        int[][] cell = new int[2][2];
        int iterationsRequired = logLength - 1;
        for (int initial = 0; initial < 16; initial++) {
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 2; col++) {
                    cell[row][col] = (initial >> (2 * row + col)) % 2;
                    cell[row][col] = (cell[row][col] == 0) ? 1 : -1;
                }
            }
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 2; col++) {
                    out[initial][row][col] = cell[row][col];
                }
            }
            for (int iteration = 0; iteration < iterationsRequired; iteration++) {
                int halfSize = (1 << (iteration + 1));
                int[][] temp = new int[size][size];
                for (int row = 0; row < halfSize; row++) {
                    for (int col = 0; col < halfSize; col++) {
                        temp[row][col] = out[initial][row][col] * cell[0][0];
                        temp[row + halfSize][col] = out[initial][row][col] * cell[0][1];
                        temp[row][col + halfSize] = out[initial][row][col] * cell[1][0];
                        temp[row + halfSize][col + halfSize] = out[initial][row][col] * cell[1][1];
                    }
                }
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        out[initial][row][col] = temp[row][col];
                    }
                }
            }
        }
        for (int initial = 0; initial < 16; initial++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    out[initial][row][col] = (out[initial][row][col] == 1) ? 0 : 1;
                }
            }
            //CustomArray.plusArrayDisplay(out[initial], false, true, "initial: " + initial);
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    //out[initial][row][col] = (out[initial][row][col] == 1) ? 0 : 1;
                }
            }
        }
        return out;
    }

    public int[][] flattenFunctionSets(int[][][] in) {
        int size = in[0].length;
        int[][] out = new int[in.length][size * size];
        for (int layer = 0; layer < in.length; layer++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    out[layer][size * row + col] = in[layer][row][col];
                }
            }
        }
        return out;
    }

    /**
     * Outputs a user supplied binary function f(row,column), a logic function or a linear function
     * Any logic operator, any pluses or minuses or multiplications or coefficients, everything
     * taken mod 2, every single one of these functions goes to zero in the ecaHashTransform,
     * and then only at most half of any hamming changes made produce non-zero terms. These functions all go to zero,
     * and then there is a linear filter that produces simple patterns of zeros in the truth table relative to the function.
     * The hash function is a lossy compression. The algorithm compresses it with linear codewords, 0,15,51,85,170,204,240,255
     * are all linear rules. So the inverse, the decompression only has linear input to work with. The inverse of the hash
     * loses any linearity whatsoever. If you hashed a 2x4 and inverted it the grain of the wood would
     *
     * @param size side of square array
     * @return a square binary array of f(row,column)
     */
    public int[][] generateFunctionTile(int size) {
        Hadamard hadamard = new Hadamard();
        int[][] h = hadamard.generateHadamardBoolean(4);
        int[][] out = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                //
                //
                //This one the zeros are at !((row^col)%2)
                //The offset extra change variable location is irrelevant
                //to the location of the fish
                out[row][col] = (row ^ col) % 2;
                //
                //
                //This one is the same as first one
                //out[row][col] = (row + col) % 2;
                //
                //
                //This one produces zeros with 5! = 120 votes at every cell, weighted, in the reconstruction finalOutput array
                //out[row][col] = (row & col) % 2;
                //
                //
                //This one is all ones unless the extra change produces that odd and evens trackedZero matrix
                //the trackedZero matrix doesn't apply to the other equations
                //out[row][col] = (row | col) % 2;
                //
                //
                //Same as the first one
                //out[row][col] = ((row*3)^(col*7)+2)%2;
                //
                //
                //Same as the first one
                //out[row][col] = ((3*row)+col)%2;
                //
                //
                //out[row][col] = h[row][col]%2;
            }
        }
        return out;
    }

    public void checkDoublesSetsFunctionsTwo() throws IOException {
        //generateAbsolutelyEverything(4);
        readFromFile();
        //generateRelativeToFunctionTile();
        int size = 4;
        int sizeSquare = 16;
        int[][] oneChangeZeros = new int[sizeSquare][sizeSquare];
        int[][][] logicFunctions = generateEveryLogicFunction(16);
        int[][][][] cubicLogic = generateEveryECAfunctionTable(16);
        int[][][] hadamardishFunctions = generateEveryHadamardishFunction(16);
        int[] addressAccountedFor = new int[65536];
        int[] workingLogicFunctions = new int[logicFunctions.length];
        int[] workingHadamardishFunctions = new int[hadamardishFunctions.length];
        int[] workingECA = new int[256];
        int[][][] addSubMult = generateAddSubtractMultiplyFunction(16);
        int[][] both = new int[16][16];
        int[][][] hRows = generateHadamardWaves(16);
        int[][][] differentOneChangeZeros = new int[65536][sizeSquare][sizeSquare];
        int[][][] differentBoth = new int[65536][sizeSquare][sizeSquare];
        int numDiffOneChangeZeros = 0;
        int numDiffBoth = 0;
        Hadamard hadamard = new Hadamard();
        gateLoop:
        for (int address = 0; address < 65536; address++) {
            oneChangeZeros = new int[sizeSquare][sizeSquare];
            both = new int[sizeSquare][sizeSquare];
            for (int row = 0; row < sizeSquare; row++) {
                if (truthTableTransform[address ^ (1 << row)] == 0) {
                    //oneChangeZeros[row][0] = 1;
                    //addressAccountedFor[address ^ (1 << row)] = 1;
                    //firstZero = true;
                }
                for (int col = 0; col < sizeSquare; col++) {
                    int[][] firstChange = hash.hashRows.addressToArray(4, address ^ (1 << row));
                    int[][] secondChange = hash.hashRows.addressToArray(4, address ^ (1 << col));
                    int[][] bothChanges = hash.hashRows.addressToArray(4, address ^ (1 << row) ^ (1 << col));
                    boolean firstZero = false;
                    if (truthTableTransform[address ^ (1 << row)] == 0) {
                        oneChangeZeros[row][col] = 1;
                        //addressAccountedFor[address ^ (1 << row)] = 1;
                        firstZero = true;
                    }
                    boolean secondZero = false;
                    if (truthTableTransform[address ^ (1 << col)] == 0) {
                        oneChangeZeros[row][col] = 1;
                        //addressAccountedFor[address ^ (1 << col)] = 1;
                        secondZero = true;
                    }
                    if (truthTableTransform[address ^ (1 << row) ^ (1 << col)] == 0) {
                        //addressAccountedFor[address ^ (1 << row) ^ (1 << col)]--;
                        both[row][col] = 1;
                        //oneChangeZeros[row][col] = 1;
                        //oneChangeZeros[row][col] = 1;
                    }
                    //if (firstZero || secondZero) continue;
                }
            }
            boolean alreadyExists = false;
            for (int index = 0; index < numDiffOneChangeZeros; index++) {
                if (Arrays.deepEquals(oneChangeZeros,differentOneChangeZeros[index])) {
                    alreadyExists = true;
                }
            }
            if (!alreadyExists) {
                differentOneChangeZeros[numDiffOneChangeZeros] = oneChangeZeros;
                numDiffOneChangeZeros++;
            }
            alreadyExists = false;
            for (int index = 0; index < numDiffBoth; index++) {
                if (Arrays.deepEquals(both,differentBoth[index])) {
                    alreadyExists = true;
                }
            }
            if (!alreadyExists) {
                differentBoth[numDiffBoth] = both;
                numDiffBoth++;
            }
            for (int function = 0; function < logicFunctions.length; function++) {
                if (Arrays.deepEquals(oneChangeZeros, logicFunctions[function])) {
                    workingLogicFunctions[function]++;
                    //addressAccountedFor[address ^ (1 << row) ^ (1 << col)] = 2;
                    for (int row = 0; row < sizeSquare; row++) {
                        //if (oneChangeZeros[row][0]  == 1) addressAccountedFor[address ^ (1 << row)] |= 1;
                        for (int col = 0; col < sizeSquare; col++) {
                            //if (oneChangeZeros[row][col] == 1) {
                            addressAccountedFor[address ^ (1 << row)] |= 1;
                            addressAccountedFor[address ^ (1 << col)] |= 1;
                            //}
                            //addressAccountedFor[address ^ (1 << row)] = 1 | addressAccountedFor[address ^ (1 << row)];
                            //addressAccountedFor[address ^ (1 << col)] = 1 | addressAccountedFor[address ^ (1 << col)];
                            //if (both[row][col] == 1) {
                            addressAccountedFor[address ^ (1 << row) ^ (1 << col)] |= 8 * both[row][col];
                            //}
                            //addressAccountedFor[address ^ (1 << row)] = 1;
                            //addressAccountedFor[address ^ (1 << col)] = 1;
                        }
                    }
                    //addressAccountedFor[address] = 1;
                }
                if (Arrays.deepEquals(both, logicFunctions[function])) {


                    workingLogicFunctions[function]++;
                    //addressAccountedFor[address ^ (1 << row) ^ (1 << col)] = 2;
                    for (int row = 0; row < sizeSquare; row++) {
                        //if (oneChangeZeros[row][0]  == 1) addressAccountedFor[address ^ (1 << row)] |= 1;
                        for (int col = 0; col < sizeSquare; col++) {
                            //if (oneChangeZeros[row][col]== 1) {
                            //addressAccountedFor[address ^ (1 << row)] |= 1;
                            //addressAccountedFor[address ^ (1 << col)] = 1;
                            //}
                            //addressAccountedFor[address ^ (1 << row)] = 1 | addressAccountedFor[address ^ (1 << row)];
                            //addressAccountedFor[address ^ (1 << col)] = 1 | addressAccountedFor[address ^ (1 << col)];
                            //if (both[row][col] == 1) {
                            //addressAccountedFor[address ^ (1 << row) ^ (1 << col)] |= 2;
                            //}
                            //addressAccountedFor[address ^ (1 << row)] = 1;
                            //addressAccountedFor[address ^ (1 << col)] = 1;
                        }
                    }
                    //addressAccountedFor[address] = 1;
                }
            }
            int[][][] cubicChanges = new int[16][16][16];
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    for (int zee = 0; zee < size; zee++) {
                        int changedAddress = address ^ (1 << row) ^ (1 << col) ^ (1 << zee);
                        if (truthTableTransform[changedAddress] == 0) {
                            cubicChanges[row][col][zee] = 1;
                        }
                    }
                }
            }
            for (int function = 0; function < cubicLogic.length; function++) {
                if (Arrays.deepEquals(cubicChanges, cubicLogic[function])) {
                    workingECA[function]++;
                    for (int row = 0; row < size; row++) {
                        for (int col = 0; col < size; col++) {
                            for (int zee = 0; zee < size; zee++) {
                                int changedAddress = address ^ (1 << row) ^ (1 << col) ^ (1 << zee);
                                //addressAccountedFor[changedAddress] |= 4;
                            }
                        }
                    }
                }
            }
//            for (int f = 0; f < hadamardishFunctions.length; f++) {
//                if (Arrays.deepEquals(hadamardishFunctions[f], oneChangeZeros)) {
//                    addressAccountedFor[address] = 1;
//                    workingHadamardishFunctions[f] = 1;
//                    for (int row = 0; row < sizeSquare; row++) {
//                        for (int col = 0; col < sizeSquare; col++) {
//                            if (oneChangeZeros[row][col] == 1) addressAccountedFor[address ^ (1 << row)] = 1;
//                            if (oneChangeZeros[row][col] == 1) addressAccountedFor[address ^ (1 << col)] = 1;
//                        }
//                    }
//                }
//            }
//            for (int f = 0; f < addSubMult.length; f++) {
//                if (Arrays.deepEquals(addSubMult[f], oneChangeZeros)) {
//                    addressAccountedFor[address] = 1;
//                    //workingHadamardishFunctions[f] = 1;
//                    for (int row = 0; row < sizeSquare; row++) {
//                        for (int col = 0; col < sizeSquare; col++) {
//                            if (oneChangeZeros[row][col] == 1) addressAccountedFor[address ^ (1 << row)] = 1;
//                            if (oneChangeZeros[row][col] == 1) addressAccountedFor[address ^ (1 << col)] = 1;
//                        }
//                    }
//                }
//            }
//            for (int f = 0; f < hRows.length; f++) {
//                if (Arrays.deepEquals(hRows[f], oneChangeZeros)) {
//                    addressAccountedFor[address] = 1;
//                    //workingHadamardishFunctions[f] = 1;
//                    for (int row = 0; row < sizeSquare; row++) {
//                        for (int col = 0; col < sizeSquare; col++) {
//                            if (oneChangeZeros[row][col] == 1) addressAccountedFor[address ^ (1 << row)] = 1;
//                            if (oneChangeZeros[row][col] == 1) addressAccountedFor[address ^ (1 << col)] = 1;
//                        }
//                    }
//                }
//            }
        }
        //CustomArray.plusArrayDisplay(oneChangeZeros, true, true, "one change zeros relative truth table");
        System.out.println(Arrays.toString(workingLogicFunctions));
        //System.out.println(Arrays.toString(workingHadamardishFunctions));
        System.out.println("done");
        int unaccounted = 0;
        for (int address = 0; address < 65536; address++) {
            if (addressAccountedFor[address] == 0) {
                unaccounted++;
            }
        }
        System.out.println("unaccounted: " + unaccounted);
        int[][] zeros = new int[256][256];
        for (int address = 0; address < 65536; address++) {
            if (addressAccountedFor[address] == 0) {
                //zeros[address / 256][address % 256] = 1;
            }
            if (addressAccountedFor[address] < 0) {
                //zeros[address / 256][address % 256] = 2;
            }
            zeros[address / 256][address % 256] = addressAccountedFor[address];
        }
        int[][] transposeCheck = new int[256][256];
        int[] distro = new int[16];
        for (int row = 0; row < 256; row++) {
            for (int col = 0; col < 256; col++) {
                distro[zeros[row][col]]++;
                if (zeros[row][col] == zeros[col][row]) {
                    transposeCheck[row][col] = 0;
                } else {
                    transposeCheck[row][col] = 1;
                }
                //zeros[row][col] ^= zeros[col][row];
            }
        }
        //zeros = PermutationsFactoradic.grayify(zeros);
        System.out.println("distro: " + Arrays.toString(distro));
        CustomArray.plusArrayDisplay(zeros, true, true, "zeros");
        System.out.println("________________________\n\n\n\n\n\n\n");
        int[][] result = hadamard.matrixMultiply(zeros, zeros);
        //result = hadamard.matrixMultiply(zeros,result);
        //result = hadamard.matrixMultiply(zeros,result);
        //result = hadamard.matrixMultiply(zeros,result);
        //result = hadamard.matrixMultiply(zeros,result);
        CustomArray.plusArrayDisplay(result, true, false, "zeros * zeros = result");
        for (int function = 0; function < logicFunctions.length; function++) {

            //System.out.println("n: " + function + " gate " + (function / 4) + " place " + ((function / 4) % 4) + " posNeg " + ((function/(16*4)) % 2) + " " + (workingLogicFunctions[function]));
            int gate = function / 4;
            int place = function % 4;
            if (workingLogicFunctions[function] != 0) {
                System.out.println("gate: " + gate + " place: " + place);
            }

        }
        for (int function = 0; function < 0; function++) {

            //System.out.println("n: " + function + " gate " + (function / 4) + " place " + ((function / 4) % 4) + " posNeg " + ((function/(16*4)) % 2) + " " + (workingLogicFunctions[function]));
            int gate = function / 4/16/16;
            int place = (function/16/16) % 4;
            if (workingLogicFunctions[function] != 0) {
                System.out.println("gate: " + gate + " place: " + place);
            }

        }

        System.out.println("workingECA " + Arrays.toString(workingECA));
        System.out.println("numDifferentOneChangeZeros: " + numDiffOneChangeZeros);
        System.out.println("numDifferentBoth: " + numDiffBoth);
    }


    /**
     * @param in
     * @return
     */
    int[][] shiftedTileAddresses(int[][] in) {
        int[][] out = new int[in.length][in[0].length];
        for (int row = 0; row < in.length; row++) {
            for (int col = 0; col < in[row].length; col++) {
                int tot = 0;
                for (int r = 0; r < in[row].length; r++) {
                    for (int c = 0; c < in[row].length; c++) {
                        tot += (1 << (in.length * r + c)) * in[(row + r) % in.length][(col + c) % in.length];
                    }
                }
                out[row][col] = tot;
            }
        }
        return out;
    }

    /**
     * @param size
     */
    public void generateAbsolutelyEverything(int size, boolean rowError) {
        int[][] out = new int[size][size];

        hash.initWolframs();
        //singles = new int[size * size];
        //shiftedSingles = new int[size * size];
        truthTableTransform = new int[65536];
        Arrays.fill(truthTableTransform, -1);
        int[][] tile = new int[size][size];
        out = generateFunctionTile(size);
        int[][] referenceNegativeOne = new int[size][size];
        for (int row = 0; row < size; row++) {
            Arrays.fill(referenceNegativeOne[row], -1);
        }
        for (int address = 0; address < 65536; address++) {
            if (address % 256 == 0) System.out.println("address: " + address);
            int[][] referenceWrapped = shiftedTileAddresses(hash.hashRows.generateAddressTile(address, size));
            boolean isDoneAlready = true;
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < 4; col++) {
                    tile[row][col] = ((address >> (size * row + col)) % 2);
                    if (truthTableTransform[referenceWrapped[row][col]] == -1) isDoneAlready = false;
                }
            }
            if (isDoneAlready) {
                //continue;
            }
            int[][][] preHash = new int[16][size][size];
            for (int posNeg = 0; posNeg < 2; posNeg++) {
                for (int t = 0; t < 8; t++) {
                    //preHash[t] = hash.initializeDepthZero(tile, hash.unpackedList[t])[1];
                    //preHash[8 + t] = hash.initializeDepthMax(tile, hash.unpackedList[t])[1];
                }
            }
            int[][][] hashed = new int[16][size][size];
            for (int posNeg = 0; posNeg < 2; posNeg++) {
                for (int t = 0; t < 8; t++) {
                    hashed[t] = hash.hashArray(preHash[t], hash.rowList[t], 1,true,rowError)[1];
                    hashed[8 + t] = hash.hashArray(preHash[8 + t], hash.rowList[t], 1,false,rowError)[1];
                }
            }
            //int[][][] cinverted = hash.reconstructDepthD(chashed, 1);
            hash.invert(hashed, 1,true);
            int[][][] inverted = hash.outResult;
            int[][] finalized = hash.invert(inverted, 1, rowError);
            //int[][] cfinalized = hash.hashInverseDepth0(cinverted, 1, 0);
            int[][] shifted = new int[size][size];
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    shifted[row][col] = finalized[(row + size / 2) % size][(col + size / 2) % size];
                }
            }
            int[][] wrapped = shiftedTileAddresses(finalized);
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    truthTableTransform[referenceWrapped[row][col]] = wrapped[row][col];
                    int shiftrow = (row + size / 2) % size;
                    int shiftcol = (col + size / 2) % size;
                    //shiftedSingles[change] += (1 << (size * shiftrow + shiftcol)) * shifted[row][col];
                }
            }
        }
        Random rand = new Random();
        for (int sample = 0; sample < 100; sample++) {
            int a = rand.nextInt(0, 65536);
            System.out.println(a + " " + truthTableTransform[a]);
        }
    }

    public void generateSingles(int size, boolean rowError) {
        int[][] out = new int[size][size];

        //hash.initWolframs();
        singles = new int[size * size];
        shiftedSingles = new int[size * size];
        Hadamard hadamard = new Hadamard();
        int[][] h = hadamard.nonReducedHadamard(size);
        int[][] changed = new int[size][size];
        int[][] tile;
        out = generateFunctionTile(size);
        for (int address = 0; address < 1; address++) {
            for (int change = 0; change < size * size; change++) {
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < 4; col++) {
                        changed[row][col] = out[row][col];
                    }
                }
                changed[change / size][change % size] ^= 1;
                int[][][] preHash = new int[16][size][size];
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        //preHash[t] = hash.initializeDepthZero(changed, hash.unpackedList[t])[1];
                        //preHash[8 + t] = hash.initializeDepthMax(changed, hash.unpackedList[t])[1];
                        preHash[t] = new int[1][1];
                        preHash[t] = new int[1][1];
                    }
                }
                int[][][] hashed = new int[16][size][size];
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        hashed[t] = hash.hashArray(preHash[t], hash.bothLists[rowError ? 0 : 1][t], 1,true,rowError)[1];
                        hashed[8 + t] = hash.hashArray(preHash[8 + t], hash.bothLists[rowError ? 0 : 1][t], 1,false,rowError)[1];
                    }
                }
                //int[][][] cinverted = hash.reconstructDepthD(chashed, 1);
                hash.invert(hashed, 1,true);
                int[][][] inverted = hash.outResult;
                int[][] finalized = hash.invert(inverted, 1, rowError);
                //int[][] cfinalized = hash.hashInverseDepth0(cinverted, 1, 0);
                int[][] shifted = new int[size][size];
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        shifted[row][col] = finalized[(row + size / 2) % size][(col + size / 2) % size];
                    }
                }
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        singles[change] += (1 << (size * row + col)) * finalized[row][col];
                        int shiftrow = (row + size / 2) % size;
                        int shiftcol = (col + size / 2) % size;
                        shiftedSingles[change] += (1 << (size * shiftrow + shiftcol)) * shifted[row][col];
                    }
                }
            }
        }
    }

    public void generateDoubles(int size, boolean rowError) {
        //hash.initWolframs();
        //checkChangesPerTransform(size);
        //int size = 4;

        doubles = new int[size * size][size * size];
        shiftedDoubles = new int[size * size][size * size];
        trackedZero = new int[2][size * size][size * size];
        for (int oneChange = 0; oneChange < size * size; oneChange++) {
            int[][] referenceTile = hash.hashRows.generateAddressTile(singles[oneChange], size);
            for (int nextChange = 0; nextChange < size * size; nextChange++) {
                int[][] tile = hash.hashRows.generateAddressTile(singles[oneChange], size);
                int cr = nextChange / size;
                int cc = nextChange % size;
                tile[nextChange / size][nextChange % size] ^= 1;
                int[][][] preHash = new int[16][size][size];
                //int[][][] cpre = new int[16][size][size];
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        //preHash[t] = hash.initializeDepthZero(tile, hash.unpackedList[t])[1];
                        //preHash[8 + t] = hash.initializeDepthMax(tile, hash.unpackedList[t])[1];
                        preHash = new int[1][1][1];
                        preHash = new int[1][1][1];
                        //cpre[t] = hash.initializeDepthZero(changed, hash.unpackedList[t])[1];
                        //cpre[8 + t] = hash.initializeDepthMax(changed, hash.unpackedList[t])[1];
                    }
                }
                int[][][] hashed = new int[16][size][size];
                //int[][][] chashed = new int[16][size][size];
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        hashed[t] = hash.hashArray(preHash[t], hash.bothLists[rowError ? 0 : 1][t], 1,true,rowError)[1];
                        hashed[8 + t] = hash.hashArray(preHash[8 + t], hash.bothLists[rowError ? 0 : 1][t], 1,false,rowError)[1];
                        //chashed[t] = hash.ecaTransform(cpre[t], hash.unpackedList[t], 1)[1];
                        //chashed[t + 8] = hash.ecaTransform(cpre[t + 8], hash.unpackedList[t], 1)[1];
                    }
                }
                //int[][][] cinverted = hash.reconstructDepthD(chashed, 1);
                hash.invert(hashed, 1,true);
                int[][][] inverted = hash.outResult;
                int[][] finalized = hash.invert(inverted, 1,rowError);
                //int[][] cfinalized = hash.hashInverseDepth0(cinverted, 1, 0);
                int quantityErrors = 0;
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        //finalized[row][col] ^= out[row][col];
                        if (finalized[row][col] == 0) {
                            trackedZero[0][cr][cc] = row;
                            trackedZero[1][cr][cc] = col;
                        }
                        //quantityErrors += finalized[(row) % size][col] ^ out[row][col];
                    }
                }
                //CustomArray.plusArrayDisplay(finalized, true, true, "finalized doubles");
                int[][] shifted = new int[size][size];
                //int[][] cshifted = new int[size][size];
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        shifted[row][col] = finalized[(row + oneChange + size / 2) % size][(col + nextChange + size / 2) % size];
                        //cshifted[row][col] = cfinalized[(row + a + size / 2) % size][(col + b + size / 2) % size];
                    }
                }
                //CustomArray.plusArrayDisplay(shifted, true, true, "shifted");
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        //changes[size * cr + cc][row][col] = shifted[row][col];
                        //shiftChanges[size * cr + cc][row][col] = cshifted[row][col];
                        //changes[size*cr+cc][row][col] = finalized[row][col];
                    }
                }
                //CustomArray.plusArrayDisplay(shiftChanges[size * cr + cc], true, true, "shiftedChanges");
                doubles[oneChange][nextChange] = hash.hashRows.addressTileToInteger(finalized);
                shiftedDoubles[oneChange][nextChange] = hash.hashRows.addressTileToInteger(shifted);
            }
        }
    }

    public void generateSinglesAndDoubles(int size, boolean rowError) {
        //hash.initWolframs();
        generateSingles(size, rowError);
        generateDoubles(size, rowError);
        int[][] none = generateFunctionTile(size);
        int[][][] all = new int[4][size][size];
        int[][][] everyCell = new int[size * size][size * size][4];
        shiftedEverything = new int[size * size][size * size][4];
        int[][][] allShifted = new int[4][size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
            }
        }
        for (int oneChange = 0; oneChange < size * size; oneChange++) {
            int[][] one = hash.hashRows.generateAddressTile(singles[oneChange], size);
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                }
            }
            for (int nextChange = 0; nextChange < size * size; nextChange++) {
                int[][] next = hash.hashRows.generateAddressTile(singles[nextChange], size);
                int[][] both = hash.hashRows.generateAddressTile(doubles[oneChange][nextChange], size);
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        int r = (row + oneChange + size / 2) % size;
                        int c = (col + nextChange + size / 2) % size;
                        all[0][row][col] = none[row][col];
                        all[1][row][col] = one[row][col];
                        all[2][row][col] = next[row][col];
                        all[3][row][col] = both[row][col];
                        allShifted[0][row][col] = none[r][c];
                        allShifted[1][row][col] = one[r][c];
                        allShifted[2][row][col] = next[r][c];
                        allShifted[3][row][col] = both[r][c];
                    }
                }
                for (int v = 0; v < 4; v++) {
                    everyCell[oneChange][nextChange][v] = hash.hashRows.addressTileToInteger(all[v]);
                    everything[oneChange][nextChange][v] = everyCell[oneChange][nextChange][v];
                    shiftedEverything[oneChange][nextChange][v] = hash.hashRows.addressTileToInteger(allShifted[v]);
                }
            }
        }
        int[][] powerLayer = new int[size * size][size * size];
        int[][] modSixteenLayer = new int[size * size][size * size];
        for (int power = 0; power < 32; power++) {
            for (int row = 0; row < size * size; row++) {
                for (int col = 0; col < size * size; col++) {
                    powerLayer[row][col] = (everything[row][col][3] >> power) % 2;
                }
            }
            CustomArray.plusArrayDisplay(powerLayer, false, false, "powerLayer " + power);
        }
        for (int row = 0; row < size * size; row++) {
            for (int col = 0; col < size * size; col++) {
                modSixteenLayer[row][col] = everything[row][col][3];
            }
        }
        CustomArray.plusArrayDisplay(modSixteenLayer, false, false, "modSixteenlayer");
        CustomArray.plusArrayDisplay(everyCell[3], false, false, "powerLayer");
    }
}
