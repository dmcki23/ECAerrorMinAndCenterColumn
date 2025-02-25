
//Daniel McKinley
//April 10, 2024
//
//This class has various permutation functions, as well as gray code functions
import java.util.Arrays;
/**
 * Utility class that includes permutations, factoradics, composition of permutations, array insertions, and gray codes
 */
public class PermutationsFactoradic {
    /**
     * Currently empty constructor
     */
    public PermutationsFactoradic() {
    }
    /**
     * Inserts a bit of value value at index spot
     *
     * @param in    integer to insert into
     * @param spot  place at which to insert
     * @param value value to insert
     * @return in with value inserted at spot
     */
    public static int insertBit(int in, int spot, int value) {
        int out = 0;
        int index = 0;
        for (int power = 0; power < 8; power++) {
            if (spot == power) {
                out += (value) * (int) Math.pow(2, index);
                index++;
            }
            out += (in / (int) Math.pow(2, power) % 2) * (int) Math.pow(2, index);
            index++;
        }
        return out;
    }
    /**
     * All factoradics of given length
     *
     * @param degree umber of characters in alphabet, integers
     * @return int array of factoradics with length degree
     */
    public static int[][] permRemainders(int degree) {
        int[][] out = new int[factorial(degree)][degree];
        int[][] intermediate = new int[factorial(degree)][degree + 1];
        for (int n = 0; n < factorial(degree); n++) {
            int temp = n;
            for (int column = 1; column <= degree; column++) {
                intermediate[n][column] = temp % column;
                temp = (temp) / column;
            }
        }
        for (int n = 0; n < factorial(degree); n++) {
            for (int column = 0; column < degree; column++) {
                out[n][column] = intermediate[n][(column + 1)];
            }
            int[] temp = new int[degree];
            for (int column = 0; column < degree; column++) {
                temp[column] = out[n][degree - column - 1];
            }
            for (int column = 0; column < degree; column++) {
                out[n][column] = temp[column];
            }
        }
        return out;
    }

    /**
     * Specific factoradic, typically for lengths of longer than 8 or 9, to save time and memory
     *
     * @param degree number of characters in the alphabet, integers
     * @param value  specific integer factoradic value
     * @return int array of a specific factoradic
     */
    public static int[] specificFactoradic(int degree, int value) {
        int[] out = new int[degree];
        int[] intermediate = new int[degree + 1];
        for (int n = value; n < value + 1; n++) {
            int temp = n;
            for (int column = 1; column <= degree; column++) {
                intermediate[column] = temp % column;
                temp = (temp) / column;
            }
        }
        for (int n = value; n < value + 1; n++) {
            for (int column = 0; column < degree; column++) {
                out[column] = intermediate[(column + 1)];
            }
            int[] temp = new int[degree];
            for (int column = 0; column < degree; column++) {
                temp[column] = out[degree - column - 1];
            }
            for (int column = 0; column < degree; column++) {
                out[column] = temp[column];
            }
        }
        return out;
    }
    /**
     * Specific permutation of a given length, typically for lengths of longer than 8 or 9, to save time and memory
     *
     * @param degree length of permutation list
     * @param value  integer factoradic value
     * @return specific permutation of length degree with factoradic number value
     */
    public static int[] specificPermutation(int degree, int value) {
        int[] remainders = specificFactoradic(degree, value);
        int[] out = new int[degree];
        int[] seq = new int[degree];
        for (int spot = 0; spot < degree; spot++) {
            seq[spot] = spot;
        }
        int[] localSequence = seq;
        for (int spot = 0; spot < degree; spot++) {
            int index = remainders[spot];
            out[spot] = localSequence[index];
            localSequence = removeElement(localSequence, index);
        }
        return out;
    }
    /**
     * Set of bit changes in a gray code of length places
     *
     * @param places length of gray code
     * @return array of bit changes in a gray code of length places
     */
    public static int[] grayBitChanges(int places) {
        int[][] out = new int[places + 1][(int) Math.pow(2, places)];
        out[2][0] = 0;
        out[2][1] = 1;
        out[2][2] = 0;
        out[2][3] = 1;
        for (int row = 3; row <= places; row++) {
            int length = (int) Math.pow(2, row - 1);
            for (int ab = 0; ab < 2; ab++) {
                for (int spot = 0; spot < length; spot++) {
                    out[row][ab * length + spot] = out[row - 1][spot];
                }
                //out[row][length-1]++;
                //out[row][2*length-1]++;
            }
            out[row][length - 1]++;
            out[row][2 * length - 1]++;
        }
        return out[places];
//        int[][] graySequence = new int[8][256];
//        graySequence[1][0] = 0;
//        graySequence[1][1] = 1;
//        int init_length = 2;
//        int[][] graySequenceByPlace = new int[8][256];
//        graySequenceByPlace[1][0] = 0;
//        graySequenceByPlace[1][1] = 0;
//        int[][][] neg_places = new int[6][8][8];
//        //System.out.println("Here");
//        int init_bit = 1;
//        for (int row = 1; row < 7; row++){
//            for (int cycle = 0; cycle < 2; cycle++){
//                for (int spot = 0; spot < init_length; spot++){
//                    graySequenceByPlace[row+1][init_length*cycle + spot] = graySequenceByPlace[row][spot];
//                    graySequence[row+1][init_length*cycle+spot] = graySequence[row][spot];
//                    if (cycle == 1 && spot == 0){
//                        graySequence[row+1][init_length*cycle+spot] = init_length;
//                        graySequenceByPlace[row+1][init_length*cycle+spot] = init_bit;
//                    }
//                }
//            }
//            init_length = init_length * 2;
//            init_bit++;
//        }
//        int[][] grayTranslated = new int[8][256];
//        for (int row = 2; row < 8; row++){
//            for (int spot = 1; spot < (int)Math.pow(2,row); spot++){
//                int alter = graySequence[row][spot];
//                grayTranslated[row][spot] = grayTranslated[row][spot-1] ^ graySequence[row][spot];
//            }
//        }
//        int[] out = new int[(int)Math.pow(2,degree)];
//        for (int spot = 0; spot < out.length; spot++){
//            out[spot] = grayTranslated[degree][spot];
//        }
//        return graySequenceByPlace[degree];
    }
    /**
     * Gray code of length degree
     *
     * @param degree length of gray code
     * @return Gray code of length degree
     */
    public static int[] graySequence(int degree) {
        int[][] graySequence = new int[16][256*256];
        graySequence[1][0] = 0;
        graySequence[1][1] = 1;
        int init_length = 2;
        int[][] graySequenceByPlace = new int[16][256*256];
        graySequenceByPlace[1][0] = 0;
        graySequenceByPlace[1][1] = 0;
        int init_bit = 1;
        for (int row = 1; row <= degree; row++) {
            for (int cycle = 0; cycle < 2; cycle++) {
                for (int spot = 0; spot < init_length; spot++) {
                    graySequenceByPlace[row + 1][init_length * cycle + spot] = graySequenceByPlace[row][spot];
                    graySequence[row + 1][init_length * cycle + spot] = graySequence[row][spot];
                    if (cycle == 1 && spot == 0) {
                        graySequence[row + 1][init_length * cycle + spot] = init_length;
                        graySequenceByPlace[row + 1][init_length * cycle + spot] = init_bit;
                    }
                }
            }
            init_length = init_length * 2;
            init_bit++;
        }
        int[][] grayTranslated = new int[16][256*256];
        for (int row = 2; row <= degree; row++) {
            for (int spot = 1; spot < (int) Math.pow(2, row); spot++) {
                grayTranslated[row][spot] = grayTranslated[row][spot - 1] ^ graySequence[row][spot];
            }
        }
        int[] out = new int[(int) Math.pow(2, degree)];
        for (int spot = 0; spot < out.length; spot++) {
            out[spot] = grayTranslated[degree][spot];
        }
        return out;
    }
    /**
     * Gray codes of lengths 1 to 256, by powers of 2
     *
     * @return gray codes with their places permuted, 1's place, 2's place,4's place, etc
     */
    public static int[][][] grayCodeBitPermutations() {
        int[][] graySequence = new int[8][256];
        graySequence[1][0] = 0;
        graySequence[1][1] = 1;
        int init_length = 2;
        int[][] graySequenceByPlace = new int[8][256];
        graySequenceByPlace[1][0] = 0;
        graySequenceByPlace[1][1] = 0;
        int degree = 5;
        int init_bit = 1;
        for (int row = 1; row < 7; row++) {
            for (int cycle = 0; cycle < 2; cycle++) {
                for (int spot = 0; spot < init_length; spot++) {
                    graySequenceByPlace[row + 1][init_length * cycle + spot] = graySequenceByPlace[row][spot];
                    graySequence[row + 1][init_length * cycle + spot] = graySequence[row][spot];
                    if (cycle == 1 && spot == 0) {
                        graySequence[row + 1][init_length * cycle + spot] = init_length;
                        graySequenceByPlace[row + 1][init_length * cycle + spot] = init_bit;
                    }
                }
            }
            init_length = init_length * 2;
            init_bit++;
        }
        int[][] grayTranslated = new int[8][256];
        for (int row = 2; row < 8; row++) {
            for (int spot = 1; spot < (int) Math.pow(2, row); spot++) {
                grayTranslated[row][spot] = grayTranslated[row][spot - 1] ^ graySequence[row][spot];
            }
        }
        int[][] perms = permSequences(degree);
        int[][][] grayPerm = new int[120][8][256];
        int[] available = new int[]{1, 2, 4, 8, 16, 32, 64, 128};
        System.out.println(factorial(3));
        String outstring = "";
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < (int) Math.pow(2, row); column++) {
                outstring += grayTranslated[row][column] + " ";
            }
            System.out.println(outstring);
            outstring = "";
        }
        System.out.println();
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < (int) Math.pow(2, row); column++) {
                outstring += graySequenceByPlace[row][column] + " ";
            }
            System.out.println(outstring);
            outstring = "";
        }

        for (int row = 1; row < degree + 1; row++) {
            for (int perm = 0; perm < factorial(row); perm++) {
                for (int spot = 1; spot < (int) Math.pow(2, row); spot++) {
                    grayPerm[perm][row][spot] = grayPerm[perm][row][spot - 1] ^ (int) Math.pow(2, perms[perm][graySequenceByPlace[row][spot]]);
                }
            }
        }
        return grayPerm;
    }
    /**
     * All permutations of length degree
     *
     * @param degree length of permutation list
     * @return integer array of all permutations of length degree
     */
    public static int[][] permSequences(int degree) {
        int[][] out = new int[factorial(degree)][degree];
        int[][] remainders = permRemainders(degree);
        for (int perm = 0; perm < factorial(degree); perm++) {
            int[] seq = new int[degree];
            for (int spot = 0; spot < degree; spot++) {
                seq[spot] = spot;
            }
            int[] localSequence = seq.clone();
            for (int spot = 0; spot < degree; spot++) {
                int index = remainders[perm][spot];
                out[perm][spot] = localSequence[index];
                int[] temp = new int[degree - spot - 1];
                int sspot = 0;
                for (int ssspot = 0; ssspot < degree - spot; ssspot++) {
                    if (ssspot == index) {
                    } else {
                        temp[sspot] = localSequence[ssspot];
                        sspot++;
                    }
                }
                localSequence = temp.clone();
            }
        }
        return out;
    }

    /**
     * Factorial n
     *
     * @param n integer to multiply out
     * @return n!
     */
    public static int factorial(int n) {
        int out = 1;
        for (int spot = 2; spot <= n; spot++) {
            out = out * spot;
        }
        return out;
    }
    /**
     * Removes a bit from an integer and shifts the remaining bits to cover the missing bit
     *
     * @param in  integer to remove a bit from
     * @param bit which bit to remove
     * @return integer in with bit's place removed, shifted to cover the missing bit
     */
    public static int removeBit(int in, int bit) {
        int out = 0;
        int index = 0;
        for (int power = 0; power < 16; power++) {
            if (power == bit) {
                continue;
            } else {
                out += (int) Math.pow(2, index) * (in / (int) Math.pow(2, power) % 2);
                index++;
            }
        }
        return out;
    }
    /**
     * Removes a bit from a double
     *
     * @param in  double to remove a bit from
     * @param bit which place to removed
     * @return a double with bit's place removed, and shifted to cover the missing place
     */
    public static  double removeBit(double in, int bit) {
        double out = in - (in / (int) Math.pow(2, bit) % 2);
        double decimalPart = out - (Math.floor(out));
        double intPart = Math.floor(out);
        out = 0;
        int index = 0;
        for (int power = 0; power < 16; power++) {
            if (power == bit) {
                continue;
            }
            out += (int) Math.pow(2, index) * (intPart / Math.pow(2, power) % 2);
            index++;
        }
        out += decimalPart;
        return out;
    }
    /**
     * Insert a bit into a double
     *
     * @param in    double to insert a bit into
     * @param bit   which place to insert bit
     * @param value value of bit to insert
     * @return in with bit's place value inserted
     */
    public static double insertBit(double in, int bit, int value) {
        double out = 0;
        double decimalPart = in - (Math.floor(in));
        double intPart = Math.floor(in);
        int index = 0;
        for (int power = 0; power < 16; power++) {
            if (power == bit) {
                out += value * Math.pow(2, bit);
                index++;
            }
            out += (intPart / Math.pow(2, power) % 2) * Math.pow(2, index);
            index++;
        }
        out = out + decimalPart;
        return out;
    }
    /**
     * Removes an element from an int array
     *
     * @param in   array to remove from
     * @param spot index of element to remove
     * @return int array in with element spot removed
     */
    public static  int[] removeElement(int[] in, int spot) {
        int[] out = new int[in.length - 1];
        int index = 0;
        for (int power = 0; power < in.length; power++) {
            if (power == spot) {
                continue;
            } else {
                out[index] = in[power];
                index++;
            }
        }
        return out;
    }

    /**
     * Permutations of permutations in list form
     *
     * @param length length of permutation list
     * @return set of permutation compositions
     */
    public static int[][][] permsOfPerms(int length) {
        int[][][] out = new int[factorial(length)][factorial(length)][length];
        int[][] perms = permSequences(length);
        int[] activePerm = new int[length];
        int[] nextPerm = new int[length];
        for (int perm = 0; perm < factorial(length); perm++) {
            activePerm = perms[perm];
            for (int permm = 0; permm < factorial(length); permm++) {
                nextPerm = new int[length];
                for (int l = 0; l < length; l++) {
                    nextPerm[l] = activePerm[perms[permm][l]];
                }
                out[perm][permm] = nextPerm;
            }
        }
        return out;
    }
    /**
     * Permutations of permutations in grid form
     *
     * @param length number of characters in the alphabet
     * @return a grid of permutation compositions
     */
    public static int[][] permsOfPermsGrid(int length) {

        int[][] perms = permSequences(length);
        int[][] out = new int[factorial(length)][factorial(length)];
        int[] activePerm = new int[length];
        int[] a = new int[length];
        int[] b = new int[length];
        int[] c = new int[length];
        for (int perm = 0; perm < factorial(length); perm++) {
            a = perms[perm];
            columnLoop:
            for (int permm = 0; permm < factorial(length); permm++) {
                b = perms[permm];
                for (int permmm = 0; permmm < factorial(length); permmm++) {
                    c = perms[permmm];
                    for (int spot = 0; spot < length; spot++) {
                        activePerm[spot] = perms[perm][perms[permm][spot]];
                    }
                    boolean isEqual = true;
                    for (int spot = 0; spot < length; spot++) {
                        if (activePerm[spot] != perms[permmm][spot]) {
                            isEqual = false;
                        }
                    }
                    if (isEqual) {
                        out[perm][permm] = permmm;
                        continue columnLoop;
                    }
                }
            }
        }
        return out;
    }
    /**
     * Inserts an int value into an array
     *
     * @param in    array you want to insert into
     * @param spot  place where you want to insert
     * @param value value that you want to insert
     * @return int array with value inserted at spot into in
     */
    public static int[] insertElement(int[] in, int spot, int value) {
        int[] out = new int[in.length + 1];
        int index = 0;
        for (int l = 0; l < in.length; l++) {
            if (l == spot) {
                out[index] = value;
                index++;
            }
            out[index] = in[l];
            index++;
        }
        if (spot == in.length) {
            out[in.length] = value;
        }
        return out;
    }
    /**
     * Return the factoradic of the (zero indexed) input  sequence
     *
     * @param in int array to find the factoradic of
     * @return factoradic of in
     */
    public static int factoradicFromSequence(int[] in) {
        int out = 0;
        for (int n = 0; n < factorial(in.length); n++) {
            if (Arrays.equals(in, specificPermutation(in.length, n))) {
                out = n;
            }
        }
        return out;
    }
    /**
     * The recursive part of combinations
     *
     * @param arr        input array
     * @param data       current active combination being produced
     * @param start      within the recursion context, the array index to start selecting at
     * @param end        within the recursion context, the array index to stop selecting at
     * @param index      current depth of recursion
     * @param r          length of selection
     * @param masterData output data
     * @param mdIndex    current index of masterData
     * @param n          length of input array
     */
    public static void combinationUtil(int[] arr, int[] data, int start,
                                int end, int index, int r, int[][] masterData, int[] mdIndex, int n) {
        // Current combination is ready to be printed, print it
        if (index == r) {
            masterData[mdIndex[0]] = data.clone();
            //System.out.println(mdIndex[0] + " " + Arrays.toString(data) );
            mdIndex[0]++;
        } else {
            // replace index with all possible elements. The condition
            // "end-i+1 >= r-index" makes sure that including one element
            // at index will make a combination with remaining elements
            // at remaining positions
            for (int i = start; i < end && i < n; i++) {
                data[index] = arr[i];
                combinationUtil(arr, data, i + 1, end + 1, index + 1, r, masterData, mdIndex, n);
            }
        }
    }
    /**
     * Wrapper function that calls combinationUtil() to produce all combinations of size r
     * from an array of size n.
     *
     * @param n size of alphabet
     * @param r size of output array
     * @return all combinations of size r from 0..n-1
     */
    public static  int[][] combinations(int n, int r) {
        // A temporary array to store all combination one by one
        int[] data = new int[r];
        int[] arr = new int[n];
        for (int spot = 0; spot < n; spot++) {
            arr[spot] = spot;
        }
        int[][] masterData = new int[factorial(n) / factorial(r) / factorial(n - r)][r];
        // Print all combination using temporary array 'data[]'
        combinationUtil(arr, data, 0, n, 0, r, masterData, new int[]{0}, n);
        return masterData;
    }
}