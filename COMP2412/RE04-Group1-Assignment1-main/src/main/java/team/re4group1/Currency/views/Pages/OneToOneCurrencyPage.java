package team.re4group1.Currency.views.Pages;

import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import team.re4group1.Database.Database;
import team.re4group1.Permission.Permission;
import team.re4group1.Currency.views.Page;
import team.re4group1.Utils.Generated;

import java.util.ArrayList;

@Generated
public class OneToOneCurrencyPage extends Page {

    public OneToOneCurrencyPage(Permission permission) {
        super(permission);
    }

    private JsonObject currentRateJson = Database.getDb().getLatestExchangeRate();

    private ArrayList<String> currencyList = Database.getDb().getCurrentCurrencyList();
    private final Text choiceTitleText = new Text();
    private final Text targetTitleText = new Text();

    private final TextField choiceTextField = new TextField();

    private final ChoiceBox<String> choiceChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
            currencyList
    ));
    private final TextField targetTextField = new TextField();
    private final ChoiceBox<String> targetChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
            currencyList
    ));


    /**
     * Limit user input, only allow user input number into text field or clear text field
     * @param tf TextField
     */
    public static void addNumberLimiter(final TextField tf) {
        tf.setTextFormatter(new TextFormatter<String>(t -> t.getText().matches("[0-9]+|")? t: null));
    }

    private void textFieldTitleTextUpdate(){
        choiceTitleText.setText(String.format("%s %s is equal to", choiceTextField.getText(), choiceChoiceBox.getSelectionModel().selectedItemProperty().get()));
        targetTitleText.setText(String.format("%s %s", targetTextField.getText(), targetChoiceBox.getSelectionModel().selectedItemProperty().get()));
    }

    private double calculateCurrencyFromTo(String from, String to){
        if (from.equals("") || to.equals(""))
            return 0;
        for (Object moneyList : currentRateJson.get("moneyList").getAsJsonArray()) {
            if (((JsonObject) moneyList).get("symbol").getAsString().equals(from)){
                if (from.equals(to)){
                    return 1;
                }
                return ((JsonObject) moneyList).get("exchangeRate").getAsJsonObject().get(to).getAsDouble();
            }
        }
        return 0;
    }

    @Generated
    private String calculateCurrency(String newValue){
        double newValueDouble;
        if (!newValue.equals("")) {
            newValueDouble = Double.parseDouble(newValue) * calculateCurrencyFromTo(choiceChoiceBox.getValue(), targetChoiceBox.getValue());
        } else {
            newValueDouble = 0;
        }
        return String.format("%.2f",newValueDouble);

    }

    @Generated
    public void currencyTextFieldRecalculate() {
        // todo when change any one of TextField, the other TextField should be recalculated
        choiceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // todo calculate
            targetTextField.textProperty().setValue(calculateCurrency(newValue));
            textFieldTitleTextUpdate();
        });
    }

    @Generated
    private void choiceBoxTitleTextUpdate(){
        if (choiceTextField.textProperty().get().equals("") || targetTextField.textProperty().get().equals("")){
            return;
        }
        choiceTitleText.setText(String.format("%s %s is equal to", choiceTextField.textProperty().get(), choiceChoiceBox.getSelectionModel().selectedItemProperty().get()));
        targetTitleText.setText(String.format("%s %s", targetTextField.textProperty().get(), targetChoiceBox.getSelectionModel().selectedItemProperty().get()));
    }

    @Generated
    public void currencyChoiceBoxRecalculate() {
        // todo when change any one of ChoiceBox, the other TextField should be recalculated
        choiceChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // todo calculate
            targetTextField.textProperty().setValue(calculateCurrency(choiceTextField.getText()));
            choiceBoxTitleTextUpdate();

        });
        targetChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            targetTextField.textProperty().setValue(calculateCurrency(choiceTextField.getText()));
            // todo calculate
            choiceBoxTitleTextUpdate();
        });
    }

    @Override
    @Generated
    public Node generatePage(Pane root) {
        // vbox to center the content
        VBox vbox = new VBox();
        vbox.setId("oneToOneCurrency-container-vbox");
        vbox.prefHeightProperty().bind(root.heightProperty());
        vbox.prefWidthProperty().bind(root.widthProperty());
        vbox.setFillWidth(false);
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setSpacing(10);
        vbox.getChildren().addAll(this.choiceTitleText, this.targetTitleText);

        // set property for choice TextField
        HBox choiceBox = new HBox();
        choiceTextField.setPrefHeight(40);
        choiceTextField.setPrefWidth(150);
        choiceTextField.setId("oneToOneCurrency-textField");
        choiceTextField.setTextFormatter(new TextFormatter<>(t -> {

            if (t.isReplaced())
                if(t.getText().matches("[^0-9]"))
                    t.setText(t.getControlText().substring(t.getRangeStart(), t.getRangeEnd()));


            if (t.isAdded()) {
                if (t.getControlText().contains(".")) {
                    if (t.getText().matches("[^0-9]")) {
                        t.setText("");
                    }
                } else if (t.getText().matches("[^0-9.]")) {
                    t.setText("");
                }
            }

            return t;
        }));
        choiceBox.getChildren().add(choiceTextField);

        // origin money type select box
        choiceChoiceBox.setPrefWidth(100);
        choiceChoiceBox.setPrefHeight(40);
        choiceChoiceBox.setId("oneToOneCurrency-choice");
        choiceBox.getChildren().add(choiceChoiceBox);
        vbox.getChildren().add(choiceBox);

        // exchange icon
        FontIcon fontIcon = new FontIcon("fa-exchange");
        HBox iconPane = new HBox(fontIcon);
        iconPane.prefWidthProperty().bind(choiceBox.widthProperty());
        iconPane.setAlignment(Pos.CENTER);


        vbox.getChildren().add(iconPane);

        HBox targetBox = new HBox();
        targetTextField.setPrefHeight(40);
        targetTextField.setPrefWidth(150);
        targetTextField.setId("oneToOneCurrency-textField");
        targetTextField.setEditable(false);
        targetTextField.setMouseTransparent(true);
        targetTextField.setFocusTraversable(false);
        targetBox.getChildren().add(targetTextField);

        // target money type select box

        targetChoiceBox.setPrefWidth(100);
        targetChoiceBox.setPrefHeight(40);
        targetChoiceBox.setId("oneToOneCurrency-choice");
        targetBox.getChildren().add(targetChoiceBox);

        vbox.getChildren().add(targetBox);

        // add event listener
        this.currencyTextFieldRecalculate();
        this.currencyChoiceBoxRecalculate();

        // set default value
        // todo exception handle
        targetChoiceBox.setValue(targetChoiceBox.getItems().get(0));
        // todo exception handle
        choiceChoiceBox.setValue(choiceChoiceBox.getItems().get(0));
        choiceTextField.textProperty().setValue("1");
        return vbox;
    }
}
