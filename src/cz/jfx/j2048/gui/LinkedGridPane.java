package cz.jfx.j2048.gui;

import java.util.stream.Stream;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Felix
 */
public class LinkedGridPane extends GridPane {

    /**
     * @param rows int
     * @param cols int
     */
    public void fill(final int rows, final int cols) {
        // Fill grid by empty nodes
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                add(new GridNode(row, col), row, col);
            }
        }
    }

    /**
     * @param node AbstractNode
     * @param rowIndex int
     * @param columnIndex int
     */
    public void add(GridNode node, int rowIndex, int columnIndex) {
        // Set references
        node.setTop(getNodeByRowColumnIndex(rowIndex - 1, columnIndex));
        node.setRight(getNodeByRowColumnIndex(rowIndex, columnIndex + 1));
        node.setBottom(getNodeByRowColumnIndex(rowIndex + 1, columnIndex));
        node.setLeft(getNodeByRowColumnIndex(rowIndex, columnIndex - 1));

        // Add to grid
        super.add(node, columnIndex, rowIndex);
    }

    /**
     * @param row int
     * @param column int
     * @return GridNode
     */
    public GridNode getNodeByRowColumnIndex(final int row, final int column) {
        GridNode result = null;
        for (Node node : getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                result = (GridNode) node;
                break;
            }
        }
        return result;
    }

    public Stream<GridNode> getNodesStream() {
        return getChildren().stream().map((n) -> (GridNode) n);
    }
}
