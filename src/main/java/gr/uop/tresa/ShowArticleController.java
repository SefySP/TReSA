package gr.uop.tresa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import gr.uop.ArticleData;
import gr.uop.FileHolder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ShowArticleController implements Initializable
{
    @FXML
    private TextArea bodyText;

    @FXML
    private Text peopleText;

    @FXML
    private Text placesText;

    @FXML
    private Text titleText;

    private File file;

    private ArticleData articleData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        FileHolder holder = FileHolder.getInstance();
        file = holder.getFile();
        System.out.println(file.getName());

        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String currentLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((currentLine = br.readLine()) != null)
            {
                stringBuilder.append(currentLine);
            }

            initializeArticleData(file, stringBuilder);
        }
        catch (IOException ioException)
        {
            System.out.println(ioException.getMessage());
        }

        setTexts();
    }

    private void initializeArticleData(File file, StringBuilder stringBuilder) throws IOException
    {
        articleData = new ArticleData(file.getName(), file.getCanonicalPath());
        articleData.setPlaces(stringBuilder);
        articleData.setPeople(stringBuilder);
        articleData.setTitle(stringBuilder);
        articleData.setBody(stringBuilder);
    }

    private void setTexts()
    {
        placesText.setText(articleData.getPlaces());
        peopleText.setText(articleData.getPeople());
        titleText.setText(articleData.getTitle());
        bodyText.setText(articleData.getBody());
    }

    @FXML
    void cancelButton(ActionEvent event)
    {
        Stage stage = (Stage) bodyText.getScene().getWindow();
        stage.close();
    }

}
