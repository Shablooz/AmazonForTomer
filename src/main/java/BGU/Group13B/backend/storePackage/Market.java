package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.System.Searcher;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.callbacks.AddToUserCart;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class Market {
    private final IStoreRepository storeRepository;
    private Searcher searcher; //inject in the loading of the system

    private final AddToUserCart addToUserCart;

    public Market() {
        SingletonCollection.setCalculatePriceOfBasket(this::calculatePriceOfBasket);

        this.storeRepository = SingletonCollection.getStoreRepository();
        this.searcher = SingletonCollection.getSearcher();
        this.addToUserCart = SingletonCollection.getAddToUserCart();
    }

    //Tomer
    //#27
    public void sendMassage(Message message, int userId, int storeId) { //need to check how to send message back to the user
        storeRepository.getStore(storeId).sendMassage(message, userId);
    }

    //#42
    public Message getUnreadMessages(int userId, int storeId) throws NoPermissionException {

        return storeRepository.getStore(storeId).getUnreadMessages(userId);
    }

    //#42
    public Message getReadMessages(int userId, int storeId) throws NoPermissionException {

        return storeRepository.getStore(storeId).getReadMessages(userId);
    }

    //#42
    public void markAsCompleted(String senderId, int messageId, int userId, int storeId) throws NoPermissionException {

        storeRepository.getStore(storeId).markAsCompleted(senderId, messageId, userId);
    }

    //#42
    public void refreshMessages(int userId, int storeId) throws NoPermissionException {
        storeRepository.getStore(storeId).refreshMessages(userId);
    }

    public void addReview(String review, int storeId, int productId, int userId) { //TODO:check get store impl
        storeRepository.getStore(storeId).addReview(review, productId, userId);
    }

    public void removeReview(int storeId, int productId, int userId) {
        storeRepository.getStore(storeId).removeReview(productId, userId);
    }

    public Review getReview(int storeId, int productId, int userId) {
        return storeRepository.getStore(storeId).getReview(productId, userId);
    }

    public float getProductScore(int storeId, int productId) {
        return storeRepository.getStore(storeId).getProductScore(productId);
    }

    public void addAndSetProductScore(int storeId, int productId, int userId, int score) {
        storeRepository.getStore(storeId).addAndSetProductScore(productId, userId, score);
    }

    public void removeProductScore(int storeId, int productId, int userId) {
        storeRepository.getStore(storeId).removeProductScore(productId, userId);
    }


    public void addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity, String description) throws NoPermissionException {
        storeRepository.getStore(storeId).addProduct(userId, storeId, productName, category, price, stockQuantity, description);
    }

    public void addStoreScore(int userId, int storeId, int score) {
        storeRepository.getStore(storeId).addStoreScore(userId, score);
    }

    public void removeStoreScore(int userId, int storeId) {
        storeRepository.getStore(storeId).removeStoreScore(userId);
    }

    public void modifyStoreScore(int userId, int storeId, int score) {
        storeRepository.getStore(storeId).modifyStoreScore(userId, score);
    }

    public float getStoreScore(int storeId) {
        return storeRepository.getStore(storeId).getStoreScore();
    }


    public void purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice, int amount) throws NoPermissionException {
        storeRepository.getStore(storeId).purchaseProposalSubmit(userId, productId, proposedPrice, amount);
    }

    public void purchaseProposalApprove(int managerId, int storeId, int productId) throws NoPermissionException {
        storeRepository.getStore(storeId).purchaseProposalApprove(managerId, productId);
    }

    public void purchaseProposalReject(int storeId, int managerId, int bidId) throws NoPermissionException {
        storeRepository.getStore(storeId).purchaseProposalReject(managerId, bidId);
    }

    public void auctionPurchase(int userId, int storeId, int productId, double newPrice) throws NoPermissionException {
        storeRepository.getStore(storeId).auctionPurchase(userId, productId, newPrice);
    }

    public PublicAuctionInfo getAuctionInfo(int userId, int storeId, int productId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getAuctionInfo(userId, productId);
    }

    public void createAuctionForProduct(int storeManagerId, int storeId, int productId,
                                        double minPrice, LocalDateTime lastDate) throws NoPermissionException {
        storeRepository.getStore(storeId).createAuctionForProduct(storeManagerId, productId, minPrice, lastDate);
    }

    public void endAuctionForProduct(int storeId, int storeManagerId, int productId) throws NoPermissionException {
        storeRepository.getStore(storeId).endAuctionForProduct(storeManagerId, productId);
    }

    //(#24) open store - requirement 3.2
    public int addStore(int founderId, String storeName, String category) {
        return storeRepository.addStore(founderId, storeName, category);
    }

    public List<Product> searchProductByName(String productName) {
        return searcher.searchByName(productName);
    }

    public List<Product> searchProductByCategory(String category) {
        return searcher.searchByCategory(category);
    }

    public List<Product> searchProductByKeywords(List<String> keywords) {
        return searcher.searchByKeywords(keywords);
    }

    public List<Product> filterByPriceRange(int minPrice, int maxPrice) {
        return searcher.filterByPriceRange(minPrice, maxPrice);
    }

    public List<Product> filterByProductRank(int minRating, int maxRating) {
        return searcher.filterByProductRank(minRating, maxRating);
    }

    public List<Product> filterByCategory(String category) {
        return searcher.filterByCategory(category);
    }

    public List<Product> filterByStoreRank(int minRating, int maxRating) {
        return searcher.filterByStoreRank(minRating, maxRating);
    }

    private double calculatePriceOfBasket(double totalAmountBeforeStoreDiscountPolicy, ConcurrentLinkedQueue<BasketProduct> successfulProducts, int storeId,
                                          String storeCoupon) {
        return Optional.of(storeRepository.getStore(storeId)).orElseThrow(
                () -> new RuntimeException("Store with id " + storeId + " does not exist")
        ).calculatePriceOfBasket(totalAmountBeforeStoreDiscountPolicy, successfulProducts, storeCoupon);
    }


    public void isProductAvailable(int productId, int storeId) throws Exception {
        storeRepository.getStore(storeId).isProductAvailable(productId);
    }

    public void setProductName(int userId, int storeId, int productId, String name) throws NoPermissionException {
        storeRepository.getStore(storeId).setProductName(userId, productId, name);
    }

    public void setProductCategory(int userId, int storeId, int productId, String category) throws NoPermissionException {
        storeRepository.getStore(storeId).setProductCategory(userId, productId, category);
    }

    public void setProductPrice(int userId, int storeId, int productId, double price) throws NoPermissionException {
        storeRepository.getStore(storeId).setProductPrice(userId, productId, price);
    }

    public void setProductStockQuantity(int userId, int storeId, int productId, int stockQuantity) throws NoPermissionException {
        storeRepository.getStore(storeId).setProductStockQuantity(userId, productId, stockQuantity);
    }

    public void removeProduct(int userId, int storeId, int productId) throws NoPermissionException {
        storeRepository.getStore(storeId).removeProduct(userId, productId);
    }

    public StoreInfo getStoreInfo(int storeId) {
        return storeRepository.getStore(storeId).getStoreInfo();
    }

    public String getStoreName(int storeId) {
        return storeRepository.getStore(storeId).getStoreName();
    }

    public String getStoreCategory(int storeId) {
        return storeRepository.getStore(storeId).getCategory();
    }

    public ProductInfo getStoreProductInfo(int storeId, int productId) {
        return storeRepository.getStore(storeId).getStoreProduct(productId).getProductInfo();
    }

    public ProductInfo getProductInfo(int productId) {
        return searcher.getProductById(productId).getProductInfo();
    }

    public String getProductName(int productId) {
        return searcher.getProductById(productId).getName();
    }

    public String getProductCategory(int productId) {
        return searcher.getProductById(productId).getCategory();
    }

    public double getProductPrice(int productId) {
        return searcher.getProductById(productId).getPrice();
    }

    public int getProductStockQuantity(int productId) {
        return searcher.getProductById(productId).getStockQuantity();
    }

    public float getProductScore(int productId) {
        return searcher.getProductById(productId).getProductScore();
    }

    public Set<ProductInfo> getAllStoreProductsInfo(int storeId) {
        return storeRepository.getStore(storeId).getAllStoreProducts().stream().map(Product::getProductInfo).collect(Collectors.toSet());
    }


}
