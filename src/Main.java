import java.io.IOException;
import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        minErrorStaging m = new minErrorStaging();
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
        minErrorStaging staging = new minErrorStaging();
        //staging.individualRuleManager(90,4,false,0,false,500,false);
        //staging.individualRuleDisplay(150,false,0,4);
        //staging.doAllRules(4,false,0,true,5000,false);
        //staging.doAllRulesCoords(4,false,0,false,0,false,new int[][]{{0,255},{85,170},{15,51},{204,240}});
        //staging.test(4);
        //staging.testAll(4);
        //staging.doAllRules(4,false,0,false,0,false);
        //PiBytes piBytes = new PiBytes();
        //piBytes.testFiveStar();
        //RuleStretchTemplate ruleStretchTemplate2 = new RuleStretchTemplate();
        //System.out.println(Arrays.toString(ruleStretchTemplate2.traditionalCenterColumn(30,16,new int[]{1},30)));
        FastMinTransform fastMinTransform = new FastMinTransform();
        //fastMinTransform.checkWolframs();
        //fastMinTransform.checkWolframsForReversibility();
        //fastMinTransform.checkNeighborWindow();
        //fastMinTransform.checkNeighborWindow(100);
        //SwingApplyFastMinTransform swingApplyFastMinTransform = new SwingApplyFastMinTransform("Image with transform");
        //swingApplyFastMinTransform.getImage();
        //fastMinTransform.checkWolframsbyCheckWolframs();
        //fastMinTransform.cnw();
        //fastMinTransform.oneHammingChange(1);
        //fastMinTransform.check();
        //fastMinTransform.reconstructFromPrimitives();
        //Addition addition = new Addition();
        //addition.testAddition();
        FastMinTransformApril fastMinTransformApril = new FastMinTransformApril();
        //fastMinTransformApril.check();
        fastMinTransform.check();


    }
}