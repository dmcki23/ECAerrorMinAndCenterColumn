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
    SwingApplyFastMinTransform(String titleString) throws IOException {
        drawPanel = new drawingPanel();
        framesOfHashing = new int[8][16 * 256][16 * 256];
        //this.setSize(1030, 1335);
        //this.setVisible(true);
        //JFrame frame = new JFrame();
        //frame.setLayout(new GridLayout());
        //getImage();
        this.setTitle(titleString);
        this.setSize(1030, 850);
        this.setLocation(250, 250);
        //panel = new JPanel();
        this.add(drawPanel);
        //frame.add(this);
        // this.setVisible(true);
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
        //String filepath = "src/ECAhashPaper/screenshotFriday.bmp";
        String filepath = "testScreenshot.bmp";
        File file = new File(filepath);
        inImage = ImageIO.read(file);
        inRaster = ((DataBufferInt) inImage.getRaster().getDataBuffer()).getData();
        size = inImage.getWidth();
        depth = inImage.getWidth();
        depth = 12;
        framesOfHashing = new int[depth][inImage.getHeight()][inImage.getWidth() * 8];
        int[][] field = new int[inImage.getHeight()][inImage.getWidth() * 8];
        System.out.println("inRaster: " + inRaster.length);
        System.out.println("imImage.getHeight(): " + inImage.getHeight());
        System.out.println("imImage.getWidth(): " + inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight(): " + inRaster.length / inImage.getHeight());
        System.out.println("inRaster.length/inImage.getWidth(): " + inRaster.length / inImage.getWidth());
        System.out.println("inRaster.length/inImage.getHeight()/inImage.getWidth(): " + inRaster.length / inImage.getHeight() / inImage.getWidth());
        for (int row = 0; row < inImage.getHeight(); row++) {
            for (int column = 0; column < inImage.getWidth(); column++) {
                for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
                    for (int lr = 0; lr < 2; lr++) {
                        int rasterCoordX = row * inImage.getWidth() + column;
                        field[row][8 * column + 2 * rgbbyte + lr] = (int) Math.abs((inRaster[rasterCoordX] >> (4 * (2 * rgbbyte + lr))) % 16);
                    }
                }
            }
        }
        framesOfHashing = fmt.ecaMinTransform(field, fmt.unpackedList[2], depth);
        rasterized = new int[depth + 1][inImage.getHeight()][inImage.getWidth()];
        for (int d = 1; d <= depth; d++) {
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int rgbbyte = 0; rgbbyte < 4; rgbbyte++) {
                        for (int lr = 0; lr < 2; lr++) {
                            rasterized[d][row][column] += (16 << (2 * rgbbyte + lr)) * framesOfHashing[d][row][column * 8 + 2 * rgbbyte + lr];
                        }
                    }
                }
            }
        }
        //reconstructedField = rasterizedRF;
        //discreteField = rasterizedRF;
        System.out.println("before repaint");
        drawPanel.triggerRepaint();
        System.out.println("after repaint");
        BufferedImage[] images = new BufferedImage[rasterized.length];
        int[][] imagesRasters = new int[depth + 1][inImage.getHeight() * inImage.getWidth()];
        //ImageWriter writer = ImageIO.getImageWritersByFormatName("bmp").next();
        ImageWriter gifWriter = ImageIO.getImageWritersByFormatName("gif").next();
        ImageOutputStream outputStream = ImageIO.createImageOutputStream(new File("src/ECAhashPaper/screenShotGIF.gif"));
        //ImageWriteParam gifWriteParam = new ImageWriteParam(null);
        gifWriter.setOutput(outputStream);
        gifWriter.prepareWriteSequence(null);
        int delayTime = 200;
        //ImageReader imageReader = ImageIO.getImageReadersByFormatName("gif").next();
        //IIOMetadata iioMetadata = imageReader.getImageMetadata(0);
        for (int repeat = 0; repeat < 2; repeat++) {
            for (int d = 0; d <= depth; d++) {
                File outFile = new File("src/ECAhashPaper/processedDepth" + d + ".bmp");
                outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                images[d] = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                int[] outRaster = ((DataBufferInt) outImage.getRaster().getDataBuffer()).getData();
                for (int index = 0; index < outRaster.length; index++) {
                    outRaster[index] = rasterized[d][index / inImage.getWidth()][index % inImage.getWidth()];
                    imagesRasters[d][index] = outRaster[index];
                }
                ImageIO.write(outImage, "bmp", outFile);
                IIOImage image = new IIOImage(outImage, null, null);
                //ImageWriteParam imageWriteParam = gifWriter.getDefaultWriteParam();
                //imageWriteParam.set
                //      image.setMetadata(metadata);
                gifWriter.writeToSequence(image, null);
//            BufferedImage frame = outImage;
//            IIOMetadata imageMetaData = writer.getDefaultImageMetadata(new ImageTypeSpecifier(frame), gifWriteParam);
//            String metaFormatName = imageMetaData.getNativeMetadataFormatName();
//            IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
//            IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
//            graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(delays[i] / 10)); // Delay time in hundredths of a second
//            graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
//            IIOMetadataNode applicationExtensionsNode = getNode(root, "ApplicationExtensions");
//            IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
//            child.setAttribute("applicationID", "NETSCAPE");
//            child.setAttribute("authenticationCode", "2.0");
//            byte[] b = ("\001" + ((i == frames.length - 1) ? "\000" : "\001") + "\000").getBytes();
//            child.setUserObject(b);
//            applicationExtensionsNode.appendChild(child);
//            imageMetaData.setFromTree(metaFormatName, root);
//            writer.writeToSequence(new javax.imageio.IIOImage(frame, null, imageMetaData), gifWriteParam);
            }
        }
        gifWriter.endWriteSequence();
        System.out.println("depth: " + depth);
        System.out.println("done with gif");
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
            //super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 1030, 830);
            g.setColor(Color.BLACK);
//
//        realRaster  = new int[1];
//        imRaster = new int[1];
            displayImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            displayRaster = ((DataBufferInt) displayImage.getRaster().getDataBuffer()).getData();
            for (int row = 0; row < inImage.getHeight(); row++) {
                for (int column = 0; column < inImage.getWidth(); column++) {
                    for (int d = depth; d <= depth; d++) {
                        //Change of color scheme here
                        //realRaster[row * 1000 + column] += (int) Math.pow(2, 23-power) * (complexField[row][column].real / Math.pow(2, -power + 3) % 2);
                        displayRaster[row * inImage.getWidth() + column] = rasterized[d][row][column];
                    }
                }
            }
            g.drawImage(displayImage, 15, 15, null);
        }
    }
}
