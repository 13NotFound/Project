package team.re4group1;

import jfxtras.styles.jmetro.JMetro;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.Style;
import team.re4group1.Utils.Generated;
import team.re4group1.Utils.RecourseLoader;
import team.re4group1.Utils.RecourseObject;
import java.io.IOException;

@Generated
public class App extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Currency/Currency.fxml"));
        Parent root = fxmlLoader.load();
        root.getStylesheets().addAll(RecourseLoader.load(
                new RecourseObject(App.class, "fonts/font.css"),
                new RecourseObject(App.class, "app.css"),
                new RecourseObject(App.class, "Currency/Currency.css"),
                new RecourseObject(App.class, "Currency/pages/OneToOneCurrency.css"),
                new RecourseObject(App.class, "Currency/pages/PopularPage.css")
        ));
        Scene scene = new Scene(root);
        stage.setTitle("Currency");
        stage.setScene(scene);
        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}