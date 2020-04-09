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
        String review = "Amazing App This is cool because if you put your phone in your pocket and take it out it automatically turns it on which can be very convenient if you are waiting for someone to notify you for something. Also- when you turn on you phone you can see all notifications and choose whether to open them or dimiss them with ease.";
        Searcher searcher = new Searcher(review);
        System.out.println(searcher.run());
    }
}
