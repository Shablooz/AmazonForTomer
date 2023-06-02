package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.System.Searcher;
import BGU.Group13B.backend.User.*;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.StoreDiscount;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.User.PurchaseHistory;
import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.permissions.ChangePermissionException;
import BGU.Group13B.backend.storePackage.permissions.DefaultFounderFunctionality;
import BGU.Group13B.backend.storePackage.permissions.DefaultOwnerFunctionality;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.callbacks.AddToUserCart;
import BGU.Group13B.service.info.DiscountAccumulationTreeInfo;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
        storeRepository.getStore(storeId).addReview(review, userId, productId);
    }

    public void removeReview(int storeId, int productId, int userId) throws NoPermissionException {
        storeRepository.getStore(storeId).removeReview(userId, productId);
    }

    public Review getReview(int storeId, int productId, int userId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getReview(userId, productId);
    }

    public List<Review> getAllReviews(int productId, int storeId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getAllReviews(productId);
    }

    public float getProductScore(int storeId, int productId, int userId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getProductScore(productId, userId);
    }

    public float getProductScoreUser(int storeId, int productId, int userId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getProductScoreUser(productId, userId);
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

    public List<ProductInfo> searchProductByName(String productName) {
        return searcher.searchByName(productName);
    }

    public List<ProductInfo> searchProductByCategory(String category) {
        return searcher.searchByCategory(category);
    }

    public List<ProductInfo> searchProductByKeywords(String keywords) {
        return searcher.searchByKeywords(keywords);
    }

    public List<ProductInfo> search(String searchWords) {
        List<ProductInfo> products = new LinkedList<>();
        products.addAll(searchProductByKeywords(searchWords));
        products.addAll(searchProductByCategory(searchWords));
        products.addAll(searchProductByName(searchWords));
        Set<ProductInfo> set = new HashSet<>(products.size());
        products.removeIf(p -> !set.add(p));
        return products;
    }

    public List<ProductInfo> filterByPriceRange(int minPrice, int maxPrice) {
        return searcher.filterByPriceRange(minPrice, maxPrice);
    }

    public List<ProductInfo> filterByProductRank(int minRating, int maxRating) {
        return searcher.filterByProductRank(minRating, maxRating);
    }

    public List<ProductInfo> filterByCategory(String category) {
        return searcher.filterByCategory(category);
    }

    public List<ProductInfo> filterByStoreRank(int minRating, int maxRating) {
        return searcher.filterByStoreRank(minRating, maxRating);
    }

    private double calculatePriceOfBasket(int storeId, BasketInfo basketInfo, UserInfo userInfo,
                                          List<String> coupons) throws PurchaseExceedsPolicyException {//add date of birth
        return Optional.of(storeRepository.getStore(storeId)).orElseThrow(
                () -> new RuntimeException("Store with id " + storeId + " does not exist")
        ).calculatePriceOfBasket(basketInfo, userInfo, coupons);
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

    public ProductInfo getStoreProductInfo(int userId, int storeId, int productId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getStoreProduct(userId, productId).getProductInfo();
    }

    public Set<ProductInfo> getAllStoreProductsInfo(int userId, int storeId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getAllStoreProducts(userId).stream().map(Product::getProductInfo).collect(Collectors.toSet());
    }


    public void hideStore(int userId, int storeId) throws NoPermissionException {
        storeRepository.getStore(storeId).hideStore(userId);
    }

    public void unhideStore(int userId, int storeId) throws NoPermissionException {
        storeRepository.getStore(storeId).unhideStore(userId);
    }

    public int getStoreFounder(int storeId) {
        return storeRepository.getStore(storeId).getStoreFounder();
    }

    public void deleteStore(int userId, int storeId) throws NoPermissionException {
        storeRepository.getStore(storeId).deleteStore(userId);
    }

    public List<PurchaseHistory> getStorePurchaseHistory(int userId, int storeId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getStorePurchaseHistory(userId);
    }

    public List<PurchaseHistory> getStorePurchaseHistoryAsAdmin(int storeId, int adminId) throws NoPermissionException {
        return getStorePurchaseHistory(adminId, storeId);
    }

    public void addOwner(int userId, int newOwnerId, int storeId) throws NoPermissionException, ChangePermissionException {
        storeRepository.getStore(storeId).addOwner(userId, newOwnerId);
    }

    public void removeOwner(int userId, int removeOwnerId, int storeId) throws NoPermissionException, ChangePermissionException {
        storeRepository.getStore(storeId).removeOwner(userId, removeOwnerId);
    }

    public void addManager(int userId, int newManagerId, int storeId) throws NoPermissionException, ChangePermissionException {
        storeRepository.getStore(storeId).addManager(userId, newManagerId);
    }

    public void removeManager(int userId, int removeManagerId, int storeId) throws NoPermissionException, ChangePermissionException {
        storeRepository.getStore(storeId).removeManager(userId, removeManagerId);
    }

    public List<WorkerCard> getStoreWorkersInfo(int userId, int storeId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getStoreWorkersInfo(userId);
    }

    public List<Integer> getStoreOwners(int storeId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getStoreOwners();
    }


    /**
     * <H1>Conditions</H1>
     */

    public void setPurchasePolicyCondition(int storeId, int userId, int conditionId) throws NoPermissionException {
        storeRepository.getStore(storeId).setPurchasePolicyCondition(userId, conditionId);
    }

    public int addORCondition(int storeId, int userId, int condition1, int condition2) throws NoPermissionException {
        return storeRepository.getStore(storeId).addORCondition(userId, condition1, condition2);
    }


    public int addANDCondition(int storeId, int userId, int condition1, int condition2) throws NoPermissionException {
        return storeRepository.getStore(storeId).addANDCondition(userId, condition1, condition2);
    }


    public int addXORCondition(int storeId, int userId, int condition1, int condition2) throws NoPermissionException {
        return storeRepository.getStore(storeId).addXORCondition(userId, condition1, condition2);
    }


    public int addIMPLYCondition(int storeId, int userId, int condition1, int condition2) throws NoPermissionException {
        return storeRepository.getStore(storeId).addIMPLYCondition(userId, condition1, condition2);
    }

    public int addStorePriceCondition(int storeId, int userId, double lowerBound, double upperBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addStorePriceCondition(userId, lowerBound, upperBound);
    }

    public int addStorePriceCondition(int storeId, int userId, double lowerBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addStorePriceCondition(userId, lowerBound);
    }

    public int addStoreQuantityCondition(int storeId, int userId, int lowerBound, int upperBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addStoreQuantityCondition(userId, lowerBound, upperBound);
    }

    public int addStoreQuantityCondition(int storeId, int userId, int lowerBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addStoreQuantityCondition(userId, lowerBound);
    }

    public int addCategoryPriceCondition(int storeId, int userId, String category, double lowerBound, double upperBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addCategoryPriceCondition(userId, category, lowerBound, upperBound);
    }


    public int addCategoryPriceCondition(int storeId, int userId, String category, double lowerBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addCategoryPriceCondition(userId, category, lowerBound);
    }


    public int addCategoryQuantityCondition(int storeId, int userId, String category, int lowerBound, int upperBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addCategoryQuantityCondition(userId, category, lowerBound, upperBound);
    }


    public int addCategoryQuantityCondition(int storeId, int userId, String category, int lowerBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addCategoryQuantityCondition(userId, category, lowerBound);
    }


    public int addDateCondition(int storeId, int userId, LocalDateTime lowerBound, LocalDateTime upperBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addDateCondition(userId, lowerBound, upperBound);
    }


    public int addDateCondition(int storeId, int userId, LocalDateTime lowerBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addDateCondition(userId, lowerBound);
    }


    public int addProductPriceCondition(int storeId, int userId, int productId, double lowerBound, double upperBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addProductPriceCondition(userId, productId, lowerBound, upperBound);
    }


    public int addProductPriceCondition(int storeId, int userId, int productId, double lowerBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addProductPriceCondition(userId, productId, lowerBound);
    }


    public int addProductQuantityCondition(int storeId, int userId, int productId, int lowerBound, int upperBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addProductQuantityCondition(userId, productId, lowerBound, upperBound);
    }


    public int addProductQuantityCondition(int storeId, int userId, int productId, int lowerBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addProductQuantityCondition(userId, productId, lowerBound);
    }


    public int addTimeCondition(int storeId, int userId, LocalDateTime lowerBound, LocalDateTime upperBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addTimeCondition(userId, lowerBound, upperBound);
    }


    public int addTimeCondition(int storeId, int userId, LocalDateTime lowerBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addTimeCondition(userId, lowerBound);
    }


    public int addUserAgeCondition(int storeId, int userId, int lowerBound, int upperBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addUserAgeCondition(userId, lowerBound, upperBound);
    }


    public int addUserAgeCondition(int storeId, int userId, int lowerBound) throws NoPermissionException {
        return storeRepository.getStore(storeId).addUserAgeCondition(userId, lowerBound);
    }

    /**
     * <H1>Discounts</H1>
     */

    public int addStoreDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String coupon) throws NoPermissionException {
        return storeRepository.getStore(storeId).addStoreDiscount(userId, conditionId, discountPercentage, expirationDate, coupon);
    }


    public int addStoreDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String coupon) throws NoPermissionException {
        return storeRepository.getStore(storeId).addStoreDiscount(userId, discountPercentage, expirationDate, coupon);
    }


    public int addStoreDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate) throws NoPermissionException {
        return storeRepository.getStore(storeId).addStoreDiscount(userId, conditionId, discountPercentage, expirationDate);
    }


    public int addStoreDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate) throws NoPermissionException {
        return storeRepository.getStore(storeId).addStoreDiscount(userId, discountPercentage, expirationDate);
    }


    public int addCategoryDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String category, String coupon) throws NoPermissionException {
        return storeRepository.getStore(storeId).addCategoryDiscount(userId, conditionId, discountPercentage, expirationDate, category, coupon);
    }


    public int addCategoryDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String category, String coupon) throws NoPermissionException {
        return storeRepository.getStore(storeId).addCategoryDiscount(userId, discountPercentage, expirationDate, category, coupon);
    }


    public int addCategoryDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String category) throws NoPermissionException {
        return storeRepository.getStore(storeId).addCategoryDiscount(userId, conditionId, discountPercentage, expirationDate, category);
    }


    public int addCategoryDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String category) throws NoPermissionException {
        return storeRepository.getStore(storeId).addCategoryDiscount(userId, discountPercentage, expirationDate, category);
    }


    public int addProductDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, int productId, String coupon) throws NoPermissionException {
        return storeRepository.getStore(storeId).addProductDiscount(userId, conditionId, discountPercentage, expirationDate, productId, coupon);
    }


    public int addProductDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, int productId, String coupon) throws NoPermissionException {
        return storeRepository.getStore(storeId).addProductDiscount(userId, discountPercentage, expirationDate, productId, coupon);
    }


    public int addProductDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, int productId) throws NoPermissionException {
        return storeRepository.getStore(storeId).addProductDiscount(userId, conditionId, discountPercentage, expirationDate, productId);
    }


    public int addProductDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, int productId) throws NoPermissionException {
        return storeRepository.getStore(storeId).addProductDiscount(userId, discountPercentage, expirationDate, productId);
    }


    public List<StoreDiscount> getStoreDiscounts(int storeId, int userId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getStoreDiscounts(userId);
    }


    public StoreDiscount getDiscount(int storeId, int userId, int discountId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getDiscount(userId, discountId);
    }


    public void removeDiscount(int storeId, int userId, int discountId) throws NoPermissionException {
        storeRepository.getStore(storeId).removeDiscount(userId, discountId);
    }

    /**
     * <H1>Discount Accumulation Tree</H1>
     */

    public void addDiscountAsRoot(int storeId, int userId, int discountId) throws NoPermissionException {
        storeRepository.getStore(storeId).addDiscountAsRoot(userId, discountId);
    }


    public void addDiscountToXORRoot(int storeId, int userId, int discountId) throws NoPermissionException {
        storeRepository.getStore(storeId).addDiscountToXORRoot(userId, discountId);
    }


    public void addDiscountToMAXRoot(int storeId, int userId, int discountId) throws NoPermissionException {
        storeRepository.getStore(storeId).addDiscountToMAXRoot(userId, discountId);

    }


    public void addDiscountToADDRoot(int storeId, int userId, int discountId) throws NoPermissionException {
        storeRepository.getStore(storeId).addDiscountToADDRoot(userId, discountId);

    }

    public void addIndividualPermission(int userId, int managerId, int storeId, UserPermissions.IndividualPermission individualPermission) throws NoPermissionException, ChangePermissionException {
        storeRepository.getStore(storeId).addPermissionToManager(userId, managerId, individualPermission);
    }

    public void removeIndividualPermission(int userId, int managerId, int storeId, UserPermissions.IndividualPermission individualPermission) throws NoPermissionException, ChangePermissionException {
        storeRepository.getStore(storeId).removePermissionFromManager(userId, managerId, individualPermission);
    }

    public DiscountAccumulationTreeInfo getDiscountAccumulationTree(int storeId, int userId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getDiscountAccumulationTree(userId);
    }

    public void deleteStoreAccumulationTree(int storeId, int userId) throws NoPermissionException {
        storeRepository.getStore(storeId).deleteStoreAccumulationTree(userId);
    }

}
