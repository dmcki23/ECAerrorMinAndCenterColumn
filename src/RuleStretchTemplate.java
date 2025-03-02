/**
 * Extends Wolfram codes beyond row 1, finds the center column of an input, paths() is a recursive function that only considers powers of 2 in extended Wolfram codes
 */
public class RuleStretchTemplate {
    /**
     * Used in the recursive paths()
     */
    int[][] paths = new int[14][8 * (int) Math.pow(2, 12)];

    /**
     * Extends a Wolfram code beyond row 1 the traditional way
     *
     * @param n Wolfram code number
     * @return Wolfram code extended beyond row 1
     */
    public int[][] ruleExtension(int n) {
        int[][] out = new int[14][(int) Math.pow(2, 17)];
        for (int spot = 0; spot < 8; spot++) {
            out[3][spot] = (n / (int) Math.pow(2, spot)) % 2;
        }
        int[][] field;
        //odd numbered neighborhoods
        for (int neighborhood = 5; neighborhood < 14; neighborhood += 2) {
            int wolframLength = (int) Math.pow(2, neighborhood);
            int numRows = (neighborhood - 1) / 2;
            //for all possible neighborhoods of wolframLength
            for (int input = 0; input < wolframLength; input++) {
                field = new int[numRows + 1][neighborhood];
                //initialize neighborhood
                for (int power = 0; power < neighborhood; power++) {
                    field[0][power] = ((input / (int) Math.pow(2, power)) % 2);
                }
                //calculate neighborhood
                for (int row = 1; row <= numRows; row++) {
                    for (int column = 1; column < neighborhood - 1; column++) {
                        field[row][column] = 1 * field[row - 1][column - 1] + 2 * field[row - 1][column] + 4 * field[row - 1][column + 1];
                        field[row][column] = out[3][field[row][column]];
                    }
                }
                //place result in Wolfram code
                out[neighborhood][input] = field[numRows][numRows];
            }
        }
        return out;
    }

    /**
     * Stretches the Wolfram code beyond row 1
     *
     * @param n Wolfram number
     * @return extended Wolfram code
     */
    public int[][] doStretch(int n) {
//        int[][] stretched = new int[64][8 * (int) Math.pow(2, 13)];
//        for (int row = 0; row < 32; row++) {
//            for (int column = 0; column < stretched.length; column++) {
//                stretched[row][column] = ((column / (int) Math.pow(2, row)) % 8);
//            }
//        }
        for (int column = 0; column < 8; column++) {
            paths[3][column] = ((n / (int) Math.pow(2, column)) % 2);
        }
        int totOps = 0;
        for (int pathLength = 5; pathLength < 10; pathLength += 2) {
            for (int path = 0; path < (int) Math.pow(2, pathLength); path++) {
                totOps = 0;
                int tot = 0;
                for (int row = 0; row < pathLength; row++) {
                    //paths[pathLength][path] += (int) Math.pow(2, row) * stretched[row][path];
//                    tot += (int)Math.pow(2,row)*paths[3][((path/(int)Math.pow(2,row))%8)];
                    tot += (int) Math.pow(2, row) * ((path / (int) Math.pow(2, row)) % 2);
                }
                //System.out.println("Pathlength " + pathLength);
                paths[pathLength][path] = path(tot, pathLength, n, totOps);
                System.out.print(" " + totOps + " ");
            }
            System.out.print("\n");
        }
        return paths;
    }

    /**
     * Recurive log reduction of extended Wolfram codee
     *
     * @param pathTot power sum of the input neighborhood
     * @param depth   number of cells in the input neighborhood
     * @param n       Wolfram number
     * @param totOps  total number of operations, for assessing efficiency
     * @return the value of input pathTot at specified depth
     */
    public int path(int pathTot, int depth, int n, int totOps) {
        int out = 0;
        for (int power = 0; power < depth - 2; power++) {
            out += (int) Math.pow(2, power) * paths[3][((pathTot / (int) Math.pow(2, power)) % 8)];
            totOps++;
        }
        if (depth == 5) {
            totOps++;
            return paths[3][out];
        } else {
            totOps++;
            return path(out, depth - 2, n, totOps);
        }
    }

    /**
     * Runs two different algorithms for Wolfram codes beyond row 1
     *
     * @param n elementary cellular automata number
     */
    public void manageBoth(int n) {
        int[][] neighborhood = ruleExtension(n);
        paths = doStretch(n);
        //int[][] paths = new int[13][(int)Math.pow(2,16)];
        for (int row = 3; row < 10; row += 2) {
            System.out.print("\n");
            for (int column = 0; column < (int) Math.pow(2, row); column++) {
                System.out.print(neighborhood[row][column]);
            }
            System.out.print("\n");
            for (int column = 0; column < (int) Math.pow(2, row); column++) {
                System.out.print(paths[row][column]);
            }
            System.out.print("\n");
//
//            for (int column = 0; column <  (int) Math.pow(2, row); column++) {
//                System.out.print(neighborhood[(row)][column]);
//            }
//            System.out.print("\n");
            for (int column = 0; column < (int) Math.pow(2, row); column++) {
                System.out.print(neighborhood[(row)][column] ^ paths[row][column]);
            }
            System.out.print("\n");
            System.out.print("\n");
        }
    }

    /**
     * Center column of the given input neighborhood with ECA n
     *
     * @param n       Wolfram code number
     * @param numRows number of rows to return
     * @param input   initial input neighborhood
     * @param size    width and length of the square ECA output field, must be > 2*numRows
     * @return center column of the automata with single bit initial input
     */
    public int[] traditionalCenterColumn(int n, int numRows, int[] input, int size) {
        int[] columnOut = new int[numRows];
        //code = basicECA.ruleExtension(30);
        //System.out.println(Arrays.toString(code[5]));
        //code[3] = new int[]{0,1,1,1,1,0,0,0};
        int[][] field = new int[size][size];
        //field[0][128] = 1;
        for (int column = size / 2 - input.length / 2; column <= size / 2 + input.length / 2; column++) {
            //System.out.println("column " + column);
            field[0][column] = input[column + input.length / 2 - size / 2];
        }
        //columnOut[0] = field[0][size/2];
        //calculate neighborhood
        //System.out.println("size/2 " + (size/2));
        //System.out.println("size/2 - input.length/2 " + (size/2-input.length/2));
        for (int row = 1; row < numRows; row++) {
            for (int ccolumn = 1; ccolumn < size - 1; ccolumn++) {
                field[row][ccolumn] = field[row - 1][ccolumn - 1] + 2 * field[row - 1][ccolumn] + 4 * field[row - 1][ccolumn + 1];
                field[row][ccolumn] = ((n / (int) Math.pow(2, field[row][ccolumn])) % 2);
            }
            columnOut[row] = field[row][size / 2];
        }
//        System.out.print("\n");
//        for (int row = 0; row < numRows; row++) {
//            for (int ccolumn = size/2-numRows; ccolumn < size/2+numRows; ccolumn++) {
//                System.out.print(field[row][ccolumn]);
//            }
//            System.out.print("\n");
//        }
//        System.out.print("\n");
        return columnOut;
    }
}
