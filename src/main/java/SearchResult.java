import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SearchResult {
    private int pageId;
    private String title;
    private String snippet;

    public SearchResult(int pageId, String title, String snippet) {
        this.pageId = pageId;
        this.title = title;
        this.snippet = snippet;
    }

    // Геттеры
    public int getPageId() { return pageId; }
    public String getTitle() { return title; }
    public String getSnippet() { return snippet; }

    @Override
    public String toString() {
        return String.format("%s (ID: %d)\n%s\n", title, pageId, snippet.replaceAll("<[^>]*>", ""));
    }
}

class SearchResponseParser {
    public SearchResult[] parseSearchResponse(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject query = jsonObject.getAsJsonObject("query");
        JsonArray searchResults = query.getAsJsonArray("search");

        SearchResult[] results = new SearchResult[searchResults.size()];

        for (int i = 0; i < searchResults.size(); i++) {
            JsonObject result = searchResults.get(i).getAsJsonObject();
            int pageId = result.get("pageid").getAsInt();
            String title = result.get("title").getAsString();
            String snippet = result.get("snippet").getAsString();

            results[i] = new SearchResult(pageId, title, snippet);
        }

        return results;
    }
}