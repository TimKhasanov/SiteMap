import java.io.*;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

public class Main {

    private static final String DATA_STORAGE_LOCATION = "src/main/resources/map.txt";
    private static final String URL = "https://skillbox.ru/";
    private static final int numberOfCores = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        Site siteParsing = new Site(URL);
        new ForkJoinPool(numberOfCores).invoke(siteParsing);
        saveToFile(DATA_STORAGE_LOCATION, siteParsing);

    }
    public static void saveToFile(String path, Site links) {
        try {
            FileWriter writer = new FileWriter(path);
            for (String searchOfLinks : links.getSet()) {
                writer.write(formatLinkView(searchOfLinks) + '\n');
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String formatLinkView(String link) {
        long slashCount = (link.chars().filter(ch -> ch == '/').count()) - 3;
        if (slashCount > 0) {
            link = String.join("", Collections.nCopies((int) slashCount, "\t")) + link;
        }
        return link;
    }
}

