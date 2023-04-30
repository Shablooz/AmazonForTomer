package BGU.Group13B.service;

import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.frontEnd.service.ISession;
import BGU.Group13B.frontEnd.service.Session;

public abstract class Driver {
    public static ISession getSession() {
        ProxySession proxySession = new ProxySession();
        ISession realSession = new Session(new Market());
        // Uncomment this line
        proxySession.setRealSession(realSession);

        return proxySession;
    }
}
