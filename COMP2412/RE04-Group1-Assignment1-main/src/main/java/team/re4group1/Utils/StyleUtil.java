package team.re4group1.Utils;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

@Generated
public class StyleUtil {
    public static void fadeIn(Node node){
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500));
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(0.8);
        fadeTransition.setNode(node);
        fadeTransition.play();
    }

    public static void fadeOut(Node node){
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500));
        fadeTransition.setFromValue(0.8);
        fadeTransition.setToValue(0);
        fadeTransition.setNode(node);
        fadeTransition.play();
    }
}
