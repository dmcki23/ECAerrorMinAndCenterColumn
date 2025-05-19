package AlgorithmCode;

import CustomLibrary.CustomArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * This class produces and verifies truth tables of logic gate transformation when hashed. If you take a codeword-produced 4x4 tile and combine it with another codeword-produced 4x4 tile
 * via a bitwise 0-15 logic gate operation and rehash the tile, the logic operation between the original codewords and the result codeword becomes transformed into another logic operation.
 * AND is 8, OR is 14.
 *
 * A logic operation between two inputs pre-hash becomes a different logic operation within the hash. Using these truth tables you can operate on hashed input without lossy inversion
 * and without the original data.
 *
 * This class generates and verifies these operation transformations for all 0-15 logic gates and all 16 row-weighted hashes, the column-weighted hashes have an incomplete set of these transformations.
 */
public class HashLogicOpTransform {
    /**
     * Loads the manager class
     * @param inHash manager class
     */
    public HashLogicOpTransform(Hash inHash){
        hash = inHash;
    }
    /**
     * Manager class
     */
    Hash hash;
    /**
     * Truth table of generated logic op transformations
     */
    int[][] logicTransform = new int[16][16];
    /**
     * A class that generates Hadamard matrices, here it is experimental at the moment
     */
    Hadamard hadamard = new Hadamard();

    /**
     * This function tests a single 0-15 logic gate against all 16 minMax codeword sets rowError or columnError
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
                        int[][] dd = new int[4][4];
                        //add the neighborhoods together pair-wise
                        for (int row = 0; row < 4; row++) {
                            for (int col = 0; col < 4; col++) {
                                //cc[row][col] = aa[row][col] + bb[row][col];
                                //if (cc[row][col] < 2) dd[row][col] = 0;
                                //else dd[row][col] = 1;
                                cc[row][col] = (gate >> (aa[row][col] + 2 * bb[row][col])) % 2;
                            }
                        }
                        //find the codeword of the sum of the neighborhoods
                        int[][] ccc = hash.hashRowsColumns[listLayer].findMinimizingCodeword(hash.bothLists[listLayer][t], cc);
                        int result = 0;
                        for (int column = 0; column < 4; column++) {
                            result += (int) Math.pow(2, column) * ccc[0][column];
                        }
                        //stortruee it in the table
                        //additionTables[posNeg][t][a][b] = result;
                        if (posNeg == 1) additionTables[posNeg][t][a][b] = hash.hashRowsColumns[listLayer].lastMaxCodeword;
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
            testGate(gate, tupleDistro,  rowError);
        }
        int[] gateDistro = new int[16];
        for (int gate = 0; gate < 16; gate++) {
            System.out.println("gate : " + gate + " tupleDistro : " + Arrays.toString(tupleDistro[gate]));

        }
        logicTransform = tupleDistro;
//        System.out.println(Arrays.toString(gateDistro));
//        int[] gateDistro2 = new int[16];
//        int[] comp = new int[16];
//        Arrays.fill(comp, 1);
//        int[][] connected = new int[16][16];
//        for (int element = 0; element < 0; element++) {
//            gateDistro2 = new int[16];
//            for (int gate = 0; gate < 16; gate++) {
//                gateDistro2[tupleDistro[gate][element]]++;
//                //connected[gate][element] = 1;
//                //connected[element][gate] = 1;
//                //connected[tupleDistro[gate][element]][tupleDistro[element][gate]] = 1;
//                //connected[tupleDistro[element][gate]][tupleDistro[gate][element]] = 1;
//                connected[gate][tupleDistro[element][gate]] = 1;
//                connected[tupleDistro[gate][element]][gate] = 1;
//                connected[element][tupleDistro[gate][element]] = 1;
//                connected[tupleDistro[element][gate]][element] = 1;
//            }
//            if (Arrays.equals(comp, gateDistro2)) {
//                System.out.println("element: " + element);
//            }
//        }
//        CustomArray.plusArrayDisplay(connected, false, false, "connected");
//        int[][] negs = new int[16][16];
//        for (int row = 0; row < 16; row++) {
//            for (int col = 0; col < 16; col++) {
//                negs[row][col] = (tupleDistro[row][col] < 0) ? 1 : 0;
//            }
//        }
//        CustomArray.plusArrayDisplay(negs, false, false, "negs");
    }

    /**
     * This function verifies that the hash logic op transform truth tables are correct by:
     * 1. hashing a bitmap
     * 2. generate an array of some changes to be made to the bitmap
     * 3. hashing the changed bitmap
     * 4. hashing the changes
     * 5. combine the hashed changes and hashed original bitmap using the appropriate generated logic gate
     * 6. testing to see if these two seperate hash pathways generate the same thing
     *
     * @throws IOException
     */
    public void verifyLogicOperationHash(String filepath, int dummy, boolean rowError) throws IOException {
        filepath = "src/ImagesProcessed/" + filepath;
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 1;
        int rows = inImage.getHeight();
        int cols = inImage.getWidth();
        int[][] bfield = new int[rows][cols*16];
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        int[][][] bFieldSet = new int[16][bfield.length][bfield[0].length];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < cols/16; column++) {
                for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                    for (int power = 0; power < 8; power++) {
                        bfield[row][16 * column + 8 * rgbbyte + power] = (int) ((Math.abs(inRaster[row * inImage.getWidth() + column]) >> (8 * rgbbyte + power)) % 2);
                        for (int posNegt = 0; posNegt < 16; posNegt++) {
                            bFieldSet[posNegt][row][16 * column + 8 * rgbbyte + power] = ((Math.abs(inRaster[row*cols/16+column]) >> (8 * rgbbyte + power)) %2);
                        }
                    }
                }
            }
        }
        //Initialize the minMax codeword truth table set
        //hash.initWolframs();
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
        int listLayer = rowError ? 0 : 1;
        for (int t = 0; t < 8; t++) {
            System.out.println("t: " + t);
            hashSet[t] = hash.hashArray(bFieldSet[t], hash.bothLists[listLayer][t], depth, true, rowError)[depth];
            hashSet[8 + t] = hash.hashArray(bFieldSet[8 + t], hash.bothLists[listLayer][t], depth, false, rowError)[depth];
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
            modificationTransformed[t] = hash.hashArray(modification, hash.bothLists[listLayer][t], depth, true, rowError)[depth];
            modificationTransformed[8 + t] = hash.hashArray(modification, hash.bothLists[listLayer][t], depth, false, rowError)[depth];
            modifiedSet[t] = hash.hashArray(modifiedSet[t], hash.bothLists[listLayer][t], depth, true, rowError)[depth];
            modifiedSet[8 + t] = hash.hashArray(modifiedSet[8 + t], hash.bothLists[listLayer][t], depth, false, rowError)[depth];
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
     * @return
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
