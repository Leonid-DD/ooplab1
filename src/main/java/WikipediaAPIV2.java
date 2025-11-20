import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WikipediaAPIV2 {
    private static final String SEARCH_URL = "https://ru.wikipedia.org/w/api.php?" +
            "action=query&format=json&list=search&srlimit=10&srsearch=";
    private static final String PAGE_URL = "https://ru.wikipedia.org/wiki/";

    public String search(String query) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String urlString = SEARCH_URL + encodedQuery;

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent",
                "WikipediaSearchApp/1.0 (Java; +https://github.com/example)");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Выполняем запрос к: " + urlString);

        int responseCode = connection.getResponseCode();
        System.out.println("Код ответа: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
            throw new RuntimeException("Доступ запрещен (403). Проверьте User-Agent и лимиты запросов.");
        } else if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP ошибка: " + responseCode + " - " + connection.getResponseMessage());
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        connection.disconnect();
        return response.toString();
    }

    public String getPageUrl(String title) throws Exception {
        return PAGE_URL + URLEncoder.encode(title.replace(" ", "_"), StandardCharsets.UTF_8);
    }

    public String getPageUrl(int pageId) {
        return "https://ru.wikipedia.org/w/index.php?curid=" + pageId;
    }
}