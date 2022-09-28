package team.re4group1.Currency.views.Pages;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;
import team.re4group1.Database.Database;
import team.re4group1.Permission.Permission;
import team.re4group1.Currency.views.Page;
import team.re4group1.Utils.Generated;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class HistoryPage extends Page{

    public Database db;
    public String startDate;
    public String endDate;

    public Map<Date, Double> getHistory() {
        return history;
    }
    private ArrayList<String> currencyList = Database.getDb().getCurrentCurrencyList();
    public Map<Date, Double> history;

    private final static String pattern = "dd/MM/yyyy";
    private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

    private final ObservableList<TableItem> statisticTableList = FXCollections.observableArrayList();

    public static class TableItem {
        public final SimpleDoubleProperty median;
        public final SimpleDoubleProperty mean;
        public final SimpleDoubleProperty max;
        public final SimpleDoubleProperty min;
        public final SimpleDoubleProperty sd;

        /**
         * inner class for table view
         * @param median median value
         * @param mean mean value
         * @param max max value
         * @param min min value
         * @param sd  sd value
         */
        public TableItem(double median, double mean, double max, double min, double sd) {
            this.median = new SimpleDoubleProperty(median);
            this.mean = new SimpleDoubleProperty(mean);
            this.max = new SimpleDoubleProperty(max);
            this.min = new SimpleDoubleProperty(min);
            this.sd = new SimpleDoubleProperty(sd);
        }

        public double getMedian() {
            return median.get();
        }

        public SimpleDoubleProperty medianProperty() {
            return median;
        }

        public void setMedian(double median) {
            this.median.set(median);
        }

        public double getMean() {
            return mean.get();
        }

        public SimpleDoubleProperty meanProperty() {
            return mean;
        }

        public void setMean(double mean) {
            this.mean.set(mean);
        }

        public double getMax() {
            return max.get();
        }

        public SimpleDoubleProperty maxProperty() {
            return max;
        }

        public void setMax(double max) {
            this.max.set(max);
        }

        public double getMin() {
            return min.get();
        }

        public SimpleDoubleProperty minProperty() {
            return min;
        }

        public void setMin(double min) {
            this.min.set(min);
        }

        public double getSd() {
            return sd.get();
        }

        public SimpleDoubleProperty sdProperty() {
            return sd;
        }

        public void setSd(double sd) {
            this.sd.set(sd);
        }
    }

    @Override
    @Generated
    public Node generatePage(Pane root) {
        ChoiceBox<String> currencyFrom = new ChoiceBox<>(FXCollections.observableArrayList(
                currencyList
        ));
        FontIcon fontIconExchange = new FontIcon("fa-exchange");
        ChoiceBox<String> currencyTo= new ChoiceBox<>(FXCollections.observableArrayList(
                currencyList
        ));
        currencyFrom.setPrefWidth(100);
        currencyFrom.setPrefHeight(30);
        currencyTo.setPrefWidth(100);
        currencyTo.setPrefHeight(30);

        HBox currencyChoiceBox = new HBox(currencyFrom, fontIconExchange,currencyTo);
        currencyChoiceBox.setAlignment(Pos.CENTER);
        currencyChoiceBox.setSpacing(30);

        DatePicker datePickerFrom = new DatePicker();
        FontIcon fontIconArrowRight = new FontIcon("fa-arrow-right");
        DatePicker datePickerTo = new DatePicker();

//        // set date selector to not allow user select data from bigger than to
//        datePickerFrom.setOnAction(event -> {
//            LocalDate date = datePickerFrom.getValue();
//            datePickerTo.setValue(date);
//        });
//        datePickerTo.setOnAction(event -> {
//            LocalDate date = datePickerTo.getValue();
//            datePickerFrom.setValue(date);
//        });

        // make ui connect between two data selector
        datePickerFrom.setValue(LocalDate.now());
        datePickerTo.setValue(datePickerFrom.getValue().plusDays(1));
        final Callback<DatePicker, DateCell> dayCellFactoryFrom = new Callback<>() {
            @Generated
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Generated
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        ChronoUnit.DAYS.between(
                                datePickerFrom.getValue(), item
                        );
                        if (item.isAfter(
                                datePickerTo.getValue())
                        ) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }

                    }
                };
            }
        };
        final Callback<DatePicker, DateCell> dayCellFactoryTo = new Callback<>() {
            @Generated
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Generated
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        ChronoUnit.DAYS.between(
                                datePickerFrom.getValue(), item
                        );
                        if (item.isBefore(
                                datePickerFrom.getValue())
                        ) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }

                    }
                };
            }
        };
        datePickerFrom.setDayCellFactory(dayCellFactoryFrom);
        datePickerTo.setDayCellFactory(dayCellFactoryTo);
        // Reference https://blog.csdn.net/zy103118/article/details/122939617
        StringConverter<LocalDate> converter = new StringConverter<>() {
            @Generated
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Generated
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        datePickerFrom.setConverter(converter);
        datePickerTo.setConverter(converter);

        HBox dataPickerHBox = new HBox(datePickerFrom, fontIconArrowRight, datePickerTo);
        dataPickerHBox.setSpacing(10);
        dataPickerHBox.setAlignment(Pos.CENTER);


        TableView<TableItem> statisticTable = new TableView<>();
        statisticTable.setItems(statisticTableList);
        statisticTable.setEditable(false);
        ArrayList<String> colNames = new ArrayList<>(Arrays.asList("median", "mean", "max", "min", "sd"));
        for (String name : colNames) {
            TableColumn<TableItem, Double> col = new TableColumn<>(name);
            col.setSortable(false);
            col.setCellValueFactory(new PropertyValueFactory<>(name));
            statisticTable.getColumns().add(col);
        }

        Button button = new Button("Calculate");

        button.setOnAction(event -> {
            statisticTableList.clear();
            try {
                Map<String, Double> statisticResult = getStatisticValues(currencyFrom.getValue(), currencyTo.getValue(),
                        datePickerFrom.getValue().format(dateFormatter), datePickerTo.getValue().format(dateFormatter));
                statisticTableList.add(
                        new TableItem(
                                statisticResult.get("sd"),
                                statisticResult.get("min"),
                                statisticResult.get("median"),
                                statisticResult.get("max"),
                                statisticResult.get("mean")
                        ));
            } catch (RuntimeException e){
                statisticTableList.add(
                        new TableItem(
                                0,
                                0,
                                0,
                                0,
                                0
                        ));
            }
        });


        StackPane stackPane = new StackPane(statisticTable);
        stackPane.setPrefWidth(root.widthProperty().get());
        stackPane.setPrefHeight(80);



        VBox vbox = new VBox(currencyChoiceBox, dataPickerHBox, button, stackPane);
        vbox.prefHeightProperty().bind(root.heightProperty());
        vbox.prefWidthProperty().bind(root.widthProperty());
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);
        return vbox;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    /**
     * get the exchange history as a map form.
     * @param currencyFrom The currency that be exchanged
     * @param currencyTo The currency that exchange to
     * @param date_from The staring date of history
     * @param date_to the end date of history
     */
    private void getHistoryData(String currencyFrom, String currencyTo,String date_from, String date_to) {
        startDate = date_from;
        endDate = date_to;
        history = db.getExchangeRateFromTo(currencyFrom, currencyTo, date_from, date_to);
    }

    /**
     * return some statistic values of the exchange rate for a period
     * @return the median, mean, max, min, standard deviation of the period of exchange rate
     */
    public Map<String, Double> getStatisticValues(String currencyFrom, String currencyTo,String date_from, String date_to) {
        getHistoryData(currencyFrom, currencyTo, date_from, date_to);
        Map<String, Double> statistic = new HashMap<>();
        List<Double> rates = new ArrayList<>();
        double sum = 0.;
        double max = -1.;
        double min = 999.;
        double median;
        double sd;
        for (Double value : history.values()) {
            rates.add(value);
            sum += value;
            if (value > max) {
                max = value;
            }
            if (value < min) {
                min = value;
            }
        }
        double mean = sum / rates.size();
        Collections.sort(rates);
        if (rates.size() % 2 == 1) {
            median = rates.get((rates.size()-1)/2);
        } else {
            median = (rates.get(rates.size()/2-1) + rates.get(rates.size()/2) + 0.0)/2;
        }
        double tmp = 0.0;
        for (Double value : rates) {
            tmp += Math.pow(value - mean, 2);
        }
        sd = Math.sqrt(tmp / rates.size());
        statistic.put("median", median);
        statistic.put("mean", mean);
        statistic.put("max", max);
        statistic.put("min", min);
        statistic.put("sd", sd);
        return statistic;
    }

    public HistoryPage(Permission permission) {
        super(permission);
        db = Database.getDb();
    }

    public void test() {
        db = Database.getTestDb();
    }

//    public static void main(String[] args) {
//        HistoryPage hp = new HistoryPage(Permission.DEFAULT);
//        Map<String, Double> rate = new HashMap<>();
//        rate.put("RMB", 4.7);
//        rate.put("USD", 0.67);
//        hp.db.addOneCurrencyRate("15/09/2022", "AUD", rate);
//        rate.put("RMB", 4.0);
//        rate.put("USD", 0.75);
//        hp.db.addOneCurrencyRate("16/09/2022", "AUD", rate);
//    }
}