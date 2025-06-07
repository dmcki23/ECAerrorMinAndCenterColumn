package AlgorithmCode;

import CustomLibrary.CustomArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * This class is has a version of the hash that operates on arrays organized into single bits per location rather than hexadecimal. Functions with RED in
 * them have an extra loop of redundancy beyond the other single bit hex reconstruction of the input raster
 */
public class HashTwoDhex {
    /**
     * Hash subroutines
     */
    public HashTruthTables hashTruthTables;
    /**
     * The entire set of min and max codewords of [0,15,51,85,170,204,240,255]
     */
    public int[][][] flatWolframs = new int[2][8][256 * 256];
    /**
     * The 8 rules referred to in the paper that have an even distribution of codewords
     * and unique codewords for every input
     */
    public int[] unpackedList = new int[]{0, 15, 51, 85, 170, 204, 240, 255};
    /**
     * Hash manager class
     */
    Hash hash;
    /**
     * Used in the inverse functions, does not do anything at the moment, was part of experimenting
     */
    int[][][] outResult;

    /**
     * Sets the manager class
     *
     * @param inHash instance of Hash, the manager class
     */
    public HashTwoDhex(Hash inHash) {
        //hashUtilities = in;
        hash = inHash;
        hashTruthTables = hash.hashRows;
    }

    /**
     * Takes in a 2D array of hashed data in codeword form, then rehashes sets of codewords increasingly far apart in steps of powers of 2, 1 apart 2 apart 4 apart ... 2^n apart
     *
     * @param input a 2D array of hashed data
     * @param rule  one of {0,15,51,85,170,204,240,255}
     * @param depth iterative depth, also the power of how far away its neighbors are
     * @return the input data, rehashed with neighbors 2^depth apart
     */
    public int[][][] ecaHashHex(int[][] input, int rule, int depth, boolean minimize, boolean rowError, int[][][] heatmap) {
        //initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        int[][][] output = new int[depth + 1][rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                output[0][row][col] = input[row][col];
            }
        }
        int layer = (minimize ? 0 : 1) + 2 * (rowError ? 0 : 1);
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //for every row, column location in the image
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    //gets the location's neighborhood
                    int cell = 0;
                    int phasePower = (int) Math.pow(2, d - 1);
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            cell += (int) Math.pow(16, 2 * r + c) * output[d - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                        }
                    }
                    //stores the neighborhood's codeword
                    output[d][row][col] = (hash.allTables[layer][rule][cell]);
                    heatmap[row][col][output[d][row][col]]++;
                }
            }
        }
        return output;
    }

    /**
     * Takes raw binary data and does the initial conversion to one codeword per point covering its
     * area of influence, before comparing them with neighbors in ecaMinMaxTransform()
     *
     * @param input a 2D binary array
     * @param rule  an ECA rule
     * @return a set of 2D arrays with input in layer 0, and layer 1 is the codeword-ified input,
     * the rest is empty
     */
    public int[][] initializeDepthZero(int[][] input, int rule, boolean minimize, boolean rowError) {
        int rows = input.length;
        int cols = input[0].length;
        int[][][] output = new int[4][rows][cols];
        int layer = (minimize ? 0 : 1) + 2 * (rowError ? 0 : 1);
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                output[0][row][col] = input[row][col];
            }
        }
        //for every location in the bitmap
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                //gets its neighborhood
                int cell = 0;
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        cell += (int) Math.pow(2, 4 * r + c) * output[0][(row + r) % rows][(col + c) % cols];
                    }
                }
                //finds the neighborhood's codeword
                //output[1][row][col] = hashTruthTables.minSolutionsAsWolfram[rule][cell];
                output[1][row][col] = hash.allTables[layer][rule][cell];
            }
        }
        return output[1];
    }

    /**
     * A hash inverse for a single codeword set
     *
     * @param input a 2D array of hashed data
     * @param depth depth of hashing of the data
     * @param rule  which rule of the set was used to hash the input (todo needs to be changed to 0-255 ECA rules)
     * @return inverted hashed data
     */
    public int[][] inverseHex(int[][] input, int depth, int rule, boolean minimize, boolean rowError) {
        int neighborDistance = 1 << (depth - 1);
        //neighborDistance = 1;
        int[][][] votes = new int[input.length][input[0].length][4];
        int minimizer = (minimize ? 0 : 1);
        for (int row = 0; row < input.length; row++) {
            for (int col = 0; col < input[0].length; col++) {
                //apply its vote to every location that it influences
                //including itself
                int[][] generatedGuess = hashTruthTables.generateCodewordTile(input[row][col], rule);
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        if (rowError) {
                            if (generatedGuess[r][c] == minimizer) {
                                votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] += (1 << r);
                            } else {
                                votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] -= (1 << r);
                            }
                        } else {
                            if (generatedGuess[r][c] == minimizer) {
                                votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] += (1 << c);
                            } else {
                                votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][c] -= (1 << c);
                            }
                        }
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        int[][] outResult = new int[input.length][input[0].length];
        for (int row = 0; row < input.length; row++) {
            for (int column = 0; column < input[0].length; column++) {
                for (int power = 0; power < 4; power++) {
                    if (votes[row][column][power] >= 0) {
                        outResult[row][column] = 0;
                    } else {
                        outResult[row][column] = 1;
                    }
                }
            }
        }
        return outResult;
    }

    /**
     * Hash inversion
     *
     * @param input A set of hashed data, input[codeword][row][column] where the codeword field contains all 32 minMax row column truth tables
     * @param depth depth of hashing on the input data
     * @return inverted hashed data
     */
    public int[][] inverseHex(int[][][] input, int depth) {
        int neighborDistance = 1 << (depth - 1);
        //neighborDistance = 1;
        int[][][] votes = new int[input[0].length][input[0][0].length][4];
        for (int row = 0; row < input[0].length; row++) {
            for (int col = 0; col < input[0][0].length; col++) {
                for (int posNeg = 0; posNeg < 4; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        //apply its vote to every location that it influences
                        //including itself
                        int[][] generatedGuess = hashTruthTables.generateCodewordTile(input[8 * posNeg + t][row][col], hash.bothLists[(posNeg / 2) % 2][t]);
                        for (int r = 0; r < 4; r++) {
                            for (int c = 0; c < 4; c++) {
                                if (posNeg % 2 == 0) {
                                    //for (int power = 0; power < 4; power++) {
                                    if (generatedGuess[r][c] == (posNeg % 2)) {
                                        votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][c] += (1 << r);
                                    } else {
                                        votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][c] -= (1 << r);
                                    }
                                } else {
                                    if (generatedGuess[r][c] == (posNeg % 2)) {
                                        votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][c] += (1 << c);
                                    } else {
                                        votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][c] -= (1 << c);
                                    }
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
        outResult = new int[16][input[0].length][input[0][0].length];
        int[][] finalOutput = new int[input[0].length][input[0][0].length];
        for (int posNeg = 0; posNeg < 4; posNeg++) {
            for (int t = 0; t < 8; t++) {
                for (int row = 0; row < input[0].length; row++) {
                    for (int column = 0; column < input[0][0].length; column++) {
                        for (int power = 0; power < 4; power++) {
                            if (votes[row][column][power] >= 0) {
                                finalOutput[row][column] = 0;
                            } else {
                                finalOutput[row][column] += (1 << power);
                            }
                        }
                    }
                }
            }
        }
        return finalOutput;
    }

    /**
     * This function experimentally tests the inverse operation and the avalanche property on a hashed bitmap
     *
     * @param filepath name of the file, not including the directory path
     * @throws IOException
     */
    public void verifyInverseAndAvalancheSingleBitsHex(String filepath, boolean hex, int bytes) throws IOException {
        //
        //
        //
        //
        //
        //Initialization
        filepath = "src/ImagesProcessed/" + filepath;
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length() - 4);
        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = new short[1];
        int[] intInRaster = new int[1];
        if (bytes == 2) inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        if (bytes == 4) intInRaster = ((DataBufferInt) inImage.getRaster().getDataBuffer()).getData();
        int depth = (int) (Math.log(inImage.getWidth() * 4) / Math.log(2));
        if (inImage.getWidth() < inImage.getWidth()) {
            depth = (int) (Math.log(inImage.getHeight() * 4) / Math.log(2));
        }
        depth++;
        //depth = 1;
        boolean rowError = true;
        int listIndex = rowError ? 0 : 1;
        boolean minimize;
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        int rows = inImage.getHeight();
        int cols = inImage.getWidth();
        cols *= bytes * 2;
        if (!hex) cols *= 4;
        int powerIncrementor = 4;
        if (!hex) powerIncrementor = 1;
        int[][][] processed = new int[32][rows][cols];
        int[][][] initial = new int[32][rows][cols];
        int[][][] raw = new int[32][rows][cols];
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                for (int rgbbyte = 0; rgbbyte < bytes; rgbbyte++) {
                    for (int power = 0; power < 8; power += powerIncrementor) {
                        for (int posNegt = 0; posNegt < 32 && bytes == 2; posNegt++) {
                            if (!hex) {
                                processed[posNegt][row][bytes * 8 * column + 8 * rgbbyte + power] = ((int) (Math.abs(inRaster[row * inImage.getWidth() + column]) >> (8 * rgbbyte + power)) % 2);
                                raw[posNegt][row][8 * bytes * column + 8 * rgbbyte + power] = processed[posNegt][row][8 * bytes * column + 8 * rgbbyte + power];
                            } else {
                                processed[posNegt][row][bytes * 2 * column + 2 * rgbbyte + power / 4] = ((int) (Math.abs(inRaster[row * inImage.getWidth() + column]) >> (8 * rgbbyte + power)) % 16);
                                raw[posNegt][row][bytes * 2 * column + 2 * rgbbyte + power / 4] = processed[posNegt][row][bytes * 2 * column + 2 * rgbbyte + power / 4];
                            }
                        }
                        for (int posNegt = 0; posNegt < 32 && bytes == 4; posNegt++) {
                            if (!hex) {
                                processed[posNegt][row][bytes * 8 * column + 8 * rgbbyte + power] = ((int) (Math.abs(intInRaster[row * inImage.getWidth() + column]) >> (8 * rgbbyte + power)) % 2);
                                raw[posNegt][row][8 * bytes * column + 8 * rgbbyte + power] = processed[posNegt][row][8 * bytes * column + 8 * rgbbyte + power];
                            } else {
                                processed[posNegt][row][bytes * 2 * column + 2 * rgbbyte + power / 4] = ((int) (Math.abs(intInRaster[row * inImage.getWidth() + column]) >> (8 * rgbbyte + power)) % 16);
                                raw[posNegt][row][bytes * 2 * column + 2 * rgbbyte + power / 4] = processed[posNegt][row][bytes * 2 * column + 2 * rgbbyte + power / 4];
                            }
                        }
                    }
                }
            }
        }
        System.out.println("processed " + Arrays.toString(CustomArray.dimensions(processed)));
        System.out.println("raw " + Arrays.toString(CustomArray.dimensions(raw)));
        int numChanges = 1;
        Random rand = new Random();
        //randomly change the copy
        int[][][] processedForAvalanche = new int[32][raw[0].length][raw[0][0].length];
        //copy the original to another array to make changes to
        for (int t = 0; t < 32; t++) {
            for (int row = 0; row < processed[0].length; row++) {
                for (int column = 0; column < processed[0][0].length; column++) {
                    processedForAvalanche[t][row][column] = processed[t][row][column];
                }
            }
        }
        //make a small number of changes to the copy to track the avalanche property through hash iteration depths
        for (int change = 0; change < numChanges; change++) {
            int randCol = rand.nextInt(0, processed[0][0].length);
            int randRow = rand.nextInt(0, processed[0].length);
            int randPower = rand.nextInt(0, 4);
            for (int t = 0; t < 32; t++) {
                processedForAvalanche[t][randRow][randCol] ^= (1);
            }
        }
        System.out.println("processedForAvalanche " + Arrays.toString(CustomArray.dimensions(processedForAvalanche)));
        for (int posNeg = 0; posNeg < 32 && !hex; posNeg++) {
            minimize = (posNeg / 8) % 2 == 0 ? true : false;
            rowError = (posNeg / 16) % 2 == 0 ? true : false;
            processed[posNeg] = initializeDepthZero(processed[posNeg], hash.bothLists[(posNeg / 16) % 2][posNeg % 8], minimize, rowError);
            processedForAvalanche[posNeg] = initializeDepthZero(processedForAvalanche[posNeg], hash.bothLists[(posNeg / 16) % 2][posNeg % 8], minimize, rowError);
        }
        //make a copy of the initial bitmap raster breakdown for later comparison to hashed versions
        for (int t = 0; t < 32; t++) {
            for (int row = 0; row < initial[0].length; row++) {
                for (int column = 0; column < initial[0][0].length; column++) {
                    initial[t][row][column] = processed[t][row][column];
                }
            }
        }
        //
        //
        //
        //
        //
        //Hashing
        //System.out.println(Arrays.deepToString(processed[0]));
        //Initialize the minMax codeword truth table set
        int[][][] hashSet = new int[32][inImage.getHeight()][inImage.getWidth()];
        int[][][][] hashed = new int[32][depth + 1][inImage.getHeight()][inImage.getWidth() * 16];
        int[][][] abHashSet = new int[32][inImage.getHeight()][inImage.getWidth()];
        int[][][][] hashedAvalanche = new int[32][depth + 1][inImage.getHeight()][inImage.getWidth()];
        int[][] avalancheDifferences = new int[depth + 1][32];
        System.out.println("depth: " + depth);
        int[][][][] heatmapZero = new int[32][rows][cols][16];
        int[][][][] heatmapOne = heatmapZero;
        int[][][][] heatmapTwo = heatmapZero;
        //hash every codeword set
        for (int t = 0; t < 32; t++) {
            listIndex = (t / 16) % 2;
            rowError = (t / 16) % 2 == 0 ? true : false;
            minimize = (t / 8) % 2 == 0 ? true : false;
            hashSet[t] = ecaHashHex(processed[t], hash.bothLists[listIndex][t % 8], depth, minimize, rowError, heatmapOne[t])[depth];
            hashed[t] = ecaHashHex(processed[t], hash.bothLists[listIndex][t % 8], depth, minimize, rowError, heatmapZero[t]);
            hashedAvalanche[t] = ecaHashHex(processedForAvalanche[t], hash.bothLists[listIndex][t % 8], depth, minimize, rowError, heatmapTwo[t]);
            //compare the original and the hashed and display the total differences, this is the minimal avalanche property
            for (int d = 0; d <= depth; d++) {
                for (int row = 0; row < hashed[0][0].length; row++) {
                    for (int column = 0; column < hashed[0][0][0].length; column++) {
                        for (int bit = 0; bit < 4; bit++) {
                            avalancheDifferences[d][t] += (((hashed[t][d][row][column] >> bit) % 2) ^ ((hashedAvalanche[t][d][row][column] >> bit) % 2));
                        }
                    }
                }
            }
        }
        System.out.println("hashSet " + Arrays.toString(CustomArray.dimensions(hashSet)));
        System.out.println("hashed " + Arrays.toString(CustomArray.dimensions(hashed)));
        System.out.println("hashedAvalanche " + Arrays.toString(CustomArray.dimensions(hashedAvalanche)));
        System.out.println("avalancheDifferences " + Arrays.toString(CustomArray.dimensions(avalancheDifferences)));
//        for (int t = 0; t < 32; t++) {
//            System.out.println("t: " + t);
//            for (int row = 0; row < 10; row++) {
//                System.out.println(Arrays.toString(Arrays.copyOfRange(heatmapZero[t][row][0], 0, 16)));
//            }
//        }
//        for (int t = 0; t < 32; t++) {
//            System.out.println("t: " + t);
//            for (int row = 0; row < 10; row++) {
//                System.out.println(Arrays.toString(Arrays.copyOfRange(heatmapOne[t][row][0], 0, 16)));
//            }
//        }
//        for (int t = 0; t < 32; t++) {
//            System.out.println("t: " + t);
//            for (int row = 0; row < 10; row++) {
//                System.out.println(Arrays.toString(Arrays.copyOfRange(heatmapTwo[t][row][0], 0, 16)));
//            }
//        }
        //
        //
        //
        //
        //Rearrange hashes for processing
        int[][][][] hashedRearranged = new int[depth + 2][32][hashed[0][0].length][hashed[0][0][0].length];
        System.out.println("hashedRearranged " + Arrays.toString(CustomArray.dimensions(hashedRearranged)));
        int tot = 0;
        for (int t = 0; t < 32; t++) {
            for (int d = 0; d <= depth; d++) {
                for (int row = 0; row < hashedRearranged[0][0].length; row++) {
                    for (int col = 0; col < hashedRearranged[0][0][0].length; col++) {
                        hashedRearranged[d][t][row][col] = hashed[t][d][row][col];
                        for (int power = 0; power < 4; power++) {
                            tot += ((hashedRearranged[d][t][row][col] >> power) % 2);
                        }
                    }
                }
            }
        }
        System.out.println("hashedRearranged " + Arrays.toString(CustomArray.dimensions(hashedRearranged)));
        System.out.println("total ones: " + tot);
        //
        //
        //
        //
        //
        //Verify inverse, avalanche, and algorithm integrity
        //the first index, 0 = single bit inverse against original single bit expansion, 1 = hex inverse against initial hex initialization, 2 = hex inverse against previous depth hash
        int[][] hashSetDifferences = new int[4][depth + 2];
        int[][][] hashIndividualDifferences = new int[4][depth + 2][32];
        int[][][] ones = new int[4][depth + 2][32];
        int[][] setOnes = new int[4][depth + 2];
        int[][][] hashSetRelativeDifferences = new int[4][depth + 2][depth + 2];
        int[][][] setOnesRelative = new int[4][depth + 2][depth + 2];
        //for every depth
        for (int d = 1; d <= depth; d++) {
            //for every codeword set
            for (int t = 0; t < 32; t++) {
                //for every cell of inverses
                //single bit version
                int[][] individualDifferences = inverse(hashedRearranged[d][t], d, hash.bothLists[(t / 16) % 2][t % 8], (t / 8) % 2 == 0 ? true : false, (t / 16) % 2 == 0 ? true : false);
                //System.out.println("individualDifferences " + Arrays.toString(CustomArray.dimensions(individualDifferences)));
                for (int row = 0; row < individualDifferences.length; row++) {
                    for (int col = 0; col < individualDifferences[0].length; col++) {
                        //sum the discrepancies
                        //sum the 1 bits to verify that the algorithm is doing anything at all
                        hashIndividualDifferences[0][d][t] += individualDifferences[row][col] ^ raw[t][row][col];
                        ones[0][d][t] += individualDifferences[row][col];
                        ones[1][d][t] += raw[t][row][col];
                    }
                }
                //hex version
                individualDifferences = inverseHex(hashedRearranged[d][t], d, hash.bothLists[(t / 16) % 2][t % 8], (t / 8) % 2 == 0 ? true : false, (t / 16) % 2 == 0 ? true : false);
                //System.out.println("individualDifferences " + Arrays.toString(CustomArray.dimensions(individualDifferences)));
                for (int row = 0; row < individualDifferences.length; row++) {
                    for (int col = 0; col < individualDifferences[0].length; col++) {
                        for (int power = 0; power < 4; power++) {
                            //sum the discrepancies
                            //sum the 1 bits to verify that the algorithm is doing anything at all
                            hashIndividualDifferences[2][d][t] += ((individualDifferences[row][col] >> power) % 2) ^ ((hashedRearranged[d - 1][t][row][col] >> power) % 2);
                            hashIndividualDifferences[3][d][t] += ((individualDifferences[row][col] >> power) % 2) ^ ((processed[t][row][col] >> power) % 2);
                            ones[2][d][t] += ((individualDifferences[row][col] >> power) % 2);
                            ones[3][d][t] += (hashedRearranged[d - 1][t][row][col] >> power) % 2;
                        }
                    }
                }
            }
            //for the single bit entire codeword set inverse
            int[][] setInverse = inverseHex(hashedRearranged[d], d);
            System.out.println("setInverse " + Arrays.toString(CustomArray.dimensions(setInverse)));
            System.out.println("d: " + d);
            for (int row = 0; row < setInverse.length; row++) {
                for (int col = 0; col < setInverse[0].length; col++) {
                    for (int power = 0; power < 4; power++) {
                        //sum the discrepancies
                        //sum the 1 bits to verify that the algorithm is doing anything at all
                        hashSetDifferences[0][d] += ((setInverse[row][col] >> power) % 2) ^ ((raw[0][row][col] >> power) % 2);
                        setOnes[0][d] += (setInverse[row][col] >> power) % 2;
                    }
                }
            }
            //for the hex entire codeword set inverse
            //these two layers don't really have anything to compare to directly and are experimental
            //the inverse above can be compared to the array directly from processing the bitmap
            //however after that, it gets processed into separate codeword sets
            //and if you're inverting the entire set back into what? There is no direct comparison
            //because there's nothing hex that isn't codeword specific
            if (hex) continue;
            setInverse = inverse(hashedRearranged[d], d);
            System.out.println("setInverse " + Arrays.toString(CustomArray.dimensions(setInverse)));
            //int[][] otherDinverse = inverse(hashedRearranged)
            for (int row = 0; row < setInverse.length; row++) {
                for (int col = 0; col < setInverse[0].length; col++) {
                    for (int power = 0; power < 1; power++) {
                        //sum the discrepancies
                        //sum the 1 bits to verify that the algorithm is doing anything at all
                        //hashSetDifferences[1][d] += ((setInverse[row][col] >> power) % 2) ^ ((hashed[0][d - 1][row][col] >> power) % 2);
                        hashSetDifferences[1][d] += ((setInverse[row][col] >> power) % 2) ^ ((raw[0][row][col] >> power) % 2);
                        setOnes[1][d] += ((setInverse[row][col] >> power) % 2);
                        setOnes[2][d] += ((raw[0][row][col] >> power) % 2);
                    }
                }
            }
            for (int relativeFrame = 1; relativeFrame <= depth; relativeFrame++) {
                int[][] relativeInverse = inverse(hashedRearranged[relativeFrame], relativeFrame);
                for (int row = 0; row < setInverse.length; row++) {
                    for (int col = 0; col < setInverse[0].length; col++) {
                        for (int power = 0; power < 1; power++) {
                            //sum the discrepancies
                            //sum the 1 bits to verify that the algorithm is doing anything at all
                            //hashSetDifferences[1][d] += ((setInverse[row][col] >> power) % 2) ^ ((hashed[0][d - 1][row][col] >> power) % 2);
                            hashSetRelativeDifferences[1][d][relativeFrame] += ((relativeInverse[row][col] >> power) % 2) ^ ((setInverse[row][col] >> power) % 2);
                            setOnesRelative[1][d][relativeFrame] += ((relativeInverse[row][col] >> power) % 2);
                            setOnesRelative[1][d][relativeFrame] += ((raw[0][row][col] >> power) % 2);
                        }
                    }
                }
            }
        }
        double[][][] hashIndividualDifferencesDouble = new double[4][depth + 2][32];
        double[][] hashSetDifferencesDouble = new double[4][depth + 2];
        int areaInputImage = inImage.getHeight() * inImage.getWidth();
        double[][][] relativeDifferences = new double[4][depth+2][depth+2];
        int num = areaInputImage * bytes * 2;
        if (!hex) num *= 4;
        double[] numBits = new double[]{num, num, num * 4, num * 4};
        for (int layer = 0; layer < 4; layer++) {
            for (int d = 1; d <= depth; d++) {
                for (int t = 0; t < 32; t++) {
                    hashIndividualDifferencesDouble[layer][d][t] = (double) hashIndividualDifferences[layer][d][t] / numBits[layer];
                }
            }
        }
        double[] numBitsSet = new double[]{num * 4, num, num};
        for (int layer = 0; layer < 4; layer++) {
            for (int d = 0; d <= depth; d++) {
                hashSetDifferencesDouble[layer][d] = (double) hashSetDifferences[layer][d] / (double) (numBits[layer]);
                for (int relativeFrame = 0; relativeFrame < d && layer < 2; relativeFrame++) {
                    relativeDifferences[layer][d][relativeFrame] = (double) hashSetRelativeDifferences[layer][d][relativeFrame]/numBitsSet[layer];
                }
            }
        }
        //display
        for (int d = 1; d <= depth; d++) {
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("depth: " + d + " the columns are t = 0..32, all 32 minMaxRowColumn codeword sets\n");
            System.out.println("these follow the total number of ones bits through every depth of iteration\n");
            System.out.println("the ones that the algorithm is doing something at all\n");
//            System.out.println("discrepancies are total errors per depth per codeword and rate is errors/(rows*cols*(4 or 16))\n");
//            System.out.println("this one is single bit single codeword total vs initial bitmap processing\n");
//            System.out.println("discrepancies: " + Arrays.toString(hashIndividualDifferences[0][d]) + "\n");
//            System.out.println("error/bit: " + Arrays.toString(hashIndividualDifferencesDouble[0][d]) + "\n");
//            System.out.println("inverse total ones: " + Arrays.toString(ones[0][d]) + "\n");
//            System.out.println("initial total ones: " + Arrays.toString(ones[1][d]) + "\n");
            System.out.println("this one is hex processed, single codeword inverse, versus frame 0 and the previous frame\n");
            System.out.println("inverse previous frame ones: " + Arrays.toString(ones[2][d]) + "\n");
            System.out.println("initial ones: " + Arrays.toString(ones[3][d]) + "\n");
            System.out.println("discrepancies vs previous frame: " + Arrays.toString(hashIndividualDifferences[2][d]) + "\n");
            System.out.println("error/bit: " + Arrays.toString(hashIndividualDifferencesDouble[2][d]) + "\n");
            System.out.println("discrepancies vs frame 0: " + Arrays.toString(hashIndividualDifferences[3][d]) + "\n");
            System.out.println("error/bit: " + Arrays.toString(hashIndividualDifferencesDouble[3][d]) + "\n");
            System.out.println();
            System.out.println();
            System.out.println();
        }
        System.out.println("=================================================================================");
        System.out.println("=================================================================================");
        System.out.println("=================================================================================");
        System.out.println("=================================================================================");
        System.out.println("these show the total errors per level of hash\n");
        System.out.println("columns here are depth, 0 is the initial image, 1 is the first frame etc...\n");
        System.out.println("complete sets\n");
        System.out.println("this one is the entire codeword set back to single bits versus the original\n");
        System.out.println("discrepancies: " + Arrays.toString(hashSetDifferences[0]) + "\n");
        System.out.println("error/bit: " + Arrays.toString(hashSetDifferencesDouble[0]) + "\n");
        System.out.println("setOnes[0]: " + Arrays.toString(setOnes[0]) + "\n");
        System.out.println("setOnes[1]: " + Arrays.toString(setOnes[1]) + "\n");
        System.out.println();
        for (int d = 1; d <= depth; d++) {
            System.out.println("==================================================================================");
            System.out.println("d: " + d);
            System.out.println("these show the total errors per level of hash\n");
            System.out.println("columns here are depth, 0 is the initial image, 1 is the first frame etc...\n");
            System.out.println("complete sets\n");
            System.out.println("this one is the entire codeword set back to single bits versus the original\n");
            System.out.println("discrepancies: " + Arrays.toString(hashSetRelativeDifferences[0][d]) + "\n");
            System.out.println("error/bit: " + Arrays.toString(relativeDifferences[0][d]) + "\n");
            System.out.println("setOnes[0]: " + Arrays.toString(setOnesRelative[0][d]) + "\n");
            System.out.println();
            System.out.println();
        }
        //
        //
        //
        //
        //Another set of inverse, algorithm inverse checks
        //compare the original and the hashed and display
        for (int d = 0; d <= depth; d++) {
            System.out.println("avalancheDifferences[" + d + "]: " + Arrays.toString(avalancheDifferences[d]));
        }
        int[][] onesHashedRearranged = new int[depth + 2][32];
        for (int d = 0; d <= depth; d++) {
            for (int t = 0; t < 32; t++) {
                for (int row = 0; row < hashedRearranged[0][0].length; row++) {
                    for (int col = 0; col < hashedRearranged[0][0].length; col++) {
                        for (int power = 0; power < 4; power++) {
                            onesHashedRearranged[d][t] += (hashedRearranged[d][t][row][col] >> power) % 2;
                        }
                    }
                }
            }
        }
        for (int d = 0; d <= depth; d++) {
            System.out.println("hashedRearranged[" + d + "]: " + Arrays.toString(onesHashedRearranged[d]) + "\n");
        }
    }

    /**
     * A hash inverse for a single codeword set
     *
     * @param input a 2D array of hashed data
     * @param depth depth of hashing of the data
     * @param rule  which rule of the set was used to hash the input (todo needs to be changed to 0-255 ECA rules)
     * @return inverted hashed data
     */
    public int[][] inverse(int[][] input, int depth, int rule, boolean minimize, boolean rowError) {
        int neighborDistance = 1 << (depth - 1);
        //neighborDistance = 1;
        int[][][] votes = new int[input.length][input[0].length][1];
        int minimizer = (minimize ? 0 : 1);
        for (int row = 0; row < input.length; row++) {
            for (int col = 0; col < input[0].length; col++) {
                //apply its vote to every location that it influences
                //including itself
                int[][] generatedGuess = hashTruthTables.generateCodewordTile(input[row][col], rule);
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        if (rowError) {
                            if (minimize) {
                                if (generatedGuess[r][c] == 0)
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] += (1 << r);
                                else
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] -= (1 << r);
                            } else {
                                if (generatedGuess[r][c] == 1)
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] += (1 << r);
                                else
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] -= (1 << r);
                            }
                        } else {
                            if (minimize) {
                                if (generatedGuess[r][c] == 0)
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] += (1 << c);
                                else
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] -= (1 << c);
                            } else {
                                if (generatedGuess[r][c] == 1)
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] += (1 << c);
                                else
                                    votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * ((r) % 2)) % input[0].length][0] -= (1 << c);
                            }
                        }
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        int[][] outResult = new int[input.length][input[0].length];
        for (int row = 0; row < input.length; row++) {
            for (int column = 0; column < input[0].length; column++) {
                for (int power = 0; power < 1; power++) {
                    if (votes[row][column][power] >= 0) {
                        outResult[row][column] += 0;
                    } else {
                        outResult[row][column] += (1 << power);
                    }
                }
            }
        }
        return outResult;
    }

    /**
     * Hash inversion
     *
     * @param input A set of hashed data, input[codeword][row][column] where the codeword field contains all 32 minMax row column truth tables
     * @param depth depth of hashing on the input data
     * @return inverted hashed data
     */
    public int[][] inverse(int[][][] input, int depth) {
        int neighborDistance = 1 << (depth - 1);
        //neighborDistance = 1;
        int[][][] votes = new int[input[0].length][input[0][0].length][1];
        for (int row = 0; row < input[0].length; row++) {
            for (int col = 0; col < input[0][0].length; col++) {
                for (int posNeg = 0; posNeg < 4; posNeg++) {
                    tLoop:
                    for (int t = 0; t < 8; t++) {
                        //System.out.println("betterThanHalf: " + betterThanHalf[8*posNeg+t]);
                        //apply its vote to every location that it influences
                        //including itself
                        int[][] generatedGuess = hashTruthTables.generateCodewordTile(input[8 * posNeg + t][row][col], hash.bothLists[(posNeg / 2) % 2][t]);
                        for (int r = 0; r < 4; r++) {
                            for (int c = 0; c < 4; c++) {
                                if (posNeg < 2) {
                                    if (posNeg % 2 == 0) {
                                        if (generatedGuess[r][c] == 0)
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] += (1 << r);
                                        else
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] -= (1 << r);
                                    } else {
                                        if (generatedGuess[r][c] == 1)
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] += (1 << r);
                                        else
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] -= (1 << r);
                                    }
                                } else {
                                    if (posNeg % 2 == 0) {
                                        if (generatedGuess[r][c] == 0)
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] += (1 << c);
                                        else
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] -= (1 << c);
                                    } else {
                                        if (generatedGuess[r][c] == 1)
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] += (1 << c);
                                        else
                                            votes[(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * ((r) % 2)) % input[0][0].length][0] -= (1 << c);
                                    }
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
        outResult = new int[16][input[0].length][input[0][0].length];
        int[][] finalOutput = new int[input[0].length][input[0][0].length];
        for (int posNeg = 0; posNeg < 1; posNeg++) {
            for (int t = 0; t < 1; t++) {
                for (int row = 0; row < input[0].length; row++) {
                    for (int column = 0; column < input[0][0].length; column++) {
                        for (int power = 0; power < 1; power++) {
                            if (votes[row][column][power] >= 0) {
                                finalOutput[row][column] += 0;
                            } else {
                                finalOutput[row][column] += (1 << power);
                            }
                        }
                    }
                }
            }
        }
        return finalOutput;
    }
}




