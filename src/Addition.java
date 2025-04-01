import java.util.Arrays;

public class Addition {
    FastMinTransform fastMinTransform = new FastMinTransform();
    minErrorStaging staging = new minErrorStaging();
    public void testAddition(){
        fastMinTransform.initWolframs();
        int[][][][] additionTables = new int[2][8][16][16];
        for (int a = 0; a < 16; a++) {
            for (int b = 0; b < 16; b++) {
                for (int posNeg = 0; posNeg < 2; posNeg++) {
                    for (int t = 0; t < 8; t++) {
                        int[][] aa = staging.generateGuess(a,fastMinTransform.unpackedList[t]);
                        int[][] bb = staging.generateGuess(b,fastMinTransform.unpackedList[t]);
                        int[][] cc = new int[4][4];
                        int[][] dd = new int[4][4];
                        for (int row = 0; row < 4; row++) {
                            for (int col = 0; col < 4; col++) {
                                cc[row][col] = aa[row][col] + bb[row][col];
                                if (cc[row][col] < 2) dd[row][col] = 0;
                                else dd[row][col] = 1;
                            }
                        }
                        int[][] ccc = staging.findMinimizingCodeword(fastMinTransform.unpackedList[t],dd,null);
                        //CustomArray.plusArrayDisplay(aa,false,false,"aa");
                        //.plusArrayDisplay(bb,false,false,"bb");
                       // CustomArray.plusArrayDisplay(cc,false,false,"a: " + a + " b: " + b);
                        int result = 0;
                        for (int column = 0; column < 4; column++) {
                            result += (int)Math.pow(2,column)*ccc[0][column];
                        }
                        additionTables[posNeg][t][a][b] = result;
                        //System.out.println();
                    }
                }
            }
        }
        for (int posNeg = 0; posNeg < 2; posNeg++) {
            for (int t = 0; t < 8; t++) {
                CustomArray.plusArrayDisplay(additionTables[posNeg][t],false,false,"posNeg: " + posNeg + " t: " + t + " " + fastMinTransform.unpackedList[t]);
            }
        }
        int[][] outTable = new int[16][16];
        HadamardThMar25 hadamard = new HadamardThMar25();
        int[][] hOut = new int[16][16];
        int[][] rowANDcol = new int[16][16];
        int[] distroAddition = new int[16];

        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                rowANDcol[row][col] = row & col;
                distroAddition[rowANDcol[row][col]]++;

                rowANDcol[row][col] ^= additionTables[0][5][row][col];
            }
        }
        CustomArray.plusArrayDisplay(rowANDcol,false,false,"rowANDcol");
        int[][] multiplicationTables = new int[16][16];
        for (int row = 0; row < 16; row++){
            for (int col = 1; col < 16; col+=2){
                multiplicationTables[row][col] = row;
                //for (int add = 1; add)
            }
        }
        for (int row = 0; row < 16; row++){
            for (int col = 0; col < 16; col++){
            }
        }
        System.out.println("distroAddition: " + Arrays.toString(distroAddition));
        int[][] h = hadamard.generateByRandCmodTwoNeg(16);

        for (int row = 0; row < 16; row++) {
            int tot = 0;
            for (int col = 0; col < 16; col++) {
                tot = 0;
                for (int power = 0; power < 4; power++){
                    tot ^= ((additionTables[0][5][row][col]/(1<<power))%2);
                }
                outTable[row][col] = tot;
                if (h[row][col] == 1){
                    h[row][col] = 0;
                } else {
                    h[row][col] = 1;
                }
                hOut[row][col] = h[row][col] ^ outTable[row][col];
            }
        }
        CustomArray.plusArrayDisplay(outTable,false,false,"xor-ed out by power");
        CustomArray.plusArrayDisplay(hOut,false,false,"Hadamard xor outTable");
    }
}
