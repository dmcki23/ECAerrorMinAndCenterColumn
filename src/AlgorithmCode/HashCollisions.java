package AlgorithmCode;

import CustomLibrary.CustomArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 *
 */
public class HashCollisions {
    /**
     * Middle layer of transform code
     */
    public HashTransform hash = new HashTransform();
    int[] truthTable;
    int[] singleChangeTruthTable;

    public int[][][] runXORtableThroughHash() {
        int[][] out = new int[16][16];
        hash.initWolframs();
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                out[row][col] = row ^ col;
            }
        }
        int[][][] hashed = new int[16][16][16];
        int[][][] inverse = new int[16][16][16];
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                hashed[8 * posNeg + t] = hash.ecaMinTransform(out, hash.unpackedList[t], 1)[1];
                //CustomArray.plusArrayDisplay(hashed[8*posNeg+t], true, false, "hashed");
                //inverse = hash.reconstructDepthD(hashed,1,hash.unpackedList[t],posNeg);
                //CustomArray.plusArrayDisplay(inverse, true, false, "inverse");
            }
        }
        inverse = hash.reconstructDepthD(hashed, 1);
        for (int posNeg = 0; posNeg < 1; posNeg++) {
            for (int t = 0; t < 8; t++) {
                CustomArray.plusArrayDisplay(inverse[8 * posNeg + t], false, false, "inverse");
                for (int row = 0; row < 16; row++) {
                    for (int col = 0; col < 16; col++) {
                    }
                }
                //System.out.println("\n\n\n\n\n\n\n");
            }
        }
        //CustomArray.plusArrayDisplay(inverse,false,false,"inverse");
        return inverse;
    }

    public void runThroughHash() {
        int[][] out = new int[16][16];
        hash.initWolframs();
        Random rand = new Random();
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                out[row][col] = (row ^ col) % 2;
                //out[row][col] = (row + col) % 2;
                //out[row][col] = rand.nextInt(0,2);
                //out[row][col] = (row & col) % 2;
                //out[row][col] = (row | col) % 2;
                //out[row][col] = ((row*3)^(col*7)+2)%2;
                //out[row][col] = ((3*row)+col)%2;
            }
        }
        int a = 0;
        int b = 0;
        for (int change = 0; change < 1; change++) {
            a = rand.nextInt(0, 16);
            b = rand.nextInt(0, 16);
            System.out.println("a = " + a + ", b = " + b);
            out[a][b] ^= 1;
        }
        CustomArray.plusArrayDisplay(out, false, false, "out");
        int[][][] preHash = new int[16][16][16];
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                preHash[t] = hash.initializeDepthZero(out, hash.unpackedList[t])[1];
                preHash[8 + t] = hash.initializeDepthMax(out, hash.unpackedList[t])[1];
            }
        }
        int[][][] hashed = new int[16][16][16];
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                hashed[t] = hash.ecaMinTransform(preHash[t], hash.unpackedList[t], 1)[1];
                hashed[8 + t] = hash.ecaMaxTransform(preHash[8 + t], hash.unpackedList[t], 1)[1];
            }
        }
        int[][][] inverted = hash.reconstructDepthD(hashed, 1);
        int[][][] in = new int[16][2][2];
        int[][][] outt = new int[16][2][2];
        int[][][] inv = new int[16][2][2];
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                for (int row = 0; row < 16; row++) {
                    for (int col = 0; col < 16; col++) {
                        //inverted[8 * posNeg + t][row][col] ^= preHash[8 * posNeg + t][row][col];
                        //in[8 * posNeg + t][row][col] = preHash[8 * posNeg + t][row][col];
                        //outt[8 * posNeg + t][row][col] = hashed[8 * posNeg + t][row][col];
                        //inv[8 * posNeg + t][row][col] = inverted[8 * posNeg + t][row][col];
                    }
                }
                System.out.println("posNeg: " + posNeg + " t: " + t);
                CustomArray.plusArrayDisplay(preHash[8 * posNeg + t], false, false, "preHash");
                CustomArray.plusArrayDisplay(hashed[8 * posNeg + t], false, false, "hashed ");
                CustomArray.plusArrayDisplay(inverted[8 * posNeg + t], false, false, "inverse");
            }
        }
        int[][] finalized = hash.hashInverseDepth0(inverted, 1, 0);
        int quantityErrors = 0;
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                //finalized[row][col] ^= out[row][col];
                quantityErrors += finalized[(row) % 16][col] ^ out[row][col];
            }
        }
        System.out.println("quantityErrors: " + quantityErrors);
        System.out.println("a: " + a + ", b: " + b);
        CustomArray.plusArrayDisplay(finalized, true, true, "finalized");
        int[][] shifted = new int[16][16];
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                shifted[row][col] = finalized[(row + a) % 16][(col + b) % 16];
            }
        }
        CustomArray.plusArrayDisplay(shifted, true, true, "shifted");
        for (int t = 0; t < 0; t++) {
            for (int posNeg = 0; posNeg < 2; posNeg++) {
                System.out.println("posNeg: " + posNeg + " t: " + t);
                for (int row = 0; row < 2; row++) {
                    for (int col = 0; col < 2; col++) {
                        System.out.print(in[8 * posNeg + t][row][col] + "\t");
                    }
                    System.out.print("\t\t");
                    for (int col = 0; col < 2; col++) {
                        System.out.print(outt[8 * posNeg + t][row][col] + "\t");
                    }
                    System.out.print("\t\t");
                    for (int col = 0; col < 2; col++) {
                        System.out.print(inverted[8 * posNeg + t][row][col] + "\t");
                    }
                    System.out.print("\n");
                }
                System.out.print("\n");
                //finalized[8*posNeg+t] = hash.hashInverseDepth0(inverted,1,hash.unpackedList[t]);
                //CustomArray.plusArrayDisplay(finalized[8*posNeg+t],false,false,"finalized");
            }
        }
    }

    public void checkChangesPerTransform() {
        int[][] out = new int[16][16];
        hash.initWolframs();
        Random rand = new Random();
        int[][][] changes = new int[256][16][16];
        int[][][] shiftChanges = new int[256][16][16];
        int[][][] trackedZero = new int[2][16][16];
        for (int row = 0; row < 16; row++) {
            Arrays.fill(trackedZero[0][row], -1);
            Arrays.fill(trackedZero[1][row], -1);
        }
        Hadamard hadamard = new Hadamard();
        int[][] h = hadamard.nonReducedHadamard(16);
        int[][] changed = new int[16][16];
        for (int cr = 0; cr < 16; cr++) {
            for (int cc = 0; cc < 16; cc++) {
                for (int row = 0; row < 16; row++) {
                    for (int col = 0; col < 16; col++) {
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
                        //out[row][col] = h[cr][cc]%2;
                        changed[row][col] = out[row][col];
                    }
                }
                int a = 0;
                int b = 0;
                int shift = 2;
                int cshift = 2;
                for (int change = 0; change < 1; change++) {
                    a = cr;
                    b = cc;
                    out[cr][cc] ^= 1;
                    changed[cr][cc] ^= 1;
                    changed[(cr + shift) % 16][(cr + shift) % 16] ^= 1;
                    System.out.println("a = " + a + ", b = " + b);
                }
                int[][][] preHash = new int[16][16][16];
                int[][][] cpre = new int[16][16][16];
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        preHash[t] = hash.initializeDepthZero(out, hash.unpackedList[t])[1];
                        preHash[8 + t] = hash.initializeDepthMax(out, hash.unpackedList[t])[1];
                        cpre[t] = hash.initializeDepthZero(changed, hash.unpackedList[t])[1];
                        cpre[8 + t] = hash.initializeDepthMax(changed, hash.unpackedList[t])[1];
                    }
                }
                int[][][] hashed = new int[16][16][16];
                int[][][] chashed = new int[16][16][16];
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        hashed[t] = hash.ecaMinTransform(preHash[t], hash.unpackedList[t], 1)[1];
                        hashed[8 + t] = hash.ecaMaxTransform(preHash[8 + t], hash.unpackedList[t], 1)[1];
                        chashed[t] = hash.ecaMinTransform(cpre[t], hash.unpackedList[t], 1)[1];
                        chashed[t + 8] = hash.ecaMaxTransform(cpre[t + 8], hash.unpackedList[t], 1)[1];
                    }
                }
                int[][][] cinverted = hash.reconstructDepthD(chashed, 1);
                int[][][] inverted = hash.reconstructDepthD(hashed, 1);
                int[][] finalized = hash.hashInverseDepth0(inverted, 1, 0);
                int[][] cfinalized = hash.hashInverseDepth0(cinverted, 1, 0);
                int quantityErrors = 0;
                for (int row = 0; row < 16; row++) {
                    for (int col = 0; col < 16; col++) {
                        //finalized[row][col] ^= out[row][col];
                        if (finalized[row][col] == 0) {
                            trackedZero[0][cr][cc] = row;
                            trackedZero[1][cr][cc] = col;
                        }
                        quantityErrors += finalized[(row) % 16][col] ^ out[row][col];
                    }
                }
                CustomArray.plusArrayDisplay(finalized, true, true, "finalized");
                int[][] shifted = new int[16][16];
                int[][] cshifted = new int[16][16];
                for (int row = 0; row < 16; row++) {
                    for (int col = 0; col < 16; col++) {
                        shifted[row][col] = finalized[(row + a + 8) % 16][(col + b + 8) % 16];
                        cshifted[row][col] = cfinalized[(row + a + 8) % 16][(col + b + 8) % 16];
                    }
                }
                CustomArray.plusArrayDisplay(shifted, true, true, "shifted");
                for (int row = 0; row < 16; row++) {
                    for (int col = 0; col < 16; col++) {
                        changes[16 * cr + cc][row][col] = shifted[row][col];
                        shiftChanges[16 * cr + cc][row][col] = cshifted[row][col];
                        //changes[16*cr+cc][row][col] = finalized[row][col];
                    }
                }
                CustomArray.plusArrayDisplay(shiftChanges[16 * cr + cc], true, true, "shiftedChanges");
            }
        }
        int[][] total = new int[16][16];
        int[][] zeros = new int[16][16];
        for (int cr = 0; cr < 16; cr++) {
            for (int cc = 0; cc < 16; cc++) {
                boolean isZeros = true;
                for (int row = 0; row < 16; row++) {
                    for (int col = 0; col < 16; col++) {
                        total[row][col] += changes[16 * cr + cc][row][col];
                        if (changes[16 * cr + cc][row][col] != 0) isZeros = false;
                    }
                }
                if (!isZeros) {
                    zeros[cr][cc] = 1;
                }
            }
        }
        int[] operation = new int[4];
        Arrays.fill(operation, -1);
        for (int cccc = 0; cccc < 256; cccc++) {
            for (int row = 0; row < 16; row++) {
                for (int col = 0; col < 16; col++) {
                    int x = changes[cccc][row][col];
                    int y = shiftChanges[cccc][row][col];
                    int z = 2 * x + y;
                }
            }
        }
        CustomArray.plusArrayDisplay(total, false, false, "total");
        CustomArray.plusArrayDisplay(zeros, false, false, "zeros");
        for (int row = 0; row < 256; row++) {
        }
        CustomArray.plusArrayDisplay(trackedZero[0], false, false, "trackedZero");
        CustomArray.plusArrayDisplay(trackedZero[1], false, false, "trackedZero");
    }

    public void checkChangesPerTransform(int size) {
        int[][] out = new int[size][size];
        hash.initWolframs();
        Random rand = new Random();
        int[][][] changes = new int[size * size][size][size];
        int[][][] shiftChanges = new int[size * size][size][size];
        int[][][] trackedZero = new int[2][size][size];
        for (int row = 0; row < size; row++) {
            Arrays.fill(trackedZero[0][row], -1);
            Arrays.fill(trackedZero[1][row], -1);
        }
        Hadamard hadamard = new Hadamard();
        int[][] h = hadamard.nonReducedHadamard(size);
        int[][] changed = new int[size][size];
        for (int cr = 0; cr < size; cr++) {
            for (int cc = 0; cc < size; cc++) {
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
                        //out[row][col] = h[cr][cc]%2;
                        changed[row][col] = out[row][col];
                    }
                }
                int a = 0;
                int b = 0;
                int shift = 2;
                int cshift = 2;
                for (int change = 0; change < 1; change++) {
                    a = cr;
                    b = cc;
                    out[cr][cc] ^= 1;
                    changed[cr][cc] ^= 1;
                    changed[(cr + shift) % size][(cr + shift) % size] ^= 1;
                    System.out.println("a = " + a + ", b = " + b);
                }
                int[][][] preHash = new int[16][size][size];
                int[][][] cpre = new int[16][size][size];
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        preHash[t] = hash.initializeDepthZero(out, hash.unpackedList[t])[1];
                        preHash[8 + t] = hash.initializeDepthMax(out, hash.unpackedList[t])[1];
                        cpre[t] = hash.initializeDepthZero(changed, hash.unpackedList[t])[1];
                        cpre[8 + t] = hash.initializeDepthMax(changed, hash.unpackedList[t])[1];
                    }
                }
                int[][][] hashed = new int[16][size][size];
                int[][][] chashed = new int[16][size][size];
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        hashed[t] = hash.ecaMinTransform(preHash[t], hash.unpackedList[t], 1)[1];
                        hashed[8 + t] = hash.ecaMaxTransform(preHash[8 + t], hash.unpackedList[t], 1)[1];
                        chashed[t] = hash.ecaMinTransform(cpre[t], hash.unpackedList[t], 1)[1];
                        chashed[t + 8] = hash.ecaMaxTransform(cpre[t + 8], hash.unpackedList[t], 1)[1];
                    }
                }
                int[][][] cinverted = hash.reconstructDepthD(chashed, 1);
                int[][][] inverted = hash.reconstructDepthD(hashed, 1);
                int[][] finalized = hash.hashInverseDepth0(inverted, 1, 0);
                int[][] cfinalized = hash.hashInverseDepth0(cinverted, 1, 0);
                int quantityErrors = 0;
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        //finalized[row][col] ^= out[row][col];
                        if (finalized[row][col] == 0) {
                            trackedZero[0][cr][cc] = row;
                            trackedZero[1][cr][cc] = col;
                        }
                        quantityErrors += finalized[(row) % size][col] ^ out[row][col];
                    }
                }
                CustomArray.plusArrayDisplay(finalized, true, true, "finalized");
                int[][] shifted = new int[size][size];
                int[][] cshifted = new int[size][size];
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        shifted[row][col] = finalized[(row + a + size / 2) % size][(col + b + size / 2) % size];
                        cshifted[row][col] = cfinalized[(row + a + size / 2) % size][(col + b + size / 2) % size];
                    }
                }
                CustomArray.plusArrayDisplay(shifted, true, true, "shifted");
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        changes[size * cr + cc][row][col] = shifted[row][col];
                        shiftChanges[size * cr + cc][row][col] = cshifted[row][col];
                        //changes[size*cr+cc][row][col] = finalized[row][col];
                    }
                }
                CustomArray.plusArrayDisplay(shiftChanges[size * cr + cc], true, true, "shiftedChanges");
            }
        }
        int[][] total = new int[size][size];
        int[][] zeros = new int[size][size];
        for (int cr = 0; cr < size; cr++) {
            for (int cc = 0; cc < size; cc++) {
                boolean isZeros = true;
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        total[row][col] += changes[size * cr + cc][row][col];
                        if (changes[size * cr + cc][row][col] != 0) isZeros = false;
                    }
                }
                if (!isZeros) {
                    zeros[cr][cc] = 1;
                }
            }
        }
        int[] operation = new int[4];
        Arrays.fill(operation, -1);
        for (int cccc = 0; cccc < size * size; cccc++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    int x = changes[cccc][row][col];
                    int y = shiftChanges[cccc][row][col];
                    int z = 2 * x + y;
                }
            }
        }
        CustomArray.plusArrayDisplay(total, false, false, "total");
        CustomArray.plusArrayDisplay(zeros, false, false, "zeros");
        for (int row = 0; row < size * size; row++) {
        }
        CustomArray.plusArrayDisplay(trackedZero[0], false, false, "trackedZero");
        CustomArray.plusArrayDisplay(trackedZero[1], false, false, "trackedZero");
    }

    public void checkChangesPerTransformAllChanges(int size) {
        int[][] out = new int[size][size];
        //hash.initWolframs();
        truthTable = new int[65536];
        Hadamard hadamard = new Hadamard();
        int[][] h = hadamard.nonReducedHadamard(size);
        int[][] changed = new int[size][size];
        int[][] tile;
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
                //out[row][col] = h[cr][cc]%2;
            }
        }
        for (int address = 0; address < 65536; address++) {
            tile = hash.m.generateAddressTile(address, size);
            //for (int cr = 0; cr < size; cr++) {
            //for (int cc = 0; cc < size; cc++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < 4; col++) {
                    //out[row][col] ^= tile[row][col];
                    changed[row][col] = tile[row][col] ^ out[row][col];
                }
            }
            int[][][] preHash = new int[16][size][size];
            int[][][] cpre = new int[16][size][size];
            for (int posNeg = 0; posNeg < 2; posNeg++) {
                for (int t = 0; t < 8; t++) {
                    //preHash[t] = hash.initializeDepthZero(out, hash.unpackedList[t])[1];
                    //preHash[8 + t] = hash.initializeDepthMax(out, hash.unpackedList[t])[1];
                    cpre[t] = hash.initializeDepthZero(changed, hash.unpackedList[t])[1];
                    cpre[8 + t] = hash.initializeDepthMax(changed, hash.unpackedList[t])[1];
                }
            }
            int[][][] hashed = new int[16][size][size];
            int[][][] chashed = new int[16][size][size];
            for (int posNeg = 0; posNeg < 2; posNeg++) {
                for (int t = 0; t < 8; t++) {
                    //hashed[t] = hash.ecaMinTransform(preHash[t], hash.unpackedList[t], 1)[1];
                    //hashed[8 + t] = hash.ecaMaxTransform(preHash[8 + t], hash.unpackedList[t], 1)[1];
                    chashed[t] = hash.ecaMinTransform(cpre[t], hash.unpackedList[t], 1)[1];
                    chashed[t + 8] = hash.ecaMaxTransform(cpre[t + 8], hash.unpackedList[t], 1)[1];
                }
            }
            int[][][] cinverted = hash.reconstructDepthD(chashed, 1);
            //int[][][] inverted = hash.reconstructDepthD(hashed, 1);
            //int[][] finalized = hash.hashInverseDepth0(inverted, 1, 0);
            int[][] cfinalized = hash.hashInverseDepth0(cinverted, 1, 0);
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    truthTable[address] += (1 << (size * row + col)) * cfinalized[row][col];
                }
            }
        }
    }

    public void checkChangesPerTransformAllSingleChanges(int size) {
        int[][] out = new int[size][size];
        //hash.initWolframs();
        singleChangeTruthTable = new int[size * size];
        Hadamard hadamard = new Hadamard();
        int[][] h = hadamard.nonReducedHadamard(size);
        int[][] changed = new int[size][size];
        int[][] tile;
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
                //out[row][col] = h[cr][cc]%2;
            }
        }
        for (int address = 0; address < size * size; address++) {
            //tile = hash.m.generateAddressTile(address, size);
            //for (int cr = 0; cr < size; cr++) {
            //for (int cc = 0; cc < size; cc++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < 4; col++) {
                    //out[row][col] ^= tile[row][col];
                    //changed[row][col] = tile[row][col] ^ out[row][col];
                    changed[row][col] = out[row][col];
                }
            }
            changed[address / size][address % size] ^= 1;
            int[][][] preHash = new int[16][size][size];
            int[][][] cpre = new int[16][size][size];
            for (int posNeg = 0; posNeg < 2; posNeg++) {
                for (int t = 0; t < 8; t++) {
                    //preHash[t] = hash.initializeDepthZero(out, hash.unpackedList[t])[1];
                    //preHash[8 + t] = hash.initializeDepthMax(out, hash.unpackedList[t])[1];
                    cpre[t] = hash.initializeDepthZero(changed, hash.unpackedList[t])[1];
                    cpre[8 + t] = hash.initializeDepthMax(changed, hash.unpackedList[t])[1];
                }
            }
            int[][][] hashed = new int[16][size][size];
            int[][][] chashed = new int[16][size][size];
            for (int posNeg = 0; posNeg < 2; posNeg++) {
                for (int t = 0; t < 8; t++) {
                    //hashed[t] = hash.ecaMinTransform(preHash[t], hash.unpackedList[t], 1)[1];
                    //hashed[8 + t] = hash.ecaMaxTransform(preHash[8 + t], hash.unpackedList[t], 1)[1];
                    chashed[t] = hash.ecaMinTransform(cpre[t], hash.unpackedList[t], 1)[1];
                    chashed[t + 8] = hash.ecaMaxTransform(cpre[t + 8], hash.unpackedList[t], 1)[1];
                }
            }
            int[][][] cinverted = hash.reconstructDepthD(chashed, 1);
            //int[][][] inverted = hash.reconstructDepthD(hashed, 1);
            //int[][] finalized = hash.hashInverseDepth0(inverted, 1, 0);
            int[][] cfinalized = hash.hashInverseDepth0(cinverted, 1, 0);
            int[][] shifted = new int[size][size];
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    shifted[row][col] = cfinalized[(row + size / 2) % size][(col + size / 2) % size];
                }
            }
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    singleChangeTruthTable[address] += (1 << (size * row + col) * shifted[row][col]);
                }
            }
        }
    }

    public void checkSinglesAgainstAll(int size) {
        hash.initWolframs();
        checkChangesPerTransformAllChanges(size);
        int totZeros = 0;
        for (int address = 0; address < 65536; address++) {
            if (truthTable[address] == 0) {
                totZeros++;
            }
        }
        System.out.println("totZeros " + totZeros);
        checkChangesPerTransformAllSingleChanges(size);
        int[][] changeTile = new int[size][size];
        int[][] successBoard = new int[size * size][size * size];
        int[] successfulGates = new int[256];
        int[][] function = new int[size][size];
        int functionAddress = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                function[row][col] = (row ^ col) % 2;
                functionAddress += (1 << (size * row + col)) * function[row][col];
            }
        }
        for (int changeZero = 0; changeZero < size * size; changeZero++) {
            for (int changeOne = 0; changeOne < size * size; changeOne++) {
                changeTile = new int[size][size];
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        changeTile[row][col] = function[row][col];
                    }
                }
                int[][] czsquare = new int[size][size];
                int[][] cosquare = new int[size][size];
                int[][] ccSquare = new int[size][size];
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        czsquare[row][col] = function[row][col];
                        cosquare[row][col] = function[row][col];
                        ccSquare[row][col] = function[row][col];
                    }
                }
                cosquare[changeOne / size][changeOne % size] ^= 1;
                czsquare[changeZero / size][changeZero % size] ^= 1;
                ccSquare[changeZero / size][changeZero % size] ^= 1;
                ccSquare[changeOne / size][changeOne % size] ^= 1;
                cosquare = hash.m.generateAddressTile(functionAddress^(1<<changeOne),4);
                czsquare = hash.m.generateAddressTile(functionAddress^(1<<changeZero),4);
                ccSquare = hash.m.generateAddressTile(functionAddress^(1<<changeOne)^(1<<changeZero),4);
                int[][] combinedSingles = new int[size][size];

                for (int gate = 0; gate < 256; gate++) {
                    for (int row = 0; row < size; row++) {
                        for (int col = 0; col < size; col++) {
                            int a = 4*ccSquare[row][col] + 2 * czsquare[row][col] + cosquare[row][col];
                            combinedSingles[row][col] = ((gate >> a) % 2);
                        }
                    }
                    if (Arrays.deepEquals(function, combinedSingles)) {
                        successBoard[changeZero][changeOne] = 1;
                        successfulGates[gate]++;
                    }
                }
            }
        }
        CustomArray.plusArrayDisplay(successBoard, true, false, "successBoard");
        for (int n = 0; n < 256; n++) {
            System.out.println("n: " + n + " " + successfulGates[n]);
        }
        int[][] subboard = new int[size * size / 2][size * size / 2];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                subboard[row][col] = successBoard[row * 2][col * 2];
            }
        }
        CustomArray.plusArrayDisplay(subboard, true, false, "subboard");
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
                depthChart[posNeg][t] = hash.initializeDepthZero(in, hash.unpackedList[t])[1];
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
     * For all possible codewords of [0,15,51,85,170,104,240,255] compares the errorScore of its output tile
     * to the codeword's Hadamard parity. This is to make some kind of sense out of why substituting these two values in
     * checkInverse() result in the same reconstitution
     */
    public void checkErrorScoreVsHadamard() {
        hash.initWolframs();
        int totDifferent = 0;
        //For every 8-tuple element
        for (int posNeg = 0; posNeg < 1; posNeg++) {
            for (int t = 0; t < 8; t++) {
                //For all possible values
                for (int input = 0; input < 16; input++) {
                    //Compare a single codeword tile's voting pattern
                    //to that codeword's Hadamard parity
                    int[][] cell = hash.m.generateCodewordTile(input, hash.unpackedList[t]);
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

    /**
     * This function takes all 16 min max codewords for each of the 65536 4x4 neighborhoods
     * and compares them with all other tuple sets in the truth table to check for uniqueness.
     * On their own they are not unique roughly 1/256 pairs are collisions. To force it to be unique
     * see checkUnitWrappedTupleUniqueness() which is what the optional non-collision loop is based on
     *
     * @return whether the tuple sets in the truth tables are distinct
     */
    public boolean checkTupleUniqueness() {
        hash.initWolframs();
        boolean out = true;
        int[][] innerOuterTuples = new int[2][16];
        int numSame = 0;
        for (int address = 0; address < 65536; address++) {
            if (address % 256 == 0) System.out.println("address: " + address);
            for (int t = 0; t < 8; t++) {
                innerOuterTuples[0][t] = hash.flatWolframs[0][t][address];
                innerOuterTuples[0][t + 8] = hash.flatWolframs[1][t][address];
            }
            for (int add = 0; add < address; add++) {
                for (int t = 0; t < 8; t++) {
                    innerOuterTuples[1][t] = hash.flatWolframs[0][t][add];
                    innerOuterTuples[1][t + 8] = hash.flatWolframs[1][t][add];
                }
                if (Arrays.equals(innerOuterTuples[0], innerOuterTuples[1])) {
                    out = false;
                    numSame++;
                }
            }
        }
        System.out.println("uniqueness: " + out);
        System.out.println("numSame: " + numSame);
        return out;
    }

    public void checkCollisions() {
        int[] numCollisions = new int[16];
        //a 5x5 binary array containing 4 4x4 subarrays that are codeword neighborhoods
        int[][] field = new int[5][5];
        //field[][] changed randomly
        int[][] changedField = new int[5][5];
        //Outer loop's neighborhood integer values
        int[] address = new int[4];
        //Outer loop's codewords
        int[][] codewords = new int[16][4];
        //Inner loop's neighborhood integer values
        int[] innerAddress = new int[4];
        //Inner loop's codewords
        int[][] innerCodewords = new int[16][4];
        //Number of times to generate a random array, and number of times to change that initial random array
        int numTrials = 5000;
        //Random number generator
        Random rand = new Random();
        //Initialize the 8-tuple truth tables
        hash.initWolframs();
        //
        for (int trial = 0; trial < numTrials; trial++) {
            for (int row = 0; row < 5; row++) {
                for (int column = 0; column < 5; column++) {
                    field[row][column] = rand.nextInt(0, 2);
                }
            }
            address = new int[4];
            for (int r = 0; r < 2; r++) {
                for (int c = 0; c < 2; c++) {
                    for (int row = 0; row < 4; row++) {
                        for (int column = 0; column < 4; column++) {
                            address[2 * r + c] += (1 << (4 * row + column)) * field[row + r][column + c];
                        }
                    }
                }
            }
            codewords = new int[16][4];
            for (int word = 0; word < 4; word++) {
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        codewords[8 * posNeg + t][word] = hash.flatWolframs[posNeg][t][address[word]];
                    }
                }
            }
            for (int numChanges = 1; numChanges < 16; numChanges++) {
                for (int tr = 0; tr < numTrials; tr++) {
                    for (int row = 0; row < 5; row++) {
                        for (int column = 0; column < 5; column++) {
                            changedField[row][column] = field[row][column];
                        }
                    }
                    for (int change = 0; change < numChanges; change++) {
                        changedField[rand.nextInt(0, field.length)][rand.nextInt(0, field[0].length)] ^= 1;
                    }
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            for (int row = 0; row < 4; row++) {
                                for (int column = 0; column < 4; column++) {
                                    innerAddress[2 * r + c] += (1 << (4 * row + column)) * changedField[row + r][column + c];
                                }
                            }
                        }
                    }
                    for (int word = 0; word < 4; word++) {
                        for (int posNeg = 0; posNeg < 2; posNeg++) {
                            for (int t = 0; t < 8; t++) {
                                innerCodewords[8 * posNeg + t][word] = hash.flatWolframs[posNeg][t][address[word]];
                            }
                        }
                    }
                    if (Arrays.equals(innerCodewords, codewords)) {
                        numCollisions[numChanges]++;
                    }
                }
            }
        }
        System.out.println("numCollisions: " + Arrays.toString(numCollisions));
    }

    /**
     * Basic unit of the hash. A power of 2 size square, 4x4 in the paper, with the input in row 0,
     * the columns wrapped - the left boundary rolls over to the right boundary and vice versa. The rest of the rows
     * are ECA output on that wrapped space.
     *
     * @param size  length of the square
     * @param inInt integer value of the input neighborhood
     * @return a square integer array of 1 row of input and the rest ECA output
     */
    public int[][] addressToArray(int size, int inInt) {
        int[][] out = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                out[row][column] = ((inInt >> (size * row + column)) % 2);
            }
        }
        return out;
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
            int[][] grid = addressToArray(4, address);
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
     * Checks random input for codeword array collisions. In isolation, very local collisions happen. When the influence neighborhoods
     * overlap and wrap bits collisions disappear experimentally even without the optional unit cell non-collision loop.
     */
    public void randomizedCollisionChecker() {
        //Address's next door neighbor integer values
        int[][] windowAddresses = new int[65536][4];
        //Rule subset truth tables for every adddress
        int[][][] windowTuples = new int[65536][4][16];
        //Initialize truth tables
        hash.initWolframs();
        //This initializes the truth tables for the sliding window on a single cell, just one row, one column, or one row and one column
        //A shorter version of the same thing in wrappedTileCodeWords()
        for (int address = 0; address < 65536; address++) {
            int[][] grid = addressToArray(4, address);
            for (int r = 0; r < 2; r++) {
                for (int c = 0; c < 2; c++) {
                    int tot = 0;
                    for (int row = 0; row < 4; row++) {
                        for (int col = 0; col < 4; col++) {
                            tot += (1 << (4 * row + col)) * grid[(row + r) % 4][(col + c) % 4];
                        }
                    }
                    windowAddresses[address][2 * r + c] = tot;
                    for (int posNeg = 0; posNeg < 2; posNeg++) {
                        for (int t = 0; t < 8; t++) {
                            windowTuples[address][2 * r + c][8 * posNeg + t] = hash.flatWolframs[posNeg][t][windowAddresses[address][2 * r + c]];
                        }
                    }
                }
            }
        }
        int widthWindow = 5;
        //Random number generator
        Random rand = new Random();
        //Original addresses randomly changed new codewords
        int[][] outerTuples = new int[widthWindow * widthWindow][16];
        //Comparison codewords
        int[][] otherTuple = new int[widthWindow * widthWindow][16];
        //A single addresses
        int[] outerAddresses = new int[25];
        //Total number of errors detected
        int numErrors = 0;
        //A loop counter
        int trial;
        //A wrapped ECA tile
        int[][] grid;
        //
        //
        //
        //These are declared here to save time on the inner loops
        //They're all loop counters
        int power;
        int r;
        int c;
        int row;
        int col;
        int posNeg;
        int t;
        int tot;
        int tr;
        int add;
        //Rate of errors, calculated at end
        double errorRate = 0;
        //Randomly generated address
        int address;
        //Next door addresses
        int[] otherAddress = new int[25];
        //Total number of random samples taken
        long checksDone = 0;
        //These loops originally checked exhaustively and were adapted to random
        //So a loop is a change in the randomness for that loop
        //
        //
        //
        //Outer loop
        for (int addressCounter = 0; addressCounter < 65536 * 256; addressCounter++) {
            //if (addressCounter % 1024 * 32 == 0) System.out.println("address: " + addressCounter + " addresses left: " + (65536*256 - addressCounter));
            //for (int numChanges = 1; numChanges < 16; numChanges++) {
            address = rand.nextInt(0, 65536);
            for (trial = 0; trial < 256; trial++) {
                //initializes the neighborhood
                trial = rand.nextInt(0, 512);
                //ingrid = gridOfInt(4, address);
                grid = new int[5][5];
                for (row = 0; row < 4; row++) {
                    for (col = 0; col < 4; col++) {
                        grid[row][col] = (address >> (4 * row + col)) % 2;
                    }
                }
                for (power = 0; power < 5; power++) {
                    grid[4][power] = ((trial / (1 << power)) % 2);
                }
                for (power = 0; power < 4; power++) {
                    grid[3 - power][4] = ((trial / (1 << (power + 5))) % 2);
                }
                //gets the tuples of its neighbors on the sliding window
                for (r = 0; r < widthWindow; r++) {
                    for (c = 0; c < widthWindow; c++) {
                        tot = 0;
                        for (row = 0; row < 4; row++) {
                            for (col = 0; col < 4; col++) {
                                tot += (1 << (4 * row + col)) * grid[(row + r) % 5][(col + c) % 5];
                            }
                        }
                        outerAddresses[5 * r + c] = tot;
                        for (posNeg = 0; posNeg < 2; posNeg++) {
                            for (t = 0; t < 8; t++) {
                                outerTuples[5 * r + c][8 * posNeg + t] = hash.flatWolframs[posNeg][t][outerAddresses[5 * r + c]];
                            }
                        }
                    }
                }
                //
                //
                //
                //Inner loop
                for (add = 0; add < 256; add++) {
                    add = rand.nextInt(0, 65536);
                    for (tr = 0; tr < 256; tr++) {
                        //initializes the neighborhood
                        tr = rand.nextInt(0, 512);
                        if (add == address && trial == tr) continue;
                        //ingrid = gridOfInt(4, add);
                        grid = new int[5][5];
                        for (row = 0; row < 4; row++) {
                            for (col = 0; col < 4; col++) {
                                grid[row][col] = (add >> (4 * row + col)) % 2;
                            }
                        }
                        for (power = 0; power < 5; power++) {
                            grid[4][power] = ((tr / (1 << power)) % 2);
                        }
                        for (power = 0; power < 4; power++) {
                            grid[3 - power][4] = ((tr / (1 << (power + 5))) % 2);
                        }
                        //gets the tuple codewords for the sliding window on the neighborhood
                        for (r = 0; r < widthWindow; r++) {
                            for (c = 0; c < widthWindow; c++) {
                                tot = 0;
                                for (row = 0; row < 4; row++) {
                                    for (col = 0; col < 4; col++) {
                                        tot += (1 << (4 * row + col)) * grid[(row + r) % 5][(col + c) % 5];
                                    }
                                }
                                otherAddress[5 * r + c] = tot;
                                for (posNeg = 0; posNeg < 2; posNeg++) {
                                    for (t = 0; t < 8; t++) {
                                        otherTuple[5 * r + c][8 * posNeg + t] = hash.flatWolframs[posNeg][t][otherAddress[5 * r + c]];
                                    }
                                }
                            }
                        }
                        checksDone++;
                        //Check for equality and tally
                        if (Arrays.deepEquals(outerTuples, otherTuple)) {
                            numErrors++;
                            double aLoops = 512 * address + trial;
                            double bLoops = 512 * add + tr;
                            errorRate = (double) numErrors / (checksDone);
                            System.out.println("error: " + numErrors + " errors/attempt = " + errorRate);
                            System.out.println("attempts/error = " + (1 / errorRate));
                            System.out.println(address + " " + trial + " " + add + " " + tr);
                        }
                    }
                }
            }
        }
        System.out.println("numErrors = " + numErrors);
        System.out.println("errorRate = " + errorRate);
    }

    /**
     * Proves a negative. Even though the tuple rules are linear the minMax errorScore for a tile depends on more than just the last row.
     * Changes in other rows besides the last row affect the code word as well. This is checked by finding codewords of random grids,
     * then making random changes in the grid anywhere but the last row, finding the codeword for the changed grid,
     * and see if the two codewords remain the same.
     */
    public void checkLastRowWeight() {
        //Number of random changes to make
        int numTrials = 50;
        //Size of grid to check
        int size = 4;
        //Initial grid
        int[][] grid = new int[size][size];
        //Randomly changed grid[][]
        int[][] changedGrid = new int[size][size];
        //Random number generator
        Random rand = new Random();
        //Set of minMax codewords
        int[] tuple = new int[16];
        //Set of changed minMax codewords
        int[] changedTuple = new int[16];
        //Number of attempts that did not change the codeword
        int numSame = 0;
        //Number of attempts that did change the codeword
        int numDifferent = 0;
        //For every attempt
        for (int trial = 0; trial < numTrials; trial++) {
            //Generate a random binary neighborhood
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    grid[row][col] = rand.nextInt(0, 2);
                }
            }
            //Find the minimizing codewords for grid[][]
            for (int t = 0; t < 8; t++) {
                hash.m.findMinimizingCodeword(hash.unpackedList[t], grid);
                tuple[t] = hash.m.lastMinCodeword;
                tuple[8 + t] = hash.m.lastMaxCodeword;
            }
            //Randomly change any row of the grid except the last and rehash
            for (int tr = 0; tr < numTrials; tr++) {
                //Random number of bit changes within the rehashing
                int numChanges = rand.nextInt(0, size);
                //Initialize random changed neighborhood
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        changedGrid[row][col] = grid[row][col];
                    }
                }
                //Make the random changes
                for (int change = 0; change < numChanges; change++) {
                    changedGrid[rand.nextInt(size - 1)][rand.nextInt(size)] ^= 1;
                }
                //Rehash
                for (int t = 0; t < 8; t++) {
                    hash.m.findMinimizingCodeword(hash.unpackedList[t], changedGrid);
                    changedTuple[t] = hash.m.lastMinCodeword;
                    changedTuple[8 + t] = hash.m.lastMaxCodeword;
                }
                //Check against original and tally appropriately
                if (Arrays.equals(tuple, changedTuple)) {
                    numSame++;
                } else {
                    numDifferent++;
                }
            }
        }
        //Display
        System.out.println("numSame = " + numSame);
        System.out.println("numDifferent = " + numDifferent);
        System.out.println("total = " + (numSame + numDifferent));
    }
}

