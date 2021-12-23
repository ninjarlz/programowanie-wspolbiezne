package pl.tul.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.log4j.Log4j2;
import pl.tul.service.*;
import pl.tul.view.FileClientPanelView;
import pl.tul.view.ViewUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.IntStream;

@Log4j2
public class FXMLController implements Initializable {

    private static final int THREADS_NUM = 5;
    private static final int FEW_FILES_NUMBER = 3;
    private static final int RANDOM_FILES_NUMBER = 6;
    private static final String WAITING_LABEL = "Waiting...";
    private static final String FILE_SIZE_SUFFIX = " MB";
    private final Map<String, Color> fileClientsColors = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> fileClientsWaitingTimesRefreshTasks = new ConcurrentHashMap<>();
    private final FileService fileService = new FileService(THREADS_NUM, this::fileUploadBeginCallback,
            this::fileUploadCallback, this::fileUploadFinishedCallback);
    private final RandomGenerator randomGenerator = new RandomGenerator();

    public HBox threadsBox;

    public VBox fileClientsBox;

    public Button addFileClientButton;

    public Button addRandomFileClientButton;

    private void fileUploadBeginCallback(FileClient fileClient, FileUpload fileUpload, FileThread fileThread) {
        Platform.runLater(() -> {
            AnchorPane fileClientPanel = getFileClientPanel(fileUpload.getClientId());
            Color fileClientColor = fileClientsColors.get(fileUpload.getClientId());
            Label filesValueLabel = (Label) fileClientPanel.getChildren().get(9);
            FileClientPanelView.setFilesValueLabel(filesValueLabel, fileClient);
            AnchorPane threadPanel = getThreadPanel(fileThread.getId());
            ViewUtils.setPanelBorderColor(threadPanel, fileClientColor);
            Label nameLabel = (Label) threadPanel.getChildren().get(0);
            ViewUtils.setLabelColor(nameLabel, fileClientColor);
            nameLabel.setText(fileUpload.getFileId());
            Label ownerLabel = (Label) threadPanel.getChildren().get(1);
            ViewUtils.setLabelColor(ownerLabel, fileClientColor);
            Label ownerValueLabel = (Label) threadPanel.getChildren().get(2);
            ownerValueLabel.setText(fileUpload.getClientId());
            Label sizeLabel = (Label) threadPanel.getChildren().get(3);
            ViewUtils.setLabelColor(sizeLabel, fileClientColor);
            Label sizeValueLabel = (Label) threadPanel.getChildren().get(5);
            sizeValueLabel.setText(fileUpload.getFileSize() + FILE_SIZE_SUFFIX);
            ProgressBar progressBar = (ProgressBar) threadPanel.getChildren().get(4);
            ViewUtils.setProgressBarColor(progressBar, fileClientColor);
        });
    }

    private void fileUploadCallback(FileUpload fileUpload, FileThread fileThread, Long sizeUploaded) {
        Platform.runLater(() -> {
            AnchorPane threadPanel = getThreadPanel(fileThread.getId());
            ProgressBar progressBar = (ProgressBar) threadPanel.getChildren().get(4);
            progressBar.setProgress(((double) sizeUploaded) / fileUpload.getFileSize());
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
            ownerValueLabel.setText(WAITING_LABEL);
            Label sizeLabel = (Label) threadPanel.getChildren().get(3);
            sizeLabel.setStyle("-fx-text-fill: black;");
            Label sizeValueLabel = (Label) threadPanel.getChildren().get(5);
            sizeValueLabel.setText(WAITING_LABEL);
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
        FileClient fileClient = new FileClient(id, Collections.synchronizedList(new ArrayList<>()));
        fileService.addFileClient(fileClient);
        addFileClientCallback(fileClient);
        return fileClient;
    }

    public FileClient createClientWithRandomFiles() throws IOException {
        String id = randomGenerator.generateFileClientId();
        List<File> fileList = createRandomFiles(id);
        FileClient fileClient = new FileClient(id, fileList);
        fileService.addFileClient(fileClient);
        addFileClientCallback(fileClient);
        return fileClient;
    }

    private void addFileClientCallback(FileClient fileClient) throws IOException {
        Color randomColor = randomGenerator.getRandomColor();
        fileClientsColors.put(fileClient.getId(), randomColor);
        AnchorPane fileClientPanel = FileClientPanelView.getFileClientPanel(fileClient, randomColor, onRemoveFileClientAction(fileClient),
                onAddSmallFileAction(fileClient), onAddBigFileAction(fileClient), onAddFewSmallFilesAction(fileClient),
                onAddFewBigFilesAction(fileClient), fileClientsWaitingTimesRefreshTasks);
        fileClientsBox.getChildren().add(fileClientPanel);
    }

    public File createFile(long fileSize, FileClient fileClient) throws IOException {
        String id = randomGenerator.generateFileId();
        File file = new File(id, fileSize, fileClient.getId());
        fileService.addFile(file);
        AnchorPane fileClientPanel = getFileClientPanel(fileClient.getId());
        Label filesValueLabel = (Label) fileClientPanel.getChildren().get(9);
        FileClientPanelView.setFilesValueLabel(filesValueLabel, fileClient);
        return file;
    }

    private EventHandler<MouseEvent> onAddEmptyFileClientAction() {
        return event -> {
            try {
                createEmptyClient();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private EventHandler<MouseEvent> onAddFileClientWithRandomFilesAction() {
        return event -> {
            try {
                createClientWithRandomFiles();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private List<File> createRandomFiles(String fileClientId) {
        List<File> files = Collections.synchronizedList(new ArrayList<>());
        IntStream.range(0, RANDOM_FILES_NUMBER).forEach(i -> {
            long size = randomGenerator.getRandomFileSize();
            String id = randomGenerator.generateFileId();
            files.add(new File(id, size, fileClientId));
        });
        Collections.sort(files);
        return files;
    }

    private EventHandler<MouseEvent> onRemoveFileClientAction(FileClient fileClient) {
        return event -> {
            fileService.removeFileClient(fileClient);
            fileClientsColors.remove(fileClient.getId());
            fileClientsBox.getChildren().removeIf(node -> node.getId().equals(fileClient.getId()));
            cancelFileUploadWaitingTimeRefreshTask(fileClient);
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

    private void cancelFileUploadWaitingTimeRefreshTask(FileClient fileClient) {
        fileClientsWaitingTimesRefreshTasks.get(fileClient.getId()).cancel(false);
        fileClientsWaitingTimesRefreshTasks.remove(fileClient.getId());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addFileClientButton.setOnMouseClicked(onAddEmptyFileClientAction());
        addRandomFileClientButton.setOnMouseClicked(onAddFileClientWithRandomFilesAction());
    }
}
