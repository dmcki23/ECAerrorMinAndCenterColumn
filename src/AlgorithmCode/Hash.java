package AlgorithmCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Main entry point into the algorithm's structure; initiates truth table generation and manages the other Hash___ classes
 */
public class Hash {
    public HashTwoDsingleBit hashTwoDsingleBit;

    /**
     * Generates the codeword set truth tables as well as some minor functions, this one is set to rowError weighted errorScores
     */
    public HashTruthTables hashRows;
    /**
     * Generates the codeword set truth tables as well as some minor functions, this one is set to columnError weighted errorScores
     */
    public HashTruthTables hashColumns;
    /**
     * Both hashRows and hashColumns in one array
     */
    public HashTruthTables[] hashRowsColumns;
    /**
     * The relative logic gate transform that occurs when hashing
     */
    public HashLogicOpTransform hashLogicOpTransform = new HashLogicOpTransform(this);
    /**
     * One D version of the hash
     */
    public HashOneD hashOneD = new HashOneD(this);
    /**
     * Two D and bitmap hash functions
     */
    public HashTwoD hashTwoD = new HashTwoD(this);
    /**
     * Generally tests the hash algorithm for collisions and uniqueness
     */
    public HashCollisions hashCollisions;
    /**
     * The entire set of 32 min and max row-column weighted codeword truth tables
     */
    public int[][][] flatWolframs = new int[4][8][256 * 256];
    /**
     * The entire set of minMax row column codewords for all 256 ECA
     */
    public int[][][] allTables = new int[4][256][256 * 256];
    /**
     * The 8 rules referred to in the paper that have an even distribution of codewords
     * and unique codewords for every input
     */
    public int[] rowList = new int[]{0, 15, 51, 85, 170, 204, 240, 255};
    /**
     * Column-weighted ECA rules that have an even distribution and unique solutions
     */
    public int[] columnList = new int[]{0, 15, 85, 90, 165, 170, 240, 255};
    /**
     * rowList and columnList in one array
     */
    public int[][] bothLists = new int[][]{{0, 15, 51, 85, 170, 204, 240, 255}, {0, 15, 85, 90, 165, 170, 240, 255}};

    /**
     * Sets the helper classes and initiates generating the 32 basic codeword tables
     *
     * @throws IOException
     */
    public Hash() throws IOException {
        hashRows = new HashTruthTables(true, this);
        hashColumns = new HashTruthTables(false, this);
        hashRowsColumns = new HashTruthTables[]{hashRows, hashColumns};
        //hashUtilities = new HashUtilities(this);
        hashLogicOpTransform = new HashLogicOpTransform(this);
        hashOneD = new HashOneD(this);
        hashCollisions = new HashCollisions(this);
        hashTwoDsingleBit = new HashTwoDsingleBit(this);
        initWolframs();
    }

    /**
     * Initializes the set of hash truth tables for both row and column weighted lists, stores it in a file and tests it (not working atm)
     */
    public void initWolframsFromFileTest() throws IOException {
        for (int r = 0; r < 8; r++) {
            hashRows.individualRule(rowList[r], 4, false, 0, false, 0, false);
            hashColumns.individualRule(columnList[r], 4, false, 0, false, 0, false);
            allTables[0][rowList[r]] = hashRows.minSolutionsAsWolfram[rowList[r]];
            allTables[1][rowList[r]] = hashRows.maxSolutionsAsWolfram[rowList[r]];
            allTables[2][columnList[r]] = hashColumns.minSolutionsAsWolfram[columnList[r]];
            allTables[3][columnList[r]] = hashColumns.maxSolutionsAsWolfram[columnList[r]];
        }
        //Initialize the truth tables for both the min and max codewords of the set
        for (int spot = 0; spot < 8; spot++) {
            for (int column = 0; column < 256 * 256; column++) {
                flatWolframs[0][spot][column] = hashRows.minSolutionsAsWolfram[rowList[spot]][column];
                flatWolframs[1][spot][column] = hashRows.maxSolutionsAsWolfram[rowList[spot]][column];
                flatWolframs[2][spot][column] = hashColumns.minSolutionsAsWolfram[columnList[spot]][column];
                flatWolframs[3][spot][column] = hashColumns.maxSolutionsAsWolfram[columnList[spot]][column];
            }
        }
        int[][][] compareCopy = new int[4][8][256 * 256];
        System.out.println("compareCopy.length: " + compareCopy.length);
        for (int layer = 0; layer < 4; layer++) {
            for (int element = 0; element < 8; element++) {
                for (int index = 0; index < 256 * 256; index++) {
                    compareCopy[layer][element][index] = flatWolframs[layer][element][index];
                }
                System.out.println(Arrays.toString(Arrays.copyOfRange(compareCopy[layer][element], 0, 80)));
            }
        }
        writeToFileMinMaxRowColumn();
        readFromFileMinMaxRowColumn();
        int same = 0;
        int different = 0;
        for (int layer = 0; layer < 4; layer++) {
            for (int element = 0; element < 8; element++) {
                for (int address = 0; address < 256 * 256; address++) {
                    if (compareCopy[layer][element][address] == flatWolframs[layer][element][address]) {
                        same++;
                    } else {
                        different++;
                    }
                }
            }
        }
        System.out.println("same: " + same);
        System.out.println("different: " + different);
    }

    /**
     * Initializes the set of hash truth tables for both row and column weighted lists, from the file generated in writeToFileMinMaxRowColumn()
     *
     * @param fromFile a dummy variable to distinguish it from the other initWolframs()
     */
    public void initWolframs(boolean fromFile) throws IOException {
//        hashRows = new HashTruthTables(true,this);
//        hashColumns = new HashTruthTables(false,this);
        readFromFileMinMaxRowColumn();
    }

    /**
     * Initializes the set of hash truth tables for both row and column weighted lists
     */
    public void initWolframs() {
        hashRows = new HashTruthTables(true, this);
        hashColumns = new HashTruthTables(false, this);
        hashRowsColumns = new HashTruthTables[]{hashRows, hashColumns};
        int[] comp = new int[65536];

        for (int t = 0; t < 8; t++) {
            hashRows.individualRule(rowList[t], 4, false, 0, false, 0, false);
            for (int address = 0; address < 256 * 256; address++) {

                allTables[0][rowList[t]][address] = hashRows.minSolutionsAsWolfram[rowList[t]][address];
                allTables[1][rowList[t]][address] = hashRows.maxSolutionsAsWolfram[rowList[t]][address];

            }
        }
        for (int t = 0; t < 8; t++) {
            hashColumns.individualRule(columnList[t], 4, false, 0, false, 0, false);
            for (int address = 0; address < 256 * 256; address++) {

                allTables[2][columnList[t]][address] = hashColumns.minSolutionsAsWolfram[columnList[t]][address];
                allTables[3][columnList[t]][address] = hashColumns.maxSolutionsAsWolfram[columnList[t]][address];
            }
        }
        //Initialize the truth tables for both the min and max codewords of the set
        for (int t = 0; t < 8; t++) {
            for (int column = 0; column < 256 * 256; column++) {
                flatWolframs[0][t][column] = hashRows.minSolutionsAsWolfram[rowList[t]][column];
                flatWolframs[1][t][column] = hashRows.maxSolutionsAsWolfram[rowList[t]][column];
                flatWolframs[2][t][column] = hashColumns.minSolutionsAsWolfram[columnList[t]][column];
                flatWolframs[3][t][column] = hashColumns.maxSolutionsAsWolfram[columnList[t]][column];
            }
        }
        System.out.println("flatWolframs.length: " + flatWolframs.length);
        for (int layer = 0; layer < 4; layer++) {
            for (int t = 0; t < 8; t++) {
                System.out.println(Arrays.toString(Arrays.copyOfRange(flatWolframs[layer][t], 0, 300)));
                comp = new int[flatWolframs[layer][t].length];
                if (Arrays.equals(comp, flatWolframs[layer][t])) {
                    System.out.println("is zero");
                }
            }
        }
        comp = new int[256*256];
        for (int list = 0; list < 2; list++) {
            for (int posNeg = 0; posNeg < 2; posNeg++) {
                for (int t = 0; t < 8; t++) {
                    System.out.println(list + " " + posNeg + " " + t + " " + Arrays.toString(Arrays.copyOfRange(allTables[2 * list + posNeg][bothLists[list][t]], 0, 300)));
                    if (Arrays.equals(comp,allTables[2*list+posNeg][bothLists[list][t]])){
                        System.out.println("is zero");
                    }
                }
            }
        }
        System.out.println("allTables.length: " + allTables.length);
        for (int layer = 0; layer < 2; layer++){
            for (int t = 0; t < 8; t++){
                System.out.println(Arrays.toString(Arrays.copyOfRange(allTables[ 2*layer][bothLists[layer][t]], 0, 300)));
                System.out.println(Arrays.toString(Arrays.copyOfRange(allTables[1+ 2*layer][bothLists[layer][t]], 0, 300)));
            }
        }
    }

    /**
     * Reads the codeword set truth tables from the file generated in writeToFileMinMaxRowColumn()
     *
     * @throws IOException
     */
    public void readFromFileMinMaxRowColumn() throws IOException {
        File file = new File("src/AlgorithmCode/minMaxCodewordsTest.dat");
        FileInputStream in = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        data = in.readAllBytes();
        //ByteBuffer buffer = ByteBuffer.wrap(data);
        int indexer = 0;
        for (int layer = 0; layer < 4; layer++) {
            for (int t = 0; t < 8; t++) {
                for (int index = 0; index < 65536; index++) {
                    flatWolframs[layer][t][index] = data[indexer];
                    indexer++;
                }
            }
        }
    }

    /**
     * Writes the 32 codeword size 4 truth tables to file
     *
     * @throws IOException
     */
    public void writeToFileMinMaxRowColumn() throws IOException {
        File file = new File("src/AlgorithmCode/minMaxCodewordsTest.dat");
        FileOutputStream out = new FileOutputStream(file);
        ByteBuffer buffer;
        byte[] data = new byte[65536 * 32 * 4];
        for (int layer = 0; layer < 4; layer++) {
            for (int t = 0; t < 8; t++) {
                //buffer = ByteBuffer.allocate(65536 * 4);
                for (int index = 0; index < 65536; index++) {
                    out.write((byte) flatWolframs[layer][t][index]);
                    //buffer.putInt(flatWolframs[posNeg + 2 * rowColumn][t][index]);
                }
                //out.write(buffer.array());
            }
        }
        out.close();
    }

}



