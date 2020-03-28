import utilities.ContentLoader;
import utilities.ContentWriter;

import java.io.File;

public class Tokenizer {
    String corpusFolder;
    String normCorpusFolder;

    public Tokenizer(String docs, String normDocs) {
        this.corpusFolder = Constants.HOME_DIR + "/files/" + docs;
        this.normCorpusFolder = Constants.HOME_DIR + "/files/" + normDocs;
    }

    protected void normalizeCorpus() {
        File dir = new File(this.corpusFolder);
        File[] files = dir.listFiles();
        for (File f : files) {
            String content = ContentLoader.loadFileContent(f.getAbsolutePath());
            TextNormalizer normalizer = new TextNormalizer(f.getName(), content);
            String normalized = normalizer.normalizeText();
            String normOutputFile = this.normCorpusFolder + "/" + f.getName();
            ContentWriter.writeContent(normOutputFile, normalized);
            System.out.println("Done: " + f.getName());
        }
    }

    public static void main(String[] args) {
        String docs = "docs";
        String normDocs = "norm-docs";
        new Tokenizer(docs, normDocs).normalizeCorpus();
    }
}
