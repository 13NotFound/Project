package team.re4group1.Permission;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestPermission {

    @Test
    void TestPermissionClass(){
        assertTrue(Permission.comparePermission(Permission.ADMIN, Permission.DEFAULT));
        assertTrue(Permission.comparePermission(Permission.ADMIN, Permission.ADMIN));
        assertFalse(Permission.comparePermission( Permission.DEFAULT, Permission.ADMIN));
        assertFalse(Permission.comparePermission(null, null));
        assertFalse(Permission.comparePermission(Permission.DEFAULT, null));
        assertFalse(Permission.comparePermission(null, Permission.DEFAULT));
    }

    @Test
    void TestGetPermissionName(){
        assertEquals("Admin", Permission.ADMIN.getPermissionName());
    }

    @Test
    void TestLoginStatus(){
        assertNotNull(LoginStatus.LOGGED_IN);
        assertNotNull(LoginStatus.LOGGED_OUT);
    }

}
