import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.util.ArrayList;

public class Searcher {
    private String indexFolder;
    private String review;

    public Searcher(String review) {
        this.indexFolder = Constants.INDEX_FOLDER;
        this.review = normalizeQuery(review.toLowerCase());
        System.out.println("Preprocessed:" + this.review);
    }

    protected String normalizeQuery(String review) {
        return new PreProcessor().normalizeText("review", review);
    }

    public ArrayList<Integer> run() {
        IndexReader reader;
        IndexSearcher searcher;
        Analyzer analyzer;
        ArrayList<Integer> resultedFiles = new ArrayList<>();

        try {
            reader = DirectoryReader.open(FSDirectory.open(new File(indexFolder).toPath()));
            searcher = new IndexSearcher(reader);
            analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser(Constants.CONTENTS, analyzer);

            Query myquery = parser.parse(review);
            TopDocs results = searcher.search(myquery, Constants.MAX_SEARCH);
            ScoreDoc[] hits = results.scoreDocs;
            ScoreDoc item = hits[0];
            Document doc = searcher.doc(item.doc);
            System.out.println(doc);

        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return resultedFiles;
    }

    public static void main(String[] args) {
        String review = "Manager is not good";
        Searcher searcher = new Searcher(review);
        System.out.println(searcher.run());
    }
}
