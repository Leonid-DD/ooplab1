import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SearchResponseParser {
    public SearchResult[] parseSearchResponse(String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

            if (jsonObject.has("error")) {
                JsonObject error = jsonObject.getAsJsonObject("error");
                String errorInfo = error.get("info").getAsString();
                throw new RuntimeException("API Error: " + errorInfo);
            }

            JsonObject query = jsonObject.getAsJsonObject("query");

            if (query == null || !query.has("search")) {
                return new SearchResult[0];
            }

            JsonArray searchResults = query.getAsJsonArray("search");

            if (searchResults.size() == 0) {
                return new SearchResult[0];
            }

            SearchResult[] results = new SearchResult[searchResults.size()];

            for (int i = 0; i < searchResults.size(); i++) {
                JsonObject result = searchResults.get(i).getAsJsonObject();
                int pageId = result.get("pageid").getAsInt();
                String title = result.get("title").getAsString();
                String snippet = result.get("snippet").getAsString();

                results[i] = new SearchResult(pageId, title, snippet);
            }

            return results;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON response: " + e.getMessage(), e);
        }
    }
}
