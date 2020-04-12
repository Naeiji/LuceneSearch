import java.io.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
    private String HLC;
    private String LLC;
    private int totalIndexed = 0;

    public Indexer(String HLC, String LLC) {
        this.HLC = HLC;
        this.LLC = LLC;
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
                    int score = 0;
                    switch (HLC) {
                        case "Usage":
                            if (LLC.equals("UI")) {
                                if (path.contains("res") || path.contains("resources") || path.contains("ui") || path.contains("Activity")) {
                                    score = 1;
                                }
                            }
                            break;
                        case "Compatibility":
                            if(path.contains("AndroidManifest")) {
                                score = 1;
                            }
                            break;
                        case "Protection":
                            if(LLC.equals("Privacy") && path.contains("AndroidManifest")) {
                                score = 1;
                            }
                            break;
                        case "Resources":
                            if (path.contains("content") || path.contains("provider") || path.contains("ContentProvider")) {
                                score = 1;
                            }
                            break;
                        default:
                            score = 0;

                    }

                    Field structureField = new NumericDocValuesField(Constants.Numeric_Field, score);
                    doc.add(structureField);
                    Field contentField = new TextField(Constants.CONTENTS, new BufferedReader(new InputStreamReader(fis, "UTF-8")));
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
        Indexer indexer = new Indexer("BOY", "boy");
        indexer.indexCorpusFiles();
        System.out.println("Files indexed:" + indexer.totalIndexed);
    }
}
