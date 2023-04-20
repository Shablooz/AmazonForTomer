package BGU.Group13B.service;

import BGU.Group13B.backend.storePackage.PublicAuctionInfo;
import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;

import java.time.LocalDateTime;

import java.util.List;

public class ProxySession implements ISession {
    private ISession realSession;

    public void setRealSession(ISession realSession) {
        if (realSession == null)
            this.realSession = realSession;
    }

    @Override
    public void addProduct(int userId, int storeId, String productName, String category, double price, int quantity) throws NoPermissionException {
        if (realSession != null)
            realSession.addProduct(userId, storeId, productName, category, price, quantity);
    }

    @Override
    public void addToCart(int userId, int storeId, int productId, int quantity) {
        if (realSession != null)
            realSession.addToCart(userId, storeId, productId, quantity);
    }

    @Override
    public void purchaseProductCart(int userId, String address, String creditCardNumber, String creditCardMonth, String creditCardYear, String creditCardHolderFirstName, String creditCardHolderLastName, String creditCardCcv, String id, String creditCardType) {

    }

    @Override
    public void purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice, int amount) {

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
    public void createAuctionForProduct(int storeManagerId, int storeId, int productId, double startingPrice, LocalDateTime lastDate) {

    }

    @Override
    public void auctionPurchase(int userId, int storeId, int productId, double price) {

    }

    @Override
    public PublicAuctionInfo getAuctionInfo(int userId, int storeId, int productId) {
        return null;
    }

    @Override
    public SystemInfo getSystemInformation(int adminId) {
        return null;
    }

    @Override
    public void register(int userId, String username, String password, String email) {

    }

    @Override
    public void searchProductByName(String productName) {
        realSession.searchProductByName(productName);
    }

    @Override
    public void searchProductByCategory(String category) {
        realSession.searchProductByCategory(category);
    }

    @Override
    public void searchProductByKeywords(List<String> keywords) {
        realSession.searchProductByKeywords(keywords);
    }

    @Override
    public void filterByPriceRange(int minPrice, int maxPrice) {
        realSession.filterByPriceRange(minPrice, maxPrice);
    }

    @Override
    public void filterByProductRank(int minRating, int maxRating) {
        realSession.filterByProductRank(minRating, maxRating);
    }

    @Override
    public void filterByCategory(String category) {
        realSession.filterByCategory(category);
    }

    @Override
    public void filterByStoreRank(int minRating, int maxRating) {
        realSession.filterByStoreRank(minRating, maxRating);
    }

    @Override
    public int login(int userID, String username, String password) {
        return realSession.login(userID, username, password);
    }

    @Override
    public void logout(int userID) {
        realSession.logout(userID);
    }

    @Override
    public void addStore(int userId, String storeName, String category) {
        realSession.addStore(userId, storeName, category);
    }

    @Override
    public void setProductName(int userId, int storeId, int productId, String name) {
        realSession.setProductName(userId, storeId, productId, name);
    }

    @Override
    public void setProductCategory(int userId, int storeId, int productId, String category) {
        realSession.setProductCategory(userId, storeId, productId, category);
    }

    @Override
    public void setProductPrice(int userId, int storeId, int productId, double price) {
        realSession.setProductPrice(userId, storeId, productId, price);
    }

    @Override
    public void setProductQuantity(int userId, int storeId, int productId, int quantity) {
        realSession.setProductQuantity(userId, storeId, productId, quantity);
    }

    @Override
    public void removeProduct(int userId, int storeId, int productId) {
        realSession.removeProduct(userId, storeId, productId);
    }
}
