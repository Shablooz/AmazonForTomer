package BGU.Group13B.service;

import BGU.Group13B.backend.storePackage.Market;

class Session implements ISession {
    private final Market market;

    public Session(Market market) {
        this.market = market;
    }

    @Override
    public void addProduct(int userId, String productName, int quantity, double price, int storeId) {
        market.addProduct(userId, productName, quantity, price, storeId);
    }
}
