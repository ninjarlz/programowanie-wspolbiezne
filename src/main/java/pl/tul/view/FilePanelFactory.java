package pl.tul.view;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import pl.tul.service.File;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FilePanelFactory {

    private static final String FILE_VIEW_PATH = "/pl/tul/view/file.fxml";
    private static final String FILE_SIZE_SUFFIX = " MB";
    private static final String FILE_WAITING_TIME_SUFFIX = " ms";

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public static AnchorPane getFilePanel(File file, Color fileClientColor, Map<String, ScheduledFuture<?>> fileWaitingTimesRefreshTasks) throws IOException {
        URL location = FilePanelFactory.class.getResource(FILE_VIEW_PATH);
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        AnchorPane filePanel = fxmlLoader.load();
        filePanel.setId(file.getId());
        filePanel.setStyle("-fx-border-width: 1px 1px 1px 1px;-fx-border-color: rgb(" + fileClientColor.getRed() + "," + fileClientColor.getGreen() + "," + fileClientColor.getBlue() + ");");
        ObservableList<Node> children = filePanel.getChildren();
        Label nameLabel = (Label) children.get(0);
        ViewUtils.setLabelColor(nameLabel, fileClientColor);
        nameLabel.setText(file.getId());
        Label sizeLabel = (Label) children.get(1);
        ViewUtils.setLabelColor(sizeLabel, fileClientColor);
        Label sizeValueLabel = (Label) children.get(2);
        sizeValueLabel.setText(file.getFileSize() + FILE_SIZE_SUFFIX);
        Label waitingTimeLabel = (Label) children.get(3);
        ViewUtils.setLabelColor(waitingTimeLabel, fileClientColor);
        Label waitingTimeValueLabel = (Label) children.get(4);
        waitingTimeValueLabel.setText(Long.toString(file.getWaitingTime()));
        ScheduledFuture<?> scheduledFuture = executorService.scheduleWithFixedDelay(() -> Platform.runLater(() -> waitingTimeValueLabel
                .setText(file.getWaitingTime() + FILE_WAITING_TIME_SUFFIX)), 0, 100, TimeUnit.MILLISECONDS);
        fileWaitingTimesRefreshTasks.put(file.getId(), scheduledFuture);
        return filePanel;
    }
}
