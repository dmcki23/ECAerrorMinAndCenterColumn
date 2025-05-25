package AlgorithmCode;

import CustomLibrary.CustomArray;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Designed to analyze and verify various properties of codeword mappings,
 * particularly focused on identifying and evaluating hash collisions within 4x4 neighborhoods.
 * It is centered around concepts like uniqueness, error scores, and collisions, with tools to assess
 * and validate these aspects experimentally and theoretically through verification checks.
 * <p>
 * This class provides methods to examine codeword properties, their uniqueness under different conditions,
 * and the relationship between codeword neighborhoods and their resulting behavior.
 */
public class HashCollisions {
    /**
     * Primary hash class, managing the other hash classes
     */
    public Hash hash;

    /**
     * Sets the manager function
     * @param inHash main Hash algorithm class
     * @throws IOException
     */
    public HashCollisions(Hash inHash) throws IOException {
        hash = inHash;
    }


    /**
     * This function takes all 16 min max codewords for each of the 65536 4x4 neighborhoods
     * and compares them with all other tuple sets in the truth table to check for uniqueness.
     * On their own they are not unique, but the whole set of 32 codewords is for any input.
     *
     * @return whether the tuple sets in the truth tables are distinct
     */
    public boolean checkTupleUniqueness() {
        boolean out = true;
        int[][] innerOuterTuples = new int[4][16];
        int numSame = 0;
        for (int address = 0; address < 65536; address++) {
            if (address % 256 == 0) System.out.println("address: " + address);
            for (int t = 0; t < 8; t++) {
                innerOuterTuples[0][t] = hash.flatWolframs[0][t][address];
                innerOuterTuples[0][t + 8] = hash.flatWolframs[1][t][address];
                innerOuterTuples[0][t] = hash.flatWolframs[2][t][address];
                innerOuterTuples[0][t + 8] = hash.flatWolframs[3][t][address];
            }
            addLoop:
            for (int add = 0; add < address; add++) {
                for (int t = 0; t < 8; t++) {
                    innerOuterTuples[1][t] = hash.flatWolframs[0][t][add];
                    innerOuterTuples[1][t + 8] = hash.flatWolframs[1][t][add];
                    innerOuterTuples[0][t] = hash.flatWolframs[2][t][add];
                    innerOuterTuples[0][t + 8] = hash.flatWolframs[3][t][add];
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

    /**
     * The method checks for collisions between codewords derived from a 5x5 binary array
     * under various random modifications. The collisions are evaluated based on whether
     * the derived codewords from the original and randomly modified arrays match.
     * <p>
     * The process involves:
     * 1. Generating a 5x5 binary random array (`field`).
     * 2. Computing a set of neighborhood-based address values and their corresponding codewords.
     * 3. Introducing varying levels of random changes (1 to 15) to the original array to produce a new array (`changedField`).
     * 4. Calculating neighborhood-based address values and codewords for the modified array.
     * 5. Comparing the codewords of the original and changed arrays to count the collisions
     * for each level of modifications through `numCollisions`.
     * 6. Outputting the collision statistics in the form of an array.
     * <p>
     * This method helps to analyze the robustness of the hashing or coding mechanism
     * when subjected to random local changes in input data.
     */
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
        //
        for (int trial = 0; trial < numTrials; trial++) {
            //Generate and hash a random tile
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
            //randomly change the original array and hash it
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
                    //If equal, is a collision
                    if (Arrays.equals(innerCodewords, codewords)) {
                        numCollisions[numChanges]++;
                    }
                }
            }
        }
        System.out.println("numCollisions: " + Arrays.toString(numCollisions));
    }

    /**
     * Checks rates of collision in the compression version of the hash
     */
    public void checkCompressionCollisions() {
        Random rand = new Random();
        int samples = 1000;
        int size = 1000;
        int[] field = new int[size];
        int[] fieldTwo = new int[size];
        int depth = 1;
        int tot = 0;
        int same = 0;
        int different = 0;
        for (int t = 0; t < 8; t++) {
            for (int layer = 0; layer < 4; layer++) {
                same = 0;
                different = 0;
                tot = 0;
                for (int sample = 0; sample < samples; sample++) {
                    //Generate and hash a random tile
                    for (int index = 0; index < size; index++) {
                        field[index] = rand.nextInt(0, 2);
                    }
                    int[] fieldHash = hash.hashOneD.hashArrayCompression(field, hash.bothLists[layer][t], depth, (layer % 2 == 0) ? true : false, ((layer / 2) % 2 == 0) ? true : false);
                    //randomly change the original array and hash it
                    for (int s = 0; s < 100; s++) {
                        for (int index = 0; index < size; index++) {
                            fieldTwo[index] = rand.nextInt(0, 2);
                        }
                        int[] fieldHashTwo = hash.hashOneD.hashArrayCompression(fieldTwo, hash.bothLists[layer][t], depth, (layer % 2 == 0) ? true : false, ((layer / 2) % 2 == 0) ? true : false);
                        //If equal, is a collision
                        if (Arrays.equals(fieldHash, fieldHashTwo)) {
                            same++;
                        } else {
                            different++;
                        }
                        tot++;
                    }
                }
                System.out.println("t: " + t + " layer: " + layer + " same: " + same + " different: " + different + " tot: " + tot);
            }
        }
    }

    /**
     * Checks if the binary complements of the truth tables are equal to any of the other rules, another symmetry besides the left-right-black-white process
     */
    public void checkTriplets() {
        hash.initWolframs();
        int same = 0;
        int diff = 0;
        int[][] symmetries = generateFourBitLRBWsymmetry();
        for (int layer = 0; layer < 4; layer++) {
            for (int address = 0; address < 65536; address++) {
                for (int t = 1; t < 4; t++) {
                    if (hash.flatWolframs[layer][t][address] != (15 - hash.flatWolframs[layer][t + 4][address])) {
                        diff++;
                    } else {
                        same++;
                    }
                }
            }
        }
        System.out.println("same: " + same + " diff: " + diff);
    }

    /**
     * The symmetries checked here are the same left-right-black-white symmetries of the 88 independent ECA rules applied to 4 bits instead of 8
     */
    public void checkCodewordSymmetry() {
        hash.initWolframs();
        int same = 0;
        int diff = 0;
        int[][] symmetries = generateFourBitLRBWsymmetry();
        System.out.println(Arrays.deepToString(symmetries));
        int[][] equals = new int[4 * 8][4 * 8];
        //Checks to see whether any of the codeword table symmetries are equal to each other using left-right-black-white symmetries
        for (int layer = 0; layer < 4; layer++) {
            for (int blayer = 0; blayer < 4; blayer++) {
                for (int t = 0; t < 8; t++) {
                    for (int to = 0; to < 8; to++) {
                        cLoop:
                        for (int c = 0; c < 4; c++) {
                            for (int address = 0; address < 65536; address++) {
                                if (symmetries[hash.flatWolframs[layer][t][address]][0] != (symmetries[hash.flatWolframs[blayer][to][address]][c]))
                                    continue cLoop;
                            }
                            equals[8 * layer + t][8 * blayer + to] = c;
                        }
                    }
                }
            }
        }
        //Checks to see whether any of the codeword table symmetries are equal to each other using the binary complement
        for (int layer = 0; layer < 4; layer++) {
            for (int blayer = 0; blayer < 4; blayer++) {
                for (int t = 0; t < 8; t++) {
                    toLoop:
                    for (int to = 0; to < 8; to++) {
                        cLoop:
                        for (int address = 0; address < 65536; address++) {
                            if (hash.flatWolframs[layer][t][address] != (15 - hash.flatWolframs[layer][t][address]))
                                continue toLoop;
                        }
                        equals[8 * layer + t][8 * blayer + to] += 2;
                    }
                }
            }
        }
        //Checks to see whether any of the codeword table symmetries are equal to each other using a logic gate to combine
        for (int layer = 0; layer < 4; layer++) {
            for (int blayer = 0; blayer < 4; blayer++) {
                for (int t = 0; t < 8; t++) {
                    toLoop:
                    for (int to = 0; to < 8; to++) {
                        cLoop:
                        for (int gate = 0; gate < 16; gate++) {
                            for (int address = 0; address < 65536; address++) {
                                int tot = 0;
                                for (int power = 0; power < 4; power++) {
                                    int a = (hash.flatWolframs[layer][t][address] >> power) % 2;
                                    int b = (hash.flatWolframs[blayer][to][address] >> power) % 2;
                                    int c = (gate>>(a + 2 * b))%2;
                                    tot += (1 << power) * c;
                                }
                                if (hash.flatWolframs[layer][t][address] != (15 - hash.flatWolframs[layer][t][address]))
                                    continue toLoop;
                            }
                            equals[8 * layer + t][8 * blayer + to] += 2;
                        }
                    }
                }
            }
        }
        CustomArray.plusArrayDisplay(equals, true, true, "Symmetries");
        //Hadamard hadamard = new Hadamard();
        //int[][] squared = hadamard.matrixMultiply(equals, equals);
        //CustomArray.plusArrayDisplay(squared, true, true, "Squared Symmetries");
        //squared = hadamard.matrixMultiply(squared, squared);
        //CustomArray.plusArrayDisplay(squared, true, true, "Squared Symmetries squared");
        //
         //
         //Checks to see if there are groups similar to the 88 left-right-black-white ECA rule symmetries
        int[][] groups = new int[32][32];
        int numUnique = 0;
        rowLoop:
        for (int row = 0; row < 32; row++) {
            checkLoop:
            for (int check = 0; check < numUnique; check++) {
                if (Arrays.equals(groups[check], groups[row])) {
                    continue rowLoop;

                }
            }
            groups[numUnique] = equals[row];
            numUnique++;
        }
        for (int g = 0; g < numUnique; g++) {
            System.out.println("Group " + g + ": " + Arrays.toString(groups[g]));
        }
        System.out.println("numUnique: " + numUnique);
    }

    /**
     * Applies the same algorithm as the 88 independent ECA left-right-black-white rules to 4 bits
     * The left-right symmetry reverses the order of addressing bits
     * The black-white symmetry reverses the truth table and taking the complement
     * The left-right-black-white symmetry does both of these operations
     * @return out[codeword][symmetry], where codeword is the input, and symmetry is a number between 0-3 representing the 4 symmetries
     */
    public int[][] generateFourBitLRBWsymmetry() {
        int[][] out = new int[16][4];
        for (int word = 0; word < 16; word++) {
            out[word][0] = word;
            //Left right symmetry
            int tot = 0;
            for (int power = 0; power < 4; power++) {
                int a = power % 2;
                int b = (power / 2) % 2;
                int c = 2 * a + b;
                tot += (1 << power) * ((word >> power) % 2);
            }
            out[word][1] = tot;
            //Black white symmetry
            tot = 0;
            for (int power = 0; power < 4; power++) {
                tot += (1 << (3 - power)) * ((word >> power) % 2);
            }
            out[word][2] = tot;
            //Both symmetries
            tot = 0;
            for (int power = 0; power < 4; power++) {
                int a = power % 2;
                int b = (power / 2) % 2;
                int c = 2 * a + b;
                tot += (1 << (3 - power)) * ((out[word][1] >> power) % 2);
            }
            out[word][3] = tot;
        }
        return out;
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
            int[][] grid = hash.hashRows.addressToArray(4, address);
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
     * Proves a negative. Even though the tuple rules are linear, the minMax errorScore for a tile depends on more than just the last row.
     * Changes in other rows besides the last row affect the code word as well. This is checked by finding codewords of random grids,
     * then making random changes in the grid anywhere but the last row, finding the codeword for the changed grid,
     * and seeing if the two codewords remain the same.
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
                hash.hashRows.findMinimizingCodeword(hash.rowList[t], grid);
                tuple[t] = hash.hashRows.lastMinCodeword;
                tuple[8 + t] = hash.hashRows.lastMaxCodeword;
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
                    hash.hashRows.findMinimizingCodeword(hash.rowList[t], changedGrid);
                    changedTuple[t] = hash.hashRows.lastMinCodeword;
                    changedTuple[8 + t] = hash.hashRows.lastMaxCodeword;
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

