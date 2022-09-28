package team.re4group1.Permission;

public enum Permission {
    ADMIN(999, "Admin"), DEFAULT(0, "DEFAULT");

    private final int permissionValue;
    private final String permissionName;

    Permission(int permissionValue, String name) {
        this.permissionValue = permissionValue;
        this.permissionName = name;
    }

    public static boolean comparePermission(Permission currentUserPer, Permission targetPage){
        if (currentUserPer == null || targetPage == null){
            return false;
        }
        return currentUserPer.permissionValue >= targetPage.permissionValue;
    }

    public String getPermissionName() {
        return permissionName;
    }
}
