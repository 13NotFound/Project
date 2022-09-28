package team.re4group1.Permission.views;

import org.junit.jupiter.api.Test;
import team.re4group1.Permission.Permission;
import static org.junit.jupiter.api.Assertions.*;

public class TestLogin {
    @Test
    void TestLoginPage() {
        assertNotNull(new Login());
        Login.setPermission(Permission.ADMIN);
        assertEquals(Permission.ADMIN, Login.getPermission());
    }
}
