package team.re4group1.Currency.views;

import org.junit.jupiter.api.Test;
import team.re4group1.Currency.views.Pages.AdminPage;
import team.re4group1.Currency.views.Pages.HistoryPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestNewCurrencySheet {
    @Test
    void testTableItem(){
        AdminPage.NewCurrencySheet tableItem = new AdminPage.NewCurrencySheet("test1", 1.);
        // test ExistCurrency
        assertEquals("test1", tableItem.getExistCurrency());
        assertEquals("test1", tableItem.existCurrencyProperty().get());
        tableItem.setExistCurrency("test1Change");
        assertEquals("test1Change", tableItem.getExistCurrency());

        // test ExistCurrency
        assertEquals(1., tableItem.getNewValue());
        assertEquals(1., tableItem.newValueProperty().get());
        tableItem.setNewValue(2.);
        assertEquals(2., tableItem.getNewValue());

    }
}
