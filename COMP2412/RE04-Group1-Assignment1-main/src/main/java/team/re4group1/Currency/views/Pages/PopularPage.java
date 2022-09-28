package team.re4group1.Currency.views.Pages;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import team.re4group1.Database.Database;
import team.re4group1.Permission.Permission;
import team.re4group1.Currency.views.Page;
import team.re4group1.Utils.Generated;

import java.util.ArrayList;
import java.util.Objects;

@Generated
public class PopularPage extends Page {

    public PopularPage(Permission permission) {
        super(permission);
    }

    @Override
    public Node generatePage(Pane root) {
        HBox layoutHBox = new HBox();
        layoutHBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(layoutHBox);
        vbox.prefHeightProperty().bind(root.heightProperty());
        vbox.prefWidthProperty().bind(root.widthProperty());
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);


        ArrayList<String> currentCurrencyList = new ArrayList<>(Database.getDb().getSelectedCurrency());
        int counter = currentCurrencyList.size();
        for (String s : Database.getDb().getCurrentCurrencyList()) {
            if (counter < 4 && !currentCurrencyList.contains(s)){
                currentCurrencyList.add(s);
                counter += 1;
            }
        }

        ArrayList<ArrayList<String>> resultList = new ArrayList<>();
        ArrayList<String> headerList = new ArrayList<>();
        headerList.add("From/To");
        headerList.addAll(currentCurrencyList);
        resultList.add(headerList);
        ArrayList<JsonObject> jsonObjects = new ArrayList<>();
        for (String s : currentCurrencyList) {
            for (JsonElement moneyList : Database.getDb().getLatestExchangeRate().get("moneyList").getAsJsonArray()) {
                if (((JsonObject)moneyList).get("symbol").getAsString().equals(s)){
                    jsonObjects.add(((JsonObject)moneyList));
                }
            }
        }
        for (JsonObject moneyList : jsonObjects) {
            String currencySymbol = moneyList.get("symbol").getAsString();
            ArrayList<String> row = new ArrayList<>();
            row.add(currencySymbol);
            for (String s : currentCurrencyList) {
                JsonElement result = moneyList.get("exchangeRate").getAsJsonObject().get(s);
                JsonElement symbol = moneyList.get("valuedUp").getAsJsonObject().get(s);
                boolean addSymbol = true;
                if (result == null){
                    row.add("-");
                } else {
                    if (symbol==null){
                        addSymbol = false;
                    }
                    row.add(result.getAsString() + (addSymbol?(symbol.getAsBoolean()?" ↑":" ↓"):""));
                }
            }
            resultList.add(row);
        }
        VBox popularTable = new VBox();
        for (ArrayList<String> row : resultList) {
            HBox hBox = new HBox();
            hBox.setId("popularPage-table-hBox");
            for (String rowElement : row) {
                Label label = new Label(rowElement);
                label.setAlignment(Pos.CENTER);
                label.setPrefHeight(40);
                label.setPrefWidth(80);
                label.setId("popularPage-table-label");
                hBox.getChildren().add(label);
            }
            popularTable.getChildren().add(hBox);
        }
        layoutHBox.getChildren().add(popularTable);
        return vbox;
    }
}
