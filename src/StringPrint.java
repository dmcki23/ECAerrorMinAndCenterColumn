public class StringPrint {
    String outstring = "";
    public String println(String instring) {
        outstring += instring + "\n";
        System.out.println(instring);
        return instring;
    }
    public String println() {
        System.out.println();
        return "";
    }
    public String print(String instring) {
        outstring += instring;
        System.out.print(instring);
        return instring;
    }
    public String getAggregatedOutput() {
        return outstring;
    }
}
