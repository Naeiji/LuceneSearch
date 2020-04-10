import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.surround.parser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.util.ArrayList;

public class Searcher {
    private String review;

    public Searcher(String review) {
        this.review = normalizeQuery(review.toLowerCase());
        System.out.println("Preprocessed:" + this.review);
    }

    protected String normalizeQuery(String review) {
        return new PreProcessor().normalizeText(review);
    }

    public ArrayList<Integer> run() {
        IndexReader reader;
        IndexSearcher searcher;
        Analyzer analyzer;
        ArrayList<Integer> resultedFiles = new ArrayList<>();

        try {
            reader = DirectoryReader.open(FSDirectory.open(new File(Constants.INDEX_FOLDER).toPath()));
            searcher = new IndexSearcher(reader);
            analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser(Constants.CONTENTS, analyzer);

            Query myquery = parser.parse(review);
            TopDocs results = searcher.search(myquery, Constants.MAX_SEARCH);
            ScoreDoc[] hits = results.scoreDocs;
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc((hit.doc));
                System.out.println(doc.toString() + " " + hit.score);
            }

            System.out.println(results.totalHits);


        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return resultedFiles;
    }

    public static void main(String[] args) {
        String review = "Great but with its downsides (bugs) Awesome lockscreen for seeing and handling notifications. My favorite feature is that I can swipe from anywhere in any direction to unlock. Has a few (very) annoying (recent) bugs such as locking the screen while I'm using my phone if I get a notification or the proximity sensor causing unwanted locks while using";
        Searcher searcher = new Searcher(review);
        System.out.println(searcher.run());
    }
}
