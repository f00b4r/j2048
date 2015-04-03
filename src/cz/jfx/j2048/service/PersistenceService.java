package cz.jfx.j2048.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Felix
 */
public class PersistenceService {

    // Constants
    private final String STORE_FILE = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "j2048.ini";

    // Vars - properties
    private final IntegerProperty bestScore;
    
    public PersistenceService() {
        bestScore = new SimpleIntegerProperty(0);
    }

    /**
     * *************************************************************************
     * PROPERTIES **************************************************************
     * *************************************************************************
     */
    public final IntegerProperty bestScoreProperty() {
        return bestScore;
    }

    /**
     * *************************************************************************
     * LOAD ********************************************************************
     * *************************************************************************
     */
    public void load() {
        try {
            Properties p = new Properties();
            // Load
            p.load(new FileInputStream(STORE_FILE));

            // Set properties
            bestScore.set(Integer.valueOf(p.getProperty("score.best", "0")));
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * *************************************************************************
     * SAVE ********************************************************************
     * *************************************************************************
     */
    public void save() {
        try {
            Properties p = new Properties();
            FileOutputStream out = new FileOutputStream(STORE_FILE);

            // Set properties
            p.setProperty("score.best", String.valueOf(bestScore.get()));

            // Store
            p.store(out, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
