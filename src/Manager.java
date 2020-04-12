import java.io.File;
import java.util.Scanner;

public class Manager {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String line = scanner.nextLine();
        String review = line.trim();
        line = scanner.nextLine();
        String[] HLC = line.trim().split(",");
        line = scanner.nextLine();
        String[] LLC = line.trim().split(",");

        // PreProcessing
        PreProcessor preProcessor = new PreProcessor();
        preProcessor.iterate(new File(Constants.DOCS_FOLDER));

        // Indexing
        Indexer indexer = new Indexer(HLC, LLC, preProcessor.getContentProviders());
        indexer.indexCorpusFiles();
        System.out.println(indexer.getTotalIndexed());

        // Searching
        Searcher searcher = new Searcher(review);
        searcher.run();
    }
}
