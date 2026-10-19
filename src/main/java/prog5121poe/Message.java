package prog5121poe;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.regex.Pattern;

/** Represents one QuickChat message. */
public class Message {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static int totalMessagesSent = 0;

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;
    private boolean sent;

    public Message(int messageNumber, String recipient, String messageText) {
        this(messageNumber, recipient, messageText, generateMessageID());
    }

    // Useful for predictable unit tests and for checking the required hash format.
    public Message(int messageNumber, String recipient, String messageText, String messageID) {
        this.messageNumber = messageNumber;
        this.recipient = recipient == null ? "" : recipient.trim();
        this.messageText = messageText == null ? "" : messageText.trim();
        this.messageID = messageID;
        this.messageHash = createMessageHash();
    }

    private static String generateMessageID() {
        long number = 1_000_000_000L + (long) (RANDOM.nextDouble() * 9_000_000_000L);
        return Long.toString(number);
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.matches("\\d{10}");
    }

    public String checkRecipientCell() {
        // South African international format: +27 followed by nine digits.
        if (Pattern.matches("^\\+27\\d{9}$", recipient)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    public boolean isRecipientValid() {
        return checkRecipientCell().equals("Cell phone number successfully captured.");
    }

    public String validateMessage() {
        if (messageText.length() <= 250) return "Message ready to send.";
        return "Message exceeds 250 characters by " + (messageText.length() - 250) + "; please reduce the size.";
    }

    public String createMessageHash() {
        String firstTwoDigits = messageID != null && messageID.length() >= 2
                ? messageID.substring(0, 2) : "00";
        String[] words = messageText.trim().isEmpty() ? new String[0] : messageText.trim().split("\\s+");
        String firstWord = words.length == 0 ? "" : words[0].replaceAll("[^\\p{L}\\p{N}]", "");
        String lastWord = words.length == 0 ? "" : words[words.length - 1].replaceAll("[^\\p{L}\\p{N}]", "");
        return (firstTwoDigits + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase(Locale.ROOT);
    }

    /** Processes SEND, DISCARD or STORE and returns the required status message. */
    public String SentMessage(String choice) {
        if (choice == null) return "Invalid choice. Please choose Send, Discard or Store.";
        switch (choice.trim().toUpperCase(Locale.ROOT)) {
            case "SEND", "SEND MESSAGE", "1" -> {
                if (!checkMessageID() || !isRecipientValid() || messageText.length() > 250) {
                    return "Message could not be sent. Please check the message ID, recipient number and message length.";
                }
                if (!sent) {
                    sent = true;
                    totalMessagesSent++;
                }
                return "Message successfully sent.";
            }
            case "DISCARD", "DISREGARD MESSAGE", "2" -> {
                sent = false;
                return "Press O to delete the message.";
            }
            case "STORE", "STORE MESSAGE", "3" -> {
                return "Message successfully stored.";
            }
            default -> { return "Invalid choice. Please choose Send, Discard or Store."; }
        }
    }

    public String printMessage() {
        return "Message ID: " + messageID + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient + "\nMessage: " + messageText;
    }

    public String printMessages() { return printMessage(); }
    public int returnTotalMessagess() { return totalMessagesSent; }
    public int returnTotalMessages() { return totalMessagesSent; }
    public static int getTotalMessagesSent() { return totalMessagesSent; }
    public static void resetTotalMessagesForTests() { totalMessagesSent = 0; }

    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public String getMessageHash() { return messageHash; }
    public boolean isSent() { return sent; }
}
