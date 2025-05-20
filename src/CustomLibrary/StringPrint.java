package CustomLibrary;

/**
 * Custom string class
 */
public class StringPrint {
    /**
     * Main field, this accumulates a String until printed
     */
    String outstring = "";

    /**
     * Currently empty
     */
    public StringPrint() {
    }

    /**
     * Appends the given string followed by a newline character to the internal string accumulator,
     * prints it to the standard output, and returns the string.
     *
     * @param instring the string to be printed and appended to the internal accumulator
     * @return the same string that was passed as input
     */
    public String println(String instring) {
        outstring += instring + "\n";
        System.out.println(instring);
        return instring;
    }

    /**
     * Prints an empty line to the standard output and returns an empty string.
     *
     * @return an empty string
     */
    public String println() {
        System.out.println();
        return "";
    }

    /**
     * Appends the given string to the internal string accumulator, prints it to the standard output,
     * and returns the string.
     *
     * @param instring the string to be printed and appended to the internal accumulator
     * @return the same string that was passed as input
     */
    public String print(String instring) {
        outstring += instring;
        System.out.print(instring);
        return instring;
    }

    /**
     * Returns the accumulated string stored in the internal string accumulator.
     *
     * @return the internal accumulated string
     */
    public String aggregatedOutput() {
        return outstring;
    }
}
