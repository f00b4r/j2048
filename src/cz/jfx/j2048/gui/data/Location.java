package cz.jfx.j2048.gui.data;

/**
 *
 * @author Felix
 */
public class Location {

    private final int rowIndex;
    private final int colIndex;

    public Location(int rowIndex, int colIndex) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;
        if (this.rowIndex != other.rowIndex) {
            return false;
        }
        if (this.colIndex != other.colIndex) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[" + rowIndex + "," + colIndex + "]";
    }

}
