package BGU.Group13B.service;

import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;
import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.backend.storePackage.Store;
import org.springframework.data.util.Pair;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;

import java.time.LocalDateTime;

import java.util.*;

public class ProxySession implements ISession {
    private ISession realSession;

    public void setRealSession(ISession realSession) {
        if (realSession != null)
            this.realSession = realSession;
    }


    @Override
    public int addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity, String description) {
        if (realSession != null)
            return realSession.addProduct(userId, storeId, productName, category, price, stockQuantity, description);
        return -1;
    }

    @Override
    public void addToCart(int userId, int storeId, int productId) {
        if (realSession != null)
            realSession.addToCart(userId, storeId, productId);
    }

    @Override
    public double purchaseProductCart(int userId, String address, String creditCardNumber, String creditCardMonth, String creditCardYear, String creditCardHolderFirstName, String creditCardHolderLastName, String creditCardCcv, String id, String creditCardType, HashMap<Integer, String> productsCoupons, String storeCoupon) {
        if (realSession != null)
            return realSession.purchaseProductCart(userId, address, creditCardNumber, creditCardMonth, creditCardYear, creditCardHolderFirstName, creditCardHolderLastName, creditCardCcv, id, creditCardType, productsCoupons, storeCoupon);
        return -1;
    }


    @Override
    public void purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice, int amount) {
        if (realSession != null)
            realSession.purchaseProposalSubmit(userId, storeId, productId, proposedPrice, amount);
    }

    @Override
    public void immediatePurchase(int userId, int storeId, int productId, int quantity) {
        if (realSession != null)
            realSession.immediatePurchase(userId, storeId, productId, quantity);
    }

    @Override
    public void createLotteryPurchaseForProduct(int storeManagerId, int storeId, int productId) {
        if (realSession != null)
            realSession.createLotteryPurchaseForProduct(storeManagerId, storeId, productId);
    }

    @Override
    public void participateInLotteryPurchase(int userId, int storeId, int productId, double fraction) {
        if (realSession != null)
            realSession.participateInLotteryPurchase(userId, storeId, productId, fraction);
    }

    @Override
    public void createAuctionForProduct(int storeManagerId, int storeId, int productId, double startingPrice, LocalDateTime lastDate) {
        if (realSession != null)
            realSession.createAuctionForProduct(storeManagerId, storeId, productId, startingPrice, lastDate);
    }

    @Override
    public void auctionPurchase(int userId, int storeId, int productId, double price) {
        if (realSession != null)
            realSession.auctionPurchase(userId, storeId, productId, price);
    }

    @Override
    public PublicAuctionInfo getAuctionInfo(int userId, int storeId, int productId) {
        if (realSession != null)
            return realSession.getAuctionInfo(userId, storeId, productId);
        return new PublicAuctionInfo(0, 0, null);
    }

    @Override
    public SystemInfo getSystemInformation(int adminId) {
        if (realSession != null)
            return realSession.getSystemInformation(adminId);
        return new SystemInfo();
    }

    @Override
    public void register(int userId, String username, String password, String email, String answer1, String answer2, String answer3) {
        if (realSession != null)
            realSession.register(userId, username, password, email, answer1, answer2, answer3);
    }

    @Override
    public void searchProductByName(String productName) {
        if (realSession != null)
            realSession.searchProductByName(productName);
    }

    @Override
    public void searchProductByCategory(String category) {
        if (realSession != null)
            realSession.searchProductByCategory(category);
    }

    @Override
    public void searchProductByKeywords(List<String> keywords) {
        if (realSession != null)
            realSession.searchProductByKeywords(keywords);
    }

    @Override
    public void filterByPriceRange(int minPrice, int maxPrice) {
        if (realSession != null)
            realSession.filterByPriceRange(minPrice, maxPrice);
    }

    @Override
    public void filterByProductRank(int minRating, int maxRating) {
        if (realSession != null)
            realSession.filterByProductRank(minRating, maxRating);
    }

    @Override
    public void filterByCategory(String category) {
        if (realSession != null)
            realSession.filterByCategory(category);
    }

    @Override
    public void filterByStoreRank(int minRating, int maxRating) {
        if (realSession != null)
            realSession.filterByStoreRank(minRating, maxRating);
    }

    @Override
    public int login(int userID, String username, String password, String answer1, String answer2, String answer3) {
        if (realSession != null)
            return realSession.login(userID, username, password, answer1, answer2, answer3);
        return -1;
    }

    @Override
    public void logout(int userID) {
        if (realSession != null)
            realSession.logout(userID);
    }

    @Override
    public int addStore(int userId, String storeName, String category) {
        if (realSession != null)
            return realSession.addStore(userId, storeName, category);
        return -1;
    }

    @Override
    public void addProductToCart(int userId, int productId, int storeId) {
        if (realSession != null)
            realSession.addProductToCart(userId, productId, storeId);
    }

    @Override
    public void getUserPurchaseHistory(int userId) {
        if (realSession != null)
            realSession.getUserPurchaseHistory(userId);
    }

    @Override
    public void openComplaint(int userId, String header, String complaint) {
        if (realSession != null)
            realSession.openComplaint(userId, header, complaint);
    }

    @Override
    public Message getComplaint(int userId) {
        if (realSession != null)
            return realSession.getComplaint(userId);
        return Message.constractMessage("", -1, "", "", "");
    }

    @Override
    public void markMessageAsReadAdmin(int userId, String receiverId, String senderId, int messageId) {
        if (realSession != null)
            realSession.markMessageAsReadAdmin(userId, receiverId, senderId, messageId);
    }

    @Override
    public void sendMassageAdmin(int userId, String receiverId, String header, String massage) {
        if (realSession != null)
            realSession.sendMassageAdmin(userId, receiverId, header, massage);
    }

    @Override
    public void answerComplaint(int userId, String answer) {
        if (realSession != null)
            realSession.answerComplaint(userId, answer);
    }

    @Override
    public Message readMessage(int userId) {
        if (realSession != null)
            return realSession.readMessage(userId);
        return Message.constractMessage("", -1, "", "", "");
    }

    @Override
    public void sendMassageStore(int userId, String header, String massage, int storeId) {
        if (realSession != null)
            realSession.sendMassageStore(userId, header, massage, storeId);
    }

    @Override
    public Message readUnreadMassageStore(int userId, int storeId) {
        if (realSession != null)
            return realSession.readUnreadMassageStore(userId, storeId);
        return Message.constractMessage("", -1, "", "", "");
    }

    @Override
    public Message readReadMassageStore(int userId, int storeId) {
        if (realSession != null)
            return realSession.readReadMassageStore(userId, storeId);
        return Message.constractMessage("", -1, "", "", "");
    }

    @Override
    public void answerQuestionStore(int userId, String answer) {
        if (realSession != null)
            realSession.answerQuestionStore(userId, answer);
    }

    @Override
    public void refreshOldMessageStore(int userId, int storeId) {
        if (realSession != null)
            realSession.refreshOldMessageStore(userId, storeId);
    }

    @Override
    public void addReview(int userId, String review, int storeId, int productId) {
        if (realSession != null)
            realSession.addReview(userId, review, storeId, productId);
    }

    @Override
    public void removeReview(int userId, int storeId, int productId) {
        if (realSession != null)
            realSession.removeReview(userId, storeId, productId);
    }

    @Override
    public Review getReview(int userId, int storeId, int productId) {
        if (realSession != null)
            return realSession.getReview(userId, storeId, productId);
        return new Review("", -1, -1, -1);
    }

    @Override
    public float getProductScore(int userId, int storeId, int productId) {
        if (realSession != null)
            return realSession.getProductScore(userId, storeId, productId);
        return -1;
    }

    @Override
    public void addAndSetProductScore(int userId, int storeId, int productId, int score) {
        if (realSession != null)
            realSession.addAndSetProductScore(userId, storeId, productId, score);
    }

    @Override
    public void removeProductScore(int userId, int storeId, int productId) {
        if (realSession != null)
            realSession.removeProductScore(userId, storeId, productId);
    }

    @Override
    public void addStoreScore(int userId, int storeId, int score) {
        if (realSession != null)
            realSession.addStoreScore(userId, storeId, score);
    }

    @Override
    public void removeStoreScore(int userId, int storeId) {
        if (realSession != null)
            realSession.removeStoreScore(userId, storeId);
    }

    @Override
    public void modifyStoreScore(int userId, int storeId, int score) {
        if (realSession != null)
            realSession.modifyStoreScore(userId, storeId, score);
    }

    @Override
    public float getStoreScore(int userId, int storeId) {
        if (realSession != null)
            return realSession.getStoreScore(userId, storeId);
        return -1;
    }

    @Override
    public void getCartDescription(int userId) {
        if (realSession != null)
            realSession.getCartDescription(userId);
    }

    @Override
    public void removeProductFromCart(int userId, int storeId, int productId) {
        if (realSession != null)
            realSession.removeProductFromCart(userId, storeId, productId);
    }

    @Override
    public void changeProductQuantityInCart(int userId, int storeId, int productId, int quantity) {
        if (realSession != null)
            realSession.changeProductQuantityInCart(userId, storeId, productId, quantity);
    }

    @Override
    public void setProductName(int userId, int storeId, int productId, String name) {
        if (realSession != null)
            realSession.setProductName(userId, storeId, productId, name);
    }

    @Override
    public void setProductCategory(int userId, int storeId, int productId, String category) {
        if (realSession != null)
            realSession.setProductCategory(userId, storeId, productId, category);
    }

    @Override
    public void setProductPrice(int userId, int storeId, int productId, double price) {
        if (realSession != null)
            realSession.setProductPrice(userId, storeId, productId, price);
    }

    @Override
    public void setProductStockQuantity(int userId, int storeId, int productId, int stockQuantity) {
        if (realSession != null)
            realSession.setProductStockQuantity(userId, storeId, productId, stockQuantity);
    }

    @Override
    public void setProductDescription(int userId, int storeId, int productId, String description) {
        if (realSession != null)
            realSession.setProductDescription(userId, storeId, productId, description);
    }

    @Override
    public void removeProduct(int userId, int storeId, int productId) {
        if (realSession != null)
            realSession.removeProduct(userId, storeId, productId);
    }

    @Override
    public String getUserName(int userId) {
        if (realSession != null)
            return realSession.getUserName(userId);
        return "";
    }

    @Override
    public void setUsername(int userId, String newUsername) {
        if (realSession != null)
            realSession.setUsername(userId, newUsername);
    }

    @Override
    public void setUserStatus(int admin_id, int userId, int newStatus) {
        if (realSession != null)
            realSession.setUserStatus(admin_id, userId, newStatus);
    }

    @Override
    public String getUserStatus(int userId) {
        if (realSession != null)
            return realSession.getUserStatus(userId);
        return "";
    }

    @Override
    public String getUserEmail(int userId) {
        if (realSession != null)
            return realSession.getUserEmail(userId);
        return "";
    }

    @Override
    public List<Pair<Integer, String>> getStoresOfUser(int userId) {
        if (realSession != null)
            return realSession.getStoresOfUser(userId);
        return new ArrayList<>();
    }

    public StoreInfo getStoreInfo(int storeId) {
        if (realSession != null)
            return realSession.getStoreInfo(storeId);
        return new StoreInfo(new Store(1, 1, "", ""));
    }

    @Override
    public String getStoreName(int storeId) {
        if (realSession != null)
            return realSession.getStoreName(storeId);
        return "";
    }

    @Override
    public String getStoreCategory(int storeId) {
        if (realSession != null)
            return realSession.getStoreCategory(storeId);
        return "";
    }

    @Override
    public ProductInfo getStoreProductInfo(int storeId, int productId) {
        if (realSession != null)
            return realSession.getStoreProductInfo(storeId, productId);
        return new ProductInfo(new Product(1, 1, "", "", -1, -1, ""));
    }

    @Override
    public ProductInfo getProductInfo(int productId) {
        if (realSession != null)
            return realSession.getProductInfo(productId);
        return new ProductInfo(new Product(1, 1, "", "", -1, -1, ""));
    }

    @Override
    public String getProductName(int productId) {
        if (realSession != null)
            return realSession.getProductName(productId);
        return "";
    }

    @Override
    public String getProductCategory(int productId) {
        if (realSession != null)
            return realSession.getProductCategory(productId);
        return "";
    }

    @Override
    public double getProductPrice(int productId) {
        if (realSession != null)
            return realSession.getProductPrice(productId);
        return -1;
    }

    @Override
    public int getProductStockQuantity(int productId) {
        if (realSession != null)
            return realSession.getProductStockQuantity(productId);
        return -1;
    }

    @Override
    public float getProductScore(int productId) {
        if (realSession != null)
            return realSession.getProductScore(productId);
        return -1;
    }

    @Override
    public Set<ProductInfo> getAllStoreProductsInfo(int storeId) {
        if (realSession != null)
            return realSession.getAllStoreProductsInfo(storeId);
        return new HashSet<>();
    }

    public boolean SecurityAnswer1Exists(int userId) {
        if (realSession != null)
            return realSession.SecurityAnswer1Exists(userId);
        return false;
    }

    @Override
    public boolean SecurityAnswer2Exists(int userId) {
        if (realSession != null)
            return realSession.SecurityAnswer2Exists(userId);
        return false;
    }

    @Override
    public boolean SecurityAnswer3Exists(int userId) {
        if (realSession != null)
            return realSession.SecurityAnswer3Exists(userId);
        return false;
    }

    @Override
    public boolean checkIfQuestionsExist(int userId) {
        if (realSession != null)
            return realSession.checkIfQuestionsExist(userId);
        return false;
    }

    @Override
    public int enterAsGuest() {
        if (realSession != null)
            return realSession.enterAsGuest();
        return -1;
    }

    @Override
    public void exitSystemAsGuest(int userId) {
        if (realSession != null)
            realSession.exitSystemAsGuest(userId);
    }

    @Override
    public int getBasketProductQuantity(int userId, int storeId, int productId) throws Exception {
        if (realSession != null)
            return realSession.getBasketProductQuantity(userId, storeId, productId);
        return -1;
    }

    @Override
    public void allowPurchasePolicyConflicts(int userId, int storeId) {
        if (realSession != null)
            realSession.allowPurchasePolicyConflicts(userId, storeId);
    }

    @Override
    public void disallowPurchasePolicyConflicts(int userId, int storeId) {
        if (realSession != null)
            realSession.disallowPurchasePolicyConflicts(userId, storeId);
    }

    @Override
    public void setStorePurchaseQuantityUpperBound(int userId, int storeId, int upperBound) {
        if (realSession != null)
            realSession.setStorePurchaseQuantityUpperBound(userId, storeId, upperBound);
    }

    @Override
    public void setStorePurchaseQuantityLowerBound(int userId, int storeId, int lowerBound) {
        if (realSession != null)
            realSession.setStorePurchaseQuantityLowerBound(userId, storeId, lowerBound);
    }

    @Override
    public void setStorePurchaseQuantityBounds(int userId, int storeId, int lowerBound, int upperBound) {
        if (realSession != null)
            realSession.setStorePurchaseQuantityBounds(userId, storeId, lowerBound, upperBound);
    }

    @Override
    public void setStorePurchasePriceUpperBound(int userId, int storeId, int upperBound) {
        if (realSession != null)
            realSession.setStorePurchasePriceUpperBound(userId, storeId, upperBound);
    }

    @Override
    public void setStorePurchasePriceLowerBound(int userId, int storeId, int lowerBound) {
        if (realSession != null)
            realSession.setStorePurchasePriceLowerBound(userId, storeId, lowerBound);
    }

    @Override
    public void setStorePurchasePriceBounds(int userId, int storeId, int lowerBound, int upperBound) {
        if (realSession != null)
            realSession.setStorePurchasePriceBounds(userId, storeId, lowerBound, upperBound);
    }

    @Override
    public void setProductPurchaseQuantityUpperBound(int userId, int storeId, int productId, int upperBound) {
        if (realSession != null)
            realSession.setProductPurchaseQuantityUpperBound(userId, storeId, productId, upperBound);
    }

    @Override
    public void setProductPurchaseQuantityLowerBound(int userId, int storeId, int productId, int lowerBound) {
        if (realSession != null)
            realSession.setProductPurchaseQuantityLowerBound(userId, storeId, productId, lowerBound);
    }

    @Override
    public void setProductPurchaseQuantityBounds(int userId, int storeId, int productId, int lowerBound, int upperBound) {
        if (realSession != null)
            realSession.setProductPurchaseQuantityBounds(userId, storeId, productId, lowerBound, upperBound);
    }

    @Override
    public void setProductPurchasePriceUpperBound(int userId, int storeId, int productId, int upperBound) {
        if (realSession != null)
            realSession.setProductPurchasePriceUpperBound(userId, storeId, productId, upperBound);
    }

    @Override
    public void setProductPurchasePriceLowerBound(int userId, int storeId, int productId, int lowerBound) {
        if (realSession != null)
            realSession.setProductPurchasePriceLowerBound(userId, storeId, productId, lowerBound);
    }

    @Override
    public void setProductPurchasePriceBounds(int userId, int storeId, int productId, int lowerBound, int upperBound) {
        if (realSession != null)
            realSession.setProductPurchasePriceBounds(userId, storeId, productId, lowerBound, upperBound);
    }

    @Override
    public int addStoreVisibleDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate) {
        if (realSession != null)
            return realSession.addStoreVisibleDiscount(userId, storeId, discountPercentage, discountLastDate);
        return -1;
    }

    @Override
    public int addStoreConditionalDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount) {
        if (realSession != null)
            return realSession.addStoreConditionalDiscount(userId, storeId, discountPercentage, discountLastDate, minPriceForDiscount, quantityForDiscount);
        return -1;
    }

    @Override
    public int addStoreHiddenDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate, String code) {
        if (realSession != null)
            return realSession.addStoreHiddenDiscount(userId, storeId, discountPercentage, discountLastDate, code);
        return -1;
    }

    @Override
    public void removeStoreDiscount(int userId, int storeId, int discountId) {
        if (realSession != null)
            realSession.removeStoreDiscount(userId, storeId, discountId);
    }

    @Override
    public int addProductVisibleDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate) {
        if (realSession != null)
            return realSession.addProductVisibleDiscount(userId, storeId, productId, discountPercentage, discountLastDate);
        return -1;
    }

    @Override
    public int addProductConditionalDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount) {
        if (realSession != null)
            return realSession.addProductConditionalDiscount(userId, storeId, productId, discountPercentage, discountLastDate, minPriceForDiscount, quantityForDiscount);
        return -1;
    }

    @Override
    public int addProductHiddenDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate, String code) {
        if (realSession != null)
            return realSession.addProductHiddenDiscount(userId, storeId, productId, discountPercentage, discountLastDate, code);
        return -1;
    }

    @Override
    public void removeProductDiscount(int userId, int storeId, int productId, int discountId) {
        if (realSession != null)
            realSession.removeProductDiscount(userId, storeId, productId, discountId);
    }
}
