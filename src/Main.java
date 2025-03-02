//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        minErrorStaging m = new minErrorStaging();
        //m.manageLengthsMinimizations(150,4,500);
        //m.ecaBestFitHashCollisionExhuastiveSizeTwo(105,2,false,false,500);
        RuleStretchTemplate ruleStretchTemplate = new RuleStretchTemplate();
        //ruleStretchTemplate.manageBoth(30);
        ECAexponentReduction ecaexponentReduction = new ECAexponentReduction();
        //ecaexponentReduction.checkColumns(30,50);
        //ecaexponentReduction.checkAllColumns(30);
        PrimeCA primeCA = new PrimeCA();
        //primeCA.outputPrimeField();
        //primeCA.checkAllColumnsTwo(primeCA.longRule, 2,5);
        //primeCA.doLogReductionPrimeThree(primeCA.primeRule, 2,3);
        //primeCA.doLogReductionPrime(primeCA.primeRule, 3,5);
        //primeCA.checkAllColumns(primeCA.longRule, 3,5);
        PrimeCAstaging primeCAstaging = new PrimeCAstaging();
        //primeCAstaging.doLogReductionPrime(primeCAstaging.primeRule,3,4);
        PrimeCAstagingLONG primeCAstagingLONG = new PrimeCAstagingLONG();
        //primeCAstagingLONG.checkAllColumnsTwo(primeCAstagingLONG.longRule, 3,4);
        primeCAstagingLONG.doLogReductionPrimeTwo(primeCAstagingLONG.primeRule, 3,5);


    }
}