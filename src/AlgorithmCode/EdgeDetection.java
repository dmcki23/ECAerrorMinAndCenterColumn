package AlgorithmCode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class EdgeDetection {
    HashTransform hash = new HashTransform();

    public int[][] detectEdges(int[][] frameOfHashing) {
        int rows = frameOfHashing.length;
        int cols = frameOfHashing[0].length;
        int[][] out = new int[frameOfHashing.length][frameOfHashing[0].length];
        double[][] doubleSet = new double[frameOfHashing.length][frameOfHashing[0].length];
        int numSamples = 0;
        Random rand = new Random();
        Hadamard hadamard = new Hadamard();
        int[][] h = hadamard.nonReducedHadamard(16);
        for (int sample = 0; sample < numSamples; sample++) {
            int a = rand.nextInt(0, rows);
            int b = rand.nextInt(0, cols);
            int randC = rand.nextInt(0, 16);
            int c = (a + randC) % rows;
            int d = (b + rand.nextInt(0, 16)) % cols;
            int abCodeword = frameOfHashing[a][b];
            int cdCodeword = frameOfHashing[c][d];
            int xMidpoint = ((a + c) / 2) % frameOfHashing.length;
            int yMidpoint = ((b + d) / 2) % frameOfHashing[0].length;
            int distance = (int) Math.sqrt((c - a) * (c - a)) + ((d - b) * (d - b));
            int difference = ((abCodeword + cdCodeword) / 2) % 16;
            int hadamardDifference = h[abCodeword][cdCodeword];
            int quadrant = 0;
            if ((c - a) < 0) quadrant = 1;
            if ((d - b) < 0) quadrant += 2;
            //doubleSet[xMidpoint][yMidpoint] += difference;
            //out[a][b] += (15 - difference);
            //out[a][b] %= 16;
            //if (abCodeword != cdCodeword) out[xMidpoint][yMidpoint] += (1<<(randC));
            //out[xMidpoint][yMidpoint] %= 16;
        }
        int[][] quads = new int[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = row;
                int y = col;
                int[] ringTots = new int[4];
                for (int quadrant = 0; quadrant < 4; quadrant++) {
                    int[] posNeg = new int[]{quadrant % 2, (quadrant / 2) % 2};
                    if (posNeg[0] == 0) posNeg[0] = 1;
                    if (posNeg[1] == 0) posNeg[1] = 1;
                    if (posNeg[0] == 1) posNeg[0] = -1;
                    if (posNeg[1] == 1) posNeg[1] = -1;
                    for (int diag = 1; diag < 3; diag++) {
                        int tot = 0;
                        int branchZeroX = (row + diag * posNeg[0] + rows) % rows;
                        int branchZeroY = (col + diag * posNeg[1] + cols) % cols;
                        for (int branch = 0; branch < 2 * (diag); branch++) {
                            if (frameOfHashing[branchZeroX][branchZeroY] == frameOfHashing[row][col]) {
                                //out[row][col] += (1 << (diag ));
                                ringTots[diag-1]++;
                            }
                            branchZeroX += quads[quadrant][0];
                            branchZeroY += quads[quadrant][1];
                            branchZeroX = (rows + branchZeroX) % rows;
                            branchZeroY = (cols + branchZeroY) % cols;
                        }

                    }
                }
                for (int diag = 0; diag < 4; diag++) {
                    if (ringTots[diag] >= 2*(diag+1)*4/2){
                        out[row][col] += (1<<(3-diag));
                    }
                }


            }
        }
        for (int row = 0; row < 100; row++) {
            //System.out.println(Arrays.toString(Arrays.copyOfRange(out[row],0,100)));
            //System.out.println(Arrays.toString(Arrays.copyOfRange(frameOfHashing[row], 0, 100)));
        }
        //CustomArray.plusArrayDisplay(out, true, false, "out");
        return out;
    }
    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void bitmapTransform(int dummy) throws IOException {
        String filepath = "kitchen.bmp";
        File file = new File(filepath);
        BufferedImage inImage = ImageIO.read(file);
        int[] inRaster = ((DataBufferInt) inImage.getRaster().getDataBuffer()).getData();
        int size = inImage.getWidth();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 1;
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
                for (int rgbbyte = 0; rgbbyte < 3; rgbbyte++) {
                    for (int power = 0; power < 8; power++) {
                        bfield[row][32 * column + 8 * rgbbyte + power + (rgbbyte * 8 + power) / 3] = (int) Math.abs((inRaster[row * inImage.getWidth() + column] >> (8 * rgbbyte + power)) % 2);
                        for (int posNegt = 0; posNegt < 0; posNegt++) {
                            bFieldSet[posNegt][row][32 * column + 8 * rgbbyte + power] = bfield[row][32 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        //Initialize the minMax codeword truth table set
        hash.initWolframs();
        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        bfield = hash.initializeDepthZero(bfield, hash.unpackedList[3])[1];
        //Do the transform
        bfield = hash.ecaMinTransformRGBrowNorm(bfield, hash.unpackedList[3], 1)[1];
        //framesOfHashing = hash.ecaMinTransform(bfield, hash.unpackedList[3], 1);
        int[][] edges;
        //edges = detectEdges(framesOfHashing[1]);
        edges = detectEdges(bfield);
        for (int cycle = 0; cycle < 1; cycle++) {
            edges = detectEdges(edges);
        }
        //edges = detectEdges(edges);
        //edges = detectEdges(edges);
        //Convert the transform back into appropriate bitmap RGB format
        int[][][] rasterized = new int[depth + 1][inImage.getHeight()][inImage.getWidth()];
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            rasterized[d][row][column] += (1 << (8 * rgbbyte + power)) * edges[row][32 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        BufferedImage outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[] outRaster = ((DataBufferInt) outImage.getRaster().getDataBuffer()).getData();
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                outRaster[inImage.getWidth() * row + column] = rasterized[0][row][column];
            }
        }
        File outFile = new File("src/edgeDetection.bmp");
        ImageIO.write(outImage, "bmp", outFile);
    }

    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void bitmapTransform() throws IOException {
        String filepath = "kitchen.bmp";
        File file = new File(filepath);
        BufferedImage inImage = ImageIO.read(file);
        int[] inRaster = ((DataBufferInt) inImage.getRaster().getDataBuffer()).getData();
        int size = inImage.getWidth();
        int depth = (int) (Math.log(inImage.getWidth() * inImage.getWidth()) / Math.log(2));
        depth = 1;
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
                for (int rgbbyte = 0; rgbbyte < 3; rgbbyte++) {
                    for (int power = 0; power < 8; power++) {
                        bfield[row][32 * column + 8 * rgbbyte + power + (rgbbyte * 8 + power) / 3] = (int) Math.abs((inRaster[row * inImage.getWidth() + column] >> (8 * rgbbyte + power)) % 2);
                        for (int posNegt = 0; posNegt < 0; posNegt++) {
                            bFieldSet[posNegt][row][32 * column + 8 * rgbbyte + power] = bfield[row][32 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        //Initialize the minMax codeword truth table set
        hash.initWolframs();
        //Change the RGB 4-bytes broken down into 32 bits into its depth 0 codewords
        bfield = hash.initializeDepthZero(bfield, hash.unpackedList[3])[1];
        //Do the transform
        bfield = hash.ecaMinTransformRGBrowNorm(bfield,hash.unpackedList[3],5)[5];
        //framesOfHashing = hash.ecaMinTransform(bfield, hash.unpackedList[3], 1);
        int[][] edges;
        //edges = detectEdges(framesOfHashing[1]);
        edges = detectEdges(bfield);
        for (int cycle = 0; cycle < 2; cycle++) {
            edges = detectEdges(edges);

        }
        int[][][] tuple = new int[16][edges.length][edges[0].length];
        tuple[3] = edges;
        edges = hash.hashInverseDepth0(tuple,1,hash.unpackedList[3]);
        //edges = detectEdges(edges);
        //edges = detectEdges(edges);
        //Convert the transform back into appropriate bitmap RGB format
        int[][][] rasterized = new int[depth + 1][inImage.getHeight()][inImage.getWidth()];
        for (int d = 0; d <= 0; d++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
                        for (int power = 0; power < 8; power++) {
                            rasterized[d][row][column] += (1 << (8 * rgbbyte + power)) * edges[row][32 * column + 8 * rgbbyte + power];
                        }
                    }
                }
            }
        }
        BufferedImage outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[] outRaster = ((DataBufferInt) outImage.getRaster().getDataBuffer()).getData();
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                outRaster[inImage.getWidth() * row + column] = rasterized[0][row][column];
            }
        }
        File outFile = new File("src/edgeDetection.bmp");
        ImageIO.write(outImage, "bmp", outFile);
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //This does the GIF file
//        BufferedImage[] images = new BufferedImage[rasterized.length];
//        int[][] imagesRasters = new int[depth + 1][inImage.getHeight() * inImage.getWidth()];
//        ImageWriter gifWriter = ImageIO.getImageWritersByFormatName("gif").next();
//        ImageOutputStream outputStream = ImageIO.createImageOutputStream(new File("src/ImagesProcessed/screenShotGIF.gif"));
//        gifWriter.setOutput(outputStream);
//        int[] outRaster = new int[inImage.getHeight() * inImage.getWidth()];
//        gifWriter.prepareWriteSequence(null);
//        outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
//        for (int repeat = 0; repeat < 1; repeat++) {
//            for (int d = 0; d <= depth; d++) {
//                File outFile = new File("src/ImagesProcessed/GifOutput/processedDepth" + d + ".bmp");
//                outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
//                outRaster = ((DataBufferInt) outImage.getRaster().getDataBuffer()).getData();
//                for (int index = 0; index < outRaster.length; index++) {
//                    outRaster[index] = rasterized[d][index / inImage.getWidth()][index % inImage.getWidth()];
//                }
//                ImageIO.write(outImage, "bmp", outFile);
//                IIOImage image = new IIOImage(outImage, null, null);
//                gifWriter.writeToSequence(image, null);
//            }
//        }
//        gifWriter.endWriteSequence();
//        System.out.println("depth: " + depth);
//        System.out.println("done with gif");
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        BufferedImage inverse = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
//        int[][][] undoInput = new int[16][inImage.getHeight()][inImage.getWidth()];
//        for (int row = 0; row < inImage.getHeight(); row++) {
//            for (int column = 0; column < inImage.getWidth(); column++) {
//                undoInput[3][row][column] = bfield[row][column];
//            }
//        }
//        System.out.println("undoInput[3].length: " + undoInput[0].length + " " + undoInput[1][0].length);
//        for (int posNegt = 0; posNegt < 8; posNegt++) {
//            bFieldSet[posNegt] = hash.hashInverseDepth0(bFieldSet[posNegt], hash.unpackedList[posNegt]);[1];
//            bFieldSet[posNegt + 8] = hash.initializeDepthMax(bFieldSet[posNegt + 8], hash.unpackedList[posNegt])[1];
//        }
//        int[][] undo = hash.hashInverseDepth0(bFieldSet, 1, 3);
//        int[][] undoRasterized = new int[inverse.getHeight()][inverse.getWidth()];
//        System.out.println("inverse.getHeight(): " + inverse.getHeight() + " inverse.getWidth(): " + inverse.getWidth());
//        System.out.println(undo.length + " " + undo[0].length);
//        System.out.println(undoRasterized.length + " " + undoRasterized[0].length);
//        for (int d = 0; d <= 0; d++) {
//            for (int row = 0; row < inverse.getHeight(); row++) {
//                for (int column = 0; column < inverse.getWidth(); column++) {
//                    for (int rgbbyte = 0; rgbbyte < 3; rgbbyte++) {
//                        for (int power = 0; power < 8; power++) {
//                            undoRasterized[row][column] += undo[row][column * 32 + 8 * rgbbyte + power] << (8 * rgbbyte + power - ((8 * rgbbyte + power) / 3));
//                        }
//                    }
//                }
//            }
//        }
//        int[] inverseImageRaster = ((DataBufferInt) inverse.getRaster().getDataBuffer()).getData();
//        for (int row = 0; row < inverse.getHeight(); row++) {
//            for (int column = 0; column < inverse.getWidth(); column++) {
//                //if (row == 655 || column == 655) { System.out.println("row: " + row + ", column: " + column); }
//                inverseImageRaster[row * inImage.getWidth() + column] = undoRasterized[row][column];
//            }
//        }
//        File inverseFile = new File("src/ImagesProcessed/inverse.bmp");
//        ImageIO.write(inverse, "bmp", inverseFile);
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        //
//        inverse = new BufferedImage(inverse.getWidth(), inverse.getHeight(), BufferedImage.TYPE_INT_RGB);
//        inverseImageRaster = ((DataBufferInt) inverse.getRaster().getDataBuffer()).getData();
//        undo = hash.reconstructDepthD(framesOfHashing[1], 1, 3);
//        undoRasterized = new int[inverse.getHeight()][inverse.getWidth()];
//        for (int d = 0; d <= 0; d++) {
//            for (int row = 0; row < inverse.getHeight(); row++) {
//                for (int column = 0; column < inverse.getWidth(); column++) {
//                    for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
//                        for (int power = 0; power < 8; power++) {
//                            undoRasterized[row][column] += undo[row][column * 32 + 8 * rgbbyte + power] << (8 * rgbbyte + power - ((8 * rgbbyte + power) / 3));
//                        }
//                    }
//                }
//            }
//        }
//        for (int row = 0; row < inverse.getHeight(); row++) {
//            for (int column = 0; column < inverse.getWidth(); column++) {
//                //if (row == 655 || column == 655) { System.out.println("row: " + row + ", column: " + column); }
//                inverseImageRaster[row * inImage.getWidth() + column] = undoRasterized[row][column];
//            }
//        }
//        File inverseDepth1 = new File("src/ImagesProcessed/inverseDepth1.bmp");
//        ImageIO.write(inverse, "bmp", inverseDepth1);
    }
}
