package CustomLibrary;

/**
 * array display class
 */
public class CustomArray {


    /**
     * Checks a 2D int array for Latin-ness, each row and column contains exactly one of each value. Used to help verify the integerity of the multiplication tables generated
     *
     * @param in 2D int array
     * @return boolean  true if in[][] is Latin and false if not
     */
    public static boolean isLatin(int[][] in) {
        boolean isLatin = true;
        for (int row = 0; row < in.length; row++) {
            for (int column = 0; column < in.length; column++) {
                for (int comp = 0; comp < in.length; comp++) {
                    if (comp == column) continue;
                    if (in[row][column] == in[row][comp]) isLatin = false;
                }
                for (int comp = 0; comp < in.length; comp++) {
                    if (comp == row) continue;
                    if (in[row][column] == in[comp][column]) isLatin = false;
                }
            }
        }
        return isLatin;
    }

    /**
     * displays a 2D integer array
     *
     * @param in       2D integer array to display
     * @param noZeros  true if you want to replace zeros with spaces on output
     * @param noSpaces true if you don't want any spaces between elements
     */
    public static void plusArrayDisplay(int[][] in, boolean noZeros, boolean noSpaces) {
        int rows = in.length;
        int columns = in[0].length;
        String outstring = "";
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (in[row][column] == 0 && noZeros) {
                    outstring += " ";
                } else {
                    outstring += in[row][column];
                }
                if (!noSpaces) {
                    outstring += " ";
                }
            }
            System.out.println(outstring);
            outstring = "";
        }
        System.out.println();
    }

    /**
     * displays a 2D integer array
     *
     * @param in       2D integer array to display
     * @param noZeros  true if you want to replace zeros with spaces on output
     * @param noSpaces true if you don't want space padding on output
     * @param label    array title
     */
    public static void plusArrayDisplay(int[][] in, boolean noZeros, boolean noSpaces, String label) {
        int rows = in.length;
        int columns = in[0].length;
        String outstring = "";
        System.out.println(label);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (in[row][column] == 0 && noZeros) {
                    outstring += " ";
                } else {
                    outstring += in[row][column];
                }
                if (!noSpaces) {
                    outstring += " ";
                }
            }
            System.out.println(outstring);
            outstring = "";
        }
        System.out.println();
    }

    /**
     * displays a 2D integer array
     *
     * @param in       2D integer array to display
     * @param noZeros  true if you want to replace zeros with spaces on output
     * @param noSpaces true if you don't want space padding on output
     * @param label    array title
     */
    public static void layersDisplay(int[][][] in, boolean noZeros, boolean noSpaces, String label) {
        int rows = in.length;
        int columns = in[0][0].length;
        String outstring = "";
        System.out.println(label);
        for (int layer = 0; layer < in.length; layer++) {
            System.out.println("Layer " + layer);
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    if (in[layer][row][column] == 0 && noZeros) {
                        outstring += " ";
                    } else {
                        outstring += in[layer][row][column];
                    }
                    if (!noSpaces) {
                        outstring += " ";
                    }
                }
                System.out.println(outstring);
                outstring = "";
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * displays a 2D integer array on the console
     *
     * @param in       2D integer array to display
     * @param noZeros  true if you want to replace zeros with spaces on output
     * @param noSpaces true if you don't want space padding on output
     * @param rowDiv   number of rows used on increment
     * @param colDiv   number of columns used on increment
     */
    public static void plusPlusArrayDisplay(int[][] in, boolean noZeros, boolean noSpaces, int rowDiv, int colDiv) {
        int rows = in.length;
        int columns = in[0].length;
        String outstring = "";
        for (int row = 0; row < rows; row += rowDiv) {
            for (int column = 0; column < rows; column += colDiv) {
                if (in[row][column] != 0) {
                    outstring += in[row][column];
                } else if (in[row][column] == 0 && noZeros) {
                    outstring += " ";
                } else {
                    outstring += in[row][column];
                }
                if (!noSpaces) {
                    outstring += " ";
                }
            }
            System.out.println(outstring);
            outstring = "";
        }
        System.out.println();
    }

    /**
     * displays a 2D integer array
     *
     * @param in 2D integer array to display
     */
    public static void basicArrayDisplay(int[][] in) {
        int rows = in.length;
        int columns = in[0].length;
        String outstring = "";
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                outstring += in[row][column];
            }
            System.out.println(outstring);
            outstring = "";
        }
        System.out.println();
    }







    /**
     * A particular place value's layer of an input array
     *
     * @param in    2D grid
     * @param layer which place value
     * @param base  which base
     * @param modN  which mod
     * @return input with parameters applied
     */
    static int[][] layerOf(int[][] in, int layer, int base, int modN) {
        int[][] out = new int[in.length][in[0].length];
        for (int row = 0; row < in.length; row++) {
            for (int column = 0; column < in.length; column++) {
                out[row][column] = ((in[row][column]) / (int) Math.pow(base, layer)) % modN;
            }
        }
        return out;
    }

    public int[][] reflectRotateTranspose(int[][] in, int rotation) {
        int size = in.length;
        int[][] out = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                out[row][column] = in[row][column];
            }
        }
        if (rotation % 2 == 1) {
            int[][] nextTemp = new int[size][size];
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    nextTemp[row][column] = out[size - 1 - row][column];
                }
            }
            out = nextTemp;
        }
        if (((rotation / 2) % 2) == 1) {
            int[][] nextTemp = new int[size][size];
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    nextTemp[row][column] = out[row][size - 1 - column];
                }
            }
            out = nextTemp;
        }
        if (((rotation / 4) % 2) == 1) {
            int[][] nextTemp = new int[size][size];
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    nextTemp[row][column] = out[column][row];
                }
            }
            out = nextTemp;
        }
        return out;
    }


}
