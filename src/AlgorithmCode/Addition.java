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
 * Two hash tiles combined by layering and voting (binary (a,b)->{0,0,1,1}), then rehashing,
 * result in Row AND Column. Hash tile addition is the non-reduced Hadamard matrix.
 */
public class Addition {
    /**
     * Used in generating addition tables
     */
    HashTransform hashTransform = new HashTransform();
    /**
     * Used in generating addition tables
     */
    HashTruthTables hash = new HashTruthTables();
    int[][] logicTransform = new int[16][16];

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
     * The first part of this function generates the addition tables of adding two hash tiles together,
     * Showing that adding tiles does indeed result in a non-reduced Hadamard matrix. After that
     * is some experimentation ???
     */
    public void testGate(int gate, int[][] tupleDistro, int[][] h) {
        //hashTransform.initWolframs();
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
                                //cc[row][col] = aa[row][col] + bb[row][col];
                                //if (cc[row][col] < 2) dd[row][col] = 0;
                                //else dd[row][col] = 1;
                                cc[row][col] = (gate >> (aa[row][col] + 2 * bb[row][col])) % 2;
                            }
                        }
                        //find the codeword of the sum of the neighborhoods
                        int[][] ccc = hash.findMinimizingCodeword(hashTransform.unpackedList[t], cc);
                        int result = 0;
                        for (int column = 0; column < 4; column++) {
                            result += (int) Math.pow(2, column) * ccc[0][column];
                        }
                        //store it in the table
                        additionTables[posNeg][t][a][b] = result;
                        if (posNeg == 1) additionTables[posNeg][t][a][b] = hash.lastMaxCodeword;
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
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                testGateLoop:
                for (int g = 0; g < 16; g++) {
                    for (int a = 0; a < 16; a++) {
                        for (int b = 0; b < 16; b++) {
                            for (int power = 0; power < 4; power++) {
                                int ab = (a >> power) % 2 + 2 *( (b >> power) % 2);
                                int c = (additionTables[posNeg][t][a][b] >> power) % 2;
                                if ((g >> ab) % 2 != c) {
                                    continue testGateLoop;
                                }
                            }
                        }
                    }
                    distro[8 * posNeg + t][g]++;
                }
            }
        }
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
        CustomArray.plusArrayDisplay(distro, false, false, "distro");
        //Display
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                if (Arrays.deepEquals(additionTables[posNeg][t], h)) {
                    System.out.println("posNeg: " + posNeg + " t: " + t);
                }
                for (int power = 0; power < 4; power++) {
                    int[][] display = new int[16][16];
                    for (int row = 0; row < 16; row++) {
                        for (int col = 0; col < 16; col++) {
                            display[row][col] = (additionTables[posNeg][t][row][col] >> power) % 2;
                        }
                    }
                    //CustomArray.plusArrayDisplay(display, false, false, "posNeg: " + posNeg + " t: " + t + " " + power);
                    //CustomArray.plusArrayDisplay(additionTables[posNeg][t], true, false, "posNeg: " + posNeg + " t: " + t + " " + hashTransform.unpackedList[t]);
                    //CustomArray.intoBinary(additionTables[posNeg][t], 4, 2, 2, true,false);
                }
            }
        }
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                for (int power = 0; power < 4; power++) {
                    int[][] display = new int[2][2];
                    for (int row = 0; row < 2; row++) {
                        for (int col = 0; col < 2; col++) {
                            display[row][col] = (additionTables[posNeg][t][row][col] >> power) % 2;
                         //   tupleDistro[gate][8 * posNeg + t] += (display[row][col] << (2 * row + col));
                        }
                    }
                    //CustomArray.plusArrayDisplay(display, false, false, "posNeg: " + posNeg + " t: " + t + " " + power);
                    //CustomArray.plusArrayDisplay(additionTables[posNeg][t], true, false, "posNeg: " + posNeg + " t: " + t + " " + hashTransform.unpackedList[t]);
                    //CustomArray.intoBinary(additionTables[posNeg][t], 4, 2, 2, true,false);
                }
            }
        }
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                //CustomArray.plusArrayDisplay(display, false, false, "posNeg: " + posNeg + " t: " + t + " " + power);
                //CustomArray.plusArrayDisplay(additionTables[posNeg][t], false, false, "posNeg: " + posNeg + " t: " + t + " " + hashTransform.unpackedList[t]);
                //CustomArray.intoBinary(additionTables[posNeg][t], 4, 2, 2, true,false);
            }
        }
    }

    public void testAllLogic() {
        hashTransform.initWolframs();
        int[][] tupleDistro = new int[16][16];
        int[][] h = new int[16][16];
        Hadamard hadamard = new Hadamard();
        h = hadamard.nonReducedHadamard(16);
        for (int gate = 0; gate < 16; gate++) {
            System.out.println("___________________________________________________________________________");
            System.out.println("___________________________________________________________________________");
            System.out.println("___________________________________________________________________________");
            System.out.println("___________________________________________________________________________");
            System.out.println("___________________________________________________________________________");
            System.out.println("___________________________________________________________________________");
            System.out.println("___________________________________________________________________________");
            System.out.println("___________________________________________________________________________");
            System.out.println("gate: " + gate);
            testGate(gate, tupleDistro, h);
        }
        int[] gateDistro = new int[16];
        for (int gate = 0; gate < 16; gate++) {
            System.out.println("gate : " + gate + " tupleDistro : " + Arrays.toString(tupleDistro[gate]));
            for (int element = 0; element < 16; element++) {
                gateDistro[tupleDistro[gate][element]]++;
            }
        }
        logicTransform = tupleDistro;
        System.out.println(Arrays.toString(gateDistro));
        int[] gateDistro2 = new int[16];
        int[] comp = new int[16];
        Arrays.fill(comp, 1);
        int[][] connected = new int[16][16];
        for (int element = 0; element < 16; element++) {
            gateDistro2 = new int[16];
            for (int gate = 0; gate < 16; gate++) {
                gateDistro2[tupleDistro[gate][element]]++;
                //connected[gate][element] = 1;
                //connected[element][gate] = 1;
                //connected[tupleDistro[gate][element]][tupleDistro[element][gate]] = 1;
                //connected[tupleDistro[element][gate]][tupleDistro[gate][element]] = 1;
                connected[gate][tupleDistro[element][gate]] = 1;
                connected[tupleDistro[gate][element]][gate] = 1;
                connected[element][tupleDistro[gate][element]] = 1;
                connected[tupleDistro[element][gate]][element] = 1;
            }
            if (Arrays.equals(comp, gateDistro2)) {
                System.out.println("element: " + element);
            }
        }
        CustomArray.plusArrayDisplay(connected, false, false, "connected");
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
        testAllLogic();
        //hashUtilities.readFromFile();
        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        //bfield = hash.initializeDepthZero(bfield, hash.unpackedList[3])[1];
        for (int t = 0; t < 8; t++) {
            //bFieldSet[t] = initializeDepthZero(bFieldSet[t], unpackedList[t])[1];
            //bFieldSet[8 + t] = initializeDepthMax(bFieldSet[8 + t], unpackedList[t])[1];
        }
        //Do the transform
        //framesOfHashing = hash.ecaMinTransform(bfield, hash.unpackedList[3], depth);
        int[][][] hashSet = new int[16][inImage.getHeight()][inImage.getWidth()];
        depth = 3;
        int gate = 8;
        for (int t = 0; t < 8; t++) {
            System.out.println("t: " + t);
            hashSet[t] = hash.ecaMinTransform(bFieldSet[t], hash.unpackedList[t], depth)[depth];
            hashSet[8 + t] = hash.ecaMaxTransform(bFieldSet[8 + t], hash.unpackedList[t], depth)[depth];
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
            modificationTransformed[t] = hash.ecaMinTransform(modification, hash.unpackedList[t], depth)[depth];
            modificationTransformed[8 + t] = hash.ecaMaxTransform(modification, hash.unpackedList[t], depth)[depth];
            modifiedSet[t] = hash.ecaMinTransform(modifiedSet[t], hash.unpackedList[t], depth)[depth];
            modifiedSet[8 + t] = hash.ecaMaxTransform(modifiedSet[8 + t], hash.unpackedList[t], depth)[depth];
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
    public void bitmapTransformCompleteSetNextMorning(String filepath, int dummy) throws IOException {
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
        testAllLogic();
        //hashUtilities.readFromFile();
        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        //bfield = hash.initializeDepthZero(bfield, hash.unpackedList[3])[1];
        for (int t = 0; t < 8; t++) {
            //bFieldSet[t] = initializeDepthZero(bFieldSet[t], unpackedList[t])[1];
            //bFieldSet[8 + t] = initializeDepthMax(bFieldSet[8 + t], unpackedList[t])[1];
        }
        //Do the transform
        //framesOfHashing = hash.ecaMinTransform(bfield, hash.unpackedList[3], depth);
        int[][][] hashSet = new int[16][inImage.getHeight()][inImage.getWidth()];
        depth = 3;
        int gate = 14;
        for (int t = 0; t < 8; t++) {
            System.out.println("t: " + t);
            hashSet[t] = hash.ecaMinTransform(bFieldSet[t], hash.unpackedList[t], depth)[depth];
            hashSet[8 + t] = hash.ecaMaxTransform(bFieldSet[8 + t], hash.unpackedList[t], depth)[depth];
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
            modificationTransformed[t] = hash.ecaMinTransform(modification, hash.unpackedList[t], depth)[depth];
            modificationTransformed[8 + t] = hash.ecaMaxTransform(modification, hash.unpackedList[t], depth)[depth];
            modifiedSet[t] = hash.ecaMinTransform(modifiedSet[t], hash.unpackedList[t], depth)[depth];
            modifiedSet[8 + t] = hash.ecaMaxTransform(modifiedSet[8 + t], hash.unpackedList[t], depth)[depth];
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
                        ab = (logicTransform[15-gate][8 + t] >> ab) % 2;
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
                        numDifferent[t] += (((modifiedSet[t][row][column]>>power)%2) ^ ((internallyModifiedSet[t][row][column]>>power)%2));
                        numDifferent[8 + t] += (((modifiedSet[8 + t][row][column]>>power)%2) ^ ((internallyModifiedSet[8 + t][row][column]>>power)%2));
                    }
                }
            }
        }
        System.out.println("numDifferent: " + Arrays.toString(numDifferent));
        System.out.println("numBits: " + (inImage.getHeight()*inImage.getWidth())*16);
    }

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
