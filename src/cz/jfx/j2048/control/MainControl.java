package cz.jfx.j2048.control;

import cz.jfx.j2048.gui.GridNode;
import cz.jfx.j2048.gui.LinkedGridPane;
import cz.jfx.j2048.gui.data.Game;
import cz.jfx.j2048.gui.data.GameState;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

/**
 *
 * @author Felix
 */
public class MainControl implements Initializable {

    // Constants
    private static final int GRID_COLS = 4;
    private static final int GRID_ROWS = 4;
    private static final int GRID_RANDOM = 1;

    // Vars
    private Game game;

    // FXML
    private Scene scene;
    @FXML
    private LinkedGridPane grid;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void init(Scene scene) {
        this.scene = scene;

        // Create game
        game = new Game(grid);

        // Attach keyboard event
        scene.addEventHandler(KeyEvent.KEY_RELEASED, (event) -> {
            // Check grid state
            if (game.stateProperty().get() != GameState.PLAY) {
                return;
            }

            switch (event.getCode()) {
                case UP:
                    game.moveUp();
                    break;
                case RIGHT:
                    game.moveRight();
                    break;
                case DOWN:
                    game.moveDown();
                    break;
                case LEFT:
                    game.moveLeft();
                    break;
                case N:
                    if (event.isControlDown()) {
                        game.reset();
                        game.play();
                        game.rand(GRID_RANDOM);
                        game.rand(GRID_RANDOM);
                        return;
                    }
                    break;
                default:
                    // Disable ether keys
                    return;
            }

            // Generate next seed
            game.rand(GRID_RANDOM);

            // Consume this event
            event.consume();
        });

        // Attach game state 
        game.stateProperty().addListener((obs, ov, nw) -> {
            if (nw == GameState.WIN) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Victory!");
                alert.setContentText("Hey man! You win, excellent job.");
                alert.showAndWait();
            } else if (nw == GameState.LOSE) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Lose!");
                alert.setContentText("Thats a pity. Try it again.");
                alert.showAndWait();
            }
        });

        // Init grid
        game.init(GRID_ROWS, GRID_COLS);

        game.rand(GRID_RANDOM);
        game.rand(GRID_RANDOM);
        // @todo
        GridNode n;
        n = (GridNode) grid.getNodeByRowColumnIndex(3, 0);
        n.valueProperty().set(4);
        n = (GridNode) grid.getNodeByRowColumnIndex(3, 1);
        n.valueProperty().set(2);
        n = (GridNode) grid.getNodeByRowColumnIndex(3, 2);
        n.valueProperty().set(2);
        n = grid.getNodeByRowColumnIndex(3, 3);
        n.valueProperty().set(2);

        ScaleTransition st = new ScaleTransition(Duration.millis(500), n);
        st.setByX(1.5);
        st.setByY(1.5);
        st.setCycleCount(4);
        st.setAutoReverse(true);

        st.play();
    }
}
