package team.re4group1.Database;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestDatabase {

    @Order(1)
    @Test
    void dataBasePathInitTest() {
        Database database_1 = Database.getDb();
        assertEquals(database_1.getPath(), "./src/main/resources/team/re4group1/Database/exchangeRate.json", "default path is incorrect");

        Database database_2 = Database.getTestDb();
        assertEquals(database_2.getPath(), "./src/test/resources/team/re4group1/Database/exchangeRate.json", "default path is incorrect");
    }

    @Order(2)
    @Test
    void dataBaseInit() {
        Database database = Database.getTestDb();

        Map<String, Double> AUDExchangeRate = new HashMap<>();
        AUDExchangeRate.put("USD", 0.80);
        AUDExchangeRate.put("CNY", 5.00);
        database.addOneCurrencyRate("09/09/2022", "AUD", AUDExchangeRate);

        Map<String, Double> USDExchangeRate = new HashMap<>();
        USDExchangeRate.put("CNY", 7.00);
        database.addOneCurrencyRate("09/09/2022", "USD", USDExchangeRate);
        Gson gson = new Gson();
        assertNotNull(gson.toJson(database.getData())
                ,
                "wrong data set up");
    }

    @Order(3)
    @Test
    void testPopularCurrency() {
        Database database = Database.getTestDb();
        Map<String, Double> AUDExchangeRate_1 = new HashMap<>();
        Map<String, Double> USDExchangeRate_1 = new HashMap<>();

        AUDExchangeRate_1.put("USD", 0.70);
        AUDExchangeRate_1.put("CNY", 5.10);

        USDExchangeRate_1.put("CNY", 6.90);

        database.addOneCurrencyRate("09/09/2022", "AUD", AUDExchangeRate_1);
        database.addOneCurrencyRate("09/09/2022", "USD", USDExchangeRate_1);

        Map<String, Double> AUDExchangeRate_2 = new HashMap<>();
        Map<String, Double> USDExchangeRate_2 = new HashMap<>();

        AUDExchangeRate_2.put("USD", 0.80);
        AUDExchangeRate_2.put("CNY", 5.00);

        USDExchangeRate_2.put("CNY", 7.00);

        database.addOneCurrencyRate("10/09/2022", "AUD", AUDExchangeRate_2);
        database.addOneCurrencyRate("10/09/2022", "USD", USDExchangeRate_2);

        assertNotNull(database.getCurrentCurrencyList().toString());
        assertNotNull(database.getLatestExchangeRate().toString());
    }

    @Order(4)
    @Test
    void testHistory() {
        Database database = Database.getTestDb();

        Map<String, Double> AUDExchangeRate_1 = new HashMap<>();
        Map<String, Double> USDExchangeRate_1 = new HashMap<>();

        AUDExchangeRate_1.put("USD", 0.70);
        AUDExchangeRate_1.put("CNY", 5.10);

        USDExchangeRate_1.put("CNY", 6.90);

        database.addOneCurrencyRate("09/09/2022", "AUD", AUDExchangeRate_1);
        database.addOneCurrencyRate("09/09/2022", "USD", USDExchangeRate_1);

        Map<String, Double> AUDExchangeRate_2 = new HashMap<>();
        Map<String, Double> USDExchangeRate_2 = new HashMap<>();

        AUDExchangeRate_2.put("USD", 0.80);

        USDExchangeRate_2.put("CNY", 7.00);

        database.addOneCurrencyRate("10/09/2022", "AUD", AUDExchangeRate_2);
        database.addOneCurrencyRate("10/09/2022", "USD", USDExchangeRate_2);

        ArrayList<Long> result = new ArrayList<>(Arrays.asList(1662652800000L, 1662739200000L));
        assertNotNull(database.getExchangeRateFromTo("AUD", "USD", "09/09/2022", "10/09/2022"));

//        assertEquals(database.getExchangeRateFromTo("AUD","USD","09/09/2022","10/09/2022").toString(),"{Fri Sep 09 00:00:00 AEST 2022=0.7, Sat Sep 10 00:00:00 AEST 2022=0.8}","wrong history");

    }

    @Order(5)
    @Test
    void testSelectCurrency() {
        Database database = Database.getTestDb();
        ArrayList<String> listCurrency = new ArrayList<>();
        listCurrency.add("CNY");
        listCurrency.add("USD");
        listCurrency.add("AUD");
        database.setSelectedCurrency(listCurrency);
        assertEquals(database.getSelectedCurrency().toString(), "[CNY, USD, AUD]", "Wrong Selected currencies");

    }

    @AfterEach
    void deleteDatabase() {
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