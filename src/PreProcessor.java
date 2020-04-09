import org.tartarus.snowball.ext.PorterStemmer;
import utilities.ContentLoader;
import utilities.ContentWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class PreProcessor {
    private ArrayList<String> stopWordList;

    public PreProcessor() {
        this.stopWordList = new ArrayList<>();
        this.stopWordList.addAll(ContentLoader.getAllLinesOptList(Constants.STOPWORD_DIR + "/stop-words"));
        this.stopWordList.addAll(ContentLoader.getAllLinesOptList(Constants.STOPWORD_DIR + "/java-keywords"));
    }

    public String normalizeText(String content) {
        ArrayList<String> splittedFile = new ArrayList<>();

        PorterStemmer ps = new PorterStemmer();
        StringTokenizer st = new StringTokenizer(content, " ._():;={},\"\'@?*+-/\\\n\t<>$");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            for (String word : token.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
                String cleanWord = word.toLowerCase().trim();
                if(this.stopWordList.contains(cleanWord)) {
                    continue;
                }
                ps.setCurrent(cleanWord);
                ps.stem();
                cleanWord = ps.getCurrent();
                if ((cleanWord.length() > 1)) {
                    splittedFile.add(cleanWord);
                }
            }
        }

        return splittedFile.toString().replaceAll(",", "").replace("[", "").replace("]", "");
    }

    public void iterate(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                iterate(f);
            }
        } else if (file.getName().endsWith(".java") || file.getName().endsWith(".xml")) {
            String content = ContentLoader.loadFileContent(file.getAbsolutePath());
            String normalized = normalizeText(content);
            String normOutputFile = Constants.CORPUS_FOLDER + "/" + file.getName();
            ContentWriter.writeContent(normOutputFile, normalized);
            System.out.println("Done: " + file.getName());
        }
    }

    public static void main(String[] args) {
        new PreProcessor().iterate(new File(Constants.DOCS_FOLDER));
    }
}
