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

    public int getPageId() { return pageId; }
    public String getTitle() { return title; }
    public String getSnippet() { return snippet; }

    @Override
    public String toString() {
        return String.format("%s (ID: %d)\n%s\n", title, pageId, snippet.replaceAll("<[^>]*>", ""));
    }
}

