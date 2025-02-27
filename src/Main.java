//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        minErrorStaging m = new minErrorStaging();
        //m.manageLengthsMinimizations(150,4,500);
        //m.ecaBestFitHashCollisionExhuastive(150,4,false,false,500);
        RuleStretchTemplate ruleStretchTemplate = new RuleStretchTemplate();
        //ruleStretchTemplate.manageBoth(30);
        ECAexponentReduction ecaexponentReduction = new ECAexponentReduction();
        //ecaexponentReduction.checkColumns(30,50);
        ecaexponentReduction.checkAllColumns(30);
    }
}