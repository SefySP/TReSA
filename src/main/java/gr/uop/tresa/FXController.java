package gr.uop.tresa;

import org.apache.lucene.queryparser.classic.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import gr.uop.lucene.LuceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class FXController implements Initializable
{

    @FXML
    private TextField searchText;

    @FXML
    private Text searchResults;

    private LuceneController luceneController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        luceneController = new LuceneController();
        try
        {
            Path indexPath = Paths.get(LuceneController.INDEX_DIR);
            if (!Files.exists(indexPath))
            {
                Files.createDirectory(indexPath);
                luceneController.createIndex();
            }
            else
            {
                File index = new File(indexPath.toString());
                String[] entries = index.list();
                if (entries != null && entries.length == 0)
                {
                    luceneController.createIndex();
                }
            }
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
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
}