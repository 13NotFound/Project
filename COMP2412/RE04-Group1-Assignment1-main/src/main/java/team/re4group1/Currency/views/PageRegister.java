package team.re4group1.Currency.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import team.re4group1.Currency.views.Factories.PageFactory;
import team.re4group1.Permission.Permission;
import team.re4group1.Utils.Generated;

@Generated
public class PageRegister {
    /**
     * use to store page name and factory method
     */
    private final Map<String, PageFactory> factories = new HashMap<>();
    /**
     * store page icon
     */
    private final Map<String, String> icons = new HashMap<>();
    /**
     * store order of the page
     */
    private final ArrayList<String> keyOrder = new ArrayList<>();

    public void register(String key, PageFactory pageFactory, String icon) {
        if (!factories.containsKey(key)) {
            factories.put(key, pageFactory);
            icons.put(key, icon);
            keyOrder.add(key);
        }
    }

    public ArrayList<String> getPageKey(Permission userPermission) {
        ArrayList<String> display = new ArrayList<>();
        for (String s : this.keyOrder) {
            if (Permission.comparePermission(userPermission, factories.get(s).getPermission())){
                display.add(s);
            }
        }
        return display;
    }

    public Node create(String key, Pane root) {
        //get corresponding factory using the identifier key
        PageFactory factory = factories.get(key);
        return factory.createPage(root);
    }

    public String getIconName(String key) {
        return icons.get(key);
    }
}
