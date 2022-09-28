package team.re4group1.Currency.views.Pages;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import team.re4group1.Database.Database;
import team.re4group1.Permission.Permission;
import team.re4group1.Currency.views.Page;
import team.re4group1.Utils.Generated;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Generated
public class AdminPage extends Page {

    private int selectedCounter = 0;

    public AdminPage(Permission permission) {
        super(permission);
    }


    public static class NewCurrencySheet{
        private final SimpleStringProperty existCurrency;
        private final SimpleDoubleProperty newValue;

        public String getExistCurrency() {
            return existCurrency.get();
        }

        public SimpleStringProperty existCurrencyProperty() {
            return existCurrency;
        }

        public void setExistCurrency(String existCurrency) {
            this.existCurrency.set(existCurrency);
        }

        public double getNewValue() {
            return newValue.get();
        }

        public SimpleDoubleProperty newValueProperty() {
            return newValue;
        }

        public void setNewValue(double newValue) {
            this.newValue.set(newValue);
        }

        public NewCurrencySheet(String existCurrency, Double newValue) {
            this.existCurrency = new SimpleStringProperty(existCurrency);
            this.newValue = new SimpleDoubleProperty(newValue);
        }
    }

    private void setupNewCurrencyButton(Button button, Pane root){
        button.setOnAction(event -> {
            HBox hBox = new HBox();
            hBox.setPadding(new Insets(5));
            hBox.setSpacing(10);
            hBox.setAlignment(Pos.CENTER);
            hBox.setPrefHeight(400);
            hBox.setPrefWidth(600);
            Stage dialog = new Stage();
            dialog.setResizable(false);
            Scene scene = new Scene(hBox);
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(root.getScene().getWindow());
            dialog.setScene(scene);
            dialog.setTitle("Add new");


            Label inputNewDate = new Label("Input new Date");
            DatePicker datePicker = new DatePicker();
            datePicker.setPrefHeight(40);
            datePicker.setMinWidth(200);
            datePicker.setConverter(new StringConverter<>() {
                private static final String DATE_PATTERN = "dd/MM/yyyy";

                /**
                 * The date formatter.
                 */
                public static final DateTimeFormatter DATE_FORMATTER =
                        DateTimeFormatter.ofPattern(DATE_PATTERN);

                @Override
                public String toString(LocalDate localDate) {
                    try {
                        return DATE_FORMATTER.format(localDate);
                    } catch (RuntimeException e) {
                        return null;
                    }
                }

                @Override
                public LocalDate fromString(String formattedString) {

                    try {
                        return LocalDate.from(DATE_FORMATTER.parse(formattedString));
                    } catch (RuntimeException parseExc) {
                        return null;
                    }
                }
            });
            Label inputCurrencyName = new Label("Input Currency Name(Only Allow 3 Uppercase character)");
            inputCurrencyName.setWrapText(true);
            TextField textField = new TextField();
            textField.setMaxWidth(200);
            textField.setTextFormatter(new TextFormatter<String>((TextFormatter.Change change) -> {
                String newText = change.getControlNewText();
                if (!newText.matches("[A-Z]+|") || newText.length() > 3) {
                    return null ;
                } else {
                    return change ;
                }
            }));
            textField.setPrefHeight(40);
            TableView<NewCurrencySheet> table = new TableView<>();
            ObservableList<NewCurrencySheet> data = FXCollections.observableArrayList();
            for (String s : Database.getDb().getCurrentCurrencyList()) {
                data.add(new NewCurrencySheet(s, 1.));
            }

            Button submit = new Button("Submit");
            submit.setOnAction(submitAction -> {
                if (textField.getText().length() != 3 || datePicker.getValue()==null){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Attention");
                    alert.setHeaderText("The data you entered does not meet the requirements :(");
                    alert.setContentText("Please select a date and enter three capital letters currency symbol.");
                    alert.showAndWait();
                    return;
                }
                HashMap<String, Double> stringDoubleHashMap = new HashMap<>();
                for (NewCurrencySheet datum : data) {
                    stringDoubleHashMap.put(datum.getExistCurrency(), datum.getNewValue());
                }
                Database.getDb().addOneCurrencyRate(datePicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        textField.getText(), stringDoubleHashMap);
                dialog.close();
            });
            VBox vBox = new VBox(inputNewDate, datePicker, inputCurrencyName, textField, submit);
            vBox.setSpacing(10);


            TableColumn<NewCurrencySheet, String> existCurrencyCol = new TableColumn<>("Currency");
            existCurrencyCol.setMinWidth(100);
            existCurrencyCol.setCellValueFactory(
                    new PropertyValueFactory<>("existCurrency"));

            Label label = new Label("Please enter the corresponding exchange rate");
            label.setWrapText(true);
            TableColumn<NewCurrencySheet, Double> newValueCol = new TableColumn<>("Value");
            newValueCol.setMinWidth(100);
            newValueCol.setCellValueFactory(
                    new PropertyValueFactory<>("newValue"));
            table.setItems(data);
            table.getColumns().add(existCurrencyCol);
            table.getColumns().add(newValueCol);
            table.setTooltip(new Tooltip("Please enter the corresponding exchange rate"));

            existCurrencyCol.setSortable(false);
            newValueCol.setSortable(false);
            table.setEditable(true);
            newValueCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter(){
                @Override
                public Double fromString(String value) {
                    try {
                        return super.fromString(value);
                    }catch (RuntimeException e){
                        return 1.;
                    }
                }
            }));
            newValueCol.setOnEditCommit(e-> e.getTableView().getItems().get(e.getTablePosition().getRow()).setNewValue(e.getNewValue()));

            VBox tableVBox = new VBox(label, table);
            hBox.getChildren().addAll(vBox, tableVBox);

            // show the windows
            dialog.show();
        });
    }

    @Override
    public Node generatePage(Pane root) {
        Button button = new Button("Add new Currency");
        this.setupNewCurrencyButton(button, root);
        HBox hBox = new HBox(button);
        hBox.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(hBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.prefHeightProperty().bind(root.heightProperty());
        vbox.prefWidthProperty().bind(root.widthProperty());


        Label selectedCurrency = new Label("Selected Popular Currency");
        VBox checkBoxVbox = new VBox(selectedCurrency);
        checkBoxVbox.setSpacing(10);

        for (String currency : Database.getDb().getCurrentCurrencyList()) {
            CheckBox checkBox = new CheckBox(currency);
            if (Database.getDb().getSelectedCurrency().contains(currency)){
                checkBox.setSelected(true);
                this.selectedCounter += 1;
            }
            checkBoxVbox.getChildren().add(checkBox);
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()){
                    if (this.selectedCounter<4){
                        Database.getDb().getSelectedCurrency().add(checkBox.getText());
                        this.selectedCounter+=1;
                    }else {
                        checkBox.setSelected(false);
                    }
                }else {
                    this.selectedCounter-=1;
                    Database.getDb().getSelectedCurrency().remove(checkBox.getText());
                }
            });
        }

        vbox.getChildren().add(checkBoxVbox);
        return vbox;
    }
}
