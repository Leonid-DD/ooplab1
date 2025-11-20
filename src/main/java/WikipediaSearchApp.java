import java.util.Scanner;

public class WikipediaSearchApp {
    private WikipediaAPI wikipediaAPI;
    private SearchResponseParser parser;
    private BrowserManager browserManager;
    private Scanner scanner;

    public WikipediaSearchApp() {
        this.wikipediaAPI = new WikipediaAPI();
        this.parser = new SearchResponseParser();
        this.browserManager = new BrowserManager();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("=== Поиск по Википедии ===");

        while (true) {
            try {
                System.out.print("\nВведите поисковый запрос (или 'выход' для завершения): ");
                String query = scanner.nextLine().trim();

                if (query.equalsIgnoreCase("выход")) {
                    System.out.println("Программа завершена.");
                    break;
                }

                if (query.isEmpty()) {
                    System.out.println("Запрос не может быть пустым.");
                    continue;
                }

                // Выполняем поиск
                String jsonResponse = wikipediaAPI.search(query);
                SearchResult[] results = parser.parseSearchResponse(jsonResponse);

                if (results.length == 0) {
                    System.out.println("По вашему запросу ничего не найдено.");
                    continue;
                }

                // Выводим результаты
                displaySearchResults(results);

                // Предлагаем выбрать статью
                if (selectAndOpenArticle(results)) {
                    break; // Выход после открытия статьи
                }

            } catch (Exception e) {
                System.out.println("Произошла ошибка: " + e.getMessage());
                System.out.println("Попробуйте еще раз.");
            }
        }

        scanner.close();
    }

    private void displaySearchResults(SearchResult[] results) {
        System.out.println("\nРезультаты поиска:");
        System.out.println("==================");

        for (int i = 0; i < results.length; i++) {
            System.out.printf("%d. %s\n", i + 1, results[i]);
            System.out.println("---");
        }
    }

    private boolean selectAndOpenArticle(SearchResult[] results) throws Exception {
        while (true) {
            System.out.print("Выберите номер статьи для открытия (1-" + results.length +
                    "), 'назад' для нового поиска или 'выход' для завершения: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("выход")) {
                System.out.println("Программа завершена.");
                return true;
            }

            if (input.equalsIgnoreCase("назад")) {
                return false;
            }

            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= results.length) {
                    SearchResult selected = results[choice - 1];
                    String pageUrl = wikipediaAPI.getPageUrl(selected.getPageId());

                    System.out.println("Открываю статью: " + selected.getTitle());
                    browserManager.openInBrowser(pageUrl);

                    System.out.print("Хотите выполнить новый поиск? (да/нет): ");
                    String answer = scanner.nextLine().trim();
                    return answer.equalsIgnoreCase("нет");
                } else {
                    System.out.println("Пожалуйста, введите число от 1 до " + results.length);
                }
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите корректный номер или команду");
            }
        }
    }

    public static void main(String[] args) {
        WikipediaSearchApp app = new WikipediaSearchApp();
        app.run();
    }
}