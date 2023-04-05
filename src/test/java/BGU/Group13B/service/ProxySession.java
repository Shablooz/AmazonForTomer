package BGU.Group13B.service;

public class ProxySession implements ISession {
    private ISession realSession;

    public void setRealSession(ISession realSession) {
        if (realSession == null)
            this.realSession = realSession;
    }

    @Override
    public void addProduct(int userId, String productName, int quantity, double price, int storeId) {
        if (realSession != null)
            realSession.addProduct(userId, productName, quantity, price, storeId);
    }
}
