package AlgorithmCode;

import CustomLibrary.CustomArray;
import CustomLibrary.PermutationsFactoradic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Produces and verifies truth tables of logic gate transformation when hashed. If you take a codeword-produced 4x4 tile and combine it with another codeword-produced 4x4 tile
 * via a bitwise 0-15 logic gate operation and rehash the tile, the logic operation between the original codewords and the result codeword becomes transformed into another logic operation.
 * AND is 8, OR is 14.
 * <p>
 * A logic operation between two inputs pre-hash becomes a different logic operation within the hash. Using these truth tables, you can operate on hashed input without lossy inversion
 * and without the original data, allowing for retroactive hashing
 * <p>
 * This class generates and verifies these operation transformations for all 0-15 logic gates and all 16 row-weighted hashes, the column-weighted hashes have an incomplete set of these transformations.
 */
public class HashLogicOpTransform {
    /**
     * Manager class
     */
    Hash hash;
    /**
     * Truth table of generated logic op transformations
     */
    int[][] logicTransformRowError = new int[16][16];
    int[][] logicTransformColumnError = new int[16][16];
    int[][][] logicTransformBoth = new int[][][]{logicTransformRowError, logicTransformColumnError};
    int[] comp = new int[16];
    int[] ones = new int[]{1, 2, 4, 8};
    int index = 0;
    int[] gateTest = new int[4];
    int g = 0;
    int[][] twoByTwoComp = new int[2][4];

    /**
     * Loads the manager class
     *
     * @param inHash manager class
     */
    public HashLogicOpTransform(Hash inHash) {
        hash = inHash;
    }

    /**
     * This function tests a single 0-15 logic gate against all 16 minMax codeword sets rowError or columnError. This is a helper function for testAllGates()
     *
     * @param gate        an integer representing a 0-15 logic gate, 8 = AND, 14 = OR, 6 = XOR
     * @param tupleDistro the output array passed from testAllGates()
     * @param inRowError  if true uses the row-weighted truth tables; if false uses the column-weighted set
     */
    public void testGate(int gate, int[][] tupleDistro, boolean inRowError) {
        int listLayer = inRowError ? 0 : 1;
        int[][][][] additionTables = new int[2][8][16][16];
        //generate addition table for every (a,b) for every minMax codeword set
        for (int a = 0; a < 16; a++) {
            for (int b = 0; b < 16; b++) {
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        //generate the neighborhoods of a and b
                        int[][] aa = hash.hashRows.generateCodewordTile(a, hash.bothLists[listLayer][t]);
                        int[][] bb = hash.hashColumns.generateCodewordTile(b, hash.bothLists[listLayer][t]);
                        int[][] cc = new int[4][4];
                        //combine the neighborhoods with the given logic gate
                        for (int row = 0; row < 4; row++) {
                            for (int col = 0; col < 4; col++) {
                                cc[row][col] = (gate >> (aa[row][col] + 2 * bb[row][col])) % 2;
                            }
                        }
                        //find the codeword of the combined neighborhood
                        int[][] ccc = hash.hashRowsColumns[listLayer].findMinimizingCodeword(hash.bothLists[listLayer][t], cc);
                        int result = 0;
                        for (int column = 0; column < 4; column++) {
                            result += (int) Math.pow(2, column) * ccc[0][column];
                        }
                        if (posNeg == 0 ){
                            additionTables[posNeg][t][a][b] = hash.hashRowsColumns[inRowError == true ? 0 : 1].lastMinCodeword;
                        } else {
                            additionTables[posNeg][t][a][b] = hash.hashRowsColumns[inRowError == true ? 0 : 1].lastMaxCodeword;
                        }
                        //store it in the table
//                        if (posNeg == 1)
//                            additionTables[posNeg][t][a][b] = hash.hashRowsColumns[listLayer].lastMaxCodeword;
//                        else additionTables[posNeg][t][a][b] = hash.hashRowsColumns[listLayer].lastMinCodeword;
                    }
                }
            }
        }
        int[][][] attemptedLogicTransform = new int[16][16][4];
        for (int g = 0; g < 16; g++) {
            for (int gg = 0; gg < 16; gg++) {
                Arrays.fill(attemptedLogicTransform[g][gg], -1);
            }
        }
        int[][] distro = new int[16][16];
        //tests the attempted logic transform gate and rejects those that aren't valid
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                Arrays.fill(distro[8 * posNeg + t], -1);
                //for all gates, for all codewords
                testGateLoop:
                for (int g = 0; g < 16; g++) {
                    for (int a = 0; a < 16; a++) {
                        for (int b = 0; b < 16; b++) {
                            for (int power = 0; power < 4; power++) {
                                int ab = ((a >> power) % 2) + 2 * ((b >> power) % 2);
                                int c = (additionTables[posNeg][t][a][b] >> power) % 2;
                                if ((g >> ab) % 2 != c) {
                                    continue testGateLoop;
                                }
                            }
                        }
                    }
                    distro[8 * posNeg + t][g] = 1;
                    break testGateLoop;
                }
            }
        }
        //stores results in tupleDistro[][]
        Arrays.fill(tupleDistro[gate], -1);
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                testGateLoop:
                for (int g = 0; g < 16; g++) {
                    if (distro[8 * posNeg + t][g] == 1) {
                        tupleDistro[gate][8 * posNeg + t] = g;
                    }
                }
            }
        }
        //after this is experimental to see why it doesn't work for the column-weighted set
//        for (int t = 0; t < 16; t++) {
//            if (tupleDistro[gate][t] == -1) {
//                //if ( (gate == 8 ||gate == 14)) continue;
//                int[][] control = new int[16][16];
//                for (int r = 0; r < 16; r++) {
//                    for (int c = 0; c < 16; c++) {
//                        int[][] a = hash.hashRows.generateCodewordTile(r, hash.bothLists[listLayer][t % 8]);
//                        int[][] b = hash.hashRows.generateCodewordTile(c, hash.bothLists[listLayer][t % 8]);
//                        int[][] cc = new int[4][4];
//                        int[][] dd = new int[4][4];
//                        for (int row = 0; row < 4; row++) {
//                            for (int col = 0; col < 4; col++) {
//                                int tot = 0;
//                                for (int power = 0; power < 4; power++) {
//                                    int ab = (1 << power) * (((a[row][col] >> power) % 2) + 2 * ((b[row][col] >> power) % 2));
//                                    tot += (1 << power) * ((gate >> ab) % 2);
//                                }
//                                cc[row][col] = tot;
//                            }
//                        }
//                        hash.hashRowsColumns[listLayer].findMinimizingCodeword(hash.bothLists[listLayer][t % 8], cc);
//                        int codeword = (1 == (t / 8)) ? hash.hashRowsColumns[listLayer].lastMinCodeword : hash.hashRowsColumns[listLayer].lastMaxCodeword;
//                        control[r][c] = codeword;
//                        int altCodeword = 0;
//                        int altGate = gate;
//                        if (listLayer == 1) altGate = 15 - gate;
//                        for (int power = 4; power < 4; power++) {
//                            altCodeword += (1 << power) * ((altGate >> (((r >> power) % 2) + 2 * ((c >> power) % 2))) % 2);
//                        }
//                        control[r][c] = codeword;
//                    }
//                }
//                CustomArray.plusArrayDisplay(control, false, false, "control");
//                for (int power = 0; power < 0; power++) {
//                    int[][] a = CustomArray.layerOf(control, power, 2, 2, true, false);
//                    int[][] b = CustomArray.layerOf(control, (power + 1) % 4, 2, 2, false, false);
//                    int[][] c = new int[16][16];
//                    for (int row = 0; row < 16; row++) {
//                        for (int col = 0; col < 16; col++) {
//                            c[row][col] = (a[row][col] ^ b[row][col]);
//                        }
//                    }
//                    CustomArray.plusArrayDisplay(c, true, false, "powers: " + power + " " + ((power + 1) % 4));
//                }
//                powerLoop:
//                for (int power = 0; power < 4; power++) {
//                    gLoop:
//                    for (int g = 0; g < 16; g++) {
//                        for (int r = 0; r < 8; r++) {
//                            if (Arrays.deepEquals(hadamard.allH[g], CustomArray.reflectRotateTransposeStatic(CustomArray.layerOf(control, power, 2, 2, false, false), r))) {
//                                tupleDistro[gate][t] = g + 16;
//                                continue powerLoop;
//                            }
//                        }
//                    }
//                    CustomArray.plusArrayDisplay(CustomArray.layerOf(control, power, 2, 2, false, false), false, false, "powers: " + power);
//                }
//            }
//        }
//        //CustomArray.plusArrayDisplay(distro, false, false, "distro");
//        //Display
//        for (int posNeg = 0; posNeg < 2; posNeg++) {
//            for (int t = 0; t < 8; t++) {
//                if (Arrays.deepEquals(additionTables[posNeg][t], h)) {
//                    //System.out.println("posNeg: " + posNeg + " t: " + t);
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
        //hash.rowError = previousRowError;
    }

    /**
     * Hash logic transform generator. This is the main function that generates the truth tables and calls testGate() for individual rules
     */
    public void testAllLogic() throws IOException {
        //hash.initWolframs();
        int[][] tupleDistro = new int[16][16];
        for (int row = 0; row < 16; row++) {
            Arrays.fill(tupleDistro[row], -1);
        }
        for (int gate = 0; gate < 16; gate++) {
            //System.out.println("___________________________________________________________________________");
            //System.out.println("gate: " + gate);
            testGate(gate, logicTransformRowError, true);
            testGate(gate, logicTransformColumnError, false);
        }
        //displays the hash logic op truth tables
        int[] gateDistro = new int[16];
        for (int gate = 0; gate < 16; gate++) {
            System.out.println("gate : " + gate + " tupleDistro : " + Arrays.toString(tupleDistro[gate]));
        }
        //logicTransformRowError = tupleDistro;
        System.out.println("Rows are logic gates, AND = 8, OR = 14, XOR = 6...");
        System.out.println("Columns are the minimizing and maximizing codewords with the given rowError parameter weighted hash truth tables");
        System.out.println("[0,15,51,85,170,204,240,255] minimizing and [0,15,51,85,170,204,240,255] maximizing - rowError set");
        CustomArray.plusArrayDisplay(logicTransformRowError, false, false, "HashLogicOpTransform truth table", 2);
        System.out.println("Rows are logic gates, AND = 8, OR = 14, XOR = 6...");
        System.out.println("Columns are the minimizing and maximizing codewords with the given rowError parameter weighted hash truth tables");
        System.out.println("[0,15,51,90,165,204,240,255] minimizing and [0,15,51,90,165,204,240,255] maximizing - columnError set");
        CustomArray.plusArrayDisplay(logicTransformColumnError, false, false, "HashLogicOpTransform truth table", 2);
        System.out.println();
    }

    /**
     * This function verifies that the hash logic op transform truth tables are correct by:
     * 1. hashing the bitmap
     * 2. generate an array of some changes to be made to the bitmap
     * 3. hashing the changed bitmap
     * 4. hashing the changes
     * 5. combine the hashed changes and hashed original bitmap using the appropriate generated logic gate
     * 6. testing to see if these two seperate hash pathways generate the same thing
     *
     * @param filepath name of the bitmap, not including the directory path
     * @param rowError if true, uses the row-weighted codewords, if false uses the column-weighted codewords
     * @throws IOException
     */
    public void verifyLogicOperationHash(String filepath, boolean rowError) throws IOException {
        filepath = "src/ImagesProcessed/" + filepath;
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 1;
        int rows = inImage.getHeight();
        int cols = inImage.getWidth();
        int[][] bfield = new int[rows][cols * 16];
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        //Put the bitmap's raster into the bfield arrays
        int[][][] bFieldSet = new int[16][bfield.length][bfield[0].length];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < cols / 16; column++) {
                for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                    for (int power = 0; power < 8; power++) {
                        bfield[row][16 * column + 8 * rgbbyte + power] = (int) ((Math.abs(inRaster[row * inImage.getWidth() + column]) >> (8 * rgbbyte + power)) % 2);
                        for (int posNegt = 0; posNegt < 16; posNegt++) {
                            bFieldSet[posNegt][row][16 * column + 8 * rgbbyte + power] = ((Math.abs(inRaster[row * cols / 16 + column]) >> (8 * rgbbyte + power)) % 2);
                        }
                    }
                }
            }
        }
        //Initialize logic gate op hash tables
        testAllLogic();
        int[][][] hashSet = new int[16][inImage.getHeight()][inImage.getWidth()];
        depth = 3;
        int gate = 8;
        int listLayer = rowError ? 0 : 1;
        //Hash the input
        for (int t = 0; t < 8; t++) {
            System.out.println("t: " + t);
            hashSet[t] = hash.hashTwoDhexadecimal.hashArray(bFieldSet[t], hash.bothLists[listLayer][t], depth, true, rowError)[depth];
            hashSet[8 + t] = hash.hashTwoDhexadecimal.hashArray(bFieldSet[8 + t], hash.bothLists[listLayer][t], depth, false, rowError)[depth];
        }
        //modification[][] is a random array
        int[][] modification = generateOperation(hashSet[0].length, hashSet[0][0].length);
        int[][][] modificationTransformed = new int[16][hashSet[0].length][hashSet[0][0].length];
        int[][][] modifiedSet = new int[16][hashSet[0].length][hashSet[0][0].length];
        int[][][] internallyModifiedSet = new int[16][hashSet[0].length][hashSet[0][0].length];
        //
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
            modificationTransformed[t] = hash.hashTwoDhexadecimal.hashArray(modification, hash.bothLists[listLayer][t], depth, true, rowError)[depth];
            modificationTransformed[8 + t] = hash.hashTwoDhexadecimal.hashArray(modification, hash.bothLists[listLayer][t], depth, false, rowError)[depth];
            modifiedSet[t] = hash.hashTwoDhexadecimal.hashArray(modifiedSet[t], hash.bothLists[listLayer][t], depth, true, rowError)[depth];
            modifiedSet[8 + t] = hash.hashTwoDhexadecimal.hashArray(modifiedSet[8 + t], hash.bothLists[listLayer][t], depth, false, rowError)[depth];
            for (int row = 0; row < modifiedSet[0].length; row++) {
                for (int column = 0; column < modifiedSet[0][0].length; column++) {
                    int tot = 0;
                    for (int power = 0; power < 4; power++) {
                        int ab = ((hashSet[t][row][column] >> power) % 2) + 2 * ((modificationTransformed[t][row][column] >> power) % 2);
                        ab = (logicTransformRowError[gate][t] >> ab) % 2;
                        tot += (1 << power) * ab;
                    }
                    internallyModifiedSet[t][row][column] = tot;
                    tot = 0;
                    for (int power = 0; power < 4; power++) {
                        int ab = ((hashSet[8 + t][row][column] >> power) % 2) + 2 * ((modificationTransformed[8 + t][row][column] >> power) % 2);
                        ab = (logicTransformRowError[15 - gate][8 + t] >> ab) % 2;
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
                    for (int power = 0; power < 16; power++) {
                        numDifferent[t] += (((modifiedSet[t][row][column] >> power) % 2) ^ ((internallyModifiedSet[t][row][column] >> power) % 2));
                        numDifferent[8 + t] += (((modifiedSet[8 + t][row][column] >> power) % 2) ^ ((internallyModifiedSet[8 + t][row][column] >> power) % 2));
                    }
                }
            }
        }
        System.out.println("numDifferent: " + Arrays.toString(numDifferent));
        System.out.println("numBits: " + (inImage.getHeight() * inImage.getWidth()) * 16);
    }

    /**
     * This function verifies that the hash logic op transform truth tables are correct by:
     * 1. hashing the bitmap
     * 2. generate an array of some changes to be made to the bitmap
     * 3. hashing the changed bitmap
     * 4. hashing the changes
     * 5. combine the hashed changes and hashed original bitmap using the appropriate generated logic gate
     * 6. testing to see if these two seperate hash pathways generate the same thing
     *
     * @param filepath name of the bitmap, not including the directory path
     * @throws IOException
     */
    public void verifyLogicOperationHash(String filepath, int gate, int depth) throws IOException {
        filepath = "src/ImagesProcessed/" + filepath;
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int rows = inImage.getHeight();
        int cols = inImage.getWidth();
        int[][] bfield = new int[rows][cols * 16];
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        //Put the bitmap's raster into the bfield arrays
        int[][][] bFieldSet = new int[32][bfield.length][bfield[0].length];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < cols / 16; column++) {
                for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                    for (int power = 0; power < 8; power++) {
                        bfield[row][16 * column + 8 * rgbbyte + power] = (int) ((Math.abs(inRaster[row * inImage.getWidth() + column]) >> (8 * rgbbyte + power)) % 2);
                        for (int posNegt = 0; posNegt < 32; posNegt++) {
                            bFieldSet[posNegt][row][16 * column + 8 * rgbbyte + power] = ((Math.abs(inRaster[row * cols / 16 + column]) >> (8 * rgbbyte + power)) % 2);
                        }
                    }
                }
            }
        }
        //Initialize logic gate op hash tables
        testAllLogic();
        int[][][] hashSet = new int[32][inImage.getHeight()][inImage.getWidth()];
        //Hash the input
        for (int t = 0; t < 32; t++) {
            hashSet[t] = hash.hashTwoDhexadecimal.hashArray(bFieldSet[t], hash.bothLists[(t / 16) % 2][t % 8], depth, (t / 8) % 2 == 0 ? true : false, (t / 16) % 2 == 0 ? true : false)[depth];
        }
        //modification[][] is a random array
        int[][] modification = generateOperation(hashSet[0].length, hashSet[0][0].length);
        int[][][] modificationTransformed = new int[16][hashSet[0].length][hashSet[0][0].length];
        int[][][] modifiedSet = new int[16][hashSet[0].length][hashSet[0][0].length];
        int[][][] internallyModifiedSet = new int[16][hashSet[0].length][hashSet[0][0].length];
        //
        for (int t = 0; t < 32; t++) {
            System.out.println("t: " + t);
            for (int row = 0; row < modifiedSet[0].length; row++) {
                for (int column = 0; column < modifiedSet[0][0].length; column++) {
                    modifiedSet[t][row][column] = bFieldSet[t][row][column] + 2 * modification[row][column];
                    modifiedSet[t][row][column] = (gate >> modifiedSet[t][row][column]) % 2;
                }
            }
            modificationTransformed[t] = hash.hashTwoDhexadecimal.hashArray(modification, hash.bothLists[(t / 16) % 2][t % 8], depth, (t / 8) % 2 == 0 ? true : false, (t / 16) % 2 == 0 ? true : false)[depth];
            modifiedSet[t] = hash.hashTwoDhexadecimal.hashArray(modifiedSet[t], hash.bothLists[(t / 16) % 2][t % 8], depth, (t / 8) % 2 == 0 ? true : false, (t / 16) % 2 == 0 ? true : false)[depth];
            for (int row = 0; row < modifiedSet[0].length && (t / 16) % 2 == 0; row++) {
                for (int column = 0; column < modifiedSet[0][0].length; column++) {
                    int tot = 0;
                    for (int power = 0; power < 4; power++) {
                        int ab = ((hashSet[t][row][column] >> power) % 2) + 2 * ((modificationTransformed[t][row][column] >> power) % 2);
                        ab = (logicTransformRowError[gate][t] >> ab) % 2;
                        tot += (1 << power) * ab;
                    }
                    internallyModifiedSet[t][row][column] = tot;
                    tot = 0;
                    for (int power = 0; power < 4; power++) {
                        int ab = ((hashSet[8 + t][row][column] >> power) % 2) + 2 * ((modificationTransformed[8 + t][row][column] >> power) % 2);
                        ab = (logicTransformRowError[15 - gate][8 + t] >> ab) % 2;
                        tot += (1 << power) * ab;
                    }
                    internallyModifiedSet[8 + t][row][column] = tot;
                }
            }
        }
//        for (int t = 0; t < 8; t++) {
//            System.out.println("t: " + t);
//            for (int row = 0; row < modifiedSet[0].length; row++) {
//                for (int column = 0; column < modifiedSet[0][0].length; column++) {
//                    modifiedSet[t][row][column] = bFieldSet[t][row][column] + 2 * modification[row][column];
//                    modifiedSet[8 + t][row][column] = bFieldSet[8 + t][row][column] + 2 * modification[row][column];
//                    modifiedSet[t][row][column] = (gate >> modifiedSet[t][row][column]) % 2;
//                    modifiedSet[8 + t][row][column] = (gate >> modifiedSet[8 + t][row][column]) % 2;
//                }
//            }
//            modificationTransformed[t] = hash.hashTwoDhexadecimal.hashArray(modification, hash.bothLists[listLayer][t], depth, true, rowError)[depth];
//            modificationTransformed[8 + t] = hash.hashTwoDhexadecimal.hashArray(modification, hash.bothLists[listLayer][t], depth, false, rowError)[depth];
//            modifiedSet[t] = hash.hashTwoDhexadecimal.hashArray(modifiedSet[t], hash.bothLists[listLayer][t], depth, true, rowError)[depth];
//            modifiedSet[8 + t] = hash.hashTwoDhexadecimal.hashArray(modifiedSet[8 + t], hash.bothLists[listLayer][t], depth, false, rowError)[depth];
//            for (int row = 0; row < modifiedSet[0].length; row++) {
//                for (int column = 0; column < modifiedSet[0][0].length; column++) {
//                    int tot = 0;
//                    for (int power = 0; power < 4; power++) {
//                        int ab = ((hashSet[t][row][column] >> power) % 2) + 2 * ((modificationTransformed[t][row][column] >> power) % 2);
//                        ab = (logicTransformRowError[gate][t] >> ab) % 2;
//                        tot += (1 << power) * ab;
//                    }
//                    internallyModifiedSet[t][row][column] = tot;
//                    tot = 0;
//                    for (int power = 0; power < 4; power++) {
//                        int ab = ((hashSet[8 + t][row][column] >> power) % 2) + 2 * ((modificationTransformed[8 + t][row][column] >> power) % 2);
//                        ab = (logicTransformRowError[15 - gate][8 + t] >> ab) % 2;
//                        tot += (1 << power) * ab;
//                    }
//                    internallyModifiedSet[8 + t][row][column] = tot;
//                }
//            }
//        }
        int[] numDifferent = new int[16];
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
        System.out.println("numDifferent: " + Arrays.toString(numDifferent));
        System.out.println("numBits: " + (inImage.getHeight() * inImage.getWidth()) * 16);
    }

    /**
     * Generates a binary array of some changes with which to test the internal hash operation transform
     *
     * @param rows number of rows to output
     * @param cols number of columns to output
     * @return a random array of size (row, cols)
     */
    public int[][] generateOperation(int rows, int cols) {
        int[][] out = new int[rows][cols];
        Random rand = new Random();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                out[row][col] = (row ^ col) % 2;
                out[row][col] = rand.nextInt(0, 2);
            }
        }
        return out;
    }

    public void analyzeHashLogicOpIncompletions() throws IOException {
        testAllLogic();
        int[][][] workingCombos = new int[16][500][16];
        int[] counterIndex = new int[16];
        int[][] lrbw = hash.hashCollisions.generateFourBitLRBWsymmetry();
        int[] evensThrees = new int[]{1, 2, 4, 8};
        int[][] combosFive = PermutationsFactoradic.combinations(5, 2);
        int[][] combosFour = PermutationsFactoradic.combinations(4, 2);
        System.out.println("combos: " + Arrays.deepToString(combosFive));
        tLoop:
        for (int t = 0; t < 16; t++) {
            System.out.println("t: " + t);
            boolean complete = true;
            int totValid = 0;
            int[] valid = new int[16];
            for (int gate = 0; gate < 16; gate++) {
                if (logicTransformColumnError[gate][t] == -1) {
                    complete = false;
                } else {
                    totValid++;
                    valid[gate] = 1;
                }
            }
            if (complete) {
                counterIndex[t]++;
                continue tLoop;
            }
            int[] valids = new int[totValid];
            int counter = 0;
            for (int gate = 0; gate < 16; gate++) {
                if (valid[gate] == 1) {
                    valids[counter] = gate;
                    counter++;
                }
            }
            int max = totValid * 5;
            int[] checklist = new int[16];
            int[][] twoByTwo = new int[2][4];
            int[][] circuitAxes = new int[3][totValid * 10];
            int[][][] opCube = new int[5 * totValid][10 * totValid][10 * totValid];
            for (int index = 0; index < totValid; index++) {
                circuitAxes[0][index] = (valids[index]);
                for (int symm = 0; symm < 4; symm++) {
                    circuitAxes[0][index + index * 4 + symm] = lrbw[valids[index]][symm];
                }
            }
            for (int index = 0; index < totValid; index++) {
                circuitAxes[1][index] = (valids[index] >> (4 * index)) % 4;
                for (int symm = 0; symm < 4; symm++) {
                    circuitAxes[1][index + 4 * totValid + symm] = lrbw[valids[index]][circuitAxes[1][index]];
                }
            }
            System.out.println("circuitAxes: " + Arrays.toString(circuitAxes[0]));
            int[] comp = new int[16];
            Arrays.fill(comp, 1);
            int[][][][] results = new int[totValid * 5][totValid * 5][totValid * 5][20000];
            checklist = new int[16];
            for (int input = 0; input < 4; input++) {
                for (int row = 0; row < max; row++) {
                    for (int col = 0; col < max; col++) {
                        for (int zee = 0; zee < max; zee++) {
                            int[] bitsOut = new int[5 + combosFive.length * 5 * totValid];
                            bitsOut[0] = input % 2;
                            bitsOut[1] = (input / 2) % 2;
                            bitsOut[2] = (circuitAxes[0][row] >> (bitsOut[0] + 2 * bitsOut[1])) % 2;
                            bitsOut[3] = (circuitAxes[0][col] >> (bitsOut[0] + 2 * bitsOut[1])) % 2;
                            bitsOut[4] = (circuitAxes[0][zee] >> (bitsOut[2] + 2 * bitsOut[3])) % 2;
                            for (int c = 0; c < combosFive.length; c++) {
                                for (int gate = 0; gate < 5 * totValid; gate++) {
                                    bitsOut[5 + c * 5 * totValid + gate] = bitsOut[combosFive[c][0]] + 2 * bitsOut[combosFive[c][1]];
                                    bitsOut[5 + c * 5 * totValid + gate] = (circuitAxes[0][gate] >> bitsOut[5 + c * 5 * totValid + gate]) % 2;
                                }
                            }
                            for (int bit = 0; bit < 5 + combosFive.length * 5 * totValid; bit++) {
                                results[row][col][zee][bit] += bitsOut[bit] * (1 << input);
                            }
                        }
                    }
                }
            }
            for (int row = 0; row < max; row++) {
                for (int col = 0; col < max; col++) {
                    for (int zee = 0; zee < max; zee++) {
                        for (int bit = 0; bit < 5 + combosFive.length * 5 * totValid; bit++) {
                            checklist[results[row][col][zee][bit]] = 1;
                            checklist[15 - results[row][col][zee][bit]] = 1;
                        }
                    }
                }
            }
            comp = new int[16];
            Arrays.fill(comp, 1);
            if (Arrays.equals(checklist, comp)) {
                counterIndex[t] = 1;
            }
            for (int power = 0; power < 4; power++) {
                if (checklist[(1 << power)] == 1 || checklist[15 - (1 << power)] == 1) {
                    counterIndex[t] = 1;
                }
            }
            System.out.println(Arrays.toString(checklist));
        }
        System.out.println("counter: " + Arrays.toString(counterIndex));
    }

    public void analyzeHashLogicOpIncompletionsFourAxis() throws IOException {
        testAllLogic();
        int[][][] workingCombos = new int[16][500][16];
        int[] counterIndex = new int[16];
        int[][] lrbw = hash.hashCollisions.generateFourBitLRBWsymmetry();
        int[] evensThrees = new int[]{1, 2, 4, 8};
        int[][] combosFive = PermutationsFactoradic.combinations(5, 2);
        int[][] combosFour = PermutationsFactoradic.combinations(4, 2);
        int[][] combosSix = PermutationsFactoradic.combinations(6, 2);
        int[][] combosTen = PermutationsFactoradic.combinations(10, 2);
        int[][] symms = hash.hashCollisions.checkCodewordSymmetry();
        System.out.println("combos: " + Arrays.deepToString(combosFive));
        int[][] validGates = new int[16][16];

        tLoop:
        for (int t = 0; t < 16; t++) {
            System.out.println("t: " + t + " " + hash.bothLists[1][t % 8]);
            boolean complete = true;
            int totValid = 0;
            int[] valid = new int[16];
            for (int gate = 0; gate < 16; gate++) {
                if (logicTransformColumnError[gate][t] == -1) {
                    complete = false;
                } else {
                    totValid++;
                    valid[gate] = 1;
                }
            }
            if (complete) {
                counterIndex[t]++;
                continue tLoop;
            }
            int[] valids = new int[totValid];
            int counter = 0;
            for (int gate = 0; gate < 16; gate++) {
                if (valid[gate] == 1) {
                    valids[counter] = gate;
                    counter++;
                }
            }
            int max = totValid * 5;
            int number = 4 + combosFour.length*5*totValid;
            int finalTotal = 4+number*number;
            int[] checklist = new int[16];
            int[][] twoByTwo = new int[2][4];
            int[][] circuitAxes = new int[3][totValid * 10];
            int[][][] opCube = new int[5 * totValid][10 * totValid][10 * totValid];
            for (int index = 0; index < totValid; index++) {
                circuitAxes[0][index] = (valids[index]);
                for (int symm = 0; symm < 4; symm++) {
                    circuitAxes[0][index + index * 4 + symm] = lrbw[valids[index]][symm];
                }
            }
            for (int index = 0; index < totValid; index++) {
                circuitAxes[1][index] = (valids[index] >> (4 * index)) % 4;
                for (int symm = 0; symm < 4; symm++) {
                    circuitAxes[1][index + 4 * totValid + symm] = lrbw[valids[index]][circuitAxes[1][index]];
                }
            }
            System.out.println("circuitAxes: " + Arrays.toString(circuitAxes[0]));
            int[] comp = new int[16];
            Arrays.fill(comp, 1);
            //int[][][][] results = new int[totValid * 5][totValid * 5][totValid * 5][20000];
            int[][][][][] fourResults = new int[totValid * 5][totValid * 5][totValid * 5][totValid * 5][10000];
            checklist = new int[16];
            for (int row = 0; row < 1; row++) {
                for (int col = 0; col < 1; col++) {
                    for (int zee = 0; zee < 1; zee++) {
                        for (int zoo = 0; zoo < 1; zoo++) {
                            int[] checkBits = new int[100000];
                            for (int input = 0; input < 4; input++) {
                                int index = 4+combosFour.length*5*totValid;

                                int[] bitsOut = new int[100000];

                                bitsOut[0] = input % 2;
                                bitsOut[1] = (input / 2) % 2;
                                bitsOut[2] = (bitsOut[0]+1)%2;
                                bitsOut[3] = (bitsOut[1]+1)%2;
                                for (int c = 0; c < combosFour.length; c++){
                                    for (int g = 0; g < 5*totValid; g++){
                                        bitsOut[4+c*5*totValid+g] = (circuitAxes[0][g] >>( bitsOut[combosFour[c][0] + 2*combosFour[c][1]]))%2;
                                    }
                                }
                                for (int x = 0; x < index; x++){
                                    for (int y = 0; y < index; y++){
                                        for (int g = 0; g < 5*totValid; g++){
                                            bitsOut[index+x*index+y*5*totValid+g] = bitsOut[x] + 2*bitsOut[y];
                                            bitsOut[index+x*index+y*5*totValid+g] = (circuitAxes[0][g]>>bitsOut[index+x*index+y*5*totValid+g])%2;
                                        }
                                    }
                                }
//                                bitsOut[2] = (circuitAxes[0][row] >> (bitsOut[0] + 2 * bitsOut[0])) % 2;
//                                bitsOut[3] = (circuitAxes[0][col] >> (bitsOut[0] + 2 * bitsOut[0])) % 2;
//                                bitsOut[4] = (circuitAxes[0][zee] >> (bitsOut[0] + 2 * bitsOut[0])) % 2;
//                                bitsOut[5] = (circuitAxes[0][zoo] >> (bitsOut[0] + 2 * bitsOut[0])) % 2;
//                                bitsOut[2] = (circuitAxes[0][row] >> (3 - (bitsOut[0] + 2 * bitsOut[0]))) % 2;
//                                bitsOut[3] = (circuitAxes[0][col] >> (3 - (bitsOut[0] + 2 * bitsOut[0]))) % 2;
//                                bitsOut[4] = (circuitAxes[0][zee] >> (3 - (bitsOut[0] + 2 * bitsOut[0]))) % 2;
//                                bitsOut[5] = (circuitAxes[0][zoo] >> (3 - (bitsOut[0] + 2 * bitsOut[0]))) % 2;
//                                for (int c = 0; c < combosTen.length; c++) {
//                                    for (int gate = 0; gate < 5 * totValid; gate++) {
//                                        bitsOut[10 + c * 5 * totValid + gate] = bitsOut[combosTen[c][0]] + 2 * bitsOut[combosTen[c][1]];
//                                        bitsOut[10 + c * 5 * totValid + gate] = (circuitAxes[0][gate] >> bitsOut[6 + c * 5 * totValid + gate]) % 2;
//                                    }
//                                }
//                                for (int bit = 0; bit < 10 + combosSix.length * 5 * totValid; bit++) {
//                                    checkBits[bit] += bitsOut[bit] * (1 << input);
//                                }
                                for (int bit = 0; bit < bitsOut.length; bit++) {
                                    checkBits[bit] += bitsOut[bit] * (1 << input);
                                }
                            }
                            for (int bit = 0; bit < checkBits.length; bit++) {
                                checklist[checkBits[bit]] = 1;
                                checklist[15 -checkBits[bit]] = 1;
                            }
                        }
                    }
                }
            }
//            for (int row = 0; row < max; row++) {
//                for (int col = 0; col < max; col++) {
//                    for (int zee = 0; zee < max; zee++) {
//                        for (int zoo = 0; zoo < max; zoo++) {
//                            for (int bit = 0; bit < 5 + combosSix.length * 5 * totValid; bit++) {
//                                checklist[fourResults[row][col][zee][zoo][bit]] = 1;
//                                checklist[15 - fourResults[row][col][zee][zoo][bit]] = 1;
//                            }
//                        }
//                    }
//                }
//            }
            for (int tt = 16; tt < 32; tt++){
                for (int ttt = 16; ttt < 32; ttt++){
                    if (symms[tt][ttt] == 1){
                        for (int row = 0; row < 16; row++){
                            if (validGates[ttt-16][row] == 1){
                                validGates[t][row] = 1;
                            }
                        }
                    }
                }
            }
            comp = new int[16];
            Arrays.fill(comp, 1);
            if (Arrays.equals(checklist, comp)) {
                counterIndex[t] = 1;
            }
            for (int power = 0; power < 4; power++) {
                if (checklist[(1 << power)] == 1 || checklist[15 - (1 << power)] == 1) {
                    counterIndex[t] = 1;
                }
            }
            System.out.println(Arrays.toString(checklist));
            validGates[t] = checklist;

        }
        for (int row = 0; row < 16; row++){
            if (counterIndex[row] == 1) {
                for (int t = 16; t < 32; t++){
                    if (symms[16+row][t] == 1){
                        counterIndex[t-16] = 1;
                    }
                }
            }
        }
        System.out.println("counter: " + Arrays.toString(counterIndex));
        CustomArray.plusArrayDisplay(validGates,false,false,"validGates[][]");
    }
}
