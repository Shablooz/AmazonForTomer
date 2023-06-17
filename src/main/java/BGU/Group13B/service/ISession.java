package BGU.Group13B.service;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.System.UserTrafficRecord;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.User.PurchaseFailedException;
import BGU.Group13B.backend.User.PurchaseHistory;
import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.backend.storePackage.newDiscoutns.DiscountInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.service.entity.ReviewService;
import BGU.Group13B.service.entity.ServiceBasketProduct;
import BGU.Group13B.service.entity.ServiceProduct;
import BGU.Group13B.service.info.DiscountAccumulationTreeInfo;
import BGU.Group13B.service.info.StoreInfo;
import BGU.Group13B.service.info.ProductInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface ISession {
    /**
     * #32
     * require 4.1
     *
     * @param userId        the user id
     * @param storeId       the store id
     * @param productName   the product id
     * @param category      the product category
     * @param price         the product price
     * @param stockQuantity the stock quantity
     * @param description   the product description
     * @return the product id
     */
    Response<Integer> addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity, String description);

    /**
     * #19
     * require 2.3
     * good for development delete this function, does not check if the item exists in a store
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @return
     */
    Response<VoidResponse> addToCart(int userId, int storeId, int productId);

    /**
     * #22
     * require 2.5
     *
     * @param userId                    the user id
     * @param creditCardNumber          the credit card number
     * @param creditCardMonth           the credit card month
     * @param creditCardYear            the credit card year
     * @param creditCardHolderFirstName the credit card holder first name
     * @param creditCardCcv             the credit card ccv
     * @param id                        the id of the card owner
     * @return total price paid by the user
     */
    double purchaseProductCart(int userId, String creditCardNumber, String creditCardMonth,
                               String creditCardYear, String creditCardHolderFirstName,
                               String creditCardCcv, String id,
                               HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsCoupons,
                               String/*store coupons*/ storeCoupon);

    Response<VoidResponse> purchaseProductCart(int userId, String creditCardNumber,
                                               String creditCardMonth, String creditCardYear,
                                               String creditCardHolderFirstName,
                                               String creditCardCVV, String id,
                                               String address, String city, String country,
                                               String zip);


    /**
     * #50
     * require 2.5.1
     *
     * @param userId        the user id
     * @param storeId       the store id
     * @param productId     the product id
     * @param proposedPrice the proposed price
     */
    Response<VoidResponse> purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice, int amount);


    void purchaseProposalApprove(int managerId, int storeId, int productId) throws NoPermissionException;

    void purchaseProposalReject(int storeId, int managerId, int bidId) throws NoPermissionException;

    /**
     * #51
     * require 2.5.2
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param quantity  the quantity of the product
     */
    void immediatePurchase(int userId, int storeId, int productId, int quantity);

    /**
     * #52
     * require 2.5.3
     *
     * @param storeManagerId the store manager id
     * @param storeId        the store id
     * @param productId      the product id
     */
    void createLotteryPurchaseForProduct(int storeManagerId, int storeId, int productId);


    /**
     * #52
     * require 2.5.3.1
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param fraction  fraction of the product price, from 0 to 1
     */
    void participateInLotteryPurchase(int userId, int storeId, int productId, double fraction);


    /**
     * #53
     * require 2.5.4
     *
     * @param storeManagerId the store manager id
     * @param storeId        the store id
     * @param productId      the product id
     * @param startingPrice  the starting price of the auction
     * @param lastDate       the last date for the auction for the product
     **/
    void createAuctionForProduct(int storeManagerId, int storeId, int productId,
                                 double startingPrice, LocalDateTime lastDate);

    /**
     * #53
     * require 2.5.4
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param newPrice  new price of the product, assert that the price is higher than the current price
     */
    void auctionPurchase(int userId, int storeId, int productId, double newPrice);


    PublicAuctionInfo getAuctionInfo(int userId, int storeId, int productId);

    /**
     * #49
     * require 6.5
     *
     * @param adminId the id of an admin
     */
    SystemInfo getSystemInformation(int adminId);


    /**
     * #15
     * require 2.1.3
     *
     * @param userId
     * @param username
     * @param password
     * @param email
     * @param answer1  - optional for security question number 1 can put empty String
     * @param answer2  - optional for security question number 2 can put empty String
     * @param answer1  - optional for security question number 3 can put empty String
     */
    void register(int userId, String username, String password, String email, String answer1, String answer2, String answer3, LocalDate birthDate);

    /**
     * #18
     * require 2.2
     *
     * @param searchWords - the search words
     * @return
     */
    Response<List<ProductInfo>> search(String searchWords);

    /**
     * #18
     * require 2.2
     *
     * @param minPrice - the minimum price of the product
     * @param maxPrice - the maximum price of the product
     */
    Response<List<ProductInfo>> filterByPriceRange(double minPrice, double maxPrice);

    /**
     * #18
     * require 2.2
     *
     * @param minRating - the minimum rating of the product
     * @param maxRating - the maximum rating of the product
     */
    Response<List<ProductInfo>> filterByProductRank(double minRating, double maxRating);

    /**
     * #18
     * require 2.2
     *
     * @param category - the category of the product
     */
    Response<List<ProductInfo>> filterByCategory(String category);

    /**
     * #18
     * require 2.2
     *
     * @param minRating - the minimum rating of the store
     * @param maxRating - the maximum rating of the store
     */
    Response<List<ProductInfo>> filterByStoreRank(double minRating, double maxRating);

    /**
     * #16
     * require 1.4
     *
     * @param userID   - current userID - of the visitor
     * @param username
     * @param password
     * @param answer1  - optional for security question number 1 can put empty String
     * @param answer2  - optional for security question number 2 can put empty String
     * @param answer3  - optional for security question number 3 can put empty String
     *                 returns the new user id - of the already existing user - need to make a uniq generator if failed returns 0
     */
    int login(int userID, String username, String password, String answer1, String answer2, String answer3);


    /**
     * #16
     * require 3.1
     *
     * @param userID - current userID - of the visitor
     *               logout as a user
     */
    void logout(int userID);


    /**
     * #24
     * require 3.2
     *
     * @param userId    this user will become the founder of the store
     * @param storeName doesn't have to be unique
     * @param category  the store category, currently a free text
     * @return the store id
     */
    Response<Integer> addStore(int userId, String storeName, String category);


    /**
     * #19
     * require 2.3
     * good for production
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     */
    Response<VoidResponse> addProductToCart(int userId, int productId, int storeId);

    Response<VoidResponse> clearMessageToReply(int userId);

    /**
     * [#28]
     *
     * @param userId    the user id
     * @param header    the header of the message
     * @param complaint the complaint
     * @return
     */
    public Response<VoidResponse> openComplaint(int userId, String header, String complaint);

    /**
     * [#47]
     *
     * @param userId the user id
     * @return the complaint message
     */
    public Response<Message> getComplaint(int userId);

    /**
     * [#47]
     *
     * @param userId     the user id
     * @param receiverId the receiver id
     * @param senderId   the sender id
     * @param messageId  the message id
     * @return
     */
    public Response<VoidResponse> markMessageAsReadAdmin(int userId, String receiverId, String senderId, int messageId);

    /**
     * [#47]
     *
     * @param userId     the user id
     * @param receiverId the receiver id
     * @param header     the header of the message
     * @param massage    the massage
     * @return
     */
    public Response<VoidResponse> sendMassageAdmin(int userId, String receiverId, String header, String massage);

    /**
     * [#47]
     *
     * @param userId the user id
     * @param answer the answer
     * @return
     */
    public Response<VoidResponse> answerComplaint(int userId, String answer);


    /**
     * @param userId the user id
     * @return the message
     */
    public Response<Message> readMessage(int userId);


    Response<VoidResponse> replayMessage(int userId, String message);

    Response<Message> readOldMessage(int userId);

    Response<VoidResponse> refreshOldMessages(int userId);

    /**
     * [#27]
     *
     * @param userId  the user id
     * @param header  the header of the message
     * @param massage the massage
     * @param storeId the store id
     * @return
     */
    public Response<VoidResponse> sendMassageStore(int userId, String header, String massage, int storeId);

    /**
     * [#42]
     *
     * @param userId  the user id
     * @param storeId the store id
     * @return the message
     */

    public Response<Message> readUnreadMassageStore(int userId, int storeId);

    /**
     * [#42]
     *
     * @param userId  the user id
     * @param storeId the store id
     * @return the message
     */

    public Response<Message> readReadMassageStore(int userId, int storeId);

    /**
     * [#42]
     *
     * @param userId the user id
     * @param answer the answer
     * @return
     */

    public Response<VoidResponse> answerQuestionStore(int userId, String answer);

    /**
     * [#42]
     *
     * @param userId  the user id
     * @param storeId the store id
     * @return
     */

    public Response<VoidResponse> refreshOldMessageStore(int userId, int storeId);


    /**
     * [#25]
     *
     * @param userId    the user id
     * @param review    the review
     * @param storeId   the store id
     * @param productId the product id
     * @return
     */

    Response<VoidResponse> addReview(int userId, String review, int storeId, int productId);

    /**
     * [#25]
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @return
     */

    public Response<VoidResponse> removeReview(int userId, int storeId, int productId);

    /**
     * [#25]
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @return the review
     */

    public Response<Review> getReview(int userId, int storeId, int productId);

    Response<List<ReviewService>> getAllReviews(int userId, int storeId, int productId);

    /**
     * [#26]
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @return the score
     */

    public Response<Float> getProductScore(int userId, int storeId, int productId);

    Response<Float> getProductScoreUser(int userId, int storeId, int productId, int userIdTarget);

    /**
     * [#26]
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param score     the score
     * @return
     */
    public Response<VoidResponse> addAndSetProductScore(int userId, int storeId, int productId, int score);

    /**
     * [#26]
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @return
     */
    public Response<VoidResponse> removeProductScore(int userId, int storeId, int productId);

    /**
     * [#26]
     *
     * @param userId  the user id
     * @param storeId the store id
     * @param score   the score
     * @return
     */
    public Response<VoidResponse> addStoreScore(int userId, int storeId, int score);

    /**
     * [#26]
     *
     * @param userId  the user id
     * @param storeId the store id
     * @return
     */
    public Response<VoidResponse> removeStoreScore(int userId, int storeId);

    /**
     * [#26]
     *
     * @param userId  the user id
     * @param storeId the store id
     * @param score   the score
     * @return
     */
    public Response<VoidResponse> modifyStoreScore(int userId, int storeId, int score);

    /**
     * @param userId  the user id
     * @param storeId the store id
     * @return the score
     **/
    public Response<Float> getStoreScore(int userId, int storeId);

    /**
     * #20
     * require 2.4
     *
     * @param userId the user id
     */
    Response<String> getCartDescription(int userId);

    Response<List<ServiceBasketProduct>> getCartContent(int userId);

    /**
     * #20
     * require 2.4
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     */
    void removeProductFromCart(int userId, int storeId, int productId);

    /**
     * #20
     * require 2.4
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param quantity  the quantity of the product
     */
    void changeProductQuantityInCart(int userId, int storeId, int productId, int quantity);


    /**
     * #32
     * require 4.1
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param name      the name of the product
     */
    Response<VoidResponse> setProductName(int userId, int storeId, int productId, String name);

    /**
     * #32
     * require 4.1
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param category  the category of the product
     */
    Response<VoidResponse> setProductCategory(int userId, int storeId, int productId, String category);

    /**
     * #32
     * require 4.1
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param price     the price of the product
     */
    Response<VoidResponse> setProductPrice(int userId, int storeId, int productId, double price);

    /**
     * #32
     * require 4.1
     *
     * @param userId        the user id
     * @param storeId       the store id
     * @param productId     the product id
     * @param stockQuantity the stock quantity of the product
     */
    Response<VoidResponse> setProductStockQuantity(int userId, int storeId, int productId, int stockQuantity);

    /**
     * #32
     * require 4.1
     *
     * @param userId      the user id
     * @param storeId     the store id
     * @param productId   the product id
     * @param description the product description
     */
    Response<VoidResponse> setProductDescription(int userId, int storeId, int productId, String description);

    /**
     * #32
     * require 4.1
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id to remove
     */
    Response<VoidResponse> removeProduct(int userId, int storeId, int productId);


    /**
     * #30
     * require 3.8
     *
     * @param userId the user id
     *               this is more of a GUI function
     */
    String getUserName(int userId);

    /**
     * #30
     * require 3.8
     *
     * @param userId      the user id
     * @param newUsername the new userName
     */
    void setUsername(int userId, String newUsername);

    /**
     * #30
     * require 3.8
     *
     * @param userId the user id
     *               to turn the user from member into admin put 1, to turn it from admin into a member put 2
     */
    void setUserStatus(int admin_id, int userId, int newStatus);

    /**
     * #30
     * require 3.8
     *
     * @param userId the user id
     *               returns the user status
     */
    String getUserStatus(int userId);


    /**
     * #30
     * require 3.8
     *
     * @param userId the user id
     *               returns the user status
     */
    String getUserEmail(int userId);


    /**
     * #30
     * require 3.8
     *
     * @param userId the user id
     *               return a list of store id - role (as String)
     */
    List<Pair<Integer, String>> getStoresOfUser(int userId);


    /**
     * #17
     * require 2.1
     *
     * @param userId  the user id
     * @param storeId the store id
     */
    Response<StoreInfo> getStoreInfo(int userId, int storeId);

    /**
     * #17
     * require 2.1
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     */
    Response<ProductInfo> getStoreProductInfo(int userId, int storeId, int productId);

    /**
     * #17
     * require 2.1
     *
     * @param storeId the store id
     */
    Response<Set<ProductInfo>> getAllStoreProductsInfo(int userId, int storeId);

    /**
     * #31
     * require 3.9
     *
     * @param userId
     */
    boolean SecurityAnswer1Exists(int userId);

    /**
     * #31
     * require 3.9
     *
     * @param userId
     */
    boolean SecurityAnswer2Exists(int userId);


    /**
     * #31
     * require 3.9
     *
     * @param userId
     */
    boolean SecurityAnswer3Exists(int userId);


    /**
     * #31
     * require 3.9
     *
     * @param userId checks if user has security questions answered
     */
    boolean checkIfQuestionsExist(int userId);


    Response<List<ServiceProduct>> getAllFailedProductsAfterPayment(int userId);

    /**
     * #13
     * require 1.1
     * returns the new user id
     */
    int enterAsGuest();

    boolean checkIfQuestionsExist(String userName);

    /**
     * #13
     * require 1.1
     *
     * @param userId the user id
     *               as a guess u can exit the system and delete all ur related data
     */
    void exitSystemAsGuest(int userId);

    /**
     * @param userId  the user id
     * @param storeId the store id
     * @return the list of the products that the user failed buy from the store
     */
    List<Integer> getFailedProducts(int userId, int storeId);


    Response<Double> getTotalPriceOfCart(int userId);

    Response<VoidResponse> cancelPurchase(int userId);

    boolean isUserLogged(int userId);


    /**
     * @param userId the user id
     * @return all the user's associated stores (all stores that the user has a role in)
     */
    Response<List<Pair<StoreInfo, String>>> getAllUserAssociatedStores(int userId);


    /**
     * #39
     * require 4.9
     *
     * @param userId  the user id (only the founder can do this action)
     * @param storeId the store id that will be hidden
     */
    Response<VoidResponse> hideStore(int userId, int storeId);

    /**
     * #40
     * require 4.10
     *
     * @param userId  the user id (only the founder can do this action)
     * @param storeId the store id that will be unhidden
     */
    Response<VoidResponse> unhideStore(int userId, int storeId);

    /**
     * #45
     * require 6.1
     *
     * @param userId  the user id (only an admin can do this action)
     * @param storeId the store id that will be deleted
     */
    Response<VoidResponse> deleteStore(int userId, int storeId);

    void pushTest();

    /**
     * @param userId the user id
     * @return the user's purchase history
     * @throws Exception
     */
    Response<List<PurchaseHistory>> getUserPurchaseHistory(int userId);

    Response<List<PurchaseHistory>>  getUserPurchaseHistoryAsAdmin(int userId, int adminId);

    Response<List<PurchaseHistory>> getStorePurchaseHistoryAsAdmin(int storeId, int adminId);


    Response<VoidResponse> fetchMessages(int userId);

    int getStoreFounder(int storeId);

    Response<Pair<Double, List<ServiceBasketProduct>>> startPurchaseBasketTransaction(int userId, List<String> coupons) throws PurchaseFailedException;

    /**
     * #43
     * require 4.13
     *
     * @param userId  the user id
     * @param storeId the store id
     * @return the store purchase history
     */
    Response<List<PurchaseHistory>> getStorePurchaseHistory(int userId, int storeId);

    /**
     * #XX
     * require XXX?
     *
     * @param userId     the user id
     * @param newOwnerId the id of the new owner
     * @param storeId    the id of the store the new owner will own
     *                   as an owner/founder you can add other users as store owners in your store
     */
    Response<VoidResponse> addOwner(int userId, int newOwnerId, int storeId);

    /**
     * #XX
     * require XXX?
     *
     * @param userId        the user id
     * @param removeOwnerId the id of the owner to be removed
     * @param storeId       the id of the store the owner will be removed from
     *                      as an owner/founder you can remove owners you assigned yourself
     */
    Response<VoidResponse> removeOwner(int userId, int removeOwnerId, int storeId);

    /**
     * #XX
     * require XXX?
     *
     * @param userId       the user id
     * @param newManagerId the id of the new manager
     * @param storeId      the id of the store the new manager will manage
     *                     as an owner/founder you can add other users as store managers in your store
     */
    Response<VoidResponse> addManager(int userId, int newManagerId, int storeId);

    /**
     * #XX
     * require XXX?
     *
     * @param userId          the user id
     * @param removeManagerId the id of the manager to be removed
     * @param storeId         the id of the store the manager will be removed from
     *                        as an owner/founder you can remove managers you assigned yourself
     */
    Response<VoidResponse> removeManager(int userId, int removeManagerId, int storeId);

    /**
     * #XX
     * require XXX?
     *
     * @param userId  the user id
     * @param storeId the id of the store to get info from
     *                as an owner/founder you can get info about the users working in the store
     */
    Response<List<WorkerCard>> getStoreWorkersInfo(int userId, int storeId);

    /**
     * <H1>Conditions</H1>
     */
    Response<VoidResponse> setPurchasePolicyCondition(int storeId, int userId, int conditionId);


    Response<Integer> addORCondition(int storeId, int userId, int condition1, int condition2);


    Response<Integer> addANDCondition(int storeId, int userId, int condition1, int condition2);


    Response<Integer> addXORCondition(int storeId, int userId, int condition1, int condition2);


    Response<Integer> addIMPLYCondition(int storeId, int userId, int condition1, int condition2);

    Response<Integer> addStorePriceCondition(int storeId, int userId, double lowerBound, double upperBound);

    Response<Integer> addStorePriceCondition(int storeId, int userId, double lowerBound);

    Response<Integer> addStoreQuantityCondition(int storeId, int userId, int lowerBound, int upperBound);

    Response<Integer> addStoreQuantityCondition(int storeId, int userId, int lowerBound);

    Response<Integer> addCategoryPriceCondition(int storeId, int userId, String category, double lowerBound, double upperBound);


    Response<Integer> addCategoryPriceCondition(int storeId, int userId, String category, double lowerBound);


    Response<Integer> addCategoryQuantityCondition(int storeId, int userId, String category, int lowerBound, int upperBound);


    Response<Integer> addCategoryQuantityCondition(int storeId, int userId, String category, int lowerBound);


    Response<Integer> addDateCondition(int storeId, int userId, LocalDateTime lowerBound, LocalDateTime upperBound);

    Response<Integer> addDateCondition(int storeId, int userId, LocalDateTime lowerBound);


    Response<Integer> addProductPriceCondition(int storeId, int userId, int productId, double lowerBound, double upperBound);


    Response<Integer> addProductPriceCondition(int storeId, int userId, int productId, double lowerBound);


    Response<Integer> addProductQuantityCondition(int storeId, int userId, int productId, int lowerBound, int upperBound);

    Response<Integer> addProductQuantityCondition(int storeId, int userId, int productId, int lowerBound);

    Response<Integer> addTimeCondition(int storeId, int userId, LocalDateTime lowerBound, LocalDateTime upperBound);


    Response<Integer> addTimeCondition(int storeId, int userId, LocalDateTime lowerBound);


    Response<Integer> addUserAgeCondition(int storeId, int userId, int lowerBound, int upperBound);

    Response<Integer> addUserAgeCondition(int storeId, int userId, int lowerBound);

    /**
     * <H1>Discounts</H1>
     */

    Response<Integer> addStoreDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String coupon);


    Response<Integer> addStoreDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String coupon);

    Response<Integer> addStoreDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate);


    Response<Integer> addStoreDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate);

    Response<Integer> addCategoryDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String category, String coupon);

    Response<Integer> addCategoryDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String category, String coupon);


    Response<Integer> addCategoryDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String category);

    Response<Integer> addCategoryDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String category);


    Response<Integer> addProductDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, int productId, String coupon);


    Response<Integer> addProductDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, int productId, String coupon);


    Response<Integer> addProductDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, int productId);


    Response<Integer> addProductDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, int productId);


    Response<List<DiscountInfo>> getStoreDiscounts(int storeId, int userId);


    Response<DiscountInfo> getDiscount(int storeId, int userId, int discountId);


    Response<VoidResponse> removeDiscount(int storeId, int userId, int discountId);

    /**
     * <H1>Discount Accumulation Tree</H1>
     */

    Response<VoidResponse> addDiscountAsRoot(int storeId, int userId, int discountId);


    Response<VoidResponse> addDiscountToXORRoot(int storeId, int userId, int discountId);


    Response<VoidResponse> addDiscountToMAXRoot(int storeId, int userId, int discountId);


    Response<VoidResponse> addDiscountToADDRoot(int storeId, int userId, int discountId);

    Response<DiscountAccumulationTreeInfo> getDiscountAccumulationTree(int storeId, int userId);

    Response<VoidResponse> deleteStoreAccumulationTree(int storeId, int userId);

    List<Integer> getStoreOwners(int storeId);

    Response<VoidResponse> addIndividualPermission(int userId, int managerId, int storeId, UserPermissions.IndividualPermission individualPermission);

    Response<VoidResponse> removeIndividualPermission(int userId, int managerId, int storeId, UserPermissions.IndividualPermission individualPermission);

    Response<Integer> getUserIdByUsername(String userName);

    Response<HashMap<Integer, String>> getUserIdsToUsernamesMapper(List<Integer> userIds);

    Response<Boolean> isStoreHidden(int storeId);

    Response<Boolean> isAdmin(int userId);

    Response<Condition> getStorePurchasePolicy(int storeId, int userId);

    Response<VoidResponse> resetStorePurchasePolicy(int storeId, int userId);

    Response<List<StoreInfo>> getAllStores();

    Response<UserTrafficRecord> getUserTrafficOfRange(int userId, LocalDate from, LocalDate to);

    Response<double[]> getStoreHistoryIncome(int storeId, int userId, LocalDate from, LocalDate to);

    Response<double[]> getSystemHistoryIncome(int userId, LocalDate from, LocalDate to);


    Response<String> getUserNameRes(int userId);
}
