package CustomLibrary;

/**
 * Contains utility functions that display and operate on integer arrays, no specific hash function algorithms
 */
public class CustomArray {
    /**
     * Does nothing at the moment
     */
    public CustomArray() {
    }

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

    public static int getArrayDimensions(Object array) {
        if (array == null || !array.getClass().isArray()) {
            return 0; // Not an array or null
        }
        int dimensions = 1; // Start with 1 for the base array
        Object current = array;
        while (current.getClass().isArray()) {
            if (java.lang.reflect.Array.getLength(current) == 0) {
                break;
            }
            current = java.lang.reflect.Array.get(current, 0); // Get the first element
            if (current != null && current.getClass().isArray()) {
                dimensions++;
            } else {
                break; // If it's not an array, we're done
            }
        }
        return dimensions;
    }

    public static int[] dimensions(Object array) {
        int dimensions = getArrayDimensions(array);
        System.out.println("dimensions: " + dimensions);

            if (dimensions ==0) {
            }
            if (dimensions ==1) {
                int[] asArray = (int[]) array;
                int[] out = new int[1];
                out[0] = asArray.length;
                return out;
            }
            if (dimensions ==2) {
                int[][] asArray = (int[][]) array;
                int[] out = new int[2];
                out[0] = asArray.length;
                out[1] = asArray[0].length;
                return out;
            }
            if (dimensions ==3) {
                int[][][] asArray = (int[][][]) array;
                int[] out = new int[3];
                out[0] = asArray.length;
                out[1] = asArray[0].length;
                out[2] = asArray[0][0].length;
                return out;
            }
            if (dimensions ==4) {
                int[][][][] asArray = (int[][][][]) array;
                int[] out = new int[4];
                out[0] = asArray.length;
                out[1] = asArray[0].length;
                out[2] = asArray[0][0].length;
                out[3] = asArray[0][0][0].length;
                return out;
            }
            if (dimensions ==5) {
                int[][][][][] asArray = (int[][][][][]) array;
                int[] out = new int[5];
                out[0] = asArray.length;
                out[1] = asArray[0].length;
                out[2] = asArray[0][0].length;
                out[3] = asArray[0][0][0].length;
                out[4] = asArray[0][0][0][0].length;
                return out;
            }
            if (dimensions ==6) {
                int[][][][][][] asArray = (int[][][][][][]) array;
                int[] out = new int[6];
                out[0] = asArray.length;
                out[1] = asArray[0].length;
                out[2] = asArray[0][0].length;
                out[3] = asArray[0][0][0].length;
                out[4] = asArray[0][0][0][0].length;
                out[5] = asArray[0][0][0][0][0].length;
                return out;
            }

        return new int[]{0};
    }

    public static int countOnes(Object array, int inPower) {
        int[] dimensions = dimensions(array);
        int i = dimensions.length;
        int out = 0;
        if (i == 0) {
        }
        if (i == 1) {
            int[] asArray = (int[]) array;
            for (int a = 0; a < dimensions[0]; a++) {
                for (int power = 0; power < inPower; power++) {
                    out += (asArray[a] >> power) % 2;
                }
            }
        }
        if (i == 2) {
            int[][] asArray = (int[][]) array;
            for (int a = 0; a < dimensions[0]; a++) {
                for (int b = 0; b < dimensions[1]; b++) {
                    for (int power = 0; power < inPower; power++) {
                        out += (asArray[a][b] >> power) % 2;
                    }
                }
            }
        }
        if (i == 3) {
            int[][][] asArray = (int[][][]) array;
            for (int a = 0; a < dimensions[0]; a++) {
                for (int b = 0; b < dimensions[1]; b++) {
                    for (int c = 0; c < dimensions[2]; c++) {
                        for (int power = 0; power < inPower; power++) {
                            out += (asArray[a][b][c] >> power) % 2;
                        }
                    }
                }
            }
        }
        if (i == 4) {
            int[][][][] asArray = (int[][][][]) array;
            for (int a = 0; a < dimensions[0]; a++) {
                for (int b = 0; b < dimensions[1]; b++) {
                    for (int c = 0; c < dimensions[2]; c++) {
                        for (int d = 0; d < dimensions[3]; d++) {
                            for (int power = 0; power < inPower; power++) {
                                out += (asArray[a][b][c][d] >> power) % 2;
                            }
                        }
                    }
                }
            }
        }
        if (i == 5) {
            int[][][][][] asArray = (int[][][][][]) array;
            for (int a = 0; a < dimensions[0]; a++) {
                for (int b = 0; b < dimensions[1]; b++) {
                    for (int c = 0; c < dimensions[2]; c++) {
                        for (int d = 0; d < dimensions[3]; d++) {
                            for (int e = 0; e < dimensions[4]; e++) {
                                for (int f = 0; f < dimensions[5]; f++) {
                                    for (int power = 0; power < inPower; power++) {
                                        out += (asArray[a][b][c][d][e] >> power) % 2;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (i == 6) {
            int[][][][][][] asArray = (int[][][][][][]) array;
            for (int a = 0; a < dimensions[0]; a++) {
                for (int b = 0; b < dimensions[1]; b++) {
                    for (int c = 0; c < dimensions[2]; c++) {
                        for (int d = 0; d < dimensions[3]; d++) {
                            for (int e = 0; e < dimensions[4]; e++) {
                                for (int f = 0; f < dimensions[5]; f++) {
                                    for (int g = 0; g < dimensions[6]; g++) {
                                        for (int power = 0; power < inPower; power++) {
                                            out += (asArray[a][b][c][d][e][f] >> power) % 2;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return out;
    }

    /**
     * displays a 2D integer array
     *
     * @param in       2D integer array to display
     * @param noZeros  true if you want to replace zeros with spaces on output
     * @param noSpaces true if you don't want space padding on output
     * @param label    array title
     */
    public static void plusArrayDisplay(int[][] in, boolean noZeros, boolean noSpaces, String label, int numberPlaces) {
        int rows = in.length;
        int columns = in[0].length;
        String outstring = "";
        System.out.println(label);
        String spaces = "";
        for (int number = 0; number < numberPlaces; number++) {
            spaces += " ";
        }
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (in[row][column] == 0 && noZeros) {
                    outstring += spaces;
                } else {
                    outstring += String.format("%0" + numberPlaces + "d", (in[row][column]));
                }
                if (!noSpaces) {
                    outstring += " ";
                }
            }
            outstring += "\\\\";
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
    public static int[][] layerOf(int[][] in, int layer, int base, int modN) {
        int[][] out = new int[in.length][in[0].length];
        for (int row = 0; row < in.length; row++) {
            for (int column = 0; column < in.length; column++) {
                out[row][column] = ((in[row][column]) / (int) Math.pow(base, layer)) % modN;
            }
        }
        return out;
    }

    /**
     * Reflects and transposes square integer arrays
     *
     * @param in       square integer array
     * @param rotation an integer 0-8, the one's place is reflecting across the y axis, the two's place is reflecting across
     *                 the x-axis, and the four's place is the transpose. 0 would be do nothing and 7 would be all 3
     * @return transformed input array
     */
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

    /**
     * Takes in a 2D array and changes it into a 1D array
     *
     * @param in 2D integer array
     * @return flattened input
     */
    public int[] flatten(int[][] in) {
        int[] out = new int[in.length * in[0].length];
        for (int row = 0; row < in.length; row++) {
            for (int column = 0; column < in[0].length; column++) {
                out[row * in[0].length + column] = in[row][column];
            }
        }
        return out;
    }
}
