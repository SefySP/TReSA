package gr.uop.tresa;

import gr.uop.FileHolder;
import gr.uop.lucene.LuceneConstants;
import gr.uop.lucene.LuceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainStageController implements Initializable
{
    @FXML
    private CheckBox checkBody;

    @FXML
    private CheckBox checkPeople;

    @FXML
    private CheckBox checkPlaces;

    @FXML
    private CheckBox checkTitle;

    @FXML
    private BorderPane mainPane;

    @FXML
    private TextField searchText = new TextField();

    @FXML
    private ListView<Hyperlink> resultListView;

    @FXML
    private Text resultTime;

    private LuceneController luceneController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        luceneController = new LuceneController();
        if(!luceneController.indexDirExists())
        {
           luceneController.createIndexDir();
           luceneController.createIndex();
        }
        else if(luceneController.isIndexDirEmpty())
        {
            luceneController.createIndex();
        }
        resultListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        resultListView.setCellFactory(hyperlinkListView -> new CenteredListViewCell());
    }

    @FXML
    void defaultSearch(ActionEvent event)
    {
        try
        {
            String searchTerm = searchText.getText().trim();
            if (!searchTerm.isEmpty())
            {
                resultListView.getItems().clear();
                List<File> resultList = luceneController.search(searchTerm, getCheckFields());
                resultTime.setText("Time: " + luceneController.getTime() + " ms");
                for (File result : resultList)
                {
                    Hyperlink link = new Hyperlink(result.getName());
                    link.setOnAction(e ->
                    {
                        showResultFileDialog(result);
                    });
                    resultListView.getItems().add(link);
                }
            }
        }
        catch (IOException | ParseException exception)
        {
            exception.printStackTrace();
        }

    }

    private String[] getCheckFields()
    {
        List<String> fields = new ArrayList<>();
        fields.add(LuceneConstants.BODY);

        if (checkPlaces.isSelected())
        {
            fields.add(LuceneConstants.PLACES);
        }
        if (checkPeople.isSelected())
        {
            fields.add(LuceneConstants.PEOPLE);
        }
        if (checkTitle.isSelected())
        {
            fields.add(LuceneConstants.TITLE);
        }
        if (!checkBody.isSelected())
        {
            fields.remove(LuceneConstants.BODY);
        }

        return fields.toArray(new String[0]);
    }

    private void showResultFileDialog(File result)
    {
        FileHolder holder = FileHolder.getInstance();
        holder.setFile(result);
        try
        {
            File showArticleFile = new File("src/main/resources/gr.uop.tresa/ShowArticleDialog.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(showArticleFile.toURI().toURL());
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage showStage = new Stage();

            showStage.setTitle(result.getName());
            showStage.initModality(Modality.APPLICATION_MODAL);
            showStage.initOwner(mainPane.getScene().getWindow());
            showStage.setScene(scene);
            showStage.show();
            showStage.setMinHeight(400.0);
            showStage.setMinWidth(400.0);
        }
        catch (IOException ioException)
        {
            System.out.println(ioException.getMessage());
        }
    }

    @FXML
    public void addFiles(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Add Files");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt", "*.txt"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));


        List<File> fileList = fileChooser.showOpenMultipleDialog(mainPane.getScene().getWindow());
        if (fileList != null)
        {
            copyFiles(fileList);

            luceneController.deleteIndexDir();
            luceneController.createIndex();
        }
    }

    private void copyFiles(List<File> fileList)
    {
        for (File file : fileList)
        {
            Path sourcePath = Paths.get(file.toURI());
            Path targetPath = Paths.get(new File(LuceneController.DATA_DIR + File.separator + file.getName()).toURI());
            System.out.println("File: " + file.getName());
            System.out.println("Source: " + sourcePath.getFileName());
            System.out.println("Target: " + targetPath.getFileName());
            try
            {
                Files.copy(sourcePath, targetPath);
            }
            catch (FileAlreadyExistsException fileAlreadyExistsException)
            {
                fileExistsAlert(sourcePath);
            }
            catch (IOException ioException)
            {
                fileIOAlert(sourcePath);
            }
        }
    }

    private void fileIOAlert(Path sourcePath)
    {
        Alert fileFiledAlert = new Alert(Alert.AlertType.ERROR);
        fileFiledAlert.setTitle("IO Error");
        fileFiledAlert.setHeaderText("File : " + sourcePath.getFileName());
        fileFiledAlert.setContentText("Something happened with this file " + sourcePath.getFileName() + "Try " +
                "again");
        fileFiledAlert.initOwner(mainPane.getScene().getWindow());
        fileFiledAlert.initModality(Modality.APPLICATION_MODAL);
        fileFiledAlert.showAndWait();
    }

    private void fileExistsAlert(Path sourcePath)
    {
        Alert fileExists = new Alert(Alert.AlertType.ERROR);
        fileExists.setTitle("File Already Exists");
        fileExists.setHeaderText("File : " + sourcePath.getFileName());
        fileExists.setContentText("This : " + sourcePath.getFileName() + " file already exists");
        fileExists.initOwner(mainPane.getScene().getWindow());
        fileExists.initModality(Modality.APPLICATION_MODAL);

        Optional<ButtonType> options = fileExists.showAndWait();
        if (options.isPresent() && options.get() == ButtonType.OK)
        {
            System.out.println("This file already exists");
        }
    }

    @FXML
    public void deleteFiles(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Delete Files");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt", "*.txt"));
        fileChooser.setInitialDirectory(new File(LuceneController.DATA_DIR));

        List<File> fileList = fileChooser.showOpenMultipleDialog(mainPane.getScene().getWindow());
        if (fileList != null)
        {
            deleteFilesIfExists(fileList);
            luceneController.deleteIndexDir();
            luceneController.createIndex();
        }
    }

    private void deleteFilesIfExists(List<File> fileList)
    {
        for (File file : fileList)
        {
            Path sourcePath = Paths.get(file.toURI());
            System.out.println("File: " + file.getName());
            System.out.println("Source: " + sourcePath.getFileName());
            try
            {
                Files.deleteIfExists(sourcePath);
            }
            catch (IOException ioException)
            {
                fileIOAlert(sourcePath);
            }
        }
    }

    @FXML
    public void editFileDialog(ActionEvent event)
    {
        try
        {
            File editArticleFile = new File("src/main/resources/gr.uop.tresa/EditArticleDialog.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(editArticleFile.toURI().toURL());
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage editStage = new Stage();

            editStage.setTitle("Edit File");
            editStage.initModality(Modality.APPLICATION_MODAL);
            editStage.initOwner(mainPane.getScene().getWindow());
            editStage.setScene(scene);
            editStage.show();
            editStage.setMinHeight(400.0);
            editStage.setMinWidth(400.0);
        }
        catch (IOException ioException)
        {
            System.out.println(ioException.getMessage());
        }

    }

    @FXML
    public void close(ActionEvent event)
    {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        if (closeRequest(stage))
        {
            stage.close();
        }
    }

    public boolean close(Stage stage)
    {
        return closeRequest(stage);
    }

    private boolean closeRequest(Stage stage)
    {
        if (!searchText.getText().trim().isEmpty())
        {
            Alert confirmExitAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmExitAlert.setTitle("Confirm");
            confirmExitAlert.setHeaderText("Exit?");
            confirmExitAlert.initOwner(stage.getOwner());
            confirmExitAlert.initModality(Modality.APPLICATION_MODAL);
            Optional<ButtonType> result = confirmExitAlert.showAndWait();
            if (result.isPresent())
            {
                if (result.get() == ButtonType.OK)
                {
                    return true;
                }
                else
                {
                    confirmExitAlert.close();
                    return false;
                }
            }
        }
        return true;
    }

    @FXML
    public void aboutUs(ActionEvent event)
    {

    }
}