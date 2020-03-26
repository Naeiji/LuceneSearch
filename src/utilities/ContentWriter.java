package utilities;

import java.io.File;
import java.io.FileWriter;

public class ContentWriter {
    public static void writeContent(String outFile, String content) {
        try {
            FileWriter fwriter = new FileWriter(new File(outFile));
            fwriter.write(content);
            fwriter.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
