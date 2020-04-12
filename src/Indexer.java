import java.io.*;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
    private String[] HLC;
    private String[] LLC;
    private int totalIndexed = 0;
    private ArrayList<String> contentProviders;

    public Indexer(String[] HLC, String[] LLC, ArrayList<String> contentProviders) {
        this.HLC = HLC;
        this.LLC = LLC;
        this.contentProviders = contentProviders;
    }

    public void indexCorpusFiles() {

        try {
            Directory dir = FSDirectory.open(new File(Constants.INDEX_FOLDER).toPath());
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(analyzer));
            indexDocs(writer, new File(Constants.CORPUS_FOLDER));
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean check(String category, String[] arr) {
        for (String element : arr) {
            if (element.equals(category)) {
                return true;
            }
        }

        return false;
    }

    public int getTotalIndexed() {
        return totalIndexed;
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
                    String path = file.getPath();
                    String score = "zero";
                    if (check("Usage", HLC) && check("UI", LLC)) {
                        if (path.contains("res") || path.contains("resources") || path.contains("ui") || path.contains("Activity")) {
                            score = "one";
                        }
                    } else if (check("Compatibility", HLC) || (check("Protection", HLC) && check("Privacy", LLC) )) {
                        if (path.contains("AndroidManifest")) {
                            score = "one";
                        }
                    } else if (check("Resources", HLC)) {
                        if (contentProviders.contains(file.getPath())) {
                            score = "one";
                        }
                    }

                    Field pathField = new StringField(Constants.PATH_FIELD, file.getPath(), Field.Store.YES);
                    doc.add(pathField);
                    Field structureField = new StringField(Constants.STRUCTURE_FIELD, score, Field.Store.YES);
                    doc.add(structureField);
                    Field textField = new TextField(Constants.TEXT_FIELD, new BufferedReader(new InputStreamReader(fis, "UTF-8")));
                    doc.add(textField);

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
}
