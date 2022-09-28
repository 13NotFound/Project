package team.re4group1.Database;

import java.util.Date;
import java.util.List;

public class ExchangeDay implements Comparable<ExchangeDay>{
    public List<Money> moneyList;
    public final Date date;

    public Date getDate() {
        return this.date;
    }

    public Double exchangeRate(String moneyFrom, String moneyTo) {
        for (Money money : moneyList) {
            if (money.getSymbol().equals(moneyFrom)) {
                return money.getExchangeRateOf(moneyTo);
            }
        }
        return 1.;
    }

    public List<Money> getMoneyList() {
        return moneyList;
    }

    public ExchangeDay(Date date,List<Money> moneyList){
        this.date = date;
        this.moneyList = moneyList;
    }


    @Override
    public int compareTo( ExchangeDay o) {
        return getDate().compareTo(o.getDate());
    }
}
