package gr.uop.tresa;

import java.net.URL;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
    }

    @FXML
    void defaultSearch(ActionEvent event)
    {
        searchResults.setText(searchText.getText());
        System.out.println(searchText.getText());
    }
}