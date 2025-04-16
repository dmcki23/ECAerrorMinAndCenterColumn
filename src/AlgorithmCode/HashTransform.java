package AlgorithmCode;

import CustomLibrary.CustomArray;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;

/**
 *
 */
public class HashTransform {
    HashUtilities hashUtilities;
    public HashTransform() {
        //hashUtilities = in;
    }
    /**
     * Hash subroutines
     */
    public HashTruthTables m = new HashTruthTables();

    /**
     * The entire set of min and max codewords of [0,15,51,85,170,204,240,255]
     */
    public int[][][] flatWolframs = new int[2][8][256 * 256];
    /**
     * The 8 rules referred to in the paper that have an even distribution of codewords
     * and unique codewords for every input
     */
    public int[] unpackedList = new int[]{0, 15, 51, 85, 170, 204, 240, 255};
    public OneDHashTransform oneDHashTransform = new OneDHashTransform(this);

    /**
     * Takes raw binary data and does the initial conversion to one codeword per point covering its
     * area of influence, before comparing them with neighbors in ecaMinMaxTransform()
     *
     * @param input a 2D binary array
     * @param rule  an ECA rule
     * @return a set of 2D arrays with input in layer 0, and layer 1 is the codeword-ified input,
     * the rest is empty
     */
    public int[][][] initializeDepthZero(int[][] input, int rule) {
        int rows = input.length;
        int cols = input[0].length;
        int[][][] deepInput = new int[4][rows][cols];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        //for every location in the bitmap
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                //gets its neighborhood
                int cell = 0;
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        cell += (int) Math.pow(2, 4 * r + c) * deepInput[0][(row + r) % rows][(col + c) % cols];
                    }
                }
                //finds the neighborhood's codeword
                deepInput[1][row][col] = m.minSolutionsAsWolfram[rule][cell];
            }
        }
        return deepInput;
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
    public int[][][] initializeDepthZeroMax(int[][] input, int rule) {
        int rows = input.length;
        int cols = input[0].length;
        int[][][] deepInput = new int[4][rows][cols];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        //for every location in the bitmap
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                //gets its neighborhood
                int cell = 0;
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        cell += (int) Math.pow(2, 4 * r + c) * deepInput[0][(row + r) % rows][(col + c) % cols];
                    }
                }
                //finds the neighborhood's codeword
                deepInput[1][row][col] = m.maxSolutionsAsWolfram[rule][cell];
            }
        }
        return deepInput;
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
    public int[][][] initializeDepthMax(int[][] input, int rule) {
        int rows = input.length;
        int cols = input[0].length;
        int[][][] deepInput = new int[4][rows][cols];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        //for every location in the bitmap
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                //gets its neighborhood
                int cell = 0;
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        cell += (int) Math.pow(2, 4 * r + c) * deepInput[0][(row + r) % rows][(col + c) % cols];
                    }
                }
                //finds the neighborhood's codeword
                deepInput[1][row][col] = m.maxSolutionsAsWolfram[rule][cell];
            }
        }
        return deepInput;
    }

    /**
     * Takes in a 2D array of hashed data in codeword form, then rehashes sets of codewords increasingly far apart in steps of powers of 2, 1 apart 2 apart 4 apart ... 2^n apart
     *
     * @param input a 2D array of hashed data
     * @param rule  one of {0,15,51,85,170,204,240,255}
     * @param depth iterative depth, also the power of how far away its neighbors are
     * @return the input data, rehashed with neighbors 2^depth apart
     */
    public int[][][] ecaMinTransform(int[][] input, int rule, int depth) {
        //initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        int[][][] output = new int[depth + 2][rows][cols];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                output[0][row][col] = input[row][col];
            }
        }
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //This is to skip the negative flip on the integer
            //it would not be necessary with unsigned integers
            //if (d%32 == 31) d++;
            //for every (row,column) location in the image
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    //gets its neighborhood
                    int cell = 0;
                    int phasePower = (1 << ((d - 1) % 16));
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            cell += (int) Math.pow(16, 2 * r + c) * output[d - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                        }
                    }
                    //stores the neighborhood's codeword
                    output[d][row][col] = (m.minSolutionsAsWolfram[rule][cell]);
                }
            }
        }
        return output;
    }

    /**
     * Takes in a 2D array of hashed data in codeword form, then rehashes sets of codewords increasingly far apart in steps of powers of 2, 1 apart 2 apart 4 apart ... 2^n apart
     *
     * @param input a 2D array of hashed data
     * @param rule  one of {0,15,51,85,170,204,240,255}
     * @param depth iterative depth, also the power of how far away its neighbors are
     * @return the input data, rehashed with neighbors 2^depth apart
     */
    public int[][][] ecaMinTransformRGBrowNorm(int[][] input, int rule, int depth) {
        //initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        int[][][] output = new int[depth + 2][rows][cols];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                output[0][row][col] = input[row][col];
            }
        }
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //This is to skip the negative flip on the integer
            //it would not be necessary with unsigned integers
            //if (d%32 == 31) d++;
            //for every (row,column) location in the image
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    //gets its neighborhood
                    int cell = 0;
                    int phasePower = (1 << ((d - 1) % 16));
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            cell += (int) Math.pow(16, 2 * r + c) * output[d - 1][(row ) % rows][(col + (2*r+c)) % cols];
                        }
                    }
                    //stores the neighborhood's codeword
                    output[d][row][col] = (m.minSolutionsAsWolfram[rule][cell]);
                }
            }
        }
        return output;
    }

    /**
     * Takes in a 2D array of hashed data in codeword form, then rehashes sets of codewords increasingly far apart in steps of powers of 2, 1 apart 2 apart 4 apart ... 2^n apart
     *
     * @param input a 2D array of hashed data
     * @param rule  one of {0,15,51,85,170,204,240,255}
     * @param depth iterative depth, also the power of how far away its neighbors are
     * @return the input data, rehashed with neighbors 2^depth apart
     */
    public int[][][] ecaMaxTransform(int[][] input, int rule, int depth) {
        //initWolframs();
        int rows = input.length;
        int cols = input[0].length;
        int[][][] deepInput = new int[depth + 1][rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
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
                            cell += (int) Math.pow(16, 2 * r + c) * deepInput[d - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                        }
                    }
                    //stores the neighborhood's codeword
                    deepInput[d][row][col] = (m.maxSolutionsAsWolfram[rule][cell]);
                }
            }
        }
        return deepInput;
    }

    /**
     * Initializes the set of hash truth tables for [0,15,51,85,170,204,240,255]
     */
    public void initWolframs() {
        for (int r = 0; r < 8; r++) {
            m.individualRule(unpackedList[r], 4, false, 0, false, 0, false);
        }
        //Initialize the truth tables for both the min and max codewords of the set
        for (int spot = 0; spot < 8; spot++) {
            for (int column = 0; column < 256 * 256; column++) {
                flatWolframs[0][spot][column] = m.minSolutionsAsWolfram[unpackedList[spot]][column];
                flatWolframs[1][spot][column] = m.maxSolutionsAsWolfram[unpackedList[spot]][column];
            }
        }
    }

    public int[][] reconstructDepthD(int[][] input, int depth, int ruleSetIndex) {
        int neighborDistance = 1 << (depth - 1);
        neighborDistance = 1;
        int[][][] votes = new int[input.length][input[0].length][4];
        for (int row = 0; row < input.length; row++) {
            for (int col = 0; col < input[0].length; col++) {
                //apply its vote to every location that it influences
                //including itself
                int[][] generatedGuess = m.generateCodewordTile(input[row][col], unpackedList[ruleSetIndex]);
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        //for (int power = 0; power < 4; power++) {
                        if (generatedGuess[r][c] == ruleSetIndex / 8) {
                            votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * (r % 2)) % input[0].length][c] += (1 << r);
                        } else {
                            votes[(row + neighborDistance * ((r / 2) % 2)) % input.length][(col + neighborDistance * (r % 2)) % input[0].length][c] -= (1 << r);
                        }
                        //}
                    }
                }
            }
        }
        //for each location, based on whether the final tally of the vote was positive or negative
        //output a 0 if positive and 1 if negative, if the vote result is not what the
        //original data is increment the error counter for analysis
        int[][] outResult = new int[input.length][input[0].length];
        int[][] outCompare = new int[input.length][input[0].length];
        int totDifferent = 0;
        int[][] finalOutput = new int[input.length][input[0].length];
        for (int row = 0; row < input.length; row++) {
            for (int column = 0; column < input[0].length; column++) {
                for (int power = 0; power < 4; power++) {
                    if (votes[row][column][power] >= 0) {
                        outResult[row][column] += 0;
                        finalOutput[row][column] += 0;
                    } else {
                        outResult[row][column] += (1 << power);
                        finalOutput[row][column] += (1 << power);
                    }
                }
                //outCompare[row][column] = outResult[row][column] ^ input[row][column];
                totDifferent += outCompare[row][column];
            }
        }
//        for (int row = 0; row < input.length; row++) {
//            for (int col = 0; col < input[0].length; col++) {
//                if (finalOutput[row][col] >= 0) {
//                    //finalOutput[row][col] += 0;
//                } else {
//                    //finalOutput[row][col] = 0;
//                }
//            }
//        }
        //System.out.println("totDifferent: " + totDifferent);
        //System.out.println("totArea: " + (input.length * input[0].length));
        //System.out.println("different/Area=errors/bit= " + ((double) totDifferent / (double) (input.length * input[0].length)));
        //CustomArray.plusArrayDisplay(finalOutput, false, false, "finalOutput");
        return outResult;
    }

    public int[][][] reconstructDepthD(int[][][] input, int depth) {
        int neighborDistance = 1 << (depth - 1);
        neighborDistance = 1;
        int[][][][] votes = new int[16][input[0].length][input[0][0].length][4];
        for (int row = 0; row < input[0].length; row++) {
            for (int col = 0; col < input[0][0].length; col++) {
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        //apply its vote to every location that it influences
                        //including itself
                        int[][] generatedGuess = m.generateCodewordTile(input[8 * posNeg + t][row][col], unpackedList[t]);
                        for (int r = 0; r < 4; r++) {
                            for (int c = 0; c < 4; c++) {
                                //for (int power = 0; power < 4; power++) {
                                if (generatedGuess[r][c] == posNeg) {
                                    votes[8 * posNeg + t][(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * (r % 2)) % input[0][0].length][c] += (1 << r);
                                } else {
                                    votes[8 * posNeg + t][(row + neighborDistance * ((r / 2) % 2)) % input[0].length][(col + neighborDistance * (r % 2)) % input[0][0].length][c] -= (1 << r);
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
        int[][][] outResult = new int[16][input[0].length][input[0][0].length];
        int[][] outCompare = new int[input[0].length][input[0][0].length];
        int totDifferent = 0;
        int[][] finalOutput = new int[input[0].length][input[0][0].length];
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                for (int row = 0; row < input[0].length; row++) {
                    for (int column = 0; column < input[0][0].length; column++) {
                        for (int power = 0; power < 4; power++) {
                            if (votes[8 * posNeg + t][row][column][power] >= 0) {
                                outResult[8 * posNeg + t][row][column] += 0;
                                finalOutput[row][column] += 0;
                            } else {
                                outResult[8 * posNeg + t][row][column] += (1 << power);
                                finalOutput[row][column] += (1 << power);
                            }
                        }
                        //outCompare[row][column] = outResult[row][column] ^ input[row][column];
                        totDifferent += outCompare[row][column];
                    }
                }
            }
        }
//        for (int row = 0; row < input.length; row++) {
//            for (int col = 0; col < input[0].length; col++) {
//                if (finalOutput[row][col] >= 0) {
//                    //finalOutput[row][col] += 0;
//                } else {
//                    //finalOutput[row][col] = 0;
//                }
//            }
//        }
        //System.out.println("totDifferent: " + totDifferent);
        //System.out.println("totArea: " + (input.length * input[0].length));
        ////System.out.println("different/Area=errors/bit= " + ((double) totDifferent / (double) (input.length * input[0].length)));
        //CustomArray.plusArrayDisplay(finalOutput, false, false, "finalOutput");
        return outResult;
    }

    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void bitmapTransform(String filepath) throws IOException {
        //String filepath = "frontYard.bmp";
        File file = new File(filepath);
        filepath = filepath.substring(0,filepath.length()-4);
        BufferedImage inImage = ImageIO.read(file);
        int[] inRaster = ((DataBufferInt) inImage.getRaster().getDataBuffer()).getData();
        int size = inImage.getWidth();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 15;
        int[][][] framesOfHashing = new int[depth][inImage.getHeight()][inImage.getWidth() * 8];
        int[][] field = new int[inImage.getHeight()][inImage.getWidth() * 8];
        int[][] bfield = new int[inImage.getHeight()][inImage.getWidth() * 32];
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
                for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
                    for (int power = 0; power < 8; power++) {
                        bfield[row][32 * column + 8 * rgbbyte + power] = (int) Math.abs((inRaster[row * inImage.getWidth() + column] >> (8 * rgbbyte + power)) % 2);
                        for (int posNegt = 0; posNegt < 16; posNegt++) {
                            bFieldSet[posNegt][row][32 * column + 8 * rgbbyte + power] = bfield[row][32 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        //Initialize the minMax codeword truth table set
        initWolframs();
        hashUtilities.readFromFile();
        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        bfield = initializeDepthZero(bfield, unpackedList[3])[1];

        //Do the transform
        framesOfHashing = ecaMinTransform(bfield, unpackedList[3], depth);
        //Convert the transform back into appropriate bitmap RGB format
        int[][][] rasterized = new int[depth + 1][inImage.getHeight()][inImage.getWidth()];
        for (int d = 0; d <= depth; d++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            rasterized[d][row][column] += (1 << (8 * rgbbyte + power)) * framesOfHashing[d][row][32 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        //
        //
        //
         //
         //
         //
         //
         //
         //
        //This does the GIF file
        BufferedImage[] images = new BufferedImage[rasterized.length];
        int[][] imagesRasters = new int[depth + 1][inImage.getHeight() * inImage.getWidth()];
        ImageWriter gifWriter = ImageIO.getImageWritersByFormatName("gif").next();
        ImageOutputStream outputStream = ImageIO.createImageOutputStream(new File("src/ImagesProcessed/"+filepath+"gif.gif"));
        gifWriter.setOutput(outputStream);
        int[] outRaster = new int[inImage.getHeight() * inImage.getWidth()];
        gifWriter.prepareWriteSequence(null);
        BufferedImage outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int repeat = 0; repeat < 1; repeat++) {
            for (int d = 0; d <= depth; d++) {
                File outFile = new File("src/ImagesProcessed/GifOutput/"+filepath+"iteration" + d + ".bmp");
                outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                outRaster = ((DataBufferInt) outImage.getRaster().getDataBuffer()).getData();
                for (int index = 0; index < outRaster.length; index++) {
                    outRaster[index] = rasterized[d][index / inImage.getWidth()][index % inImage.getWidth()];
                }
                ImageIO.write(outImage, "bmp", outFile);
                IIOImage image = new IIOImage(outImage, null, null);
                gifWriter.writeToSequence(image, null);
            }
        }
        gifWriter.endWriteSequence();

        System.out.println("depth: " + depth);
        System.out.println("done with gif");
        //
        //
        //
        //
        //
        //
        //
        //
         //
         //
         //
         //
        BufferedImage inverse = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[][][] undoInput = new int[16][inImage.getHeight()][inImage.getWidth()];
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                undoInput[3][row][column] = bfield[row][column];
            }
        }
        System.out.println("undoInput[3].length: " + undoInput[0].length + " " + undoInput[1][0].length);
        for (int posNegt = 0; posNegt < 8; posNegt++) {
            bFieldSet[posNegt] = initializeDepthZero(bFieldSet[posNegt], unpackedList[posNegt])[1];
            bFieldSet[posNegt + 8] = initializeDepthMax(bFieldSet[posNegt + 8], unpackedList[posNegt])[1];
        }
        int[][] undo = hashInverseDepth0(bFieldSet, 1, 3);
        int[][] undoRasterized = new int[inverse.getHeight()][inverse.getWidth()];
        System.out.println("inverse.getHeight(): " + inverse.getHeight() + " inverse.getWidth(): " + inverse.getWidth());
        System.out.println(undo.length + " " + undo[0].length);
        System.out.println(undoRasterized.length + " " + undoRasterized[0].length);
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inverse.getHeight(); row++) {
                for (int column = 0; column < inverse.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 3; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            undoRasterized[row][column] += undo[row][column * 32 + 8 * rgbbyte + power]<<(8*rgbbyte+power-((8*rgbbyte+power)/3));
                        }
                    }
                }
            }
        }
        int[] inverseImageRaster = ((DataBufferInt) inverse.getRaster().getDataBuffer()).getData();
        for (int row = 0; row < inverse.getHeight(); row++) {
            for (int column = 0; column < inverse.getWidth(); column++) {
                //if (row == 655 || column == 655) { System.out.println("row: " + row + ", column: " + column); }
                inverseImageRaster[row * inImage.getWidth() + column] = undoRasterized[row][column];
            }
        }
        File inverseFile = new File("src/ImagesProcessed/"+filepath+"inverse.bmp");
        ImageIO.write(inverse, "bmp", inverseFile);
        //
         //
         //
         //
         //
         //
         //
         //
         //
         //
        inverse = new BufferedImage(inverse.getWidth(), inverse.getHeight(), BufferedImage.TYPE_INT_RGB);
        inverseImageRaster = ((DataBufferInt) inverse.getRaster().getDataBuffer()).getData();
        undo = reconstructDepthD(framesOfHashing[1],1,3);
        undoRasterized = new int[inverse.getHeight()][inverse.getWidth()];
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inverse.getHeight(); row++) {
                for (int column = 0; column < inverse.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            undoRasterized[row][column] += undo[row][column * 32 + 8 * rgbbyte + power]<<(8*rgbbyte+power-((8*rgbbyte+power)/3));
                        }
                    }
                }
            }
        }
        for (int row = 0; row < inverse.getHeight(); row++) {
            for (int column = 0; column < inverse.getWidth(); column++) {
                //if (row == 655 || column == 655) { System.out.println("row: " + row + ", column: " + column); }
                inverseImageRaster[row * inImage.getWidth() + column] = undoRasterized[row][column];
            }
        }
        File inverseDepth1 = new File("src/ImagesProcessed/"+filepath+"inverseDepth1.bmp");
        ImageIO.write(inverse, "bmp", inverseDepth1);
    }
    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void bitmapTransform(String filepath, int dummy) throws IOException {
        //String filepath = "kitchenAlteredRGB.bmp";
        File file = new File(filepath);
        filepath = filepath.substring(0, filepath.length()-4);

        BufferedImage inImage = ImageIO.read(file);
        short[] inRaster = ((DataBufferUShort) inImage.getRaster().getDataBuffer()).getData();
        int size = inImage.getWidth();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 24;
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
        initWolframs();
        //hashUtilities.readFromFile();

        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        bfield = initializeDepthZero(bfield, unpackedList[3])[1];

        //Do the transform
        framesOfHashing = ecaMinTransform(bfield, unpackedList[3], depth);
        //Convert the transform back into appropriate bitmap RGB format
        short[][][] rasterized = new short[depth + 1][inImage.getHeight()][inImage.getWidth()];
        for (int d = 0; d <= depth; d++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            rasterized[d][row][column] += (1 << (8 * rgbbyte + power)) * framesOfHashing[d][row][16 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //This does the GIF file
        BufferedImage[] images = new BufferedImage[rasterized.length];
        int[][] imagesRasters = new int[depth + 1][inImage.getHeight() * inImage.getWidth()];
        ImageWriter gifWriter = ImageIO.getImageWritersByFormatName("gif").next();
        ImageOutputStream outputStream = ImageIO.createImageOutputStream(new File("src/ImagesProcessed/"+filepath+"gif.gif"));
        gifWriter.setOutput(outputStream);
        short[] outRaster = new short[inImage.getHeight() * inImage.getWidth()];
        gifWriter.prepareWriteSequence(null);
        BufferedImage outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
        for (int repeat = 0; repeat < 1; repeat++) {
            for (int d = 0; d <= depth; d++) {
                File outFile = new File("src/ImagesProcessed/GifOutput/"+filepath+"iteration" + d + ".bmp");
                outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
                outRaster = ((DataBufferUShort) outImage.getRaster().getDataBuffer()).getData();
                for (int index = 0; index < outRaster.length; index++) {
                    outRaster[index] = rasterized[d][index / inImage.getWidth()][index % inImage.getWidth()];
                }
                ImageIO.write(outImage, "bmp", outFile);
                IIOImage image = new IIOImage(outImage, null, null);
                gifWriter.writeToSequence(image, null);
            }
        }
        gifWriter.endWriteSequence();

        System.out.println("depth: " + depth);
        System.out.println("done with gif");
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        BufferedImage inverse = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
        int[][][] undoInput = new int[16][inImage.getHeight()][inImage.getWidth()];
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                undoInput[3][row][column] = bfield[row][column];
            }
        }
        System.out.println("undoInput[3].length: " + undoInput[0].length + " " + undoInput[1][0].length);
        for (int posNegt = 0; posNegt < 8; posNegt++) {
            bFieldSet[posNegt] = initializeDepthZero(bFieldSet[posNegt], unpackedList[posNegt])[1];
            bFieldSet[posNegt + 8] = initializeDepthMax(bFieldSet[posNegt + 8], unpackedList[posNegt])[1];
        }
        int[][] undo = hashInverseDepth0(bFieldSet, 1, 3);
        short[][] undoRasterized = new short[inverse.getHeight()][inverse.getWidth()];
        System.out.println("inverse.getHeight(): " + inverse.getHeight() + " inverse.getWidth(): " + inverse.getWidth());
        System.out.println(undo.length + " " + undo[0].length);
        System.out.println(undoRasterized.length + " " + undoRasterized[0].length);
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inverse.getHeight(); row++) {
                for (int column = 0; column < inverse.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            undoRasterized[row][column] += undo[row][column * 16 + 8 * rgbbyte + power]<<(8*rgbbyte+power);
                        }
                    }
                }
            }
        }
        short[] inverseImageRaster = ((DataBufferUShort) inverse.getRaster().getDataBuffer()).getData();
        for (int row = 0; row < inverse.getHeight(); row++) {
            for (int column = 0; column < inverse.getWidth(); column++) {
                //if (row == 655 || column == 655) { System.out.println("row: " + row + ", column: " + column); }
                inverseImageRaster[row * inImage.getWidth() + column] = (short) (undoRasterized[row][column]^inRaster[row*inImage.getWidth()+column]);
            }
        }
        File inverseFile = new File("src/ImagesProcessed/"+filepath+"inverse.bmp");
        ImageIO.write(inverse, "bmp", inverseFile);
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        inverse = new BufferedImage(inverse.getWidth(), inverse.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
        inverseImageRaster = ((DataBufferUShort) inverse.getRaster().getDataBuffer()).getData();
        undo = reconstructDepthD(framesOfHashing[1],1,3);
        undoRasterized = new short[inverse.getHeight()][inverse.getWidth()];
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inverse.getHeight(); row++) {
                for (int column = 0; column < inverse.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 2; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            undoRasterized[row][column] += undo[row][column * 16 + 8 * rgbbyte + power]<<(8*rgbbyte+power);
                        }
                    }
                }
            }
        }
        for (int row = 0; row < inverse.getHeight(); row++) {
            for (int column = 0; column < inverse.getWidth(); column++) {
                //if (row == 655 || column == 655) { System.out.println("row: " + row + ", column: " + column); }
                inverseImageRaster[row * inImage.getWidth() + column] = (short) (undoRasterized[row][column] ^ inRaster[inImage.getWidth()*row+column]);
            }
        }
        File inverseDepth1 = new File("src/ImagesProcessed/"+filepath+"inverseDepth1.bmp");
        ImageIO.write(inverse, "bmp", inverseDepth1);
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
        binaryArray = ecaMinTransform(binaryArray, unpackedList[2], 1)[1];
        //hashInverseDepth0(binaryArray, 1, unpackedList[2]);
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
    public int[][] hashInverseDepth0(int[][][] in, int depth, int rule) {
        int neighborDistance = 1 << (depth - 1);
        //load the minMax 8 tuple subset Wolfram codes
        //initWolframs();
        int[][][][] depthChart = new int[2][8][in.length][in[0].length];
        //puts the input data as layer 0 of the output data
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                //System.out.println("posNeg: " + posNeg + " t: " + t);
                //depthChart[posNeg][t] = initializeDepthZero(in, unpackedList[t])[1];
                //depthChart[posNeg][t] =
            }
        }
        //this array is the vote tally, location is influenced by 16 neighborhoods within a distance of 4
        //each of these neighborhoods has 16 terms in the min max codeword set of the 8 tuple
        //every term of every vote is weighted by 2^RelativeRow
        int[][] outVotes = new int[in[0].length][in[0][0].length];
        int r;
        int c;
        int t;
        int posNeg;
        int hadamardValue;
        int power;
        int row;
        int column;
        //for every location in the transformed bitmap data
        for (row = 0; row < in[0].length; row++) {
            //System.out.println("row: " + row + " out of " + in.length);
            for (column = 0; column < in[0][0].length; column++) {
                //for every term in its min max codeword set
                for (posNeg = 0; posNeg < 2; posNeg++) {
                    for (t = 0; t < 8; t++) {
                        //apply its vote to every location that it influences
                        //including itself
                        int[][] generatedGuess = m.generateCodewordTile(in[8 * posNeg + t][row][column], unpackedList[t]);
                        //CustomArray.plusArrayDisplay(generatedGuess, false, true, "generatedGuess");
                        for (r = 0; r < 4; r++) {
                            for (c = 0; c < 4; c++) {
                                //int a = (generatedGuess[r][c] );
                                if (generatedGuess[r][c] == posNeg) {
                                    //if (hadamardValue == posNeg) {
                                    outVotes[(row + (r)) % in[0].length][(column + (c)) % in[0][0].length] += (1 << r);
                                } else {
                                    outVotes[(row + (r)) % in[0].length][(column + (c)) % in[0][0].length] -= (1 << r);
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
        int[][] outResult = new int[in[0].length][in[0][0].length];
        int[][] outCompare = new int[in[0].length][in[0][0].length];
        int totDifferent = 0;
        for (row = 0; row < in[0].length; row++) {
            for (column = 0; column < in[0][0].length; column++) {
                if (outVotes[row][column] >= 0) {
                    outResult[row][column] = 0;
                } else {
                    outResult[row][column] = 1;
                }
                //outCompare[row][column] = outResult[row][column] ^ in[row][column];
                totDifferent += outCompare[row][column];
            }
        }
        //System.out.println("totDifferent: " + totDifferent);
        //System.out.println("totArea: " + (in.length * in[0].length));
        //System.out.println("different/Area=errors/bit= " + ((double) totDifferent / (double) (in.length * in[0].length)));
        //CustomArray.plusArrayDisplay(outVotes, true, false, "outVotes");
        //System.out.println("outResult.getHeight: " + " " + outResult.length + outResult[0].length);
        return outResult;
    }
}



