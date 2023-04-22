package BGU.Group13B.service;

import BGU.Group13B.backend.storePackage.Market;

public abstract class Driver {
    public static ISession getSession() {
        ProxySession proxySession = new ProxySession();
        ISession realSession = new Session(new Market());
        // Uncomment this line
        proxySession.setRealSession(realSession);

        return proxySession;
    }
}
