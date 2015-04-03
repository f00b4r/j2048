package cz.jfx.j2048.data;

import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Felix
 */
public class TileValue extends SimpleIntegerProperty {

    // Constants
    public static final int EMPTY_VALUE = 0;
    public static final int MIN_VALUE = 2;
    public static final int MAX_VALUE = 2048;

    public void empty() {
        this.set(EMPTY_VALUE);
    }

    public boolean isEmpty() {
        return this.get() == EMPTY_VALUE;
    }

}
