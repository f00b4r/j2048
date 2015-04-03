package cz.jfx.j2048.gui.data;

import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Felix
 */
public class NodeValue extends SimpleIntegerProperty {

    // Constants
    private static final int EMPTY_VALUE = 0;

    public void empty() {
        this.set(EMPTY_VALUE);
    }

    public boolean isEmpty() {
        return this.get() == EMPTY_VALUE;
    }

}
