package BGU.Group13B.frontEnd.components;


import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinSessionState;

import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;


public class SessionToIdMapper {

    private static SessionToIdMapper instance;
    private final ConcurrentHashMap<String, Integer> sessionToId;

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

    public synchronized void add(String session, int id) {
        this.sessionToId.put(session, id);
    }

    //will moistly be used for communication
    public synchronized int get(String session) {
        return this.sessionToId.get(session);
    }

    public synchronized int getCurrentSessionId() {
        return this.sessionToId.get(VaadinSession.getCurrent().getSession().getId());
    }

    public synchronized TimerTask kickExpired() {
        TimerTask task = new TimerTask() {
            public void run() {
                for (String key : sessionToId.keySet()) {
                    if (isSessionExpired())
                        sessionToId.remove(key);
                }
            }
        };
        return task;
    }


    public synchronized boolean isSessionExpired() {
        // Get the current Vaadin session
        VaadinSession session = VaadinSession.getCurrent();
        return session == null || session.getState() == VaadinSessionState.CLOSED;
    }

    public synchronized boolean containsKey(String sessionId) {
        return this.sessionToId.containsKey(sessionId);
    }
    public synchronized void updateCurrentSession(int newId)
    {
        this.sessionToId.put(VaadinSession.getCurrent().getSession().getId(), newId);
    }
}




