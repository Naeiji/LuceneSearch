import utilities.ContentLoader;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class TextNormalizer {
    private String content;
    private ArrayList<String> splittedFile;
    private ArrayList<String> stopWordList;

    public TextNormalizer(String content) {
        this.content = content;
        this.splittedFile = new ArrayList<>();

        this.stopWordList = new ArrayList<>();
        this.stopWordList.addAll(ContentLoader.getAllLinesOptList(StaticPath.STOPWORD_DIR + "/stop-words"));
        this.stopWordList.addAll(ContentLoader.getAllLinesOptList(StaticPath.STOPWORD_DIR + "/java-keywords"));
    }

    public String normalizeText() {
        int a = this.content.indexOf("class");
        int b = this.content.lastIndexOf("}");

        this.content = this.content.substring(a, b + 1);

        StringTokenizer st = new StringTokenizer(this.content, " ._():;={},\"\'@?+-/\\\n\t<>");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            for (String word : token.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
                String cleanWord = word.toLowerCase().trim();
                if ((cleanWord.length() > 2) && !this.stopWordList.contains(cleanWord)) {
                    this.splittedFile.add(cleanWord);
                }
            }
        }

        return this.splittedFile.toString().replaceAll(",", "").replace("[", "").replace("]", "");
    }
}