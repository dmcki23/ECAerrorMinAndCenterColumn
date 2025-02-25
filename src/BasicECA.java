import java.util.Arrays;
import java.util.Random;
/**
 * Utility class for elementary cellular automata (ECA), Wolfram codes 0-255
 */
public class BasicECA {


    /**
     * ECA 0-255 symmetry groups
     */
    public int[][] lrbwByRule = new int[256][4];
    /**
     * ECA 0-255 symmetry groups arranged by 0-88 independent groups
     */
    public int[][] lrbwGroups = new int[88][4];
    /**
     * ECA Wolfram Codes 0-255
     */
    public int[][] ecaWolframCodes = new int[256][8];
    /**
     * True means use single bit init input, false means use random input
     */
    public boolean singleInit;
    /**
     * Length of Wolfram code
     */
    public int wolframLength;
    /**
     * Numbers in binary array form
     */
    public int[][] initInput;

    /**
     * Wolfram codes of ECA beyond row 1, by size of input neighborhood, includes even numbers as a 2-bit place number.
     * Contains data for the last extended ECA rule in ruleExtension()
     */
    public int[][] ruleExtensions = new int[10][1024];

    /**
     * Wolfram complexity classification, by rule
     */
    public int[] ruleClasses;
    /**
     * Init
     */
    public BasicECA() {
        singleInit = true;
        for (int n = 0; n < 256; n++) {
            int temp = n;
            for (int power = 0; power < 8; power++) {
                ecaWolframCodes[n][power] = temp % 2;
                temp = temp - (temp % 2);
                temp = temp / 2;
            }
        }
        initInput = new int[4096][16];
        for (int n = 0; n < 4096; n++) {
            int temp = n;
            for (int power = 0; power < 16; power++) {
                initInput[n][power] = temp % 2;
                temp = temp - (temp % 2);
                temp = temp / 2;
            }
        }
        lrTableInit();
        ruleClassify();
    }
    /**
     * Set of wolfram codes for a given ECA rule beyond row 1, calculated by running the ECA for every possible input neighborhood with length log(wolframLength(rows))
     *
     * @param n elementary cellular automata rule
     * @return array of wolfram codes for a given ECA rule beyond row 1
     */
    public int[][] ruleExtension(int n) {
        int[][] out = new int[10][512];
        for (int spot = 0; spot < 8; spot++) {
            out[3][spot] = ecaWolframCodes[n][spot];
        }
        int[][] field;
        //odd numbered neighborhoods
        for (int neighborhood = 5; neighborhood < 10; neighborhood += 2) {
            wolframLength = (int) Math.pow(2, neighborhood);
            int numRows = (neighborhood - 1) / 2;
            field = new int[numRows + 1][neighborhood];
            //for all possible neighborhoods of wolframLength
            for (int input = 0; input < wolframLength; input++) {
                //initialize neighborhood
                for (int power = 0; power < neighborhood; power++) {
                    field[0][power] = initInput[input][power];
                }
                //calculate neighborhood
                for (int row = 1; row <= numRows; row++) {
                    for (int column = row; column < neighborhood - row; column++) {
                        field[row][column] = field[row - 1][column - 1] + 2 * field[row - 1][column] + 4 * field[row - 1][column + 1];
                        field[row][column] = out[3][field[row][column]];
                    }
                }
                //place result in Wolfram code
                out[neighborhood][input] = field[numRows][numRows];
            }
        }
        //even numbered neighborhoods, Wolfram code is a set of 2 bit numbers
        for (int neighborhood = 4; neighborhood < 9; neighborhood += 2) {
            int numRows = (neighborhood) / 2;
            wolframLength = (int) Math.pow(2, neighborhood);
            field = new int[neighborhood][neighborhood + 1];
            //for all possible neighborhoods of wolframLength
            for (int input = 0; input < wolframLength; input++) {
                //initialize neighborhood
                for (int power = 0; power < neighborhood; power++) {
                    field[0][power] = initInput[input][power];
                }
                //calculate results
                for (int row = 1; row <= numRows; row++) {
                    for (int column = row; column < neighborhood - row; column++) {
                        field[row][column] = field[row - 1][column - 1] + 2 * field[row - 1][column] + 4 * field[row - 1][column + 1];
                        field[row][column] = out[3][field[row][column]];
                    }
                }
                //place result in Wolfram code
                out[neighborhood][input] = 2 * field[numRows - 1][numRows - 1] + field[numRows - 1][numRows];
            }
        }
        ruleExtensions = out;
        return out;
    }
    /**
     * Generates the left-right-black-white rule symmetry groups for the 0-255 elementary cellular automata
     * <p>
     * The black white symmetry is generated by reversing the 8 bits of the wolfram code and negating them
     * <p>
     * The left right symmetry is generated by reversing the 3 places (1's place, 2's place, 4's place) of each
     * of the 8 bits
     * <p>
     * Left-right-black-white symmetry is doing both of the operations
     */
    public void lrTableInit() {
        for (int n = 0; n < 256; n++) {
            int[] outDec = new int[4];
            int[][] out = new int[4][8];
            for (int power = 0; power < 8; power++) {
                //identity
                out[0][power] = ecaWolframCodes[n][power];
                //reverse code order and negate
                out[1][power] = ecaWolframCodes[n][7 - power];
                out[1][power] = (out[1][power] + 1) % 2;
                //reverse bit order
                int lr = 0;
                for (int bit = 0; bit < 3; bit++) {
                    lr += (int) Math.pow(2, 2 - bit) * (power / (int) Math.pow(2, bit) % 2);
                }
                out[2][power] = ecaWolframCodes[n][lr];
                //reverse code order and negate
                out[3][7 - power] = ecaWolframCodes[n][lr];
                out[3][7 - power] = (out[3][7 - power] + 1) % 2;
            }
            //generate integer values of Wolfram codes
            for (int var = 0; var < 4; var++) {
                for (int power = 0; power < 8; power++) {
                    outDec[var] += (int) Math.pow(2, power) * out[var][power];
                }
            }
            for (int var = 0; var < 4; var++) {
                lrbwByRule[n][var] = outDec[var];
            }
        }
        //This section groups the 256 elementary rules into the 88 independent symmetry groups
        for (int i = 0; i < 88; i++) {
            for (int j = 0; j < 4; j++) {
                lrbwGroups[i][j] = -1;
            }
        }
        boolean spotFound = true;
        for (int n = 0; n < 256; n++) {
            spotFound = false;
            boolean alreadyEqual = false;
            int index = 0;
            if (lrbwGroups[index][0] == -1 || n == lrbwGroups[index][0] || n == lrbwGroups[index][1] || n == lrbwGroups[index][2] || n == lrbwGroups[index][3]) {
                spotFound = true;
                if (lrbwGroups[index][0] == n || lrbwGroups[index][1] == n || lrbwGroups[index][2] == n || lrbwGroups[index][3] == n) {
                    alreadyEqual = true;
                }
            }
            while (!spotFound) {
                index++;
                if (lrbwGroups[index][0] == -1 || n == lrbwGroups[index][0] || n == lrbwGroups[index][1] || n == lrbwGroups[index][2] || n == lrbwGroups[index][3]) {
                    spotFound = true;
                }
                if (lrbwGroups[index][0] == n || lrbwGroups[index][1] == n || lrbwGroups[index][2] == n || lrbwGroups[index][3] == n) {
                    alreadyEqual = true;
                }
            }
            if (!alreadyEqual) {
                for (int column = 0; column < 4; column++) {
                    lrbwGroups[index][column] = lrbwByRule[n][column];
                }
            }
            //System.out.println("n, " + n + " index, " + index);
        }
    }
    /**
     * Outputs the left-right-black-white rule symmetry groups
     */
    public void display() {
        System.out.println("All elementary automata rules and their equivalents");
        for (int n = 0; n < 256; n++) {
            System.out.println(Arrays.toString(lrbwByRule[n]));
        }
        System.out.println("Equivalent Rules sorted by group");
        for (int rule = 0; rule < 88; rule++) {
            //System.out.println("spot: " + rule + " \t" + equivRules[rule][0] + "\t" + equivRules[rule][1] + "\t" + equivRules[rule][2] + "\t" + equivRules[rule][3]);
            System.out.println(Arrays.toString(lrbwGroups[rule]));
        }
    }
    /**
     * Elementary cellular automata with single-bit initial input
     *
     * @param n       elementary cellular automata rule, 0-255
     * @param rows    number of rows to calculate
     * @param columns number of columns to calculate
     * @return 2D binary integer array with the results of the ECA's single bit init input;
     */
    public int[][] basicOut(int n, int rows, int columns) {
        int[][] out = new int[rows + 1][columns];
        //initialize single bit initial input
        out[0][columns / 2] = 1;
        //run standard ECA calculation
        for (int row = 1; row <= rows; row++) {
            for (int column = 1; column < columns - 2; column++) {
                out[row][column] = (out[row - 1][column - 1] + 2 * out[row - 1][column] + 4 * out[row - 1][column + 1]);
                out[row][column] = ecaWolframCodes[n][out[row][column]];
            }
        }
        return out;
    }
    /**
     * Elementary cellular automata with random initial input
     *
     * @param n           elementary cellular automata rule, 0-255
     * @param rows        number of rows to calculate
     * @param columns     number of columns to calculate
     * @param widthRandom desired width of random input
     * @return 2d integer array of ECA rule with random initial input
     */
    public int[][] randomOut(int n, int rows, int columns, int widthRandom) {
        int[][] randOut = new int[rows + 1][columns];
        Random rand = new Random();
        //initialize neighborhood to random values
        for (int column = 0; column < widthRandom; column++) {
            randOut[0][columns / 2 - widthRandom / 2 + column] = rand.nextInt(0, 2);
        }
        for (int row = 1; row <= rows; row++) {
            for (int column = 1; column < columns - 2; column++) {
                randOut[row][column] = (randOut[row - 1][column - 1] + 2 * randOut[row - 1][column] + 4 * randOut[row - 1][column + 1]);
                randOut[row][column] = ecaWolframCodes[n][randOut[row][column]];
            }
        }
        return randOut;
    }
    /**
     * Behavior classes of the ECA
     *
     * @return an integer array of every ECA's class
     */
    public int[] ruleClassify() {
        int[][] classes = new int[4][256];
        classes[0] = new int[]{0, 8, 32, 40, 128, 136, 160, 168};
        classes[1] = new int[]{1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13, 14, 15, 19, 23, 24, 25, 26, 27, 28, 29, 33, 34, 35, 36, 37, 38, 42, 43, 44, 46, 50, 51, 56, 57, 58, 62, 72, 73, 74, 76, 77, 78, 94, 104, 108, 130, 132, 134, 138, 140, 142, 152, 154, 156, 162, 164, 170, 172, 178, 184, 200, 204, 232};
        classes[2] = new int[]{18, 22, 30, 45, 60, 90, 105, 122, 126, 146, 150};
        classes[3] = new int[]{41, 54, 106, 110};
        ruleClasses = new int[256];
        for (int row = 0; row < 4; row++) {
            for (int spot = 0; spot < classes[row].length; spot++) {
                for (int symm = 0; symm < 4; symm++) {
                    ruleClasses[lrbwByRule[classes[row][spot]][symm]] = row + 1;
                }
            }
        }
        return ruleClasses;
    }
}

