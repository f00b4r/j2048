package cz.jfx.j2048.app;

import cz.jfx.j2048.control.MainControl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author Felix
 */
public class Application extends javafx.application.Application {

    // Constants
    public static final String VERSION = "1.1";
    public static final String RELEASED = "27-04-2015";

    // Vars
    private ApplicationContainer container;

    @Override
    public void stop() throws Exception {
    }

    @Override
    public void init() throws Exception {
        // Load fonts
        Font.loadFont(getClass().getResource("/cz/jfx/j2048/resources/fonts/ClearSans-Bold.ttf").toExternalForm(), 10.0);

        // Load services
        container = new ApplicationContainer();

        // Load properties
        container.getPersistenceService().load();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Create loader
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cz/jfx/j2048/resources/Main.fxml"));
        MainControl controller = container.createMainControl();
        loader.setController(controller);

        // Create scene
        Scene scene = new Scene((Parent) loader.load());
        controller.init(scene);

        // Add icons
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/cz/jfx/j2048/resources/images/icon-16.png")));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/cz/jfx/j2048/resources/images/icon-32.png")));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/cz/jfx/j2048/resources/images/icon-64.png")));

        // Set on close
        stage.setOnCloseRequest((e) -> {
            container.getPersistenceService().save();
        });
        
        // Configure stage
        stage.setMinWidth(450);
        stage.setMinHeight(450);
        stage.setTitle("j2048 [" + VERSION + "]");
        stage.setScene(scene);
        stage.show();
    }

}
