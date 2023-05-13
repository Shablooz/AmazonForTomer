package BGU.Group13B.frontEnd.components;


import BGU.Group13B.backend.Pair;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinSessionState;

import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;


public class SessionToIdMapper {

    private static SessionToIdMapper instance;
    private final ConcurrentHashMap<String, Pair<Integer/*user id*/, AtomicBoolean/*refresh required*/>> sessionToId;
    private ConcurrentHashMap<Integer, VaadinSession> idToSession;

    private SessionToIdMapper() {
        // Private constructor to prevent instantiation from outside
        this.sessionToId = new ConcurrentHashMap<>();
        this.idToSession=new ConcurrentHashMap<>();
    }

    public synchronized static SessionToIdMapper getInstance() {
        if (instance == null) {
            instance = new SessionToIdMapper();
        }
        return instance;
    }

    public synchronized void add(String session, int id) {
        this.sessionToId.put(session, Pair.of(id, new AtomicBoolean(false)));
        if(getCurrentSessionId()!=id)
            idToSession.remove(getCurrentSessionId());

        VaadinSession currentSession=VaadinSession.getCurrent();
        this.idToSession.put(id,currentSession);
    }

    //will moistly be used for communication
    public synchronized int get(String session) {
        return this.sessionToId.get(session).getFirst();
    }
    public synchronized VaadinSession getSession(int id) {
        return this.idToSession.get(id);
    }

    public synchronized int getCurrentSessionId() {
        return this.sessionToId.get(VaadinSession.getCurrent().getSession().getId()).getFirst();
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
        this.sessionToId.put(VaadinSession.getCurrent().getSession().getId(), Pair.of(newId, new AtomicBoolean(false)));
    }

    //synchronize is not really needed here
    public synchronized boolean refreshRequired() {
        return this.sessionToId.get(VaadinSession.getCurrent().getSession().getId()).getSecond().get();
    }
    //synchronize is not really needed here
    public synchronized void setRefreshRequired(boolean refreshRequired) {
        this.sessionToId.get(VaadinSession.getCurrent().getSession().getId()).getSecond().set(refreshRequired);
    }
}




