package AlgorithmCode;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
//import javax.imageio.plugins.gif.GIFImageWriteParam;

/**
 * Manages and displays the results of ECA minMax hashing any given *.bmp image,
 * as well as producing a *.gif file animating each frame of the hash, illustrating
 * the avalanche property
 */
public class SwingApplyFastMinTransform extends JFrame {
    /**
     * Raster of the display image
     */
    public int[] displayRaster;
    /**
     * This is the image the frame displays, painted in the JPanel's paintComponent()
     */
    public BufferedImage displayImage;
    /**
     * This is the transform data, before converting it back to 4 byte bitmap RGB format
     * framesOfHashing[iteration][row][column]
     */
    public int[][][] framesOfHashing;
    /**
     * Number of iterations of hashing to do
     */
    public int depth;
    /**
     * This is the width of the image
     */
    public int size;
    /**
     * Input image
     */
    public BufferedImage inImage;
    /**
     * Raster of the input image
     */
    public int[] inRaster;
    /**
     * Contains algorithm code
     */
    HashTransform fmt = new HashTransform();
    /**
     * Swing component used as the frame's canvas
     */
    drawingPanel drawPanel;
    /**
     * Used in making each frame of the .gif file
     */
    BufferedImage outImage;
    /**
     * This is the transform data from framesOfHashing converted back into bitmap 4 byte RGB format
     * rasterized[iteration][row][column]
     */
    int[][][] rasterized;

    /**
     * Initializes a JFrame on which to display the panel
     */
    public SwingApplyFastMinTransform() throws IOException {
        drawPanel = new drawingPanel();
        framesOfHashing = new int[8][16 * 256][16 * 256];
        this.setTitle("ECA minMax codeword transform");
        this.setSize(1030, 850);
        this.setLocation(250, 250);
        this.add(drawPanel);
        this.setVisible(true);
    }

    /**
     * Gets depth
     *
     * @return depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Sets depth
     *
     * @param depth
     */
    public void setDepth(int depth) {
        framesOfHashing = new int[depth][size][size];
        this.depth = depth;
    }

    /**
     * Loads a bitmap, eca hash transforms it, displays it, makes a .gif file
     *
     * @throws IOException
     */
    public void getImage() throws IOException {
        String filepath = "testScreenshot.bmp";
        File file = new File(filepath);
        inImage = ImageIO.read(file);
        inRaster = ((DataBufferInt) inImage.getRaster().getDataBuffer()).getData();
        size = inImage.getWidth();
        depth = inImage.getWidth();
        depth = 5;
        framesOfHashing = new int[depth][inImage.getHeight()][inImage.getWidth() * 8];
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
        fmt.initWolframs();
        bfield = fmt.initializeDepthZero(bfield,fmt.unpackedList[3])[1];
        //Do the transform
        framesOfHashing = fmt.ecaMinTransform(bfield, fmt.unpackedList[3], depth);
        //Convert the transform back into appropriate bitmap RGB format
        rasterized = new int[depth + 1][inImage.getHeight()][inImage.getWidth()];
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
        displayImage = inImage;
        drawPanel.triggerRepaint();
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
        for (int repeat = 0; repeat < 1; repeat++) {
            for (int d = 0; d <= depth; d++) {
//                File outFile = new File("src/ImagesProcessed/GifOutput/processedDepth" + d + ".bmp");
//                images[d] = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
//                //images[d] = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
//                imagesRasters[d] = ((DataBufferInt) images[d].getRaster().getDataBuffer()).getData();
//                for (int index = 0; index < imagesRasters[d].length; index++) {
//                    imagesRasters[d][index] = rasterized[d][index / inImage.getWidth()][index % inImage.getWidth()];
//                    //imagesRasters[d][index] = outRaster[index];
//                }
//                ImageIO.write(images[d], "bmp", outFile);
//                IIOImage image = new IIOImage(images[d], null, null);
//                gifWriter.writeToSequence(image, null);
                File outFile = new File("src/ImagesProcessed/GifOutput/processedDepth" + d + ".bmp");
                outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                //images[d] = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                outRaster = ((DataBufferInt) outImage.getRaster().getDataBuffer()).getData();
                for (int index = 0; index < outRaster.length; index++) {
                    outRaster[index] = rasterized[d][index / inImage.getWidth()][index % inImage.getWidth()];
                    //imagesRasters[d][index] = outRaster[index];
                }
                ImageIO.write(outImage, "bmp", outFile);
                IIOImage image = new IIOImage(outImage, null, null);
                gifWriter.writeToSequence(image, null);
            }
        }
        gifWriter.endWriteSequence();
        System.out.println("depth: " + depth);
        System.out.println("done with gif");
        drawPanel.triggerRepaint();
    }

    /**
     * Functions as the frame's canvas
     */
    public class drawingPanel extends JPanel {
        public drawingPanel() {
            this.setSize(800, 600);
            this.setVisible(true);
        }

        /**
         * Triggers a repaint of this component
         */
        public void triggerRepaint() {
            this.repaint();
        }

        /**
         * JPanel paint implementation
         *
         * @param g the <code>Graphics</code> context in which to paint
         */
        public void paintComponent(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 1030, 830);
            g.setColor(Color.BLACK);
            displayImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            displayRaster = ((DataBufferInt) displayImage.getRaster().getDataBuffer()).getData();
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int d = depth; d <= depth; d++) {
                        //displayRaster[row * inImage.getWidth() + column] = rasterized[d][row][column];
                    }
                }
            }
            g.drawImage(inImage, 15, 15, null);
        }
    }
}
