import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.net.URI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("3 najnowsze aktualnosci z zsegw.pl");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 400);

            JEditorPane editorPane = new JEditorPane();
            editorPane.setContentType("text/html");
            editorPane.setEditable(false);
            editorPane.setBackground(Color.WHITE);

            JScrollPane scrollPane = new JScrollPane(editorPane);
            frame.add(scrollPane);

            new Thread(() -> {
                try {
                    String url = "https://zsegw.pl/";
                    Document doc = Jsoup.connect(url).get();
                    Elements posts = doc.select("div.elementskit-blog-block-post");

                    StringBuilder html = new StringBuilder();
                    html.append("<html><body style='font-family:Arial; font-size:14px;'>");
                    html.append("<h2>zrodlo: <a href='" + url + "'>zsegw.pl</a></h2><hr>");

                    int count = 0;
                    for (Element post : posts) {
                        if (count >= 3) break;
                        Element titleEl = post.selectFirst("h2.entry-title a");
                        if (titleEl != null) {
                            String title = titleEl.text();
                            String link = titleEl.absUrl("href");
                            html.append("<p><b>")
                                    .append(count + 1)
                                    .append(".</b> <a href='")
                                    .append(link)
                                    .append("'>")
                                    .append(title)
                                    .append("</a></p>");
                            count++;
                        }
                    }

                    if (count == 0) html.append("<p>nie znaleziono aktualnosci</p>");
                    html.append("</body></html>");

                    editorPane.setText(html.toString());
                    editorPane.setCaretPosition(0);

                } catch (Exception e) {
                    editorPane.setText("<html><body><b>Błąd:</b> " + e.getMessage() + "</body></html>");
                }
            }).start();

            // obsługa kliknięcia w link
            editorPane.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        try {
                            Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "nie udalo sie otworzyc linku: " + ex.getMessage());
                        }
                    }
                }
            });

            frame.setVisible(true);
        });
    }
}