package prog5121poe;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Path MESSAGE_FILE = Path.of("messages.json");

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("Welcome to QuickChat.");
            System.out.print("Enter First Name: ");
            String firstName = input.nextLine().trim();
            System.out.print("Enter Last Name: ");
            String lastName = input.nextLine().trim();
            System.out.print("Create Username: ");
            String username = input.nextLine().trim();
            System.out.print("Create Password: ");
            String password = input.nextLine();
            System.out.print("Enter Cellphone (+27XXXXXXXXX): ");
            String phone = input.nextLine().trim();

            Login login = new Login(firstName, lastName, username, password, phone);
            String registration = login.registerUser();
            System.out.println(registration);
            if (!registration.equals("User has been registered successfully.")) {
                System.out.println("Please restart and register with valid details.");
                return;
            }

            System.out.println("\nLOGIN");
            System.out.print("Username: ");
            String loginUsername = input.nextLine().trim();
            System.out.print("Password: ");
            String loginPassword = input.nextLine();
            System.out.println(login.returnLoginStatus(loginUsername, loginPassword));
            if (!login.loginUser(loginUsername, loginPassword)) return;

            System.out.print("How many messages would you like to enter? ");
            int messageLimit = readNonNegativeInt(input);
            List<Message> sentMessages = new ArrayList<>();
            int messagesCreated = 0;
            boolean running = true;
            while (running) {
                System.out.println("\n1) Send Messages\n2) Show recently sent messages\n3) Quit");
                System.out.print("Choose an option: ");
                String option = input.nextLine().trim();
                switch (option) {
                    case "1" -> {
                        if (messagesCreated >= messageLimit) {
                            System.out.println("You have reached the number of messages you selected.");
                            break;
                        }
                        System.out.print("Enter recipient cell number (+27 followed by 9 digits): ");
                        String recipient = input.nextLine().trim();
                        System.out.print("Enter your message (maximum 250 characters): ");
                        String text = input.nextLine();
                        Message message = new Message(messagesCreated, recipient, text);
                        System.out.println("Message ID generated: " + message.getMessageID());
                        System.out.println(message.checkRecipientCell());
                        System.out.println(message.validateMessage());
                        if (!message.isRecipientValid() || text.length() > 250) break;

                        System.out.println("\n1) Send Message\n2) Disregard Message\n3) Store Message to send later");
                        System.out.print("Choose an option: ");
                        String action = input.nextLine().trim();
                        String result = message.SentMessage(action);
                        System.out.println(result);
                        if (action.equals("1") || action.equalsIgnoreCase("send") || action.equalsIgnoreCase("send message")) {
                            if (message.isSent()) {
                                sentMessages.add(message);
                                System.out.println(message.printMessage());
                            }
                        } else if (action.equals("2") || action.equalsIgnoreCase("discard") || action.equalsIgnoreCase("disregard message")) {
                            System.out.print("Enter O to delete the message: ");
                            input.nextLine();
                        } else if (action.equals("3") || action.equalsIgnoreCase("store") || action.equalsIgnoreCase("store message")) {
                            try {
                                MessageStore.storeMessage(message, MESSAGE_FILE);
                                System.out.println("Message stored in " + MESSAGE_FILE.toAbsolutePath());
                            } catch (IOException ex) {
                                System.out.println("Could not store message: " + ex.getMessage());
                            }
                        }
                        messagesCreated++;
                    }
                    case "2" -> System.out.println("Coming Soon.");
                    case "3" -> running = false;
                    default -> System.out.println("Invalid option. Please choose 1, 2 or 3.");
                }
            }
            System.out.println("Total messages sent: " + Message.getTotalMessagesSent());
            System.out.println("Thank you for using QuickChat.");
        }
    }

    private static int readNonNegativeInt(Scanner input) {
        while (true) {
            String value = input.nextLine().trim();
            try {
                int count = Integer.parseInt(value);
                if (count >= 0) return count;
            } catch (NumberFormatException ignored) { }
            System.out.print("Please enter a whole number of 0 or more: ");
        }
    }
}
