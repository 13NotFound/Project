package team.re4group1.Currency.views;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import team.re4group1.Permission.Permission;
import team.re4group1.Utils.Generated;

@Generated
public abstract class Page {
    protected Permission permission;
    public Page(Permission permission) {
        this.permission = permission;
    }

    public abstract Node generatePage(Pane root);

}
