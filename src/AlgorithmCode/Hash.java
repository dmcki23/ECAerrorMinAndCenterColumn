package AlgorithmCode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

/**
 * This is the main entry point into the algorithm's structure, manages the others
 */
public class Hash {
    /**
     * Hash subroutines
     */
    public HashTruthTables hashRows;
    /**
     * Hash subroutines
     */
    public HashTruthTables hashColumns;
    /**
     * Both hashRows and hashColumns in one array, todo maybe get rid of the original version and keep the array
     */
    public HashTruthTables[] hashRowsColumns;
    /**
     * Provides utility methods and functionalities related to hashing operations.
     * Used as a helper component within the HashTransform class.
     */
    public HashUtilities hashUtilities = new HashUtilities(this);
    /**
     * The relative logic gate transform that occurs when hashing
     */
    public HashLogicOpTransform hashLogicOpTransform = new HashLogicOpTransform(this);
    /**
     * One D version of the hash
     */
    public HashTransformOneD oneDHashTransform = new HashTransformOneD(this);
    public HashCollisions hashCollisions;
    /**
     * The entire set of min and max codewords of [0,15,51,85,170,204,240,255]
     */
    public int[][][] flatWolframs = new int[4][8][256 * 256];
    /**
     * The entire set of minMax row column codewords for all 256 ECA
     */
    public int[][][] allTables = new int[4][256][256 * 256];
    /**
     * The 8 rules referred to in the paper that have an even distribution of codewords
     * and unique codewords for every input
     */
    public int[] rowList = new int[]{0, 15, 51, 85, 170, 204, 240, 255};
    /**
     * Column-weighted ECA rules that have an even distribution and unique solutions
     */
    public int[] columnList = new int[]{0, 15, 85, 90, 165, 170, 240, 255};
    /**
     * rowList and columnList in one array
     */
    public int[][] bothLists = new int[][]{{0, 15, 51, 85, 170, 204, 240, 255}, {0, 15, 85, 90, 165, 170, 240, 255}};
    /**
     * A three-dimensional integer array used to store the output results of certain hashing
     * transformations. The exact structure and usage are determined by the implementation of
     * the methods within the class.
     * <p>
     * The outResult variable is typically populated and manipulated by methods such as
     * ecaTransform or inverse, which perform transformations and rehashing tasks based on
     * the inputs provided.
     * <p>
     * It serves as a container for data produced during the execution of the hash-related
     * algorithms in the class.
     */
    int[][][] outResult;

    public Hash() throws IOException {
        hashRows = new HashTruthTables(true, this);
        hashColumns = new HashTruthTables(false, this);
        hashRowsColumns = new HashTruthTables[]{hashRows, hashColumns};
        hashUtilities = new HashUtilities(this);
        hashLogicOpTransform = new HashLogicOpTransform(this);
        oneDHashTransform = new HashTransformOneD(this);
        hashCollisions = new HashCollisions(this);
        initWolframs();
    }

    /**
     * Takes in a 2D array of hashed data in codeword form, then rehashes sets of codewords increasingly far apart in steps of powers of 2, 1 apart 2 apart 4 apart ... 2^n apart
     *
     * @param input a 2D array of hashed data
     * @param rule  one of {0,15,51,85,170,204,240,255}
     * @param depth iterative depth, also the power of how far away its neighbors are
     * @return the input data, rehashed with neighbors 2^depth apart
     */
    public int[][][] hashArray(int[][] input, int rule, int depth, boolean minimize, boolean rowError) {
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
        int tableLayer = (minimize ? 0 : 1) + 2 * (rowError ? 0 : 1);
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
                    //output[d][row][col] = (m.minSolutionsAsWolfram[rule][cell]);
                    output[d][row][col] = allTables[tableLayer][rule][cell];
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
    public int[][] hashArrayCompression(int[][] input, int rule, int depth, boolean minimize, boolean rowError) {
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
        int[][] used = new int[rows][cols];
        int tableLayer = (minimize ? 0 : 1) + 2 * (rowError ? 0 : 1);
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //This is to skip the negative flip on the integer
            //it would not be necessary with unsigned integers
            //if (d%32 == 31) d++;
            //for every (row,column) location in the image
            for (int row = 0; row < rows / (1 << (d - 1)); row++) {
                for (int col = 0; col < cols / (1 << (d - 1)); col++) {
                    //gets its neighborhood
                    int cell = 0;
                    int phasePower = (1 << ((d - 1) % 16));
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            cell += (int) Math.pow(16, 2 * r + c) * output[d - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                            //used[(row + phasePower * r) % rows][(col + phasePower * c) % cols] = 1;
                        }
                    }
                    //stores the neighborhood's codeword
                    //output[d][row][col] = (m.minSolutionsAsWolfram[rule][cell]);
                    output[d][row][col] = allTables[tableLayer][rule][cell];
                }
            }
        }
        int[][] out = new int[rows / (1 << (depth - 1))][cols / (1 << (depth - 1))];
        for (int row = 0; row < (1 << (depth - 1)); row++) {
            for (int col = 0; col < (1 << (depth - 1)); col++) {
                out[row][col] = output[depth][row][col];
            }
        }
        return out;
    }

    /**
     * Initializes the set of hash truth tables for both row and column weighted lists
     */
    public void initWolframsFromFileTest() throws IOException {
        for (int r = 0; r < 8; r++) {
            hashRows.individualRule(rowList[r], 4, false, 0, false, 0, false);
            hashColumns.individualRule(columnList[r], 4, false, 0, false, 0, false);
            allTables[0][rowList[r]] = hashRows.minSolutionsAsWolfram[rowList[r]];
            allTables[1][rowList[r]] = hashRows.maxSolutionsAsWolfram[rowList[r]];
            allTables[2][columnList[r]] = hashColumns.minSolutionsAsWolfram[columnList[r]];
            allTables[3][columnList[r]] = hashColumns.maxSolutionsAsWolfram[columnList[r]];
        }
        //Initialize the truth tables for both the min and max codewords of the set
        for (int spot = 0; spot < 8; spot++) {
            for (int column = 0; column < 256 * 256; column++) {
                flatWolframs[0][spot][column] = hashRows.minSolutionsAsWolfram[rowList[spot]][column];
                flatWolframs[1][spot][column] = hashRows.maxSolutionsAsWolfram[rowList[spot]][column];
                flatWolframs[2][spot][column] = hashColumns.minSolutionsAsWolfram[columnList[spot]][column];
                flatWolframs[3][spot][column] = hashColumns.maxSolutionsAsWolfram[columnList[spot]][column];
            }
        }
        int[][][] compareCopy = new int[4][8][256 * 256];
        System.out.println("compareCopy.length: " + compareCopy.length);
        for (int layer = 0; layer < 4; layer++) {
            for (int element = 0; element < 8; element++) {
                for (int index = 0; index < 256 * 256; index++) {
                    compareCopy[layer][element][index] = flatWolframs[layer][element][index];
                }
                System.out.println(Arrays.toString(Arrays.copyOfRange(compareCopy[layer][element], 0, 80)));
            }
        }
        writeToFileMinMaxRowColumn();
        readFromFileMinMaxRowColumn();
        int same = 0;
        int different = 0;
        for (int layer = 0; layer < 4; layer++) {
            for (int element = 0; element < 8; element++) {
                for (int address = 0; address < 256 * 256; address++) {
                    if (compareCopy[layer][element][address] == flatWolframs[layer][element][address]) {
                        same++;
                    } else {
                        different++;
                    }
                }
            }
        }
        System.out.println("same: " + same);
        System.out.println("different: " + different);
    }

    /**
     * Initializes the set of hash truth tables for both row and column weighted lists
     */
    public void initWolframs(boolean fromFile) throws IOException {
//        hashRows = new HashTruthTables(true,this);
//        hashColumns = new HashTruthTables(false,this);
        readFromFileMinMaxRowColumn();
    }

    /**
     * Initializes the set of hash truth tables for both row and column weighted lists
     */
    public int initWolframs() {
        int[] comp = new int[65536];
        //if (!Arrays.equals(comp, flatWolframs[0][1])) return 0;
        //initWolframs(true);
        for (int r = 0; r < 8; r++) {
            hashRows.individualRule(rowList[r], 4, false, 0, false, 0, false);
            hashColumns.individualRule(columnList[r], 4, false, 0, false, 0, false);
            allTables[0][rowList[r]] = hashRows.minSolutionsAsWolfram[rowList[r]];
            allTables[1][rowList[r]] = hashRows.maxSolutionsAsWolfram[rowList[r]];
            allTables[2][columnList[r]] = hashColumns.minSolutionsAsWolfram[columnList[r]];
            allTables[3][columnList[r]] = hashColumns.maxSolutionsAsWolfram[columnList[r]];
        }
        //Initialize the truth tables for both the min and max codewords of the set
        for (int spot = 0; spot < 8; spot++) {
            for (int column = 0; column < 256 * 256; column++) {
                flatWolframs[0][spot][column] = hashRows.minSolutionsAsWolfram[rowList[spot]][column];
                flatWolframs[1][spot][column] = hashRows.maxSolutionsAsWolfram[rowList[spot]][column];
                flatWolframs[2][spot][column] = hashColumns.minSolutionsAsWolfram[columnList[spot]][column];
                flatWolframs[3][spot][column] = hashColumns.maxSolutionsAsWolfram[columnList[spot]][column];
            }
        }
        return 0;
    }

    public void readFromFileMinMaxRowColumn() throws IOException {
//        File file = new File("src/AlgorithmCode/minMaxCodewordsTest.dat");
//        FileInputStream in = new FileInputStream(file);
//        byte[] data = new byte[(int) file.length()];
//        data = in.readAllBytes();
//        System.out.println("data.length: " + data.length);
//        for (int posNeg = 0; posNeg < 2; posNeg++) {
//            for (int rowColumn = 0; rowColumn < 2; rowColumn++) {
//                for (int t = 0; t < 8; t++) {
//                    for (int index = 0; index < 65536; index++) {
//                        flatWolframs[posNeg + 2 * rowColumn][t][index] = (int) data[index];
//                        if (posNeg == 0)
//                            hashRowsColumns[rowColumn].minSolutionsAsWolfram[bothLists[rowColumn][t]][index] = flatWolframs[posNeg + 2 * rowColumn][t][index];
//                        else
//                            hashRowsColumns[rowColumn].maxSolutionsAsWolfram[bothLists[rowColumn][t]][index] = flatWolframs[posNeg + 2 * rowColumn][t][index];
//                    }
//                }
//            }
//        }
//        System.out.println();
//        for (int layer = 0; layer < 4; layer++) {
//            for (int t = 0; t < 8; t++) {
//                System.out.println(Arrays.toString(Arrays.copyOfRange(flatWolframs[layer][t], 0, 80)));
//            }
//        }
//        in.close();
//
//
//
//        FileInputStream in = new FileInputStream(file);
//        byte[] data = new byte[(int) file.length()];
//        in.read(data);
//        IntBuffer intBuf =
//                ByteBuffer.wrap(data)
//                        .order(ByteOrder.BIG_ENDIAN)
//                        .asIntBuffer();
//        int[] array = new int[intBuf.remaining()];
//        in.close();
        File file = new File("src/AlgorithmCode/minMaxCodewordsTest.dat");
        FileInputStream in = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        data = in.readAllBytes();
        //ByteBuffer buffer = ByteBuffer.wrap(data);
        int indexer = 0;
        for (int layer = 0; layer < 4; layer++) {
            for (int t = 0; t < 8; t++) {
                for (int index = 0; index < 65536; index++) {
                    flatWolframs[layer][t][index] = data[indexer];
                    indexer++;
                }
            }
        }
    }

    public void writeToFileMinMaxRowColumn() throws IOException {
        File file = new File("src/AlgorithmCode/minMaxCodewordsTest.dat");
        FileOutputStream out = new FileOutputStream(file);
        ByteBuffer buffer;
        byte[] data = new byte[65536 * 32 * 4];
        for (int layer = 0; layer < 4; layer++) {
            for (int t = 0; t < 8; t++) {
                //buffer = ByteBuffer.allocate(65536 * 4);
                for (int index = 0; index < 65536; index++) {
                    out.write((byte) flatWolframs[layer][t][index]);
                    //buffer.putInt(flatWolframs[posNeg + 2 * rowColumn][t][index]);
                }
                //out.write(buffer.array());
            }
        }
        out.close();
    }

    /**
     * Computes the inverse transformation of a 2D array of hashed data using a voting mechanism
     * derived from neighboring influences determined through specific rules and parameters.
     *
     * @param input        a 2D array of integers representing the hashed input data
     * @param depth        an integer indicating how far away neighbors are considered (powers of 2, e.g., 2^n)
     * @param ruleSetIndex the index of the rule set used to generate and evaluate votes
     * @param rowError     a boolean indicating whether row-based or column-based processing is used
     * @return a 2D array of integers representing the rehashed inverse-transformed data
     */
    public int[][] invert(int[][] input, int depth, int ruleSetIndex, boolean rowError) {
        int neighborDistance = 1 << (depth - 1);
        //neighborDistance = 1;
        int[][][] votes = new int[input.length][input[0].length][4];
        int listLayer = rowError ? 0 : 1;
        for (int row = 0; row < input.length; row++) {
            for (int col = 0; col < input[0].length; col++) {
                //apply its vote to every location that it influences
                //including itself
                int[][] generatedGuess = hashRows.generateCodewordTile(input[row][col], bothLists[listLayer][ruleSetIndex % 8]);
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        //for (int power = 0; power < 4; power++) {
                        if (generatedGuess[r][c] == ruleSetIndex / 8 && rowError) {
                            votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * (r % 2)) % input[0].length][c] += (1 << r);
                        } else {
                            votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * (r % 2)) % input[0].length][c] -= (1 << r);
                        }
                        if (generatedGuess[r][c] == ruleSetIndex / 8 && !rowError) {
                            votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * (r % 2)) % input[0].length][c] += (1 << c);
                        } else {
                            votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * (r % 2)) % input[0].length][c] -= (1 << c);
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
     * Computes the inverse transformation of a 3D array of hashed data using a voting mechanism
     * derived from neighboring influences determined through specific rules and parameters.
     *
     * @param input    a 3D array of integers representing the hashed input data
     * @param depth    an integer indicating how far away neighbors are considered (powers of 2, e.g., 2^n)
     * @param rowError a boolean indicating whether row-based or column-based processing is used
     * @return a 2D array of integers representing the rehashed inverse-transformed data
     */
    public int[][] invert(int[][][] input, int depth, boolean rowError) {
        int neighborDistance = 1 << (depth - 1);
        neighborDistance = 1;
        int listLayer = rowError ? 0 : 1;
        int[][][][] votes = new int[16][input[0].length][input[0][0].length][4];
        for (int row = 0; row < input[0].length; row++) {
            for (int col = 0; col < input[0][0].length; col++) {
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        //apply its vote to every location that it influences
                        //including itself
                        int[][] generatedGuess = hashRows.generateCodewordTile(input[8 * posNeg + t][row][col], bothLists[listLayer][t]);
                        for (int r = 0; r < 4; r++) {
                            for (int c = 0; c < 4; c++) {
                                //for (int power = 0; power < 4; power++) {
                                if (generatedGuess[r][c] == posNeg && rowError) {
                                    votes[8 * posNeg + t][(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * (r % 2)) % input[0][0].length][c] += (1 << r);
                                } else {
                                    votes[8 * posNeg + t][(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * (r % 2)) % input[0][0].length][c] -= (1 << r);
                                }
                                if (generatedGuess[r][c] == posNeg && !rowError) {
                                    votes[8 * posNeg + t][(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * (r % 2)) % input[0][0].length][c] += (1 << c);
                                } else {
                                    votes[8 * posNeg + t][(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * (r % 2)) % input[0][0].length][c] -= (1 << c);
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
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void hashBitmap(String filepath, int rule, boolean minimize, boolean rowError) throws IOException {
        filepath = "src/ImagesProcessed/" + filepath;
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 100;
        int rows = inImage.getHeight();
        int cols = inImage.getWidth() * 4;
        int[][][] framesOfHashing = new int[depth][inImage.getHeight()][inImage.getWidth() * 8];
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        int[][] bFieldSet = new int[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < cols / 4; column++) {
                for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                    for (int hex = 0; hex < 2; hex++) {
                        bFieldSet[row][4 * column + 2 * rgbbyte + hex] = ((Math.abs(inRaster[row * (cols / 4) + column]) >> (8 * rgbbyte + 4 * hex)) % 16);
                    }
                }
            }
        }
        //Initialize the minMax codeword truth table set
        initWolframs();
        //initWolframs(true);
        //hashRows.individualRule(rule,4,false,0,false,0,false);
        //Do the transform
        framesOfHashing = hashArray(bFieldSet, rule, depth, minimize, rowError);
        //Convert the transform back into appropriate bitmap RGB format
        short[][][] rasterized = new short[depth + 1][inImage.getHeight()][inImage.getWidth()];
        for (int d = 0; d <= depth; d++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                        for (int hex = 0; hex < 2; hex++) {
                            rasterized[d][row][column] += (1 << (8 * rgbbyte + 4 * hex)) * framesOfHashing[d][row][4 * column + 2 * rgbbyte + hex];
                        }
                    }
                }
            }
        }
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //This does the GIF file
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
//                //File outFile = new File(filepath + "iteration" + d + ".bmp");
//                outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
//                outRaster = ((DataBufferUShort) outImage.getRaster().getDataBuffer()).getData();
//                for (int index = 0; index < outRaster.length; index++) {
//                    outRaster[index] = rasterized[d][index / inImage.getWidth()][index % inImage.getWidth()];
//                }
//                //ImageIO.write(outImage, "bmp", outFile);
//                IIOImage image = new IIOImage(outImage, null, null);
//
//                gifWriter.writeToSequence(image, null);
//            }
//        }
//        gifWriter.endWriteSequence();
        writeGif(rasterized ,filepath + "gif.gif",depth,inImage);
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
                undoInput[3][row][column] = bFieldSet[row][column];
            }
        }
        System.out.println("undoInput[3].length: " + undoInput[0].length + " " + undoInput[1][0].length);
        int[][] undo = invert(bFieldSet, 1, minimize ? 0 : 8, rowError);
        short[][] undoRasterized = new short[inverse.getHeight()][inverse.getWidth()];
        System.out.println("inverse.getHeight(): " + inverse.getHeight() + " inverse.getWidth(): " + inverse.getWidth());
        System.out.println(undo.length + " " + undo[0].length);
        System.out.println(undoRasterized.length + " " + undoRasterized[0].length);
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inverse.getHeight(); row++) {
                for (int column = 0; column < inverse.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                        for (int hex = 0; hex < 2; hex++) {
                            undoRasterized[row][column] += undo[row][column * 4 + 2 * rgbbyte + hex] << (8 * rgbbyte + 4 * hex);
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
        undo = invert(framesOfHashing[1], 1, 3, rowError);
        undoRasterized = new short[inverse.getHeight()][inverse.getWidth()];
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inverse.getHeight(); row++) {
                for (int column = 0; column < inverse.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                        for (int hex = 0; hex < 2; hex++) {
                            undoRasterized[row][column] += undo[row][column * 4 + 2 * rgbbyte + hex] << (8 * rgbbyte + 4 * hex);
                        }
                    }
                }
            }
        }
        File inverseDepth1 = new File(filepath + "inverseDepth1.bmp");
        ImageIO.write(inverse, "bmp", inverseDepth1);
        int numDifferent = 0;
        for (int row = 0; row < inRaster.length; row++) {
            //long a = inverseImageRasterSet[row] ^ inRaster[row];
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

    public void writeGif(short[][][] frames, String filepath, int depth, BufferedImage inImage) throws IOException {
        AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
        animatedGifEncoder.start(filepath);
        animatedGifEncoder.setDelay(1000);
        BufferedImage[] outImages = new BufferedImage[depth+1];
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
        short[][] outRaster = new short[depth+1][inImage.getHeight() * inImage.getWidth()];
        for (int repeat = 0; repeat < 1; repeat++) {
            for (int d = 0; d <= depth; d++) {
                outImages[d] = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
                outRaster[d] = ((DataBufferUShort) outImages[d].getRaster().getDataBuffer()).getData();
                for (int index = 0; index < outRaster[d].length; index++) {
                    outRaster[d][index] = frames[d][index / inImage.getWidth()][index % inImage.getWidth()];
                }
                animatedGifEncoder.addFrame(outImages[d]);
            }
        }
        animatedGifEncoder.finish();
    }

    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void verifyInverseAndAvalanche(String filepath, int dummy, boolean rowError) throws IOException {
        filepath = "src/ImagesProcessed/" + filepath;
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 7;
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        int rows = inImage.getHeight();
        int cols = inImage.getWidth() * 16;
        int[][][] bFieldSet = new int[16][rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < cols / 16; column++) {
                for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                    for (int power = 0; power < 8; power++) {
                        for (int posNegt = 0; posNegt < 16; posNegt++) {
                            bFieldSet[posNegt][row][16 * column + 8 * rgbbyte + power] = ((Math.abs(inRaster[row * cols / 16 + column]) >> (8 * rgbbyte + power)) % 2);
                        }
                    }
                }
            }
        }
        //Initialize the minMax codeword truth table set
        initWolframs();
        //Do the transform
        int[][][] hashSet = new int[16][inImage.getHeight()][inImage.getWidth()];
        int[][][][] hashed = new int[16][10][inImage.getHeight()][inImage.getWidth()];
        int[][][] abHashSet = new int[16][inImage.getHeight()][inImage.getWidth()];
        int[][][][] abHashed = new int[16][10][inImage.getHeight()][inImage.getWidth()];
        Random rand = new Random();
        int randCol = rand.nextInt(0, bFieldSet[0][0].length);
        int randRow = rand.nextInt(0, bFieldSet[0].length);
        int[][][] abbFieldSet = new int[16][bFieldSet[0].length][bFieldSet[0][0].length];
        int randNext = rand.nextInt(0, 16);
        int numChanges = 8;
        for (int t = 0; t < 8; t++) {
            for (int row = 0; row < bFieldSet[0].length; row++) {
                for (int column = 0; column < bFieldSet[0][0].length; column++) {
                    abbFieldSet[t][row][column] = bFieldSet[t][row][column];
                    abbFieldSet[t + 8][row][column] = bFieldSet[t + 8][row][column];
                }
            }
            //abbFieldSet[t][randRow][randCol] = (15-abbFieldSet[t][randRow][randCol]);
            //abbFieldSet[t+8][randRow][randCol] = (15-abbFieldSet[t+8][randRow][randCol]);
            //abbFieldSet[t][randRow][randCol] = randNext;
            //abbFieldSet[t+8][randRow][randCol] = randNext;
        }
        for (int change = 0; change < numChanges; change++) {
            randCol = rand.nextInt(0, bFieldSet[0][0].length);
            randRow = rand.nextInt(0, bFieldSet[0].length);
            randNext = rand.nextInt(0, 16);
            for (int t = 0; t < 8; t++) {
                abbFieldSet[t][randRow][randCol] = randNext;
                abbFieldSet[t + 8][randRow][randCol] = randNext;
            }
        }
        int[] avalancheDifferences = new int[16];
        int listIndex = rowError ? 0 : 1;
        System.out.println("depth: " + depth);
        for (int t = 0; t < 8; t++) {
            //hashSet[t] = ecaMinTransform(bFieldSet[t], unpackedList[t], depth)[1];
            //hashSet[8 + t] = ecaMaxTransform(bFieldSet[8 + t], unpackedList[t], depth)[1];
            hashSet[t] = bFieldSet[t];
            hashSet[8 + t] = bFieldSet[8 + t];
            abHashSet[t] = abbFieldSet[t];
            abHashSet[8 + t] = abbFieldSet[8 + t];
            hashSet[t] = hashArray(hashSet[t], bothLists[listIndex][t], depth, true, rowError)[depth];
            hashed[t] = hashArray(hashSet[t], bothLists[listIndex][t], depth, true, rowError);
            hashSet[8 + t] = hashArray(hashSet[8 + t], bothLists[listIndex][t], depth, false, rowError)[depth];
            hashed[t + 8] = hashArray(hashSet[8 + t], bothLists[listIndex][t], depth, false, rowError);
            abHashed[t] = hashArray(abbFieldSet[t], bothLists[listIndex][t], depth, true, rowError);
            abHashed[8 + t] = hashArray(abbFieldSet[8 + t], bothLists[listIndex][t], depth, false, rowError);
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < rows; column++) {
                    for (int bit = 0; bit < 16; bit++) {
                        avalancheDifferences[t] += (((hashed[t][depth][row][column] >> bit) % 2) ^ ((abHashed[t][depth][row][column] >> bit) % 2));
                        avalancheDifferences[8 + t] += (((hashed[8 + t][depth][row][column] >> bit) % 2) ^ ((abHashed[8 + t][depth][row][column] >> bit) % 2));
                    }
                }
            }
        }
        System.out.println("avalancheDifferences: " + Arrays.toString(avalancheDifferences));
        for (int t = 0; t < 8; t++) {
            int total = 0;
            int[][] recon = invert(hashed[t][depth], depth, t, rowError);
            for (int row = 0; row < recon.length; row++) {
                for (int column = 0; column < recon[0].length; column++) {
                    //total += recon[row][column] ^ hashed[t][depth-1][row][column];
                    for (int power = 0; power < 4; power++) {
                        total += ((recon[row][column] >> power) % 2) ^ ((hashed[t][depth - 1][row][column] >> power) % 2);
                    }
                }
            }
            System.out.println("total: " + total);
            total = 0;
            recon = invert(hashed[t + 8][depth], depth, t + 8, rowError);
            for (int row = 0; row < recon.length; row++) {
                for (int column = 0; column < recon[0].length; column++) {
                    //total += recon[row][column] ^ hashed[t+8][depth-1][row][column];
                    for (int power = 0; power < 4; power++) {
                        total += ((recon[row][column] >> power) % 2) ^ ((hashed[t + 8][depth - 1][row][column] >> power) % 2);
                    }
                }
            }
            System.out.println("total: " + total);
        }
        for (int t = 0; t < 0; t++) {
            int total = 0;
            int[][] recon = invert(hashed[t][depth], depth, t, rowError);
            for (int row = 0; row < recon.length; row++) {
                for (int column = 0; column < recon[0].length; column++) {
                    //total += recon[row][column] ^ hashed[t][depth-1][row][column];
                    for (int power = 0; power < 4; power++) {
                        total += ((recon[row][column] >> power) % 2) ^ ((abHashed[t][depth - 1][row][column] >> power) % 2);
                    }
                }
            }
            System.out.println("total: " + total);
            total = 0;
            recon = invert(hashed[t + 8][depth], depth, t + 8, rowError);
            for (int row = 0; row < recon.length; row++) {
                for (int column = 0; column < recon[0].length; column++) {
                    //total += recon[row][column] ^ hashed[t+8][depth-1][row][column];
                    for (int power = 0; power < 4; power++) {
                        total += ((recon[row][column] >> power) % 2) ^ ((abHashed[t + 8][depth - 1][row][column] >> power) % 2);
                    }
                }
            }
            System.out.println("total: " + total);
        }
    }
}



