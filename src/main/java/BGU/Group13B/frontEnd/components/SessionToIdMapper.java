package BGU.Group13B.frontEnd.components;


import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinSessionState;

import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;


public class SessionToIdMapper {

    private static SessionToIdMapper instance;
    private ConcurrentHashMap<String, Integer> sessionToId;

    private SessionToIdMapper() {
        // Private constructor to prevent instantiation from outside
        this.sessionToId = new ConcurrentHashMap<>();
    }

    public synchronized static SessionToIdMapper getInstance() {
        if (instance == null) {
            instance = new SessionToIdMapper();
        }
        return instance;
    }

    public void add(String session, int id) {
        this.sessionToId.put(session, id);
    }

    public int get(String session) {
        return this.sessionToId.get(session);
    }

    public TimerTask kickExpired() {
        for (String key : sessionToId.keySet()) {
            if (isSessionExpired())
                sessionToId.remove(key);
        }
        return null;
    }

    public boolean isSessionExpired() {
        // Get the current Vaadin session
        VaadinSession session = VaadinSession.getCurrent();
        return session == null || session.getState() == VaadinSessionState.CLOSED;
    }
}




