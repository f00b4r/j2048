package cz.jfx.j2048.gui;

import java.util.List;
import java.util.stream.Stream;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

/**
 *
 * @author Felix
 */
public class TileGrid extends GridPane {

    /**
     * @param rows int
     * @param cols int
     */
    public void fill(final int rows, final int cols) {
        // Fill grid by empty nodes
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                add(new Tile(row, col), row, col);
            }
        }
    }

    /**
     * @param rows int
     * @param cols int
     */
    public void refill(final int rows, final int cols) {
        // Clear grid
        getChildren().clear();

        // Refill
        fill(rows, cols);
    }

    /**
     * @param node AbstractNode
     * @param rowIndex int
     * @param columnIndex int
     */
    public void add(Tile node, int rowIndex, int columnIndex) {
        // Set references
        node.setTop(getNodeByRowColumnIndex(rowIndex - 1, columnIndex));
        node.setRight(getNodeByRowColumnIndex(rowIndex, columnIndex + 1));
        node.setBottom(getNodeByRowColumnIndex(rowIndex + 1, columnIndex));
        node.setLeft(getNodeByRowColumnIndex(rowIndex, columnIndex - 1));

        // Binding: If node was randomized, do some effect
        node.randomizedProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
                st.setFromX(0.5);
                st.setToX(1);
                st.setFromY(0.5);
                st.setToY(1);

                FadeTransition ft = new FadeTransition(Duration.millis(300), node);
                ft.setFromValue(0.1);
                ft.setToValue(1);

                ParallelTransition pt = new ParallelTransition(st, ft);
                pt.play();
            }
        });

        // Binding: If node was merge, do some effect
        node.mergedProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                RotateTransition rt;
                switch (node.valueProperty().get()) {
                    case 1024:
                        rt = new RotateTransition(Duration.millis(100), node);
                        rt.setByAngle(360);
                        rt.setCycleCount(1);
                        rt.play();
                        break;
                    case 2048:
                        rt = new RotateTransition(Duration.millis(100), node);
                        rt.setByAngle(360);
                        rt.setCycleCount(2);
                        rt.play();
                        break;
                }
            }
        });

        // Add to grid
        super.add(node, columnIndex, rowIndex);
    }

    /**
     * @param row int
     * @param column int
     * @return GridNode
     */
    public Tile getNodeByRowColumnIndex(final int row, final int column) {
        Tile result = null;
        for (Node node : getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                result = (Tile) node;
                break;
            }
        }
        return result;
    }

    public Stream<Tile> getNodesStream() {
        return getChildren().stream().map((n) -> (Tile) n);
    }

    public List<Node> getNodes() {
        return getChildren();
    }
}
