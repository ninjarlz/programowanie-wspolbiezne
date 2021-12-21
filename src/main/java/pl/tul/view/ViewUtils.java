package pl.tul.view;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

import java.awt.*;

public class ViewUtils {
    public static void setLabelColor(Label label, Color color) {
        label.setStyle("-fx-text-fill: rgb(" + color.getRed() + "," + color.getGreen() +
                "," + color.getBlue() + ");");
    }

    public static void setPanelBorderColor(AnchorPane panel, Color color) {
        panel.setStyle("-fx-border-width: 2px 2px 2px 2px;-fx-border-color: rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");");
    }

    public static void setProgressBarColor(ProgressBar progressBar, Color color) {
        progressBar.setStyle("-fx-accent: rgb(" + color.getRed() + "," + color.getGreen() +
                "," + color.getBlue() + ");");
    }
}
