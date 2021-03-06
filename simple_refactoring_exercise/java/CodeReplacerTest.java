import java.io.*;
import junit.framework.*;

public class CodeReplacerTest extends TestCase {
    CodeReplacer replacer;
    public CodeReplacerTest(String testName){super(testName);}
    protected void setUp() {replacer = new CodeReplacer();}

    public void testTemplateLoadedProperly() {
	try {
	    replacer.substitute("ignored ",
				new PrintWriter(new StringWriter()));
	} catch (Exception ex) {
	    fail("No exception expected, but saw:" + ex);
	}
	assertEquals("xxx%CODE%yyy%ALTCODE%zzz\n",
		     replacer.sourceTemplate);
    }
    public void testSubstitution() {
	StringWriter stringOut = new StringWriter();
	PrintWriter testOut = new PrintWriter (stringOut);
	String trackingId = "01234567";
	    replacer.substitute(trackingId, testOut);

	assertEquals("xxx01234567yyy01234-567zzz\n",
		     stringOut.toString());
    }
    public static Test suite() {
	return new TestSuite(CodeReplacerTest.class);
    }

    static public void main( String args[]) {
	junit.textui.TestRunner.run (suite());
	
    }

}
