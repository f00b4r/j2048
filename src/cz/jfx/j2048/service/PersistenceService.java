package cz.jfx.j2048.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Felix
 */
public class PersistenceService {

    // Constants
    private final String STORE_FILE = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "j2048.ini";

    // Vars - properties
    private final IntegerProperty bestScore;
    private final IntegerProperty totalGames;
    private final IntegerProperty instanceGames;
    private final StringProperty nick;

    public PersistenceService() {
        bestScore = new SimpleIntegerProperty(0);
        totalGames = new SimpleIntegerProperty(0);
        instanceGames = new SimpleIntegerProperty(0);
        nick = new SimpleStringProperty(System.getProperty("user.name"));
    }

    /**
     * *************************************************************************
     * PROPERTIES **************************************************************
     * *************************************************************************
     */
    public final IntegerProperty bestScoreProperty() {
        return bestScore;
    }

    public final IntegerProperty totalGamesProperty() {
        return totalGames;
    }

    public final IntegerProperty instanceGamesProperty() {
        return instanceGames;
    }

    public final StringProperty nickProperty() {
        return nick;
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
            totalGames.set(Integer.valueOf(p.getProperty("games.total", "0")));
        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
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
            p.setProperty("games.total", String.valueOf(totalGames.get()));

            // Store
            p.store(out, null);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
}
