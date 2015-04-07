package cz.jfx.j2048.gui;

import cz.jfx.j2048.data.Location;
import cz.jfx.j2048.data.Direction;
import cz.jfx.j2048.data.TileValue;
import java.util.Random;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

/**
 *
 * @author Felix
 */
public class Tile extends Label {

    // Vars
    private Tile left;
    private Tile top;
    private Tile right;
    private Tile bottom;

    // Vars - final
    private final Location location;

    // Vars - properties
    private final TileValue value;
    private final BooleanProperty modified;
    private final BooleanProperty merged;
    private final BooleanProperty randomized;

    public Tile(Location location) {
        this.location = location;
        this.value = new TileValue();
        this.modified = new SimpleBooleanProperty();
        this.merged = new SimpleBooleanProperty();
        this.randomized = new SimpleBooleanProperty();

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
        this.setMinSize(50, 50);
        this.setMaxSize(300, 300);
        this.setPrefSize(120, 120);
        this.setAlignment(Pos.CENTER);
    }

    public Tile(int rowIndex, int colIndex) {
        this(new Location(rowIndex, colIndex));
    }

    /**
     * *************************************************************************
     * LINK-MODEL **************************************************************
     * *************************************************************************
     */
    public Tile getLeft() {
        return left;
    }

    public void setLeft(Tile left) {
        this.left = left;
        // Set LEFT node REF to this node on RIGHT
        if (left != null && (left.getRight() == null || !left.getRight().equals(this))) {
            left.setRight(this);
        }
    }

    public Tile getTop() {
        return top;
    }

    public void setTop(Tile top) {
        this.top = top;
        // Set TOP node REF to this node on BOTTOM
        if (top != null && (top.getBottom() == null || !top.getBottom().equals(this))) {
            top.setBottom(this);
        }
    }

    public Tile getRight() {
        return right;
    }

    public void setRight(Tile right) {
        this.right = right;
        // Set RIGHT node REF to this node on LEFT
        if (right != null && (right.getLeft() == null || !right.getLeft().equals(this))) {
            right.setLeft(this);
        }
    }

    public Tile getBottom() {
        return bottom;
    }

    public void setBottom(Tile bottom) {
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
    public TileValue valueProperty() {
        return value;
    }

    public BooleanProperty modifiedProperty() {
        return modified;
    }

    public BooleanProperty mergedProperty() {
        return merged;
    }

    public BooleanProperty randomizedProperty() {
        return randomized;
    }

    /**
     * *************************************************************************
     * MOVING ******************************************************************
     * *************************************************************************
     */
    public void doBeforeMove() {
        // Clean field
        modified.set(false);
        merged.set(false);
        randomized.set(false);
    }

    public boolean doMove(Direction direction) {
        // If this value is empty, stop moving..
        if (isEmpty()) {
            return false;
        }

        // Find node in requested direction
        Tile requestNode = null;
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
    }

    public boolean acceptNode(Tile node) {
        int v = value.get();
        return (v <= 0 || (v == node.valueProperty().get() && !merged.get() && !node.merged.get()));
    }

    /**
     * *************************************************************************
     * RECALCULATION ***********************************************************
     * *************************************************************************
     */
    public void doRecalculate(Tile node) {
        // Merge values
        value.set(value.get() + node.valueProperty().get());
        // Set merged
        merged.set((value.get() - node.valueProperty().get()) > 0);
        // Set modified
        modified.set(true);
    }

    /**
     * *************************************************************************
     * RANDOMIZE ***************************************************************
     * *************************************************************************
     */
    public boolean doRand() {
        int v = value.get();

        if (v <= TileValue.EMPTY_VALUE) {
            if ((new Random()).nextDouble() > 0.90) {
                // Set double value 
                value.set(TileValue.MIN_VALUE * 2);
            } else {
                // Set value 
                value.set(TileValue.MIN_VALUE);
            }

            // Update state
            randomized.set(true);

            return true;
        }

        return false;
    }

    /**
     * *************************************************************************
     * HELPERS *****************************************************************
     * *************************************************************************
     */
    public boolean isEmpty() {
        return value.isEmpty();
    }

    public boolean isMergeable() {
        if (top != null && top.getValue() == getValue()) {
            return true;
        }
        if (right != null && right.getValue() == getValue()) {
            return true;
        }
        if (bottom != null && bottom.getValue() == getValue()) {
            return true;
        }
        if (left != null && left.getValue() == getValue()) {
            return true;
        }
        return false;
    }

    public boolean isModified() {
        return modified.get();
    }

    public boolean isMerged() {
        return merged.get();
    }

    public boolean isRandomized() {
        return randomized.get();
    }

    public Location getLocation() {
        return location;
    }

    public int getValue() {
        return value.get();
    }

    @Override
    public String toString() {
        return "GridNode{" + "location=" + location + ", value=" + value + ", modified=" + modified.get() + ", merged=" + merged.get() + '}';
    }

}
