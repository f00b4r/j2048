package cz.jfx.j2048.data;

import cz.jfx.j2048.gui.Tile;
import cz.jfx.j2048.gui.TileGrid;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Felix
 */
public class Game {

    // Vars - final
    private final TileGrid grid;

    // Vars - properties
    private final IntegerProperty changes;
    private final ObjectProperty<Dimension> dimension;
    private final ObjectProperty<GameState> state;
    private final IntegerProperty score;
    private final IntegerProperty moves;

    public Game(TileGrid grid) {
        this.grid = grid;
        this.changes = new SimpleIntegerProperty();

        this.dimension = new SimpleObjectProperty<>();
        this.score = new SimpleIntegerProperty();
        this.moves = new SimpleIntegerProperty();

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

    public ObjectProperty<Dimension> dimensionProperty() {
        return dimension;
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty movesProperty() {
        return score;
    }

    /**
     * *************************************************************************
     * MANIPULATION ************************************************************
     * *************************************************************************
     */
    protected void init() {
        // Reset grid (refill with nodes)
        reset();

        // Invoke game to play
        play();
    }

    public final void init(int rows, int cols) {
        init(new Dimension(rows, cols));
    }

    public final void init(Dimension dimension) {
        this.dimension.set(dimension);
        init();
    }

    public final void reset() {
        // Set fresh
        state.set(GameState.FRESH);

        // Reset last moves
        changes.set(1);

        // Reset moves 
        moves.set(0);

        // Reset score
        score.set(0);

        // Rr-fill grid by empty nodes
        Dimension d = dimension.get();
        grid.refill(d.getRows(), d.getCols());

        // Bindings: listen to node changes
        grid.getNodesStream().forEach((n) -> {
            n.mergedProperty().addListener((obs, ov, nw) -> {
                // Only if merged==true
                if (nw) {
                    score.set(score.get() + n.getValue());
                }
            });
        });

    }

    public final void play() {
        // Let's PLAY!
        state.set(GameState.PLAY);
    }

    /**
     * @param count int
     */
    public final void rand(int count) {
        // Break if game is not plaing..
        if (state.get() != GameState.PLAY) {
            return;
        }

        // Get empty nodes
        List<Tile> tiles = grid.getNodesStream()
                .filter((n) -> n.isEmpty())
                .collect(Collectors.toList());

        // Check if is there any place to add new nodes
        if (tiles.size() <= 0) {
            // No available nodes..
            state.set(GameState.HOLD);
            return;
        }

        // Check if there was moved in last seed..
        if (changes.get() <= 0) {
            // No moves
            state.set(GameState.HOLD);
            return;
        }

        // Random tiles
        Collections.shuffle(tiles);

        // Create random with unique seed
        Random r = new Random(System.currentTimeMillis() - tiles.size() + changes.get());

        while (count > 0) {

            // Pick node, if only one left pick this one, otherwise
            // pick random node
            Tile n;
            if (tiles.size() == 1) {
                n = tiles.get(0);
            } else {
                n = tiles.get(r.nextInt(tiles.size() - 1));
            }

            // Randomize tile, if failed, do it again
            if (n.doRand()) {
                count--;
            }
        }

        // Check game options - user win? user lose?
        check();
    }

    public final void moveUp() {
        move(Direction.TOP);
    }

    public final void moveRight() {
        move(Direction.RIGHT);
    }

    public final void moveDown() {
        move(Direction.BOTTOM);
    }

    public final void moveLeft() {
        move(Direction.LEFT);
    }

    protected void move(Direction direction) {
        // Update game state
        state.set(GameState.PLAY);

        // Clear last moves
        changes.set(0);

        // Get nodes stream
        List<Tile> nodes = grid.getNodesStream().collect(Collectors.toList());
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

        // Check game options - user win? user lose?
        check();
    }

    public void check() {
        // Check if user WIN
        // #1 - Is there any node with MAX_VALUE
        if (grid.getNodesStream()
                .filter((n) -> n.valueProperty().get() >= TileValue.MAX_VALUE)
                .count() > 0) {
            // Update game state
            state.set(GameState.WIN);
            return;
        }

        // Check if user LOSE
        // #1 - If there are not any available tiles
        long availables = grid.getNodesStream().filter((n) -> n.isEmpty()).count();
        if (availables <= 0) {
            // #2 - And there are not any two tiles that can be merged
            long mergeables = grid.getNodesStream().filter((n) -> n.isMergeable()).count();
            if (mergeables <= 0) {
                // Game ends
                state.set(GameState.LOSE);
            }
        }
    }

    public boolean isRunning() {
        GameState s = state.get();
        return s == GameState.PLAY || s == GameState.HOLD;
    }
}
