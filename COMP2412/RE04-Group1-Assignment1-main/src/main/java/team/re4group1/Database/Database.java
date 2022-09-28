package team.re4group1.Database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Database {

    public static Database db = new Database();
    public static Database testDb = new Database("./src/test/resources/team/re4group1/Database/exchangeRate.json");
    private String path;
    private Data data;
    /*
     * example of json datebase
     * <code>
     *  exchangerate:
     *  {
     *   dd/mm/yyyy:
     *      {
     *       AUD:{USD:0.6,CNY:5.0},
     *       USD{AUD:1.2,CNY:7.0},
     *       CNY{AUD:0.2,USD,0.18}
     *      },
     *  dd/mm/yyyy:
     *      {
     *      AUD:{USD:0.6,CNY:5.0},
     *      USD{AUD:1.2,CNY:7.0},
     *      CNY{AUD:0.2,USD,0.18}
     *      }
     *  }
     *  Admin
     *  {
     *      {username, hashedpwd}
     *      {username, hashedpwd}
     *  }
     * </code>
     */

    /**
     * getter method return current path, default path is "Database.Database/exchangeRate.json"
     * @return the current path
     */
    public String getPath() {
        return path;
    }
    /**
     * getter method return current data in JSONObject form
     * @return data
     */
    public Data getData() {
        return data;
    }

    public ArrayList<String> getSelectedCurrency() {
        return selectedCurrency;
    }

    public void setSelectedCurrency(ArrayList<String> selectedCurrency) {
        this.selectedCurrency = selectedCurrency;
    }

    private ArrayList<String> selectedCurrency = new ArrayList<>(4);

    /**
     * Constructor without declaring new path
     */
    public Database(){
        setPath("./src/main/resources/team/re4group1/Database/exchangeRate.json");
        readFile();
    }

    /**
     * Constructor declaring new path
     * @param newPath the path of database file
     */
    public Database(String newPath){
        setPath(newPath);
        readFile();
    }

    /**
     * Change path to given newPath
     * @param newPath newpath to be changed into
     */
    public void setPath(String newPath){
        this.path = newPath;
    }
    /**
     * save file to given path
     */
    public void saveFile(){
        try (FileWriter file = new FileWriter(path)) {
            Gson gson = new Gson();
            gson.toJson(data, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * open file and save into this.data, if file not exists create file
     */
    public void readFile(){
        try {
            File New_File = new File(path);
            if(!New_File.createNewFile()&& New_File.length()>0){
                Gson gson = new Gson();
                Reader reader = new FileReader(path);
                this.data = gson.fromJson(reader, Data.class);
                System.out.println(this.data);
            }else {
                this.data = new Data();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * check if date is valid
     * @param dateString string format of date
     * @return whether date is a valid date string
     */
    public Boolean checkDate(String dateString){
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        try {
            DateFor.parse(dateString);
            return true;
        } catch (ParseException e) {e.printStackTrace();}
        return null;
    }

    /**
     * convert string to date
     * @param dateString string date to be convert into Date class
     * @return Date class date
     */
    public Date toDate(String dateString){
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return DateFor.parse(dateString);
        } catch (ParseException e) {e.printStackTrace();}
        return null;
    }

    /**
     * add one currency exchange rate to a specific date, exchange rate has to be a dictionary in the format {currency1: rate1,currency2: rate2}
     * @param dateString the specific date
     * @param symbol the currency
     * @param exchangeRate a dictionary of exchange rate, e.g,{dd/mm/yy: rate1,dd/mm/yy: rate2}
     */
    public void addOneCurrencyRate(String dateString, String symbol, Map<String, Double> exchangeRate){
        assert(checkDate(dateString)): "input date"+dateString+"is incorrect format, correct format: dd/mm/yyyy";
        Date newDate = toDate(dateString);
        data.addExchangeDay(newDate,symbol,exchangeRate);
        saveFile();
    }

    /**
     * get exchange rate of one currency to another currency from a date to a date
     * @param currencyFrom currency to be exchanged from
     * @param currencyTo currency to be exchanged to
     * @param date_from time interval from
     * @param date_to time interbal to
     * @return a dictionary in the fomart {date1:exchange_rate_1,date2:exchange_rate_2}
     */
    public Map<Date, Double> getExchangeRateFromTo(String currencyFrom, String currencyTo,String date_from, String date_to){
        return data.exchangeRateFromTo(currencyFrom,currencyTo,toDate(date_from),toDate(date_to));
    }

    public JsonObject getLatestExchangeRate(){
        return data.getPopularCurrency();
    }

    public ArrayList<String> getCurrentCurrencyList(){
        ArrayList<String> name = new ArrayList<>();
        try {
            for (Object moneyList : this.getLatestExchangeRate().get("moneyList").getAsJsonArray()) {
                name.add(((JsonObject) moneyList).get("symbol").getAsString());
            }
        }catch (RuntimeException ignore){}
        return name;
    }
    public static Database getDb() {return db;}

    public static Database getTestDb() {return testDb;}
}
