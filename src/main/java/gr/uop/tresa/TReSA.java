package gr.uop.tresa;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TReSA extends Application
{
    private static final double MIN_HEIGHT = 400.0;
    private static final double MIN_WIDTH = 400.0;
    private FXController fxController;

    @Override
    public void start(Stage primaryStage) throws IOException
    {
        File file = new File("src/main/resources/gr.uop.tresa/TReSABase.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(file.toURI().toURL());
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("TReSA");
        primaryStage.setScene(scene);
        primaryStage.show();
        fxController = fxmlLoader.getController();

        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setOnCloseRequest(windowEvent ->
        {
            if (fxController.close(primaryStage))
                primaryStage.close();
            else
                windowEvent.consume();
        });
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}