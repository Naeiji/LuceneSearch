package utilities;

import java.io.File;
import java.io.FileWriter;

public class ContentWriter {
    public static void writeContent(String outFile, String content) {
        try {
            File file = new File(outFile);
            file.getParentFile().mkdirs();
            FileWriter fwriter = new FileWriter(file);
            fwriter.write(content);
            fwriter.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
