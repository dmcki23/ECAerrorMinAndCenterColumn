import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class WAVfile {
    minErrorStaging ecAminErrorFit = new minErrorStaging();
    int[][][] minNeighborhoods;
    byte[] fileBytes;
    byte[] wavBytes;
    byte[] wavOutBytes;
    byte[] leftoverBytes;
    byte[] headerBytes;
    int headerLength;
    int numFields;

    public void doFile(int rule, int subSize, int numTrials, int headerSize) throws IOException {
        int[] wolfram = new int[8];
        headerLength = headerSize;
        for (int power = 0; power < 8; power++) {
            wolfram[power] = ((rule / (int) Math.pow(2, power)) % 2);
        }
        String filename = "minErrorWav.wav";
        FileInputStream fis = new FileInputStream(filename);
        fileBytes = fis.readAllBytes();
        wavBytes = Arrays.copyOfRange(fileBytes, headerLength, fileBytes.length);
        wavOutBytes = new byte[fileBytes.length];
        headerBytes = new byte[headerLength];
        double max = wavBytes.length;
        double compressed = Math.sqrt(max * max / subSize / subSize);
        System.out.println("Max " + max + " compressed " + compressed);
        System.out.println("max compression ratio " + ((double) compressed / (double) max));
        for (int spot = 0; spot < headerLength; spot++) {
            wavOutBytes[spot] = fileBytes[spot];
            headerBytes[spot] = fileBytes[spot];
        }
        int numBits = wavBytes.length * 8;


        System.out.println("WAV file size in bytes: " + numBits / 8);
        numFields = numBits / (subSize * subSize);
        int leftovers = numBits % (subSize * subSize);
        System.out.println("Leftovers " + leftovers);
        leftovers /= 8;
        System.out.println("Leftovers " + leftovers);
        leftoverBytes = new byte[leftovers];

        minNeighborhoods = new int[numFields+headerLength][8][subSize];

        fis.close();
        File file = new File("waveOutTwo.wav");
        FileOutputStream fos = new FileOutputStream(file);
        System.out.println("numGrids " + numFields);
        System.out.println("byte test " + (byte)((int)Math.pow(2,7)) + " " + (byte)((int)Math.pow(2,7)/Math.pow(2,7)));
        int gridLocation = 0;
        int gridIndex = headerLength;
        int location = 0;
        int index = headerLength;
        int totErrors = 0;
        for (int grid = 0; grid < numFields; grid++) {
            if (grid % 10 == 0) System.out.println("Grids left " + (numFields-grid));
            int[][] solution = new int[subSize][subSize];
            int[][] temp = new int[subSize][subSize];
            for (int row = 0; row < subSize; row++) {
                for (int col = 0; col < subSize; col++) {
                    byte mask = (byte) ((int) Math.pow(2,7-location));
                    byte result = (byte) (mask & fileBytes[index]);
                    result = (byte) ((int)result /(int)Math.pow(2,7-location));
                    temp[row][col] = (int)(Math.abs(result));

                    if (location == 7) {
                        index++;
                        location = 0;
                    } else {
                        location++;
                    }

                }
            }
            //CustomArray.plusArrayDisplay(temp,false,true,"temp " + grid);
            //if (grid < 73solution = transformAndCheck(temp, wolfram, 200, grid);
            //solution = ecAminErrorFit.checkPascalErrorCorrectionLargeThree(wolfram,temp,100);
            solution = transformAndCheck(rule, temp,wolfram,numTrials,grid);
            //CustomArray.plusArrayDisplay(solution,false,true,"solution " + grid);
            int errors = 0;
            for (int row = 0; row < subSize; row++) {
                for (int col = 0; col < subSize; col++) {
                    errors += (temp[row][col] ^ solution[row][col]);
                    totErrors += (temp[row][col] ^ solution[row][col]);
                }
            }
            for (int row = 0; row < subSize; row++) {
                for (int col = 0; col < subSize; col++) {
                    //if (grid > 72 && grid < 997) solution[row][col] =temp[row][col];
                }
            }
            //System.out.println("grid " + grid + " errors " + errors + " rate " + (subSize*subSize/errors));
            //System.out.println("\n\n\n\n");
            //solution = ecAminErrorFit.checkPascalErrorCorrectionLargeTwo(wolfram, temp,  50);
            for (int row = 0; row < subSize; row++) {
                //System.out.println("row " + row + " " + Arrays.toString(solution[row]));
                for (int col = 0; col < subSize; col++) {
                    byte mask = (byte) (solution[row][col] *(int)Math.pow(2,7-gridLocation));
                    if (gridLocation == 0 && solution[row][col] == 1) mask = -128;
                    //System.out.println("mask " + mask + " solution[row][col] " + solution[row][col] + " gridLocation " + gridLocation);
                    wavOutBytes[gridIndex] = (byte) (mask | wavOutBytes[gridIndex]);
                    //wavOutBytes[gridIndex] = wavBytes[gridIndex - headerLength];
                    if (gridLocation == 7) {
                        //System.out.println(wavOutBytes[gridIndex]);
                        gridIndex++;
                        gridLocation = 0;
                        //wavOutBytes[gridIndex] = wavBytes[gridIndex];
                    } else {
                        gridLocation++;
                    }
                }
            }
        }
        System.out.println("location " + location);
        for (int l = gridIndex; l < wavOutBytes.length; l++) {
            wavOutBytes[l] = fileBytes[l];
//            leftoverBytes[l] = fileBytes[gridIndex + 1];
        }
        fos.write(wavOutBytes);
        System.out.println(wavOutBytes.length);
        fos.close();
        System.out.println("done");
        System.out.println("area per error " + numFields*subSize*subSize/totErrors);
    }

    public int[][] transformAndCheck(int rule, int[][] field, int[] wolfram, int numTrials, int index) {
        int[][] tempField = new int[field.length][field[0].length];
        int size = tempField.length;
        int[][] nextTemp = new int[size][size];
        int[][] vote = new int[size][size];
        for (int rotation = 0; rotation < 8; rotation++) {
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    tempField[row][column] = field[row][column];
                }
            }
            if (rotation % 2 == 1) {
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        nextTemp[row][column] = tempField[size - 1 - row][column];
                    }
                }
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        tempField[row][column] = nextTemp[row][column];
                    }
                }
                //tempField = nextTemp;
            }
            if (((rotation / 2) % 2) == 1) {
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        nextTemp[row][column] = tempField[row][size - 1 - column];
                    }
                }
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        tempField[row][column] = nextTemp[row][column];
                    }
                }
            }
            if (((rotation / 4) % 2) == 1) {
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        nextTemp[row][column] = tempField[column][row];
                    }
                }
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        tempField[row][column] = nextTemp[row][column];

                    }
                }
            }
            //Finds the minimum error producing neighborhood//
            //CustomArray.plusArrayDisplay(tempField,false,true,"transpose,reflect " + index);
            //tempField = ecAminErrorFit.checkPascalErrorCorrectionLargeThree(wolfram, tempField, numTrials);
            tempField = ecAminErrorFit.findMinimizingCodeword(rule,tempField,new int[8]);
            for (int spot = 0; spot < size; spot++) {
                //minNeighborhoods[index][rotation][spot] = ecAminErrorFit.minNeighborhood[spot];
            }
            //if ((rotation / 8) % 2 == 1) {
            //tempField = checkPascalErrorCorrectionLargeTwoDiagonal(wolfram, tempField, numTrials);
            //} //else tempField = checkPascalErrorCorrectionLargeTwo(wolfram, tempField, numTrials);
            //unreflects, unrotates, untransposes the solution
            if (rotation % 2 == 1) {
                //int[][] nextTemp = new int[size][size];
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        nextTemp[row][column] = tempField[size - 1 - row][column];
                    }
                }
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        tempField[row][column] = nextTemp[row][column];
                    }
                }
            }
            if (((rotation / 2) % 2) == 1) {
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        nextTemp[row][column] = tempField[row][size - 1 - column];
                    }
                }
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        tempField[row][column] = nextTemp[row][column];
                    }
                }
            }
            if (((rotation / 4) % 2) == 1) {
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        nextTemp[row][column] = tempField[column][row];
                    }
                }
                for (int row = 0; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        tempField[row][column] = nextTemp[row][column];
                    }
                }
            }
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    vote[row][column] += tempField[row][column];
                    //vote[row][column]++;
                }
            }
        }
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if (vote[row][column] > 2) {
                    tempField[row][column] = 1;
                } else {
                    tempField[row][column] = 0;
                }
            }
        }
        //CustomArray.plusArrayDisplay(vote,false,true,"vote");
        return tempField;
    }

    public void reassemble(int[] wolfram, int numTrials, int numBits, int subSize) throws IOException {
        File file = new File("reassembleWav.wav");
        //FileInputStream fis = new FileInputStream("minErrorWav.wav");
        int numBytes = numTrials * 8 * subSize * subSize / 8;

        //byte[] inputBytes = fis.readAllBytes();
        byte[] outputBytes = new byte[1];
        for (int spot = 0; spot < headerLength; spot++) {
            outputBytes[spot] = headerBytes[spot];
        }
        FileOutputStream fos = new FileOutputStream(file);
        int[][] trialField = new int[subSize][subSize];
        int gridIndex = headerLength;
        int gridLocation = 0;
        wavOutBytes = new byte[numBytes+headerLength];

        int size = subSize;
        for (int grid = 0; grid < numFields; grid++) {
            for (int rotation = 0; rotation < 8; rotation++) {
                for (int column = 0; column < subSize; column++) {
                    trialField[0][column] = minNeighborhoods[grid][rotation][column];
                }
                for (int row = 1; row < size; row++) {
                    for (int column = 0; column < size; column++) {
                        int a = ((column - 1) + size) % size;
                        int b = column;
                        int c = ((column + 1)) % size;
                        trialField[row][column] = trialField[row - 1][a] + 2 * trialField[row - 1][b] + 4 * trialField[row - 1][c];
                        trialField[row][column] = wolfram[trialField[row][column]];
                    }
                }
                for (int row = 0; row < subSize; row++) {
                    for (int col = 0; col < subSize; col++) {
                        byte mask = (byte) (trialField[row][col] * (int) Math.pow(2, gridLocation));
                        wavOutBytes[gridIndex] = (byte) (mask | wavOutBytes[gridIndex]);
                        //wavOutBytes[gridIndex] = wavBytes[gridIndex - 100];
                        if (gridLocation == 7) {
                            gridIndex++;
                            gridLocation = 0;
                        } else {
                            gridLocation++;
                        }
                    }
                }

            }
        }
        for (int l = 0; l < leftoverBytes.length; l++) {
            outputBytes[gridIndex+l] = leftoverBytes[l];
        }
        fos.write(outputBytes);
        fos.close();
    }
}
//RIFF chunk
//ckID 	4 	Chunk ID: "RIFF"
//cksize 	4 	Chunk size: 4+n
//WAVEID 	4 	WAVE ID: "WAVE"
//WAVE chunks 	n 	Wave chunks containing format information and sampled data

//format chunk
//ckID 	4 	Chunk ID: "fmt "
//cksize 	4 	Chunk size: 16, 18 or 40
//wFormatTag 	2 	Format code
//nChannels 	2 	Number of interleaved channels
//nSamplesPerSec 	4 	Sampling rate (blocks per second)
//nAvgBytesPerSec 	4 	Data rate
//nBlockAlign 	2 	Data block size (bytes)
//wBitsPerSample 	2 	Bits per sample
//cbSize 	2 	Size of the extension (0 or 22)
//wValidBitsPerSample 	2 	Number of valid bits
//dwChannelMask 	4 	Speaker position mask
//SubFormat 	16 	GUID, including the data format code
//
//
//0x0001 	WAVE_FORMAT_PCM 	PCM
//0x0003 	WAVE_FORMAT_IEEE_FLOAT 	IEEE float
//0x0006 	WAVE_FORMAT_ALAW 	8-bit ITU-T G.711 A-law
//0x0007 	WAVE_FORMAT_MULAW 	8-bit ITU-T G.711 µ-law
//0xFFFE 	WAVE_FORMAT_EXTENSIBLE 	Determined by SubFormat
//
//non-PCM must have fact chunk
//ckID 	4 	Chunk ID: "fact"
//cksize 	4 	Chunk size: minimum 4
//dwSampleLength 	4 	Number of samples (per channel)
//
//
//
//
//
//ckID 	4 	Chunk ID: "data"
//cksize 	4 	Chunk size: n
//sampled data 	n 	Samples
//pad byte 	0 or 1 	Padding byte if n is odd
//
//
//
//
//
//
//
//
//PCM Data
//Field 	Length 	Contents
//ckID 	4 	Chunk ID: "RIFF"
//cksize 	4 	Chunk size: 4 + 24 + (8 + M*Nc*Ns + (0 or 1)
//WAVEID 	4 	WAVE ID: "WAVE"
//ckID 	4 	Chunk ID: "fmt "
//cksize 	4 	Chunk size: 16
//wFormatTag 	2 	WAVE_FORMAT_PCM
//nChannels 	2 	Nc
//nSamplesPerSec 	4 	F
//nAvgBytesPerSec 	4 	F*M*Nc
//nBlockAlign 	2 	M*Nc
//wBitsPerSample 	2 	rounds up to 8*M
//ckID 	4 	Chunk ID: "data"
//cksize 	4 	Chunk size: M*Nc*Ns
//sampled data 	M*Nc*Ns 	Nc*Ns channel-interleaved M-byte samples
//pad byte 	0 or 1 	Padding byte if M*Nc*Ns is odd