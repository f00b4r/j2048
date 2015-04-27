package cz.jfx.j2048.service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Felix
 */
public class RankService {

    // Vars - final
    private static final String URL = "http://apps.jfx.cz/j2048/";

    // Vars - service
    private final PersistenceService persistenceService;

    public RankService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void publish(int score) {
        try {
            List<String> params = new ArrayList<>();
            params.add("user=" + persistenceService.nickProperty().get());
            params.add("score=" + score);
            params.add("instanceGames=" + persistenceService.instanceGamesProperty().get());
            params.add("totalGames=" + persistenceService.totalGamesProperty().get());

            URL url = new URL(URL + "?" + params.stream().collect(Collectors.joining("&")));
            url.openStream();
            
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
}
