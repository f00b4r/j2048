package cz.jfx.j2048.gui.data;

import cz.jfx.j2048.gui.GridNode;
import cz.jfx.j2048.gui.LinkedGridPane;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

/**
 *
 * @author Felix
 */
public class Game {

    // Vars - final
    private final LinkedGridPane grid;

    // Vars - properties
    private final IntegerProperty changes;
    private final ObjectProperty<Dimension> dimension;
    private final ObjectProperty<GameState> state;

    public Game(LinkedGridPane grid) {
        this.grid = grid;
        this.changes = new SimpleIntegerProperty();

        this.dimension = new SimpleObjectProperty<>();
        this.dimension.set(new Dimension(4, 4));

        this.state = new SimpleObjectProperty<>();
        this.state.set(GameState.FRESH);
    }

    /**
     * *************************************************************************
     * PROPERTIES **************************************************************
     * *************************************************************************
     */
    public ObjectProperty<GameState> stateProperty() {
        return state;
    }

    public IntegerProperty changesProperty() {
        return changes;
    }

    public ObjectProperty<Dimension> dimensionProperty() {
        return dimension;
    }

    /**
     * *************************************************************************
     * MANIPULATION ************************************************************
     * *************************************************************************
     */
    public void init() {
        reset();
        play();
    }

    public void init(int rows, int cols) {
        init(new Dimension(rows, cols));
    }

    public void init(Dimension dimension) {
        this.dimension.set(dimension);
        init();
    }

    public void reset() {
        // Set fresh
        state.set(GameState.FRESH);

        // Clear grid
        grid.getChildren().clear();

        // Reset last moves
        changes.setValue(1);

        // Fill grid by empty nodes
        Dimension d = dimension.get();
        grid.fill(d.getRows(), d.getCols());
    }

    public void play() {
        // Let's PLAY!
        state.set(GameState.PLAY);
    }

    /**
     * @param count int
     */
    public void rand(int count) {
        // Break if game is not plaing..
        if (state.get() != GameState.PLAY) {
            return;
        }

        // Check if is there any place to add new nodes
        if (grid.getNodesStream().filter((n) -> n.isEmpty()).count() <= 0) {
            // No available nodes..
            return;
        }

        // Check if there was moved in last seed..
        if (changes.get() <= 0) {
            // No moves
            return;
        }

        Random r = new Random();
        Dimension d = dimension.get();
        while (count > 0) {
            // Randomize col& row
            int row = r.nextInt(d.getRows());
            int col = r.nextInt(d.getCols());

            // Find node
            GridNode n = grid.getNodeByRowColumnIndex(row, col);

            // Randomize node, if failed, do it again
            if (n.doRand()) {
                count--;
            }
        }
    }

    public void moveUp() {
        move(Direction.TOP);
    }

    public void moveRight() {
        move(Direction.RIGHT);
    }

    public void moveDown() {
        move(Direction.BOTTOM);
    }

    public void moveLeft() {
        move(Direction.LEFT);
    }

    protected void move(Direction direction) {
        // Clear last moves
        changes.set(0);

        // Get nodes stream
        List<GridNode> nodes = grid.getNodesStream().collect(Collectors.toList());

        // Reverse stream (nodes priority)
        if (direction == Direction.BOTTOM || direction == Direction.RIGHT) {
            Collections.reverse(nodes);
        }

        // Fire before move
        nodes.forEach((n) -> {
            n.doBeforeMove();
        });

        // Fire move
        nodes.forEach((n) -> {
            // Do move
            n.doMove(direction);

            // If node value vas modified increase moves
            if (n.isModified()) {
                changes.set(changes.get() + 1);
            }
        });

        // Fire after move
        nodes.forEach((n) -> {
            n.doAfterMove();
        });
    }
}
