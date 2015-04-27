package cz.jfx.j2048.app;

import cz.jfx.j2048.control.MainControl;
import cz.jfx.j2048.service.PersistenceService;
import cz.jfx.j2048.service.RankService;

/**
 *
 * @author Felix
 */
public class ApplicationContainer {

    // Vars
    private final PersistenceService persistenceService;
    private final RankService rankService;

    public ApplicationContainer() {
        persistenceService = new PersistenceService();
        rankService = new RankService(persistenceService);
    }

    /**
     * *************************************************************************
     * SERVICES ****************************************************************
     * *************************************************************************
     */
    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    public RankService getRankService() {
        return rankService;
    }

    /**
     * *************************************************************************
     * FACTORIES ***************************************************************
     * *************************************************************************
     */
    public MainControl createMainControl() {
        return new MainControl(getPersistenceService(), getRankService());
    }
}
