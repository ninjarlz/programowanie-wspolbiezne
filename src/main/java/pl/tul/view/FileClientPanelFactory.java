package pl.tul.view;

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

public class FileClientPanelFactory {

    private static final String FILE_CLIENT_VIEW_PATH = "/pl/tul/view/file-client.fxml";

    public static AnchorPane getFileClientPanel(FileClient fileClient, Color color, EventHandler<MouseEvent> onRemoveAction,
                                                EventHandler<MouseEvent> addSmallFileAction, EventHandler<MouseEvent> addBigFileAction,
                                                EventHandler<MouseEvent> addFewSmallFilesAction, EventHandler<MouseEvent> addFewBigFilesAction) throws IOException {
        URL location = FileClientPanelFactory.class.getResource(FILE_CLIENT_VIEW_PATH);
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        AnchorPane fileClientPanel = fxmlLoader.load();
        fileClientPanel.setId(fileClient.getId());
        ObservableList<Node> children = fileClientPanel.getChildren();
        Label nameLabel = (Label) children.get(0);
        nameLabel.setStyle("-fx-text-fill: rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");");
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
        return fileClientPanel;
    }
}
