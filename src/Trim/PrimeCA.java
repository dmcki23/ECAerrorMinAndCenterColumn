import javax.net.ssl.SSLContext;
import java.math.BigInteger;
import java.util.Arrays;
/**
 * Same algorithm as ECAexponentReduction but applied to the PrimeCA [citation]
 */
public class PrimeCA {
    /**
     * Wolfram code of primeCA
     */
    public int[] primeRule;
    /**
     * Output space of primeCA
     */
    public int[][] primeField;
    /**
     * Output space of primeCA broken down into layers of 2's places
     */
    public int[][][] primeFieldPowers;
    /**
     * A stretch version of the primeCA
     */
    public int[][] extRule;
    /**
     * At what point does the algorithm start repeating?
     */
    int passRepeat;
    /**
     * Long version of primeCA Wolfram code
     */
    long[] longRule;
    /**
     * Generates the main rule and outputs the rule and the results
     */
    PrimeCA() {
        generatePrimeCArule();
//        String outstring = "";
//        for (int row = 0; row < 100; row++) {
//            for (int column = 450; column < 550; column++) {
//                if (primeFieldPowers[0][row][column] != 0) {
//                    outstring += Integer.toString(primeFieldPowers[0][row][column]);
//                } else {
//                    outstring += " ";
//                }
//            }
//            System.out.println(outstring);
//            outstring = "";
//        }
//        System.out.println("\n\n\n");
//        for (int row = 0; row < 100; row++) {
//            for (int column = 450; column < 550; column++) {
//                if (primeFieldPowers[1][row][column] != 0) {
//                    outstring += Integer.toString(primeFieldPowers[1][row][column]);
//                } else {
//                    outstring += " ";
//                }
//            }
//            System.out.println(outstring);
//            outstring = "";
//        }
//        System.out.println("\n\n\n");
//        for (int row = 0; row < 100; row++) {
//            for (int column = 450; column < 550; column++) {
//                if (primeFieldPowers[2][row][column] != 0) {
//                    outstring += Integer.toString(primeFieldPowers[2][row][column]);
//                } else {
//                    outstring += " ";
//                }
//            }
//            System.out.println(outstring);
//            outstring = "";
//        }
//        System.out.println("\n\n\n");
//        for (int row = 0; row < 100; row++) {
//            for (int column = 450; column < 550; column++) {
//                if (primeFieldPowers[3][row][column] != 0) {
//                    outstring += Integer.toString(primeFieldPowers[3][row][column]);
//                } else {
//                    outstring += " ";
//                }
//            }
//            System.out.println(outstring);
//            outstring = "";
//        }
        outputPrimeField();
    }
    /**
     * Outputs the primeCA
     */
    public void outputPrimeField() {
        String outstring = "";
        for (int row = 0; row < 100; row++) {
            for (int column = 450; column < 550; column++) {
                if (primeField[row][column] != 0) {
                    outstring += String.format("% 2d", primeField[row][column]);
                } else {
                    outstring += "  ";
                }
            }
            System.out.println(outstring);
            outstring = "";
        }
        for (int row = 0; row < 16; row++) {
            for (int column = 0; column < 16; column++) {
                outstring = "";
                for (int zee = 0; zee < 16; zee++) {
                    int ns = 256 * row + 16 * column + zee;
                    outstring += Integer.toString(primeRule[ns]) + "\t";
                }
                //System.out.println(outstring);
                outstring = "";
            }
            //System.out.println();
        }
    }
    /**
     * Generates the Wolfram code of the primeCA
     */
    public void generatePrimeCArule() {
        int[] hexadecimal_rule_truth_table = new int[4096];
        //With[{rule = {{13, 3, 13}->12,
        // {6, _, 4}->15,
        // {10, _, 3|11}->15,
        // {13, 7, _}->8,
        // {13, 8, 7}->13,
        // {15, 8, _}->1,
        // {8, _, _}->7,
        // {15, 1, _}->2,
        // {_, 1, _}->1,
        // {1, _, _}->8,
        // {2|4|5, _, _}->13,
        // {15, 2, _}->4,
        // {_, 4, 8}->4,
        // {_, 4, _}->5,
        // {_, 5, _}->3,
        // {15, 3, _}->12,
        // {_, x:2|3|8, _}:> x,
        // {_, x:11|12, _} :> x - 1,
        // {11, _, _}->13,
        // {13, _, 1|2|3|5|6|10|11}-> 15,
        // {13, 0, 8}->15,
        // {14, _, 6|10}->15,
        // {10, 0|9|13, 6|10}->15,
        // {6, _, 6}->0,
        // {_, _, 10}->9,
        // {6|10, 15, 9}->14,
        // {_, 6|10, 9|14|15}->10,
        // {_, 6|10, _}->6,
        // {6|10, 15, _}->13,
        // {13|14, _, 9|15}->14,
        // {13|14, _, _}->13,
        // {_, _, 15}->15,
        // {_, _, 9|14}->9,
        // {_, _, _}->0},
        // init = {{10, 0, 4, 8}, 0}},
        primeRule = new int[4096];
        int n = 0;
        //With[{rule = {{13, 3, 13}->12,
        //#0
        n = 13 * 256 + 3 * 16 + 13;
        primeRule[n] = 12;
        // {6, _, 4}->15,
        //#1
        for (int middle = 0; middle < 16; middle++) {
            primeRule[6 * 256 + middle * 16 + 4] = 15;
        }
        // {10, _, 3|11}->15,
        //#2
        for (int middle = 0; middle < 16; middle++) {
            primeRule[10 * 256 + middle * 16 + 3] = 15;
            primeRule[10 * 256 + middle * 16 + 11] = 15;
        }
        // {13, 7, _}->8,
        //#3
        for (int right = 0; right < 16; right++) {
            primeRule[13 * 256 + 7 * 16 + right] = 8;
        }
        // {13, 8, 7}->13,
        //#4
        primeRule[13 * 256 + 8 * 16 + 7] = 13;
        // {15, 8, _}->1,
        //#5
        for (int right = 0; right < 16; right++) {
            primeRule[15 * 256 + 8 * 16 + right] = 1;
        }
        // {8, _, _}->7,
        //#6
        for (int middle = 0; middle < 16; middle++) {
            for (int right = 0; right < 16; right++) {
                primeRule[8 * 256 + middle * 16 + right] = 7;
            }
        }
        // {15, 1, _}->2,
        //#7
        for (int right = 0; right < 16; right++) {
            primeRule[15 * 256 + 16 + right] = 2;
        }
        // {_, 1, _}->1,
        //#8
        for (int left = 0; left < 16; left++) {
            for (int right = 0; right < 16; right++) {
                primeRule[left * 256 + 16 + right] = 1;
            }
        }
        // {1, _, _}->8,
        //#9
        for (int middle = 0; middle < 16; middle++) {
            for (int right = 0; right < 16; right++) {
                primeRule[256 + middle * 16 + right] = 8;
            }
        }
        // {2|4|5, _, _}->13,
        //#10
        for (int middle = 0; middle < 16; middle++) {
            for (int right = 0; right < 16; right++) {
                primeRule[2 * 256 + middle * 16 + right] = 13;
                primeRule[4 * 256 + middle * 16 + right] = 13;
                primeRule[5 * 256 + middle * 16 + right] = 13;
            }
        }
        // {15, 2, _}->4,
        //#11
        for (int right = 0; right < 16; right++) {
            primeRule[15 * 256 + 2 * 16 + right] = 4;
        }
        // {_, 4, 8}->4,
        //#12
        for (int left = 0; left < 16; left++) {
            primeRule[left * 256 + 4 * 16 + 8] = 4;
        }
        // {_, 4, _}->5,
        //#13
        for (int left = 0; left < 16; left++) {
            for (int right = 0; right < 16; right++) {
                primeRule[left * 256 + 4 * 16 + right] = 5;
            }
        }
        // {_, 5, _}->3,
        //#14
        for (int left = 0; left < 16; left++) {
            for (int right = 0; right < 16; right++) {
                primeRule[left * 256 + 5 * 16 + right] = 3;
            }
        }
        // {15, 3, _}->12,
        //#15
        for (int right = 0; right < 16; right++) {
            primeRule[15 * 256 + 3 * 16 + right] = 12;
        }
        // {_, x:2|3|8, _}:> x,
        //#16
        for (int left = 0; left < 16; left++) {
            for (int right = 0; right < 16; right++) {
                primeRule[left * 256 + 2 * 16 + right] = 2;
                primeRule[left * 256 + 3 * 16 + right] = 3;
                primeRule[left * 256 + 8 * 16 + right] = 8;
            }
        }
        // {_, x:11|12, _} :> x - 1,
        //#17
        for (int left = 0; left < 16; left++) {
            for (int right = 0; right < 16; right++) {
                primeRule[left * 256 + 11 * 16 + right] = 10;
                primeRule[left * 256 + 12 * 16 + right] = 11;
            }
        }
        // {11, _, _}->13,
        //#18
        for (int middle = 0; middle < 16; middle++) {
            for (int right = 0; right < 16; right++) {
                primeRule[11 * 256 + middle * 16 + right] = 13;
            }
        }
        // {13, _, 1|2|3|5|6|10|11}-> 15,
        //#19
        for (int middle = 0; middle < 16; middle++) {
            primeRule[13 * 256 + middle * 16 + 1] = 15;
            primeRule[13 * 256 + middle * 16 + 2] = 15;
            primeRule[13 * 256 + middle * 16 + 3] = 15;
            primeRule[13 * 256 + middle * 16 + 5] = 15;
            primeRule[13 * 256 + middle * 16 + 6] = 15;
            primeRule[13 * 256 + middle * 16 + 10] = 15;
            primeRule[13 * 256 + middle * 16 + 11] = 15;
        }
        // {13, 0, 8}->15,
        //#20
        primeRule[13 * 256 + 8] = 15;
        // {14, _, 6|10}->15,
        //#21
        for (int middle = 0; middle < 16; middle++) {
            primeRule[14 * 256 + middle * 16 + 6] = 15;
            primeRule[14 * 256 + middle * 16 + 10] = 15;
        }
        // {10, 0|9|13, 6|10}->15,
        //#22
        primeRule[10 * 256 + 6] = 15;
        primeRule[10 * 256 + 10] = 15;
        primeRule[10 * 256 + 9 * 16 + 6] = 15;
        primeRule[10 * 256 + 9 * 16 + 10] = 15;
        primeRule[10 * 256 + 13 * 16 + 6] = 15;
        primeRule[10 * 256 + 13 * 16 + 10] = 15;
        // {6, _, 6}->0,
        //#23
        for (int middle = 0; middle < 16; middle++) {
            primeRule[256 * 6 + middle * 16 + 6] = 0;
        }
        // {_, _, 10}->9,
        //#24
        for (int left = 0; left < 16; left++) {
            for (int middle = 0; middle < 16; middle++) {
                primeRule[left * 256 + middle * 16 + 10] = 9;
            }
        }
        // {6|10, 15, 9}->14,
        //#25
        primeRule[6 * 256 + 15 * 16 + 9] = 14;
        primeRule[10 * 256 + 15 * 16 + 9] = 14;
        // {_, 6|10, 9|14|15}->10,
        //#26
        for (int left = 0; left < 16; left++) {
            primeRule[left * 256 + 6 * 16 + 9] = 10;
            primeRule[left * 256 + 6 * 16 + 14] = 10;
            primeRule[left * 256 + 6 * 16 + 15] = 10;
            primeRule[left * 256 + 10 * 16 + 9] = 10;
            primeRule[left * 256 + 10 * 16 + 14] = 10;
            primeRule[left * 256 + 10 * 16 + 15] = 10;
        }
        // {_, 6|10, _}->6,
        //#27
        for (int left = 0; left < 16; left++) {
            for (int right = 0; right < 16; right++) {
                primeRule[left * 256 + 6 * 16 + right] = 6;
                primeRule[left * 256 + 10 * 16 + right] = 6;
            }
        }
        // {6|10, 15, _}->13,
        //#28
        for (int right = 0; right < 16; right++) {
            primeRule[6 * 256 + 15 * 16 + right] = 13;
            primeRule[10 * 256 + 15 * 16 + right] = 13;
        }
        // {13|14, _, 9|15}->14,
        //#29
        for (int middle = 0; middle < 16; middle++) {
            primeRule[13 * 256 + middle * 16 + 9] = 14;
            primeRule[13 * 256 + middle * 16 + 15] = 14;
            primeRule[14 * 256 + middle * 16 + 9] = 14;
            primeRule[14 * 256 + middle * 16 + 15] = 14;
        }
        // {13|14, _, _}->13,
        //#30
        for (int middle = 0; middle < 16; middle++) {
            for (int right = 0; right < 16; right++) {
                primeRule[13 * 256 + middle * 16 + right] = 13;
                primeRule[14 * 256 + middle * 16 + right] = 13;
            }
        }
        // {_, _, 15}->15,
        //#31
        for (int left = 0; left < 16; left++) {
            for (int middle = 0; middle < 16; middle++) {
                primeRule[left * 256 + middle * 16 + 15] = 15;
            }
        }
        // {_, _, 9|14}->9,
        //#32
        for (int left = 0; left < 16; left++) {
            for (int middle = 0; middle < 16; middle++) {
                primeRule[left * 256 + middle * 16 + 9] = 9;
                primeRule[left * 256 + middle * 16 + 14] = 9;
            }
        }
        // {_, _, _}->0},
        //#33
        for (int ns = 0; ns < 4096; ns++) {
            primeRule[ns] = 0;
        }
        // {_, _, 9|14}->9,
        //#32
        for (int left = 0; left < 16; left++) {
            for (int middle = 0; middle < 16; middle++) {
                primeRule[left * 256 + middle * 16 + 9] = 9;
                primeRule[left * 256 + middle * 16 + 14] = 9;
            }
        }
        // {_, _, 15}->15,
        //#31
        for (int left = 0; left < 16; left++) {
            for (int middle = 0; middle < 16; middle++) {
                primeRule[left * 256 + middle * 16 + 15] = 15;
            }
        }
        // {13|14, _, _}->13,
        //#30
        for (int middle = 0; middle < 16; middle++) {
            for (int right = 0; right < 16; right++) {
                primeRule[13 * 256 + middle * 16 + right] = 13;
                primeRule[14 * 256 + middle * 16 + right] = 13;
            }
        }
        // {13|14, _, 9|15}->14,
        //#29
        for (int middle = 0; middle < 16; middle++) {
            primeRule[13 * 256 + middle * 16 + 9] = 14;
            primeRule[13 * 256 + middle * 16 + 15] = 14;
            primeRule[14 * 256 + middle * 16 + 9] = 14;
            primeRule[14 * 256 + middle * 16 + 15] = 14;
        }
        // {6|10, 15, _}->13,
        //#28
        for (int right = 0; right < 16; right++) {
            primeRule[6 * 256 + 15 * 16 + right] = 13;
            primeRule[10 * 256 + 15 * 16 + right] = 13;
        }
        // {_, 6|10, _}->6,
        //#27
        for (int left = 0; left < 16; left++) {
            for (int right = 0; right < 16; right++) {
                primeRule[left * 256 + 6 * 16 + right] = 6;
                primeRule[left * 256 + 10 * 16 + right] = 6;
            }
        }
        // {_, 6|10, 9|14|15}->10,
        //#26
        for (int left = 0; left < 16; left++) {
            primeRule[left * 256 + 6 * 16 + 9] = 10;
            primeRule[left * 256 + 6 * 16 + 14] = 10;
            primeRule[left * 256 + 6 * 16 + 15] = 10;
            primeRule[left * 256 + 10 * 16 + 9] = 10;
            primeRule[left * 256 + 10 * 16 + 14] = 10;
            primeRule[left * 256 + 10 * 16 + 15] = 10;
        }
        // {6|10, 15, 9}->14,
        //#25
        primeRule[6 * 256 + 15 * 16 + 9] = 14;
        primeRule[10 * 256 + 15 * 16 + 9] = 14;
        // {_, _, 10}->9,
        //#24
        for (int left = 0; left < 16; left++) {
            for (int middle = 0; middle < 16; middle++) {
                primeRule[left * 256 + middle * 16 + 10] = 9;
            }
        }
        // {6, _, 6}->0,
        //#23
        for (int middle = 0; middle < 16; middle++) {
            primeRule[256 * 6 + middle * 16 + 6] = 0;
        }
        // {10, 0|9|13, 6|10}->15,
        //#22
        primeRule[10 * 256 + 6] = 15;
        primeRule[10 * 256 + 10] = 15;
        primeRule[10 * 256 + 9 * 16 + 6] = 15;
        primeRule[10 * 256 + 9 * 16 + 10] = 15;
        primeRule[10 * 256 + 13 * 16 + 6] = 15;
        primeRule[10 * 256 + 13 * 16 + 10] = 15;
        // {14, _, 6|10}->15,
        //#21
        for (int middle = 0; middle < 16; middle++) {
            primeRule[14 * 256 + middle * 16 + 6] = 15;
            primeRule[14 * 256 + middle * 16 + 10] = 15;
        }
        // {13, 0, 8}->15,
        //#20
        primeRule[13 * 256 + 8] = 15;
        // {13, _, 1|2|3|5|6|10|11}-> 15,
        //#19
        for (int middle = 0; middle < 16; middle++) {
            primeRule[13 * 256 + middle * 16 + 1] = 15;
            primeRule[13 * 256 + middle * 16 + 2] = 15;
            primeRule[13 * 256 + middle * 16 + 3] = 15;
            primeRule[13 * 256 + middle * 16 + 5] = 15;
            primeRule[13 * 256 + middle * 16 + 6] = 15;
            primeRule[13 * 256 + middle * 16 + 10] = 15;
            primeRule[13 * 256 + middle * 16 + 11] = 15;
        }
        // {11, _, _}->13,
        //#18
        for (int middle = 0; middle < 16; middle++) {
            for (int right = 0; right < 16; right++) {
                primeRule[11 * 256 + middle * 16 + right] = 13;
            }
        }
        // {_, x:11|12, _} :> x - 1,
        //#17
        for (int left = 0; left < 16; left++) {
            for (int right = 0; right < 16; right++) {
                primeRule[left * 256 + 11 * 16 + right] = 10;
                primeRule[left * 256 + 12 * 16 + right] = 11;
            }
        }
        // {_, x:2|3|8, _}:> x,
        //#16
        for (int left = 0; left < 16; left++) {
            for (int right = 0; right < 16; right++) {
                primeRule[left * 256 + 2 * 16 + right] = 2;
                primeRule[left * 256 + 3 * 16 + right] = 3;
                primeRule[left * 256 + 8 * 16 + right] = 8;
            }
        }
        // {15, 3, _}->12,
        //#15
        for (int right = 0; right < 16; right++) {
            primeRule[15 * 256 + 3 * 16 + right] = 12;
        }
        // {_, 5, _}->3,
        //#14
        for (int left = 0; left < 16; left++) {
            for (int right = 0; right < 16; right++) {
                primeRule[left * 256 + 5 * 16 + right] = 3;
            }
        }
        //#13
        for (int left = 0; left < 16; left++) {
            for (int right = 0; right < 16; right++) {
                primeRule[left * 256 + 4 * 16 + right] = 5;
            }
        }
        // {_, 4, 8}->4,
        //#12
        for (int left = 0; left < 16; left++) {
            primeRule[left * 256 + 4 * 16 + 8] = 4;
        }
        // {15, 2, _}->4,
        //#11
        for (int right = 0; right < 16; right++) {
            primeRule[15 * 256 + 2 * 16 + right] = 4;
        }
        // {2|4|5, _, _}->13,
        //#10
        for (int middle = 0; middle < 16; middle++) {
            for (int right = 0; right < 16; right++) {
                primeRule[2 * 256 + middle * 16 + right] = 13;
                primeRule[4 * 256 + middle * 16 + right] = 13;
                primeRule[5 * 256 + middle * 16 + right] = 13;
            }
        }
        // {1, _, _}->8,
        //#9
        for (int middle = 0; middle < 16; middle++) {
            for (int right = 0; right < 16; right++) {
                primeRule[256 + middle * 16 + right] = 8;
            }
        }
        // {_, 1, _}->1,
        //#8
        for (int left = 0; left < 16; left++) {
            for (int right = 0; right < 16; right++) {
                primeRule[left * 256 + 16 + right] = 1;
            }
        }
        // {15, 1, _}->2,
        //#7
        for (int right = 0; right < 16; right++) {
            primeRule[15 * 256 + 16 + right] = 2;
        }
        // {8, _, _}->7,
        //#6
        for (int middle = 0; middle < 16; middle++) {
            for (int right = 0; right < 16; right++) {
                primeRule[8 * 256 + middle * 16 + right] = 7;
            }
        }
        // {15, 8, _}->1,
        //#5
        for (int right = 0; right < 16; right++) {
            primeRule[15 * 256 + 8 * 16 + right] = 1;
        }
        // {13, 8, 7}->13,
        //#4
        primeRule[13 * 256 + 8 * 16 + 7] = 13;
        // {13, 7, _}->8,
        //#3
        for (int right = 0; right < 16; right++) {
            primeRule[13 * 256 + 7 * 16 + right] = 8;
        }
        // {10, _, 3|11}->15,
        //#2
        for (int middle = 0; middle < 16; middle++) {
            primeRule[10 * 256 + middle * 16 + 3] = 15;
            primeRule[10 * 256 + middle * 16 + 11] = 15;
        }
        // {6, _, 4}->15,
        //#1
        for (int middle = 0; middle < 16; middle++) {
            primeRule[6 * 256 + middle * 16 + 4] = 15;
        }
        //With[{rule = {{13, 3, 13}->12,
        //#0
        n = 13 * 256 + 3 * 16 + 13;
        primeRule[n] = 12;
        //does a stretch, a long conversion
        int[] temp = new int[primeRule.length];
        for (int spot = 0; spot < primeRule.length; spot++) {
            int tot = 0;
            for (int power = 0; power < 3; power++) {
                tot += (int) Math.pow(16, 2 - power) * ((spot / (int) Math.pow(16, power)) % 16);
            }
            temp[tot] = primeRule[spot];
        }
        primeRule = temp;
        extRule = new int[3][4096 * 256];
        longRule = new long[primeRule.length];
        for (int spot = 0; spot < 4096; spot++) {
            extRule[0][spot] = primeRule[spot];
            longRule[spot] = (long) primeRule[spot];
        }
        for (int row = 1; row < 3; row++) {
            for (int spot = 0; spot < 4096 * (int) Math.pow(16, row); spot++) {
                int tot = 0;
                for (int power = 0; power < 2 * row + 1; power++) {
                    tot += (int) Math.pow(16, power) * primeRule[((spot / (int) Math.pow(16, power))) % 4096];
                }
                extRule[row][spot] = extRule[row - 1][tot];
            }
        }
        //generates the output
        primeField = new int[1000][1000];
        primeField[0][498] = 10;
        primeField[0][500] = 4;
        primeField[0][501] = 8;
        for (int row = 1; row < 1000; row++) {
            for (int column = 1; column < 1000 - 1; column++) {
                primeField[row][column] = 256 * primeField[row - 1][column + 1] + 16 * primeField[row - 1][column] + primeField[row - 1][column - 1];
                primeField[row][column] = primeRule[primeField[row][column]];
            }
        }
        primeFieldPowers = new int[4][1000][1000];
        for (int row = 0; row < 1000; row++) {
            for (int column = 0; column < 1000; column++) {
                for (int power = 0; power < 4; power++) {
                    primeFieldPowers[power][row][column] = primeField[row][column] & ((int) Math.pow(2, power));
                }
            }
        }
    }

    /**
     * Input, stretch, feedback algorithm to find offset equal center columns
     *
     * @param n           Wolfram code of primeCA
     * @param initRow     row conversion of how long the input neighborhood is
     * @param feedbackRow at what point to start feeding it back to feedbackRow-1?
     * @return all iterations all solutions all rows
     */
    public long[][][] doLogReductionPrimeTwo(int[] n, int initRow, int feedbackRow) {
        //
        //
        //Initialization
        long[] wolfram = new long[n.length];
        for (int spot = 0; spot < n.length; spot++) {
            wolfram[spot] = (long) n[spot];
        }
        long initialValue = 0;
        int rowZero = initRow;
        BigInteger iv = new BigInteger(String.valueOf(initialValue));
        long[] initial = new long[]{0, 0, 0, 10, 0, 4, 8};
        for (int spot = 0; spot < initial.length; spot++) {
            initialValue += (1 << (4 * spot)) * initial[spot];
        }
        long location = 4096 * 10 + 4096 * 256 * 4 + 4096 * 256 * 16 * 8;
        System.out.println("initalValue " + initialValue);
        iv = BigInteger.valueOf(initialValue);
        System.out.println("location " + location);
        iv = BigInteger.valueOf(location);
        System.out.println("initialValue " + initialValue + " BI iv " + iv.toString());
        long[][][] passes = new long[32][24][64];
        long[][] dec = new long[passes.length][passes[0].length];
        int maxRows = 10;
        long value;
        long[] values = new long[32];
        long[] active;
        values[0] = initialValue;
        int lengthRepeat = -1;
        for (int pass = 0; pass < 32 && lengthRepeat == -1; pass++) {
            for (int row = 0; row < maxRows; row++) {
                int powers = 1 + 2 * (row - 1);
                for (int power = 0; power < powers; power++) {
                    passes[pass][row][power] = -1;
                }
            }
        }
        value = initialValue;
        active = initial;
        BigInteger[][] decbi = new BigInteger[dec.length][dec[0].length];
        BigInteger[] biValues = new BigInteger[values.length];
        for (int spot = 0; spot < biValues.length; spot++) {
            biValues[spot] = new BigInteger(String.valueOf(0));
        }
        //
        //
        //Main loop, does it until it finds a repeat
        passLoop:
        for (int pass = 0; pass < 32 && lengthRepeat == -1; pass++) {
            //System.out.println("\n\n\n\nvalue " + value);
            long tot = 0;
            //
            //
            //Stretch loop, multiplies by 16 each time, adding a zero to each side of the neighborhood
            for (int row = initRow; row < maxRows; row++) {
                int powers = 1 + 2 * (row - 1);
                for (int power = 0; power < powers; power++) {
//                    if ((int)((value>>(4*power))%wolfram.length) < 0) {
//                        break passLoop;
//                    }
                    BigInteger po = BigInteger.valueOf(16).pow(power);
                    //System.out.println("po " + po);
                    BigInteger result = iv.divide(po);
                    result = result.mod(BigInteger.valueOf(wolfram.length));
                    //passes[pass][row][power] = wolfram[(int) ((value >> (4 * power)) % wolfram.length)];
                    passes[pass][row][power] = wolfram[result.intValue()];
                }
                iv = iv.multiply(BigInteger.valueOf(16));
//                long[] temp = new long[active.length+2];
//                for (int spot = 1; spot < temp.length-1; spot++){
//                    temp[spot] = active[spot-1];
//                }
//                active = temp;
//                value *= 16;
            }
            //totals the rows for decimal format
            rowLoop:
            for (int row = rowZero; row < maxRows; row++) {
                //tot = 0;
                int powers = 1 + 2 * (row - 1);
                decbi[pass][row] = new BigInteger(String.valueOf(0));
                BigInteger po = BigInteger.valueOf(1);
                for (int power = 0; power < powers; power++) {
                    po = BigInteger.valueOf(16).pow(power);
                    //System.out.println("power " + power);
                    //tot += (passes[pass][row][power] << (4 * power));
                    decbi[pass][row] = decbi[pass][row].add((BigInteger.valueOf((passes[pass][row][power]))).multiply(po));
                    //po = po.multiply(BigInteger.valueOf(16));
                }
                //dec[pass][row] = tot;
            }
            //value = dec[pass][feedbackRow];
            //values[pass] = value;
            //
            //
            //Updates final iteration value, sets initialization row if first iteration, checks for repeats
            iv = decbi[pass][feedbackRow];
            biValues[pass] = iv;
            initRow = feedbackRow - 1;
//            long[] temp = new long[active.length-2];
//            for (int power = 0; power < temp.length; power++) {
//                temp[power] = active[power] + 16 * active[power + 1] + 256 * active[power + 2];
//                temp[power] = wolfram[(int) temp[power]];
//            }
//            active = temp;
            for (int passs = 0; passs < pass; passs++) {
                if (values[passs] == values[pass]) {
                    //lengthRepeat = pass;
                    //passRepeat = pass;
                }
                if (biValues[passs].compareTo(biValues[pass]) == 0) {
                    lengthRepeat = pass;
                    passRepeat = pass;
                }
            }
            //initRow = llv - 1;
            //Output and an exit clause
            System.out.print("\n");
            System.out.println("passes[" + pass + "]");
            for (int row = rowZero; row < maxRows; row++) {
                for (int column = 0; column < 1 + 2 * (row - 1); column++) {
                    System.out.print(String.format("% 2d", passes[pass][row][column]));
                }
                System.out.print(" " + decbi[pass][row].toString() + "\n");
            }
            if (lengthRepeat != -1) break passLoop;
            System.out.print("\n");
        }
        if (passRepeat == 0) passRepeat = 32;
        System.out.println("repeat " + passRepeat);
        //
        //
        //Compare produced neighborhoods' center columns with the function input neighborhood, with a row-wise offset
        int[] a = Arrays.copyOfRange(traditionalCenterColumn(wolfram, initial, 256, 1024), 0, 128);
        for (int pass = 0; pass < passRepeat; pass++) {
            long[] passArr = Arrays.copyOfRange(passes[pass][feedbackRow], 0, 2 * (feedbackRow - 1) + 1);
            int[] b = Arrays.copyOfRange(traditionalCenterColumn(wolfram, passArr, 256, 1024), 0, 128);
            //            System.out.println("Pass " + pass);
//            System.out.println(Arrays.toString(passArr));
//            System.out.println(Arrays.toString(a));
//            System.out.println(Arrays.toString(b));
            for (int spot = 0; spot < 64; spot++) {
                if (Arrays.equals(Arrays.copyOfRange(b, 1, 33), Arrays.copyOfRange(a, spot, spot + 32))) {
                    System.out.println("Pass " + pass + " Spot " + spot);
                    System.out.println(dec[pass][feedbackRow]);
                }
            }
        }
        return passes;
    }


    /**
     * Takes an input neighborhood's center column and checks other numerically close neighborhood's center columns
     *
     * @param wolfram     primeCA Wolfram code
     * @param startRow    length of input neighborhood in row form
     * @param feedbackRow at what point to start feeding the stretched neighborhood back in?
     */
    public void checkAllColumnsTwo(long[] wolfram, int startRow, int feedbackRow) {
        long[][][] passes = doLogReductionPrimeTwo(primeRule, startRow, feedbackRow);
        int length = 2 * (feedbackRow - 1) + 1;
        //length = 2 * feedbackRow + 1;
        long[] input = new long[length];
        long[] l = new long[length + 1];
        l[0] = 1;
        for (int n = 1; n <= length; n++) {
            l[n] = l[n - 1] * 16;
        }
        int lengthCompare = 16;
        int[] mainColumn = traditionalCenterColumnLong(wolfram, new long[]{0, 0, 0, 10, 0, 4, 8}, 512, 2048);
        //mainColumn = Arrays.copyOfRange(mainColumn,0,lengthCompare);
        System.out.println(Arrays.toString(mainColumn));
        int[] comp;
        long total = 0;
        long location = 4096 * 10 + 4096 * 256 * 4 + 4096 * 256 * 16 * 8;
        location *= 16;
        //location += 256*256*256*256;
        System.out.println("length of loop " + l[length]);
        //for every neighborhood in feedbackRow
        for (int pspot = 0; pspot < passRepeat; pspot++) {
            location = 0;
            for (int spot = 0; spot < length; spot++) {
                location += l[spot] * passes[pspot][feedbackRow][spot];
            }
            //location = 4096 * 10 + 4096 * 256 * 4 + 4096 * 256 * 16 * 8;
            //location *= (16*(feedbackRow-3));
            //check nearby locations
            for (long n = location; n < location + 20; n++) {
                if (n % 1000000 == 0) System.out.println("n " + n + " remaining " + (l[length] - n));
                for (int power = 0; power < length; power++) {
                    // input[power] = ((n / l[power]) % 16);
                    BigInteger p = BigInteger.valueOf(16);
                    BigInteger po = p.pow(power);
                    //System.out.println(po.toString(10));
                    BigInteger value = BigInteger.valueOf(n);
                    input[power] = (value.divide(po).mod(BigInteger.valueOf(16)).intValue());
                }
                // input = passes[pspot][feedbackRow-1];
                //input[input.length - 1] = 1;
                //input = new long[]{0,0,0,10,0,4,8};
                System.out.println(Arrays.toString(input));
                comp = traditionalCenterColumnLong(wolfram, input, 512, 2048);
                //comp = Arrays.copyOfRange(comp,0,lengthCompare);
                //System.out.println(Arrays.toString(Arrays.copyOfRange(comp, 0, 256)));
                //System.out.println(Arrays.toString(Arrays.copyOfRange(mainColumn, 0, 256)));
                //System.out.println("comp " +Arrays.toString( comp));
                //check at a range of spots row-wise offsets for equality
                for (int spot = 0; spot < 64; spot++) {
                    int[] c = Arrays.copyOfRange(comp, 1, 17);
                    int[] m = Arrays.copyOfRange(mainColumn, spot, spot + 16);
                    //System.out.println(Arrays.toString(c));
                    //System.out.println(Arrays.toString(m));
                    if (Arrays.equals(c, m)) {
                        System.out.println("Found");
                        System.out.println("spot " + spot + " " + Arrays.toString(input));
                        System.out.println(Arrays.toString(traditionalCenterColumnLongSpot(wolfram, input, 20, 512, spot)));
                        System.out.println();
//                    System.out.println(Arrays.toString(mainColumn));
//                    System.out.println(Arrays.toString(comp));
                        //System.out.println();
                        for (int compLength = 16; compLength < 256; compLength++) {
                            //if (compLength < 20) System.out.println("compLength" + compLength);
                            if (Arrays.equals(Arrays.copyOfRange(comp, 1, 1 + compLength), Arrays.copyOfRange(mainColumn, spot, spot + compLength))) {
                            } else {
                                //System.out.println("Breaks down at " + compLength);
                                //System.out.println();
                                break;
                            }
                        }
                        total++;
                    }
                }
            }
            //System.out.println("total " + total);
        }
    }
        /**
     * Finds the center column of an input neighborhood
     *
     * @param in      primeCA Wolfram code
     * @param inputIn input neighborhood
     * @param numRows number of rows to return
     * @param size    size of output space, must be > 2*numRows
     * @return the center column of inputIn[]'s output
     */
    public int[] traditionalCenterColumnLong(long[] in, long[] inputIn, int numRows, int size) {
        //
        //
        //Initialization
//        int[] n = new int[in.length];
//        for (int spot = 0; spot < in.length; spot++) {
//            n[spot] = (int) in[spot];
//        }
//        int[] input = new int[inputIn.length];
//        for (int spot = 0; spot < inputIn.length; spot++) {
//            input[spot] = (int) inputIn[spot];
//        }
        int[] columnOut = new int[numRows];
        //code = basicECA.ruleExtension(30);
        //System.out.println(Arrays.toString(code[5]));
        //code[3] = new int[]{0,1,1,1,1,0,0,0};
        long[][] field = new long[size][size];
        //field[0][128] = 1;
        for (int column = size / 2 - inputIn.length / 2; column <= size / 2 + inputIn.length / 2; column++) {
            //System.out.println("column " + column);
            field[0][column] = inputIn[column + inputIn.length / 2 - size / 2];
        }
        //System.out.println(Arrays.toString(field[0]));
        columnOut[0] = (int) field[0][size / 2];
        //calculate neighborhood
        //System.out.println("size/2 " + (size/2));
        //System.out.println("size/2 - input.length/2 " + (size/2-input.length/2));
        //Run Wolfram code on input
        int column = 0;
        for (int row = 1; row < numRows; row++) {
            for (column = 1; column < size - 1; column++) {
//                for (int bit = 0; bit < 3; bit++) {
//                    field[row][ccolumn] += (int) Math.pow(16, bit) * field[row - 1][ccolumn - 1 + bit];
//                }
                field[row][column] = field[row - 1][column - 1] + 16 * field[row - 1][column] + 16 * 16 * field[row - 1][column + 1];
                field[row][column] = in[(int) field[row][column]];
            }
            columnOut[row] = (int) field[row][size / 2];
        }
        for (int row = 0; row < 10; row++) {
            //System.out.println(Arrays.toString(Arrays.copyOfRange(field[row],size/2-10,size/2+10)));
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
        /**
     * Finds the center column of an input neighborhood
     *
     * @param in      primeCA Wolfram code
     * @param inputIn input neighborhood
     * @param numRows number of rows to return
     * @param size    size of output space, must be > 2*numRows
     * @return the center column of inputIn[]'s output
     */
    public int[] traditionalCenterColumn(long[] in, long[] inputIn, int numRows, int size) {
        //
        //
        //Initialization
        int[] n = new int[in.length];
        for (int spot = 0; spot < in.length; spot++) {
            n[spot] = (int) in[spot];
        }
        int[] input = new int[inputIn.length];
        for (int spot = 0; spot < inputIn.length; spot++) {
            input[spot] = (int) inputIn[spot];
        }
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
        columnOut[0] = field[0][size / 2];
        //calculate neighborhood
        //System.out.println("size/2 " + (size/2));
        //System.out.println("size/2 - input.length/2 " + (size/2-input.length/2));
        //Run Wolfram code on input
        for (int row = 1; row < numRows; row++) {
            for (int ccolumn = row; ccolumn < size - 1 - row; ccolumn++) {
                for (int bit = 0; bit < 3; bit++) {
                    field[row][ccolumn] += (int) Math.pow(16, bit) * field[row - 1][ccolumn - 1 + bit];
                }
                field[row][ccolumn] = n[field[row][ccolumn]];
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


    /**
     * Finds the center column of an input neighborhood
     *
     * @param in      primeCA Wolfram code
     * @param inputIn input neighborhood
     * @param numRows number of rows to return
     * @param size    size of output space, must be > 2*numRows
     * @param spot    offset row of center column
     * @return the center column of inputIn[]'s output
     */
    public int[] traditionalCenterColumnLongSpot(long[] in, long[] inputIn, int numRows, int size, int spot) {
        //Initialize
//        int[] n = new int[in.length];
//        for (int spot = 0; spot < in.length; spot++) {
//            n[spot] = (int) in[spot];
//        }
//        int[] input = new int[inputIn.length];
//        for (int spot = 0; spot < inputIn.length; spot++) {
//            input[spot] = (int) inputIn[spot];
//        }
        int[] columnOut = new int[numRows];
        //code = basicECA.ruleExtension(30);
        //System.out.println(Arrays.toString(code[5]));
        //code[3] = new int[]{0,1,1,1,1,0,0,0};
        long[][] field = new long[size][size];
        //field[0][128] = 1;
        for (int column = size / 2 - inputIn.length / 2; column <= size / 2 + inputIn.length / 2; column++) {
            //System.out.println("column " + column);
            field[0][column] = inputIn[column + inputIn.length / 2 - size / 2];
        }
        //System.out.println(Arrays.toString(field[0]));
        columnOut[0] = (int) field[0][size / 2];
        //calculate neighborhood
        //System.out.println("size/2 " + (size/2));
        //System.out.println("size/2 - input.length/2 " + (size/2-input.length/2));
        int column = 0;
        //Run Wolfram code on input
        for (int row = 1; row < numRows; row++) {
            for (column = 1; column < size - 1; column++) {
//                for (int bit = 0; bit < 3; bit++) {
//                    field[row][ccolumn] += (int) Math.pow(16, bit) * field[row - 1][ccolumn - 1 + bit];
//                }
                field[row][column] = field[row - 1][column - 1] + 16 * field[row - 1][column] + 16 * 16 * field[row - 1][column + 1];
                field[row][column] = in[(int) field[row][column]];
            }
            columnOut[row] = (int) field[row][size / 2];
        }
        for (int row = 0; row < 10; row++) {
            //System.out.println(Arrays.toString(Arrays.copyOfRange(field[row],size/2-10,size/2+10)));
        }
//        System.out.print("\n");
//        for (int row = 0; row < numRows; row++) {
//            for (int ccolumn = size/2-numRows; ccolumn < size/2+numRows; ccolumn++) {
//                System.out.print(field[row][ccolumn]);
//            }
//            System.out.print("\n");
//        }
//        System.out.print("\n");
        int[] neighborhoodOut = new int[spot * 2 + 1];
        for (column = 0; column < neighborhoodOut.length / 2; column++) {
            neighborhoodOut[column] = (int) field[0 + column][size / 2 - neighborhoodOut.length / 2 + column];
        }
        for (column = neighborhoodOut.length / 2; column < neighborhoodOut.length; column++) {
            neighborhoodOut[column] = (int) field[column - 2 * (column - neighborhoodOut.length / 2)][size / 2 - neighborhoodOut.length / 2 + column];
        }
        return neighborhoodOut;
    }

    /**
     * Input, stretch, feedback algorithm to find offset equal center columns
     *
     * @param n           Wolfram code of primeCA
     * @param initRow     row conversion of how long the input neighborhood is
     * @param feedbackRow at what point to start feeding it back to feedbackRow-1?
     * @return all iterations all solutions all rows
     */
    public long[][][] doLogReductionPrimeThree(int[] n, int initRow, int feedbackRow) {
        //
        //
        //Initilization
        long[] wolfram = new long[n.length];
        for (int spot = 0; spot < n.length; spot++) {
            wolfram[spot] = (long) n[spot];
        }
        long initialValue = 0;
        int rowZero = initRow;
        BigInteger iv = new BigInteger(String.valueOf(initialValue));
        long[] initial = new long[]{0, 0, 0, 10, 0, 4, 8};
        for (int spot = 0; spot < initial.length; spot++) {
            initialValue += (1 << (4 * spot)) * initial[spot];
        }
        long location = 4096 * 10 + 4096 * 256 * 4 + 4096 * 256 * 16 * 8;
        System.out.println("initalValue " + initialValue);
        iv = BigInteger.valueOf(initialValue);
        System.out.println("location " + location);
        iv = BigInteger.valueOf(location);
        System.out.println("initialValue " + initialValue + " BI iv " + iv.toString());
        long[][][] passes = new long[32][24][64];
        long[][] dec = new long[passes.length][passes[0].length];
        int maxRows = 10;
        long value;
        long[] values = new long[32];
        long[] active;
        values[0] = initialValue;
        int lengthRepeat = -1;
        for (int pass = 0; pass < 32 && lengthRepeat == -1; pass++) {
            for (int row = 0; row < maxRows; row++) {
                int powers = 1 + 2 * (row - 1);
                for (int power = 0; power < powers; power++) {
                    passes[pass][row][power] = -1;
                }
            }
        }
        value = initialValue;
        active = initial;
        BigInteger[][] decbi = new BigInteger[dec.length][dec[0].length];
        BigInteger[] biValues = new BigInteger[values.length];
        for (int spot = 0; spot < biValues.length; spot++) {
            biValues[spot] = new BigInteger(String.valueOf(0));
        }
        int lastSpot = 0;
        //
        //
        //Main loop, goes until it finds a repeat
        passLoop:
        for (int pass = 0; pass < 32 && lengthRepeat == -1; pass++) {
            //System.out.println("\n\n\n\nvalue " + value);
            long tot = 0;
            //Stretch loop, finds the address of the input in the stretched Wolfram[(address/2^row)%WolframLength], adds a zero on either side, and repeats
            for (int row = initRow; row < maxRows; row++) {
                int powers = 1 + 2 * (row - 1);
                for (int power = 0; power < powers; power++) {
//                    if ((int)((value>>(4*power))%wolfram.length) < 0) {
//                        break passLoop;
//                    }
                    BigInteger po = BigInteger.valueOf(16).pow(power);
                    //System.out.println("po " + po);
                    BigInteger result = iv.divide(po);
                    result = result.mod(BigInteger.valueOf(wolfram.length));
                    //passes[pass][row][power] = wolfram[(int) ((value >> (4 * power)) % wolfram.length)];
                    passes[pass][row][power] = wolfram[result.intValue()];
                }
                iv = iv.multiply(BigInteger.valueOf(16));
//                long[] temp = new long[active.length+2];
//                for (int spot = 1; spot < temp.length-1; spot++){
//                    temp[spot] = active[spot-1];
//                }
//                active = temp;
//                value *= 16;
            }
            //Totals the results into decimal form
            rowLoop:
            for (int row = rowZero; row < maxRows; row++) {
                //tot = 0;
                int powers = 1 + 2 * (row - 1);
                decbi[pass][row] = new BigInteger(String.valueOf(0));
                BigInteger po = BigInteger.valueOf(1);
                for (int power = 0; power < powers; power++) {
                    po = BigInteger.valueOf(16).pow(power);
                    //System.out.println("power " + power);
                    //tot += (passes[pass][row][power] << (4 * power));
                    decbi[pass][row] = decbi[pass][row].add((BigInteger.valueOf((passes[pass][row][power]))).multiply(po));
                    //po = po.multiply(BigInteger.valueOf(16));
                }
                //dec[pass][row] = tot;
            }
            //value = dec[pass][feedbackRow];
            //values[pass] = value;
            //Sets the final value of the pass
            //And if it is the zeroth pass it resets the init row to feedbackRow-1
            iv = decbi[pass][feedbackRow];
            biValues[pass] = iv;
            initRow = feedbackRow - 1;
//            long[] temp = new long[active.length-2];
//            for (int power = 0; power < temp.length; power++) {
//                temp[power] = active[power] + 16 * active[power + 1] + 256 * active[power + 2];
//                temp[power] = wolfram[(int) temp[power]];
//            }
//            active = temp;
            //Checks if the final result of the pass is a repeat
            for (int passs = 0; passs < pass; passs++) {
                if (values[passs] == values[pass]) {
                    //lengthRepeat = pass;
                    //passRepeat = pass;
                }
                if (biValues[passs].compareTo(biValues[pass]) == 0) {
                    lengthRepeat = pass;
                    passRepeat = pass;
                }
            }
            //initRow = llv - 1;
            //Output
            System.out.print("\n");
            System.out.println("passes[" + pass + "]");
            for (int row = rowZero; row < maxRows; row++) {
                for (int column = 0; column < 1 + 2 * (row - 1); column++) {
                    System.out.print(String.format("% 2d", passes[pass][row][column]));
                }
                System.out.print(" " + decbi[pass][row].toString() + "\n");
            }
            if (lengthRepeat != -1) break passLoop;
            System.out.print("\n");
        }
        if (passRepeat == 0) passRepeat = 32;
        System.out.println("repeat " + passRepeat);
        int lastPass = 0;
        //Checks all the results against the input neighborhood's center column at a row-wise offset for equality
        System.out.println("Identical center columns with phase spot");
        int[] a = Arrays.copyOfRange(traditionalCenterColumn(wolfram, initial, 256, 1024), 0, 128);
        for (int pass = 0; pass < passRepeat; pass++) {
            long[] passArr = Arrays.copyOfRange(passes[pass][feedbackRow], 0, 2 * (feedbackRow - 1) + 1);
            int[] b = Arrays.copyOfRange(traditionalCenterColumn(wolfram, passArr, 256, 1024), 0, 128);
            //            System.out.println("Pass " + pass);
//            System.out.println(Arrays.toString(passArr));
//            System.out.println(Arrays.toString(a));
//            System.out.println(Arrays.toString(b));
            for (int spot = 0; spot < 64; spot++) {
                if (Arrays.equals(Arrays.copyOfRange(b, 1, 33), Arrays.copyOfRange(a, spot, spot + 32))) {
                    System.out.println("Pass " + pass + " Spot " + spot);
                    System.out.println(dec[pass][feedbackRow]);
                    lastSpot = spot;
                    lastPass = pass;
                }
            }
        }
        System.out.println("lastPass " + lastPass + " " + lastSpot);
        System.out.println("Non-identical center columns with phase lastSpot");
        lastSpot++;
        lastPass++;
        //Checks the results beyond the feebackRow-initRow point
        for (int pass = lastPass; pass < passRepeat; pass++) {
            long[] passArr = Arrays.copyOfRange(passes[pass][feedbackRow], 0, 2 * (feedbackRow - 1) + 1);
            int[] b = Arrays.copyOfRange(traditionalCenterColumn(wolfram, passArr, 256, 1024), 0, 128);
            //            System.out.println("Pass " + pass);
//            System.out.println(Arrays.toString(passArr));
//            System.out.println(Arrays.toString(a));
//            System.out.println(Arrays.toString(b));
            for (int spot = lastSpot; spot < lastSpot + 1; spot++) {
                System.out.println("Pass " + pass + " Spot " + spot);
                System.out.println(dec[pass][feedbackRow]);
                lastSpot = spot;
                int[] column = traditionalCenterColumn(longRule, passArr, 32, 128);
                System.out.println(Arrays.toString(column));
                System.out.println();
            }
            lastSpot++;
        }
        return passes;
    }





    /**
     * Takes an input neighborhood's center column and compares it to other neighborhoods numerically close to it
     *
     * @param wolfram     primeCA Wolfram code
     * @param startRow    length of input neighborhood
     * @param feedbackRow at what row to start feeding back
     */
    public void checkAllColumns(long[] wolfram, int startRow, int feedbackRow) {
        long[][][] passes = doLogReductionPrimeTwo(primeRule, startRow, feedbackRow);
        int length = 2 * (feedbackRow - 1) + 1;
        length = 2 * startRow + 1;
        long[] input = new long[length];
        long[] l = new long[length + 1];
        l[0] = 1;
        for (int n = 1; n <= length; n++) {
            l[n] = l[n - 1] * 16;
        }
        int[] mainColumn = traditionalCenterColumnLong(wolfram, new long[]{0, 0, 0, 10, 0, 4, 8}, 256, 1024);
        System.out.println(Arrays.toString(mainColumn));
        int[] comp;
        long total = 0;
        long location = 4096 * 10 + 4096 * 256 * 4 + 4096 * 256 * 16 * 8;
        location *= 16;
//        for (int spot = 0; spot < length; spot++){
//            location += l[spot]*
//        }
        //location += 256*256*256*256;
        System.out.println("length of loop " + l[length]);
        //for neighborhoods close to it
        for (long n = location; n < location + 20; n++) {
            if (n % 1000000 == 0) System.out.println("n " + n + " remaining " + (l[length] - n));
            for (int power = 0; power < length; power++) {
                input[power] = ((n / l[power]) % 16);
            }
            input[input.length - 1] = 1;
            //input = new long[]{0,0,0,10,0,4,8};
            System.out.println(Arrays.toString(input));
            comp = traditionalCenterColumnLong(wolfram, input, 256, 1024);
            System.out.println(Arrays.toString(Arrays.copyOfRange(comp, 0, 256)));
            System.out.println(Arrays.toString(Arrays.copyOfRange(mainColumn, 0, 256)));
            //System.out.println("comp " +Arrays.toString( comp));
            //Row-wise offset loop
            for (int spot = 0; spot < 10; spot++) {
                if (Arrays.equals(Arrays.copyOfRange(comp, 1, 17), Arrays.copyOfRange(mainColumn, spot, spot + 16))) {
                    System.out.println("Found");
                    System.out.println("spot " + spot + " " + Arrays.toString(input));
//                    System.out.println(Arrays.toString(mainColumn));
//                    System.out.println(Arrays.toString(comp));
                    //System.out.println();
                    for (int compLength = 16; compLength < 256; compLength++) {
                        if (compLength < 20) System.out.println("compLength" + compLength);
                        if (Arrays.equals(Arrays.copyOfRange(comp, 1, 1 + compLength), Arrays.copyOfRange(mainColumn, spot, spot + compLength))) {
                        } else {
                            System.out.println("Breaks down at " + compLength);
                            System.out.println();
                            break;
                        }
                    }
                    total++;
                }
            }
        }
        System.out.println("total " + total);
    }










    /**
     * A version of the input, stretch, feedback algorithm that produces an equal but row-wise offset center column, applied to the hexadecimal prime number automata [citation]
     *
     * @param n           primeCA Wolfram code
     * @param initRow     size of input neighborhood in row format
     * @param feedbackRow at what length input neighborhood to start feeding back in
     * @return all iterations up to repeat, all rows
     */
    public long[][][] doLogReductionPrime(int[] n, int initRow, int feedbackRow) {
        //
        //
        //Initialization
        long[] wolfram = new long[n.length];
        for (int spot = 0; spot < n.length; spot++) {
            wolfram[spot] = (long) n[spot];
        }
        long initialValue = 0;
        long[] initial = new long[]{0, 0, 0, 10, 0, 4, 8};
        for (int spot = 0; spot < initial.length; spot++) {
            initialValue += (1 << (4 * spot)) * initial[spot];
        }
        long[][][] passes = new long[32][24][64];
        long[][] dec = new long[passes.length][passes[0].length];
        int maxRows = 10;
        long value;
        long[] values = new long[32];
        long[] active;
        values[0] = initialValue;
        int lengthRepeat = -1;
        for (int pass = 0; pass < 32 && lengthRepeat == -1; pass++) {
            for (int row = 0; row < maxRows; row++) {
                int powers = 1 + 2 * (row - 1);
                for (int power = 0; power < powers; power++) {
                    passes[pass][row][power] = -1;
                }
            }
        }
        value = initialValue;
        active = initial;
        //
        //
        //Main loop, goes until it finds a repeat neighborhood
        passLoop:
        for (int pass = 0; pass < 32 && lengthRepeat == -1; pass++) {
            //System.out.println("\n\n\n\nvalue " + value);
            long tot = 0;
            for (int row = initRow; row < maxRows; row++) {
                int powers = 1 + 2 * (row - 1);
                for (int power = 0; power < powers; power++) {
                    if ((int) ((value >> (4 * power)) % wolfram.length) < 0) {
                        break passLoop;
                    }
                    passes[pass][row][power] = wolfram[(int) ((value >> (4 * power)) % wolfram.length)];
                }
                long[] temp = new long[active.length + 2];
                for (int spot = 1; spot < temp.length - 1; spot++) {
                    temp[spot] = active[spot - 1];
                }
                active = temp;
                value *= 16;
            }
            rowLoop:
            for (int row = 0; row < maxRows; row++) {
                tot = 0;
                int powers = 1 + 2 * (row - 1);
                for (int power = 0; power < powers; power++) {
                    //System.out.println("power " + power);
                    tot += (passes[pass][row][power] << (4 * power));
                }
                dec[pass][row] = tot;
            }
            value = dec[pass][feedbackRow];
            values[pass] = value;
            initRow = feedbackRow - 1;
            long[] temp = new long[active.length - 2];
            for (int power = 0; power < temp.length; power++) {
                temp[power] = active[power] + 16 * active[power + 1] + 256 * active[power + 2];
                temp[power] = wolfram[(int) temp[power]];
            }
            active = temp;
            for (int passs = 0; passs < pass; passs++) {
                if (values[passs] == values[pass]) {
                    lengthRepeat = pass;
                    passRepeat = pass;
                }
            }
            //initRow = llv - 1;
            System.out.print("\n");
            System.out.println("passes[" + pass + "]");
            for (int row = 0; row < 22; row++) {
                for (int column = 0; column < 1 + 2 * (row - 1); column++) {
                    System.out.print(String.format("%1d", passes[pass][row][column]));
                }
                System.out.print(" " + dec[pass][row] + "\n");
            }
            if (lengthRepeat != -1) break passLoop;
            System.out.print("\n");
        }
        if (passRepeat == 0) passRepeat = 32;
        System.out.println("repeat " + passRepeat);
        int[] a = Arrays.copyOfRange(traditionalCenterColumn(wolfram, initial, 256, 512), 0, 128);
        for (int pass = 0; pass < passRepeat; pass++) {
            long[] passArr = Arrays.copyOfRange(passes[pass][feedbackRow], 0, 2 * (feedbackRow - 1) + 1);
            int[] b = Arrays.copyOfRange(traditionalCenterColumn(wolfram, passArr, 256, 512), 0, 128);
//            System.out.println("Pass " + pass);
//            System.out.println(Arrays.toString(passArr));
//            System.out.println(Arrays.toString(a));
//            System.out.println(Arrays.toString(b));
            for (int spot = 0; spot < 64; spot++) {
                if (Arrays.equals(Arrays.copyOfRange(b, 1, 33), Arrays.copyOfRange(a, spot, spot + 32))) {
                    System.out.println("Pass " + pass + " Spot " + spot);
                    System.out.println(dec[pass][feedbackRow]);
                }
            }
        }
        return passes;
    }









}
