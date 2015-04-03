package cz.jfx.j2048.app;

import cz.jfx.j2048.control.MainControl;
import cz.jfx.j2048.service.PersistenceService;

/**
 *
 * @author Felix
 */
public class ApplicationContainer {

    // Vars
    private final PersistenceService persistenceService;

    public ApplicationContainer() {
        persistenceService = new PersistenceService();
    }

    /**
     * *************************************************************************
     * SERVICES ****************************************************************
     * *************************************************************************
     */
    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    /**
     * *************************************************************************
     * FACTORIES ***************************************************************
     * *************************************************************************
     */
    public MainControl createMainControl() {
        return new MainControl(getPersistenceService());
    }
}
