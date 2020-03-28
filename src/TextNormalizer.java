import org.tartarus.snowball.ext.PorterStemmer;
import utilities.ContentLoader;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class TextNormalizer {
    private String name;
    private String content;
    private ArrayList<String> splittedFile;
    private ArrayList<String> stopWordList;

    public TextNormalizer(String name, String content) {
        this.name = name;
        this.content = content;
        this.splittedFile = new ArrayList<>();

        this.stopWordList = new ArrayList<>();
        this.stopWordList.addAll(ContentLoader.getAllLinesOptList(Constants.STOPWORD_DIR + "/stop-words"));
        this.stopWordList.addAll(ContentLoader.getAllLinesOptList(Constants.STOPWORD_DIR + "/java-keywords"));
    }

    public String normalizeText() {
        if (this.name.endsWith(".java")) {
            int a = this.content.indexOf("class");
            int b = this.content.lastIndexOf("}");

            this.content = this.content.substring(a, b + 1);
        }

        PorterStemmer ps = new PorterStemmer();
        StringTokenizer st = new StringTokenizer(this.content, " ._():;={},\"\'@?+-/\\\n\t<>$");
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
}