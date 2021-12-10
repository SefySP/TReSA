package gr.uop.tresa;

import org.apache.lucene.queryparser.classic.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import gr.uop.lucene.LuceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FXController implements Initializable
{
    @FXML
    private BorderPane mainPane;

    @FXML
    private TextField searchText;

    @FXML
    private Text searchResults;

    private LuceneController luceneController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        luceneController = new LuceneController();
        if (luceneController.indexDirExists() && luceneController.isIndexDirEmpty())
        {
            luceneController.createIndex();
        }
    }

    @FXML
    void defaultSearch(ActionEvent event)
    {
        try
        {
            String searchTerm = searchText.getText().trim();
            if (!searchTerm.isEmpty())
            {
                luceneController.search(searchTerm);
                searchResults.setText(luceneController.getTopResults());
            }
        }
        catch (IOException | ParseException exception)
        {
            exception.printStackTrace();
        }

    }

    @FXML
    public void addFiles(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Add Files");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt","*.txt"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));


        List<File> fileList = fileChooser.showOpenMultipleDialog(mainPane.getScene().getWindow());
        if (fileList != null)
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
                    System.out.println("already exists!!");
                }
                catch (IOException ioException)
                {
                    System.out.println(ioException.getMessage());
                }
            }

            luceneController.deleteIndexDir();
            luceneController.createIndex();
        }
    }

    @FXML
    public void deleteFiles(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Delete Files");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt","*.txt"));
        fileChooser.setInitialDirectory(new File(LuceneController.DATA_DIR));

        List<File> fileList = fileChooser.showOpenMultipleDialog(mainPane.getScene().getWindow());
        if (fileList != null)
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
                    System.out.println(ioException.getMessage());
                }
            }
            luceneController.deleteIndexDir();
            luceneController.createIndex();
        }
    }

    @FXML
    public void close(ActionEvent event)
    {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void aboutUs(ActionEvent event)
    {

    }
}