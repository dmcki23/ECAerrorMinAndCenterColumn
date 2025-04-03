import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

public class FastMinTransformApril {
    FastMinTransform fmt = new FastMinTransform();

    public void check() throws IOException {
        String filepath = "lion.bmp";
        File file = new File(filepath);
        BufferedImage inImage = ImageIO.read(file);
        int[] inRaster = ((DataBufferInt) inImage.getRaster().getDataBuffer()).getData();
        int[][] binaryArray = new int[inImage.getHeight()][inImage.getWidth() * 32];
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

    public void checkInverse(int[][] in) {
        fmt.initWolframs();
        int[][][][] depthChart = new int[2][8][in.length][in[0].length];
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                System.out.println("posNeg: " + posNeg + " t: " + t);
                depthChart[posNeg][t] = fmt.minTransform(in, new int[]{posNeg, t / 2, t % 2})[1];
            }
        }
        int[][] outVotes = new int[in.length][in[0].length];
        int r;
        int c;
        int t;
        int posNeg;
        for (int row = 0; row < in.length; row++) {
            System.out.println("row: " + row + " out of " + in.length);
            for (int column = 0; column < in[0].length; column++) {
                for (posNeg = 0; posNeg < 2; posNeg++) {
                    for (t = 0; t < 8; t++) {
                        //int[][] generatedGuess = m.generateGuess(depthChart[posNeg][t][row][column], fmt.unpackedList[t]);
                        int hadamardValue = 0;
                        for (int power = 0; power < 4; power++) {
                            hadamardValue += ((depthChart[posNeg][t][row][column] / (int) Math.pow(2, power)) % 2);
                        }
                        hadamardValue %= 2;
                        for (r = 0; r < 4; r++) {
                            for (c = 0; c < 4; c++) {
                                //int a = (generatedGuess[r][c] );
                                //if (generatedGuess[r][c] == posNeg) {
                                if (hadamardValue == posNeg) {
                                    outVotes[(row + r) % in.length][(column + c) % in[0].length] += (int) Math.pow(2, r);
                                } else {
                                    outVotes[(row + r) % in.length][(column + c) % in[0].length] -= (int) Math.pow(2, r);
                                }
                            }
                        }
                    }
                }
            }
        }
        int[][] outResult = new int[in.length][in[0].length];
        int[][] outCompare = new int[in.length][in[0].length];
        int totDifferent = 0;
        for (int row = 0; row < in.length; row++) {
            for (int column = 0; column < in[0].length; column++) {
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

    public int[][] lrbwFour() {
        int[][] out = new int[16][4];
        for (int row = 0; row < 16; row++) {
            out[row][0] = row;
            int[] temp = new int[4];
            int tot = 0;
            for (int power = 0; power < 4; power++) {
                tot += (int) Math.pow(2, power) * ((row / (int) Math.pow(2, 3 - power)) % 2);
            }
            out[row][2] = 15 - tot;
            out[row][3] = 15 - tot;
            tot = 0;
            int totb = 0;
            for (int power = 0; power < 2; power++) {
                for (int pow = 0; pow < 2; pow++){

                }
            }
        }
        return out;
    }
    public void checkErrorWeights(){
        fmt.initWolframs();
        int totDifferent = 0;
        for (int posNeg = 0; posNeg < 1; posNeg++) {
            for (int t = 0; t < 8; t++) {
                for (int input = 0; input < 16; input++) {
                    int[][] cell = fmt.m.generateGuess(input,fmt.unpackedList[t]);
                    int error = 0;
                    for (int row = 0; row < 4; row++) {
                        for (int column = 0; column < 4; column++) {
                            if (cell[row][column] == 0) {
                                error += (1<<row);
                            } else {
                                error -= (1<<row);
                            }

                        }
                    }
                    int totInInput = 0;
                    for (int power = 0 ; power < 4; power++) {
                        totInInput += ((input/(int)Math.pow(2, power)) % 2);
                    }
                    totInInput %= 2;
                    System.out.println("totInInput: " + totInInput);
                    System.out.println("error: " + error);
                    int vote = 0;
                    if (error >= 0) vote = 0;
                    else vote = 1;
                    totDifferent += (vote^totInInput);
                    System.out.println("vote: " + vote);
                }
            }
        }
        System.out.println("totDifferent: " + totDifferent);
    }
}
