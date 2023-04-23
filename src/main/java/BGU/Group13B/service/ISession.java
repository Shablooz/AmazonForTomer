package BGU.Group13B.service;

import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;
import org.springframework.data.util.Pair;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;

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
     * @return              the product id
     */
    int addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity, String description);

    /**
     * #19
     * require 2.3
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     */
    void addToCart(int userId, int storeId, int productId);

    /**
     * #22
     * require 2.5
     *
     * @param userId                    the user id
     * @param address                   the address of the user
     * @param creditCardNumber          the credit card number
     * @param creditCardMonth           the credit card month
     * @param creditCardYear            the credit card year
     * @param creditCardHolderFirstName the credit card holder first name
     * @param creditCardHolderLastName  the credit card holder last name
     * @param creditCardCcv             the credit card ccv
     * @param id                        the id of the card owner
     * @param creditCardType            the credit card type
     * @return total price paid by the user
     */
    double purchaseProductCart(int userId, String address, String creditCardNumber, String creditCardMonth,
                             String creditCardYear, String creditCardHolderFirstName,
                             String creditCardHolderLastName, String creditCardCcv, String id,
                             String creditCardType, HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsCoupons,
                             String/*store coupons*/ storeCoupon);

    /**
     * #50
     * require 2.5.1
     *
     * @param userId        the user id
     * @param storeId       the store id
     * @param productId     the product id
     * @param proposedPrice the proposed price
     */
    void purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice, int amount);


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
    void register(int userId, String username, String password, String email, String answer1, String answer2, String answer3);

    /**
     * #18
     * require 2.2
     *
     * @param productName - the name of the product
     */
    void searchProductByName(String productName);

    /**
     * #18
     * require 2.2
     *
     * @param category - the category of the product
     */
    void searchProductByCategory(String category);

    /**
     * #18
     * require 2.2
     *
     * @param keywords - list of keywords
     */
    void searchProductByKeywords(List<String> keywords);

    /**
     * #18
     * require 2.2
     *
     * @param minPrice - the minimum price of the product
     * @param maxPrice - the maximum price of the product
     */
    void filterByPriceRange(int minPrice, int maxPrice);

    /**
     * #18
     * require 2.2
     *
     * @param minRating - the minimum rating of the product
     * @param maxRating - the maximum rating of the product
     */
    void filterByProductRank(int minRating, int maxRating);

    /**
     * #18
     * require 2.2
     *
     * @param category - the category of the product
     */
    void filterByCategory(String category);

    /**
     * #18
     * require 2.2
     *
     * @param minRating - the minimum rating of the store
     * @param maxRating - the maximum rating of the store
     */
    void filterByStoreRank(int minRating, int maxRating);

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
    int addStore(int userId, String storeName, String category);


    /**
     * #19
     * require 2.3
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     */
    void addProductToCart(int userId, int productId, int storeId);


    void getUserPurchaseHistory(int userId);

    /**
     * [#28]
     *
     * @param userId    the user id
     * @param header    the header of the message
     * @param complaint the complaint
     */
    public void openComplaint(int userId, String header, String complaint);

    /**
     * [#47]
     *
     * @param userId the user id
     * @return the complaint message
     */
    public Message getComplaint(int userId);

    /**
     * [#47]
     *
     * @param userId     the user id
     * @param receiverId the receiver id
     * @param senderId   the sender id
     * @param messageId  the message id
     */
    public void markMessageAsReadAdmin(int userId, String receiverId, String senderId, int messageId);

    /**
     * [#47]
     *
     * @param userId     the user id
     * @param receiverId the receiver id
     * @param header     the header of the message
     * @param massage    the massage
     */
    public void sendMassageAdmin(int userId, String receiverId, String header, String massage);

    /**
     * [#47]
     *
     * @param userId the user id
     * @param answer the answer
     */
    public void answerComplaint(int userId, String answer);


    /**
     * @param userId the user id
     * @return the message
     */
    public Message readMessage(int userId);


    /**
     * [#27]
     *
     * @param userId  the user id
     * @param header  the header of the message
     * @param massage the massage
     * @param storeId the store id
     */
    public void sendMassageStore(int userId, String header, String massage, int storeId);

    /**
     * [#42]
     *
     * @param userId  the user id
     * @param storeId the store id
     * @return the message
     */

    public Message readUnreadMassageStore(int userId, int storeId);

    /**
     * [#42]
     *
     * @param userId  the user id
     * @param storeId the store id
     * @return the message
     */

    public Message readReadMassageStore(int userId, int storeId);

    /**
     * [#42]
     *
     * @param userId the user id
     * @param answer the answer
     */

    public void answerQuestionStore(int userId, String answer);

    /**
     * [#42]
     *
     * @param userId  the user id
     * @param storeId the store id
     */

    public void refreshOldMessageStore(int userId, int storeId);


    /**
     * [#25]
     *
     * @param userId    the user id
     * @param review    the review
     * @param storeId   the store id
     * @param productId the product id
     */

    void addReview(int userId, String review, int storeId, int productId);

    /**
     * [#25]
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     */

    public void removeReview(int userId, int storeId, int productId);

    /**
     * [#25]
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @return the review
     */

    public Review getReview(int userId, int storeId, int productId);

    /**
     * [#26]
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @return the score
     */

    public float getProductScore(int userId, int storeId, int productId);

    /**
     * [#26]
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param score     the score
     */
    public void addAndSetProductScore(int userId, int storeId, int productId, int score);

    /**
     * [#26]
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     */
    public void removeProductScore(int userId, int storeId, int productId);

    /**
     * [#26]
     *
     * @param userId  the user id
     * @param storeId the store id
     * @param score   the score
     */
    public void addStoreScore(int userId, int storeId, int score);

    /**
     * [#26]
     *
     * @param userId  the user id
     * @param storeId the store id
     */
    public void removeStoreScore(int userId, int storeId);

    /**
     * [#26]
     *
     * @param userId  the user id
     * @param storeId the store id
     * @param score   the score
     */
    public void modifyStoreScore(int userId, int storeId, int score);

    /**
     * @param userId  the user id
     * @param storeId the store id
     * @return the score
     **/
    public float getStoreScore(int userId, int storeId);

    /**
     * #20
     * require 2.4
     *
     * @param userId the user id
     */
    void getCartDescription(int userId);

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
    void setProductName(int userId, int storeId, int productId, String name);

    /**
     * #32
     * require 4.1
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param category  the category of the product
     */
    void setProductCategory(int userId, int storeId, int productId, String category);

    /**
     * #32
     * require 4.1
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param price     the price of the product
     */
    void setProductPrice(int userId, int storeId, int productId, double price);

    /**
     * #32
     * require 4.1
     *
     * @param userId        the user id
     * @param storeId       the store id
     * @param productId     the product id
     * @param stockQuantity the stock quantity of the product
     */
    void setProductStockQuantity(int userId, int storeId, int productId, int stockQuantity);

    /**
     * #32
     * require 4.1
     *
     * @param userId        the user id
     * @param storeId       the store id
     * @param productId     the product id
     * @param description   the product description
     */
    void setProductDescription(int userId, int storeId, int productId, String description);

    /**
     * #13
     * require 1.1
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id to remove
     */
    void removeProduct(int userId, int storeId, int productId);


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
     * @param storeId the store id
     */
    StoreInfo getStoreInfo(int storeId);

    /**
     * #17
     * require 2.1
     *
     * @param storeId the store id
     */
    String getStoreName(int storeId);

    /**
     * #17
     * require 2.1
     *
     * @param storeId the store id
     */
    String getStoreCategory(int storeId);

    /**
     * #17
     * require 2.1
     *
     * @param storeId   the store id
     * @param productId the product id
     */
    ProductInfo getStoreProductInfo(int storeId, int productId);

    /**
     * #17
     * require 2.1
     *
     * @param productId the product id
     */
    ProductInfo getProductInfo(int productId);

    /**
     * #17
     * require 2.1
     *
     * @param productId the product id
     */
    String getProductName(int productId);

    /**
     * #17
     * require 2.1
     *
     * @param productId the product id
     */
    String getProductCategory(int productId);

    /**
     * #17
     * require 2.1
     *
     * @param productId the product id
     */
    double getProductPrice(int productId);

    /**
     * #17
     * require 2.1
     *
     * @param productId the product id
     */
    int getProductStockQuantity(int productId);

    /**
     * #17
     * require 2.1
     *
     * @param productId the product id
     */
    float getProductScore(int productId);

    /**
     * #17
     * require 2.1
     *
     * @param storeId the store id
     */
    Set<ProductInfo> getAllStoreProductsInfo(int storeId);

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


    /**
     * #33
     * require 4.3
     *
     * @param userId  the user id
     * @param storeId the store id
     */
    void allowPurchasePolicyConflicts(int userId, int storeId);

    /**
     * #33
     * require 4.3
     *
     * @param userId  the user id
     * @param storeId the store id
     */
    void disallowPurchasePolicyConflicts(int userId, int storeId);

    /**
     * #33
     * require 4.3
     *
     * @param userId     the user id
     * @param storeId    the store id
     * @param upperBound the upper bound of the quantity
     */
    void setStorePurchaseQuantityUpperBound(int userId, int storeId, int upperBound);

    /**
     * #33
     * require 4.3
     *
     * @param userId     the user id
     * @param storeId    the store id
     * @param lowerBound the lower bound of the quantity
     */
    void setStorePurchaseQuantityLowerBound(int userId, int storeId, int lowerBound);

    /**
     * #33
     * require 4.3
     *
     * @param userId     the user id
     * @param storeId    the store id
     * @param lowerBound the lower bound of the quantity
     * @param upperBound the upper bound of the quantity
     */
    void setStorePurchaseQuantityBounds(int userId, int storeId, int lowerBound, int upperBound);

    /**
     * #33
     * require 4.3
     *
     * @param userId     the user id
     * @param storeId    the store id
     * @param upperBound the upper bound of the price
     */
    void setStorePurchasePriceUpperBound(int userId, int storeId, int upperBound);

    /**
     * #33
     * require 4.3
     *
     * @param userId     the user id
     * @param storeId    the store id
     * @param lowerBound the lower bound of the price
     */
    void setStorePurchasePriceLowerBound(int userId, int storeId, int lowerBound);

    /**
     * #33
     * require 4.3
     *
     * @param userId     the user id
     * @param storeId    the store id
     * @param lowerBound the lower bound of the price
     * @param upperBound the upper bound of the price
     */
    void setStorePurchasePriceBounds(int userId, int storeId, int lowerBound, int upperBound);

    /**
     * #33
     * require 4.3
     *
     * @param userId     the user id
     * @param storeId    the store id
     * @param productId  the product id
     * @param upperBound the upper bound of the quantity
     */
    void setProductPurchaseQuantityUpperBound(int userId, int storeId, int productId, int upperBound);

    /**
     * #33
     * require 4.3
     *
     * @param userId     the user id
     * @param storeId    the store id
     * @param productId  the product id
     * @param lowerBound the lower bound of the quantity
     */
    void setProductPurchaseQuantityLowerBound(int userId, int storeId, int productId, int lowerBound);

    /**
     * #33
     * require 4.3
     *
     * @param userId     the user id
     * @param storeId    the store id
     * @param productId  the product id
     * @param lowerBound the lower bound of the quantity
     * @param upperBound the upper bound of the quantity
     */
    void setProductPurchaseQuantityBounds(int userId, int storeId, int productId, int lowerBound, int upperBound);

    /**
     * #33
     * require 4.3
     *
     * @param userId     the user id
     * @param storeId    the store id
     * @param productId  the product id
     * @param upperBound the upper bound of the price
     */
    void setProductPurchasePriceUpperBound(int userId, int storeId, int productId, int upperBound);

    /**
     * #33
     * require 4.3
     *
     * @param userId     the user id
     * @param storeId    the store id
     * @param productId  the product id
     * @param lowerBound the lower bound of the price
     */
    void setProductPurchasePriceLowerBound(int userId, int storeId, int productId, int lowerBound);

    /**
     * #33
     * require 4.3
     *
     * @param userId     the user id
     * @param storeId    the store id
     * @param productId  the product id
     * @param lowerBound the lower bound of the price
     * @param upperBound the upper bound of the price
     */
    void setProductPurchasePriceBounds(int userId, int storeId, int productId, int lowerBound, int upperBound);

    /**
     * #33
     * require 4.3
     *
     * @param userId             the user id
     * @param storeId            the store id
     * @param discountPercentage the discount percentage
     * @param discountLastDate   the discount last date. when expired, the discount will be removed
     * @return the discount id
     */
    int addStoreVisibleDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate);

    /**
     * #33
     * require 4.3
     *
     * @param userId              the user id
     * @param storeId             the store id
     * @param discountPercentage  the discount percentage
     * @param discountLastDate    the discount last date. when expired, the discount will be removed
     * @param minPriceForDiscount the minimum price for the discount to be applied
     * @param quantityForDiscount the quantity for the discount to be applied
     * @return the discount id
     */
    int addStoreConditionalDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount);

    /**
     * #33
     * require 4.3
     *
     * @param userId             the user id
     * @param storeId            the store id
     * @param discountPercentage the discount percentage
     * @param discountLastDate   the discount last date. when expired, the discount will be removed
     * @param code               the code for the discount
     * @return the discount id
     */
    int addStoreHiddenDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate, String code);

    /**
     * #33
     * require 4.3
     *
     * @param userId     the user id
     * @param storeId    the store id
     * @param discountId the discount id to be removed
     */
    void removeStoreDiscount(int userId, int storeId, int discountId);

    /**
     * #33
     * require 4.3
     *
     * @param userId             the user id
     * @param storeId            the store id
     * @param productId          the product id
     * @param discountPercentage the discount percentage
     * @param discountLastDate   the discount last date. when expired, the discount will be removed
     * @return the discount id
     */
    int addProductVisibleDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate);

    /**
     * #33
     * require 4.3
     *
     * @param userId              the user id
     * @param storeId             the store id
     * @param productId           the product id
     * @param discountPercentage  the discount percentage
     * @param discountLastDate    the discount last date. when expired, the discount will be removed
     * @param minPriceForDiscount the minimum price for the discount to be applied
     * @param quantityForDiscount the quantity for the discount to be applied
     * @return the discount id
     */
    int addProductConditionalDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount);

    /**
     * #33
     * require 4.3
     *
     * @param userId             the user id
     * @param storeId            the store id
     * @param productId          the product id
     * @param discountPercentage the discount percentage
     * @param discountLastDate   the discount last date. when expired, the discount will be removed
     * @param code               the code for the discount
     * @return the discount id
     */
    int addProductHiddenDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate, String code);

    /**
     * #33
     * require 4.3
     *
     * @param userId     the user id
     * @param storeId    the store id
     * @param productId  the product id
     * @param discountId the discount id to be removed
     */
    void removeProductDiscount(int userId, int storeId, int productId, int discountId);

    /**
     * #13
     * require 1.1
     * returns the new user id
     */
    int enterAsGuest();

    /**
     * #13
     * require 1.1
     *
     * @param userId the user id
     *               as a guess u can exit the system and delete all ur related data
     */
    void exitSystemAsGuest(int userId);

    /**
     * @param userId the user id
     * @param storeId the store id
     * @return the list of the products that the user failed buy from the store
     * */
    List<Integer> getFailedProducts(int userId, int storeId);

    /**
     * @param userId the user id
     * @param storeId the store id
     * @param productId the product id
     * @throws Exception if the product is not in the store
     *
     * */

}
