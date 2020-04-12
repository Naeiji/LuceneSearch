import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Searcher {
    private String review;

    public Searcher(String review) {
        this.review = normalizeQuery(review.toLowerCase());
        System.out.println("Preprocessed:" + this.review);
    }

    protected String normalizeQuery(String review) {
        return new PreProcessor().normalizeText(review);
    }

    public void run() {
        IndexReader reader;
        IndexSearcher searcher;
        Analyzer analyzer;

        try {
            reader = DirectoryReader.open(FSDirectory.open(new File(Constants.INDEX_FOLDER).toPath()));
            searcher = new IndexSearcher(reader);
            analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser(Constants.TEXT_FIELD, analyzer);


            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            Query query1 = new TermQuery(new Term(Constants.TEXT_FIELD, review));
            booleanQuery.add(query1, BooleanClause.Occur.SHOULD);
            Query query2 = new TermQuery(new Term(Constants.STRUCTURE_FIELD, "one"));
            booleanQuery.add(query2, BooleanClause.Occur.SHOULD);

            Query myquery = parser.parse(booleanQuery.build().toString());
            TopDocs results = searcher.search(myquery, Constants.MAX_SEARCH);
            for (ScoreDoc hit : results.scoreDocs) {
                Document doc = searcher.doc(hit.doc);
                String path = doc.get(Constants.PATH_FIELD).replace(Constants.CORPUS_FOLDER, "");
                System.out.println("Hit: " + path + " Score: " + hit.score + "  " + doc.get(Constants.STRUCTURE_FIELD));
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
