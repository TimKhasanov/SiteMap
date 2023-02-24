
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveTask;

public class Site extends RecursiveTask<Set<String>> {

    private String url;
    private static final String ATTRIBUTE_KEY = "href";
    private static final String CSS_QUERY = "a[href]";
    private static final Set<String> links = new TreeSet<>();

    public Site(String url) {
        System.out.println(url);
        this.url = url;
    }

    private Set<String> pageParsing(String url) {
        Set<String> links = new TreeSet<>();
        try {
            Document doc = Jsoup.connect(url).maxBodySize(0).get();
            Elements elements = doc.select(CSS_QUERY);

            for (Element element : elements) {
                String link = element.absUrl(ATTRIBUTE_KEY);
                if (checkURL(link) && addNewURL(link)) {
                    links.add(link);
                }
            }
            Thread.sleep(200);
        } catch (HttpStatusException | SocketTimeoutException ex) {
            return links;
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return links;
    }

    private synchronized boolean addNewURL(String url) {
        return links.add(url);
    }

    private boolean checkURL(String url) {
        return url.startsWith("https://skillbox.ru/") && url.endsWith("/");
    }


    @Override
    protected Set<String> compute() {
        List<Site> taskList = new CopyOnWriteArrayList<>();
        Set<String> m = new TreeSet<>(pageParsing(url));
        for (String link : m) {
            Site task = new Site(link);
            task.fork();
            taskList.add(task);
            links.add(link);
        }
        for (Site task1 : taskList) {
            task1.join();
        }
        return links;
    }

    public Set<String> getSet() {
        return links;
    }
}




