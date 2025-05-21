package TrimmedCode;

import AlgorithmCode.Hadamard;
import AlgorithmCode.HashTransform;
import CustomLibrary.CustomArray;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class TrimMay {
    /**
     * The first part of this function generates the addition tables of adding two hash tiles together,
     * Showing that adding tiles does indeed result in a non-reduced Hadamard matrix. After that
     * is some experimentation ???
     */
    public void testGateTwo(int gate, int[][] tupleDistro, int[][] h) {
        //hashTransform.initWolframs();
        int[][][][] additionTables = new int[2][8][16][16];
        Random rand = new Random();
        //generate addition table for every (a,b) for every minMax codeword 8-tuple
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                gloop:
                for (int g = 0; g < 16; g++) {
                    //System.out.println("posNeg: " + posNeg + " t: " + t + " g: " + g);
                    boolean gateWorks = true;
                    for (int a = 0; a < 16; a++) {
                        for (int b = 0; b < 16; b++) {
                            //generate the neighborhoods of a and b
                            int[][] aa = hash.generateCodewordTile(a, hashTransform.unpackedList[t]);
                            int[][] bb = hash.generateCodewordTile(b, hashTransform.unpackedList[t]);
                            int[][] cc = new int[4][4];
                            //add the neighborhoods together pair-wise
                            for (int row = 0; row < 4; row++) {
                                for (int col = 0; col < 4; col++) {
                                    cc[row][col] = (gate >> (aa[row][col] + 2 * bb[row][col])) % 2;
                                }
                            }
                            //find the codeword of the sum of the neighborhoods
                            int[][] ccc = hash.findMinimizingCodeword(hashTransform.unpackedList[t], cc);
                            int result = hash.lastMinCodeword;
                            if (posNeg == 1) result = hash.lastMaxCodeword;
                            int next = 0;
                            for (int power = 0; power < 4; power++) {
                                int ab = ((a >> power) % 2) + 2 * ((b >> power) % 2);
                                next += (1 << power) * ((g >> ab) % 2);
                            }
                            if (next != result) {
                                gateWorks = false;
                                continue gloop;
                            }
                        }
                    }
                    if (gateWorks) {
                        logicTransform[gate][8 * posNeg + t] = g;
                    }
                }
            }
        }
        CustomArray.plusArrayDisplay(logicTransform, false, false, "logicTransform");
//
//        int[][][] attemptedLogicTransform = new int[16][16][4];
//        for (int g = 0; g < 16; g++) {
//            for (int gg = 0; gg < 16; gg++) {
//                Arrays.fill(attemptedLogicTransform[g][gg], -1);
//            }
//        }
//        int[][] distro = new int[16][16];
//        for (int posNeg = 0; posNeg < 2; posNeg++) {
//            for (int t = 0; t < 8; t++) {
//                testGateLoop:
//                for (int g = 0; g < 16; g++) {
//                    for (int a = 0; a < 16; a++) {
//                        for (int b = 0; b < 16; b++) {
//                            for (int power = 0; power < 4; power++) {
//                                int ab = (a >> power) % 2 + 2 * ((b >> power) % 2);
//                                int c = (additionTables[posNeg][t][a][b] >> power) % 2;
//                                if ((g >> ab) % 2 != c) {
//                                    continue testGateLoop;
//                                }
//                            }
//                        }
//                    }
//                    distro[8 * posNeg + t][g]++;
//                }
//            }
//        }
//        for (int posNeg = 0; posNeg < 2; posNeg++) {
//            for (int t = 0; t < 8; t++) {
//                testGateLoop:
//                for (int g = 0; g < 16; g++) {
//                    if (distro[8 * posNeg + t][g] == 1) {
//                        tupleDistro[gate][8 * posNeg + t] = g;
//                    }
//                }
//            }
//        }
//        CustomArray.plusArrayDisplay(distro, false, false, "distro");
//        //Display
//        for (int posNeg = 0; posNeg < 2; posNeg++) {
//            for (int t = 0; t < 8; t++) {
//                if (Arrays.deepEquals(additionTables[posNeg][t], h)) {
//                    System.out.println("posNeg: " + posNeg + " t: " + t);
//                }
//                for (int power = 0; power < 4; power++) {
//                    int[][] display = new int[16][16];
//                    for (int row = 0; row < 16; row++) {
//                        for (int col = 0; col < 16; col++) {
//                            display[row][col] = (additionTables[posNeg][t][row][col] >> power) % 2;
//                        }
//                    }
//                    //CustomArray.plusArrayDisplay(display, false, false, "posNeg: " + posNeg + " t: " + t + " " + power);
//                    //CustomArray.plusArrayDisplay(additionTables[posNeg][t], true, false, "posNeg: " + posNeg + " t: " + t + " " + hashTransform.unpackedList[t]);
//                    //CustomArray.intoBinary(additionTables[posNeg][t], 4, 2, 2, true,false);
//                }
//            }
//        }
//        for (int posNeg = 0; posNeg < 2; posNeg++) {
//            for (int t = 0; t < 8; t++) {
//                for (int power = 0; power < 4; power++) {
//                    int[][] display = new int[2][2];
//                    for (int row = 0; row < 2; row++) {
//                        for (int col = 0; col < 2; col++) {
//                            display[row][col] = (additionTables[posNeg][t][row][col] >> power) % 2;
//                            //   tupleDistro[gate][8 * posNeg + t] += (display[row][col] << (2 * row + col));
//                        }
//                    }
//                    //CustomArray.plusArrayDisplay(display, false, false, "posNeg: " + posNeg + " t: " + t + " " + power);
//                    //CustomArray.plusArrayDisplay(additionTables[posNeg][t], true, false, "posNeg: " + posNeg + " t: " + t + " " + hashTransform.unpackedList[t]);
//                    //CustomArray.intoBinary(additionTables[posNeg][t], 4, 2, 2, true,false);
//                }
//            }
//        }
//        for (int posNeg = 0; posNeg < 2; posNeg++) {
//            for (int t = 0; t < 8; t++) {
//                //CustomArray.plusArrayDisplay(display, false, false, "posNeg: " + posNeg + " t: " + t + " " + power);
//                //CustomArray.plusArrayDisplay(additionTables[posNeg][t], false, false, "posNeg: " + posNeg + " t: " + t + " " + hashTransform.unpackedList[t]);
//                //CustomArray.intoBinary(additionTables[posNeg][t], 4, 2, 2, true,false);
//            }
//        }
    }
    /**
     * Does the legwork of reconstituting input from sets of codewords
     * The commented out code that includes a and generatedGuess() is the original voting mechanism
     * where a codeword generates a square neighborhood to decompress the data back to original
     * What's here at the moment is the Hadamard parity, which results in the same thing with an
     * almost identical error rate. The Hadamard parity is count the number of 1s in the bits of
     * the binary codeword and take that mod 2. For some reason the Hadamard parity can be substituted
     * for the codeword's generate neighborhood. This was found by experimentation after it was discovered
     * that codeword addition results in the non-reduced ROW AND COLUMN matrix that produces the boolean
     * Hadamard matrix.
     *
     * @param in 2D codeword input array
     */
    public int[][] hashInverseDepth0(int[][][] in, int depth, int rule) {
        int neighborDistance = 1 << (depth - 1);
        //load the minMax 8 tuple subset Wolfram codes
        //initWolframs();
        int[][][][] depthChart = new int[2][8][in.length][in[0].length];
        //puts the input data as layer 0 of the output data
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                //System.out.println("posNeg: " + posNeg + " t: " + t);
                //depthChart[posNeg][t] = initializeDepthZero(in, unpackedList[t])[1];
                //depthChart[posNeg][t] =
            }
        }
        //this array is the vote tally, location is influenced by 16 neighborhoods within a distance of 4
        //each of these neighborhoods has 16 terms in the min max codeword set of the 8 tuple
        //every term of every vote is weighted by 2^RelativeRow
        int[][] outVotes = new int[in[0].length][in[0][0].length];
        int r;
        int c;
        int t;
        int posNeg;
        int hadamardValue;
        int power;
        int row;
        int column;
        //for every location in the transformed bitmap data
        for (row = 0; row < in[0].length; row++) {
            //System.out.println("row: " + row + " out of " + in.length);
            for (column = 0; column < in[0][0].length; column++) {
                //for every term in its min max codeword set
                for (posNeg = 0; posNeg < 2; posNeg++) {
                    for (t = 0; t < 8; t++) {
                        //apply its vote to every location that it influences
                        //including itself
                        int[][] generatedGuess = m.generateCodewordTile(in[8 * posNeg + t][row][column], unpackedList[t]);
                        //CustomArray.plusArrayDisplay(generatedGuess, false, true, "generatedGuess");
                        for (r = 0; r < 4; r++) {
                            for (c = 0; c < 4; c++) {
                                //int a = (generatedGuess[r][c] );
                                if (generatedGuess[r][c] == posNeg) {
                                    //if (hadamardValue == posNeg) {
                                    outVotes[(row + (r)) % in[0].length][(column + (c)) % in[0][0].length] += (1 << r);
                                } else {
                                    outVotes[(row + (r)) % in[0].length][(column + (c)) % in[0][0].length] -= (1 << r);
                                }
                            }
                        }
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        int[][] outResult = new int[in[0].length][in[0][0].length];
        int[][] outCompare = new int[in[0].length][in[0][0].length];
        int totDifferent = 0;
        for (row = 0; row < in[0].length; row++) {
            for (column = 0; column < in[0][0].length; column++) {
                if (outVotes[row][column] >= 0) {
                    outResult[row][column] = 0;
                } else {
                    outResult[row][column] = 1;
                }
                //outCompare[row][column] = outResult[row][column] ^ in[row][column];
                totDifferent += outCompare[row][column];
            }
        }
        //System.out.println("totDifferent: " + totDifferent);
        //System.out.println("totArea: " + (in.length * in[0].length));
        //System.out.println("different/Area=errors/bit= " + ((double) totDifferent / (double) (in.length * in[0].length)));
        //CustomArray.plusArrayDisplay(outVotes, true, false, "outVotes");
        //System.out.println("outResult.getHeight: " + " " + outResult.length + outResult[0].length);
        return outResult;
    }

    public int[] oneDtransformMin(int[] in, int rule, int phasePower, boolean reduce, boolean max) {
        int spotSkip = 1;
        if (reduce) spotSkip = 4;
        int[] out = new int[in.length];
        for (int spot = 0; spot < in.length; spot += spotSkip) {
            for (int index = 0; index < 4; index++) {
                out[spot / spotSkip] += (1 << (4 * index)) * (in[(spot + index * (1 << phasePower)) % in.length]);
            }
            out[spot / spotSkip] = m.minSolutionsAsWolfram[rule][out[spot / spotSkip]];
            if (max) out[spot / spotSkip] = m.maxSolutionsAsWolfram[rule][out[spot / spotSkip]];
        }
        return out;
    }

    public int[] oneD(int[] in, int rule, int length, boolean reduce, boolean max) {
        int log4 = (int) Math.ceil(Math.log(in.length) / Math.log(4));
        int[] out = in;
        for (int iter = 0; iter < log4; iter++) {
            out = oneDtransformMin(out, rule, iter, false, max);
        }
        if (reduce) {
            log4 -= (int) Math.ceil(Math.log(length) / Math.log(4));
        }
        for (int iter = 0; iter < log4; iter++) {
            out = oneDtransformMin(out, rule, 1, true, max);
        }
        return out;
    }
    /**
     * Takes raw binary data and does the initial conversion to one codeword per point covering its
     * area of influence, before comparing them with neighbors in ecaMinMaxTransform()
     *
     * @param input a 2D binary array
     * @param rule  an ECA rule
     * @return a set of 2D arrays with input in layer 0, and layer 1 is the codeword-ified input,
     * the rest is empty
     */
    public int[][][] initializeDepthZero(int[][] input, int rule) {
        int rows = input.length;
        int cols = input[0].length;
        int[][][] deepInput = new int[4][rows][cols];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        //for every location in the bitmap
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                //gets its neighborhood
                int cell = 0;
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        cell += (int) Math.pow(2, 4 * r + c) * deepInput[0][(row + r) % rows][(col + c) % cols];
                    }
                }
                //finds the neighborhood's codeword
                deepInput[1][row][col] = m.minSolutionsAsWolfram[rule][cell];
            }
        }
        return deepInput;
    }

    /**
     * Takes raw binary data and does the initial conversion to one codeword per point covering its
     * area of influence, before comparing them with neighbors in ecaMinMaxTransform()
     *
     * @param input a 2D binary array
     * @param rule  an ECA rule
     * @return a set of 2D arrays with input in layer 0, and layer 1 is the codeword-ified input,
     * the rest is empty
     */
    public int[][][] initializeDepthZeroMax(int[][] input, int rule) {
        int rows = input.length;
        int cols = input[0].length;
        int[][][] deepInput = new int[4][rows][cols];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        //for every location in the bitmap
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                //gets its neighborhood
                int cell = 0;
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        cell += (int) Math.pow(2, 4 * r + c) * deepInput[0][(row + r) % rows][(col + c) % cols];
                    }
                }
                //finds the neighborhood's codeword
                deepInput[1][row][col] = m.maxSolutionsAsWolfram[rule][cell];
            }
        }
        return deepInput;
    }

    /**
     * Takes raw binary data and does the initial conversion to one codeword per point covering its
     * area of influence, before comparing them with neighbors in ecaMinMaxTransform()
     *
     * @param input a 2D binary array
     * @param rule  an ECA rule
     * @return a set of 2D arrays with input in layer 0, and layer 1 is the codeword-ified input,
     * the rest is empty
     */
    public int[][][] initializeDepthMax(int[][] input, int rule) {
        int rows = input.length;
        int cols = input[0].length;
        int[][][] deepInput = new int[4][rows][cols];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        //for every location in the bitmap
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                //gets its neighborhood
                int cell = 0;
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        cell += (int) Math.pow(2, 4 * r + c) * deepInput[0][(row + r) % rows][(col + c) % cols];
                    }
                }
                //finds the neighborhood's codeword
                deepInput[1][row][col] = m.maxSolutionsAsWolfram[rule][cell];
            }
        }
        return deepInput;
    }
    /**
     * Takes in a 2D array of hashed data in codeword form, then rehashes sets of codewords increasingly far apart in steps of powers of 2, 1 apart 2 apart 4 apart ... 2^n apart
     *
     * @param input a 2D array of hashed data
     * @param rule  one of {0,15,51,85,170,204,240,255}
     * @param depth iterative depth, also the power of how far away its neighbors are
     * @return the input data, rehashed with neighbors 2^depth apart
     */
    public int[][][] ecaMaxTransform(int[][] input, int rule, int depth) {
        //initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        int[][][] deepInput = new int[depth + 1][rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //for every row, column location in the image
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    //gets the location's neighborhood
                    int cell = 0;
                    int phasePower = (int) Math.pow(2, d - 1);
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            cell += (int) Math.pow(16, 2 * r + c) * deepInput[d - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                        }
                    }
                    //stores the neighborhood's codeword
                    deepInput[d][row][col] = (m.maxSolutionsAsWolfram[rule][cell]);
                }
            }
        }
        return deepInput;
    }
    /**
     * Takes in a 2D array of hashed data in codeword form, then rehashes sets of codewords increasingly far apart in steps of powers of 2, 1 apart 2 apart 4 apart ... 2^n apart
     *
     * @param input a 2D array of hashed data
     * @param rule  one of {0,15,51,85,170,204,240,255}
     * @param depth iterative depth, also the power of how far away its neighbors are
     * @return the input data, rehashed with neighbors 2^depth apart
     */
    public int[][][] ecaMinTransform(int[][] input, int rule, int depth) {
        //initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        int[][][] output = new int[depth + 2][rows][cols];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                output[0][row][col] = input[row][col];
            }
        }
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //This is to skip the negative flip on the integer
            //it would not be necessary with unsigned integers
            //if (d%32 == 31) d++;
            //for every (row,column) location in the image
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    //gets its neighborhood
                    int cell = 0;
                    int phasePower = (1 << ((d - 1) % 16));
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            cell += (int) Math.pow(16, 2 * r + c) * output[d - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                        }
                    }
                    //stores the neighborhood's codeword
                    output[d][row][col] = (m.minSolutionsAsWolfram[rule][cell]);
                }
            }
        }
        return output;
    }
    /**
     * Takes in a 2D array of hashed data in codeword form, then rehashes sets of codewords increasingly far apart in steps of powers of 2, 1 apart 2 apart 4 apart ... 2^n apart
     *
     * @param input a 2D array of hashed data
     * @param rule  one of {0,15,51,85,170,204,240,255}
     * @param depth iterative depth, also the power of how far away its neighbors are
     * @return the input data, rehashed with neighbors 2^depth apart
     */
    public int[][][] ecaMinTransformRGBrowNorm(int[][] input, int rule, int depth) {
        //initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        int[][][] output = new int[depth + 2][rows][cols];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                output[0][row][col] = input[row][col];
            }
        }
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //This is to skip the negative flip on the integer
            //it would not be necessary with unsigned integers
            //if (d%32 == 31) d++;
            //for every (row,column) location in the image
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    //gets its neighborhood
                    int cell = 0;
                    int phasePower = (1 << ((d - 1) % 16));
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            cell += (int) Math.pow(16, 2 * r + c) * output[d - 1][(row) % rows][(col + (2 * r + c)) % cols];
                        }
                    }
                    //stores the neighborhood's codeword
                    output[d][row][col] = (m.minSolutionsAsWolfram[rule][cell]);
                }
            }
        }
        return output;
    }
    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void bitmapTransform(String filepath) throws IOException {
        //String filepath = "frontYard.bmp";
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        int[] inRaster = ((DataBufferInt) inImage.getRaster().getDataBuffer()).getData();
        int size = inImage.getWidth();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 15;
        int[][][] framesOfHashing = new int[depth][inImage.getHeight()][inImage.getWidth() * 8];
        int[][] field = new int[inImage.getHeight()][inImage.getWidth() * 8];
        int[][] bfield = new int[inImage.getHeight()][inImage.getWidth() * 32];
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight(): " + inRaster.length / inImage.getHeight());
        System.out.println("inRaster.length/inImage.getWidth(): " + inRaster.length / inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight()/inImage.getWidth(): " + inRaster.length / inImage.getHeight() / inImage.getWidth());
        //Transforms the image into its appropriate local algorithm format
//        for (int row = 0; row < inImage.getHeight(); row++) {
//            for (int column = 0; column < inImage.getWidth(); column++) {
//                for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
//                    for (int lr = 0; lr < 2; lr++) {
//                        int rasterCoordX = row * inImage.getWidth() + column;
//                        field[row][8 * column + 2 * rgbbyte + lr] = (int) Math.abs((inRaster[rasterCoordX] >> (4 * rgbbyte + 2 * lr)) % 16);
//                        for (int power = 0; power < 4; power++) {
//                            bfield[row][32 * column + 8 * rgbbyte + 4 * lr + power] = (field[row][8 * column + 2 * rgbbyte + lr] >> power) % 2;
//                        }
//                    }
//                }
//            }
//        }
        int[][][] bFieldSet = new int[16][bfield.length][bfield[0].length];
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
                    for (int power = 0; power < 8; power++) {
                        bfield[row][32 * column + 8 * rgbbyte + power] = (int) Math.abs((inRaster[row * inImage.getWidth() + column] >> (8 * rgbbyte + power)) % 2);
                        for (int posNegt = 0; posNegt < 16; posNegt++) {
                            bFieldSet[posNegt][row][32 * column + 8 * rgbbyte + power] = bfield[row][32 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        //Initialize the minMax codeword truth table set
        initWolframs();
        hashUtilities.readFromFile();
        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        bfield = initializeDepthZero(bfield, unpackedList[3])[1];
        //Do the transform
        framesOfHashing = ecaMinTransform(bfield, unpackedList[3], depth);
        //Convert the transform back into appropriate bitmap RGB format
        int[][][] rasterized = new int[depth + 1][inImage.getHeight()][inImage.getWidth()];
        for (int d = 0; d <= depth; d++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            rasterized[d][row][column] += (1 << (8 * rgbbyte + power)) * framesOfHashing[d][row][32 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //This does the GIF file
        BufferedImage[] images = new BufferedImage[rasterized.length];
        int[][] imagesRasters = new int[depth + 1][inImage.getHeight() * inImage.getWidth()];
        ImageWriter gifWriter = ImageIO.getImageWritersByFormatName("gif").next();
        ImageOutputStream outputStream = ImageIO.createImageOutputStream(new File("src/ImagesProcessed/" + filepath + "gif.gif"));
        gifWriter.setOutput(outputStream);
        int[] outRaster = new int[inImage.getHeight() * inImage.getWidth()];
        gifWriter.prepareWriteSequence(null);
        BufferedImage outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int repeat = 0; repeat < 1; repeat++) {
            for (int d = 0; d <= depth; d++) {
                File outFile = new File("src/ImagesProcessed/GifOutput/" + filepath + "iteration" + d + ".bmp");
                outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                outRaster = ((DataBufferInt) outImage.getRaster().getDataBuffer()).getData();
                for (int index = 0; index < outRaster.length; index++) {
                    outRaster[index] = rasterized[d][index / inImage.getWidth()][index % inImage.getWidth()];
                }
                ImageIO.write(outImage, "bmp", outFile);
                IIOImage image = new IIOImage(outImage, null, null);
                gifWriter.writeToSequence(image, null);
            }
        }
        gifWriter.endWriteSequence();
        System.out.println("depth: " + depth);
        System.out.println("done with gif");
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        BufferedImage inverse = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[][][] undoInput = new int[16][inImage.getHeight()][inImage.getWidth()];
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                undoInput[3][row][column] = bfield[row][column];
            }
        }
        System.out.println("undoInput[3].length: " + undoInput[0].length + " " + undoInput[1][0].length);
        for (int posNegt = 0; posNegt < 8; posNegt++) {
            bFieldSet[posNegt] = initializeDepthZero(bFieldSet[posNegt], unpackedList[posNegt])[1];
            bFieldSet[posNegt + 8] = initializeDepthMax(bFieldSet[posNegt + 8], unpackedList[posNegt])[1];
        }
        int[][] undo = hashInverseDepth0(bFieldSet, 1, 3);
        int[][] undoRasterized = new int[inverse.getHeight()][inverse.getWidth()];
        System.out.println("inverse.getHeight(): " + inverse.getHeight() + " inverse.getWidth(): " + inverse.getWidth());
        System.out.println(undo.length + " " + undo[0].length);
        System.out.println(undoRasterized.length + " " + undoRasterized[0].length);
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inverse.getHeight(); row++) {
                for (int column = 0; column < inverse.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 3; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            undoRasterized[row][column] += undo[row][column * 32 + 8 * rgbbyte + power] << (8 * rgbbyte + power - ((8 * rgbbyte + power) / 3));
                        }
                    }
                }
            }
        }
        int[] inverseImageRaster = ((DataBufferInt) inverse.getRaster().getDataBuffer()).getData();
        for (int row = 0; row < inverse.getHeight(); row++) {
            for (int column = 0; column < inverse.getWidth(); column++) {
                //if (row == 655 || column == 655) { System.out.println("row: " + row + ", column: " + column); }
                inverseImageRaster[row * inImage.getWidth() + column] = undoRasterized[row][column];
            }
        }
        File inverseFile = new File("src/ImagesProcessed/" + filepath + "inverse.bmp");
        ImageIO.write(inverse, "bmp", inverseFile);
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        inverse = new BufferedImage(inverse.getWidth(), inverse.getHeight(), BufferedImage.TYPE_INT_RGB);
        inverseImageRaster = ((DataBufferInt) inverse.getRaster().getDataBuffer()).getData();
        undo = reconstructDepthD(framesOfHashing[1], 1, 3,true);
        undoRasterized = new int[inverse.getHeight()][inverse.getWidth()];
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inverse.getHeight(); row++) {
                for (int column = 0; column < inverse.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            undoRasterized[row][column] += undo[row][column * 32 + 8 * rgbbyte + power] << (8 * rgbbyte + power - ((8 * rgbbyte + power) / 3));
                        }
                    }
                }
            }
        }
        for (int row = 0; row < inverse.getHeight(); row++) {
            for (int column = 0; column < inverse.getWidth(); column++) {
                //if (row == 655 || column == 655) { System.out.println("row: " + row + ", column: " + column); }
                inverseImageRaster[row * inImage.getWidth() + column] = undoRasterized[row][column];
            }
        }
        File inverseDepth1 = new File("src/ImagesProcessed/" + filepath + "inverseDepth1.bmp");
        ImageIO.write(inverse, "bmp", inverseDepth1);
    }

    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void bitmapTransform(String filepath, int dummy) throws IOException {
        //String filepath = "kitchenAlteredRGB.bmp";
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int size = inImage.getWidth();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 24;
        int[][][] framesOfHashing = new int[depth][inImage.getHeight()][inImage.getWidth() * 8];
        int[][] field = new int[inImage.getHeight()][inImage.getWidth() * 8];
        int[][] bfield = new int[inImage.getHeight()][inImage.getWidth() * 16];
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight(): " + inRaster.length / inImage.getHeight());
        System.out.println("inRaster.length/inImage.getWidth(): " + inRaster.length / inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight()/inImage.getWidth(): " + inRaster.length / inImage.getHeight() / inImage.getWidth());
        //Transforms the image into its appropriate local algorithm format
//        for (int row = 0; row < inImage.getHeight(); row++) {
//            for (int column = 0; column < inImage.getWidth(); column++) {
//                for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
//                    for (int lr = 0; lr < 2; lr++) {
//                        int rasterCoordX = row * inImage.getWidth() + column;
//                        field[row][8 * column + 2 * rgbbyte + lr] = (int) Math.abs((inRaster[rasterCoordX] >> (4 * rgbbyte + 2 * lr)) % 16);
//                        for (int power = 0; power < 4; power++) {
//                            bfield[row][32 * column + 8 * rgbbyte + 4 * lr + power] = (field[row][8 * column + 2 * rgbbyte + lr] >> power) % 2;
//                        }
//                    }
//                }
//            }
//        }
        int[][][] bFieldSet = new int[16][bfield.length][bfield[0].length];
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                    for (int power = 0; power < 8; power++) {
                        bfield[row][16 * column + 8 * rgbbyte + power] = (int) Math.abs((inRaster[row * inImage.getWidth() + column] >> (8 * rgbbyte + power)) % 2);
                        for (int posNegt = 0; posNegt < 16; posNegt++) {
                            bFieldSet[posNegt][row][16 * column + 8 * rgbbyte + power] = bfield[row][16 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        //Initialize the minMax codeword truth table set
        initWolframs();
        //hashUtilities.readFromFile();
        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        bfield = initializeDepthZero(bfield, unpackedList[3])[1];
        //Do the transform
        framesOfHashing = ecaMinTransform(bfield, unpackedList[3], depth);
        //Convert the transform back into appropriate bitmap RGB format
        short[][][] rasterized = new short[depth + 1][inImage.getHeight()][inImage.getWidth()];
        for (int d = 0; d <= depth; d++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            rasterized[d][row][column] += (1 << (8 * rgbbyte + power)) * framesOfHashing[d][row][16 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //This does the GIF file
        BufferedImage[] images = new BufferedImage[rasterized.length];
        int[][] imagesRasters = new int[depth + 1][inImage.getHeight() * inImage.getWidth()];
        ImageWriter gifWriter = ImageIO.getImageWritersByFormatName("gif").next();
        ImageOutputStream outputStream = ImageIO.createImageOutputStream(new File("src/ImagesProcessed/" + filepath + "gif.gif"));
        gifWriter.setOutput(outputStream);
        short[] outRaster = new short[inImage.getHeight() * inImage.getWidth()];
        gifWriter.prepareWriteSequence(null);
        BufferedImage outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
        for (int repeat = 0; repeat < 1; repeat++) {
            for (int d = 0; d <= depth; d++) {
                File outFile = new File("src/ImagesProcessed/GifOutput/" + filepath + "iteration" + d + ".bmp");
                outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
                outRaster = ((DataBufferUShort) outImage.getRaster().getDataBuffer()).getData();
                for (int index = 0; index < outRaster.length; index++) {
                    outRaster[index] = rasterized[d][index / inImage.getWidth()][index % inImage.getWidth()];
                }
                ImageIO.write(outImage, "bmp", outFile);
                IIOImage image = new IIOImage(outImage, null, null);
                gifWriter.writeToSequence(image, null);
            }
        }
        gifWriter.endWriteSequence();
        System.out.println("depth: " + depth);
        System.out.println("done with gif");
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        BufferedImage inverse = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
        int[][][] undoInput = new int[16][inImage.getHeight()][inImage.getWidth()];
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                undoInput[3][row][column] = bfield[row][column];
            }
        }
        System.out.println("undoInput[3].length: " + undoInput[0].length + " " + undoInput[1][0].length);
        for (int posNegt = 0; posNegt < 8; posNegt++) {
            bFieldSet[posNegt] = initializeDepthZero(bFieldSet[posNegt], unpackedList[posNegt])[1];
            bFieldSet[posNegt + 8] = initializeDepthMax(bFieldSet[posNegt + 8], unpackedList[posNegt])[1];
        }
        int[][] undo = hashInverseDepth0(bFieldSet, 1, 3);
        short[][] undoRasterized = new short[inverse.getHeight()][inverse.getWidth()];
        System.out.println("inverse.getHeight(): " + inverse.getHeight() + " inverse.getWidth(): " + inverse.getWidth());
        System.out.println(undo.length + " " + undo[0].length);
        System.out.println(undoRasterized.length + " " + undoRasterized[0].length);
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inverse.getHeight(); row++) {
                for (int column = 0; column < inverse.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            undoRasterized[row][column] += undo[row][column * 16 + 8 * rgbbyte + power] << (8 * rgbbyte + power);
                        }
                    }
                }
            }
        }
        short[] inverseImageRaster = ((DataBufferUShort) inverse.getRaster().getDataBuffer()).getData();
        for (int row = 0; row < inverse.getHeight(); row++) {
            for (int column = 0; column < inverse.getWidth(); column++) {
                //if (row == 655 || column == 655) { System.out.println("row: " + row + ", column: " + column); }
                inverseImageRaster[row * inImage.getWidth() + column] = (short) (undoRasterized[row][column] ^ inRaster[row * inImage.getWidth() + column]);
            }
        }
        File inverseFile = new File("src/ImagesProcessed/" + filepath + "inverse.bmp");
        ImageIO.write(inverse, "bmp", inverseFile);
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        inverse = new BufferedImage(inverse.getWidth(), inverse.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
        inverseImageRaster = ((DataBufferUShort) inverse.getRaster().getDataBuffer()).getData();
        undo = reconstructDepthD(framesOfHashing[1], 1, 3,true);
        undoRasterized = new short[inverse.getHeight()][inverse.getWidth()];
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inverse.getHeight(); row++) {
                for (int column = 0; column < inverse.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            undoRasterized[row][column] += undo[row][column * 16 + 8 * rgbbyte + power] << (8 * rgbbyte + power);
                        }
                    }
                }
            }
        }
        for (int row = 0; row < inverse.getHeight(); row++) {
            for (int column = 0; column < inverse.getWidth(); column++) {
                //if (row == 655 || column == 655) { System.out.println("row: " + row + ", column: " + column); }
                inverseImageRaster[row * inImage.getWidth() + column] = (short) (undoRasterized[row][column] ^ inRaster[inImage.getWidth() * row + column]);
            }
        }
        File inverseDepth1 = new File("src/ImagesProcessed/" + filepath + "inverseDepth1.bmp");
        ImageIO.write(inverse, "bmp", inverseDepth1);
        int numDifferent = 0;
        for (int row = 0; row < inRaster.length; row++) {
            long a = inverseImageRaster[row] ^ inRaster[row];
            a = (long) Math.abs(a);
            for (int power = 0; power < 16; power++) {
                if (((a >> power)) % 2 == 1) {
                    numDifferent++;
                }
            }
        }
        System.out.println("numDifferent: " + numDifferent);
        long tot = inRaster.length * 16;
        double rate = (double) numDifferent / tot;
        System.out.println("rate: " + rate);
    }

    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void bitmapTransformCompleteSet(String filepath, int dummy) throws IOException {
        //String filepath = "kitchenAlteredRGB.bmp";
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int size = inImage.getWidth();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 7;
        int[][][] framesOfHashing = new int[depth][inImage.getHeight()][inImage.getWidth() * 8];
        int[][] field = new int[inImage.getHeight()][inImage.getWidth() * 8];
        int[][] bfield = new int[inImage.getHeight()][inImage.getWidth() * 16];
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight(): " + inRaster.length / inImage.getHeight());
        System.out.println("inRaster.length/inImage.getWidth(): " + inRaster.length / inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight()/inImage.getWidth(): " + inRaster.length / inImage.getHeight() / inImage.getWidth());
        //Transforms the image into its appropriate local algorithm format
//        for (int row = 0; row < inImage.getHeight(); row++) {
//            for (int column = 0; column < inImage.getWidth(); column++) {
//                for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
//                    for (int lr = 0; lr < 2; lr++) {
//                        int rasterCoordX = row * inImage.getWidth() + column;
//                        field[row][8 * column + 2 * rgbbyte + lr] = (int) Math.abs((inRaster[rasterCoordX] >> (4 * rgbbyte + 2 * lr)) % 16);
//                        for (int power = 0; power < 4; power++) {
//                            bfield[row][32 * column + 8 * rgbbyte + 4 * lr + power] = (field[row][8 * column + 2 * rgbbyte + lr] >> power) % 2;
//                        }
//                    }
//                }
//            }
//        }
        int[][][] bFieldSet = new int[16][bfield.length][bfield[0].length];
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                    for (int power = 0; power < 8; power++) {
                        bfield[row][16 * column + 8 * rgbbyte + power] = (int) Math.abs((inRaster[row * inImage.getWidth() + column] >> (8 * rgbbyte + power)) % 2);
                        for (int posNegt = 0; posNegt < 16; posNegt++) {
                            bFieldSet[posNegt][row][16 * column + 8 * rgbbyte + power] = bfield[row][16 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        //Initialize the minMax codeword truth table set
        initWolframs();
        //hashUtilities.readFromFile();
        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        bfield = initializeDepthZero(bfield, unpackedList[3])[1];
        for (int t = 0; t < 8; t++) {
            //bFieldSet[t] = initializeDepthZero(bFieldSet[t], unpackedList[t])[1];
            //bFieldSet[8 + t] = initializeDepthMax(bFieldSet[8 + t], unpackedList[t])[1];
        }
        //Do the transform
        framesOfHashing = ecaMinTransform(bfield, unpackedList[3], depth);
        int[][][] hashSet = new int[16][inImage.getHeight()][inImage.getWidth()];
        int[][][][] hashed = new int[16][10][inImage.getHeight()][inImage.getWidth()];
        int[][][] abHashSet = new int[16][inImage.getHeight()][inImage.getWidth()];
        int[][][][] abHashed = new int[16][10][inImage.getHeight()][inImage.getWidth()];
        Random rand = new Random();
        int randCol = rand.nextInt(0,bFieldSet[0][0].length);
        int randRow = rand.nextInt(0,bFieldSet[0].length);
        int[][][] abbFieldSet = new int[16][bFieldSet[0].length][bFieldSet[0][0].length];
        int randNext = rand.nextInt(0,16);
        int numChanges = 8;
        for (int t = 0; t < 8; t++) {
            for (int row = 0; row < bFieldSet[0].length; row++) {
                for (int column = 0; column < bFieldSet[0][0].length; column++) {
                    abbFieldSet[t][row][column] = bFieldSet[t][row][column];
                    abbFieldSet[t+8][row][column] = bFieldSet[t+8][row][column];
                }
            }

            //abbFieldSet[t][randRow][randCol] = (15-abbFieldSet[t][randRow][randCol]);
            //abbFieldSet[t+8][randRow][randCol] = (15-abbFieldSet[t+8][randRow][randCol]);
            //abbFieldSet[t][randRow][randCol] = randNext;
            //abbFieldSet[t+8][randRow][randCol] = randNext;

        }
        for (int change = 0; change < numChanges; change++) {
            randCol = rand.nextInt(0,bFieldSet[0][0].length);
            randRow = rand.nextInt(0,bFieldSet[0].length);
            randNext = rand.nextInt(0,16);
            for (int t = 0; t < 8; t++) {
                abbFieldSet[t][randRow][randCol] = randNext;
                abbFieldSet[t+8][randRow][randCol] = randNext;
            }
        }
        int[] avalancheDifferences = new int[16];
        System.out.println("depth: " + depth);
        for (int t = 0; t < 8; t++) {
            //hashSet[t] = ecaMinTransform(bFieldSet[t], unpackedList[t], depth)[1];
            //hashSet[8 + t] = ecaMaxTransform(bFieldSet[8 + t], unpackedList[t], depth)[1];
            hashSet[t] = bFieldSet[t];
            hashSet[8 + t] = bFieldSet[8 + t];
            abHashSet[t] = abbFieldSet[t];
            abHashSet[8 + t] = abbFieldSet[8 + t];
            hashSet[t] = ecaMinTransform(hashSet[t], unpackedList[t], depth)[depth];
            hashed[t] = ecaMinTransform(hashSet[t], unpackedList[t], depth);
            hashSet[8 + t] = ecaMaxTransform(hashSet[8 + t], unpackedList[t], depth)[depth];
            hashed[t + 8] = ecaMaxTransform(hashSet[8 + t], unpackedList[t], depth);
            abHashed[t] = ecaMinTransform(abbFieldSet[t], unpackedList[t], depth);
            abHashed[8 + t] = ecaMaxTransform(abbFieldSet[8 + t], unpackedList[t], depth);
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {

                    for (int bit = 0; bit < 16; bit++){
                        avalancheDifferences[t] += (((hashed[t][depth][row][column]>>bit)%2)^((abHashed[t][depth][row][column]>>bit)%2));
                        avalancheDifferences[8+t] += (((hashed[8+t][depth][row][column]>>bit)%2)^((abHashed[8+t][depth][row][column]>>bit)%2));

                    }
                }
            }
        }
        System.out.println("avalancheDifferences: " + Arrays.toString(avalancheDifferences));
        //Convert the transform back into appropriate bitmap RGB format
        short[][][] rasterized = new short[depth + 1][inImage.getHeight()][inImage.getWidth()];
        for (int d = 0; d <= depth; d++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            rasterized[d][row][column] += (1 << (8 * rgbbyte + power)) * framesOfHashing[d][row][16 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        short[][][] rasterizedSet = new short[16][inImage.getHeight()][inImage.getWidth()];
        for (int t = 0; t < 8; t++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            rasterizedSet[t][row][column] += (1 << (8 * rgbbyte + power)) * hashSet[t][row][16 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //This does the GIF file
        BufferedImage[] images = new BufferedImage[rasterized.length];
        int[][] imagesRasters = new int[depth + 1][inImage.getHeight() * inImage.getWidth()];
        ImageWriter gifWriter = ImageIO.getImageWritersByFormatName("gif").next();
        ImageOutputStream outputStream = ImageIO.createImageOutputStream(new File("src/ImagesProcessed/" + filepath + "gif.gif"));
        gifWriter.setOutput(outputStream);
        short[] outRaster = new short[inImage.getHeight() * inImage.getWidth()];
        gifWriter.prepareWriteSequence(null);
        BufferedImage outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
        for (int repeat = 0; repeat < 1; repeat++) {
            for (int d = 0; d <= depth; d++) {
                File outFile = new File("src/ImagesProcessed/GifOutput/" + filepath + "iteration" + d + ".bmp");
                outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
                outRaster = ((DataBufferUShort) outImage.getRaster().getDataBuffer()).getData();
                for (int index = 0; index < outRaster.length; index++) {
                    outRaster[index] = rasterized[d][index / inImage.getWidth()][index % inImage.getWidth()];
                }
                ImageIO.write(outImage, "bmp", outFile);
                IIOImage image = new IIOImage(outImage, null, null);
                gifWriter.writeToSequence(image, null);
            }
        }
        gifWriter.endWriteSequence();
        System.out.println("depth: " + depth);
        System.out.println("done with gif");
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        BufferedImage inverse = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
        int[][][] undoInput = new int[16][inImage.getHeight()][inImage.getWidth()];
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                undoInput[3][row][column] = bfield[row][column];
            }
        }
        System.out.println("undoInput[3].length: " + undoInput[0].length + " " + undoInput[1][0].length);
        int[][] undoSet = reconstructDepthD(hashSet, 2, true);
        for (int t = 0; t < 8; t++) {
            //undoSet[t] = initializeDepthZero(bFieldSet[t], unpackedList[t])[1];
        }
        int[][] undo = hashInverseDepth0(bFieldSet, 1, 3);
        short[][] undoRasterized = new short[inverse.getHeight()][inverse.getWidth()];
        short[][] undoRasterizedSet = new short[inverse.getHeight()][inverse.getWidth()];
        System.out.println("inverse.getHeight(): " + inverse.getHeight() + " inverse.getWidth(): " + inverse.getWidth());
        System.out.println(undo.length + " " + undo[0].length);
        System.out.println(undoRasterized.length + " " + undoRasterized[0].length);
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inverse.getHeight(); row++) {
                for (int column = 0; column < inverse.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            undoRasterized[row][column] += undo[row][column * 16 + 8 * rgbbyte + power] << (8 * rgbbyte + power);
                            undoRasterizedSet[row][column] += undoSet[row][column * 16 + 8 * rgbbyte + power] << (8 * rgbbyte + power);
                        }
                    }
                }
            }
        }
        for (int t = 0; t <  8; t++){
            int total = 0;
            int[][] recon = reconstructDepthD(hashed[t][depth],depth,t,true);
            for (int row = 0; row < recon.length; row++) {
                for (int column = 0; column < recon[0].length; column++) {
                    //total += recon[row][column] ^ hashed[t][depth-1][row][column];
                    for (int power = 0; power < 4; power++) {
                        total += ((recon[row][column] >> power) % 2) ^ ((hashed[t][depth-1][row][column] >> power) % 2);
                    }
                }
            }
            System.out.println("total: " + total);
            total = 0;
            recon = reconstructDepthD(hashed[t+8][depth],depth,t+8,true);
            for (int row = 0; row < recon.length; row++) {
                for (int column = 0; column < recon[0].length; column++) {
                    //total += recon[row][column] ^ hashed[t+8][depth-1][row][column];
                    for (int power = 0; power < 4; power++) {
                        total += ((recon[row][column] >> power) % 2) ^ ((hashed[t+8][depth-1][row][column] >> power) % 2);
                    }
                }
            }
            System.out.println("total: " + total);
        }
        for (int t = 0; t <  0; t++){
            int total = 0;
            int[][] recon = reconstructDepthD(hashed[t][depth],depth,t,true);
            for (int row = 0; row < recon.length; row++) {
                for (int column = 0; column < recon[0].length; column++) {
                    //total += recon[row][column] ^ hashed[t][depth-1][row][column];
                    for (int power = 0; power < 4; power++) {
                        total += ((recon[row][column] >> power) % 2) ^ ((abHashed[t][depth-1][row][column] >> power) % 2);
                    }
                }
            }
            System.out.println("total: " + total);
            total = 0;
            recon = reconstructDepthD(hashed[t+8][depth],depth,t+8,true);
            for (int row = 0; row < recon.length; row++) {
                for (int column = 0; column < recon[0].length; column++) {
                    //total += recon[row][column] ^ hashed[t+8][depth-1][row][column];
                    for (int power = 0; power < 4; power++) {
                        total += ((recon[row][column] >> power) % 2) ^ ((abHashed[t+8][depth-1][row][column] >> power) % 2);
                    }
                }
            }
            System.out.println("total: " + total);
        }
        short[] inverseImageRaster = ((DataBufferUShort) inverse.getRaster().getDataBuffer()).getData();
        short[] inverseImageRasterSet = new short[inverse.getHeight() * inverse.getWidth()];
        for (int row = 0; row < inverse.getHeight(); row++) {
            for (int column = 0; column < inverse.getWidth(); column++) {
                //if (row == 655 || column == 655) { System.out.println("row: " + row + ", column: " + column); }
                inverseImageRaster[row * inImage.getWidth() + column] = (short) (undoRasterized[row][column] ^ inRaster[row * inImage.getWidth() + column]);
                inverseImageRasterSet[row * inImage.getWidth() + column] = (short) (undoRasterizedSet[row][column] ^ inRaster[inImage.getWidth() * row + column]);
            }
        }
        File inverseFile = new File("src/ImagesProcessed/" + filepath + "inverse.bmp");
        ImageIO.write(inverse, "bmp", inverseFile);
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        inverse = new BufferedImage(inverse.getWidth(), inverse.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
        //inverseImageRaster = ((DataBufferUShort) inverse.getRaster().getDataBuffer()).getData();
        undo = reconstructDepthD(framesOfHashing[1], 1, 3,true);
        undoRasterized = new short[inverse.getHeight()][inverse.getWidth()];
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inverse.getHeight(); row++) {
                for (int column = 0; column < inverse.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            undoRasterized[row][column] += undo[row][column * 16 + 8 * rgbbyte + power] << (8 * rgbbyte + power);
                        }
                    }
                }
            }
        }
        for (int row = 0; row < inverse.getHeight(); row++) {
            for (int column = 0; column < inverse.getWidth(); column++) {
                //if (row == 655 || column == 655) { System.out.println("row: " + row + ", column: " + column); }
                //inverseImageRaster[row * inImage.getWidth() + column] = (short) (undoRasterized[row][column] ^ inRaster[inImage.getWidth() * row + column]);
            }
        }
        File inverseDepth1 = new File("src/ImagesProcessed/" + filepath + "inverseDepth1.bmp");
        ImageIO.write(inverse, "bmp", inverseDepth1);
        int numDifferent = 0;
        for (int row = 0; row < inRaster.length; row++) {
            //long a = inverseImageRasterSet[row] ^ inRaster[row];
            long a = inverseImageRasterSet[row] ^ inRaster[row];
            a = (long) Math.abs(a);
            for (int power = 0; power < 16; power++) {
                if (((a >> power)) % 2 == 1) {
                    numDifferent++;
                }
            }
        }
        System.out.println("numDifferent: " + numDifferent);
        long tot = inRaster.length * 16;
        double rate = (double) numDifferent / tot;
        System.out.println("rate: " + rate);
    }

    public int[][] altAddition(int size, int orGoesTo, int andGoesTo) {
        int[][] out = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int[] a = new int[size];
                int[] b = new int[size];
                int[] result = new int[size];
                int[] carry = new int[size];
                int[] comp = new int[size];
                for (int power = 0; power < size; power++) {
                    a[power] = (row >> power) % 2;
                    b[power] = (col >> power) % 2;
                    result[power] = (2 * a[power] + b[power]);
                    result[power] = (orGoesTo >> (result[power])) % 2;
                    carry[power] = (2 * a[power] + b[power] + 1) % 2;
                    carry[power] = (andGoesTo >> (carry[power])) % 2;
                    int[] temp = new int[size];
                    for (int p = 1; p < size; p++) {
                        temp[p] = carry[(power - 1 + size) % size];
                    }
                    carry = temp;
                }
                while (!Arrays.equals(comp, carry)) {
                    a = result;
                    b = carry;
                    for (int power = 0; power < size; power++) {
                        result[power] = (2 * a[power] + b[power]);
                        result[power] = (orGoesTo >> (result[power])) % 2;
                        carry[power] = (2 * a[power] + b[power] + 1) % 2;
                        carry[power] = (andGoesTo >> (carry[power])) % 2;
                        int[] temp = new int[size];
                        for (int p = 1; p < size; p++) {
                            temp[p] = carry[(power - 1 + size) % size];
                        }
                        carry = temp;
                    }
                }
                for (int power = 0; power < size; power++) {
                    out[row][col] += (1 << power) * result[power];
                }
            }
        }
        return out;
    }

    public int[][][][] allAltAddition(int[][] in) {
        int[][][][] out = new int[16][16][16][16];
        for (int gate = 0; gate < 16; gate++) {
            for (int element = 0; element < 16; element++) {
                out[gate][element] = altAddition(16, in[gate][element], in[element][gate]);
            }
        }
        return out;
    }
    /**
     * The first part of this function generates the addition tables of adding two hash tiles together,
     * Showing that adding tiles does indeed result in a non-reduced Hadamard matrix. After that
     * is some experimentation ???
     */
    public void testAddition() {
        hashTransform.initWolframs();
        int[][][][] additionTables = new int[2][8][16][16];
        //generate addition table for every (a,b) for every minMax codeword 8-tuple
        for (int a = 0; a < 16; a++) {
            for (int b = 0; b < 16; b++) {
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        //generate the neighborhoods of a and b
                        int[][] aa = hash.generateCodewordTile(a, hashTransform.unpackedList[t]);
                        int[][] bb = hash.generateCodewordTile(b, hashTransform.unpackedList[t]);
                        int[][] cc = new int[4][4];
                        int[][] dd = new int[4][4];
                        //add the neighborhoods together pair-wise
                        for (int row = 0; row < 4; row++) {
                            for (int col = 0; col < 4; col++) {
                                cc[row][col] = aa[row][col] + bb[row][col];
                                if (cc[row][col] < 2) dd[row][col] = 0;
                                else dd[row][col] = 1;
                            }
                        }
                        //find the codeword of the sum of the neighborhoods
                        int[][] ccc = hash.findMinimizingCodeword(hashTransform.unpackedList[t], dd);
                        int result = 0;
                        for (int column = 0; column < 4; column++) {
                            result += (int) Math.pow(2, column) * ccc[0][column];
                        }
                        //store it in the table
                        additionTables[posNeg][t][a][b] = result;
                    }
                }
            }
        }
        //Display
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                CustomArray.plusArrayDisplay(additionTables[posNeg][t], false, false, "posNeg: " + posNeg + " t: " + t + " " + hashTransform.unpackedList[t]);
            }
        }
        //
        //
        //
        //Check against the non-reduced Hadamard row AND column matrix
        //And check the distribution of terms in the addition tables
        int[][] outTable = new int[16][16];
        Hadamard hadamard = new Hadamard();
        int[][] hOut = new int[16][16];
        int[][] rowANDcol = new int[16][16];
        int[] distroAddition = new int[16];
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                rowANDcol[row][col] = row & col;
                distroAddition[rowANDcol[row][col]]++;
                rowANDcol[row][col] ^= additionTables[0][5][row][col];
            }
        }
        CustomArray.plusArrayDisplay(rowANDcol, false, false, "rowANDcol");
        System.out.println("distroAddition: " + Arrays.toString(distroAddition));
        int[][] h = hadamard.generateHadamard(16);
        for (int row = 0; row < 16; row++) {
            int tot = 0;
            for (int col = 0; col < 16; col++) {
                tot = 0;
                for (int power = 0; power < 4; power++) {
                    tot ^= ((additionTables[0][5][row][col] / (1 << power)) % 2);
                }
                outTable[row][col] = tot;
                if (h[row][col] == 1) {
                    h[row][col] = 0;
                } else {
                    h[row][col] = 1;
                }
                hOut[row][col] = h[row][col] ^ outTable[row][col];
            }
        }
        CustomArray.plusArrayDisplay(outTable, false, false, "xor-ed out by power");
        CustomArray.plusArrayDisplay(hOut, false, false, "AlgorithmCode.Hadamard xor outTable");
    }
    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void bitmapTransformCompleteSet(String filepath, int dummy) throws IOException {
        //String filepath = "kitchenAlteredRGB.bmp";
        HashTransform hash = new HashTransform();
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int size = inImage.getWidth();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 1;
        int[][][] framesOfHashing = new int[depth][inImage.getHeight()][inImage.getWidth() * 8];
        int[][] field = new int[inImage.getHeight()][inImage.getWidth() * 8];
        int[][] bfield = new int[inImage.getHeight()][inImage.getWidth() * 16];
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight(): " + inRaster.length / inImage.getHeight());
        System.out.println("inRaster.length/inImage.getWidth(): " + inRaster.length / inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight()/inImage.getWidth(): " + inRaster.length / inImage.getHeight() / inImage.getWidth());
        //Transforms the image into its appropriate local algorithm format
//        for (int row = 0; row < inImage.getHeight(); row++) {
//            for (int column = 0; column < inImage.getWidth(); column++) {
//                for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
//                    for (int lr = 0; lr < 2; lr++) {
//                        int rasterCoordX = row * inImage.getWidth() + column;
//                        field[row][8 * column + 2 * rgbbyte + lr] = (int) Math.abs((inRaster[rasterCoordX] >> (4 * rgbbyte + 2 * lr)) % 16);
//                        for (int power = 0; power < 4; power++) {
//                            bfield[row][32 * column + 8 * rgbbyte + 4 * lr + power] = (field[row][8 * column + 2 * rgbbyte + lr] >> power) % 2;
//                        }
//                    }
//                }
//            }
//        }
        int[][][] bFieldSet = new int[16][bfield.length][bfield[0].length];
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                    for (int power = 0; power < 8; power++) {
                        bfield[row][16 * column + 8 * rgbbyte + power] = (int) Math.abs((inRaster[row * inImage.getWidth() + column] >> (8 * rgbbyte + power)) % 2);
                        for (int posNegt = 0; posNegt < 16; posNegt++) {
                            bFieldSet[posNegt][row][16 * column + 8 * rgbbyte + power] = bfield[row][16 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        //Initialize the minMax codeword truth table set
        hash.initWolframs();
        testAllLogic(true);
        //hashUtilities.readFromFile();
        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        //bfield = hash.initializeDepthZero(bfield, hash.unpackedList[3])[1];
        for (int t = 0; t < 8; t++) {
            //bFieldSet[t] = initializeDepthZero(bFieldSet[t], unpackedList[t])[1];
            //bFieldSet[8 + t] = initializeDepthMax(bFieldSet[8 + t], unpackedList[t])[1];
        }
        //Do the transform
        //framesOfHashing = hash.ecaTransform(bfield, hash.unpackedList[3], depth);
        int[][][] hashSet = new int[16][inImage.getHeight()][inImage.getWidth()];
        depth = 3;
        int gate = 8;
        for (int t = 0; t < 8; t++) {
            System.out.println("t: " + t);
            hashSet[t] = hash.ecaTransform(bFieldSet[t], hash.unpackedList[t], depth)[depth];
            hashSet[8 + t] = hash.ecaTransform(bFieldSet[8 + t], hash.unpackedList[t], depth)[depth];
            //hashSet[t] = bFieldSet[t];
            //hashSet[8 + t] = bFieldSet[8 + t];
        }
        int[][] modification = generateOperation(hashSet[0].length, hashSet[0][0].length);
        int[][][] modificationTransformed = new int[16][hashSet[0].length][hashSet[0][0].length];
        int[][][] modifiedSet = new int[16][hashSet[0].length][hashSet[0][0].length];
        int[][][] internallyModifiedSet = new int[16][hashSet[0].length][hashSet[0][0].length];
        for (int t = 0; t < 8; t++) {
            System.out.println("t: " + t);
            for (int row = 0; row < modifiedSet[0].length; row++) {
                for (int column = 0; column < modifiedSet[0][0].length; column++) {
                    modifiedSet[t][row][column] = bFieldSet[t][row][column] + 2 * modification[row][column];
                    modifiedSet[8 + t][row][column] = bFieldSet[8 + t][row][column] + 2 * modification[row][column];
                    modifiedSet[t][row][column] = (gate >> modifiedSet[t][row][column]) % 2;
                    modifiedSet[8 + t][row][column] = (gate >> modifiedSet[8 + t][row][column]) % 2;
                }
            }
            modificationTransformed[t] = hash.ecaTransform(modification, hash.unpackedList[t], depth)[depth];
            modificationTransformed[8 + t] = hash.ecaTransform(modification, hash.unpackedList[t], depth)[depth];
            modifiedSet[t] = hash.ecaTransform(modifiedSet[t], hash.unpackedList[t], depth)[depth];
            modifiedSet[8 + t] = hash.ecaTransform(modifiedSet[8 + t], hash.unpackedList[t], depth)[depth];
            for (int row = 0; row < modifiedSet[0].length; row++) {
                for (int column = 0; column < modifiedSet[0][0].length; column++) {
                    int tot = 0;
                    for (int power = 0; power < 4; power++) {
                        int ab = ((hashSet[t][row][column] >> power) % 2) + 2 * ((modificationTransformed[t][row][column] >> power) % 2);
                        ab = (logicTransform[gate][t] >> ab) % 2;
                        tot += (1 << power) * ab;
                    }
                    internallyModifiedSet[t][row][column] = tot;
                    tot = 0;
                    for (int power = 0; power < 4; power++) {
                        int ab = ((hashSet[8 + t][row][column] >> power) % 2) + 2 * ((modificationTransformed[8 + t][row][column] >> power) % 2);
                        ab = (logicTransform[gate][8 + t] >> ab) % 2;
                        tot += (1 << power) * ab;
                    }
                    internallyModifiedSet[8 + t][row][column] = tot;
                }
            }
        }
        int[] numDifferent = new int[16];
        for (int t = 0; t < 8; t++) {
            for (int row = 0; row < modifiedSet[0].length; row++) {
                for (int column = 0; column < modifiedSet[0][0].length; column++) {
                    numDifferent[t] += (modifiedSet[t][row][column] ^ internallyModifiedSet[t][row][column]);
                    numDifferent[8 + t] += (modifiedSet[8 + t][row][column] ^ internallyModifiedSet[8 + t][row][column]);
                }
            }
        }
        System.out.println("numDifferent: " + Arrays.toString(numDifferent));
    }
    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void bitmapTransform(int dummy) throws IOException {
        String filepath = "denTwoByteRGB.bmp";
        File file = new File(filepath);
        BufferedImage inImage = ImageIO.read(file);
        int[] inRaster = ((DataBufferInt) inImage.getRaster().getDataBuffer()).getData();
        int size = inImage.getWidth();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 1;
        int[][][] framesOfHashing = new int[depth][inImage.getHeight()][inImage.getWidth() * 8];
        int[][] field = new int[inImage.getHeight()][inImage.getWidth() * 8];
        int numHexChars = 4;
        int[][] bfield = new int[inImage.getHeight()][inImage.getWidth() * numHexChars];
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight(): " + inRaster.length / inImage.getHeight());
        System.out.println("inRaster.length/inImage.getWidth(): " + inRaster.length / inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight()/inImage.getWidth(): " + inRaster.length / inImage.getHeight() / inImage.getWidth());
        //Transforms the image into its appropriate local algorithm format
//        for (int row = 0; row < inImage.getHeight(); row++) {
//            for (int column = 0; column < inImage.getWidth(); column++) {
//                for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
//                    for (int lr = 0; lr < 2; lr++) {
//                        int rasterCoordX = row * inImage.getWidth() + column;
//                        field[row][8 * column + 2 * rgbbyte + lr] = (int) Math.abs((inRaster[rasterCoordX] >> (4 * rgbbyte + 2 * lr)) % 16);
//                        for (int power = 0; power < 4; power++) {
//                            bfield[row][32 * column + 8 * rgbbyte + 4 * lr + power] = (field[row][8 * column + 2 * rgbbyte + lr] >> power) % 2;
//                        }
//                    }
//                }
//            }
//        }
        int[][][] bFieldSet = new int[16][bfield.length][bfield[0].length];
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                for (int rgbhex = 0; rgbhex < numHexChars; rgbhex++) {
                    bfield[row][numHexChars * column + rgbhex] = ((int) Math.abs((inRaster[row * inImage.getWidth() + column] / (int) Math.pow(2, (4 * rgbhex))) % 16));
                    for (int posNegt = 0; posNegt < 0; posNegt++) {
                        //bFieldSet[posNegt][row][4 * column + 4 * rgbhex + power] = bfield[row][4 * column + 4 * rgbhex + power];
                    }
                }
            }
        }
        //Initialize the minMax codeword truth table set
        //hash.initWolframs();
        //generateAbsolutelyEverything(4);
        readFromFile();
        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        //bfield = hash.initializeDepthZero(bfield, hash.unpackedList[3])[1];
        //Do the transform
        //bfield = hash.ecaMinTransformRGBrowNorm(bfield, hash.unpackedList[3], 1)[1];
        //framesOfHashing = hash.ecaMinTransform(bfield, hash.unpackedList[3], 1);
        int[][] edges = new int[1][1];
        edges = doTransformNormalizePixels(bfield, depth);
//        edges = doTransform(bfield, 0);
        //edges = doTransform(edges, 0, 4);
        //edges = detectEdges(framesOfHashing[1]);
        // edges = detectEdges(bfield);
        for (int cycle = 0; cycle < 1; cycle++) {
            //edges = detectEdges(edges);
        }
        //edges = detectEdges(edges);
        //edges = detectEdges(edges);
        //Convert the transform back into appropriate bitmap RGB format
        int[][][] rasterized = new int[depth + 1][inImage.getHeight()][inImage.getWidth()];
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbhex = 0; rgbhex < numHexChars; rgbhex++) {
                        rasterized[d][row][column] += (int) Math.pow(2, 4 * rgbhex) * edges[row][numHexChars * column + rgbhex];
                    }
                }
            }
        }
        BufferedImage outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[] outRaster = ((DataBufferInt) outImage.getRaster().getDataBuffer()).getData();
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                outRaster[inImage.getWidth() * row + column] = rasterized[0][row][column];
            }
        }
        File outFile = new File("src/denProcessedTwoRGB.bmp");
        ImageIO.write(outImage, "bmp", outFile);
    }

    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void bitmapTransformTwoRGBBytes(String filepath, int dummy) throws IOException {
        //String filepath = "kitchenAlteredRGB.bmp";
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.lastIndexOf("."));
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int size = inImage.getWidth();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 1;
        int[][][] framesOfHashing = new int[depth][inImage.getHeight()][inImage.getWidth() * 8];
        int[][] field = new int[inImage.getHeight()][inImage.getWidth() * 8];
        int numHexChars = 4;
        int[][] bfield = new int[inImage.getHeight()][inImage.getWidth() * numHexChars];
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight(): " + inRaster.length / inImage.getHeight());
        System.out.println("inRaster.length/inImage.getWidth(): " + inRaster.length / inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight()/inImage.getWidth(): " + inRaster.length / inImage.getHeight() / inImage.getWidth());
        //Transforms the image into its appropriate local algorithm format
        int[][][] bFieldSet = new int[16][bfield.length][bfield[0].length];
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                for (int rgbhex = 0; rgbhex < numHexChars; rgbhex++) {
                    bfield[row][numHexChars * column + rgbhex] = ((int) Math.abs((inRaster[row * inImage.getWidth() + column] / (int) Math.pow(2, (4 * rgbhex))) % 16));
                    for (int posNegt = 0; posNegt < 0; posNegt++) {
                        //bFieldSet[posNegt][row][4 * column + 4 * rgbhex + power] = bfield[row][4 * column + 4 * rgbhex + power];
                    }
                }
            }
        }
        //Initialize the minMax codeword truth table set
        //hash.initWolframs();
        //generateAbsolutelyEverything(4);
        readFromFile();
        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        //bfield = hash.initializeDepthZero(bfield, hash.unpackedList[3])[1];
        //Do the transform
        //bfield = hash.ecaMinTransformRGBrowNorm(bfield, hash.unpackedList[3], 1)[1];
        //framesOfHashing = hash.ecaMinTransform(bfield, hash.unpackedList[3], 1);
        int[][] edges = new int[1][1];
        edges = doTransformNormalizePixels(bfield, depth);
//        edges = doTransform(bfield, 0);
        //edges = doTransform(edges, 0, 4);
        //edges = doTransform(edges, 1, 4);
        //edges = doTransform(edges, 2, 4);
        for (int d = 0; d < 3; d++) {
            edges = doTransform(edges, d, 4);
        }
        //edges = detectEdges(framesOfHashing[1]);
        // edges = detectEdges(bfield);
        for (int cycle = 0; cycle < 0; cycle++) {
            //edges = detectEdges(edges);
        }
        //edges = detectEdges(edges);
        //edges = detectEdges(edges);
        //Convert the transform back into appropriate bitmap RGB format
        int[][][] rasterized = new int[depth + 1][inImage.getHeight()][inImage.getWidth()];
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbhex = 0; rgbhex < numHexChars; rgbhex++) {
                        rasterized[d][row][column] += (int) Math.pow(2, 4 * rgbhex) * edges[row][numHexChars * column + rgbhex];
                    }
                    rasterized[d][row][column] ^= inRaster[row * inImage.getWidth() + column];
                }
            }
        }
        BufferedImage outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[] outRaster = ((DataBufferInt) outImage.getRaster().getDataBuffer()).getData();
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                outRaster[inImage.getWidth() * row + column] = rasterized[0][row][column];
            }
        }
        File outFile = new File("src/ImagesProcessed/" + filepath + "hashedXORoriginal.bmp");
        ImageIO.write(outImage, "bmp", outFile);
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbhex = 0; rgbhex < numHexChars; rgbhex++) {
                        rasterized[d][row][column] += (int) Math.pow(2, 4 * rgbhex) * edges[row][numHexChars * column + rgbhex];
                    }
                    //rasterized[d][row][column] ^= inRaster[row * inImage.getWidth() + column];
                }
            }
        }
        outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        outRaster = ((DataBufferInt) outImage.getRaster().getDataBuffer()).getData();
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                outRaster[inImage.getWidth() * row + column] = rasterized[0][row][column];
            }
        }
        outFile = new File("src/ImagesProcessed/" + filepath + "hashed.bmp");
        ImageIO.write(outImage, "bmp", outFile);
    }

    public void checkDoubles() {
        generateAbsolutelyEverything(4);
        generateRelativeToFunctionTile();
        int size = 4;
        int sizeSquare = 16;
        int[][] oneChangeZeros = new int[sizeSquare][sizeSquare];
        int[][][][] overlap = new int[sizeSquare][sizeSquare][size][size];
        int[][] functionTile = generateFunctionTile(4);
        gateLoop:
        for (int gate = 0; gate < 256; gate++) {
            boolean gateWorks = true;
            for (int row = 0; row < sizeSquare; row++) {
                for (int col = 0; col < sizeSquare; col++) {
                    int[][] firstChange = hash.m.addressToArray(4, relativeTruthTable[(1 << row)]);
                    int[][] secondChange = hash.m.addressToArray(4, relativeTruthTable[(1 << col)]);
                    int[][] bothChanges = hash.m.addressToArray(4, relativeTruthTable[(1 << row) ^ (1 << col)]);
                    boolean firstZero = false;
                    if (relativeTruthTable[1 << row] == 0) {
                        oneChangeZeros[row][col] = 1;
                        firstZero = true;
                    }
                    boolean secondZero = false;
                    if (relativeTruthTable[1 << col] == 0) {
                        oneChangeZeros[row][col] = 1;
                        secondZero = true;
                    }
                    if (firstZero || secondZero) continue;
                    for (int r = 0; r < size; r++) {
                        for (int c = 0; c < size; c++) {
                            overlap[row][col][r][c] = 4 * functionTile[r][c] + 2 * firstChange[r][c] + secondChange[c][r];
                            if ((gate >> overlap[row][col][r][c]) % 2 != bothChanges[r][c]) {
                                gateWorks = false;
                                continue gateLoop;
                            }
                        }
                    }
                }
            }
            if (gateWorks) {
                //System.out.println("address: " + address + " gate: " + gate);
            }
        }
        //CustomArray.plusArrayDisplay(oneChangeZeros, true, true, "one change zeros relative truth table");
        System.out.println("done");
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                //CustomArray.plusArrayDisplay(overlap[row][col], true, true, "overlap relative truth table");
            }
        }
    }

    public void checkDoublesSetsFunctions() throws IOException {
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
                    int[][] firstChange = hash.m.addressToArray(4, address ^ (1 << row));
                    int[][] secondChange = hash.m.addressToArray(4, address ^ (1 << col));
                    int[][] bothChanges = hash.m.addressToArray(4, address ^ (1 << row) ^ (1 << col));
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
    }

    public void checkDoublesRand() {
        generateAbsolutelyEverything(4);
        generateRelativeToFunctionTile();
        int size = 4;
        int sizeSquare = 16;
        int[][] oneChangeZeros = new int[sizeSquare][sizeSquare];
        int[][][][] overlap = new int[sizeSquare][sizeSquare][size][size];
        int[][] functionTile = generateFunctionTile(4);
        int randGate;
        int randAddress;
        Random rand = new Random();
        for (int address = 0; address < 65536; address++) {
            //if (address % 256 == 0) System.out.println("address: " + address);
            randAddress = rand.nextInt(0, 65536);
            int[][] addressTile = hash.m.addressToArray(4, randAddress);
            gateLoop:
            for (int gate = 0; gate < 65536; gate++) {
                randGate = rand.nextInt(0, 65536);
                boolean gateWorks = true;
                for (int row = 0; row < sizeSquare; row++) {
                    for (int col = 0; col < sizeSquare; col++) {
                        int[][] firstChange = hash.m.addressToArray(4, relativeTruthTable[(1 << row)]);
                        int[][] secondChange = hash.m.addressToArray(4, relativeTruthTable[(1 << col)]);
                        int[][] bothChanges = hash.m.addressToArray(4, relativeTruthTable[(1 << row) ^ (1 << col)]);
                        boolean firstZero = false;
                        if (relativeTruthTable[1 << row] == 0) {
                            oneChangeZeros[row][col] = 1;
                            firstZero = true;
                        }
                        boolean secondZero = false;
                        if (relativeTruthTable[1 << col] == 0) {
                            oneChangeZeros[row][col] = 1;
                            secondZero = true;
                        }
                        if (firstZero || secondZero) continue;
                        for (int r = 0; r < size; r++) {
                            for (int c = 0; c < size; c++) {
                                overlap[row][col][r][c] = 8 * addressTile[r][c] + 4 * functionTile[r][c] + 2 * firstChange[r][c] + secondChange[c][r];
                                if ((randGate >> overlap[row][col][r][c]) % 2 != bothChanges[r][c]) {
                                    gateWorks = false;
                                    continue gateLoop;
                                }
                            }
                        }
                    }
                }
                if (gateWorks) {
                    System.out.println("address: " + address + " gate: " + gate);
                }
            }
        }
        //CustomArray.plusArrayDisplay(oneChangeZeros, true, true, "one change zeros relative truth table");
        System.out.println("done");
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                //CustomArray.plusArrayDisplay(overlap[row][col], true, true, "overlap relative truth table");
            }
        }
    }

    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void bitmapTransformCompleteSetOneD(String filepath, int dummy) throws IOException {
        //String filepath = "kitchenAlteredRGB.bmp";
        HashTransform hash = new HashTransform();
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int size = inImage.getWidth();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 1;
        int[][][] framesOfHashing = new int[depth][inImage.getHeight()][inImage.getWidth() * 8];
        int[][] field = new int[inImage.getHeight()][inImage.getWidth() * 8];
        int[][] bfield = new int[inImage.getHeight()][inImage.getWidth() * 16];
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight(): " + inRaster.length / inImage.getHeight());
        System.out.println("inRaster.length/inImage.getWidth(): " + inRaster.length / inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight()/inImage.getWidth(): " + inRaster.length / inImage.getHeight() / inImage.getWidth());
        //Transforms the image into its appropriate local algorithm format
//        for (int row = 0; row < inImage.getHeight(); row++) {
//            for (int column = 0; column < inImage.getWidth(); column++) {
//                for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
//                    for (int lr = 0; lr < 2; lr++) {
//                        int rasterCoordX = row * inImage.getWidth() + column;
//                        field[row][8 * column + 2 * rgbbyte + lr] = (int) Math.abs((inRaster[rasterCoordX] >> (4 * rgbbyte + 2 * lr)) % 16);
//                        for (int power = 0; power < 4; power++) {
//                            bfield[row][32 * column + 8 * rgbbyte + 4 * lr + power] = (field[row][8 * column + 2 * rgbbyte + lr] >> power) % 2;
//                        }
//                    }
//                }
//            }
//        }
        int[][][] bFieldSet = new int[16][bfield.length][bfield[0].length];
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                    for (int power = 0; power < 8; power++) {
                        bfield[row][16 * column + 8 * rgbbyte + power] = (int) Math.abs((inRaster[row * inImage.getWidth() + column] >> (8 * rgbbyte + power)) % 2);
                        for (int posNegt = 0; posNegt < 16; posNegt++) {
                            bFieldSet[posNegt][row][16 * column + 8 * rgbbyte + power] = bfield[row][16 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        int[][] flatB = new int[16][bfield.length * bfield[0].length];
        for (int t = 0; t < 16; t++) {
            for (int row = 0; row < bfield.length; row++) {
                for (int column = 0; column < bfield[0].length; column++) {
                    flatB[t][row * bfield[0].length + column] = bFieldSet[t][row][column];
                }
            }
        }
        //Initialize the minMax codeword truth table set
        hash.initWolframs();
        testAllLogic(true);
        //hashUtilities.readFromFile();
        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        //bfield = hash.initializeDepthZero(bfield, hash.unpackedList[3])[1];
        for (int t = 0; t < 8; t++) {
            //bFieldSet[t] = initializeDepthZero(bFieldSet[t], unpackedList[t])[1];
            //bFieldSet[8 + t] = initializeDepthMax(bFieldSet[8 + t], unpackedList[t])[1];
        }
        //Do the transform
        //framesOfHashing = hash.ecaTransform(bfield, hash.unpackedList[3], depth);
        int[][][] hashSet = new int[16][inImage.getHeight()][inImage.getWidth()];
        int[][] flatHashSet = new int[16][inImage.getHeight() * inImage.getWidth()];
        depth = 3;
        int gate = 8;
        boolean rowError = true;
        for (int t = 0; t < 8; t++) {
            System.out.println("t: " + t);
            hashSet[t] = hash.ecaTransform(bFieldSet[t], hash.unpackedList[t], depth,true,rowError)[depth];
            hashSet[8 + t] = hash.ecaTransform(bFieldSet[8 + t], hash.unpackedList[t], depth,true,rowError)[depth];
            //hashSet[t] = bFieldSet[t];
            //hashSet[8 + t] = bFieldSet[8 + t];
            flatHashSet[t] = hash.oneD(flatB[t], hash.unpackedList[t], 1, false, false);
            flatHashSet[t + 8] = hash.oneD(flatB[t + 8], hash.unpackedList[t], 1, false, true);
        }
        int[][] modification = generateOperation(hashSet[0].length, hashSet[0][0].length);
        int[][][] modificationTransformed = new int[16][hashSet[0].length][hashSet[0][0].length];
        int[][][] modifiedSet = new int[16][hashSet[0].length][hashSet[0][0].length];
        int[][][] internallyModifiedSet = new int[16][hashSet[0].length][hashSet[0][0].length];
        int[][] modTransFlat = new int[16][flatHashSet[0].length];
        int[][] modSetFlat = new int[16][flatHashSet[0].length];
        int[][] intModSetFlat = new int[16][flatHashSet[0].length];
        for (int t = 0; t < 8; t++) {
            System.out.println("t: " + t);
            for (int row = 0; row < modifiedSet[0].length; row++) {
                for (int column = 0; column < modifiedSet[0][0].length; column++) {
                    modifiedSet[t][row][column] = bFieldSet[t][row][column] + 2 * modification[row][column];
                    modifiedSet[8 + t][row][column] = bFieldSet[8 + t][row][column] + 2 * modification[row][column];
                    modifiedSet[t][row][column] = (gate >> modifiedSet[t][row][column]) % 2;
                    modifiedSet[8 + t][row][column] = (gate >> modifiedSet[8 + t][row][column]) % 2;
                }
            }
            for (int row = 0; row < modSetFlat[0].length; row++) {
                modSetFlat[t][row] = flatB[t][row] + 2 * modification[row / hashSet[0][0].length][row % hashSet[0][0].length];
                modSetFlat[t + 8][row] = flatB[t + 8][row] + 2 * modification[row / hashSet[0][0].length][row % hashSet[0][0].length];
            }
            modificationTransformed[t] = hash.ecaTransform(modification, hash.unpackedList[t], depth,true,true)[depth];
            modificationTransformed[8 + t] = hash.ecaTransform(modification, hash.unpackedList[t], depth,true,true)[depth];
            modifiedSet[t] = hash.ecaTransform(modifiedSet[t], hash.unpackedList[t], depth,true)[depth];
            modifiedSet[8 + t] = hash.ecaTransform(modifiedSet[8 + t], hash.unpackedList[t], depth)[depth];
            modTransFlat[t] = hash.oneD(modSetFlat[t], hash.unpackedList[t], 1, false, false);
            modTransFlat[8 + t] = hash.oneD(modSetFlat[8 + t], hash.unpackedList[t], 1, false, true);
            for (int row = 0; row < modifiedSet[0].length; row++) {
                for (int column = 0; column < modifiedSet[0][0].length; column++) {
                    int tot = 0;
                    for (int power = 0; power < 4; power++) {
                        int ab = ((hashSet[t][row][column] >> power) % 2) + 2 * ((modificationTransformed[t][row][column] >> power) % 2);
                        ab = (logicTransform[gate][t] >> ab) % 2;
                        tot += (1 << power) * ab;
                    }
                    internallyModifiedSet[t][row][column] = tot;
                    tot = 0;
                    for (int power = 0; power < 4; power++) {
                        int ab = ((hashSet[8 + t][row][column] >> power) % 2) + 2 * ((modificationTransformed[8 + t][row][column] >> power) % 2);
                        ab = (logicTransform[15 - gate][8 + t] >> ab) % 2;
                        tot += (1 << power) * ab;
                    }
                    internallyModifiedSet[8 + t][row][column] = tot;
                }
            }
            for (int row = 0; row < modSetFlat[0].length; row++) {
                int tot = 0;
                for (int power = 0; power < 4; power++) {
                    int ab = ((flatHashSet[t][row] >> power) % 2) + 2 * ((modificationTransformed[t][row / modificationTransformed[t][0].length][row % modificationTransformed[t + 8][0].length] >> power) % 2);
                    ab = (logicTransform[gate][t] >> ab) % 2;
                    tot += (1 << power) * ab;
                }
                intModSetFlat[t][row] = tot;
                tot = 0;
                for (int power = 0; power < 4; power++) {
                    int ab = ((flatHashSet[8 + t][row] >> power) % 2) + 2 * ((modificationTransformed[8 + t][row / modificationTransformed[t][0].length][row % modificationTransformed[t + 8][0].length] >> power) % 2);
                    ab = (logicTransform[15 - gate][8 + t] >> ab) % 2;
                    tot += (1 << power) * ab;
                }
                intModSetFlat[8 + t][row] = tot;
            }
        }
        int[] numDifferent = new int[16];
        int[] flatNumDifferent = new int[16];
        for (int t = 0; t < 8; t++) {
            for (int row = 0; row < modifiedSet[0].length; row++) {
                for (int column = 0; column < modifiedSet[0][0].length; column++) {
                    for (int power = 0; power < 16; power++) {
                        numDifferent[t] += (((modifiedSet[t][row][column] >> power) % 2) ^ ((internallyModifiedSet[t][row][column] >> power) % 2));
                        numDifferent[8 + t] += (((modifiedSet[8 + t][row][column] >> power) % 2) ^ ((internallyModifiedSet[8 + t][row][column] >> power) % 2));
                    }
                }
            }
        }
        for (int t = 0; t < 8; t++) {
            for (int row = 0; row < modSetFlat[0].length; row++) {
                for (int power = 0; power < 16; power++) {
                    flatNumDifferent[t] += (((modSetFlat[t][row] >> power) % 2) ^ ((intModSetFlat[t][row] >> power) % 2));
                    flatNumDifferent[8 + t] += (((modSetFlat[8 + t][row] >> power) % 2) ^ ((intModSetFlat[8 + t][row] >> power) % 2));
                }
            }
        }
        System.out.println("numDifferent: " + Arrays.toString(numDifferent));
        System.out.println("numBits: " + (inImage.getHeight() * inImage.getWidth()) * 16);
        System.out.println("flatNumDifferent: " + Arrays.toString(numDifferent));
        System.out.println("numBits: " + (inImage.getWidth() * inImage.getHeight() * 16));
    }
    public int[][] generateGatePlacesFractal(int gate, int size) {
        int[][] out = new int[size][size];
        int logSize = (int) (Math.log(size) / Math.log(2));
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int[] r = new int[logSize];
                int[] c = new int[logSize];
                int[] result = new int[logSize];
                for (int power = 0; power < logSize; power++) {
                    r[power] = (row >> power) % 2;
                    c[power] = (col >> power) % 2;
                    result[power] = r[power] + 2 * c[power];
                    result[power] = (gate >> (result[power])) % 2;
                    out[row][col] += (1 << power) * result[power];
                }
            }
        }
        return out;
    }

    /**
     * Attempts to reconstruct the original bitmap raster after doing one iteration of the hash transform
     *
     * @throws IOException
     */
    public void check() throws IOException {
        String filepath = "lion.bmp";
        File file = new File(filepath);
        BufferedImage inImage = ImageIO.read(file);
        int[] inRaster = ((DataBufferInt) inImage.getRaster().getDataBuffer()).getData();
        int[][] binaryArray = new int[inImage.getHeight()][inImage.getWidth() * 32];
        //this converts the image's 4 byte rgb code format raster into a binary array
        //this conversion is done in the column direction, so the data has 8 times the columns
        //of the input
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
        binaryArray = ecaTransform(binaryArray, unpackedList[2], 1,true,true)[1];
        //hashInverseDepth0(binaryArray, 1, unpackedList[2]);
    }
    /**
     * Takes in a 2D array of hashed data in codeword form, then rehashes sets of codewords increasingly far apart in steps of powers of 2, 1 apart 2 apart 4 apart ... 2^n apart
     *
     * @param input a 2D array of hashed data
     * @param rule  one of {0,15,51,85,170,204,240,255}
     * @param depth iterative depth, also the power of how far away its neighbors are
     * @return the input data, rehashed with neighbors 2^depth apart
     */
    public int[][] ecaMaxTransform(int[] input, int rule, int depth) {
        //initWolframs();
        int rows = input.length;
        int[][] deepInput = new int[depth + 1][rows];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            deepInput[0][row] = input[row];
        }
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //for every (row,column) location in the image
            for (int row = 0; row < rows; row++) {
                //gets its neighborhood
                int cell = 0;
                int phasePower = (int) Math.pow(2, d - 1);
                for (int r = 0; r < 4; r++) {
                    cell += (int) Math.pow(16, r) * deepInput[d - 1][(row + phasePower * r) % rows];
                }
                //stores the neighborhood's codeword
                deepInput[d][row] = (hash.m.maxSolutionsAsWolfram[rule][cell]);
            }
        }
        return deepInput;
    }
    /**
     * Takes raw binary data and does the initial conversion to one codeword per point covering its
     * area of influence, before comparing them with neighbors in ecaMinMaxTransform()
     *
     * @param input a 2D binary array
     * @param rule  an ECA rule
     * @return a set of 2D arrays with input in layer 0, and layer 1 is the codeword-ified input,
     * the rest is empty
     */
    public int[][] initializeDepthZero(int[] input, int rule) {
        int rows = input.length;
        int[][] deepInput = new int[4][rows];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            deepInput[0][row] = input[row];
        }
        //for every location in the bitmap
        for (int row = 0; row < rows; row++) {
            //gets its neighborhood
            int cell = 0;
            for (int r = 0; r < 16; r++) {
                cell += (int) Math.pow(2, r) * deepInput[0][(row + r) % rows];
            }
            //finds the neighborhood's codeword
            deepInput[1][row] = hash.m.minSolutionsAsWolfram[rule][cell];
        }
        return deepInput;
    }
    /**
     * Does the legwork of reconstituting input from sets of codewords
     * The commented out code that includes a and generatedGuess() is the original voting mechanism
     * where a codeword generates a square neighborhood to decompress the data back to original
     * What's here at the moment is the Hadamard parity, which results in the same thing with an
     * almost identical error rate. The Hadamard parity is count the number of 1s in the bits of
     * the binary codeword and take that mod 2. For some reason the Hadamard parity can be substituted
     * for the codeword's generate neighborhood. This was found by experimentation after it was discovered
     * that codeword addition results in the non-reduced ROW AND COLUMN matrix that produces the boolean
     * Hadamard matrix.
     *
     * @param in 2D codeword input array
     */
    public void reconstructDepth0(int[] in, int depth) {
        //load the minMax 8 tuple subset Wolfram codes
        hash.initWolframs();
        //Operating set
        int[][][] depthChart = new int[2][8][in.length];
        //puts the input data as layer 0 of the output data
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                System.out.println("posNeg: " + posNeg + " t: " + t);
                depthChart[posNeg][t] = initializeDepthZero(in, hash.unpackedList[t])[1];
            }
        }
        //this array is the vote tally, location is influenced by 16 neighborhoods within a distance of 4
        //each of these neighborhoods has 16 terms in the min max codeword set of the 8 tuple
        //every term of every vote is weighted by 2^RelativeRow
        int[] outVotes = new int[in.length];
        int r;
        int c;
        int t;
        int posNeg;
        int hadamardValue;
        int power;
        int row;
        int column;
        //for every location in the transformed bitmap data
        for (row = 0; row < in.length; row++) {
            System.out.println("row: " + row + " out of " + in.length);
            //for every term in its min max codeword set
            for (posNeg = 0; posNeg < 2; posNeg++) {
                for (t = 0; t < 8; t++) {
                    //apply its vote to every location that it influences
                    //including itself
                    //int[][] generatedGuess = m.generateGuess(depthChart[posNeg][t][row][column], fmt.unpackedList[t]);
                    hadamardValue = 0;
                    for (power = 0; power < 4; power++) {
                        //hadamardValue += ((depthChart[posNeg][t][row] >> power) % 2);
                        hadamardValue += ((in[row]>>power)%2);
                    }
                    hadamardValue %= 2;
                    for (r = 0; r < 16; r++) {
                        //int a = (generatedGuess[r][c] );
                        //if (generatedGuess[r][c] == posNeg) {
                        if (hadamardValue == posNeg) {
                            outVotes[(row + r) % in.length] += (1 << r);
                        } else {
                            outVotes[(row + r) % in.length] -= (1 << r);
                        }
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        int[] outResult = new int[in.length];
        int[] outCompare = new int[in.length];
        int totDifferent = 0;
        for (row = 0; row < in.length; row++) {
            if (outVotes[row] >= 0) {
                outResult[row] = 0;
            } else {
                outResult[row] = 1;
            }
            outCompare[row] = outResult[row] ^ in[row];
            totDifferent += outCompare[row];
        }
        System.out.println("totDifferent: " + totDifferent);
        System.out.println("totLength: " + (in.length));
        System.out.println("different/Area=errors/bit= " + ((double) totDifferent / (double) (in.length)));
    }

    /**
     * Does the 4 bit version of the 8 bit ECA left, right, black, white symmetries, leaving the place value instead of reducing
     *
     * @return
     */
    public int[][] lrbwCodewordTemplate() {
        int[][][] in = lrbwFourTemplate();
        int[][] out = new int[in.length][in[0].length];
        for (int row = 0; row < in.length; row++) {
            for (int lr = 0; lr < 2; lr++) {
                for (int bw = 0; bw < 2; bw++) {
                    for (int power = 0; power < 4; power++) {
                        out[row][2 * lr + bw] += (1 << in[row][2 * lr + bw][power]) * ((row >> power) % 2);
                    }
                }
            }
        }
        return out;
    }
    /**
     * Does the 4 bit version of the 8 bit ECA left-right-black-white symmetries
     *
     * @return
     */
    public int[][][] lrbwFourTemplate() {
        int[][][] out = new int[16][4][4];
        for (int row = 0; row < 16; row++) {
            for (int power = 0; power < 4; power++) {
                out[row][0][power] = row;
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
        int[][] inDec = new int[16][4];
        for (int row = 0; row < 16; row++) {
            for (int column = 0; column < 4; column++) {
                for (int power = 0; power < 4; power++) {
                    inDec[row][column] += (int) Math.pow(2, power) * out[row][column][power];
                }
            }
        }
        return out;
    }

    /**
     * Sierpinski gasket in a square matrix
     *
     * @param size size of array desired
     * @return Pascal triangle square matrix of size size
     */
    public int[][] pascalDiag(int size) {
        int[][] out = new int[size][size];
        out[0][0] = 1;
        for (int row = 0; row < size; row++) {
            out[0][row] = 1;
            out[row][0] = 1;
        }
        for (int row = 2; row < size; row++) {
            for (int col = 1; col < row; col++) {
                int a = row - col;
                int b = col;
                out[a][b] = (out[a - 1][b] + out[a][b - 1]) % 2;
            }
        }
        for (int row = 0; row < size; row++) {
            for (int col = 0; col <= row; col++) {
                out[size - 1 - row][size - 1 - col] = out[row][col];
            }
        }
        CustomArray.plusArrayDisplay(out, false, false, "pascal");
        return out;
    }

    /**
     * Sierpinski gasket on the diagonal in a square matrix
     *
     * @param size
     * @return
     */
    public int[][] pascalLR(int size) {
        int[][] out = new int[size][size];
        out[0][0] = 1;
        for (int row = 0; row < size; row++) {
            out[row][0] = 1;
            out[row][row] = 1;
            out[0][row] = 1;
        }
        for (int row = 2; row < size; row++) {
            for (int col = 1; col < row; col++) {
                int a = row;
                int b = col;
                out[a][b] = (out[a - 1][b] + out[a][b - 1]) % 2;
                out[b][a] = out[a][b];
            }
        }
        CustomArray.plusArrayDisplay(out, false, false, "pascal");
        return out;
    }

    /**
     * Sierpinksi gasket XOR Hadmard matrix
     *
     * @param size length of array
     * @return Sierpinski XOR Hadmard
     */
    public int[][] pascalXORhadamard(int size) {
        int[][] H = generateHadamardBoolean(size);
        int[][] pascal;
        pascal = pascalLR(size);
        int[][] pascalDiag = pascalDiag(size);
        int[][] out = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                out[row][col] = H[row][col] ^ pascal[col][row];
                pascalDiag[row][col] = (pascalDiag[row][col] ^ H[row][col]);
            }
        }
        CustomArray.plusArrayDisplay(out, false, false, "pascal");
        CustomArray.plusArrayDisplay(out, false, false, "pascalDiag");
        return out;
    }

    public double[][][] dftOfHadamard(int size) {
        double[][][] out = new double[size][size][2];
        int[][] H = generateHadamardBoolean(size);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                out[row][col][0] = H[row][col];
            }
        }
        out = dft(out);
        return out;
    }

    public double[][] dft(double[][] in) {
        double[][] out = new double[in.length][2];
        double coefficient = -2 * Math.PI;
        for (int row = 0; row < in.length; row++) {
            for (int column = 0; column < in.length; column++) {
                double r = in[column][0] * Math.cos((coefficient * row * column) / (double) in.length) - in[column][1] * Math.sin((coefficient * row * column) / (double) in.length);
                double c = in[column][0] * Math.sin((coefficient * row * column) / (double) in.length) + in[column][1] * Math.cos((coefficient * row * column) / (double) in.length);
                out[row][0] += r;
                out[row][1] += c;
            }
        }
        return out;
    }

    public double[][][] dft(double[][][] in) {
        double[][][] out = new double[in.length][in[0].length][2];
        double coefficient = -2 * Math.PI;
        for (int row = 0; row < in.length; row++) {
            for (int column = 0; column < in.length; column++) {
                double[] innerSum = new double[2];
                double[] outerSum = new double[2];
                for (int rr = 0; rr < in.length; rr++) {
                    innerSum = new double[2];
                    for (int cc = 0; cc < in[row].length; cc++) {
                        double r = in[row][column][0] * Math.cos((coefficient * cc * rr) / (double) in.length) - in[row][column][1] * Math.sin((coefficient * cc * rr) / (double) in.length);
                        double c = in[row][column][0] * Math.sin((coefficient * cc * rr) / (double) in.length) + in[row][column][1] * Math.cos((coefficient * cc * rr) / (double) in.length);
                        innerSum[0] += r;
                        innerSum[1] += c;
                    }
                    innerSum[0] = innerSum[0] * Math.cos((coefficient * row * column) / (double) in.length) - innerSum[1] * Math.sin((coefficient * row * column) / (double) in.length);
                    innerSum[1] = innerSum[1] * Math.sin((coefficient * row * column) / (double) in.length) + innerSum[1] * Math.cos((coefficient * row * column) / (double) in.length);
                    outerSum[0] += innerSum[0];
                    outerSum[1] += innerSum[1];
                }
                out[row][column][0] = outerSum[0];
                out[row][column][1] = outerSum[1];
            }
        }
        for (int row = 0; row < in.length; row++) {
            for (int column = 0; column < in.length; column++) {
                System.out.print(Math.round(out[row][column][0]) + " ");
            }
            System.out.print("\n");
        }
        return out;
    }
    /**
     * Does the legwork of reconstituting input from sets of codewords
     * The commented out code that includes a and generatedGuess() is the original voting mechanism
     * where a codeword generates a square neighborhood to decompress the data back to original
     * What's here at the moment is the Hadamard parity, which results in the same thing with an
     * almost identical error rate. The Hadamard parity is count the number of 1s in the bits of
     * the binary codeword and take that mod 2. For some reason the Hadamard parity can be substituted
     * for the codeword's generate neighborhood. This was found by experimentation after it was discovered
     * that codeword addition results in the non-reduced ROW AND COLUMN matrix that produces the boolean
     * Hadamard matrix.
     *
     * @param in 2D codeword input array
     */
    public void checkInverse(int[][] in) {
        //load the minMax 8 tuple subset Wolfram codes
        hash.initWolframs();
        int[][][][] depthChart = new int[2][8][in.length][in[0].length];
        //puts the input data as layer 0 of the output data
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                System.out.println("posNeg: " + posNeg + " t: " + t);
                //depthChart[posNeg][t] = hash.initializeDepthZero(in, hash.unpackedList[t])[1];
            }
        }
        //this array is the vote tally, location is influenced by 16 neighborhoods within a distance of 4
        //each of these neighborhoods has 16 terms in the min max codeword set of the 8 tuple
        //every term of every vote is weighted by 2^RelativeRow
        int[][] outVotes = new int[in.length][in[0].length];
        int r;
        int c;
        int t;
        int posNeg;
        int hadamardValue;
        int power;
        int row;
        int column;
        //for every location in the transformed bitmap data
        for (row = 0; row < in.length; row++) {
            System.out.println("row: " + row + " out of " + in.length);
            for (column = 0; column < in[0].length; column++) {
                //for every term in its min max codeword set
                for (posNeg = 0; posNeg < 2; posNeg++) {
                    for (t = 0; t < 8; t++) {
                        //apply its vote to every location that it influences
                        //including itself
                        //int[][] generatedGuess = m.generateGuess(depthChart[posNeg][t][row][column], fmt.unpackedList[t]);
                        hadamardValue = 0;
                        for (power = 0; power < 4; power++) {
                            hadamardValue += ((depthChart[posNeg][t][row][column] >> power) % 2);
                        }
                        hadamardValue %= 2;
                        for (r = 0; r < 4; r++) {
                            for (c = 0; c < 4; c++) {
                                //int a = (generatedGuess[r][c] );
                                //if (generatedGuess[r][c] == posNeg) {
                                if (hadamardValue == posNeg) {
                                    outVotes[(row + r) % in.length][(column + c) % in[0].length] += (1 << r);
                                } else {
                                    outVotes[(row + r) % in.length][(column + c) % in[0].length] -= (1 << r);
                                }
                            }
                        }
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        int[][] outResult = new int[in.length][in[0].length];
        int[][] outCompare = new int[in.length][in[0].length];
        int totDifferent = 0;
        for (row = 0; row < in.length; row++) {
            for (column = 0; column < in[0].length; column++) {
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
    /**
     * Attempts to reconstruct the original bitmap raster after doing one iteration of the hash transform
     *
     * @throws IOException
     */
    public void check() throws IOException {
        String filepath = "lion.bmp";
        File file = new File(filepath);
        BufferedImage inImage = ImageIO.read(file);
        int[] inRaster = ((DataBufferInt) inImage.getRaster().getDataBuffer()).getData();
        int[][] binaryArray = new int[inImage.getHeight()][inImage.getWidth() * 32];
        //this converts the image's 4 byte rgb code format raster into a binary array
        //this conversion is done in the column direction, so the data has 8 times the columns
        //of the input
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
    /**
     * Verifies the optional non-collision loop mentioned in the paper. This checks for collisions between all 65536 minMax 8-tuples,
     * when the codeword is wrapped with itself. Instead of just collisions between sets of codewords, its checking for collisions
     * between sets of codeword neighborhoods. The 0-65536 value that the truth table is addressed by is a binary 4x4 array.
     */
    public void checkUnitWrappedTupleUniqueness() {
        //Each of the 65536 neighborhoods are wrapped with themselves
        //The original binary neighborhood's origin is changed from simply the (0,0) codeword
        //to all (0..4,0,..4) centered codewords. That is, if the neighborhood is
        //wrapped column-wise and row-wise and the boundaries of the neighborhood are moved
        //you get the same binary array reconfigured
        //
        //
        //All the neighborhoods' wrapped addresses
        int[][] slidingAddresses = new int[65536][16];
        //All the neighborhoods' wrapped addresses' minMax codeword set values
        int[][][] slidingTuples = new int[65536][16][16];
        //Initialize the truth tables
        hash.initWolframs();
        //For every address find its wrapped neighborhood set's integer values
        for (int address = 0; address < 65536; address++) {
            //Generate the address's neighborhood array
            int[][] grid = hash.m.addressToArray(4, address);
            //For every wrapped sub-array
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    //Find it's integer value
                    int tot = 0;
                    for (int row = 0; row < 4; row++) {
                        for (int col = 0; col < 4; col++) {
                            tot += (1 << (4 * row + col)) * grid[(row + r) % 4][(col + c) % 4];
                        }
                    }
                    //Store the address
                    slidingAddresses[address][4 * r + c] = tot;
                    //Find all the subset's truth table values for that address
                    for (int posNeg = 0; posNeg < 2; posNeg++) {
                        for (int t = 0; t < 8; t++) {
                            slidingTuples[address][4 * r + c][8 * posNeg + t] = hash.flatWolframs[posNeg][t][slidingAddresses[address][4 * r + c]];
                        }
                    }
                }
            }
        }
        //Compare all addresses minMax codeword 16 tuple for uniqueness
        int numErrors = 0;
        for (int address = 0; address < 65536; address++) {
            if (address % 256 * 16 == 0) System.out.println("address: " + address / 256);
            for (int trial = 0; trial < address; trial++) {
                if (address == trial) continue;
                if (Arrays.deepEquals(slidingTuples[trial], slidingTuples[address])) {
                    numErrors++;
                    System.out.println("error: " + numErrors);
                }
            }
        }
        System.out.println("numErrors: " + numErrors);
    }

    /**
     * For all possible codewords compares the errorScore of its output tile
     * to the codeword's Hadamard parity. This is to make some kind of sense out of why substituting these two values in
     * checkInverse() result in the same reconstitution
     * @param rowError if true uses row-weighted codewords, if false uses column-weighted codewords
     */
    public void checkErrorScoreVsHadamard(boolean rowError) {
        hash.initWolframs();
        int totDifferent = 0;
        int listLayer = rowError ? 0 : 1;
        //For every 8-tuple element
        for (int posNeg = 0; posNeg < 1; posNeg++) {
            for (int t = 0; t < 8; t++) {
                //For all possible values
                for (int input = 0; input < 16; input++) {
                    //Compare a single codeword tile's voting pattern
                    //to that codeword's Hadamard parity
                    int[][] cell = hash.hashRows.generateCodewordTile(input, hash.bothLists[listLayer][t]);
                    //Do the voting
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
                    //Get the Hadamard parity
                    int totInInput = 0;
                    for (int power = 0; power < 4; power++) {
                        totInInput += ((input >> power) % 2);
                    }
                    totInInput %= 2;
                    System.out.println("totInInput: " + totInInput);
                    System.out.println("error: " + error);
                    //Get the results from the voting loops
                    int vote = 0;
                    if (error >= 0) vote = 0;
                    else vote = 1;
                    //Compare
                    totDifferent += (vote ^ totInInput);
                    System.out.println("vote: " + vote);
                }
            }
        }
        //The Hadamard parity is correlated with the voting result
        //I have no direct explanation yet, only that codeword addition is
        //the non-reduced boolean Hadamard matrix
        System.out.println("totDifferent: " + totDifferent);
        System.out.println("totDifferent = " + totDifferent + " out of 256, errors/address = " + (double) totDifferent / 256.0 + " correlation rate = (totLocations-errors)/numAddresses = " + (double) (256 - totDifferent) / 256.0);
        System.out.println("this analysis is comparing ECA minMax hash transform addition and Hadamard parity");
        System.out.println("Hadamard parity is the number of ones in its binary representation mod 2");
    }
}
