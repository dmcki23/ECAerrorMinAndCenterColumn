import java.util.Arrays;
import java.util.Random;

public class AttemptSize8 {
    minErrorStaging m = new minErrorStaging();
    FastMinTransform fmt = new FastMinTransform();

    public void checkLastRowWeight() {
        int numTrials = 50;
        int size = 4;
        int[][] grid = new int[size][size];
        int[][] changedGrid = new int[size][size];
        Random rand = new Random();
        int[] tuple = new int[16];
        int[] changedTuple = new int[16];
        int numSame = 0;
        int numDifferent = 0;
        for (int trial = 0; trial < numTrials; trial++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    grid[row][col] = rand.nextInt(0, 2);
                }
            }
            for (int t = 0; t < 8; t++) {
                m.findMinimizingCodewordLarge(fmt.unpackedList[t], grid, new int[8]);
                tuple[t] = m.lastMinCodeword;
                tuple[8 + t] = m.lastMaxCodeword;
            }
            for (int tr = 0; tr < numTrials; tr++) {
                int numChanges = rand.nextInt(0, size);
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        changedGrid[row][col] = grid[row][col];
                    }
                }
                for (int change = 0; change < numChanges; change++) {
                    changedGrid[rand.nextInt(size - 1)][rand.nextInt(size)] ^= 1;
                }
                for (int t = 0; t < 8; t++) {
                    m.findMinimizingCodewordLarge(fmt.unpackedList[t], changedGrid, new int[8]);
                    changedTuple[t] = m.lastMinCodeword;
                    changedTuple[8  + t] = m.lastMaxCodeword;

                }
                if (Arrays.equals(tuple, changedTuple)) {
                    numSame++;
                } else {
                    numDifferent++;
                }
            }
        }
        System.out.println(numSame);
        System.out.println(numDifferent);
        System.out.println((numSame + numDifferent));
    }
}
