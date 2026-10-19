package prog5121poe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    @Test
    public void testCorrectUsername() {
        Login login = new Login("Kyle","Smith","kyl_1","Ch&sec@ke99!","+27838968976");
        assertTrue(login.checkUserName());
    }

    @Test
    public void testIncorrectUsername() {
        Login login = new Login("Kyle","Smith","kyle!!!!!!","Ch&sec@ke99!","+27838968976");
        assertFalse(login.checkUserName());
    }

    @Test
    public void testCorrectPassword() {
        Login login = new Login("Kyle","Smith","kyl_1","Ch&sec@ke99!","+27838968976");
        assertTrue(login.checkPasswordComplexity());
    }

    @Test
    public void testIncorrectPassword() {
        Login login = new Login("Kyle","Smith","kyl_1","password","+27838968976");
        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    public void testCorrectCellNumber() {
        Login login = new Login("Kyle","Smith","kyl_1","Ch&sec@ke99!","+27838968976");
        assertTrue(login.checkCellPhoneNumber());
    }

    @Test
    public void testIncorrectCellNumber() {
        Login login = new Login("Kyle","Smith","kyl_1","Ch&sec@ke99!","08966553");
        assertFalse(login.checkCellPhoneNumber());
    }

    @Test
    public void testSuccessfulLogin() {
        Login login = new Login("Kyle","Smith","kyl_1","Ch&sec@ke99!","+27838968976");
        assertTrue(login.loginUser("kyl_1","Ch&sec@ke99!"));
    }

    @Test
    public void testFailedLogin() {
        Login login = new Login("Kyle","Smith","kyl_1","Ch&sec@ke99!","+27838968976");
        assertFalse(login.loginUser("wrong","wrong"));
    }
}