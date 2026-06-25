import database.DatabaseConnection;
import database.ListenerAccount;
import database.NotEnoughCreditsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ListenerAccountTest {
    //3a
    @Test
    public void testRegister() throws Exception {
        int id = ListenerAccount.Persistence.register("User", "Password");
        assertTrue(id > 0);

    }
    //3b
    @Test
    public void testLogin() throws Exception {
        int id = ListenerAccount.Persistence.register("loginUser", "password123");
        ListenerAccount account = ListenerAccount.Persistence.authenticate("loginUser", "password123");
        assertEquals("loginUser", account.getUsername());
    }
    //3c
    @Test
    public void testEmptyCredits() throws Exception {
        ListenerAccount.Persistence.register("user1", "pass1");
        ListenerAccount account = ListenerAccount.Persistence.authenticate("user1", "pass1");
        assertEquals(0, account.getCredits());
    }
    @Test
    public void testAddCredits() throws Exception {
        ListenerAccount.Persistence.register("user2", "pass2");
        ListenerAccount account = ListenerAccount.Persistence.authenticate("user2", "pass2");
        account.addCredits(4);
        assertEquals(4, account.getCredits());
    }
    //3d
    @Test
    public void testBuyOwnedSong() throws Exception {
        ListenerAccount.Persistence.init();
        ListenerAccount.Persistence.register("user12", "pass12");
        ListenerAccount account = ListenerAccount.Persistence.authenticate("user12", "pass12");
        account.addCredits(5);
        ListenerAccount.Persistence.addSong(account.getId(), 1);
        account.buySong(1);
        assertEquals(5, account.getCredits());
    }
    @Test
    public void testBuySong() throws Exception {
        ListenerAccount.Persistence.init();
        ListenerAccount.Persistence.register("user123", "pass123");
        ListenerAccount account = ListenerAccount.Persistence.authenticate("user123", "pass123");
        account.addCredits(1);
        account.buySong(1);
        assertEquals(0, account.getCredits());
    }
    @Test
    public void testBuySongWithout() throws Exception {
        ListenerAccount.Persistence.init();
        ListenerAccount.Persistence.register("user1234", "pass1234");
        ListenerAccount account = ListenerAccount.Persistence.authenticate("user1234", "pass1234");
        assertThrows(NotEnoughCreditsException.class, () -> account.buySong(1));
    }
    //inne
    @BeforeEach
    void connect() throws SQLException {
        DatabaseConnection.connect("temp.db");
        ListenerAccount.Persistence.init();
    }

    @AfterEach
    void disconnect() {
        DatabaseConnection.disconnect();
        new java.io.File("temp.db").delete();
    }
}
