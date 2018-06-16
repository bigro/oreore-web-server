import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Util {
    static final HashMap<String, String> contentTypeMap =
            new HashMap<String, String>() {
                {
                    put("html", "text/html");
                    put("htm", "text/html");
                    put("txt", "text/plain");
                    put("css", "text/css");
                    put("png", "image/png");
                    put("jpg", "image/jpeg");
                    put("jpeg", "image/jpeg");
                    put("gif", "image/gif");
                }
            };

    static void writeLine(OutputStream output, String line) throws IOException {
                for (char c : line.toCharArray()) {
                    output.write((int) c);
                }
                output.write((int) '\r');
                output.write((int) '\n');
            }

    static String readLine(InputStream input) throws IOException {
                int ch;
                String ret = "";
                while ((ch = input.read()) != -1) {
                    if (ch == '\r') {
                    } else if (ch == '\n') {
                        break;
                    } else {
                        ret += (char) ch;
                    }
                }
        
                if (ch == -1) {
                    return null;
                } else {
                    return ret;
                }
            }

    static String getDateStringUtc() {
                ZonedDateTime time = ZonedDateTime.now(ZoneId.of("GMT"));
                return time.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss"));
            }

    static String getContentType(String ext) {
        String contentType = contentTypeMap.get(ext.toLowerCase());
        if (contentType == null) {
            return "application/octet-stream";
        }
        return contentType;
    }
}
