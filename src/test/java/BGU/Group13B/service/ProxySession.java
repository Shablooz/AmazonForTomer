package BGU.Group13B.service;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.User.PurchaseFailedException;
import BGU.Group13B.backend.User.PurchaseHistory;
import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;
import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.newDiscoutns.DiscountInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.StoreDiscount;
import BGU.Group13B.service.entity.ReviewService;
import BGU.Group13B.service.entity.ServiceBasketProduct;
import BGU.Group13B.service.entity.ServiceProduct;
import BGU.Group13B.service.info.DiscountAccumulationTreeInfo;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.*;

public class ProxySession implements ISession {
    private ISession realSession;

    public void setRealSession(ISession realSession) {
        if (realSession != null)
            this.realSession = realSession;
    }


    @Override
    public Response<Integer> addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity, String description) {
        if (realSession != null)
            return realSession.addProduct(userId, storeId, productName, category, price, stockQuantity, description);
        return null;
    }

    @Override
    public void addToCart(int userId, int storeId, int productId) {
        if (realSession != null)
            realSession.addToCart(userId, storeId, productId);
    }

    @Override
    public double purchaseProductCart(int userId, String creditCardNumber, String creditCardMonth, String creditCardYear, String creditCardHolderFirstName, String creditCardCcv, String id, HashMap<Integer, String> productsCoupons, String storeCoupon) {
        if (realSession != null)
            return realSession.purchaseProductCart(userId, creditCardNumber, creditCardMonth, creditCardYear, creditCardHolderFirstName, creditCardCcv, id, productsCoupons, storeCoupon);
        return -1;
    }

    @Override
    public Response<VoidResponse> purchaseProductCart(int userId, String creditCardNumber, String creditCardMonth, String creditCardYear, String creditCardHolderFirstName, String creditCardCVV, String id, String address, String city, String country, String zip) {
        if (realSession != null)
            return realSession.purchaseProductCart(userId, creditCardNumber, creditCardMonth, creditCardYear, creditCardHolderFirstName, creditCardCVV, id, address, city, country, zip);
        return null;
    }


    @Override
    public Pair<Double, List<ServiceBasketProduct>> startPurchaseBasketTransaction(int userId, List<String> coupons) throws PurchaseFailedException {
        if (realSession != null)
            return realSession.startPurchaseBasketTransaction(userId, coupons);
        return null;
    }

    @Override
    public Response<List<PurchaseHistory>> getStorePurchaseHistory(int userId, int storeId) {
        if (realSession != null)
            return realSession.getStorePurchaseHistory(userId, storeId);
        return null;
    }

    @Override
    public Response<VoidResponse> addOwner(int userId, int newOwnerId, int storeId) {
        if (realSession != null)
            return realSession.addOwner(userId, newOwnerId, storeId);
        return null;
    }

    @Override
    public Response<VoidResponse> removeOwner(int userId, int removeOwnerId, int storeId) {
        if (realSession != null)
            return realSession.removeOwner(userId, removeOwnerId, storeId);
        return null;
    }

    @Override
    public Response<VoidResponse> addManager(int userId, int newManagerId, int storeId) {
        if (realSession != null)
            return realSession.addManager(userId, newManagerId, storeId);
        return null;
    }

    @Override
    public Response<VoidResponse> removeManager(int userId, int removeManagerId, int storeId) {
        if (realSession != null)
            return realSession.removeManager(userId, removeManagerId, storeId);
        return null;
    }

    @Override
    public Response<List<WorkerCard>> getStoreWorkersInfo(int userId, int storeId) {
        if (realSession != null)
            return realSession.getStoreWorkersInfo(userId, storeId);
        return null;
    }

    @Override
    public Response<VoidResponse> setPurchasePolicyCondition(int storeId, int userId, int conditionId) {
        if (realSession != null)
            return realSession.setPurchasePolicyCondition(storeId, userId, conditionId);
        return null;
    }

    @Override
    public Response<Integer> addORCondition(int storeId, int userId, int condition1, int condition2) {
        if (realSession != null)
            return realSession.addORCondition(storeId, userId, condition1, condition2);
        return null;
    }

    @Override
    public Response<Integer> addANDCondition(int storeId, int userId, int condition1, int condition2) {
        if (realSession != null)
            return realSession.addANDCondition(storeId, userId, condition1, condition2);
        return null;
    }

    @Override
    public Response<Integer> addXORCondition(int storeId, int userId, int condition1, int condition2) {
        if (realSession != null)
            return realSession.addXORCondition(storeId, userId, condition1, condition2);
        return null;
    }

    @Override
    public Response<Integer> addIMPLYCondition(int storeId, int userId, int condition1, int condition2) {
        if (realSession != null)
            return realSession.addIMPLYCondition(storeId, userId, condition1, condition2);
        return null;
    }

    @Override
    public Response<Integer> addStorePriceCondition(int storeId, int userId, double lowerBound, double upperBound) {
        if(realSession != null)
            return realSession.addStorePriceCondition(storeId, userId, lowerBound, upperBound);
        return null;
    }

    @Override
    public Response<Integer> addStorePriceCondition(int storeId, int userId, double lowerBound) {
        if (realSession != null)
            return realSession.addStorePriceCondition(storeId, userId, lowerBound);
        return null;
    }

    @Override
    public Response<Integer> addStoreQuantityCondition(int storeId, int userId, int lowerBound, int upperBound) {
        if (realSession != null)
            return realSession.addStoreQuantityCondition(storeId, userId, lowerBound, upperBound);
        return null;
    }

    @Override
    public Response<Integer> addStoreQuantityCondition(int storeId, int userId, int lowerBound) {
        if (realSession != null)
            return realSession.addStoreQuantityCondition(storeId, userId, lowerBound);
        return null;
    }

    @Override
    public Response<Integer> addCategoryPriceCondition(int storeId, int userId, String category, double lowerBound, double upperBound) {
        if (realSession != null)
            return realSession.addCategoryPriceCondition(storeId, userId, category, lowerBound, upperBound);
        return null;
    }

    @Override
    public Response<Integer> addCategoryPriceCondition(int storeId, int userId, String category, double lowerBound) {
        if (realSession != null)
            return realSession.addCategoryPriceCondition(storeId, userId, category, lowerBound);
        return null;
    }

    @Override
    public Response<Integer> addCategoryQuantityCondition(int storeId, int userId, String category, int lowerBound, int upperBound) {
        if (realSession != null)
            return realSession.addCategoryQuantityCondition(storeId, userId, category, lowerBound, upperBound);
        return null;
    }

    @Override
    public Response<Integer> addCategoryQuantityCondition(int storeId, int userId, String category, int lowerBound) {
        if (realSession != null)
            return realSession.addCategoryQuantityCondition(storeId, userId, category, lowerBound);
        return null;
    }

    @Override
    public Response<Integer> addDateCondition(int storeId, int userId, LocalDateTime lowerBound, LocalDateTime upperBound) {
        if (realSession != null)
            return realSession.addDateCondition(storeId, userId, lowerBound, upperBound);
        return null;
    }

    @Override
    public Response<Integer> addDateCondition(int storeId, int userId, LocalDateTime lowerBound) {
        if (realSession != null)
            return realSession.addDateCondition(storeId, userId, lowerBound);
        return null;
    }

    @Override
    public Response<Integer> addProductPriceCondition(int storeId, int userId, int productId, double lowerBound, double upperBound) {
        if (realSession != null)
            return realSession.addProductPriceCondition(storeId, userId, productId, lowerBound, upperBound);
        return null;
    }

    @Override
    public Response<Integer> addProductPriceCondition(int storeId, int userId, int productId, double lowerBound) {
        if (realSession != null)
            return realSession.addProductPriceCondition(storeId, userId, productId, lowerBound);
        return null;
    }

    @Override
    public Response<Integer> addProductQuantityCondition(int storeId, int userId, int productId, int lowerBound, int upperBound) {
        if (realSession != null)
            return realSession.addProductQuantityCondition(storeId, userId, productId, lowerBound, upperBound);
        return null;
    }

    @Override
    public Response<Integer> addProductQuantityCondition(int storeId, int userId, int productId, int lowerBound) {
        if (realSession != null)
            return realSession.addProductQuantityCondition(storeId, userId, productId, lowerBound);
        return null;
    }

    @Override
    public Response<Integer> addTimeCondition(int storeId, int userId, LocalDateTime lowerBound, LocalDateTime upperBound) {
        if (realSession != null)
            return realSession.addTimeCondition(storeId, userId, lowerBound, upperBound);
        return null;
    }

    @Override
    public Response<Integer> addTimeCondition(int storeId, int userId, LocalDateTime lowerBound) {
        if (realSession != null)
            return realSession.addTimeCondition(storeId, userId, lowerBound);
        return null;
    }

    @Override
    public Response<Integer> addUserAgeCondition(int storeId, int userId, int lowerBound, int upperBound) {
        if (realSession != null)
            return realSession.addUserAgeCondition(storeId, userId, lowerBound, upperBound);
        return null;
    }

    @Override
    public Response<Integer> addUserAgeCondition(int storeId, int userId, int lowerBound) {
        if (realSession != null)
            return realSession.addUserAgeCondition(storeId, userId, lowerBound);
        return null;
    }

    @Override
    public Response<Integer> addStoreDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String coupon) {
        if (realSession != null)
            return realSession.addStoreDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, coupon);
        return null;
    }

    @Override
    public Response<Integer> addStoreDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String coupon) {
        if (realSession != null)
            return realSession.addStoreDiscount(storeId, userId, discountPercentage, expirationDate, coupon);
        return null;
    }

    @Override
    public Response<Integer> addStoreDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate) {
        if (realSession != null)
            return realSession.addStoreDiscount(storeId, userId, conditionId, discountPercentage, expirationDate);
        return null;
    }

    @Override
    public Response<Integer> addStoreDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate) {
        if (realSession != null)
            return realSession.addStoreDiscount(storeId, userId, discountPercentage, expirationDate);
        return null;
    }

    @Override
    public Response<Integer> addCategoryDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        if (realSession != null)
            return realSession.addCategoryDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, category, coupon);
        return null;
    }

    @Override
    public Response<Integer> addCategoryDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        if (realSession != null)
            return realSession.addCategoryDiscount(storeId, userId, discountPercentage, expirationDate, category, coupon);
        return null;
    }

    @Override
    public Response<Integer> addCategoryDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String category) {
        if (realSession != null)
            return realSession.addCategoryDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, category);
        return null;
    }

    @Override
    public Response<Integer> addCategoryDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String category) {
        if (realSession != null)
            return realSession.addCategoryDiscount(storeId, userId, discountPercentage, expirationDate, category);
        return null;
    }

    @Override
    public Response<Integer> addProductDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        if (realSession != null)
            return realSession.addProductDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, productId, coupon);
        return null;
    }

    @Override
    public Response<Integer> addProductDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        if (realSession != null)
            return realSession.addProductDiscount(storeId, userId, discountPercentage, expirationDate, productId, coupon);

        return null;
    }

    @Override
    public Response<Integer> addProductDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, int productId) {
        if (realSession != null)
            return realSession.addProductDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, productId);

        return null;
    }

    @Override
    public Response<Integer> addProductDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, int productId) {
        if (realSession != null)
            return realSession.addProductDiscount(storeId, userId, discountPercentage, expirationDate, productId);

        return null;
    }

    @Override
    public Response<List<DiscountInfo>> getStoreDiscounts(int storeId, int userId) {
        if (realSession != null)
            return realSession.getStoreDiscounts(storeId, userId);

        return null;
    }

    @Override
    public Response<DiscountInfo> getDiscount(int storeId, int userId, int discountId) {
        if (realSession != null)
            return realSession.getDiscount(storeId, userId, discountId);

        return null;
    }

    @Override
    public Response<VoidResponse> removeDiscount(int storeId, int userId, int discountId) {
        if (realSession != null)
            return realSession.removeDiscount(storeId, userId, discountId);

        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> addDiscountAsRoot(int storeId, int userId, int discountId) {
        if (realSession != null)
            return realSession.addDiscountAsRoot(storeId, userId, discountId);

        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> addDiscountToXORRoot(int storeId, int userId, int discountId) {
        if (realSession != null)
            return realSession.addDiscountToXORRoot(storeId, userId, discountId);

        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> addDiscountToMAXRoot(int storeId, int userId, int discountId) {
        if (realSession != null)
            return realSession.addDiscountToMAXRoot(storeId, userId, discountId);

        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> addDiscountToADDRoot(int storeId, int userId, int discountId) {
        if (realSession != null)
            return realSession.addDiscountToADDRoot(storeId, userId, discountId);

        return Response.success(new VoidResponse());
    }

    @Override
    public Response<DiscountAccumulationTreeInfo> getDiscountAccumulationTree(int storeId, int userId) {
        if (realSession != null)
            return realSession.getDiscountAccumulationTree(storeId, userId);

        return null;
    }

    @Override
    public Response<VoidResponse> deleteStoreAccumulationTree(int storeId, int userId) {
        if (realSession != null)
            return realSession.deleteStoreAccumulationTree(storeId, userId);

        return Response.success(new VoidResponse());
    }

    @Override
    public List<Integer> getStoreOwners(int storeId) {
        if (realSession != null)
            return realSession.getStoreOwners(storeId);

        return null;
    }

    @Override
    public Response<VoidResponse> addIndividualPermission(int userId, int managerId, int storeId, UserPermissions.IndividualPermission individualPermission) {
        if (realSession != null)
            return realSession.addIndividualPermission(userId, managerId, storeId, individualPermission);

        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> removeIndividualPermission(int userId, int managerId, int storeId, UserPermissions.IndividualPermission individualPermission) {
        if (realSession != null)
            return realSession.removeIndividualPermission(userId, managerId, storeId, individualPermission);

        return Response.success(new VoidResponse());
    }

    @Override
    public Response<Integer> getUserIdByUsername(String userName) {
        if (realSession != null)
            return realSession.getUserIdByUsername(userName);

        return null;
    }

    @Override
    public Response<HashMap<Integer, String>> getUserIdsToUsernamesMapper(List<Integer> userIds) {
        if (realSession != null)
            return realSession.getUserIdsToUsernamesMapper(userIds);

        return null;
    }

    @Override
    public Response<Boolean> isStoreHidden(int storeId) {
        if (realSession != null)
            return realSession.isStoreHidden(storeId);

        return null;
    }

    @Override
    public Response<Boolean> isAdmin(int userId) {
        if (realSession != null)
            return realSession.isAdmin(userId);

        return null;
    }

    @Override
    public Response<Condition> getStorePurchasePolicy(int storeId, int userId) {
        if (realSession != null)
            return realSession.getStorePurchasePolicy(storeId, userId);

        return null;
    }


    @Override
    public void purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice, int amount) {
        if (realSession != null)
            realSession.purchaseProposalSubmit(userId, storeId, productId, proposedPrice, amount);
    }

    @Override
    public void purchaseProposalApprove(int managerId, int storeId, int productId) throws NoPermissionException {
        if(realSession != null)
            realSession.purchaseProposalApprove(managerId, storeId, productId);
    }

    @Override
    public void purchaseProposalReject(int storeId, int managerId, int bidId) throws NoPermissionException {
        if(realSession != null)
            realSession.purchaseProposalReject(storeId, managerId, bidId);
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
    public void register(int userId, String username, String password, String email, String answer1, String answer2, String answer3, LocalDate dateOfBirth) {
        if (realSession != null)
            realSession.register(userId, username, password, email, answer1, answer2, answer3, dateOfBirth);
    }

    @Override
    public Response<List<ProductInfo>> search(String searchWords) {
        if (realSession != null)
            realSession.search(searchWords);
        return null;
    }

    @Override
    public Response<List<ProductInfo>> filterByPriceRange(int minPrice, int maxPrice) {
        if (realSession != null)
            realSession.filterByPriceRange(minPrice, maxPrice);
        return null;
    }

    @Override
    public Response<List<ProductInfo>> filterByProductRank(int minRating, int maxRating) {
        if (realSession != null)
            realSession.filterByProductRank(minRating, maxRating);
        return null;
    }

    @Override
    public Response<List<ProductInfo>> filterByCategory(String category) {
        if (realSession != null)
            realSession.filterByCategory(category);
        return null;
    }

    @Override
    public Response<List<ProductInfo>> filterByStoreRank(int minRating, int maxRating) {
        if (realSession != null)
            realSession.filterByStoreRank(minRating, maxRating);
        return null;
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
    public Response<Integer> addStore(int userId, String storeName, String category) {
        if (realSession != null)
            return realSession.addStore(userId, storeName, category);
        return null;
    }

    @Override
    public Response<VoidResponse> addProductToCart(int userId, int productId, int storeId) {
        if (realSession != null)
            return realSession.addProductToCart(userId, productId, storeId);
        return Response.success(new VoidResponse());
    }


    @Override
    public Response<VoidResponse> openComplaint(int userId, String header, String complaint) {
        if (realSession != null)
            return realSession.openComplaint(userId, header, complaint);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<Message> getComplaint(int userId) {
        if (realSession != null)
            return realSession.getComplaint(userId);
        return Response.success(Message.constractMessage("", -1, "", "", ""));
    }

    @Override
    public Response<VoidResponse> markMessageAsReadAdmin(int userId, String receiverId, String senderId, int messageId) {
        if (realSession != null)
            return realSession.markMessageAsReadAdmin(userId, receiverId, senderId, messageId);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> sendMassageAdmin(int userId, String receiverId, String header, String massage) {
        if (realSession != null)
            return realSession.sendMassageAdmin(userId, receiverId, header, massage);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> answerComplaint(int userId, String answer) {
        if (realSession != null)
            return realSession.answerComplaint(userId, answer);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<Message> readMessage(int userId) {
        if (realSession != null)
            return realSession.readMessage(userId);
        return Response.success(Message.constractMessage("", -1, "", "", ""));
    }

    @Override
    public Response<VoidResponse> clearMessageToReply(int userId) {
        if (realSession != null)
            return realSession.clearMessageToReply(userId);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> replayMessage(int userId, String massage) {
        if (realSession != null)
            return realSession.replayMessage(userId, massage);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<Message> readOldMessage(int userId) {
        if (realSession != null)
            return realSession.readOldMessage(userId);
        return Response.success(Message.constractMessage("", -1, "", "", ""));
    }

    @Override
    public Response<VoidResponse> refreshOldMessages(int userId) {
        if (realSession != null)
            return realSession.refreshOldMessages(userId);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> sendMassageStore(int userId, String header, String massage, int storeId) {
        if (realSession != null)
            return realSession.sendMassageStore(userId, header, massage, storeId);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<Message> readUnreadMassageStore(int userId, int storeId) {
        if (realSession != null)
            return realSession.readUnreadMassageStore(userId, storeId);
        return Response.success(Message.constractMessage("", -1, "", "", ""));
    }

    @Override
    public Response<Message> readReadMassageStore(int userId, int storeId) {
        if (realSession != null)
            return realSession.readReadMassageStore(userId, storeId);
        return Response.success(Message.constractMessage("", -1, "", "", ""));
    }

    @Override
    public Response<VoidResponse> answerQuestionStore(int userId, String answer) {
        if (realSession != null)
            return realSession.answerQuestionStore(userId, answer);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> refreshOldMessageStore(int userId, int storeId) {
        if (realSession != null)
            return realSession.refreshOldMessageStore(userId, storeId);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> addReview(int userId, String review, int storeId, int productId) {
        if (realSession != null)
            return realSession.addReview(userId, review, storeId, productId);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> removeReview(int userId, int storeId, int productId) {
        if (realSession != null)
            return realSession.removeReview(userId, storeId, productId);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<Review> getReview(int userId, int storeId, int productId) {
        if (realSession != null)
            return realSession.getReview(userId, storeId, productId);
        return Response.success(new Review("", -1, -1, -1));
    }

    @Override
    public Response<List<ReviewService>> getAllReviews(int userId, int storeId, int productId) {
        if (realSession != null)
            return realSession.getAllReviews(userId, storeId, productId);
        return Response.success(null);
    }

    @Override
    public Response<Float> getProductScore(int userId, int storeId, int productId) {
        if (realSession != null)
            return realSession.getProductScore(userId, storeId, productId);
        return Response.success((float) -1);
    }
    @Override
    public Response<Float> getProductScoreUser(int userId, int storeId, int productId,int userIdTarget) {
        if (realSession != null)
            return realSession.getProductScoreUser(userId, storeId, productId,userIdTarget);
        return Response.success((float) -1);
    }

    @Override
    public Response<VoidResponse> addAndSetProductScore(int userId, int storeId, int productId, int score) {
        if (realSession != null)
            return realSession.addAndSetProductScore(userId, storeId, productId, score);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> removeProductScore(int userId, int storeId, int productId) {
        if (realSession != null)
            return realSession.removeProductScore(userId, storeId, productId);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> addStoreScore(int userId, int storeId, int score) {
        if (realSession != null)
            return realSession.addStoreScore(userId, storeId, score);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> removeStoreScore(int userId, int storeId) {
        if (realSession != null)
            return realSession.removeStoreScore(userId, storeId);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<VoidResponse> modifyStoreScore(int userId, int storeId, int score) {
        if (realSession != null)
            return realSession.modifyStoreScore(userId, storeId, score);
        return Response.success(new VoidResponse());
    }

    @Override
    public Response<Float> getStoreScore(int userId, int storeId) {
        if (realSession != null)
            return realSession.getStoreScore(userId, storeId);
        return Response.success((float) -1);
    }

    @Override
    public Response<String> getCartDescription(int userId) {
        if (realSession != null)
            realSession.getCartDescription(userId);
        return null;
    }

    @Override
    public Response<List<ServiceBasketProduct>> getCartContent(int userId) {
        if (realSession != null)
            return realSession.getCartContent(userId);
        return null;
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
    public Response<VoidResponse> setProductName(int userId, int storeId, int productId, String name) {
        if (realSession != null)
            return realSession.setProductName(userId, storeId, productId, name);
        return null;
    }

    @Override
    public Response<VoidResponse> setProductCategory(int userId, int storeId, int productId, String category) {
        if (realSession != null)
            return realSession.setProductCategory(userId, storeId, productId, category);
        return null;
    }

    @Override
    public Response<VoidResponse> setProductPrice(int userId, int storeId, int productId, double price) {
        if (realSession != null)
            return realSession.setProductPrice(userId, storeId, productId, price);
        return null;
    }

    @Override
    public Response<VoidResponse> setProductStockQuantity(int userId, int storeId, int productId, int stockQuantity) {
        if (realSession != null)
            return realSession.setProductStockQuantity(userId, storeId, productId, stockQuantity);
        return null;
    }


    @Override
    public Response<VoidResponse> setProductDescription(int userId, int storeId, int productId, String description) {
        if (realSession != null)
            return realSession.setProductDescription(userId, storeId, productId, description);
        return null;
    }

    @Override
    public Response<VoidResponse> removeProduct(int userId, int storeId, int productId) {
        if (realSession != null)
            return realSession.removeProduct(userId, storeId, productId);
        return null;
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

    @Override
    public Response<StoreInfo> getStoreInfo(int userId, int storeId) {
        if (realSession != null)
            return realSession.getStoreInfo(userId, storeId);
        return null;
    }

    @Override
    public Response<ProductInfo> getStoreProductInfo(int userId, int storeId, int productId) {
        if (realSession != null)
            return realSession.getStoreProductInfo(userId, storeId, productId);
        return null;
    }

    @Override
    public Response<Set<ProductInfo>> getAllStoreProductsInfo(int userId, int storeId) {
        if (realSession != null)
            return realSession.getAllStoreProductsInfo(userId, storeId);
        return null;
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
    public List<ServiceProduct> getAllFailedProductsAfterPayment(int userId) {
        if (realSession != null)
            return realSession.getAllFailedProductsAfterPayment(userId);
        return new ArrayList<>();
    }

    @Override
    public int enterAsGuest() {
        if (realSession != null)
            return realSession.enterAsGuest();
        return -1;
    }

    @Override
    public boolean checkIfQuestionsExist(String userName) {
        return realSession.checkIfQuestionsExist(userName);
    }

    @Override
    public void exitSystemAsGuest(int userId) {
        if (realSession != null)
            realSession.exitSystemAsGuest(userId);
    }

    @Override
    public List<Integer> getFailedProducts(int userId, int storeId) {
        if (realSession != null)
            return realSession.getFailedProducts(userId, storeId);
        return new ArrayList<>();
    }

    @Override
    public double getTotalPriceOfCart(int userId) {
        if (realSession != null)
            return realSession.getTotalPriceOfCart(userId);
        return -1;
    }

    @Override
    public void cancelPurchase(int userId) {
        if (realSession != null)
            realSession.cancelPurchase(userId);
    }

    @Override
    public boolean isUserLogged(int userId) {
        if (realSession != null)
            return realSession.isUserLogged(userId);
        return false;
    }

    @Override
    public Response<String> getUserPurchaseHistory(int userId) {
        if (realSession != null)
            return realSession.getUserPurchaseHistory(userId);
        return null;
    }

    @Override
    public Response<String> getUserPurchaseHistoryAsAdmin(int userId, int adminId) {
        if (realSession != null)
            return realSession.getUserPurchaseHistoryAsAdmin(userId, adminId);
        return null;
    }

    @Override
    public Response<List<PurchaseHistory>> getStorePurchaseHistoryAsAdmin(int storeId, int adminId) {
        if (realSession != null)
            return realSession.getStorePurchaseHistoryAsAdmin(storeId, adminId);
        return null;
    }

    @Override
    public int getStoreFounder(int storeId) {
        if (realSession != null)
            return realSession.getStoreFounder(storeId);
        return -1;
    }

    @Override
    public Response<List<Pair<StoreInfo, String>>> getAllUserAssociatedStores(int userId) {
        if (realSession != null)
            return realSession.getAllUserAssociatedStores(userId);
        return null;
    }


    @Override
    public Response<VoidResponse> hideStore(int userId, int storeId) {
        if (realSession != null)
            return realSession.hideStore(userId, storeId);
        return null;
    }

    @Override
    public Response<VoidResponse> unhideStore(int userId, int storeId) {
        if (realSession != null)
            return realSession.unhideStore(userId, storeId);
        return null;
    }

    @Override
    public Response<VoidResponse> deleteStore(int userId, int storeId) {
        if (realSession != null)
            return realSession.deleteStore(userId, storeId);
        return null;
    }

    @Override
    public void pushTest() {
        realSession.pushTest();
    }

    @Override
    public Response<VoidResponse> fetchMessages(int userId) {
        return realSession.fetchMessages(userId);
    }

}
