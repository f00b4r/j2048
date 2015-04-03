package cz.jfx.j2048.control;

import cz.jfx.j2048.app.Application;
import cz.jfx.j2048.gui.TileGrid;
import cz.jfx.j2048.data.Game;
import cz.jfx.j2048.data.GameState;
import cz.jfx.j2048.service.PersistenceService;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

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

    // Vars - services
    private final PersistenceService persistenceService;

    // FXML
    private Scene scene;
    @FXML
    private TileGrid grid;
    @FXML
    private Label currentScore;
    @FXML
    private Label bestScore;

    public MainControl(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void init(Scene scene) {
        this.scene = scene;

        // Create game
        game = new Game(grid);

        // Bindings: listen movings event
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            // Check grid state
            if (!game.isRunning()) {
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
                default:
                    // Disable other keys
                    return;
            }

            // Generate next seed
            game.rand(GRID_RANDOM);

            // Consume this event
            event.consume();
        });

        // Bindings: Other keyboard event
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            switch (event.getCode()) {
                case N:
                    // New game
                    if (event.isControlDown()) {
                        actionNewGame();
                        return;
                    }
                    break;
                case F1:
                    // About
                    actionAbout();
                    break;
            }

            // Consume this event
            event.consume();
        });

        // Bindings: game state changes
        game.stateProperty().addListener((obs, ov, nv) -> {
            if (nv == GameState.WIN) {
                // Show dialog
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Victory!");
                alert.setContentText("Hey man! You win, excellent job.");
                alert.showAndWait();
            } else if (nv == GameState.LOSE) {
                // Show dialog
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Lose!");
                alert.setContentText("That's a pity. Try again.");
                alert.showAndWait();
            }
        });

        // Bindigs: game score
        game.scoreProperty().addListener((obs, ov, nv) -> {
            // Update label
            currentScore.setText(nv.toString());

            // Store score
            if (persistenceService.bestScoreProperty().get() < nv.intValue()) {
                persistenceService.bestScoreProperty().set(nv.intValue());
                persistenceService.save();
            }
        });

        // Bindings: best score
        bestScore.textProperty().bind(persistenceService.bestScoreProperty().asString());

        // Init grid
        game.init(GRID_ROWS, GRID_COLS);

        // Ready to play
        actionNewGame();
    }

    /**
     * *************************************************************************
     * GUI ACTIONS *************************************************************
     * *************************************************************************
     */
    @FXML
    private void actionNewGame() {
        game.reset();
        game.play();
        game.rand(GRID_RANDOM*2);
    }

    @FXML
    private void actionAbout() {
        Dialog dialog = new Dialog();
        dialog.initOwner(scene.getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("About");
        dialog.setHeaderText("j2048");

        String content = new Scanner(getClass().getResourceAsStream("/cz/jfx/j2048/resources/about.txt")).useDelimiter("\\A").next();
        content = content.replace("$$VERSION$$", Application.VERSION);
        content = content.replace("$$RELEASED$$", Application.RELEASED);
        dialog.setContentText(content);

        ButtonType loginButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(loginButtonType);
        dialog.show();
    }
}
