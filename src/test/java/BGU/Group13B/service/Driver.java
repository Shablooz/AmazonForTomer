package BGU.Group13B.service;

public abstract class Driver {
    public static ISession getSession() {
        ProxySession session = new ProxySession();

        // Uncomment this line
        // session.setRealSession(InitClasses.getSession()));//fixme: add real session

        return session;
    }
}
