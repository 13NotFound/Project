package team.re4group1.Permission.views;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import team.re4group1.CurrencyController;
import team.re4group1.Permission.LoginStatus;
import team.re4group1.Permission.Permission;
import team.re4group1.Utils.Generated;


public class Login {

    /**
     * init to Default Permission
     */
    public static Permission UserPermission = Permission.DEFAULT;
    public static Permission getPermission() {
        return UserPermission;
    }

    public static LoginStatus loginStatus = LoginStatus.LOGGED_OUT;

    public static void setPermission(Permission permission) {
        UserPermission = permission;
    }



    @Generated
    private void onLoginButtonActive(TextField usernameField, PasswordField passwordField, CurrencyController currencyController, Stage dialog, MenuItem menuAuth){
        String username = usernameField.getText();
        String passwd = passwordField.getText();
        if("admin".equals(username) && "admin".equals(passwd)){
            // todo refer to Database#Admin @Ethan
            System.out.println("login successful");
            dialog.close();
            // change current user to admin so that we can see admin page
            Login.setPermission(Permission.ADMIN);
            Login.loginStatus = LoginStatus.LOGGED_IN;
            menuAuth.setText("Logout");
            currencyController.initSpiltPane();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Username or Password is incorrect!");
            alert.showAndWait();
        }
    }

    @Generated
    public Node generatePage(CurrencyController currencyController, Stage dialog, MenuItem menuAuth) {
        VBox outerBox = new VBox();
        outerBox.setSpacing(30);
        outerBox.setPrefWidth(300);
        outerBox.setPrefHeight(200);
        outerBox.setAlignment(Pos.CENTER);

        Label usernameLabel = new Label("Username");
        usernameLabel.setPrefHeight(30);
        Label passwordLabel = new Label("Password");
        passwordLabel.setPrefHeight(30);
        TextField usernameField = new TextField();
        usernameField.setPrefHeight(30);
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefHeight(30);

        //grid layout
        GridPane gr = new GridPane();

        gr.add(usernameLabel,0,0);
        gr.add(usernameField,1,0);
        gr.add(passwordLabel,0,1);
        gr.add(passwordField,1,1);
        gr.setAlignment(Pos.CENTER);
        gr.setHgap(5);
        gr.setVgap(15);

        HBox buttonHBox = new HBox();
        buttonHBox.setSpacing(30);
        buttonHBox.setAlignment(Pos.CENTER);
        Button login = new Button("Login");
        Button reset = new Button("Reset");
        login.setOnAction(e-> onLoginButtonActive(usernameField, passwordField, currencyController, dialog, menuAuth));
        buttonHBox.getChildren().addAll(reset, login);
        outerBox.getChildren().addAll(gr, buttonHBox);
        return outerBox;
    }


}
