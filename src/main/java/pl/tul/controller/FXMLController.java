package pl.tul.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.log4j.Log4j2;
import pl.tul.service.*;
import pl.tul.view.FileClientPanelFactory;
import pl.tul.view.FilePanelFactory;
import pl.tul.view.ViewUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Log4j2
public class FXMLController implements Initializable {

    private static final int THREADS_NUM = 5;
    private static final int FEW_FILES_NUMBER = 4;
    private static final String WAITING_LABEL = "Waiting...";
    private static final String EMPTY_LABEL = "Waiting...";
    private static final String FILE_SIZE_SUFFIX = " MB";
    private final Map<String, Color> fileClientsColors = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> fileWaitingTimesRefreshTasks = new ConcurrentHashMap<>();
    private final FileService fileService = new FileService(THREADS_NUM, this::fileUploadBeginCallback,
            this::fileUploadCallback, this::fileUploadFinishedCallback);
    private final RandomGenerator randomGenerator = new RandomGenerator();

    public HBox threadsBox;

    public VBox fileClientsBox;

    public Button addFileClientButton;

    private void fileUploadBeginCallback(File file, FileThread fileThread) {
        Platform.runLater(() -> {
            cancelFileWaitingTimeRefreshTask(file);
            AnchorPane fileClientPanel = getFileClientPanel(file.getClientId());
            ScrollPane filesScrollPane = (ScrollPane) fileClientPanel.getChildren().get(6);
            VBox filesBox = (VBox) filesScrollPane.contentProperty().get();
            filesBox.getChildren().removeIf(node -> node.getId().equals(file.getId()));
            Color fileClientColor = fileClientsColors.get(file.getClientId());
            AnchorPane threadPanel = getThreadPanel(fileThread.getId());
            threadPanel.setStyle("-fx-border-width: 2px 2px 2px 2px;-fx-border-color: rgb(" + fileClientColor.getRed() + "," + fileClientColor.getGreen() + "," + fileClientColor.getBlue() + ");");
            Label nameLabel = (Label) threadPanel.getChildren().get(0);
            ViewUtils.setLabelColor(nameLabel, fileClientColor);
            nameLabel.setText(file.getId());
            Label ownerLabel = (Label) threadPanel.getChildren().get(1);
            ViewUtils.setLabelColor(ownerLabel, fileClientColor);
            Label ownerValueLabel = (Label) threadPanel.getChildren().get(2);
            ownerValueLabel.setText(file.getClientId());
            Label sizeLabel = (Label) threadPanel.getChildren().get(3);
            ViewUtils.setLabelColor(sizeLabel, fileClientColor);
            Label sizeValueLabel = (Label) threadPanel.getChildren().get(5);
            sizeValueLabel.setText(file.getFileSize() + FILE_SIZE_SUFFIX);
            ProgressBar progressBar = (ProgressBar) threadPanel.getChildren().get(4);
            ViewUtils.setProgressBarColor(progressBar, fileClientColor);
        });
    }

    private void fileUploadCallback(File file, FileThread fileThread, Long sizeUploaded) {
        Platform.runLater(() -> {
            AnchorPane threadPanel = getThreadPanel(fileThread.getId());
            ProgressBar progressBar = (ProgressBar) threadPanel.getChildren().get(4);
            progressBar.setProgress(((double) sizeUploaded) / file.getFileSize());
        });
    }

    private void fileUploadFinishedCallback(FileThread fileThread) {
        Platform.runLater(() -> {
            AnchorPane threadPanel = getThreadPanel(fileThread.getId());
            threadPanel.setStyle("-fx-border-width: 2px 2px 2px 2px;-fx-border-color: black;");
            Label nameLabel = (Label) threadPanel.getChildren().get(0);
            nameLabel.setText(WAITING_LABEL);
            nameLabel.setStyle("-fx-text-fill: black;");
            Label ownerLabel = (Label) threadPanel.getChildren().get(1);
            ownerLabel.setStyle("-fx-text-fill: black;");
            Label ownerValueLabel = (Label) threadPanel.getChildren().get(2);
            ownerValueLabel.setText(EMPTY_LABEL);
            Label sizeLabel = (Label) threadPanel.getChildren().get(3);
            sizeLabel.setStyle("-fx-text-fill: black;");
            Label sizeValueLabel = (Label) threadPanel.getChildren().get(5);
            sizeValueLabel.setText(EMPTY_LABEL);
            ProgressBar progressBar = (ProgressBar) threadPanel.getChildren().get(4);
            progressBar.setProgress(0.);
            progressBar.setStyle("-fx-accent: black");
        });
    }

    private AnchorPane getThreadPanel(String fileThreadId) {
        return (AnchorPane) threadsBox.getChildren().stream()
                .filter(node -> node.getId().equals(fileThreadId)).findAny().orElseThrow();
    }

    private AnchorPane getFileClientPanel(String clientId) {
        return (AnchorPane) fileClientsBox.getChildren()
                .stream().filter(node -> node.getId().equals(clientId)).findAny().orElseThrow();
    }

    public FileClient createEmptyClient() throws IOException {
        String id = randomGenerator.generateFileClientId();
        FileClient fileClient = new FileClient(id, new ArrayList<>());
        fileService.addFileClient(fileClient);
        Color randomColor = randomGenerator.getRandomColor();
        fileClientsColors.put(fileClient.getId(), randomColor);
        AnchorPane fileClientPanel = FileClientPanelFactory.getFileClientPanel(fileClient, randomColor, onRemoveFileClientAction(fileClient),
                onAddSmallFileAction(fileClient), onAddBigFileAction(fileClient), onAddFewSmallFilesAction(fileClient),
                onAddFewBigFilesAction(fileClient));
        fileClientsBox.getChildren().add(fileClientPanel);
        return fileClient;
    }

    public File createFile(long fileSize, FileClient fileClient) throws IOException {
        Color fileClientColor = fileClientsColors.get(fileClient.getId());
        String id = randomGenerator.generateFileId();
        File file = new File(id, fileSize, fileClient.getId());
        AnchorPane filePanel = FilePanelFactory.getFilePanel(file, fileClientColor, fileWaitingTimesRefreshTasks);
        AnchorPane fileClientPanel = getFileClientPanel(fileClient.getId());
        ScrollPane filesScrollPane = (ScrollPane) fileClientPanel.getChildren().get(6);
        VBox filesBox = (VBox) filesScrollPane.contentProperty().get();
        filesBox.getChildren().add(filePanel);
        fileService.addFile(file);
        return file;
    }

    private EventHandler<MouseEvent> onAddFileClientAction() {
        return event -> {
            try {
                createEmptyClient();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private EventHandler<MouseEvent> onRemoveFileClientAction(FileClient fileClient) {
        return event -> {
            fileService.removeFileClient(fileClient);
            fileClientsColors.remove(fileClient.getId());
            fileClientsBox.getChildren().removeIf(node -> node.getId().equals(fileClient.getId()));
            fileClient.getFileList().forEach(this::cancelFileWaitingTimeRefreshTask);
        };
    }

    private EventHandler<MouseEvent> onAddSmallFileAction(FileClient fileClient) {
        return event -> {
            try {
                long smallSize = randomGenerator.getRandomSmallFileSize();
                createFile(smallSize, fileClient);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private EventHandler<MouseEvent> onAddFewSmallFilesAction(FileClient fileClient) {
        return event -> {
            try {
                for (int i = 0; i < FEW_FILES_NUMBER; i++) {
                    long smallSize = randomGenerator.getRandomSmallFileSize();
                    createFile(smallSize, fileClient);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private EventHandler<MouseEvent> onAddBigFileAction(FileClient fileClient) {
        return event -> {
            try {
                long bigSize = randomGenerator.getRandomBigFileSize();
                createFile(bigSize, fileClient);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private EventHandler<MouseEvent> onAddFewBigFilesAction(FileClient fileClient) {
        return event -> {
            try {
                for (int i = 0; i < FEW_FILES_NUMBER; i++) {
                    long bigSize = randomGenerator.getRandomBigFileSize();
                    createFile(bigSize, fileClient);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void cancelFileWaitingTimeRefreshTask(File file) {
        fileWaitingTimesRefreshTasks.get(file.getId()).cancel(false);
        fileWaitingTimesRefreshTasks.remove(file.getId());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addFileClientButton.setOnMouseClicked(onAddFileClientAction());
    }
}
