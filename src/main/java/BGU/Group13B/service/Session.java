package BGU.Group13B.service;

import BGU.Group13B.backend.storePackage.Market;

class Session implements ISession {
    private final Market market;

    public Session(Market market) {
        this.market = market;
    }
}
