package AlgorithmCode;

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
    int[][] logicTransform = new int[16][16];
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
                        //store it in the table
                        if (posNeg == 1)
                            additionTables[posNeg][t][a][b] = hash.hashRowsColumns[listLayer].lastMaxCodeword;
                        else additionTables[posNeg][t][a][b] = hash.hashRowsColumns[listLayer].lastMinCodeword;
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
     *
     * @param rowError if true, uses 2^row, if false uses 2^column weight on errorScore
     */
    public void testAllLogic(boolean rowError) throws IOException {
        //hash.initWolframs();
        int[][] tupleDistro = new int[16][16];
        for (int gate = 0; gate < 16; gate++) {
            System.out.println("___________________________________________________________________________");
            System.out.println("gate: " + gate);
            testGate(gate, tupleDistro, rowError);
        }
        int[] gateDistro = new int[16];
        for (int gate = 0; gate < 16; gate++) {
            System.out.println("gate : " + gate + " tupleDistro : " + Arrays.toString(tupleDistro[gate]));
        }
        logicTransform = tupleDistro;
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
        testAllLogic(true);

        int[][][] hashSet = new int[16][inImage.getHeight()][inImage.getWidth()];
        depth = 3;
        int gate = 8;
        int listLayer = rowError ? 0 : 1;
        //Hash the input
        for (int t = 0; t < 8; t++) {
            System.out.println("t: " + t);
            hashSet[t] = hash.twoDHashTransform.hashArray(bFieldSet[t], hash.bothLists[listLayer][t], depth, true, rowError)[depth];
            hashSet[8 + t] = hash.twoDHashTransform.hashArray(bFieldSet[8 + t], hash.bothLists[listLayer][t], depth, false, rowError)[depth];
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
            modificationTransformed[t] = hash.twoDHashTransform.hashArray(modification, hash.bothLists[listLayer][t], depth, true, rowError)[depth];
            modificationTransformed[8 + t] = hash.twoDHashTransform.hashArray(modification, hash.bothLists[listLayer][t], depth, false, rowError)[depth];
            modifiedSet[t] = hash.twoDHashTransform.hashArray(modifiedSet[t], hash.bothLists[listLayer][t], depth, true, rowError)[depth];
            modifiedSet[8 + t] = hash.twoDHashTransform.hashArray(modifiedSet[8 + t], hash.bothLists[listLayer][t], depth, false, rowError)[depth];
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
}
