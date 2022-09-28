package team.re4group1.Database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.DecimalFormat;
import java.util.*;

public class Data {
    public List<ExchangeDay> exchangeDays;

    /**
     * constructor
     */
    public Data(){
        this.exchangeDays = new ArrayList<ExchangeDay>();
    }

    /**
     * will return the data of exchange of currency from date to date
     * @param symbolFrom the currecny from
     * @param symbolTo the currency to
     * @param from date from
     * @param to date to
     * @return a dictionary contains exchange rate in the format {date:exchangerate, date:exchangerate}
     */

    public Map<Date,Double> exchangeRateFromTo(String symbolFrom,String symbolTo, Date from, Date to){
        Map<Date,Double> history = new HashMap<Date,Double>();
        for (ExchangeDay exchangeDay : exchangeDays) {
            if (!exchangeDay.getDate().before(from)&&!exchangeDay.getDate().after(to)) {

                for(int i = 0;i<exchangeDay.getMoneyList().size();i++){
                    if(exchangeDay.getMoneyList().get(i).getSymbol().equals(symbolFrom)){
                        history.put(exchangeDay.getDate(),exchangeDay.getMoneyList().get(i).getExchangeRateOf(symbolTo));
                    }
                }
            }
        }
        return history;
    }

    /**
     * add a currency's exchange rate in day
     * @param date date to be added
     * @param symbol the currency
     * @param exchangeRate the exchange rate
     */

    public void addExchangeDay(Date date, String symbol, Map<String, Double> exchangeRate){
        DecimalFormat df = new DecimalFormat("0.00");
        Money money = new Money(symbol,exchangeRate);
        boolean foundDate = false;
        boolean moneyExist = false;
        for (ExchangeDay exchangeDay : exchangeDays) {
            if (exchangeDay.getDate().equals(date)) {
                for(Money moneyOnDay: exchangeDay.getMoneyList()){
                    if(exchangeRate.containsKey(moneyOnDay.getSymbol())) {
                        moneyOnDay.setExchangeRate(symbol, Double.valueOf(df.format(1/exchangeRate.get(moneyOnDay.getSymbol()))));
                    }
                    if(moneyOnDay.getSymbol().equals(symbol)){
                        for(String eachSymbol: exchangeRate.keySet()){
                            moneyOnDay.setExchangeRate(eachSymbol,exchangeRate.get(eachSymbol));
                        }
                        moneyExist = true;
                    }
                }
                if(!moneyExist) {
                    exchangeDay.getMoneyList().add(money);
                }
                foundDate = true;
            }
        }
        if(!foundDate){
            List<Money> moneyList = new ArrayList<Money>();
            moneyList.add(money);
            for(String otherMoney:exchangeRate.keySet()){
                Map<String, Double> newExchangeRate = new HashMap<String,Double>();
                newExchangeRate.put(symbol,1/exchangeRate.get(otherMoney));
                Money newMoney = new Money(otherMoney,newExchangeRate);
                moneyList.add(newMoney);
            }
            ExchangeDay exchangeDay = new ExchangeDay(date,moneyList);
            this.exchangeDays.add(exchangeDay);
        }
        Collections.sort(exchangeDays);
    }

    /**
     *
     * @return {"moneyList":[{"symbol":"AUD","exchangeRate":{"EUR":0.6,"USD":0.8,"CNY":5.0},"valuedUp":{"EUR":true,"USD":true,"CNY":false}},{"symbol":"USD","exchangeRate":{"AUD":1.2,"EUR":0.9,"CNY":7.0},"valuedUp":{"AUD":false,"EUR":true,"CNY":true}}],"date":"Sep 10, 2022, 12:00:00 AM"}
     */
    public JsonObject getPopularCurrency(){
        Collections.sort(exchangeDays);
        Collections.reverse(exchangeDays);

        for(Money money:exchangeDays.get(0).getMoneyList()){
            Map<String, Boolean> valuedUp = new HashMap<>();
            for(String exchangeTo: money.exchangeRate.keySet()){
                if(exchangeDays.size()<2){
                    money.getValuedUp().put(exchangeTo,true);
                }else if(Double.compare(exchangeDays.get(1).exchangeRate(money.getSymbol(), exchangeTo)
                        , money.getExchangeRateOf(exchangeTo)) < 0 ){
                    money.getValuedUp().put(exchangeTo,true);
                }else{
                    money.getValuedUp().put(exchangeTo,false);
                }
            }
        }
        Gson gson = new Gson();

        return (JsonObject) gson.toJsonTree(exchangeDays.get(0));
    }

}

