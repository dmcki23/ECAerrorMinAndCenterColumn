package TrimmedCode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

public class FastMinStaging {
    minErrorStaging m = new minErrorStaging();
    int[][][][] wolframs;
    int[][][][][] solutions;
    int[][] ruleList = new int[][]{{0, 255}, {15, 85}, {204, 204}, {170, 240}};
    int[][][] flatWolframs = new int[2][8][256 * 256];
    int[][][] reconstructed;
    int[][] vectorField;
    int[][] packedList = new int[][]{{0, 255}, {15, 240}, {51, 204}, {85, 170}};
    int[] unpackedList = new int[]{0, 15, 51, 85, 170, 204, 240, 255};
    int[][][][] contiguous;
    int[][][][] buckets;
    public int[][][] lrbwFourTemplate() {
        int[][][] out = new int[16][4][4];
        for (int row = 0; row < 16; row++) {
            for (int power = 0; power < 4; power++) {
                out[row][0][power] = row ;
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
        return out;
    }

    public int[][] lrbwCodewordTemplate(){
        int[][][] in = lrbwFourTemplate();
        int[][] out = new int[in.length][in[0].length];
        for (int row = 0; row < in.length; row++) {
            for (int lr = 0; lr < 2; lr++) {
                for (int bw = 0; bw < 2; bw++) {
                    for (int power = 0; power < 4; power++) {
                        out[row][2*lr+bw] += (1<<in[row][2*lr+bw][power])*((row>>power)%2);
                    }
                }
            }
        }
        return out;
    }
    public int[][][][] initWolframs() {
        int[][] ruleList = packedList;
        int[][][] wolframIn = new int[4][2][256 * 256];
        int[][][] maxWolframIn = new int[4][2][256 * 256];
        m.doAllRulesCoords(4, false, 0, false, 0, false, ruleList);

        for (int spot = 0; spot < 4; spot++) {
            for (int lr = 0; lr < 2; lr++) {
                //m.individualRule(ruleList[spot][lr],4,false,0,false,0,false);
                wolframIn[spot][lr] = m.minSolutionsAsWolfram[ruleList[spot][lr]];
                maxWolframIn[spot][lr] = m.maxSolutionsAsWolfram[ruleList[spot][lr]];
            }
        }
        //int[][] wolfram = m.minSolutionsAsWolfram;
        //int[][] maxWolfram = m.maxSolutionsAsWolfram;
        //minMax,group,leftright
        wolframs = new int[2][4][2][256 * 256];
        for (int spot = 0; spot < 4; spot++) {
            for (int lr = 0; lr < 2; lr++) {
                for (int column = 0; column < 256 * 256; column++) {
                    wolframs[0][spot][lr][column] = wolframIn[spot][lr][column];
                    wolframs[1][spot][lr][column] = maxWolframIn[spot][lr][column];
                    flatWolframs[0][2 * spot + lr][column] = wolframIn[spot][lr][column];
                    flatWolframs[1][2 * spot + lr][column] = maxWolframIn[spot][lr][column];
                }
            }
        }
        return wolframs;
    }
    public int[][] gridOfInt(int size, int inInt) {
        int[][] out = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                out[row][column] = ((inInt >> (4 * row + column)) % 2);
            }
        }
        return out;
    }
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
}
