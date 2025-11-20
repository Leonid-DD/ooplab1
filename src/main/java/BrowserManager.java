import java.awt.Desktop;
import java.net.URI;

public class BrowserManager {
    public void openInBrowser(String url) throws Exception {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(url));
            } else {
                System.out.println("Браузер не поддерживается на этой платформе");
            }
        } else {
            System.out.println("Desktop не поддерживается на этой платформе");
        }
    }
}