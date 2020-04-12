import org.tartarus.snowball.ext.PorterStemmer;
import utilities.ContentLoader;
import utilities.ContentWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class PreProcessor {
    private ArrayList<String> stopWordList;
    private ArrayList<String> contentProviders;

    public PreProcessor() {
        this.stopWordList = new ArrayList<>();
        this.stopWordList.addAll(ContentLoader.getAllLinesOptList(Constants.STOPWORD_DIR + "/stop-words"));
        this.stopWordList.addAll(ContentLoader.getAllLinesOptList(Constants.STOPWORD_DIR + "/java-keywords"));
    }

    public String normalizeText(String content) {
        ArrayList<String> splittedFile = new ArrayList<>();

        PorterStemmer ps = new PorterStemmer();
        StringTokenizer st = new StringTokenizer(content, " ._()#!:;={},\"\'@?*+-/\\\n\t<>$");
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

    public ArrayList<String> getContentProviders() {
        return contentProviders;
    }

    public void iterate(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                iterate(f);
            }
        } else if (file.getName().endsWith(".java") || file.getName().endsWith(".xml")) {
            if (file.getAbsolutePath().contains("/values-")) {
                return;
            }

            String content = ContentLoader.loadFileContent(file.getAbsolutePath());

            contentProviders = new ArrayList<>();
            if(content.contains("content") || content.contains("provider") || content.contains("ContentProvider")) {
                contentProviders.add(file.getPath());
            }

            String normalized = normalizeText(content);
            String normOutputFile = file.getAbsolutePath().replace(Constants.DOCS_FOLDER, Constants.CORPUS_FOLDER);
            ContentWriter.writeContent(normOutputFile, normalized);
            System.out.println("Done: " + file.getName());
        }
    }
}
