module team.re4groupOne {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jfxtras.styles.jmetro;
    requires org.kordamp.ikonli.javafx;
    requires com.google.gson;
//    opens team.re4group1.currency to javafx.fxml;
//    exports team.re4group1.currency;
    exports team.re4group1;
    exports team.re4group1.Database;
    opens team.re4group1 to javafx.fxml;
    opens team.re4group1.Currency.views.Pages to javafx.base;
    opens team.re4group1.Database to com.google.gson;
}