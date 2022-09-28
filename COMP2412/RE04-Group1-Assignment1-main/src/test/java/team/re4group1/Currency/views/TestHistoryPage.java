package team.re4group1.Currency.views;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import team.re4group1.Currency.views.Pages.HistoryPage;
import team.re4group1.Database.Database;
import team.re4group1.Permission.Permission;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestHistoryPage {
    /**
     * Those testcase is testing for the framework class for the data table in the History page,
     * it is mainly test whether the get set method works properly.
     */
    @Test
    void testTableItem() {
        HistoryPage.TableItem tableItem = new HistoryPage.TableItem(1, 1, 1, 1, 1);
        // test median
        assertEquals(1, tableItem.getMedian());
        assertEquals(1, tableItem.medianProperty().get());
        tableItem.setMedian(2);
        assertEquals(2, tableItem.getMedian());
        // test mean
        assertEquals(1, tableItem.getMean());
        assertEquals(1, tableItem.meanProperty().get());
        tableItem.setMean(2);
        assertEquals(2, tableItem.getMean());
        // test Max
        assertEquals(1, tableItem.getMax());
        assertEquals(1, tableItem.maxProperty().get());
        tableItem.setMax(2);
        assertEquals(2, tableItem.getMax());
        // test Min
        assertEquals(1, tableItem.getMin());
        assertEquals(1, tableItem.minProperty().get());
        tableItem.setMin(2);
        assertEquals(2, tableItem.getMin());

        // test Sd
        assertEquals(1, tableItem.getSd());
        assertEquals(1, tableItem.sdProperty().get());
        tableItem.setSd(2);
        assertEquals(2, tableItem.getSd());
    }

    /**
     * test the static value return by the getStatisticValues function
     */
    @Test
    void testGetStatistic() {
        HistoryPage page = new HistoryPage(Permission.DEFAULT);
        Database db = Database.getTestDb();
        page.test();
        Map<String, Double> rate = new HashMap<>();
        rate.put("RMB", 4.7);
        db.addOneCurrencyRate("15/09/2022", "AUD", rate);


        // median for odd elements
        Map<String, Double> statistic = page.getStatisticValues("AUD", "RMB", "15/09/2022", "15/09/2022");
        assertEquals(0.0, statistic.get("sd"));
        assertEquals(4.7, statistic.get("min"));
        assertEquals(4.7, statistic.get("median"));
        assertEquals(4.7, statistic.get("max"));
        assertEquals(4.7, statistic.get("mean"));

        // median for even elements
        rate.clear();
        rate.put("USD", 0.7);
        db.addOneCurrencyRate("09/09/2022", "AUD", rate);
        rate.clear();
        rate.put("USD", 0.8);
        db.addOneCurrencyRate("10/09/2022", "AUD", rate);
        statistic = page.getStatisticValues("AUD", "USD", "09/09/2022", "10/09/2022");
        assertEquals(0,   Math.floor(statistic.get("sd")));
        assertEquals(0, Math.floor(statistic.get("min")));
        assertEquals(0, Math.floor(statistic.get("median")));
        assertEquals(0, Math.floor(statistic.get("max")));
        assertEquals(0, Math.floor(statistic.get("mean")));

    }

    /**
     * get getter for the Page
     */
    @Test
    void testGetter() {
        HistoryPage page = new HistoryPage(Permission.DEFAULT);
        Database db = Database.getTestDb();
        page.test();
        Map<String, Double> rate = new HashMap<>();
        rate.put("USD", 0.7);
        db.addOneCurrencyRate("09/09/2022", "AUD", rate);
        rate.clear();
        rate.put("USD", 0.8);
        db.addOneCurrencyRate("10/09/2022", "AUD", rate);

        //getHistory()
        page.getStatisticValues("AUD", "USD", "09/09/2022", "10/09/2022");
        Map<Date, Double> test = page.getHistory();
        Map<Date, Double> groundTruth = Database.getTestDb().getExchangeRateFromTo("AUD", "USD", "09/09/2022", "10/09/2022");
        assertEquals(page.getHistory(), groundTruth);

        //getStartDate()
        assertEquals(page.getStartDate(), "09/09/2022");
        //getEndDate()
        assertEquals(page.getEndDate(), "10/09/2022");
    }

    @AfterEach
    void deleteDatabase(){
        File myObj = new File("./src/test/resources/team/re4group1/Database/exchangeRate.json");
        try {
            FileWriter fileWriter = new FileWriter(myObj);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
