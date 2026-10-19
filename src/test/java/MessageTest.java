package prog5121poe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
    @BeforeEach
    void resetCount() { Message.resetTotalMessagesForTests(); }

    @Test
    void messageOf250CharactersIsReady() {
        Message message = new Message(0, "+27718693002", "a".repeat(250), "1234567890");
        assertEquals("Message ready to send.", message.validateMessage());
    }

    @Test
    void messageOver250CharactersIsRejected() {
        Message message = new Message(0, "+27718693002", "a".repeat(255), "1234567890");
        assertEquals("Message exceeds 250 characters by 5; please reduce the size.", message.validateMessage());
    }

    @Test
    void recipientWithInternationalCodeIsAccepted() {
        Message message = new Message(0, "+27718693002", "Hi Mike, can you join us for dinner tonight?", "0012345678");
        assertEquals("Cell phone number successfully captured.", message.checkRecipientCell());
    }

    @Test
    void recipientWithoutInternationalCodeIsRejected() {
        Message message = new Message(0, "08575975889", "Hi Keegan, did you receive the payment?", "0012345678");
        assertTrue(message.checkRecipientCell().contains("incorrectly formatted"));
    }

    @Test
    void messageHashUsesFirstTwoIdDigitsNumberAndFirstLastWords() {
        Message message = new Message(0, "+27718693002", "Hi Mike, can you join us for dinner tonight?", "0012345678");
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    void generatedMessageIdContainsTenDigits() {
        Message message = new Message(1, "+27718693002", "Hello world", "1234567890");
        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageID().length());
    }

    @Test
    void automaticallyGeneratedMessageIdContainsTenDigits() {
        Message message = new Message(1, "+27718693002", "Hello world");
        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageID().length());
    }

    @Test
    void hashesForMultipleMessagesCanBeCheckedInALoop() {
        String[] texts = {"Hi Mike tonight", "Hello Keegan payment", "Please call tomorrow"};
        String[] expected = {"12:0:HITONIGHT", "34:1:HELLOPAYMENT", "56:2:PLEASETOMORROW"};
        String[] ids = {"1234567890", "3456789012", "5678901234"};
        for (int i = 0; i < texts.length; i++) {
            Message message = new Message(i, "+27718693002", texts[i], ids[i]);
            assertEquals(expected[i], message.createMessageHash());
        }
    }

    @Test
    void sendDiscardAndStoreReturnRequiredMessages() {
        Message send = new Message(0, "+27718693002", "Hi Mike tonight", "1234567890");
        assertEquals("Message successfully sent.", send.SentMessage("1"));
        assertEquals(1, send.returnTotalMessagess());

        Message discard = new Message(1, "+27718693002", "Hi Keegan payment", "1234567891");
        assertEquals("Press O to delete the message.", discard.SentMessage("2"));

        Message store = new Message(2, "+27718693002", "Save this message", "1234567892");
        assertEquals("Message successfully stored.", store.SentMessage("3"));
    }

    @Test
    void invalidMessageCannotBeSent() {
        Message message = new Message(0, "+27718693002", "x".repeat(251), "1234567890");
        assertTrue(message.SentMessage("1").startsWith("Message could not be sent"));
        assertEquals(0, message.returnTotalMessagess());
    }

    @Test
    void storeMessageWritesJson() throws Exception {
        Path file = Files.createTempFile("quickchat-test", ".json");
        try {
            Message message = new Message(0, "+27718693002", "Hi \"Mike\" tonight", "1234567890");
            MessageStore.storeMessage(message, file);
            String json = Files.readString(file);
            assertTrue(json.startsWith("["));
            assertTrue(json.contains("\\\"Mike\\\""));
            assertTrue(json.contains("\"messageID\""));
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
