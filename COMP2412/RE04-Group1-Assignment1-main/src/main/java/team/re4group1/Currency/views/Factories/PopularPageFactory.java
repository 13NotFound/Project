package team.re4group1.Currency.views.Factories;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import team.re4group1.Permission.Permission;
import team.re4group1.Currency.views.Pages.PopularPage;
import team.re4group1.Utils.Generated;

@Generated
public class PopularPageFactory implements PageFactory {
    private static final Permission permission = Permission.DEFAULT;
    @Override
    public Node createPage(Pane root) {
        return new PopularPage(Permission.DEFAULT).generatePage(root);
    }

    @Override
    public Permission getPermission() {
        return permission;
    }
}
