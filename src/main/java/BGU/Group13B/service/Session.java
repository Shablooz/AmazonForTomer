package BGU.Group13B.service;

import BGU.Group13B.backend.SystemInfo;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;

import java.time.LocalDateTime;

class Session implements ISession {
    private final Market market;

    public Session(Market market) {
        this.market = market;
    }

    @Override
    public void addProduct(int userId, String productName, int quantity, double price, int storeId) throws NoPermissionException {
        market.addProduct(userId, productName, quantity, price, storeId);
    }

    @Override
    public void addToCart(int userId, int storeId, int productId, int quantity) {

    }

    @Override
    public void purchaseProductCart(int userId, String address, String creditCardNumber, String creditCardMonth, String creditCardYear, String creditCardHolderFirstName, String creditCardHolderLastName, String creditCardCcv, String id, String creditCardType) {

    }

    @Override
    public void purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice, int amount) {
        try {
            market.purchaseProposalSubmit(userId, storeId, productId, proposedPrice, amount);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
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
    public void createAuctionForProduct(int storeManagerId, int storeId, int productId,
                                        double minPrice, LocalDateTime lastDate) {

    }

    @Override
    public void auctionPurchase(int userId, int storeId, int productId, double newPrice) {

    }
    @Override
    public PublicAuctionInfo getAuctionInfo(int userId, int storeId, int productId) {
        return null;
    }


    @Override
    public SystemInfo getSystemInformation(int adminId) {
        return null;
    }
}
