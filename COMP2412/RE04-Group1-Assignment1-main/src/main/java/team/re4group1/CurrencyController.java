package team.re4group1;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.kordamp.ikonli.javafx.FontIcon;
import team.re4group1.Permission.LoginStatus;
import team.re4group1.Permission.Permission;
import team.re4group1.Permission.views.Login;
import team.re4group1.Utils.Generated;
import team.re4group1.Utils.StyleUtil;
import team.re4group1.Currency.views.Factories.*;
import team.re4group1.Currency.views.PageRegister;
import java.net.URL;
import java.util.ResourceBundle;

@Generated
public class CurrencyController implements Initializable {
    public AnchorPane root;
    public AnchorPane splitPaneLeft;
    public AnchorPane splitPaneRight;
    private final PageRegister pageRegister = new PageRegister();
    public MenuItem menuAuth;
    private String currentPageName;

    public void initSpiltPane(){
        double buttonHeight = 40;
        VBox vBox = new VBox();
        vBox.setId("navi-left-button-vbox");

        for (String name : pageRegister.getPageKey(Login.getPermission())) {
            // todo Need to adapt to night mode
            FontIcon fontIcon = new FontIcon(pageRegister.getIconName(name));
//            MDL2IconFont fontIcon = new MDL2IconFont(pageRegister.getIconName(name));
            Button button = new Button(name, fontIcon);
            // set id for stylesheet
            button.setId("navi-button");
            button.prefWidthProperty().bind(splitPaneLeft.widthProperty());
            // fix button height
            button.setMinHeight(buttonHeight);
            // corresponding to css file currency/Currency.css
            button.getStyleClass().add("navi-button");
            vBox.getChildren().add(button);
            button.setOnMouseClicked(event -> {
                if (name.equals(this.currentPageName)){
                    return;
                }
                for (Node child : vBox.getChildren()) {
                    child.getStyleClass().remove("navi-button-on-focus");
                }
                button.getStyleClass().add("navi-button-on-focus");
                splitPaneRight.getChildren().forEach(StyleUtil::fadeOut);
                splitPaneRight.getChildren().clear();
                Node node = pageRegister.create(name, splitPaneRight);
                StyleUtil.fadeIn(node);
                this.currentPageName = name;
                splitPaneRight.getChildren().add(node);
            });
        }
        splitPaneLeft.getChildren().clear();
        splitPaneLeft.getChildren().add(vBox);
        // todo - need discussion @Gary Du
        // automatic choose first element, because we already ensure we will have
        // at least one child, so that not have to judge error
        Node firstElement = vBox.getChildren().get(0);
        firstElement.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
                firstElement.getLayoutX(), firstElement.getLayoutY(), firstElement.getLayoutX(), firstElement.getLayoutY(), MouseButton.PRIMARY, 1,
                true, true, true, true, true, true, true, true, true, true, null));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Icon cheatsheet https://kordamp.org/ikonli/cheat-sheet-fontawesome.html
        pageRegister.register("Currency", new OneToOneCurrencyPageFactory(), "fa-exchange");
        pageRegister.register("Popular", new PopularPageFactory(), "fa-table");
        pageRegister.register("History", new HistoryPageFactory(), "fa-history");
        pageRegister.register("Admin", new AdminPageFactory(), "fa-wrench");

        initSpiltPane();
    }

    public void changeScene(ActionEvent actionEvent) {

    }

    public void onMenuAuthClick(ActionEvent actionEvent) {
        if (Login.loginStatus == LoginStatus.LOGGED_OUT){
            Stage dialog = new Stage();
            dialog.setResizable(false);
            Login login = new Login();
            Node node = login.generatePage(this, dialog, menuAuth);
            Scene scene = new Scene((Parent) node);
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(root.getScene().getWindow());
            dialog.setScene(scene);
            dialog.setTitle("Login");
            dialog.show();
        } else if (Login.loginStatus == LoginStatus.LOGGED_IN) {
            menuAuth.setText("Login");
            Login.loginStatus = LoginStatus.LOGGED_OUT;
            Login.setPermission(Permission.DEFAULT);
            this.initSpiltPane();
        }
    }
}