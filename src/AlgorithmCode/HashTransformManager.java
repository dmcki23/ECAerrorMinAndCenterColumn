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
public class HashTransformManager {
    /**
     * Middle layer of transform code
     */
    HashTransform fmt = new HashTransform();

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
        fmt.initWolframs();
        int[][][][] depthChart = new int[2][8][in.length][in[0].length];
        //puts the input data as layer 0 of the output data
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                System.out.println("posNeg: " + posNeg + " t: " + t);
                depthChart[posNeg][t] = fmt.initializeDepthZero(in, fmt.unpackedList[t])[1];
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
        fmt.initWolframs();
        int totDifferent = 0;
        //For every 8-tuple element
        for (int posNeg = 0; posNeg < 1; posNeg++) {
            for (int t = 0; t < 8; t++) {
                //For all possible values
                for (int input = 0; input < 16; input++) {
                    //Compare a single codeword tile's voting pattern
                    //to that codeword's Hadamard parity
                    int[][] cell = fmt.m.generateWrappedECAsquare(input, fmt.unpackedList[t]);
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
        fmt.initWolframs();
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
                        codewords[8 * posNeg + t][word] = fmt.flatWolframs[posNeg][t][address[word]];
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
                                innerCodewords[8 * posNeg + t][word] = fmt.flatWolframs[posNeg][t][address[word]];
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
    public int[][] gridOfInt(int size, int inInt) {
        int[][] out = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                out[row][column] = ((inInt >> (4 * row + column)) % 2);
            }
        }
        return out;
    }

    /**
     * Verifies the optional non-collision loop mentioned in the paper. This checks for collisions between all 65536 minMax 8-tuples,
     * when the codeword is wrapped with itself. Instead of just collisions between sets of codewords, its checking for collisions
     * between sets of codeword neighborhoods. The 0-65536 value that the truth table is addressed by is a binary 4x4 array.
     */
    public void wrappedTileCodewords() {
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
        fmt.initWolframs();
        //For every address find its wrapped neighborhood set's integer values
        for (int address = 0; address < 65536; address++) {
            //Generate the address's neighborhood array
            int[][] grid = gridOfInt(4, address);
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
                            slidingTuples[address][4 * r + c][8 * posNeg + t] = fmt.flatWolframs[posNeg][t][slidingAddresses[address][4 * r + c]];
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
    }

    /**
     * Checks random input for codeword collisions
     */
    public void randomizedCollisionChecker() {
        //Address's next door neighbor integer values
        int[][] slidingAddresses = new int[65536][4];
        //Rule subset truth tables for every adddress
        int[][][] slidingTuples = new int[65536][4][16];
        //Initialize truth tables
        fmt.initWolframs();
        //This initializes the truth tables for the sliding window on a single cell, just one row, one column, or one row and one column
        //A shorter version of the same thing in wrappedTileCodeWords()
        for (int address = 0; address < 65536; address++) {
            int[][] grid = gridOfInt(4, address);
            for (int r = 0; r < 2; r++) {
                for (int c = 0; c < 2; c++) {
                    int tot = 0;
                    for (int row = 0; row < 4; row++) {
                        for (int col = 0; col < 4; col++) {
                            tot += (1 << (4 * row + col)) * grid[(row + r) % 4][(col + c) % 4];
                        }
                    }
                    slidingAddresses[address][2 * r + c] = tot;
                    for (int posNeg = 0; posNeg < 2; posNeg++) {
                        for (int t = 0; t < 8; t++) {
                            slidingTuples[address][2 * r + c][8 * posNeg + t] = fmt.flatWolframs[posNeg][t][slidingAddresses[address][2 * r + c]];
                        }
                    }
                }
            }
        }
        //Random number generator
        Random rand = new Random();
        //Original addresses randomly changed new codewords
        int[][] changedSlidingTuples = new int[25][16];
        //Comparison codewords
        int[][] otherTuple = new int[25][16];
        //A single addresses
        int[] changedSlidingAddresses = new int[25];
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
        //These loops originally checked exhaustively and were adapted to random
        //So a loop is a change in the randomness for that loop
        //
         //
         //
         //Outer loop
        for (int addressCounter = 0; addressCounter < 65536; addressCounter++) {
            if (addressCounter % 256 * 16 == 0) System.out.println("address: " + addressCounter);
            //for (int numChanges = 1; numChanges < 16; numChanges++) {
            address = rand.nextInt(0, 65536);
            for (trial = 0; trial < 512; trial++) {
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
                for (r = 0; r < 5; r++) {
                    for (c = 0; c < 5; c++) {
                        tot = 0;
                        for (row = 0; row < 4; row++) {
                            for (col = 0; col < 4; col++) {
                                tot += (1 << (4 * row + col)) * grid[(row + r) % 5][(col + c) % 5];
                            }
                        }
                        changedSlidingAddresses[5 * r + c] = tot;
                        for (posNeg = 0; posNeg < 2; posNeg++) {
                            for (t = 0; t < 8; t++) {
                                changedSlidingTuples[5 * r + c][8 * posNeg + t] = fmt.flatWolframs[posNeg][t][changedSlidingAddresses[5 * r + c]];
                            }
                        }
                    }
                }
                //
                 //
                 //
                 //Inner loop
                for (add = 0; add < 65536; add++) {
                    add = rand.nextInt(0, 65536);
                    for (tr = 0; tr < 512; tr++) {
                        //initializes the neighborhood
                        tr = rand.nextInt(0, 65536);
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
                        for (r = 0; r < 5; r++) {
                            for (c = 0; c < 5; c++) {
                                tot = 0;
                                for (row = 0; row < 4; row++) {
                                    for (col = 0; col < 4; col++) {
                                        tot += (1 << (4 * row + col)) * grid[(row + r) % 5][(col + c) % 5];
                                    }
                                }
                                otherAddress[5 * r + c] = tot;
                                for (posNeg = 0; posNeg < 2; posNeg++) {
                                    for (t = 0; t < 8; t++) {
                                        otherTuple[5 * r + c][8 * posNeg + t] = fmt.flatWolframs[posNeg][t][otherAddress[5 * r + c]];
                                    }
                                }
                            }
                        }
                        //Check for equality and tally
                        if (Arrays.deepEquals(changedSlidingTuples, otherTuple)) {
                            numErrors++;
                            double aLoops = 512 * address + trial;
                            double bLoops = 512 * add + tr;
                            errorRate = numErrors / (aLoops * (65536 * 512) + bLoops);
                            System.out.println("error: " + numErrors + " errors/attempt = " + errorRate);
                            System.out.println("attempts/error = " + (1 / errorRate));
                        }
                    }
                }
            }
        }
        System.out.println("numErrors = " + numErrors);
    }

    /**
     * Shows a negative. Even though the tuple rules are linear the minMax errorScore for a tile depends on more than the last row.
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
                fmt.m.findMinimizingCodeword(fmt.unpackedList[t], grid);
                tuple[t] = fmt.m.lastMinCodeword;
                tuple[8 + t] = fmt.m.lastMaxCodeword;
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
                    fmt.m.findMinimizingCodeword(fmt.unpackedList[t], changedGrid);
                    changedTuple[t] = fmt.m.lastMinCodeword;
                    changedTuple[8 + t] = fmt.m.lastMaxCodeword;
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
        System.out.println(numSame);
        System.out.println(numDifferent);
        System.out.println((numSame + numDifferent));
    }
}

