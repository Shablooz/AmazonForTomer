package BGU.Group13B.service;

import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;
import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import org.springframework.data.util.Pair;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Set;

public class ProxySession implements ISession {
    private ISession realSession;

    public void setRealSession(ISession realSession) {
        if (realSession == null)
            this.realSession = realSession;
    }


    @Override
    public void addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity, String description) {
        if (realSession != null)
            realSession.addProduct(userId, storeId, productName, category, price, stockQuantity, description);
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
    public void register(int userId, String username, String password, String email,String answer1,String answer2,String answer3) {

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
    public int login(int userID, String username, String password,String answer1,String answer2, String answer3) {
        return realSession.login(userID, username, password, answer1, answer2 ,answer3);
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
    public void addProductToCart(int userId, int productId, int storeId) {
        realSession.addProductToCart(userId, productId, storeId);
    }

    @Override
    public void getUserPurchaseHistory(int userId) {
        realSession.getUserPurchaseHistory(userId);
    }

    @Override
    public void openComplaint(int userId, String header, String complaint) {
        realSession.openComplaint(userId,header,complaint);
    }

    @Override
    public Message getComplaint(int userId) {
        return realSession.getComplaint(userId);
    }

    @Override
    public void markMessageAsRead(int userId, String receiverId, String senderId, int messageId) {
        realSession.markMessageAsRead(userId, receiverId, senderId, messageId);
    }

    @Override
    public void sendMassageAdmin(int userId, String receiverId, String header, String massage) {
        realSession.sendMassageAdmin(userId, receiverId, header, massage);
    }

    @Override
    public void answerComplaint(int userId, String answer) {
        realSession.answerComplaint(userId, answer);
    }

    @Override
    public Message readMassage(int userId, String receiverId) {
        return realSession.readMassage(userId, receiverId);
    }

    @Override
    public void sendMassageStore(int userId, String header, String massage, int storeId) {
        realSession.sendMassageStore(userId, header, massage, storeId);
    }

    @Override
    public Message readUnreadMassageStore(int userId, int storeId) {
        return realSession.readUnreadMassageStore(userId, storeId);
    }

    @Override
    public Message readReadMassageStore(int userId, int storeId) {
        return realSession.readReadMassageStore(userId, storeId);
    }

    @Override
    public void answerQuestionStore(int userId, String answer) {
        realSession.answerQuestionStore(userId, answer);
    }

    @Override
    public void refreshOldMessageStore(int userId, int storeId) {
        realSession.refreshOldMessageStore(userId, storeId);
    }

    @Override
    public void addReview(int userId, String review, int storeId, int productId) {
        realSession.addReview(userId, review, storeId, productId);
    }

    @Override
    public void removeReview(int userId, int storeId, int productId) {
        realSession.removeReview(userId, storeId, productId);
    }

    @Override
    public Review getReview(int userId, int storeId, int productId) {
        return realSession.getReview(userId, storeId, productId);
    }

    @Override
    public float getProductScore(int userId, int storeId, int productId) {
        return realSession.getProductScore(userId, storeId, productId);
    }

    @Override
    public void addAndSetProductScore(int userId, int storeId, int productId, int score) {
        realSession.addAndSetProductScore(userId, storeId, productId, score);
    }

    @Override
    public void removeProductScore(int userId, int storeId, int productId) {
        realSession.removeProductScore(userId, storeId, productId);
    }

    @Override
    public void addStoreScore(int userId, int storeId, int score) {
        realSession.addStoreScore(userId, storeId, score);
    }

    @Override
    public void removeStoreScore(int userId, int storeId) {
        realSession.removeStoreScore(userId, storeId);
    }

    @Override
    public void modifyStoreScore(int userId, int storeId, int score) {
        realSession.modifyStoreScore(userId, storeId, score);
    }

    @Override
    public float getStoreScore(int userId, int storeId) {
        return realSession.getStoreScore(userId, storeId);
    }

    @Override
    public void getCartContent(int userId) {
        realSession.getCartContent(userId);
    }

    @Override
    public void removeProductFromCart(int userId, int storeId, int productId) {
        realSession.removeProductFromCart(userId, storeId, productId);
    }

    @Override
    public void changeProductQuantityInCart(int userId, int storeId, int productId, int quantity) {
        realSession.changeProductQuantityInCart(userId, storeId, productId, quantity);
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
    public void setProductStockQuantity(int userId, int storeId, int productId, int stockQuantity) {
        realSession.setProductStockQuantity(userId, storeId, productId, stockQuantity);
    }

    @Override
    public void removeProduct(int userId, int storeId, int productId) {
        realSession.removeProduct(userId, storeId, productId);
    }

    @Override
    public String getUserName(int userId) {
        return realSession.getUserName(userId);
    }

    @Override
    public void setUsername(int userId, String newUsername) {
        realSession.setUsername(userId,newUsername);
    }

    @Override
    public void setUserStatus(int userId, int newStatus) {
        realSession.setUserStatus(userId,newStatus);
    }

    @Override
    public String getUserStatus(int userId) {
        return realSession.getUserStatus(userId);
    }

    @Override
    public List<Pair<Integer, String>> getStoresOfUser(int userId) {
        return realSession.getStoresOfUser(userId);
        }

    public StoreInfo getStoreInfo(int storeId) {
        return realSession.getStoreInfo(storeId);
    }

    @Override
    public String getStoreName(int storeId) {
        return realSession.getStoreName(storeId);
    }

    @Override
    public String getStoreCategory(int storeId) {
        return realSession.getStoreCategory(storeId);
    }

    @Override
    public ProductInfo getStoreProductInfo(int storeId, int productId) {
        return realSession.getStoreProductInfo(storeId, productId);
    }

    @Override
    public ProductInfo getProductInfo(int productId) {
        return realSession.getProductInfo(productId);
    }

    @Override
    public String getProductName(int productId) {
        return realSession.getProductName(productId);
    }

    @Override
    public String getProductCategory(int productId) {
        return realSession.getProductCategory(productId);
    }

    @Override
    public double getProductPrice(int productId) {
        return realSession.getProductPrice(productId);
    }

    @Override
    public int getProductStockQuantity(int productId) {
        return realSession.getProductStockQuantity(productId);
    }

    @Override
    public float getProductScore(int productId) {
        return realSession.getProductScore(productId);
    }

    @Override
    public Set<ProductInfo> getAllStoreProductsInfo(int storeId) {
        return realSession.getAllStoreProductsInfo(storeId);
    }
    public boolean SecurityAnswer1Exists(int userId) {
        return realSession.SecurityAnswer1Exists(userId);
    }

    @Override
    public boolean SecurityAnswer2Exists(int userId) {
        return realSession.SecurityAnswer2Exists(userId);
    }

    @Override
    public boolean SecurityAnswer3Exists(int userId) {
        return realSession.SecurityAnswer3Exists(userId);
    }

    @Override
    public boolean checkIfQuestionsExist(int userId) {
        return false;
    }
}
