import java.util.Arrays;

/**
 * In an algorithm similar to long division, an ECA input neighborhood is plugged in, stretched, then fed back in to the previous row.
 * It results in a repeat at some point because there are only so many possible input neighborhoods of length l,
 * and from iteration 1 to some point the fedback neighborhoods produce an identical center column with a negative row offset of length iteration.
 */
public class ECAexponentReduction {
    /**
     * Utility functions for this class
     */
    RuleStretchTemplate ruleStretchTemplate = new RuleStretchTemplate();
    /**
     * At what iteration does the algorithm (passLoop in doLogReductionTwo) stop repeating?
     */
    int passRepeat;
    /**
     * Integer version of all solutions all rows for a specific iteration
     */
    int[][] decPasses;

    /**
     * Does the input, stretch, feedback algorithm to find offset equal center columns to the input neighborhood's center column
     *
     * @param n            ECA rule 0-255
     * @param logIn        length of input neighborhood
     * @param initialValue decimal value of input neighborhood
     * @param feedbackRow  stretched to this row, then fed back into the feedbackRow-1 Wolfram code
     * @return all solutions all rows all iterations
     */
    public int[][][] doLogReductionTwo(int n, int logIn, int initialValue, int feedbackRow) {
        //All neighborhoods calculated in the algorithm
        int[][][] passes = new int[32][24][64];
        //Decimal version of passes
        decPasses = new int[passes.length][passes[0].length];
        //Maximum number of rows to stretch to, much more than 22 here is an integer overflow and would require BigInteger
        int maxRows = 22;
        //Currently active Wolfram code address
        int value;
        //Initial row
        int initRow;
        //Holds the values of the feedback row so that the loop can check for repeats
        int[] values = new int[32];
        values[0] = initialValue;
        //Wolfram code array
        int[] nw = new int[8];
        for (int power = 0; power < 8; power++) {
            nw[power] = ((n / (int) Math.pow(2, power)) % 2);
        }
        //Initializes to -1
        int lengthRepeat = -1;
        for (int pass = 0; pass < 32 && lengthRepeat == -1; pass++) {
            for (int row = 0; row < maxRows; row++) {
                int powers = 1 + 2 * (row - 1);
                for (int power = 0; power < powers; power++) {
                    passes[pass][row][power] = -1;
                }
            }
        }
        //
        value = initialValue;
        //initRow and logIn aren't the same because initRow changes to feedbackRow-1 after the first loop
        initRow = logIn;
        //Main loop
        passLoop:
        for (int pass = 0; pass < 32 && lengthRepeat == -1; pass++) {
            //For each size neighborhood within maxRows
            //First loop it starts at initRow, after the first loop it starts at feedbackRow-1
            for (int row = initRow; row < maxRows; row++) {
                //Size of current neighborhood
                int powers = 1 + 2 * (row - 1);
                //Calculate 1 row for the current Wolfram code address (value)
                for (int power = 0; power < powers; power++) {
                    passes[pass][row][power] = nw[((value / (int) Math.pow(2, power)) % 8)];
                }
                //Extend to next row's neighborhood
                value *= 2;
            }
            //Converts neighborhoods to decimals
            for (int row = 0; row < maxRows; row++) {
                int tot = 0;
                int powers = 1 + 2 * (row - 1);
                for (int power = 0; power < powers; power++) {
                    tot += (int) Math.pow(2, power) * passes[pass][row][power];
                }
                decPasses[pass][row] = tot;
            }
            //Set for next loop
            value = decPasses[pass][feedbackRow];
            values[pass] = value;
            //Checks for repeat
            for (int passs = 0; passs < pass; passs++) {
                if (values[passs] == values[pass]) {
                    lengthRepeat = pass;
                    passRepeat = pass;
                }
            }
            //This is only relevant during the first loop
            initRow = feedbackRow - 1;
            //Display results of current loop
            System.out.print("\n");
            System.out.println("passes[" + pass + "]");
            for (int row = 0; row < 22; row++) {
                for (int column = 0; column < 1 + 2 * (row - 1); column++) {
                    System.out.print(String.format("%1d", passes[pass][row][column]));
                }
                System.out.print(" " + decPasses[pass][row] + "\n");
            }
            if (lengthRepeat != -1) break passLoop;
            System.out.print("\n");
        }
        System.out.println("repeat " + passRepeat);
        return passes;
    }

    /**
     * A manager function that compares center columns of doLogReductionTwo() solutions to all possible neighborhoods of a certain size
     *
     * @param n ECA 0-255 rule
     */
    public void checkAllColumns(int n) {
        int[] a = ruleStretchTemplate.traditionalCenterColumn(n, 256, new int[]{1}, 512);
        System.out.println(Arrays.toString(a));
        int[] comp;
        for (int l = 1; l < 2; l++) {
            int numPowers = 2 * (l - 1) + 1;
            for (int input = 2; input < 3; input++) {
                for (int ll = 3; ll < 4; ll++) {
                    int[][][] passes = doLogReductionTwo(n, 1, input, ll);
                    System.out.println("passRepeat " + passRepeat);
                    for (int pass = 0; pass <= passRepeat; pass++) {
                        int[] neighborhood = Arrays.copyOfRange(passes[pass][ll + 1], 0, 2 * (ll) + 1);
                        System.out.println(Arrays.toString(neighborhood) + " " + decPasses[pass][ll + 1]);
                        comp = ruleStretchTemplate.traditionalCenterColumn(n, 256, neighborhood, 512);
                        // comp = ruleStretchTemplate.traditionalCenterColumn(n,128,comp,256);
                        //comp = Arrays.copyOfRange(comp,0,64);
                        boolean found = false;
                        for (int spot = 1; spot < 128; spot++) {
                            int[] temp = Arrays.copyOfRange(comp, 1, 17);
                            if (Arrays.equals(temp, Arrays.copyOfRange(a, spot, spot + 16))) {
                                System.out.println("l " + l + " input " + input + " ll " + ll + " pass " + pass);
                                System.out.println("spot " + spot);
                                System.out.println();
                                found = true;
                            }
                        }
                        if (!found) {
//                            System.out.println(Arrays.toString(Arrays.copyOfRange(comp, 1, 31)));
//                            System.out.println(Arrays.toString(Arrays.copyOfRange(a, pass + 2, pass + 32)));
//                            System.out.println();
                        }
                    }
                    for (int in = 0; in < (int) Math.pow(2, 2 * (ll) + 1); in++) {
                        int[] second = new int[2 * ll + 1];
                        for (int index = 0; index < second.length; index++) {
                            second[index] = ((in / (int) Math.pow(2, index)) % 2);
                        }
                        second = ruleStretchTemplate.traditionalCenterColumn(n, 256, second, 512);
                        boolean found = false;
                        for (int spot = 1; spot < 128; spot++) {
                            int[] temp = Arrays.copyOfRange(second, 1, 17);
                            if (Arrays.equals(temp, Arrays.copyOfRange(a, spot, spot + 16))) {
                                System.out.println("spot " + spot + " in " + in + " middle three " + ((in / 8) % 8));
//                                System.out.println("l " + l + " input " + input + " ll " + ll + " pass " + pass);
//                                System.out.println("spot " + spot);
                                System.out.println();
                                found = true;
                            }
                        }
                        if (!found) {
//                            System.out.println(Arrays.toString(Arrays.copyOfRange(comp, 1, 31)));
//                            System.out.println(Arrays.toString(Arrays.copyOfRange(a, pass+2, pass + 32)));
                            System.out.println();
                        }
                    }
                }
            }
        }
    }

    /**
     * A manager function that compares center columns of doLogReductionTwo() solutions to all possible neighborhoods of a certain size
     *
     * @param n ECA 0-255 rule
     */
    public void checkAllColumns(int n, int neighborhoodInt, int neighborhoodSize) {
        int[] initNeighborhood = new int[neighborhoodSize];
        for (int spot = 0; spot < neighborhoodSize; spot++) {
            initNeighborhood[spot] = ((neighborhoodInt / (int) Math.pow(2, spot)) % 2);
        }
        int[] a = ruleStretchTemplate.traditionalCenterColumn(n, 256, initNeighborhood, 512);
        System.out.println(Arrays.toString(a));
        int[] comp;
        for (int in = 0; in < (int) Math.pow(2, neighborhoodSize); in++) {
            int[] second = new int[neighborhoodSize];
            for (int index = 0; index < second.length; index++) {
                second[index] = ((in / (int) Math.pow(2, index)) % 2);
            }
            second = ruleStretchTemplate.traditionalCenterColumn(n, 256, second, 512);
            boolean found = false;
            for (int spot = 1; spot < 128; spot++) {
                int[] temp = Arrays.copyOfRange(second, 1, 17);
                if (Arrays.equals(temp, Arrays.copyOfRange(a, spot, spot + 16))) {
                    System.out.println("spot " + spot + " in " + in + " middle three " + ((in / 8) % 8));
//                                System.out.println("l " + l + " input " + input + " ll " + ll + " pass " + pass);
//                                System.out.println("spot " + spot);
                    System.out.println();
                    found = true;
                }
            }
            if (!found) {
//                            System.out.println(Arrays.toString(Arrays.copyOfRange(comp, 1, 31)));
//                            System.out.println(Arrays.toString(Arrays.copyOfRange(a, pass+2, pass + 32)));
                System.out.println();
            }
        }
    }
}
