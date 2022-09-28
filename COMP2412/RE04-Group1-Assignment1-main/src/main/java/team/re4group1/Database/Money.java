package team.re4group1.Database;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Money {
    public String symbol;

    public Map<String, Double> exchangeRate;
    public Map<String, Boolean> valuedUp = new HashMap<>();


    public Map<String, Boolean> getValuedUp() {
        return valuedUp;
    }



    public String getSymbol() {
        return symbol;
    }
    public void setExchangeRate(String rateTo, Double rate) {
        exchangeRate.put(rateTo,rate);
    }

    public Double getExchangeRateOf(String symbol) {
        return exchangeRate.getOrDefault(symbol, 0d);
    }

    public boolean currencyChecker(String symbol){
        return symbol.matches("[A-Z]+") && symbol.length() == 3;
    }
    /**
     * check if currency name is strictly three letter and all capitals
     * @param symbol currency to be checked
     * @return boolean if currency is valid
     */
    public Money(String symbol, Map<String, Double> exchangeRate) {
        if(currencyChecker(symbol)){
            this.symbol = symbol;
            this.exchangeRate = exchangeRate;
        }
    }

}
