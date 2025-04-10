package AlgorithmCode;

import CustomLibrary.CustomArray;

public class HashUtilities {
    /**
     * Middle layer of transform code
     */
    public HashTransform hash = new HashTransform();
    int[] truthTable;
    int[] singleChangeTruthTable;
    int[][] localChanges = new int[65536][16];
    int[][] twoChanges;
    int[][][] everything = new int[16][16][4];
    int[][] shiftedSingles;
    int[][][] shiftedDoubles;
    int[][][] shiftedEverything;
    public void exploreTwoChanges(int size) {
        hash.initWolframs();
        combinationsTwoChanges(size);
        int[][][] table = new int[4][size*size][size*size];
        for (int v = 0; v < 4; v++) {
            for (int oneChange = 0; oneChange < size*size; oneChange++) {
                for (int nextChange = 0; nextChange < size*size; nextChange++) {
                    table[v][oneChange][nextChange] = everything[oneChange][nextChange][v];
                }
            }
            CustomArray.plusArrayDisplay(table[v], true, false, "table " + v);
        }
    }

    public int[][] generateFunctionTile(int size) {
        int[][] out = new int[size][size];
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
                //changed[row][col] = out[row][col];
            }
        }
        return out;
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
        for (int address = 0; address < 1; address++) {
            for (int change = 0; change < 16; change++) {
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < 4; col++) {
                        changed[row][col] = out[row][col];
                    }
                }
                changed[change / size][change % size] ^= 1;
                int[][][] preHash = new int[16][size][size];
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        preHash[t] = hash.initializeDepthZero(changed, hash.unpackedList[t])[1];
                        preHash[8 + t] = hash.initializeDepthMax(changed, hash.unpackedList[t])[1];
                    }
                }
                int[][][] hashed = new int[16][size][size];
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        hashed[t] = hash.ecaMinTransform(preHash[t], hash.unpackedList[t], 1)[1];
                        hashed[8 + t] = hash.ecaMaxTransform(preHash[8 + t], hash.unpackedList[t], 1)[1];
                    }
                }
                //int[][][] cinverted = hash.reconstructDepthD(chashed, 1);
                int[][][] inverted = hash.reconstructDepthD(hashed, 1);
                int[][] finalized = hash.hashInverseDepth0(inverted, 1, 0);
                //int[][] cfinalized = hash.hashInverseDepth0(cinverted, 1, 0);
                int[][] shifted = new int[size][size];
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        shifted[row][col] = finalized[(row + size / 2) % size][(col + size / 2) % size];
                    }
                }
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        singleChangeTruthTable[change] += (1 << (size * row + col) * shifted[row][col]);
                    }
                }
            }
        }
    }
}
