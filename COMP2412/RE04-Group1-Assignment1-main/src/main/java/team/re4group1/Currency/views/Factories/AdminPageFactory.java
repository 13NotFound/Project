package team.re4group1.Currency.views.Factories;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import team.re4group1.Permission.Permission;
import team.re4group1.Currency.views.Pages.AdminPage;
import team.re4group1.Utils.Generated;

@Generated
public class AdminPageFactory implements PageFactory {
    private static final Permission permission = Permission.ADMIN;
    
        /**
     * This function revokes the application page according to user's permission
     * @param root
     * @return
     */
    @Override
    public Node createPage(Pane root) {
        return new AdminPage(permission).generatePage(root);
    }

    @Override
    public Permission getPermission() {
        return permission;
    }
}
