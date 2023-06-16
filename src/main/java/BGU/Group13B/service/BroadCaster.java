package BGU.Group13B.service;

import com.vaadin.flow.shared.Registration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class BroadCaster {

    static Executor executor = Executors.newSingleThreadExecutor();


    static HashMap<Integer, Consumer<String>> listeners = new HashMap<>();

    public static synchronized Registration register(Integer id, Consumer<String> listener) {
        listeners.put(id, listener);

        return () -> {
            synchronized (BroadCaster.class) {
                listeners.remove(id);
            }
        };
    }

    public static synchronized boolean broadcast(Integer id, String message) {
        Consumer<String> listener = listeners.get(id);
        if (listener != null) {
            executor.execute(() -> listener.accept(message));
            return true;
        }
        return false;
    }

    //Lior's addition - for user traffic
    static HashMap<Integer, Runnable> userTrafficListeners = new HashMap<>();

    public static synchronized Registration registerUserTraffic(Integer id, Runnable listener) {
        userTrafficListeners.put(id, listener);

        return () -> {
            synchronized (BroadCaster.class) {
                userTrafficListeners.remove(id);
            }
        };
    }

    public static synchronized void broadcastUserTraffic() {
        for (var listener : userTrafficListeners.values()) {
            if(listener != null)
                executor.execute(listener);
        }
    }

}
