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
                Document doc = searcher.doc(hit.doc);
                System.out.println(doc.toString() + " " + hit.score);
            }

            System.out.println(results.totalHits);


        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return resultedFiles;
    }

    public static void main(String[] args) {
        String review = "Great app just needs a few additions This is a fantastic app for someone coming from the Motorola ecosystem that misses the active display as badly as I did. There needs to be a way to swipe the media player out of the way to access the normal acdisplay screen. There also needs to be an addition that brings up acdisplay when the phone is picked up after sitting for a set period of time. I have issues with the proximity wake but pulling it out of my pocket always seems to work. Otherwise this is a fantastic app!";
        Searcher searcher = new Searcher(review);
        System.out.println(searcher.run());
    }
}
