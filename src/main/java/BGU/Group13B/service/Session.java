package BGU.Group13B.service;

import BGU.Group13B.backend.Repositories.Interfaces.ICartRepository;
import BGU.Group13B.backend.SystemInfo;
import BGU.Group13B.backend.storePackage.Market;

class Session implements ISession {
    private final Market market;
    private final ICartRepository cartRepository;
    public Session(Market market, ICartRepository cartRepository) {
        this.market = market;
        this.cartRepository = cartRepository;
    }

    @Override
    public void addProduct(int userId, String productName, int quantity, double price, int storeId) {
        market.addProduct(userId, productName, quantity, price, storeId);
    }

    @Override
    public void purchaseProductCart(int userId, String address, String creditCardNumber, String creditCardMonth, String creditCardYear, String creditCardHolderFirstName, String creditCardHolderLastName, String creditCardCcv, String id, String creditCardType) {

    }

    @Override
    public void purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice) {

    }

    @Override
    public void immediatePurchase(int userId, int storeId, int productId, int quantity) {

    }

    @Override
    public void createLotteryPurchaseForProduct(int storeManagerId, int storeId, int productId) {

    }

    @Override
    public void participateInLotteryPurchase(int userId, int storeId, int productId, double fraction) {

    }

    @Override
    public void auctionPurchase(int userId, int storeId, int productId, double price) {

    }

    @Override
    public SystemInfo getSystemInformation(int adminId) {
        return null;
    }
}
