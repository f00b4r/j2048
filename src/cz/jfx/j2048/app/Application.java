package cz.jfx.j2048.app;

import cz.jfx.j2048.control.MainControl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.swing.Icon;

/**
 *
 * @author Felix
 */
public class Application extends javafx.application.Application {

    @Override
    public void stop() throws Exception {
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Create loader
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cz/jfx/j2048/resources/Main.fxml"));
        MainControl controller = new MainControl();
        loader.setController(controller);

        // Create scene
        Scene scene = new Scene((Parent) loader.load());
        controller.init(scene);
        
        // Add icons
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/cz/jfx/j2048/resources/images/icon-16.png")));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/cz/jfx/j2048/resources/images/icon-32.png")));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/cz/jfx/j2048/resources/images/icon-64.png")));
        
        // Configure stage
        stage.setTitle("j2048");
        stage.setScene(scene);
        stage.show();
    }

}
