package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.System.Searcher;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
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
    public void sendMassage(Message message, int userId, int storeId) throws NoPermissionException { //need to check how to send message back to the user
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

    public void addReview(String review, int storeId, int productId, int userId) throws NoPermissionException { //TODO:check get store impl
        storeRepository.getStore(storeId).addReview(review, productId, userId);
    }

    public void removeReview(int storeId, int productId, int userId) throws NoPermissionException {
        storeRepository.getStore(storeId).removeReview(productId, userId);
    }

    public Review getReview(int storeId, int productId, int userId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getReview(productId, userId);
    }

    public float getProductScore(int storeId, int productId, int userId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getProductScore(userId, productId);
    }

    public void addAndSetProductScore(int storeId, int productId, int userId, int score) throws NoPermissionException {
        storeRepository.getStore(storeId).addAndSetProductScore(productId, userId, score);
    }

    public void removeProductScore(int storeId, int productId, int userId) throws NoPermissionException {
        storeRepository.getStore(storeId).removeProductScore(productId, userId);
    }


    public int addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity, String description) throws NoPermissionException {
        return storeRepository.getStore(storeId).addProduct(userId, productName, category, price, stockQuantity, description);
    }

    public void addStoreScore(int userId, int storeId, int score) throws NoPermissionException {
        storeRepository.getStore(storeId).addStoreScore(userId, score);
    }

    public void removeStoreScore(int userId, int storeId) throws NoPermissionException {
        storeRepository.getStore(storeId).removeStoreScore(userId);
    }

    public void modifyStoreScore(int userId, int storeId, int score) throws NoPermissionException {
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

    public List<Product> searchProductByKeywords(String keywords) {
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
                                          String storeCoupon) throws PurchaseExceedsPolicyException {
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

    public void setProductDescription(int userId, int storeId, int productId, String description) throws NoPermissionException {
        storeRepository.getStore(storeId).setProductDescription(userId, productId, description);
    }

    public void removeProduct(int userId, int storeId, int productId) throws NoPermissionException {
        storeRepository.getStore(storeId).removeProduct(userId, productId);
    }

    public StoreInfo getStoreInfo(int userId, int storeId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getStoreInfo(userId);
    }

    public String getStoreName(int storeId) {
        return storeRepository.getStore(storeId).getStoreName();
    }

    public String getStoreCategory(int storeId) {
        return storeRepository.getStore(storeId).getCategory();
    }

    public ProductInfo getStoreProductInfo(int userId, int storeId, int productId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getStoreProduct(userId, productId).getProductInfo();
    }

    public Set<ProductInfo> getAllStoreProductsInfo(int userId, int storeId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getAllStoreProducts(userId).stream().map(Product::getProductInfo).collect(Collectors.toSet());
    }

    public void allowPurchasePolicyConflicts(int userId, int storeId) throws NoPermissionException {
        storeRepository.getStore(storeId).allowPurchasePolicyConflicts(userId);
    }

    public void disallowPurchasePolicyConflicts(int userId, int storeId) throws NoPermissionException {
        storeRepository.getStore(storeId).disallowPurchasePolicyConflicts(userId);
    }

    public void setStorePurchaseQuantityUpperBound(int userId, int storeId, int upperBound) throws NoPermissionException {
        storeRepository.getStore(storeId).setStorePurchaseQuantityUpperBound(userId, upperBound);
    }

    public void setStorePurchaseQuantityLowerBound(int userId, int storeId, int lowerBound) throws NoPermissionException {
        storeRepository.getStore(storeId).setStorePurchaseQuantityLowerBound(userId, lowerBound);
    }

    public void setStorePurchaseQuantityBounds(int userId, int storeId, int lowerBound, int upperBound) throws NoPermissionException {
        storeRepository.getStore(storeId).setStorePurchaseQuantityBounds(userId, lowerBound, upperBound);
    }

    public void setStorePurchasePriceUpperBound(int userId, int storeId, int upperBound) throws NoPermissionException {
        storeRepository.getStore(storeId).setStorePurchasePriceUpperBound(userId, upperBound);
    }

    public void setStorePurchasePriceLowerBound(int userId, int storeId, int lowerBound) throws NoPermissionException {
        storeRepository.getStore(storeId).setStorePurchasePriceLowerBound(userId, lowerBound);
    }

    public void setStorePurchasePriceBounds(int userId, int storeId, int lowerBound, int upperBound) throws NoPermissionException {
        storeRepository.getStore(storeId).setStorePurchasePriceBounds(userId, lowerBound, upperBound);
    }

    public void setProductPurchaseQuantityUpperBound(int userId, int storeId, int productId, int upperBound) throws NoPermissionException {
        storeRepository.getStore(storeId).setProductPurchaseQuantityUpperBound(userId, productId, upperBound);
    }

    public void setProductPurchaseQuantityLowerBound(int userId, int storeId, int productId, int lowerBound) throws NoPermissionException {
        storeRepository.getStore(storeId).setProductPurchaseQuantityLowerBound(userId, productId, lowerBound);
    }

    public void setProductPurchaseQuantityBounds(int userId, int storeId, int productId, int lowerBound, int upperBound) throws NoPermissionException {
        storeRepository.getStore(storeId).setProductPurchaseQuantityBounds(userId, productId, lowerBound, upperBound);
    }

    public void setProductPurchasePriceUpperBound(int userId, int storeId, int productId, int upperBound) throws NoPermissionException {
        storeRepository.getStore(storeId).setProductPurchasePriceUpperBound(userId, productId, upperBound);
    }

    public void setProductPurchasePriceLowerBound(int userId, int storeId, int productId, int lowerBound) throws NoPermissionException {
        storeRepository.getStore(storeId).setProductPurchasePriceLowerBound(userId, productId, lowerBound);
    }

    public void setProductPurchasePriceBounds(int userId, int storeId, int productId, int lowerBound, int upperBound) throws NoPermissionException {
        storeRepository.getStore(storeId).setProductPurchasePriceBounds(userId, productId, lowerBound, upperBound);
    }

    public int addStoreVisibleDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate) throws NoPermissionException {
        return storeRepository.getStore(storeId).addStoreVisibleDiscount(userId, discountPercentage, discountLastDate);
    }

    public int addStoreConditionalDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount) throws NoPermissionException {
        return storeRepository.getStore(storeId).addStoreConditionalDiscount(userId, discountPercentage, discountLastDate, minPriceForDiscount, quantityForDiscount);
    }

    public int addStoreHiddenDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate, String code) throws NoPermissionException {
        return storeRepository.getStore(storeId).addStoreHiddenDiscount(userId, discountPercentage, discountLastDate, code);
    }

    public void removeStoreDiscount(int userId, int storeId, int discountId) throws NoPermissionException {
        storeRepository.getStore(storeId).removeStoreDiscount(userId, discountId);
    }

    public int addProductVisibleDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate) throws NoPermissionException {
        return storeRepository.getStore(storeId).addProductVisibleDiscount(userId, productId, discountPercentage, discountLastDate);
    }

    public int addProductConditionalDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount) throws NoPermissionException {
        return storeRepository.getStore(storeId).addProductConditionalDiscount(userId, productId, discountPercentage, discountLastDate, minPriceForDiscount, quantityForDiscount);
    }

    public int addProductHiddenDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate, String code) throws NoPermissionException {
        return storeRepository.getStore(storeId).addProductHiddenDiscount(userId, productId, discountPercentage, discountLastDate, code);
    }

    public void removeProductDiscount(int userId, int storeId, int productId, int discountId) throws NoPermissionException {
        storeRepository.getStore(storeId).removeProductDiscount(userId, productId, discountId);
    }

    public void hideStore(int userId, int storeId) throws NoPermissionException {
        storeRepository.getStore(storeId).hideStore(userId);
    }

    public void unhideStore(int userId, int storeId) throws NoPermissionException {
        storeRepository.getStore(storeId).unhideStore(userId);
    }
}
