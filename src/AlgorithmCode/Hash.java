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
 * This is the main entry point into the algorithm's structure, manages the others as well as containing a few of the 2D hash functions
 */
public class Hash {
    /**
     * Hash subroutines, single bit processing instead of the hexadecimal used here
     */
    public HashNonHexadecimal hashNonHexadecimal;
    /**
     * Generates the codeword set truth tables as well as some minor functions, this one is set to rowError weighted errorScores
     */
    public HashTruthTables hashRows;
    /**
     * Generates the codeword set truth tables as well as some minor functions, this one is set to columnError weighted errorScores
     */
    public HashTruthTables hashColumns;
    /**
     * Both hashRows and hashColumns in one array
     */
    public HashTruthTables[] hashRowsColumns;
    /**
     * The relative logic gate transform that occurs when hashing
     */
    public HashLogicOpTransform hashLogicOpTransform = new HashLogicOpTransform(this);
    /**
     * One D version of the hash
     */
    public HashTransformOneD oneDHashTransform = new HashTransformOneD(this);
    /**
     * Generally tests the hash algorithm for collisions and uniqueness
     */
    public HashCollisions hashCollisions;
    /**
     * The entire set of 32 min and max row-column weighted codeword truth tables
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
     * Sets the helper classes and initiates generating the 32 basic codeword tables
     *
     * @throws IOException
     */
    public Hash() throws IOException {
        hashRows = new HashTruthTables(true, this);
        hashColumns = new HashTruthTables(false, this);
        hashRowsColumns = new HashTruthTables[]{hashRows, hashColumns};
        //hashUtilities = new HashUtilities(this);
        hashLogicOpTransform = new HashLogicOpTransform(this);
        oneDHashTransform = new HashTransformOneD(this);
        hashCollisions = new HashCollisions(this);
        hashNonHexadecimal = new HashNonHexadecimal(this);
        initWolframs();
    }

    /**
     * Hashes 2D data with the given parameters, hash-in-place version
     *
     * @param input    a 2D array of hashed data
     * @param rule     a 0-255 ECA rule
     * @param depth    iterative depth, also the power of how far away its neighbors are at each step
     * @param minimize if true, uses the error-minimizing codewords, and if false uses the error-maximizing codewords
     * @param rowError if true, uses the row-weighted codewords, and if false uses the column-weighted codewords
     * @return hashed input data
     */
    public int[][][] hashArray(int[][] input, int rule, int depth, boolean minimize, boolean rowError) {
        //initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        int[] comp = new int[allTables[0][0].length];
        int[][][] output = new int[depth + 1][rows][cols];
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
                    int phasePower = (1 << ((d - 1) % 24));
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
     * Hashes a 2D array of hexadecimal data with the given parameters, compression version
     *
     * @param input    a 2D array of data to be hashed
     * @param rule     a 0-255 ECA rule
     * @param depth    iterative depth
     * @param minimize if true uses the minimizing codeword set of the rule, if false uses the maximizing codewords
     * @param rowError if true uses the row-weighted errorScore truth tables, if false uses the column-weighted tables
     * @return the input data, hashed
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
     * Initializes the set of hash truth tables for both row and column weighted lists, stores it in a file and tests it (not working atm)
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
     * Initializes the set of hash truth tables for both row and column weighted lists, from the file generated in writeToFileMinMaxRowColumn()
     *
     * @param fromFile a dummy variable to distinguish it from the other initWolframs()
     */
    public void initWolframs(boolean fromFile) throws IOException {
//        hashRows = new HashTruthTables(true,this);
//        hashColumns = new HashTruthTables(false,this);
        readFromFileMinMaxRowColumn();
    }

    /**
     * Initializes the set of hash truth tables for both row and column weighted lists
     */
    public void initWolframs() {
        hashRows = new HashTruthTables(true, this);
        hashColumns = new HashTruthTables(false, this);
        hashRowsColumns = new HashTruthTables[]{hashRows, hashColumns};
        int[] comp = new int[65536];
        //if (!Arrays.equals(comp, flatWolframs[0][1])) return 0;
        //initWolframs(true);
        for (int t = 0; t < 8; t++) {
            hashRows.individualRule(rowList[t], 4, false, 0, false, 0, false);
            for (int address = 0; address < 256 * 256; address++) {
                //hashColumns.individualRule(columnList[t], 4, false, 0, false, 0, false);
                allTables[0][rowList[t]][address] = hashRows.minSolutionsAsWolfram[rowList[t]][address];
                allTables[1][rowList[t]][address] = hashRows.maxSolutionsAsWolfram[rowList[t]][address];
                //allTables[2][columnList[t]] = hashColumns.minSolutionsAsWolfram[columnList[t]];
                //allTables[3][columnList[t]] = hashColumns.maxSolutionsAsWolfram[columnList[t]];
            }
        }
        for (int t = 0; t < 8; t++) {
            hashColumns.individualRule(columnList[t], 4, false, 0, false, 0, false);
            for (int address = 0; address < 256 * 256; address++) {
                //hashRows.individualRule(rowList[t], 4, false, 0, false, 0, false);
                //allTables[0][rowList[t]] = hashRows.minSolutionsAsWolfram[rowList[t]];
                //allTables[1][rowList[t]] = hashRows.maxSolutionsAsWolfram[rowList[t]];
                allTables[2][columnList[t]][address] = hashColumns.minSolutionsAsWolfram[columnList[t]][address];
                allTables[3][columnList[t]][address] = hashColumns.maxSolutionsAsWolfram[columnList[t]][address];
            }
        }
        //Initialize the truth tables for both the min and max codewords of the set
        for (int t = 0; t < 8; t++) {
            for (int column = 0; column < 256 * 256; column++) {
                flatWolframs[0][t][column] = hashRows.minSolutionsAsWolfram[rowList[t]][column];
                flatWolframs[1][t][column] = hashRows.maxSolutionsAsWolfram[rowList[t]][column];
                flatWolframs[2][t][column] = hashColumns.minSolutionsAsWolfram[columnList[t]][column];
                flatWolframs[3][t][column] = hashColumns.maxSolutionsAsWolfram[columnList[t]][column];
            }
        }
        System.out.println("flatWolframs.length: " + flatWolframs.length);
        for (int layer = 0; layer < 4; layer++) {
            for (int t = 0; t < 8; t++) {
                System.out.println(Arrays.toString(Arrays.copyOfRange(flatWolframs[layer][t], 0, 300)));
                comp = new int[flatWolframs[layer][t].length];
                if (Arrays.equals(comp, flatWolframs[layer][t])) {
                    System.out.println("is zero");
                }
            }
        }
        for (int list = 0; list < 2; list++) {
            for (int posNeg = 0; posNeg < 2; posNeg++) {
                for (int t = 0; t < 8; t++) {
                    System.out.println(list + " " + posNeg + " " + t + " " + Arrays.toString(Arrays.copyOfRange(allTables[2 * list + posNeg][bothLists[list][t]], 0, 300)));
                }
            }
        }
    }

    /**
     * Reads the codeword set truth tables from the file generated in writeToFileMinMaxRowColumn()
     *
     * @throws IOException
     */
    public void readFromFileMinMaxRowColumn() throws IOException {
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

    /**
     * Writes the 32 codeword size 4 truth tables to file
     *
     * @throws IOException
     */
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
     * Computes the inverse transformation of a 2D array of hashed data using a voting mechanism, single codeword set
     *
     * @param input    a 2D array of integers representing the hashed input data
     * @param depth    an integer indicating how far away neighbors are considered (powers of 2, e.g., 2^n)
     * @param rule     0-255 ECA rule
     * @param minimize whether this is a minimization codeword or a maximization codeword
     * @param rowError a boolean indicating whether row-based or column-based processing is used
     * @return a 2D array of integers representing the rehashed inverse-transformed data
     */
    public int[][] invert(int[][] input, int depth, int rule, boolean minimize, boolean rowError) {
        int neighborDistance = 1 << (depth - 1);
        //neighborDistance = 1;
        int[][][] votes = new int[input.length][input[0].length][4];
        int listLayer = rowError ? 0 : 1;
        int negativeSign = minimize ? 0 : 1;
        for (int row = 0; row < input.length; row++) {
            for (int col = 0; col < input[0].length; col++) {
                //apply its vote to every location that it influences
                //including itself
                int[][] generatedGuess = hashRows.generateCodewordTile(input[row][col], rule);
                if (rowError) {
                    for (int r = 0; r < 4; r++) {
                        for (int c = 0; c < 4; c++) {
                            //for (int power = 0; power < 4; power++) {
                            if (generatedGuess[r][c] == negativeSign) {
                                votes[(row + neighborDistance * ((r) % 2)) % input.length][(col + neighborDistance * ((r / 2) % 2)) % input[0].length][c] += (1 << r);
                            } else {
                                votes[(row + neighborDistance * ((r) % 2)) % input.length][(col + neighborDistance * ((r / 2) % 2)) % input[0].length][c] -= (1 << r);
                            }
                            //}
                        }
                    }
                } else {
                    for (int r = 0; r < 4; r++) {
                        for (int c = 0; c < 4; c++) {
                            if (generatedGuess[r][c] == negativeSign) {
                                votes[(row + neighborDistance * ((r) % 2)) % input.length][(col + neighborDistance * ((r / 2) % 2)) % input[0].length][c] += (1 << c);
                            } else {
                                votes[(row + neighborDistance * ((r) % 2)) % input.length][(col + neighborDistance * ((r / 2) % 2)) % input[0].length][c] -= (1 << c);
                            }
                            //}
                        }
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
        return outResult;
    }

    /**
     * Computes the inverse of a 2D array of hashed data using a voting mechanism using all 32 codewords
     *
     * @param input 32 codeword sets of 2D hashed data, input[codeword][row][column]
     * @param depth an integer indicating how far away neighbors are considered (powers of 2, e.g., 2^n)
     * @return a 2D array of integers representing the rehashed inverse-transformed data
     */
    public int[][] invert(int[][][] input, int depth) {
        int neighborDistance = (int) Math.pow(2, depth - 1);
        //neighborDistance = 1;
        int listLayer;
        boolean rowError = false;
        int[][][][] votes = new int[32][input[0].length][input[0][0].length][4];
        int[][][] altVotes = new int[input[0].length][input[0][0].length][4];
        for (int row = 0; row < input[0].length; row++) {
            for (int col = 0; col < input[0][0].length; col++) {
                //for (int posNeg = 0; posNeg < 2; posNeg++) {
                for (int t = 0; t < 16; t++) {
                    int posNeg = (t / 8) % 2;
                    listLayer = (t / 16) % 2;
                    rowError = (t / 16) % 2 == 0 ? true : false;
                    //apply its vote to every location that it influences
                    //including itself
                    int[][] generatedGuess = hashRows.generateCodewordTile(input[t][row][col], bothLists[listLayer][t % 8]);
                    if (rowError) {
                        for (int r = 0; r < 4; r++) {
                            for (int c = 0; c < 4; c++) {
                                //for (int power = 0; power < 4; power++) {
                                if (generatedGuess[r][c] == posNeg) {
                                    votes[t][(row + neighborDistance * ((r) % 2)) % input[0].length][(col + neighborDistance * ((r / 2) % 2)) % input[0][0].length][c] += (1 << r);
                                    altVotes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r / 2) % 2)) % input[0][0].length][c] += (1 << r);
                                } else {
                                    votes[t][(row + neighborDistance * ((r) % 2)) % input[0].length][(col + neighborDistance * ((r / 2) % 2)) % input[0][0].length][c] -= (1 << r);
                                    altVotes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][c] -= (1 << r);
                                }
                                //}
                            }
                        }
                    } else {
                        for (int r = 0; r < 4; r++) {
                            for (int c = 0; c < 4; c++) {
                                //for (int power = 0; power < 4; power++) {
                                if (generatedGuess[r][c] == posNeg) {
                                    votes[t][(row + neighborDistance * ((r) % 2)) % input[0].length][(col + neighborDistance * ((r / 2) % 2)) % input[0][0].length][c] += (1 << c);
                                    altVotes[(row + neighborDistance * ((r) % 2)) % input[0].length][(col + neighborDistance * ((r / 2) % 2)) % input[0][0].length][c] += (1 << c);
                                } else {
                                    votes[t][(row + neighborDistance * ((r) % 2)) % input[0].length][(col + neighborDistance * ((r / 2) % 2)) % input[0][0].length][c] -= (1 << c);
                                    altVotes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][c] -= (1 << c);
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
        //outResult = new int[16][input[0].length][input[0][0].length];
        int[][] outCompare = new int[input[0].length][input[0][0].length];
        int totDifferent = 0;
        int[][] finalOutput = new int[input[0].length][input[0][0].length];
        int[][][] intermediateVote = new int[input[0].length][input[0][0].length][4];
        for (int t = 0; t < 32; t++) {
            for (int row = 0; row < input[0].length; row++) {
                for (int column = 0; column < input[0][0].length; column++) {
                    for (int power = 0; power < 4; power++) {
                        if (votes[t][row][column][power] >= 0) {
                            intermediateVote[row][column][power] += 0;
                            //outResult[t][row][column] += 0;
                            finalOutput[row][column] += 0;
                        } else {
                            intermediateVote[row][column][power] += (1 << power);
                            //outResult[t][row][column] += (1 << power);
                            finalOutput[row][column] += (1 << power);
                        }
                    }
                    //outCompare[row][column] = outResult[row][column] ^ input[row][column];
                    totDifferent += outCompare[row][column];
                }
            }
        }
        for (int row = 0; row < input[0].length; row++) {
            for (int col = 0; col < input[0][0].length; col++) {
                for (int power = 0; power < 4; power++) {
                    if (intermediateVote[row][col][power] >= 0) {
                        finalOutput[row][col] += 0;
                    } else {
                        finalOutput[row][col] += (1 << power);
                    }
                }
            }
        }
        finalOutput = new int[input[0].length][input[0][0].length];
        for (int row = 0; row < input[0].length; row++) {
            for (int column = 0; column < input[0][0].length; column++) {
                for (int power = 0; power < 4; power++) {
                    if (altVotes[row][column][power] >= 0) {
                        finalOutput[row][column] += 0;
                    } else {
                        finalOutput[row][column] += (1 << power);
                    }
                }
            }
        }
        return finalOutput;
    }

    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file using the given parameters
     *
     * @param filepath name of the file, not including the directory path
     * @param rule     0-255 ECA rule to use
     * @param minimize if true, uses the minimizing codewords, if false uses the maximizing codewords
     * @param rowError if true, uses the row-weighted errorScore, if false uses the column-weighted errorScore
     * @throws IOException
     */
    public void hashBitmap(String filepath, int rule, boolean minimize, boolean rowError) throws IOException {
        filepath = "src/ImagesProcessed/" + filepath;
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 10;
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
        //Do the transform
        framesOfHashing = hashArray(bFieldSet, rule, depth, minimize, rowError);
        //Convert the transform back into appropriate bitmap RGB format
        short[][][] rasterized = new short[depth + 1][inImage.getHeight()][inImage.getWidth()];
        BufferedImage outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
        short[] outRaster = ((DataBufferUShort) outImage.getRaster().getDataBuffer()).getData();
        File frameFile = new File(filepath + "resized.bmp");
        ImageIO.write(inImage, "bmp", frameFile);
        for (int d = 0; d <= depth; d++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                        for (int hex = 0; hex < 2; hex++) {
                            rasterized[d][row][column] += (1 << (8 * rgbbyte + 4 * hex)) * framesOfHashing[d][row][4 * column + 2 * rgbbyte + hex];
                            outRaster[row * cols / 4 + column] = (short) rasterized[d][row][column];
                        }
                    }
                }
            }
            frameFile = new File(filepath + "frames" + (d + 1) + ".bmp");
            ImageIO.write(outImage, "bmp", frameFile);
        }
        writeGif(rasterized, filepath + "gif.gif", depth, inImage);
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
        int[][] undo = invert(bFieldSet, 1, rule, minimize, rowError);
        short[][] undoRasterized = new short[inverse.getHeight()][inverse.getWidth()];
        System.out.println("inverse.getHeight(): " + inverse.getHeight() + " inverse.getWidth(): " + inverse.getWidth());
        System.out.println(undo.length + " " + undo[0].length);
        System.out.println(undoRasterized.length + " " + undoRasterized[0].length);
        for (int d = 0; d <= 1; d++) {
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
        undo = invert(framesOfHashing[1], 1, rule, minimize, rowError);
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

    /**
     * Generates a gif from hashed data in hashBitmap(), uses AnimatedGifEncoder
     *
     * @param frames   hashed data from hashBitmap(), frames[a][row][column] where frames[a] is a single frame of the gif
     * @param filepath name and location of where to put the gif
     * @param depth    depth of iteration, how many frames
     * @param inImage  passed as a parameter mostly for the inImage.getHeight() and .getWidth() information
     * @throws IOException
     */
    public void writeGif(short[][][] frames, String filepath, int depth, BufferedImage inImage) throws IOException {
        AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
        animatedGifEncoder.start(filepath);
        animatedGifEncoder.setDelay(1000);
        BufferedImage[] outImages = new BufferedImage[depth + 1];
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
        short[][] outRaster = new short[depth + 1][inImage.getHeight() * inImage.getWidth()];
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
     * This function experimentally tests the inverse operation and the avalanche property on a hashed bitmap
     *
     * @param filepath name of the file, not including the directory path
     * @throws IOException
     */
    public void verifyInverseAndAvalanche(String filepath) throws IOException {
        filepath = "src/ImagesProcessed/" + filepath;
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int depth = (int) (Math.log(inImage.getWidth() * 4) / Math.log(2));
        if (inImage.getWidth() < inImage.getWidth()) {
            depth = (int) (Math.log(inImage.getHeight() * 4) / Math.log(2));
        }
        //depth = 1;
        boolean rowError = true;
        int listIndex = rowError ? 0 : 1;
        boolean minimize;
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        int rows = inImage.getHeight();
        int cols = inImage.getWidth() * 4;
        int[][][] bFieldSet = new int[32][rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < cols / 4; column++) {
                for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                    for (int power = 0; power < 2; power++) {
                        for (int posNegt = 0; posNegt < 32; posNegt++) {
                            bFieldSet[posNegt][row][4 * column + 2 * rgbbyte + power] = ((Math.abs(inRaster[row * cols / 4 + column]) >> (8 * rgbbyte + 4 * power)) % 16);
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
                }
            }
        }
        for (int change = 0; change < numChanges; change++) {
            randCol = rand.nextInt(0, bFieldSet[0][0].length);
            randRow = rand.nextInt(0, bFieldSet[0].length);
            randNext = rand.nextInt(0, 16);
            for (int t = 0; t < 32; t++) {
                abbFieldSet[t][randRow][randCol] = randNext;
            }
        }
        int[] avalancheDifferences = new int[32];
        System.out.println("depth: " + depth);
        for (int t = 0; t < 32; t++) {
            listIndex = (t / 16) % 2;
            rowError = (t / 16) % 2 == 0 ? true : false;
            minimize = (t / 8) % 2 == 0 ? true : false;
            hashSet[t] = hashArray(bFieldSet[t], bothLists[listIndex][t % 8], depth, minimize, rowError)[depth];
            hashed[t] = hashArray(bFieldSet[t], bothLists[listIndex][t % 8], depth, minimize, rowError);
            abHashed[t] = hashArray(abbFieldSet[t], bothLists[listIndex][t % 8], depth, minimize, rowError);
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
            int[][] recon = invert(hashSet[t], depth, bothLists[listIndex][t % 8], minimize, rowError);
            System.out.println("t: " + t);
            //System.out.println(Arrays.deepToString(hashSet[t]));
            //System.out.println(Arrays.deepToString(recon));
            //System.out.println(Arrays.deepToString(bFieldSet[t]));
            for (int row = 0; row < recon.length; row++) {
                for (int column = 0; column < recon[0].length; column++) {
                    //total += recon[row][column] ^ hashed[t][depth-1][row][column];
                    for (int power = 0; power < 4; power++) {
                        total += ((recon[row][column] >> power) % 2) ^ ((bFieldSet[0][row][column] >> power) % 2);
                    }
                }
            }
            System.out.println("total: " + total + " " + (double) (total) / (inRaster.length * 16));
        }
        int[][] invertedSet = invert(hashSet, depth);
        int total = 0;
        for (int row = 0; row < invertedSet.length; row++) {
            for (int col = 0; col < invertedSet[0].length; col++) {
                for (int power = 0; power < 4; power++) {
                    total += ((invertedSet[row][col] >> power) % 2) ^ ((bFieldSet[0][row][col] >> power) % 2);
                }
            }
        }
        System.out.println("overall total: " + total);
        //System.out.println(Arrays.toString(allTables[3][165]));
    }
}



