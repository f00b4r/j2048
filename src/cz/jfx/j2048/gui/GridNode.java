package cz.jfx.j2048.gui;

import cz.jfx.j2048.gui.data.Location;
import cz.jfx.j2048.gui.data.Direction;
import cz.jfx.j2048.gui.data.NodeValue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

/**
 *
 * @author Felix
 */
public class GridNode extends Label {

    // Constants
    private static final int MIN_VALUE = 2;
    private static final int MAX_VALUE = 2048;

    // Vars
    private GridNode left;
    private GridNode top;
    private GridNode right;
    private GridNode bottom;

    // Vars - final
    private final Location location;

    // Vars - properties
    private final NodeValue value;
    private final BooleanProperty modified;
    private final BooleanProperty merged;

    public GridNode(Location location) {
        this.location = location;
        this.value = new NodeValue();
        this.modified = new SimpleBooleanProperty();
        this.merged = new SimpleBooleanProperty();

        // Bind value => class
        this.value.addListener((obs, ov, nv) -> {
            // Clear current classes
            this.getStyleClass().clear();

            // Add base class
            this.getStyleClass().add("node");

            // Add empty class or class according to value
            if (nv.intValue() == 0) {
                this.getStyleClass().add("node-empty");
            } else {
                this.getStyleClass().add("node-" + nv.toString());
            }

            // Set text
            if (nv.intValue() == 0) {
                this.textProperty().set("");
            } else {
                this.textProperty().set(nv.toString());
            }

        });

        // Add styles
        this.getStyleClass().add("node");
        this.getStyleClass().add("node-empty");

        // Basic settings
        this.setPrefSize(150, 150);
        this.setAlignment(Pos.CENTER);
        this.setFont(new Font("System Bold", 40));
    }

    public GridNode(int rowIndex, int colIndex) {
        this(new Location(rowIndex, colIndex));
    }

    /**
     * *************************************************************************
     * GETTERS/SETTERS *********************************************************
     * *************************************************************************
     */
    public Location getLocation() {
        return location;
    }

    /**
     * *************************************************************************
     * LINK-MODEL **************************************************************
     * *************************************************************************
     */
    public GridNode getLeft() {
        return left;
    }

    public void setLeft(GridNode left) {
        this.left = left;
        // Set LEFT node REF to this node on RIGHT
        if (left != null && (left.getRight() == null || !left.getRight().equals(this))) {
            left.setRight(this);
        }
    }

    public GridNode getTop() {
        return top;
    }

    public void setTop(GridNode top) {
        this.top = top;
        // Set TOP node REF to this node on BOTTOM
        if (top != null && (top.getBottom() == null || !top.getBottom().equals(this))) {
            top.setBottom(this);
        }
    }

    public GridNode getRight() {
        return right;
    }

    public void setRight(GridNode right) {
        this.right = right;
        // Set RIGHT node REF to this node on LEFT
        if (right != null && (right.getLeft() == null || !right.getLeft().equals(this))) {
            right.setLeft(this);
        }
    }

    public GridNode getBottom() {
        return bottom;
    }

    public void setBottom(GridNode bottom) {
        this.bottom = bottom;
        // Set BOTTOM node REF to this node on TOP
        if (bottom != null && (bottom.getTop() == null || !bottom.getTop().equals(this))) {
            bottom.setTop(this);
        }
    }

    /**
     * *************************************************************************
     * PROPERTIES **************************************************************
     * *************************************************************************
     */
    public NodeValue valueProperty() {
        return value;
    }

    /**
     * *************************************************************************
     * MOVING ******************************************************************
     * *************************************************************************
     */
    public void doBeforeMove() {
    }

    public boolean doMove(Direction direction) {
        // If this value is empty, stop moving..
        if (isEmpty()) {
            return false;
        }

        // Find node in requested direction
        GridNode requestNode = null;
        switch (direction) {
            case TOP:
                requestNode = top;
                break;
            case RIGHT:
                requestNode = right;
                break;
            case BOTTOM:
                requestNode = bottom;
                break;
            case LEFT:
                requestNode = left;
                break;
        }

        // Provide move only if requested node is not null
        if (requestNode != null) {

            // Accept requested node move from this node
            boolean acceptMoving = requestNode.acceptNode(this);
            if (acceptMoving) {
                // Recalculate next node value
                requestNode.doRecalculate(this);

                // Erase actual value
                value.empty();

                // Set modified
                modified.set(true);
            }

            // Do recursively moving to requested node
            boolean recursionMove = requestNode.doMove(direction);

            // If requested node does not accept node, lets
            // try it after moving again. Otherwise return result
            // of moving.
            if (!acceptMoving && recursionMove) {
                return this.doMove(direction);
            } else {
                return acceptMoving;
            }
        }

        return false;
    }

    public void doAfterMove() {
        // Clean field
        modified.set(false);
        merged.set(false);
    }

    public boolean acceptNode(GridNode node) {
        int v = value.get();
        return (v <= 0 || (v == node.valueProperty().get() && !merged.get() && !node.merged.get()));
    }

    public void doRecalculate(GridNode node) {
        // Set merged
        merged.set(value.get() > 0);
        // Set modified
        modified.set(true);
        // Merge values
        value.set(value.get() + node.valueProperty().get());
    }

    public boolean doRand() {
        int v = value.get();

        if (v <= 2) {
            value.set(v + MIN_VALUE);
            return true;
        }

        return false;
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public boolean isModified() {
        return modified.get();
    }

    public boolean isMerged() {
        return merged.get();
    }

    @Override
    public String toString() {
        return "GridNode{" + "location=" + location + ", value=" + value + ", modified=" + modified.get() + ", merged=" + merged.get() + '}';
    }

}
