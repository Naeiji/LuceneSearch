import java.io.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
    private String docs;
    private String index;
    public int totalIndexed = 0;

    public Indexer() {
        this.docs = Constants.DOCS_FOLDER;
        this.index = Constants.INDEX_FOLDER;
    }

    public void indexCorpusFiles() {
        try {
            Directory dir = FSDirectory.open(new File(index).toPath());
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(dir, config);
            indexDocs(writer, new File(this.docs));
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void clearIndexFiles() {
        File[] files = new File(this.index).listFiles();
        for (File f : files) {
            f.delete();
        }
        System.out.println("Index cleared successfully.");
    }

    protected void indexDocs(IndexWriter writer, File file) {
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();

                for (String i : files) {
                    indexDocs(writer, new File(file, i));
                }
            } else {
                FileInputStream fis;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException fnfe) {
                    return;
                }
                try {
                    // make a new, empty document
                    Document doc = new Document();
                    Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
                    doc.add(pathField);
                    Field contentField = new TextField("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8")));
                    doc.add(contentField);

                    // doc.add(new TextField("contents", new BufferedReader(
                    // new InputStreamReader(fis, "UTF-8"))));
                    // System.out.println("adding " + file);

                    writer.addDocument(doc);
                    totalIndexed++;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Indexer indexer = new Indexer();
        indexer.indexCorpusFiles();
        System.out.println("Files indexed:" + indexer.totalIndexed);
    }
}
