package AlgorithmCode;

import CustomLibrary.CustomArray;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 */
public class HashTransform {
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
     * Does the Hash transform on 1D input
     *
     * @param input binary array
     * @return
     */
    public int[] oneD(int[] input) {
        int[] out = new int[input.length];
        return out;
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
        int[][][] deepInput = new int[depth + 1][rows][cols];
        //initialize layer 0 to the input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                deepInput[0][row][col] = input[row][col];
            }
        }
        //for however many iterations you want to do, typically log2(inputWidth+inputHeight)
        for (int d = 1; d <= depth; d++) {
            //for every (row,column) location in the image
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    //gets its neighborhood
                    int cell = 0;
                    int phasePower = (int) Math.pow(2, d - 1);
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            cell += (int) Math.pow(16, 2 * r + c) * deepInput[d - 1][(row + phasePower * r) % rows][(col + phasePower * c) % cols];
                        }
                    }
                    //stores the neighborhood's codeword
                    deepInput[d][row][col] = (m.minSolutionsAsWolfram[rule][cell]);
                }
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
        for (int r = 0; r < 8; r++){
            m.individualRule(unpackedList[r],4,false,0,false,0,false);
        }
        //Initialize the truth tables for both the min and max codewords of the set
        for (int spot = 0; spot < 8; spot++) {
            for (int column = 0; column < 256 * 256; column++) {
                flatWolframs[0][spot][column] = m.minSolutionsAsWolfram[unpackedList[spot]][column];
                flatWolframs[1][spot][column] = m.maxSolutionsAsWolfram[unpackedList[spot]][column];
            }
        }
    }
    public int[][] reconstruct(int[][] input, int depth){
        int[][] out = new int[input.length][input[0].length];

        return out;
    }
    public int[] reconstruct(int[] input, int depth){
        int[] out = new int[input.length];
        return out;
    }

    public void writeSetToFile() throws IOException {
        String filename = "src/AlgorithmCode/tupleWolframs.txt";
        initWolframs();
        File file = new File(filename);
        FileWriter fw = new FileWriter(file);
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                String outstring = "";
                for (int row = 0; row < 256 * 256; row++) {
                    outstring += flatWolframs[posNeg][t][row]+" ";
                }
                fw.write(outstring+"\n");

            }
        }
        fw.close();
    }
    public void readSetFromFile() throws IOException {
        String filename = "src/AlgorithmCode/tupleWolframs.txt";
        flatWolframs = new int[2][8][65536];
        File file = new File(filename);
        FileReader reader = new FileReader(file);
        int length = 1;
        char[] buffer = new char[length];
        int charactersRead = reader.read(buffer, 0, length);
        String fileString = "";
        while (charactersRead != -1) {
            fileString += new String(buffer, 0, charactersRead);
        }
        int index = 0;
        int posNeg = 0;
        int t = 0;
        String[][] wolframStrings = new String[2][8];
        int start = 0;
        int end = 0;
        for (int fileSpot = 0; fileSpot < fileString.length(); fileSpot++) {
            if (fileString.charAt(fileSpot) == '\n') {
                end = fileSpot;
                wolframStrings[posNeg][t] = fileString.substring(start, end);
                start = end + 2;
                t++;
                if (t == 8) {
                    t = 0;
                    posNeg = 1;
                }
            }
        }
    }
    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void bitmapTransform() throws IOException {
        String filepath = "testScreenshot.bmp";
        File file = new File(filepath);
        BufferedImage inImage = ImageIO.read(file);
        int[] inRaster = ((DataBufferInt) inImage.getRaster().getDataBuffer()).getData();
        int size = inImage.getWidth();
        int depth = inImage.getWidth();
        depth = 5;
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
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
                    for (int lr = 0; lr < 2; lr++) {
                        int rasterCoordX = row * inImage.getWidth() + column;
                        field[row][8 * column + 2 * rgbbyte + lr] = (int) Math.abs((inRaster[rasterCoordX] >> (4 * rgbbyte + 2*lr)) % 16);
                        for (int power = 0; power < 4; power++){
                            bfield[row][32*column+8*rgbbyte+4*lr+power] = (field[row][8*column+2*rgbbyte+lr]>>power)%2;
                        }
                    }
                }
            }
        }
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
                    for (int power = 0; power < 8; power++){
                        bfield[row][32*column] = (int)Math.abs((inRaster[row*inImage.getWidth() + column]>>(8*rgbbyte+power)) % 2);
                    }
                }
            }
        }
        initWolframs();
        bfield = initializeDepthZero(bfield,unpackedList[3])[1];
        //Do the transform
        framesOfHashing = ecaMinTransform(bfield, unpackedList[3], depth);
        //Convert the transform back into appropriate bitmap RGB format
        int[][][] rasterized = new int[depth + 1][inImage.getHeight()][inImage.getWidth()];
//        for (int d = 0; d <= depth; d++) {
//            for (int row = 0; row < inImage.getHeight(); row++) {
//                for (int column = 0; column < inImage.getWidth(); column++) {
//                    for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
//                        for (int lr = 0; lr < 2; lr++) {
//                            rasterized[d][row][column] +=  (framesOfHashing[d][row][column * 8 + 2 * rgbbyte + lr] << (8*rgbbyte+4*lr));
//                        }
//                    }
//                }
//            }
//        }
        for (int d = 0; d <= depth; d++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            rasterized[d][row][column] += (1<<(8*rgbbyte+power)) * framesOfHashing[d][row][32*column  + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }

        //
        //
        //
        //The rest of this does the GIF file
        BufferedImage[] images = new BufferedImage[rasterized.length];
        int[][] imagesRasters = new int[depth + 1][inImage.getHeight() * inImage.getWidth()];
        ImageWriter gifWriter = ImageIO.getImageWritersByFormatName("gif").next();
        ImageOutputStream outputStream = ImageIO.createImageOutputStream(new File("src/ImagesProcessed/screenShotGIF.gif"));
        gifWriter.setOutput(outputStream);
        int[] outRaster = new int[inImage.getHeight()*inImage.getWidth()];
        gifWriter.prepareWriteSequence(null);
        BufferedImage outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int repeat = 0; repeat < 1; repeat++) {
            for (int d = 0; d <= depth; d++) {
                File outFile = new File("src/ImagesProcessed/GifOutput/processedDepth" + d + ".bmp");
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
        checkInverse(initializeDepthZero(binaryArray,51)[1]);
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
        initWolframs();
        int[][][][] depthChart = new int[2][8][in.length][in[0].length];
        //puts the input data as layer 0 of the output data
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                System.out.println("posNeg: " + posNeg + " t: " + t);
                depthChart[posNeg][t] = initializeDepthZero(in, unpackedList[t])[1];
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
}



