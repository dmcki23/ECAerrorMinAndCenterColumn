import AlgorithmCode.*;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

/**
 * Standard Main class
 */
public class Main {
    /**
     * Standard main function
     * @param args Standard arg parameter, empty atm
     * @throws IOException prints a message
     */
    public static void main(String[] args) throws IOException {

        //
         //
         //
         //
         //
         //From this point: May 13, testing the recent refactor
        //
         //In the paper, the only major things I saw were the avalanche and testing paragraphs and images
        //also need to think about the website
        Hash hash = new Hash();
        //HashCollisions hashCollisions = new HashCollisions();
        int rule = 2;
        String filepath = "kitchenShrunkTest.bmp";

        //this one boots everything up upon construction, sometimes causes conflicts if called independently
        //hash.initWolframs();
        //hash.hashRows.doAllRules(4,false,0,false,0,false);
        //hash.hashColumns.doAllRules(4,false,0,false,0,false);
        //does size 8, must initialize(8) first
        //hash.hashColumns.initialize(8);
        //hash.hashRows.initialize(8);
        //hash.hashRows.doAllRules(8,false,0,true,1000,false);
        //hash.hashColumns.doAllRules(8,false,0,true,1000,false);
        //good
        //hash.oneDHashTransform.testOneD();
        //needs checking
        //hash.hashArrayCompression(new int[1][1],hash.bothLists[0][rule],1,true,true);
        //hash.hashArrayCompression(new int[1][1],hash.bothLists[0][rule],1,false,true);
        //changeChange() needs display tweaking
        //these two test random input of size four with changeChange() calculating the codeword hammingDistance/errorScoreChange
        //hash.hashRows.doAllRules(4,true,100,true,100,false);
        //hash.hashColumns.doAllRules(4,true,100,true,100,false);
        //these hash a bitmap
        //hash.hashTwoDbitmap.hashBitmap(filepath, hash.bothLists[0][rule],true,true);
        //hash.hashBitmap(filepath, hash.bothLists[1][rule],false,true);
        //hash.hashBitmap(filepath, hash.bothLists[0][rule],true,false);
        //hash.hashBitmap(filepath, hash.bothLists[1][rule],false,true);
        //hash.verifyInverseAndAvalanche(filepath,0);
        //hash.verifyInverseAndAvalanche(filepath,0,false);
        //filepath = "kitchenShrunkThreeByte.bmp";
        //hash.hashTwoDhex.verifyInverseAndAvalancheSingleBitsHex(filepath,false,4);
        //hash.hashTwoDhex.verifyInverseAndAvalancheHex(filepath,false,4);
        hash.hashLogicOpTransform.checkEveryGateEveryDepth(filepath);
        //hash.hashLogicOpTransform.testAllLogic();
        //hash.hashLogicOpTransform.verifyLogicOperationHash(filepath,0,true);
        //hash.hashLogicOpTransform.analyzeHashLogicOpIncompletionsFourAxis();
        //This one doesn't do anything because the truth table is incomplete,
        //Not all gates and elements have a single operation transform
        //Some gates and elements do, but not all have a known retroactive hashing
        //hash.hashLogicOpTransform.verifyLogicOperationHash(filepath,0,false);
        //done, is unique with all 4 layers
        //hashCollisions.checkTupleUniqueness();
        //needs doing
        //hashCollisions.checkCompressionCollisions();
        //hash.hashCollisions.checkCodewordSymmetry();
        //hash.hashRows.doAllRules(4);
        //This randomly checks the algorithm with size 8 arrays instead of 4

        //I feel like if all the previous are working, the paper is ready for arXiv
        //todo changeChange() tweaks
        //todo checkCompressionCollisions()
        //
         //
         //
         //
         //
        //hash.initWolframs(true);
        //hash.initWolframsFromFileTest();
        //hash.hashRows.doAllRules(4,false,0,false,0,false);
        //hash.hashColumns.doAllRules(4,false,0,false,0,false);
        //hash.hashRows.doAllRules(8,false,0,true,1000,false);
        //hash.hashColumns.doAllRules(8,false,0,true,100,false);
        //good
        //hash.oneDHashTransform.testOneD();
        //needs checking
        //hash.hashArrayCompression(new int[1][1],hash.bothLists[0][rule],1,true,true);
        //hash.hashArrayCompression(new int[1][1],hash.bothLists[0][rule],1,false,true);
        //changeChange() needs tweaking
        //hash.hashRows.doAllRules(4,true,100,true,100,false);
        //hash.hashColumns.doAllRules(4,true,100,true,100,false);
        //most of hashBitmap() is working except the gif
        //hash.hashBitmap(filepath, hash.bothLists[0][rule],true,true);
        //hash.hashBitmap(filepath, hash.bothLists[1][rule],false,true);
        //hash.hashBitmap(filepath, hash.bothLists[0][rule],true,false);
        //hash.hashBitmap(filepath, hash.bothLists[1][rule],false,true);
        //needs double checking
        //hash.verifyInverseAndAvalanche(filepath,0);
        //hash.verifyInverseAndAvalanche(filepath,0,false);
        //hash.hashNonHexadecimal.hashBitmapSingleBit(filepath,0);
        //hash.hashLogicOpTransform.testAllLogic(true);
        //hash.hashLogicOpTransform.testAllLogic(false);
        //hash.hashLogicOpTransform.verifyLogicOperationHash(filepath,0,true);
        //hash.hashLogicOpTransform.verifyLogicOperationHash(filepath,0,false);
        //done, is unique with all 4 layers
        //hashCollisions.checkTupleUniqueness();
        //needs doing
        //hashCollisions.checkCompressionCollisions();

        //hash.hashCollisions.checkCodewordSymmetry();
        //hash.hashRows.doAllRules(4);
        //
         //
        //
        //
         //
         //
         //
         //May 20
        //commenting, testing, avalanche, images, website presentation, possibly reading from the file as well
        //hash.twoDHashTransform.hashBitmap(filepath, hash.bothLists[0][rule],true,true);
        //hash.hashTwoDbitmap.verifyInverseAndAvalanche(filepath);
        //hash.hashLogicOpTransform.verifyLogicOperationHash("kitchenShrunk.bmp",true);
        //hash.hashTwoDhex.verifyInverseAndAvalancheSingleBits(filepath);
        //hash.hashLogicOpTransform.verifyLogicOperationHash("kitchenShrunk.bmp",true);
        //
         //
        //
        //
        //
        //
        //I keep these as a record of experimentation and debugging
        //HashTruthTables m = new HashTruthTables(true);
        //m.manageLengthsMinimizations(150,4,500);
        //m.ecaBestFitHashCollisionExhuastiveSizeTwo(105,2,false,false,500);
        // ruleStretchTemplate = new RuleStretchTemplate();
        //ruleStretchTemplate.manageBoth(30);
        //ECAexponentReduction ecaexponentReduction = new ECAexponentReduction();
        //ecaexponentReduction.checkColumns(30,50);
        //ecaexponentReduction.checkAllColumns(30);
        //primeCA.outputPrimeField();
        //primeCA.checkAllColumnsTwo(primeCA.longRule, 2,5);
        //primeCA.doLogReductionPrimeThree(primeCA.primeRule, 2,3);
        //primeCA.doLogReductionPrime(primeCA.primeRule, 3,5);
        //primeCA.checkAllColumns(primeCA.longRule, 3,5);
        //primeCAstaging.doLogReductionPrime(primeCAstaging.primeRule,3,4);
        //PrimeCAstagingLONG primeCAstagingLONG = new PrimeCAstagingLONG();
        //primeCAstagingLONG.checkAllColumnsTwo(primeCAstagingLONG.longRule, 3,4);
        //primeCAstagingLONG.doLogReductionPrimeTwo(primeCAstagingLONG.primeRule, 3,5);
        //staging.individualRuleManager(90,4,false,0,false,500,false);
        //staging.individualRuleDisplay(150,false,0,4);
        //staging.doAllRules(4,false,0,true,5000,false);
        //staging.doAllRulesCoords(4,false,0,false,0,false,new int[][]{{0,255},{85,170},{15,51},{204,240}});
        //staging.test(4);
        //staging.testAll(4);
        //staging.doAllRules(4,false,0,false,0,false);
        //AlgorithmCode.PiBytes piBytes = new AlgorithmCode.PiBytes();
        //piBytes.testFiveStar();
        //RuleStretchTemplate ruleStretchTemplate2 = new RuleStretchTemplate();
        //System.out.println(Arrays.toString(ruleStretchTemplate2.traditionalCenterColumn(30,16,new int[]{1},30)));
        //fastMinTransform.checkWolframs();
        //fastMinTransform.checkWolframsForReversibility();
        //fastMinTransform.checkNeighborWindow();
        //fastMinTransform.checkNeighborWindow(100);
        //AlgorithmCode.SwingApplyFastMinTransform swingApplyFastMinTransform = new AlgorithmCode.SwingApplyFastMinTransform("Image with transform");
        //swingApplyFastMinTransform.getImage();
        //fastMinTransform.checkWolframsbyCheckWolframs();
        //fastMinTransform.cnw();
        //fastMinTransform.oneHammingChange(1);
        //fastMinTransform.check();
        //fastMinTransform.reconstructFromPrimitives();
        //AlgorithmCode.Addition addition = import TrimmedCode.AttemptSize8;
        //new AlgorithmCode.Addition();
        //addition.testAddition();
        //HashCollisions hashCollisions = new HashCollisions();
        //fastMinTransformApril.check();
        //fastMinTransform.check();
        //fastMinTransformApril.checkErrorWeights();
        //HashLogicOpTransform a = new HashLogicOpTransform();
        //a.checkAdditionParity(16);
        //fastMinTransformApril.checkCollisions();
        //fastMinTransformApril.checkWolframCollision();
        //fastMinTransformApril.checkOneTileSlideNextDoor();
        //fastMinTransformApril.checkOneTileSlide();
        //fastMinTransformApril.checkOneTileSlideNextDoorRandomized();
        //AttemptSize8 attemptSize8 = new AttemptSize8();
        //attemptSize8.checkLastRowWeight();
        //m.individualRuleManager(150,4,false,0,false,0,false);
        //PiBytes piBytes = new PiBytes();
        //piBytes.checkStar();
        //
        //
        //
        //
        //Testing post trim, post javadoc, post inline commenting, post april 5 2025
        //Everything after this line has been tested post 4-5-2025, DM
        //Hash hash = new Hash();
        //HashCollisions hashCollisions = new HashCollisions();
        //m.doAllRules(4,false,0,false,0,false);
        //hash.hash.initWolframs();
        //for (int posNeg = 0; posNeg < 2; posNeg++) {
        //    for (int row = 0; row < 8; row++) {
        //        for (int col = 0; col < 64; col++) {
        //            System.out.print(hash.hash.flatWolframs[posNeg][row][col] + "\t");
        //        }
        //        System.out.print("\n");
        //    }
        //}
        //
        //hash.check();
        //Hadamard hadamard = new Hadamard();
        //hadamard.test(16);
        //m.oneFiftyDisplay();
        //hash.getImage();
        //hashCollisions.checkErrorScoreVsHadamard();
        //hashCollisions.checkTupleUniqueness();
        //hashCollisions.checkUnitWrappedTupleUniqueness();
        //hashCollisions.randomizedCollisionChecker();
        //hashCollisions.checkLastRowWeight();
        //a = new Addition();
        //a.testXOR();
        //Somewhere around here new features began
        //hash.check();
        //Hadamard hadamard = new Hadamard();
        //hadamard.dftOfHadamard(8);
        //a.testAllLogic();
        //hash.check();
        //hashCollisions.runXORtableThroughHash();
        //hashCollisions.runThroughHash();
        //hashCollisions.checkChangesPerTransform();
        //hash.bitmapTransform(0);
        //hashCollisions.checkChangesPerTransform(4);
        //hashCollisions.checkSinglesAgainstAll(4);
        //hashCollisions.checkTableForTwoChanges();
        //hashCollisions.exploreTwoChanges(4);
        //HashUtilities hashUtilities = new HashUtilities();
        //hashUtilities.exploreTwoChanges(4);
        //hashUtilities.checkSinglesAgainstAllVisualize(8);
        //hashUtilities.generateAbsolutelyEverything(4);
        //hash.bitmapTransform();
        //hashUtilities.zerosRelativeTruthTable();
        //EdgeDetection edgeDetection = new EdgeDetection();
        //edgeDetection.bitmapTransform();
        //hashUtilities.zerosRelativeTruthTable();
        //hashUtilities.checkDoubles();
        //edgeDetection.bitmapTransform();
        //hashUtilities.zerosRelativeTruthTable();
        //hashUtilities.checkDoublesSetsFunctions();
        //hashUtilities.generateEveryHadamardishFunction(4);
        //hashUtilities.bitmapTransformTwoRGBBytes(0);
        //hashUtilities.testWriteToFile();
        //hashUtilities.checkDoublesSetsFunctions();
        //hashUtilities.generateEveryLogicFunction(16);
        //hashUtilities.bitmapTransformTwoRGBBytes("linesTwoByteRGB.bmp",0);
        //hashUtilities.checkDoublesSetsFunctions();
        //edgeDetection.bitmapTransform("OtherRoom");
        //a.testAllLogic();
        //hash.bitmapTransformCompleteSet("OtherRoom.bmp",0);
        //hash.bitmapTransformCompleteSet("OtherRoom.bmp",0);
        //hashUtilities.checkDoublesSetsFunctionsTwo();
        //a.bitmapTransformCompleteSetOneD("kitchenShrunk.bmp",0);
        //a.testAllLogic();
        //hash.bitmapTransformCompleteSetColumnsToo("kitchenShrunk.bmp",0,false);
        //hash.m.rowError = false;
        //hash.m.doAllRules(4,false,0,false,0,false);
        //a.testAllLogic(false);
        //hash.m.oneFiftyDisplay();
        //hashCollisions.checkTupleUniqueness();
        }
}