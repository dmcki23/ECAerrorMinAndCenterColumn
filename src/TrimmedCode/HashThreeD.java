package AlgorithmCode;

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

/**
 * This class is has a version of the hash that operates on arrays organized into single bits per location rather than hexadecimal
 */
public class HashThreeD {
    /**
     * Hash subroutines
     */
    public HashTruthTables hashTruthTables;
    /**
     * The entire set of min and max codewords of [0,15,51,85,170,204,240,255]
     */
    public int[][][] flatWolframs = new int[2][8][256 * 256];
    /**
     * The 8 rules referred to in the paper that have an even distribution of codewords
     * and unique codewords for every input
     */
    public int[] unpackedList = new int[]{0, 15, 51, 85, 170, 204, 240, 255};
    /**
     * Hash manager class
     */
    Hash hash;
    /**
     * Used in the inverse functions, does not do anything at the moment, was part of experimenting
     */
    int[][][] outResult;

    /**
     * Sets the manager class
     *
     * @param inHash instance of Hash, the manager class
     */
    public HashThreeD(Hash inHash) {
        //hashUtilities = in;
        hash = inHash;
        hashTruthTables = hash.hashRows;
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
    public int[][][] initializeDepthZeroTwo(int[][] input, int rule, boolean minimize, boolean rowError) {
        int rows = input.length;
        int cols = input[0].length;
        int[][][] output = new int[rows][cols][8];
        int layer = (minimize ? 0 : 1) + 2 * (rowError ? 0 : 1);
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                //output[0][row][col] = input[row][col];
            }
        }
        //This adds a dimension of redundancy to the data as a third dimension of pixel data
        int[][][] intermediate = new int[input.length][input[0].length][8];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                for (int bit = 0; bit < 16; bit++) {
                    intermediate[row][col][bit] = input[row][(col + bit) % input[0].length];
                }
            }
        }
        //for every location in the bitmap
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                for (int zee = 0; zee < 8; zee++) {
                    //gets its neighborhood
                    int cell = 0;
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            for (int z = 0; z < 2; z++) {
                                cell += (int) Math.pow(4, 4 * r + 2 * c + z) * (intermediate[(row + 2 * r) % rows][(col + 2 * c) % cols][(zee + 2 * z) % 16] + 2 * intermediate[(row + 2 * r + 1) % rows][(col + 2 * c + 1) % cols][(zee + 2 * z + 1) % 16]);
                            }
                        }
                    }
                    //finds the neighborhood's codeword
                    output[row][col][zee] = hash.allTables[layer][rule][cell];
                    //output[1][row][col] = hash.allTables[layer][rule][cell];
                }
            }
        }
        return output;
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
    public int[][][] initializeDepthZero(int[][] input, int rule, boolean minimize, boolean rowError) {
        int rows = input.length;
        int cols = input[0].length;
        int[][][] output = new int[rows][cols][4];
        int layer = (minimize ? 0 : 1) + 2 * (rowError ? 0 : 1);
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                output[0][row][col] = input[row][col];
            }
        }
        //for every location in the bitmap
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                //gets its neighborhood
                int cell = 0;
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        cell += (int) Math.pow(2, 4 * r + c) * output[0][(row + r) % rows][(col + c) % cols];
                    }
                }
                //finds the neighborhood's codeword
                //output[1][row][col] = hashTruthTables.minSolutionsAsWolfram[rule][cell];
                int shiftedCell = cell;
                for (int r = 0; r < 4; r++) {
                    output[row][col][r] = hash.allTables[layer][rule][shiftedCell];
                    int temp = shiftedCell % 16;
                    shiftedCell = shiftedCell / 16;
                    shiftedCell += temp * 16 * 16 * 16;
                }
                //output[row][col][0] = hash.allTables[layer][rule][cell];
            }
        }
        return output;
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
                deepInput[1][row][col] = hashTruthTables.maxSolutionsAsWolfram[rule][cell];
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
                deepInput[1][row][col] = hashTruthTables.maxSolutionsAsWolfram[rule][cell];
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
                    output[d][row][col] = (hashTruthTables.minSolutionsAsWolfram[rule][cell]);
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
                    output[d][row][col] = (hashTruthTables.minSolutionsAsWolfram[rule][cell]);
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
                    deepInput[d][row][col] = (hashTruthTables.maxSolutionsAsWolfram[rule][cell]);
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
    public int[][][] ecaHashHex(int[][] input, int rule, int depth, boolean minimize, boolean rowError, int[][][] heatmap) {
        //initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        int[][][] output = new int[depth + 1][rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                output[0][row][col] = input[row][col];
            }
        }
        int layer = (minimize ? 0 : 1) + 2 * (rowError ? 0 : 1);
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
                            cell += (int) Math.pow(16, 2 * r + c) * output[d - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                        }
                    }
                    //stores the neighborhood's codeword
                    output[d][row][col] = (hash.allTables[layer][rule][cell]);
                    heatmap[row][col][output[d][row][col]]++;
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
    public int[][][][] ecaHashHex(int[][][] input, int rule, int depth, boolean minimize, boolean rowError, int[][][][] heatmap) {
        //initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        int[][][][] output = new int[depth + 1][rows][cols][8];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                for (int zee = 0; zee < 8; zee++) {
                    output[0][row][col][zee] = input[row][col][zee];
                }
            }
        }
        int layer = (minimize ? 0 : 1) + 2 * (rowError ? 0 : 1);
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //for every row, column location in the image
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    for (int zee = 0; zee < 8; zee++) {
                        for (int pair = 0; pair < 2; pair++) {
                            //gets the location's neighborhood
                            int cell = 0;
                            int phasePower = (int) Math.pow(2, d - 1);
                            for (int r = 0; r < 2; r++) {
                                for (int c = 0; c < 2; c++) {
                                    for (int z = 0; z < 2; z++) {
                                        cell += (int) Math.pow(4, 4 * r + 2*c+z) * (output[d - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols][(zee+z)%8]/(1<<(2*pair))%4);

                                    }
                                }
                            }
                            //stores the neighborhood's codeword
                            output[d][row][col][zee] += (1<<(2*pair))*(hash.allTables[layer][rule][cell]%4);
                            //heatmap[row][col][output[d][row][col][zee]]++;
                        }
                    }
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
    public int[][][] ecaHash(int[][] input, int rule, int depth, boolean minimize, boolean rowError) {
        //initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        int[][][] output = new int[depth + 1][rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                output[0][row][col] = input[row][col];
            }
        }
        int listLayer = (minimize ? 0 : 1) + 2 * (rowError ? 0 : 1);
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //for every row, column location in the image
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    //gets the location's neighborhood
                    int cell = 0;
                    int phasePower = (int) Math.pow(2, d - 1);
                    for (int r = 0; r < 4; r++) {
                        for (int c = 0; c < 4; c++) {
                            cell += (int) Math.pow(2, 4 * r + c) * output[d - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                        }
                    }
                    //stores the neighborhood's codeword
                    output[d][row][col] = (hash.allTables[listLayer][rule][cell] % 2);
                }
            }
        }
        return output;
    }

    /**
     * Initializes the set of hash truth tables for [0,15,51,85,170,204,240,255]
     */
    public void initWolframs() {
        for (int r = 0; r < 8; r++) {
            hashTruthTables.individualRule(unpackedList[r], 4, false, 0, false, 0, false);
        }
        //Initialize the truth tables for both the min and max codewords of the set
        for (int spot = 0; spot < 8; spot++) {
            for (int column = 0; column < 256 * 256; column++) {
                flatWolframs[0][spot][column] = hashTruthTables.minSolutionsAsWolfram[unpackedList[spot]][column];
                flatWolframs[1][spot][column] = hashTruthTables.maxSolutionsAsWolfram[unpackedList[spot]][column];
            }
        }
    }

    /**
     * A hash inverse for a single codeword set
     *
     * @param input        a 2D array of hashed data
     * @param depth        depth of hashing of the data
     * @param ruleSetIndex which member of the set was used to hash the input (todo needs to be changed to 0-255 ECA rules)
     * @return inverted hashed data
     */
    public int[][] reconstructDepthD(int[][] input, int depth, int ruleSetIndex) {
        int neighborDistance = 1 << (depth - 1);
        //neighborDistance = 1;
        int[][][] votes = new int[input.length][input[0].length][4];
        for (int row = 0; row < input.length; row++) {
            for (int col = 0; col < input[0].length; col++) {
                //apply its vote to every location that it influences
                //including itself
                int[][] generatedGuess = hashTruthTables.generateCodewordTile(input[row][col], unpackedList[ruleSetIndex % 8]);
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        //for (int power = 0; power < 4; power++) {
//                        if (generatedGuess[r][c] == ruleSetIndex / 8) {
//                            votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * (r % 2)) % input[0].length][c] += (1 << r);
//                        } else {
//                            votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * (r % 2)) % input[0].length][c] -= (1 << r);
//                        }
                        if (generatedGuess[r][c] == ruleSetIndex / 8) {
                            votes[(row + neighborDistance * ((r) % 2)) % input.length][(col + neighborDistance * ((r / 2) % 2)) % input[0].length][c] += (1 << r);
                        } else {
                            votes[(row + neighborDistance * ((r) % 2)) % input.length][(col + neighborDistance * ((r / 2) % 2)) % input[0].length][c] -= (1 << r);
                        }
                        //}
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        int[][] outResult = new int[input.length][input[0].length];
        int[][] outCompare = new int[input.length][input[0].length];
        int totDifferent = 0;
        int[][] finalOutput = new int[input.length][input[0].length];
        for (int row = 0; row < input.length; row++) {
            for (int column = 0; column < input[0].length; column++) {
                for (int power = 0; power < 4; power++) {
                    if (votes[row][column][power] >= 0) {
                        outResult[row][column] += 0;
                        finalOutput[row][column] += 0;
                    } else {
                        outResult[row][column] += (1 << power);
                        finalOutput[row][column] += (1 << power);
                    }
                }
                //outCompare[row][column] = outResult[row][column] ^ input[row][column];
                totDifferent += outCompare[row][column];
            }
        }
//        for (int row = 0; row < input.length; row++) {
//            for (int col = 0; col < input[0].length; col++) {
//                if (finalOutput[row][col] >= 0) {
//                    //finalOutput[row][col] += 0;
//                } else {
//                    //finalOutput[row][col] = 0;
//                }
//            }
//        }
        //System.out.println("totDifferent: " + totDifferent);
        //System.out.println("totArea: " + (input.length * input[0].length));
        //System.out.println("different/Area=errors/bit= " + ((double) totDifferent / (double) (input.length * input[0].length)));
        //CustomArray.plusArrayDisplay(finalOutput, false, false, "finalOutput");
        return outResult;
    }

    /**
     * A hash inverse for a single codeword set
     *
     * @param input a 2D array of hashed data
     * @param depth depth of hashing of the data
     * @param rule  which rule of the set was used to hash the input (todo needs to be changed to 0-255 ECA rules)
     * @return inverted hashed data
     */
    public int[][] inverseHex(int[][] input, int depth, int rule, boolean minimize, boolean rowError) {
        int neighborDistance = 1 << (depth - 1);
        //neighborDistance = 1;
        int[][][] votes = new int[input.length][input[0].length][4];
        int minimizer = (minimize ? 0 : 1);
        for (int row = 0; row < input.length; row++) {
            for (int col = 0; col < input[0].length; col++) {
                //apply its vote to every location that it influences
                //including itself
                int[][] generatedGuess = hashTruthTables.generateCodewordTile(input[row][col], rule);
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        if (rowError) {
                            if (generatedGuess[r][c] == minimizer) {
                                votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] += (1 << r);
                            } else {
                                votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] -= (1 << r);
                            }
                        } else {
                            if (generatedGuess[r][c] == minimizer) {
                                votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] += (1 << c);
                            } else {
                                votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] -= (1 << c);
                            }
                        }
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        int[][] outResult = new int[input.length][input[0].length];
        for (int row = 0; row < input.length; row++) {
            for (int column = 0; column < input[0].length; column++) {
                for (int power = 0; power < 4; power++) {
                    if (votes[row][column][power] >= 0) {
                        outResult[row][column] = 0;
                    } else {
                        outResult[row][column] = 1;
                    }
                }
            }
        }
        return outResult;
    }

    /**
     * A hash inverse for a single codeword set
     *
     * @param input a 2D array of hashed data
     * @param depth depth of hashing of the data
     * @param rule  which rule of the set was used to hash the input (todo needs to be changed to 0-255 ECA rules)
     * @return inverted hashed data
     */
    public int[][] inverse(int[][] input, int depth, int rule, boolean minimize, boolean rowError) {
        int neighborDistance = 1 << (depth - 1);
        //neighborDistance = 1;
        int[][][] votes = new int[input.length][input[0].length][1];
        int minimizer = (minimize ? 0 : 1);
        for (int row = 0; row < input.length; row++) {
            for (int col = 0; col < input[0].length; col++) {
                //apply its vote to every location that it influences
                //including itself
                int[][] generatedGuess = hashTruthTables.generateCodewordTile(input[row][col], rule);
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        if (rowError) {
                            if (minimize) {
                                if (generatedGuess[r][c] == 0)
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] += (1 << r);
                                else
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] -= (1 << r);
                            } else {
                                if (generatedGuess[r][c] == 1)
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] += (1 << r);
                                else
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] -= (1 << r);
                            }
                        } else {
                            if (minimize) {
                                if (generatedGuess[r][c] == 0)
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] += (1 << c);
                                else
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] -= (1 << c);
                            } else {
                                if (generatedGuess[r][c] == 1)
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] += (1 << c);
                                else
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] -= (1 << c);
                            }
                        }
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        int[][] outResult = new int[input.length][input[0].length];
        for (int row = 0; row < input.length; row++) {
            for (int column = 0; column < input[0].length; column++) {
                for (int power = 0; power < 1; power++) {
                    if (votes[row][column][power] >= 0) {
                        outResult[row][column] += 0;
                    } else {
                        outResult[row][column] += (1 << power);
                    }
                }
            }
        }
        return outResult;
    }

    /**
     * A hash inverse for a single codeword set
     *
     * @param input a 2D array of hashed data
     * @param depth depth of hashing of the data
     * @param rule  which rule of the set was used to hash the input (todo needs to be changed to 0-255 ECA rules)
     * @return inverted hashed data
     */
    public int[][] inverseHexTest(int[][] input, int depth, int rule, boolean minimize, boolean rowError) {
        int neighborDistance = 1 << (depth - 1);
        //neighborDistance = 1;
        int[][][] votes = new int[input.length][input[0].length][16];
        int minimizer = (minimize ? 0 : 1);
        for (int row = 0; row < input.length; row++) {
            for (int col = 0; col < input[0].length; col++) {
                //apply its vote to every location that it influences
                //including itself
                int[][] generatedGuess = hashTruthTables.generateCodewordTile(input[row][col], rule);
//                for (int r = 0; r < 4 && rowError; r++) {
//                    int tot = 0;
//                    for (int c = 0; c < 4; c++){
//                        tot += (1<<c)*generatedGuess[r][c];
//                    }
//                    if (minimize) {
//
//                        votes[(row + neighborDistance * ((r/2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][tot] += (1 << r);
//
//                    } else {
//                        votes[(row + neighborDistance * ((r/2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][tot] -= (1 << r);
//
//                    }
//                }
//                for (int c = 0; c < 4 && !rowError; c++) {
//                    int tot = 0;
//                    for (int r = 0; r < 4; r++){
//                        tot += (1<<c)*generatedGuess[r][c];
//                    }
//                    if (minimize) {
//
//                        votes[(row + neighborDistance * ((r/2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][tot] += (1 << r);
//
//                    } else {
//                        votes[(row + neighborDistance * ((r/2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][tot] -= (1 << r);
//
//                    }
//                }
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        if (rowError) {
                            if (minimize) {
                                if (generatedGuess[r][c] == 0)
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] += (1 << r);
                                else
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] -= (1 << r);
                            } else {
                                if (generatedGuess[r][c] == 1)
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] += (1 << r);
                                else
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] -= (1 << r);
                            }
                        } else {
                            if (minimize) {
                                if (generatedGuess[r][c] == 0)
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] += (1 << c);
                                else
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] -= (1 << c);
                            } else {
                                if (generatedGuess[r][c] == 1)
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] += (1 << c);
                                else
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] -= (1 << c);
                            }
                        }
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        int[][] outResult = new int[input.length][input[0].length];
        for (int row = 0; row < input.length; row++) {
            for (int column = 0; column < input[0].length; column++) {
                for (int power = 0; power < 4; power++) {
                    if (votes[row][column][power] >= 0) {
                        outResult[row][column] += 0;
                    } else {
                        outResult[row][column] += (1 << power);
                    }
                }
            }
        }
        return outResult;
    }

    /**
     * Hash inversion
     *
     * @param input A set of hashed data, input[codeword][row][column] where the codeword field contains all 32 minMax row column truth tables
     * @param depth depth of hashing on the input data
     * @return inverted hashed data
     */
    public int[][] reconstructDepthD(int[][][] input, int depth) {
        int neighborDistance = 1 << (depth - 1);
        neighborDistance = 1;
        int[][][][] votes = new int[16][input[0].length][input[0][0].length][4];
        for (int row = 0; row < input[0].length; row++) {
            for (int col = 0; col < input[0][0].length; col++) {
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        //apply its vote to every location that it influences
                        //including itself
                        int[][] generatedGuess = hashTruthTables.generateCodewordTile(input[8 * posNeg + t][row][col], unpackedList[t]);
                        for (int r = 0; r < 4; r++) {
                            for (int c = 0; c < 4; c++) {
                                //for (int power = 0; power < 4; power++) {
                                if (generatedGuess[r][c] == posNeg) {
                                    votes[8 * posNeg + t][(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * (r % 2)) % input[0][0].length][c] += (1 << r);
                                } else {
                                    votes[8 * posNeg + t][(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * (r % 2)) % input[0][0].length][c] -= (1 << r);
                                }
                                //}
                            }
                        }
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        outResult = new int[16][input[0].length][input[0][0].length];
        int[][] outCompare = new int[input[0].length][input[0][0].length];
        int totDifferent = 0;
        int[][] finalOutput = new int[input[0].length][input[0][0].length];
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                for (int row = 0; row < input[0].length; row++) {
                    for (int column = 0; column < input[0][0].length; column++) {
                        for (int power = 0; power < 4; power++) {
                            if (votes[8 * posNeg + t][row][column][power] >= 0) {
                                outResult[8 * posNeg + t][row][column] += 0;
                                finalOutput[row][column] += 0;
                            } else {
                                outResult[8 * posNeg + t][row][column] += (1 << power);
                                finalOutput[row][column] += (1 << power);
                            }
                        }
                        //outCompare[row][column] = outResult[row][column] ^ input[row][column];
                        totDifferent += outCompare[row][column];
                    }
                }
            }
        }
//        for (int row = 0; row < input.length; row++) {
//            for (int col = 0; col < input[0].length; col++) {
//                if (finalOutput[row][col] >= 0) {
//                    //finalOutput[row][col] += 0;
//                } else {
//                    //finalOutput[row][col] = 0;
//                }
//            }
//        }
        //System.out.println("totDifferent: " + totDifferent);
        //System.out.println("totArea: " + (input.length * input[0].length));
        ////System.out.println("different/Area=errors/bit= " + ((double) totDifferent / (double) (input.length * input[0].length)));
        //CustomArray.plusArrayDisplay(finalOutput, false, false, "finalOutput");
        return finalOutput;
    }

    /**
     * Hash inversion
     *
     * @param input A set of hashed data, input[codeword][row][column] where the codeword field contains all 32 minMax row column truth tables
     * @param depth depth of hashing on the input data
     * @return inverted hashed data
     */
    public int[][] inverseHex(int[][][] input, int depth) {
        int neighborDistance = 1 << (depth - 1);
        //neighborDistance = 1;
        int[][][] votes = new int[input[0].length][input[0][0].length][4];
        for (int row = 0; row < input[0].length; row++) {
            for (int col = 0; col < input[0][0].length; col++) {
                for (int posNeg = 0; posNeg < 4; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        //apply its vote to every location that it influences
                        //including itself
                        int[][] generatedGuess = hashTruthTables.generateCodewordTile(input[8 * posNeg + t][row][col], hash.bothLists[(posNeg / 2) % 2][t]);
                        for (int r = 0; r < 4; r++) {
                            for (int c = 0; c < 4; c++) {
                                if (posNeg >= 0) {
                                    //for (int power = 0; power < 4; power++) {
                                    if (generatedGuess[r][c] == (posNeg % 2)) {
                                        votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][c] += (1 << r);
                                    } else {
                                        votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][c] -= (1 << r);
                                    }
                                } else {
                                    if (generatedGuess[r][c] == (posNeg % 2)) {
                                        votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][c] += (1 << c);
                                    } else {
                                        votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][c] -= (1 << c);
                                    }
                                }
                                //}
                            }
                        }
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        outResult = new int[16][input[0].length][input[0][0].length];
        int[][] finalOutput = new int[input[0].length][input[0][0].length];
        for (int posNeg = 0; posNeg < 4; posNeg++) {
            for (int t = 0; t < 8; t++) {
                for (int row = 0; row < input[0].length; row++) {
                    for (int column = 0; column < input[0][0].length; column++) {
                        for (int power = 0; power < 4; power++) {
                            if (votes[row][column][power] >= 0) {
                                finalOutput[row][column] = 0;
                            } else {
                                finalOutput[row][column] += (1 << power);
                            }
                        }
                    }
                }
            }
        }
        return finalOutput;
    }

    /**
     * Hash inversion
     *
     * @param input A set of hashed data, input[codeword][row][column] where the codeword field contains all 32 minMax row column truth tables
     * @param depth depth of hashing on the input data
     * @return inverted hashed data
     */
    public int[][] inverse(int[][][] input, int depth) {
        int neighborDistance = 1 << (depth - 1);
        //neighborDistance = 1;
        int[][][] votes = new int[input[0].length][input[0][0].length][1];
        for (int row = 0; row < input[0].length; row++) {
            for (int col = 0; col < input[0][0].length; col++) {
                for (int posNeg = 0; posNeg < 4; posNeg++) {
                    tLoop:
                    for (int t = 0; t < 8; t++) {
                        //System.out.println("betterThanHalf: " + betterThanHalf[8*posNeg+t]);
                        //apply its vote to every location that it influences
                        //including itself
                        int[][] generatedGuess = hashTruthTables.generateCodewordTile(input[8 * posNeg + t][row][col], hash.bothLists[(posNeg / 2) % 2][t]);
                        for (int r = 0; r < 4; r++) {
                            for (int c = 0; c < 4; c++) {
                                if (posNeg < 2) {
                                    if (posNeg % 2 == 0) {
                                        if (generatedGuess[r][c] == 0)
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] += (1 << r);
                                        else
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] -= (1 << r);
                                    } else {
                                        if (generatedGuess[r][c] == 1)
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] += (1 << r);
                                        else
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] -= (1 << r);
                                    }
                                } else {
                                    if (posNeg % 2 == 0) {
                                        if (generatedGuess[r][c] == 0)
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] += (1 << c);
                                        else
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] -= (1 << c);
                                    } else {
                                        if (generatedGuess[r][c] == 1)
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] += (1 << c);
                                        else
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] -= (1 << c);
                                    }
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
        outResult = new int[16][input[0].length][input[0][0].length];
        int[][] finalOutput = new int[input[0].length][input[0][0].length];
        for (int posNeg = 0; posNeg < 1; posNeg++) {
            for (int t = 0; t < 1; t++) {
                for (int row = 0; row < input[0].length; row++) {
                    for (int column = 0; column < input[0][0].length; column++) {
                        for (int power = 0; power < 1; power++) {
                            if (votes[row][column][power] >= 0) {
                                finalOutput[row][column] += 0;
                            } else {
                                finalOutput[row][column] += (1 << power);
                            }
                        }
                    }
                }
            }
        }
        return finalOutput;
    }

    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @param filepath name of the bitmap being transformed, not including the directory path
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
        //hashUtilities.readFromFile();
        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        //bfield = initializeDepthZero(bfield, unpackedList[3])[1];
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
            //bFieldSet[posNegt] = initializeDepthZero(bFieldSet[posNegt], unpackedList[posNegt])[1];
            bFieldSet[posNegt + 8] = initializeDepthMax(bFieldSet[posNegt + 8], unpackedList[posNegt])[1];
        }
        int[][] undo = hashInverseDepth0(bFieldSet, 1);
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
        undo = reconstructDepthD(framesOfHashing[1], 1, 3);
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
     * @param filepath name of the bitmap to be hashed, not including the directory structure
     * @param dummy    a dummy variable to distinguish it from the other bitmapTransform()
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
        //bfield = initializeDepthZero(bfield, unpackedList[3])[1];
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
            //bFieldSet[posNegt] = initializeDepthZero(bFieldSet[posNegt], unpackedList[posNegt])[1];
            bFieldSet[posNegt + 8] = initializeDepthMax(bFieldSet[posNegt + 8], unpackedList[posNegt])[1];
        }
        int[][] undo = hashInverseDepth0(bFieldSet, 1);
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
        undo = reconstructDepthD(framesOfHashing[1], 1, 3);
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
     * @param filepath name of the bitmap to be hashed, not including the directory path
     * @param dummy    a dummy variable to distinguish it from other hashBitmap()s
     * @throws IOException
     */
    public void hashBitmapSingleBit(String filepath, int dummy) throws IOException {
        //String filepath = "kitchenAlteredRGB.bmp";
        filepath = "src/ImagesProcessed/" + filepath;
        System.out.println("filepath: " + filepath);
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
        //initWolframs();
        //hashUtilities.readFromFile();
        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        //bfield = initializeDepthZero(bfield, unpackedList[3])[1];
        for (int t = 0; t < 8; t++) {
            //bFieldSet[t] = initializeDepthZero(bFieldSet[t], unpackedList[t])[1];
            bFieldSet[8 + t] = initializeDepthMax(bFieldSet[8 + t], unpackedList[t])[1];
        }
        //Do the transform
        framesOfHashing = ecaMinTransform(bfield, unpackedList[3], depth);
        int[][][] hashSet = new int[16][inImage.getHeight()][inImage.getWidth()];
        int[][][][] hashed = new int[16][10][inImage.getHeight()][inImage.getWidth()];
        for (int t = 0; t < 8; t++) {
            //hashSet[t] = ecaMinTransform(bFieldSet[t], unpackedList[t], depth)[1];
            //hashSet[8 + t] = ecaMaxTransform(bFieldSet[8 + t], unpackedList[t], depth)[1];
            hashSet[t] = bFieldSet[t];
            hashSet[8 + t] = bFieldSet[8 + t];
            hashSet[t] = ecaMinTransform(hashSet[t], unpackedList[t], depth)[depth];
            hashed[t] = ecaMinTransform(bFieldSet[t], unpackedList[t], depth);
            hashSet[8 + t] = ecaMaxTransform(hashSet[8 + t], unpackedList[t], depth)[depth];
            hashed[t + 8] = ecaMaxTransform(bFieldSet[t], unpackedList[t], depth);
        }
        for (int t = 0; t < 7; t++) {
            //System.out.println(Arrays.deepToString(hashed[t]));
        }
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
//        BufferedImage[] images = new BufferedImage[rasterized.length];
//        int[][] imagesRasters = new int[depth + 1][inImage.getHeight() * inImage.getWidth()];
//        ImageWriter gifWriter = ImageIO.getImageWritersByFormatName("gif").next();
//        ImageOutputStream outputStream = ImageIO.createImageOutputStream(new File(filepath + "gif.gif"));
//        gifWriter.setOutput(outputStream);
//        short[] outRaster = new short[inImage.getHeight() * inImage.getWidth()];
//        gifWriter.prepareWriteSequence(null);
//        BufferedImage outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
//        for (int repeat = 0; repeat < 1; repeat++) {
//            for (int d = 0; d <= depth; d++) {
//                File outFile = new File("src/ImagesProcessed/GifOutput/" + filepath + "iteration" + d + ".bmp");
//                outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
//                outRaster = ((DataBufferUShort) outImage.getRaster().getDataBuffer()).getData();
//                for (int index = 0; index < outRaster.length; index++) {
//                    outRaster[index] = rasterized[d][index / inImage.getWidth()][index % inImage.getWidth()];
//                }
//                ImageIO.write(outImage, "bmp", outFile);
//                IIOImage image = new IIOImage(outImage, null, null);
//                gifWriter.writeToSequence(image, null);
//            }
//        }
//        gifWriter.endWriteSequence();
//        System.out.println("depth: " + depth);
//        System.out.println("done with gif");
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
        int[][] undoSet = reconstructDepthD(hashSet, depth);
        for (int t = 0; t < 8; t++) {
            //undoSet[t] = initializeDepthZero(bFieldSet[t], unpackedList[t])[1];
        }
        int[][] undo = hashInverseDepth0(bFieldSet, depth);
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
        for (int t = 0; t < 8; t++) {
            int total = 0;
            int[][] recon = reconstructDepthD(hashed[t][depth], depth, t);
            for (int row = 0; row < recon.length; row++) {
                for (int column = 0; column < recon[0].length; column++) {
                    //total += recon[row][column] ^ hashed[t][depth-1][row][column];
                    for (int power = 0; power < 4; power++) {
                        total += ((recon[row][column] >> power) % 2) ^ ((hashed[t][depth - 1][row][column] >> power) % 2);
                    }
                }
            }
            System.out.println("total: " + total);
            System.out.println("t: " + t);
            //System.out.println(Arrays.deepToString(recon));
            //System.out.println(Arrays.deepToString(hashed[t][depth]));
            total = 0;
            recon = reconstructDepthD(hashed[t + 8][depth], depth, t + 8);
            for (int row = 0; row < recon.length; row++) {
                for (int column = 0; column < recon[0].length; column++) {
                    //total += recon[row][column] ^ hashed[t+8][depth-1][row][column];
                    for (int power = 0; power < 4; power++) {
                        total += ((recon[row][column] >> power) % 2) ^ ((hashed[t + 8][depth - 1][row][column] >> power) % 2);
                    }
                }
            }
            System.out.println("total: " + total);
            System.out.println("t: " + t);
            //System.out.println(Arrays.deepToString(recon));
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
        File inverseFile = new File(filepath + "inverse.bmp");
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
        undo = reconstructDepthD(framesOfHashing[1], depth, 3);
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
                inverseImageRaster[row * inImage.getWidth() + column] = (short) (undoRasterized[row][column]);
            }
        }
        File inverseDepth1 = new File(filepath + "inverseDepth1.bmp");
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
        binaryArray = ecaMinTransform(binaryArray, unpackedList[2], 1)[1];
        //hashInverseDepth0(binaryArray, 1, unpackedList[2]);
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
     * @param in    a set of 2D codeword input arrays, in[codewordSet][row][column] so that the first field is all 32 codewords
     * @param depth how many iterations of hashing the input has already gone through
     * @return inverted input
     */
    public int[][] hashInverseDepth0(int[][][] in, int depth) {
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
                        int[][] generatedGuess = hashTruthTables.generateCodewordTile(in[8 * posNeg + t][row][column], unpackedList[t]);
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
        return outResult;
    }

    /**
     * Flattens a 2D array into a 1D array
     *
     * @param in 2D input array
     * @return 1D version of in[][]
     */
    public int[] flatten(int[][] in) {
        int[] out = new int[in.length * in[0].length];
        for (int row = 0; row < in.length; row++) {
            for (int column = 0; column < in[0].length; column++) {
                out[row * in[0].length + column] = in[row][column];
            }
        }
        return out;
    }

    /**
     * Experimentally tests the inverse function and avalanche properties on a bitmap; this one breaks down a bitmap's RGB codes into
     * single bits instead of the hexadecimal used in verifyInverseAndAvalanche() ---- not tested uses invert() which is not written for single bits
     *
     * @param filepath name of the bitmap file, not including directory structure
     * @throws IOException
     */
    public void verifyInverseAndAvalancheSingleBit(String filepath) throws IOException {
        filepath = "src/ImagesProcessed/" + filepath;
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 1;
        boolean rowError = true;
        int listIndex = rowError ? 0 : 1;
        boolean minimize;
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        int rows = inImage.getHeight();
        int cols = inImage.getWidth() * 16;
        int[][][] bFieldSet = new int[32][rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < cols / 4; column++) {
                for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                    for (int power = 0; power < 8; power++) {
                        for (int posNegt = 0; posNegt < 32; posNegt++) {
                            bFieldSet[posNegt][row][16 * column + 8 * rgbbyte + power] = ((Math.abs(inRaster[row * cols / 16 + column]) >> (8 * rgbbyte + power)) % 16);
                        }
                    }
                }
            }
        }
        System.out.println(Arrays.deepToString(bFieldSet[0]));
        //Initialize the minMax codeword truth table set
        //initWolframs();
        //Do the transform
        int[][][] hashSet = new int[32][inImage.getHeight()][inImage.getWidth()];
        int[][][][] hashed = new int[32][depth + 1][inImage.getHeight()][inImage.getWidth()];
        int[][][] abHashSet = new int[32][inImage.getHeight()][inImage.getWidth()];
        int[][][][] abHashed = new int[32][depth + 1][inImage.getHeight()][inImage.getWidth()];
        int[][][][] initialized = new int[32][depth + 1][inImage.getHeight()][inImage.getWidth()];
        Random rand = new Random();
        int randCol = rand.nextInt(0, bFieldSet[0][0].length);
        int randRow = rand.nextInt(0, bFieldSet[0].length);
        int[][][] abbFieldSet = new int[32][bFieldSet[0].length][bFieldSet[0][0].length];
        int randNext = rand.nextInt(0, 16);
        int numChanges = 8;
        for (int t = 0; t < 32; t++) {
            for (int row = 0; row < bFieldSet[0].length; row++) {
                for (int column = 0; column < bFieldSet[0][0].length; column++) {
                    abbFieldSet[t][row][column] = bFieldSet[t][row][column];
                    //abbFieldSet[t + 8][row][column] = bFieldSet[t + 8][row][column];
                }
            }
        }
        for (int change = 0; change < numChanges; change++) {
            randCol = rand.nextInt(0, bFieldSet[0][0].length);
            randRow = rand.nextInt(0, bFieldSet[0].length);
            randNext = rand.nextInt(0, 16);
            for (int t = 0; t < 32; t++) {
                abbFieldSet[t][randRow][randCol] = randNext;
                //abbFieldSet[t + 8][randRow][randCol] = randNext;
            }
        }
        int[] avalancheDifferences = new int[32];
        System.out.println("depth: " + depth);
        for (int t = 0; t < 32; t++) {
            listIndex = (t / 16) % 2;
            rowError = (t / 16) % 2 == 0 ? true : false;
            minimize = (t / 8) % 2 == 0 ? true : false;
            hashSet[t] = hash.hashTwoDhexadecimal.hashArray(bFieldSet[t], hash.bothLists[listIndex][t % 8], depth, minimize, rowError)[depth];
            hashed[t] = hash.hashTwoDhexadecimal.hashArray(bFieldSet[t], hash.bothLists[listIndex][t % 8], depth, minimize, rowError);
            abHashed[t] = hash.hashTwoDhexadecimal.hashArray(abbFieldSet[t], hash.bothLists[listIndex][t % 8], depth, minimize, rowError);
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < rows; column++) {
                    for (int bit = 0; bit < 16; bit++) {
                        avalancheDifferences[t] += (((hashed[t][depth][row][column] >> bit) % 2) ^ ((abHashed[t][depth][row][column] >> bit) % 2));
                    }
                }
            }
        }
        System.out.println("avalancheDifferences: " + Arrays.toString(avalancheDifferences));
        for (int t = 0; t < 32; t++) {
            listIndex = (t / 16) % 2;
            rowError = (t / 16) % 2 == 0 ? true : false;
            minimize = (t / 8) % 2 == 0 ? true : false;
            int total = 0;
            int[][] recon = hash.hashTwoDhexadecimal.invert(hashSet[t], depth, hash.bothLists[listIndex][t % 8], minimize, rowError);
            System.out.println("t: " + t);
            for (int row = 0; row < recon.length; row++) {
                for (int column = 0; column < recon[0].length; column++) {
                    for (int power = 0; power < 4; power++) {
                        total += ((recon[row][column] >> power) % 2) ^ ((bFieldSet[0][row][column] >> power) % 2);
                    }
                }
            }
            System.out.println("total: " + total + " " + (double) (total) / (inRaster.length * 16));
        }
        int[][] invertedSet = hash.hashTwoDhexadecimal.invert(hashSet, depth);
        int total = 0;
        for (int row = 0; row < invertedSet.length; row++) {
            for (int col = 0; col < invertedSet[0].length; col++) {
                for (int power = 0; power < 4; power++) {
                    total += ((invertedSet[row][col] >> power) % 2) ^ ((bFieldSet[0][row][col] >> power) % 2);
                }
            }
        }
        System.out.println("overall total: " + total);
    }

    /**
     * This function experimentally tests the inverse operation and the avalanche property on a hashed bitmap
     *
     * @param filepath name of the file, not including the directory path
     * @throws IOException
     */
    public void verifyInverseAndAvalancheSingleBits(String filepath) throws IOException {
        //
        //
        //
        //
        //
        //Initialization
        filepath = "src/ImagesProcessed/" + filepath;
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int depth = (int) (Math.log(inImage.getWidth() * 16) / Math.log(2));
        if (inImage.getWidth() < inImage.getWidth()) {
            depth = (int) (Math.log(inImage.getHeight() * 16) / Math.log(2));
        }
        depth++;
        //depth = 1;
        boolean rowError = true;
        int listIndex = rowError ? 0 : 1;
        boolean minimize;
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        int rows = inImage.getHeight();
        int cols = inImage.getWidth() * 16;
        int[][][] bFieldSet = new int[32][rows][cols];
        int[][][] initial = new int[32][rows][cols];
        int[][][] veryInitial = new int[32][rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < cols / 16; column++) {
                for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                    for (int power = 0; power < 8; power++) {
                        for (int posNegt = 0; posNegt < 32; posNegt++) {
                            bFieldSet[posNegt][row][16 * column + 8 * rgbbyte + power] = ((int) (Math.abs(inRaster[row * cols / 16 + column]) >> (8 * rgbbyte + power)) % 2);
                            veryInitial[posNegt][row][column] = bFieldSet[posNegt][row][16 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        for (int t = 0; t < 32; t++) {
            for (int row = 0; row < 50; row++) {
                System.out.println(Arrays.toString(Arrays.copyOfRange(bFieldSet[t][row], 0, 50)));
            }
        }
        int numChanges = 1;
        Random rand = new Random();
        //randomly change the copy
        int[][][] abbFieldSet = new int[32][veryInitial[0].length][veryInitial[0][0].length];
        //copy the original to another array
        for (int t = 0; t < 32; t++) {
            for (int row = 0; row < bFieldSet[0].length; row++) {
                for (int column = 0; column < bFieldSet[0][0].length; column++) {
                    abbFieldSet[t][row][column] = bFieldSet[t][row][column];
                }
            }
        }
        //make a small number of changes to the copy to track the avalanche property through hash iteration depths
        for (int change = 0; change < numChanges; change++) {
            int randCol = rand.nextInt(0, bFieldSet[0][0].length);
            int randRow = rand.nextInt(0, bFieldSet[0].length);
            int randPower = rand.nextInt(0, 4);
            for (int t = 0; t < 32; t++) {
                abbFieldSet[t][randRow][randCol] ^= (1);
            }
        }
        int[][][][] bThreeD = new int[32][rows][cols][4];
        int[][][][] abThreeD = new int[32][rows][cols][4];
        for (int posNeg = 0; posNeg < 32; posNeg++) {
            minimize = (posNeg / 8) % 2 == 0 ? true : false;
            rowError = (posNeg / 16) % 2 == 0 ? true : false;
            bThreeD[posNeg] = initializeDepthZero(bFieldSet[posNeg], hash.bothLists[(posNeg / 16) % 2][posNeg % 8], minimize, rowError);
            abThreeD[posNeg] = initializeDepthZero(abbFieldSet[posNeg], hash.bothLists[(posNeg / 16) % 2][posNeg % 8], minimize, rowError);
        }
//        int[][][][] bThreeD = new int[32][rows][cols][4];
//        int[][][][] abThreeD = new int[32][rows][cols][4];
        for (int t = 0; t < 32; t++) {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    for (int word = 0; word < 4; word++) {
                        //bThreeD[t][row][col][word] = bFieldSet[t][row][col];
                    }
                }
            }
        }
        //make a copy of the initial bitmap raster breakdown for later comparison to hashed versions
        int[][][][] threeDinitial = new int[32][rows][cols][4];
        for (int t = 0; t < 32; t++) {
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < cols; column++) {
                    threeDinitial[t][row][column] = bThreeD[t][row][column];
                }
            }
        }
        for (int t = 0; t < 32; t++) {
            for (int row = 0; row < 50; row++) {
                //System.out.println(Arrays.toString(Arrays.copyOfRange(bFieldSet[t][row], 0, 50)));
            }
        }
        //
        //
        //
        //
        //
        //Hashing
        //System.out.println(Arrays.deepToString(bFieldSet[0]));
        //Initialize the minMax codeword truth table set
        int[][][][] hashSetThreeD = new int[32][rows][cols][4];
        int[][][][][] hashedThreeD = new int[32][depth + 1][rows][cols][4];
        int[][][][][] abHashedThreeD = new int[32][depth + 1][rows][cols][4];
        int[][] avalancheDifferences = new int[depth + 1][32];
        int[][][] hashSet = new int[32][inImage.getHeight()][inImage.getWidth()];
        int[][][][] hashed = new int[32][depth + 1][inImage.getHeight()][inImage.getWidth() * 16];
        int[][][] abHashSet = new int[32][inImage.getHeight()][inImage.getWidth()];
        int[][][][] abHashed = new int[32][depth + 1][inImage.getHeight()][inImage.getWidth()];
        //int[][] avalancheDifferences = new int[depth+1][32];
        System.out.println("depth: " + depth);
        int[][][][] heatmap = new int[32][inImage.getHeight()][inImage.getWidth() * 16][16];
        int[][][][] otherHeatmap = heatmap;
        int[][][][] thirdHeatmap = heatmap;
        //int[][][][][] threeDheatmap = new int[4][32][inImage.getHeight()][inImage.getWidth() * 16][4][]
        //hash every codeword set
        for (int t = 0; t < 32; t++) {
            listIndex = (t / 16) % 2;
            rowError = (t / 16) % 2 == 0 ? true : false;
            minimize = (t / 8) % 2 == 0 ? true : false;
            hashSet[t] = ecaHashHex(bFieldSet[t], hash.bothLists[listIndex][t % 8], depth, minimize, rowError, otherHeatmap[t])[depth];
            hashed[t] = ecaHashHex(bFieldSet[t], hash.bothLists[listIndex][t % 8], depth, minimize, rowError, heatmap[t]);
            abHashed[t] = ecaHashHex(abbFieldSet[t], hash.bothLists[listIndex][t % 8], depth, minimize, rowError, thirdHeatmap[t]);
            //compare the original and the hashed and display the total differences, this is the minimal avalanche property
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < rows; column++) {
                    for (int bit = 0; bit < 4; bit++) {
                        //avalancheDifferences[t] += (((hashed[t][depth][row][column] >> bit) % 2) ^ ((abHashed[t][depth][row][column] >> bit) % 2));
                    }
                }
            }
        }
        for (int t = 0; t < 32; t++) {
            System.out.println("t: " + t);
            for (int row = 0; row < 10; row++) {
                System.out.println(Arrays.toString(Arrays.copyOfRange(heatmap[t][row][0], 0, 16)));
            }
        }
        for (int t = 0; t < 32; t++) {
            System.out.println("t: " + t);
            for (int row = 0; row < 10; row++) {
                System.out.println(Arrays.toString(Arrays.copyOfRange(otherHeatmap[t][row][0], 0, 16)));
            }
        }
        for (int t = 0; t < 32; t++) {
            System.out.println("t: " + t);
            for (int row = 0; row < 10; row++) {
                System.out.println(Arrays.toString(Arrays.copyOfRange(thirdHeatmap[t][row][0], 0, 16)));
            }
        }
        //
        //
        //
        //
        //Rearrange hashes for processing
        int[][][][] hashedRearranged = new int[depth + 2][32][hashed[0].length][hashed[0][0].length];
        int tot = 0;
        for (int t = 0; t < 32; t++) {
            for (int d = 0; d <= depth; d++) {
                for (int row = 0; row < hashed[0].length; row++) {
                    for (int col = 0; col < hashed[0][0].length; col++) {
                        hashedRearranged[d][t][row][col] = hashed[t][row][col][d];
                        for (int power = 0; power < 4; power++) {
                            tot += ((hashedRearranged[d][t][row][col] >> power) % 2);
                        }
                    }
                }
            }
        }
        System.out.println("total ones: " + tot);
        //
        //
        //
        //
        //
        //Verify inverse, avalanche, and algorithm integrity
        //the first index, 0 = single bit inverse against original single bit expansion, 1 = hex inverse against initial hex initialization, 2 = hex inverse against previous depth hash
        int[][] hashSetDifferences = new int[4][depth + 2];
        int[][][] hashIndividualDifferences = new int[4][depth + 2][32];
        int[][][] ones = new int[4][depth + 2][32];
        int[][] setOnes = new int[4][depth + 2];
        int[] betterThanHalf = new int[32];
        //for every depth
        for (int d = 1; d <= depth; d++) {
            //for every codeword set
            for (int t = 0; t < 32; t++) {
                //for every cell of inverses
                //single bit version
                int[][] individualDifferences = inverse(hashedRearranged[d][t], d, hash.bothLists[(t / 16) % 2][t % 8], (t / 8) % 2 == 0 ? true : false, (t / 16) % 2 == 0 ? true : false);
                for (int row = 0; row < individualDifferences.length; row++) {
                    for (int col = 0; col < individualDifferences[0].length; col++) {
                        //sum the discrepancies
                        //sum the 1 bits to verify that the algorithm is doing anything at all
                        hashIndividualDifferences[0][d][t] += individualDifferences[row][col] ^ initial[t][row][col];
                        ones[0][d][t] += individualDifferences[row][col];
                        ones[3][d][t] += initial[t][row][col];
                    }
                }
                //hex version
                individualDifferences = inverseHex(hashedRearranged[d][t], d, hash.bothLists[(t / 16) % 2][t % 8], (t / 8) % 2 == 0 ? true : false, (t / 16) % 2 == 0 ? true : false);
                for (int row = 0; row < individualDifferences.length; row++) {
                    for (int col = 0; col < individualDifferences[0].length; col++) {
                        for (int power = 0; power < 4; power++) {
                            //sum the discrepancies
                            //sum the 1 bits to verify that the algorithm is doing anything at all
                            hashIndividualDifferences[1][d][t] += ((individualDifferences[row][col] >> power) % 2) ^ ((hashedRearranged[d - 1][t][row][col] >> power) % 2);
                            hashIndividualDifferences[2][d][t] += ((individualDifferences[row][col] >> power) % 2) ^ ((bFieldSet[t][row][col] >> power) % 2);
                            ones[1][d][t] += ((individualDifferences[row][col] >> power) % 2);
                            ones[2][d][t] += (hashedRearranged[d - 1][t][row][col] >> power) % 2;
                        }
                    }
                }
            }
            //for the single bit entire codeword set inverse
            int[][] setInverse = inverse(hashedRearranged[d], d);
            for (int row = 0; row < setInverse.length; row++) {
                for (int col = 0; col < setInverse[0].length; col++) {
                    //sum the discrepancies
                    //sum the 1 bits to verify that the algorithm is doing anything at all
                    hashSetDifferences[0][d] += setInverse[row][col] ^ initial[0][row][col];
                    setOnes[0][d] += setInverse[row][col];
                }
            }
            //for the hex entire codeword set inverse
            setInverse = inverseHex(hashedRearranged[d], d);
            for (int row = 0; row < setInverse.length; row++) {
                for (int col = 0; col < setInverse[0].length; col++) {
                    for (int power = 0; power < 4; power++) {
                        //sum the discrepancies
                        //sum the 1 bits to verify that the algorithm is doing anything at all
                        hashSetDifferences[1][d] += ((setInverse[row][col] >> power) % 2) ^ ((hashed[0][d - 1][row][col] >> power) % 2);
                        hashSetDifferences[2][d] += ((setInverse[row][col] >> power) % 2) ^ ((hashed[0][0][row][col] >> power) % 2);
                        setOnes[1][d] += ((setInverse[row][col] >> power) % 2);
                        setOnes[2][d] += ((hashed[0][d][row][col] >> power) % 2);
                    }
                }
            }
        }
        double[][][] hashIndividualDifferencesDouble = new double[4][depth + 2][32];
        double[][] hashSetDifferencesDouble = new double[4][depth + 2];
        int areaInputImage = inImage.getHeight() * inImage.getWidth();
        double[] numCodewords = new double[]{16 * areaInputImage, 64 * areaInputImage, 64 * areaInputImage, 16 * areaInputImage};
        for (int layer = 0; layer < 4; layer++) {
            for (int d = 1; d <= depth; d++) {
                for (int t = 0; t < 32; t++) {
                    hashIndividualDifferencesDouble[layer][d][t] = (double) hashIndividualDifferences[layer][d][t] / numCodewords[layer];
                }
                if (layer == 3) continue;
                hashSetDifferencesDouble[layer][d] = (double) hashSetDifferences[layer][d] / (double) numCodewords[layer];
            }
        }
        //display
        for (int d = 1; d <= depth; d++) {
            System.out.println("depth: " + d);
            System.out.println("these follow the total number of ones bits through every depth of iteration");
            System.out.println("the ones that the algorithm is doing something at all");
            System.out.println("discrepancies are total errors per depth per codeword and rate is errors/(rows*cols*(4 or 16))");
            System.out.println();
            System.out.println("this one is the single bit single codeword total vs initial");
            System.out.println("ones: " + Arrays.toString(ones[0][d]));
            System.out.println("discrepancies: " + Arrays.toString(hashIndividualDifferences[0][d]));
            System.out.println("error/bit: " + Arrays.toString(hashIndividualDifferencesDouble[0][d]));
            System.out.println();
            System.out.println("this one is the hex single codeword total ones vs last frame of hash");
            System.out.println("ones: " + Arrays.toString(ones[1][d]));
            System.out.println("discrepancies: " + Arrays.toString(hashIndividualDifferences[1][d]));
            System.out.println("error/bit: " + Arrays.toString(hashIndividualDifferencesDouble[1][d]));
            System.out.println();
            System.out.println("this one is the hex entire codeword set total ones vs initial");
            System.out.println("ones: " + Arrays.toString(ones[2][d]));
            System.out.println("discrepancies: " + Arrays.toString(hashIndividualDifferences[2][d]));
            System.out.println("error/bit: " + Arrays.toString(hashIndividualDifferencesDouble[2][d]));
            System.out.println();
            System.out.println("ones in the initial bitmap raster breakdown to one bit");
            System.out.println("ones: " + Arrays.toString(ones[3][d]));
            System.out.println();
        }
        System.out.println("these show the total errors per level of hash");
        System.out.println("complete sets");
        System.out.println("this one is single bit entire codeword versus the original");
        System.out.println("discrepancies: " + Arrays.toString(hashSetDifferences[0]));
        System.out.println("error/bit: " + Arrays.toString(hashSetDifferencesDouble[0]));
        System.out.println("setOnes[0]: " + Arrays.toString(setOnes[0]));
        System.out.println();
        System.out.println("this one is hex entire codeword versus the last frame of hashing");
        System.out.println("discrepancies: " + Arrays.toString(hashSetDifferences[1]));
        System.out.println("error/bit: " + Arrays.toString(hashSetDifferencesDouble[1]));
        System.out.println("setOnes[1]: " + Arrays.toString(setOnes[1]));
        System.out.println();
        System.out.println("this one is hex entire codeword versus the initial frame of hashing");
        System.out.println("discrepancies: " + Arrays.toString(hashSetDifferences[2]));
        System.out.println("error/bit: " + Arrays.toString(hashSetDifferencesDouble[2]));
        System.out.println("setOnes[2]: " + Arrays.toString(setOnes[2]));
        System.out.println();
        System.out.println();
        //
        //
        //
        //
        //Another set of inverse, algorithm inverse checks
        //compare the original and the hashed and display
//        System.out.println("avalancheDifferences: " + Arrays.toString(avalancheDifferences));
//        //int[] betterThanHalf = new int[32];
//        for (int t = 0; t < 32; t++) {
//            listIndex = (t / 16) % 2;
//            rowError = (t / 16) % 2 == 0 ? true : false;
//            minimize = (t / 8) % 2 == 0 ? true : false;
//            int total = 0;
//            int totOnes = 0;
//            int[][] recon = inverse(hashedRearranged[1][t], 1, hash.bothLists[listIndex][t % 8], minimize, rowError);
//            System.out.println("t: " + t);
//            for (int row = 0; row < recon.length; row++) {
//                for (int column = 0; column < recon[0].length; column++) {
//                    //total += recon[row][column] ^ hashed[t][depth-1][row][column];
//                    for (int power = 0; power < 1; power++) {
//                        total += ((recon[row][column] >> power) % 2) ^ ((veryInitial[t][row][column] >> power) % 2);
//                        totOnes += ((recon[row][column] >> power) % 2);
//                    }
//                }
//            }
//            System.out.println("total incorrect: " + total + " errors/bit: " + (double) (total) / (inRaster.length * 16));
//            if ((double) (total) / (inRaster.length * 16) > 0.5) {
//                //betterThanHalf[t]++;
//            }
//            System.out.println("total ones: " + totOnes);
//        }
//        //System.out.println(Arrays.toString(betterThanHalf));
//        int[][] invertedSet = inverse(hashedRearranged[1], 1);
//        int total = 0;
//        for (int row = 0; row < invertedSet.length; row++) {
//            for (int col = 0; col < invertedSet[0].length; col++) {
//                for (int power = 0; power < 1; power++) {
//                    total += ((invertedSet[row][col] >> power) % 2) ^ ((veryInitial[0][row][col] >> power) % 2);
//                }
//            }
//        }
//        System.out.println("overall total: " + total + " errors/bit: " + (double) (total) / (inRaster.length * 64));
//        //System.out.println(Arrays.toString(allTables[3][165]));
//        //
//        //
//        //
//        //
//        //
//        //
//        System.out.println();
//        for (int t = 0; t < 32; t++) {
//            listIndex = (t / 16) % 2;
//            rowError = (t / 16) % 2 == 0 ? true : false;
//            minimize = (t / 8) % 2 == 0 ? true : false;
//            total = 0;
//            int totOnes = 0;
//            int[][] recon = inverseHex(hashedRearranged[1][t], 1, hash.bothLists[listIndex][t % 8], minimize, rowError);
//            System.out.println("t: " + t);
//            for (int row = 0; row < recon.length; row++) {
//                for (int column = 0; column < recon[0].length; column++) {
//                    //total += recon[row][column] ^ hashed[t][depth-1][row][column];
//                    for (int power = 0; power < 4; power++) {
//                        total += ((recon[row][column] >> power) % 2) ^ ((hashedRearranged[0][t][row][column] >> power) % 2);
//                        totOnes += ((recon[row][column] >> power) % 2);
//                    }
//                }
//            }
//            System.out.println("total incorrect: " + total + " errors/bit: " + (double) (total) / (inRaster.length * 16));
//            if ((double) (total) / (inRaster.length * 16) > 0.5) {
//                //betterThanHalf[t]++;
//            }
//            System.out.println("total ones: " + totOnes);
//            System.out.println();
//        }
//        //System.out.println(Arrays.toString(betterThanHalf));
//        invertedSet = inverseHex(hashedRearranged[1], 1);
//        total = 0;
//        for (int row = 0; row < invertedSet.length; row++) {
//            for (int col = 0; col < invertedSet[0].length; col++) {
//                for (int power = 0; power < 4; power++) {
//                    total += ((invertedSet[row][col] >> power) % 2) ^ ((initial[0][row][col] >> power) % 2);
//                }
//            }
//        }
//        System.out.println("overall total: " + total + " errors/bit: " + (double) (total) / (inRaster.length * 64));
//        //System.out.println(Arrays.toString(allTables[3][165]));
    }
}




