package anbaximeiza.memo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static Scene scene;

    /**
     * Loads the FXML file and returns the associated node. The method expects that
     * the file is
     * located in "src/main/resources/fxml".
     *
     * @param fxml the name of the FXML file (without extension)
     * @return the root node of the FXML file
     * @throws IOException if the FXML file is not found
     */
    public static Parent loadFxml(final String fxml) throws IOException {
        return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
    }

    /**
     * This method is invoked when the application starts. It loads and shows the
     * "room" scene.
     *
     * @param stage the primary stage of the application
     * @throws IOException if the "src/main/resources/fxml/room.fxml" file is not
     *                     found
     */
    @Override
    public void start(final Stage stage) throws IOException {
        Parent root = loadFxml("mainNav");
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        root.requestFocus();
    }

    /**
     * Sets the root of the scene to the specified FXML file.
     *
     * @param fxml the name of the FXML file (without extension)
     * @throws IOException if the FXML file is not found
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFxml(fxml));
    }

    public static void main(final String[] args) {
        launch();
    }

}