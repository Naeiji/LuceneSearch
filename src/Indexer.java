import java.io.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
    private int totalIndexed = 0;

    public void indexCorpusFiles() {
        try {
            Directory dir = FSDirectory.open(new File(Constants.INDEX_FOLDER).toPath());
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(analyzer));
            indexDocs(writer, new File(Constants.DOCS_FOLDER));
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void indexDocs(IndexWriter writer, File file) {
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
                    Document doc = new Document();
                    Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
                    doc.add(pathField);
                    Field contentField = new TextField("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8")));
                    doc.add(contentField);

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
