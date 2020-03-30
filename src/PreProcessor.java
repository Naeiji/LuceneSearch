import org.tartarus.snowball.ext.PorterStemmer;
import utilities.ContentLoader;
import utilities.ContentWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class PreProcessor {
    private ArrayList<String> splittedFile;
    private ArrayList<String> stopWordList;

    public PreProcessor() {
        this.splittedFile = new ArrayList<>();

        this.stopWordList = new ArrayList<>();
        this.stopWordList.addAll(ContentLoader.getAllLinesOptList(Constants.STOPWORD_DIR + "/stop-words"));
        this.stopWordList.addAll(ContentLoader.getAllLinesOptList(Constants.STOPWORD_DIR + "/java-keywords"));
    }

    public String normalizeText(String name, String content) {
        if (name.endsWith(".java")) {
            int a = content.indexOf("class");
            int b = content.lastIndexOf("}");

            content = content.substring(a, b + 1);
        }

        PorterStemmer ps = new PorterStemmer();
        StringTokenizer st = new StringTokenizer(content, " ._():;={},\"\'@?+-/\\\n\t<>$");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            for (String word : token.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
                String cleanWord = word.toLowerCase().trim();
                ps.setCurrent(cleanWord);
                ps.stem();
                cleanWord = ps.getCurrent();
                if ((cleanWord.length() > 1) && !this.stopWordList.contains(cleanWord)) {
                    this.splittedFile.add(cleanWord);
                }
            }
        }

        return this.splittedFile.toString().replaceAll(",", "").replace("[", "").replace("]", "");
    }

    private void normalizeCorpus() {
        File dir = new File(Constants.DOCS_FOLDER);
        File[] files = dir.listFiles();
        for (File f : files) {
            String content = ContentLoader.loadFileContent(f.getAbsolutePath());
            String normalized = normalizeText(f.getName(), content);
            String normOutputFile = Constants.CORPUS_FOLDER + "/" + f.getName();
            ContentWriter.writeContent(normOutputFile, normalized);
            System.out.println("Done: " + f.getName());
        }
    }

    public static void main(String[] args) {
        new PreProcessor().normalizeCorpus();
    }
}
