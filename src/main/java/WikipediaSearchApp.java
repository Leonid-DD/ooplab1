import java.util.Scanner;

public class WikipediaSearchApp {
    private WikipediaAPIV2 wikipediaAPI;
    private SearchResponseParser parser;
    private BrowserManager browserManager;
    private Scanner scanner;

    public WikipediaSearchApp() {
        this.wikipediaAPI = new WikipediaAPIV2();
        this.parser = new SearchResponseParser();
        this.browserManager = new BrowserManager();
        this.scanner = new Scanner(System.in, "UTF-8");
    }

    public void run() {
        System.out.println("=== Поиск по Википедии ===");
        System.out.println("Для выхода введите 'выход'");

        while (true) {
            try {
                System.out.print("\nВведите поисковый запрос: ");
                String query = scanner.nextLine().trim();

                if (query.equalsIgnoreCase("выход")) {
                    System.out.println("Программа завершена.");
                    break;
                }

                if (query.isEmpty()) {
                    System.out.println("Запрос не может быть пустым.");
                    continue;
                }

                System.out.println("Ищем: " + query + "...");

                // Выполняем поиск
                String jsonResponse = wikipediaAPI.search(query);
                SearchResult[] results = parser.parseSearchResponse(jsonResponse);

                if (results.length == 0) {
                    System.out.println("По вашему запросу ничего не найдено.");
                    System.out.println("Попробуйте изменить запрос или использовать другие ключевые слова.");
                    continue;
                }

                // Выводим результаты
                displaySearchResults(results);

                // Предлагаем выбрать статью
                if (selectAndOpenArticle(results)) {
                    break;
                }

            } catch (RuntimeException e) {
                System.out.println("Ошибка: " + e.getMessage());
                handleSpecificErrors(e);
            } catch (Exception e) {
                System.out.println("Неожиданная ошибка: " + e.getMessage());
                e.printStackTrace();
            }
        }

        scanner.close();
    }

    private void handleSpecificErrors(RuntimeException e) {
        String message = e.getMessage();
        if (message != null) {
            if (message.contains("403")) {
                System.out.println("Советы по решению проблемы 403:");
                System.out.println("1. Проверьте подключение к интернету");
                System.out.println("2. Возможно, требуется VPN");
                System.out.println("3. Подождите несколько минут и попробуйте снова");
            } else if (message.contains("429")) {
                System.out.println("Слишком много запросов. Подождите 1-2 минуты.");
            }
        }
    }

    private void displaySearchResults(SearchResult[] results) {
        System.out.println("\nНайдено результатов: " + results.length);
        System.out.println("==================");

        for (int i = 0; i < results.length; i++) {
            System.out.printf("%d. %s\n", i + 1, results[i]);
            if (i < results.length - 1) {
                System.out.println("---");
            }
        }
    }

    private boolean selectAndOpenArticle(SearchResult[] results) throws Exception {
        while (true) {
            System.out.print("\nВыберите номер статьи (1-" + results.length +
                    "), 'назад' для нового поиска или 'выход': ");
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

                    // Пробуем оба способа формирования URL
                    String pageUrl;
                    try {
                        pageUrl = wikipediaAPI.getPageUrl(selected.getTitle());
                    } catch (Exception e) {
                        pageUrl = wikipediaAPI.getPageUrl(selected.getPageId());
                    }

                    System.out.println("Открываю статью: " + selected.getTitle());
                    System.out.println("URL: " + pageUrl);

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