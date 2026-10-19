package prog5121poe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/** Stores messages in a JSON file without requiring an additional library. */
public final class MessageStore {
    private MessageStore() { }

    public static void storeMessage(Message message, Path file) throws IOException {
        String existing = Files.exists(file) ? Files.readString(file, StandardCharsets.UTF_8).trim() : "";
        String item = "  {\n"
                + "    \"messageID\": \"" + escape(message.getMessageID()) + "\",\n"
                + "    \"messageHash\": \"" + escape(message.getMessageHash()) + "\",\n"
                + "    \"messageNumber\": " + message.getMessageNumber() + ",\n"
                + "    \"recipient\": \"" + escape(message.getRecipient()) + "\",\n"
                + "    \"message\": \"" + escape(message.getMessageText()) + "\"\n"
                + "  }";
        String output;
        if (existing.isEmpty() || existing.equals("[]")) {
            output = "[\n" + item + "\n]\n";
        } else if (existing.endsWith("]")) {
            String body = existing.substring(0, existing.length() - 1).stripTrailing();
            output = body + ",\n" + item + "\n]\n";
        } else {
            throw new IOException("The JSON file is not a valid message array.");
        }
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        Path temp = Files.createTempFile(parent, "quickchat-", ".json.tmp");
        try {
            Files.writeString(temp, output, StandardCharsets.UTF_8);
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException ex) {
            Files.deleteIfExists(temp);
            throw ex;
        }
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\b", "\\b").replace("\f", "\\f").replace("\n", "\\n")
                .replace("\r", "\\r").replace("\t", "\\t");
    }
}
