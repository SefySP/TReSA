package gr.uop.tresa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import gr.uop.ArticleData;
import gr.uop.lucene.LuceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditArticleController implements Initializable
{
    @FXML
    private BorderPane editFilePane;

    @FXML
    private TextArea bodyText;

    @FXML
    private Label filePathText;

    @FXML
    private TextField peopleText;

    @FXML
    private TextField placesText;

    @FXML
    private TextField titleText;

    @FXML
    private Button applyButton;

    private ArticleData articleData;
    private LuceneController luceneController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        luceneController = new LuceneController();
    }

    @FXML
    public void openFile(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Delete Files");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt","*.txt"));
        fileChooser.setInitialDirectory(new File(LuceneController.DATA_DIR));

        File file = fileChooser.showOpenDialog(editFilePane.getScene().getWindow());

        if (file != null)
        {
            placesText.setDisable(false);
            peopleText.setDisable(false);
            titleText.setDisable(false);
            bodyText.setDisable(false);
            applyButton.setDisable(false);

            try (BufferedReader br = new BufferedReader(new FileReader(file)))
            {
                String currentLine;
                StringBuilder stringBuilder = new StringBuilder();
                while ((currentLine = br.readLine()) != null)
                {
                    stringBuilder.append(currentLine);
                }

                articleData = new ArticleData(file.getName(), file.getCanonicalPath());
                articleData.setPlaces(stringBuilder);
                articleData.setPeople(stringBuilder);
                articleData.setTitle(stringBuilder);
                articleData.setBody(stringBuilder);
            }
            catch (IOException ioException)
            {
                System.out.println(ioException.getMessage());
            }

            placesText.setText(articleData.getPlaces());
            peopleText.setText(articleData.getPeople());
            titleText.setText(articleData.getTitle());
            bodyText.setText(articleData.getBody());
            filePathText.setText(articleData.getFileName());
        }
        else
        {
            placesText.setDisable(true);
            peopleText.setDisable(true);
            titleText.setDisable(true);
            bodyText.setDisable(true);
            applyButton.setDisable(true);

            placesText.setText("");
            peopleText.setText("");
            titleText.setText("");
            bodyText.setText("");
            filePathText.setText("file");
        }

    }

    @FXML
    public void applyEdit(ActionEvent event)
    {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(articleData.getFilePath(), false)))
        {
            bw.write("<PLACES>" + placesText.getText() + "</PLACES>\n");
            bw.write("<PEOPLE>" + peopleText.getText() + "</PEOPLE>\n");
            bw.write("<TITLE>" + titleText.getText() + "</TITLE>\n");
            bw.write("<BODY>" + bodyText.getText() + "</BODY>");
            bw.flush();
        }
        catch (IOException ioException)
        {
            System.out.println(ioException.getMessage());
        }
        luceneController.deleteIndexDir();
        luceneController.createIndex();

/*
        Alert successEditAlert = new Alert(Alert.AlertType.INFORMATION);
        successEditAlert.initOwner(editFilePane.getScene().getWindow());
        successEditAlert.initModality(Modality.APPLICATION_MODAL);
        successEditAlert.showAndWait();
*/

        Stage stage = (Stage) editFilePane.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancelEdit(ActionEvent event)
    {
        if (articleData == null)
        {
            Stage stage = (Stage) editFilePane.getScene().getWindow();
            stage.close();
            return;
        }
        if (!placesText.getText().equalsIgnoreCase(articleData.getPlaces()) ||
            !peopleText.getText().equalsIgnoreCase(articleData.getPeople()) ||
            !titleText.getText().equalsIgnoreCase(articleData.getTitle())   ||
            !bodyText.getText().equalsIgnoreCase(articleData.getBody()))
        {
            Alert confirmEditAlert = new Alert(Alert.AlertType.WARNING);
            confirmEditAlert.setHeaderText("Confirm edit?");
            confirmEditAlert.initOwner(editFilePane.getScene().getWindow());
            confirmEditAlert.initModality(Modality.APPLICATION_MODAL);
            confirmEditAlert.getButtonTypes().add(ButtonType.CLOSE);
            Optional<ButtonType> result = confirmEditAlert.showAndWait();
            if (result.isPresent())
            {
                if (result.get() == ButtonType.OK)
                {
                    Stage stage = (Stage) editFilePane.getScene().getWindow();
                    stage.close();
                }
                else
                {
                    confirmEditAlert.close();
                }
            }
        }
    }
}