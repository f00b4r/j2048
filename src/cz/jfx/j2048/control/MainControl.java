package cz.jfx.j2048.control;

import cz.jfx.j2048.app.Application;
import cz.jfx.j2048.gui.TileGrid;
import cz.jfx.j2048.data.Game;
import cz.jfx.j2048.data.GameState;
import cz.jfx.j2048.service.PersistenceService;
import cz.jfx.j2048.service.RankService;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javax.imageio.ImageIO;

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
    private final RankService rankService;

    // FXML
    private Scene scene;
    @FXML
    private AnchorPane root;
    @FXML
    private TileGrid grid;
    @FXML
    private Label currentScore;
    @FXML
    private Label bestScore;
    @FXML
    private Pane logo;

    public MainControl(PersistenceService persistenceService, RankService rankService) {
        this.persistenceService = persistenceService;
        this.rankService = rankService;
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
                    }
                    break;
                case F5:
                    // New game
                    actionNewGame();
                    break;
                case F10:
                    // Screenshot
                    actionScreenshot();
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
                // Share score
                rankService.publish(game.scoreProperty().get());

                // Show dialog
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Victory!");
                alert.setContentText("Hey man! You win, excellent job.");
                alert.showAndWait();
            } else if (nv == GameState.LOSE) {
                // Share score
                rankService.publish(game.scoreProperty().get());

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

        // Bindings: logo show/hide
        logo.visibleProperty().bind(scene.widthProperty().greaterThan(650));

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
        // Increase games in this instance
        persistenceService.instanceGamesProperty().set(persistenceService.instanceGamesProperty().get() + 1);
        // Increase total games 
        persistenceService.totalGamesProperty().set(persistenceService.totalGamesProperty().get() + 1);

        // Reset & play
        game.reset();
        game.play();
        game.rand(GRID_RANDOM * 2);
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

    @FXML
    private void actionScreenshot() {
        WritableImage image = root.snapshot(new SnapshotParameters(), null);

        FileChooser chooser = new FileChooser();

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image (.png)", "png");
        chooser.getExtensionFilters().add(filter);
        chooser.setSelectedExtensionFilter(filter);
        chooser.setInitialDirectory(new File("."));
        chooser.setInitialFileName("snapshot.png");

        File file = chooser.showSaveDialog(scene.getWindow());

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Snapshop generated.");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Application can not generate snapshot.");
            alert.showAndWait();
        }
    }

}
