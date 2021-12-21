package pl.tul.view;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import pl.tul.service.FileClient;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FileClientPanelView {

    private static final String FILE_CLIENT_VIEW_PATH = "/pl/tul/view/file-client.fxml";
    private static final String FILE_SIZE_SUFFIX = " MB";
    private static final String FILE_WAITING_TIME_SUFFIX = " ms";

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    public static AnchorPane getFileClientPanel(FileClient fileClient, Color color, EventHandler<MouseEvent> onRemoveAction,
                                                EventHandler<MouseEvent> addSmallFileAction, EventHandler<MouseEvent> addBigFileAction,
                                                EventHandler<MouseEvent> addFewSmallFilesAction, EventHandler<MouseEvent> addFewBigFilesAction,
                                                Map<String, ScheduledFuture<?>> fileClientsWaitingTimesRefreshTasks) throws IOException {
        URL location = FileClientPanelView.class.getResource(FILE_CLIENT_VIEW_PATH);
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        AnchorPane fileClientPanel = fxmlLoader.load();
        ViewUtils.setPanelBorderColor(fileClientPanel, color);
        fileClientPanel.setId(fileClient.getId());
        ObservableList<Node> children = fileClientPanel.getChildren();
        Label nameLabel = (Label) children.get(0);
        ViewUtils.setLabelColor(nameLabel, color);
        nameLabel.setText(fileClient.getId());
        Button addSmallFileButton = (Button) children.get(1);
        addSmallFileButton.setOnMouseClicked(addSmallFileAction);
        Button addBigFileButton = (Button) children.get(2);
        addBigFileButton.setOnMouseClicked(addBigFileAction);
        Button addFewSmallFilesButton = (Button) children.get(3);
        addFewSmallFilesButton.setOnMouseClicked(addFewSmallFilesAction);
        Button addFewBigFilesButton = (Button) children.get(4);
        addFewBigFilesButton.setOnMouseClicked(addFewBigFilesAction);
        Button removeButton = (Button) children.get(5);
        removeButton.setOnMouseClicked(onRemoveAction);
        Label waitingTimeLabel = (Label) children.get(6);
        ViewUtils.setLabelColor(waitingTimeLabel, color);
        Label waitingTimeValueLabel = (Label) children.get(7);
        Label filesLabel = (Label) children.get(8);
        ViewUtils.setLabelColor(filesLabel, color);
        Label filesValueLabel = (Label) children.get(9);
        setFilesValueLabel(filesValueLabel, fileClient);
        ScheduledFuture<?> scheduledFuture = executorService.scheduleWithFixedDelay(() -> Platform.runLater(() -> waitingTimeValueLabel
                .setText(fileClient.getWaitingTime() + FILE_WAITING_TIME_SUFFIX)), 0, 100, TimeUnit.MILLISECONDS);
        fileClientsWaitingTimesRefreshTasks.put(fileClient.getId(), scheduledFuture);
        return fileClientPanel;
    }

    public static void setFilesValueLabel(Label filesValueLabel, FileClient fileClient) {
        StringBuilder stringBuilder = new StringBuilder();
        fileClient.getFileList().forEach(file -> stringBuilder.append(file.getId()).append(" (")
                .append(file.getFileSize()).append(FILE_SIZE_SUFFIX).append(");  "));
        filesValueLabel.setText(stringBuilder.toString());
    }
}
