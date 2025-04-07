package AlgorithmCode;

import CustomLibrary.CustomArray;

import java.util.Arrays;

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
                    }
                }
            }
        }
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
                for (int power = 0; power < 1; power++) {
                    int[][] display = new int[2][2];
                    for (int row = 0; row < 2; row++) {
                        for (int col = 0; col < 2; col++) {
                            display[row][col] = (additionTables[posNeg][t][row][col] >> power) % 2;
                            tupleDistro[gate][8 * posNeg + t] += (display[row][col] << (2 * row + col));
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
        System.out.println(Arrays.toString(gateDistro));
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
}
