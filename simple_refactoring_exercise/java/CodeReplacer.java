import java.io.*;


/**
 * Replace %CODE% with requested id, and * replace %ALTCODE% w/"dashed" version
 * of id.
 */
public class CodeReplacer {

    public final String TEMPLATE_DIR = "templatedir";
    public final String templateFile;
    String sourceTemplate;
    
    CodeReplacer() {
        templateFile = System.getProperty(TEMPLATE_DIR, "") + "template.html";
        try {
            sourceTemplate = readTemplate(new FileReader(templateFile));
        } catch (IOException ex) {
            System.out.println("exception caught" + ex.getMessage());
        }
    }
    
    /**
     * @param reqId java.lang.String
     * @param oStream java.io.OutputStream
     * @exception java.io.IOException The exception description.
     */
    private String readTemplate(FileReader fileReader) throws IOException {
        StringBuffer sb = new StringBuffer("");
        BufferedReader br = null;
        try {
            br = new BufferedReader(fileReader);
            String line;
            while (((line = br.readLine()) != null)) {
                sb.append(line);
                sb.append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("exception caught" + e.getCause() + e.getMessage());
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return sb.toString();
    }

    private String substituteForCode(String template, String reqId, String substitutablePattern) {
        int templateSplitBegin = template.indexOf(substitutablePattern);
        int templateSplitEnd = templateSplitBegin + substitutablePattern.length();
        StringBuffer sb = new StringBuffer("");
        sb.append(template.substring(0, templateSplitBegin));
        sb.append(reqId);
        sb.append(template.substring(templateSplitEnd, template.length()));
        return sb.toString();
    }

    public void substitute(String reqId, PrintWriter out) {
        try {
            String result = sourceTemplate;
            result = substituteForCode(result, reqId, "%CODE%");
            result = substituteForCode(result,
                    reqId.substring(0, 5) + "-" + reqId.substring(5, 8),
                    "%ALTCODE%");
            out.print(result);
        } catch (Exception e) {
            System.out.println("Error in substitute()");
        }
        out.close();
    }
}


