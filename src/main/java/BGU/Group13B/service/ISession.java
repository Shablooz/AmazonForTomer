package BGU.Group13B.service;

import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;

import java.time.LocalDateTime;

import java.util.List;

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
     */
    void addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity, String description);

    /**
     * #19
     * require 2.3
     *
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param quantity  the quantity
     */
    void addToCart(int userId, int storeId, int productId, int quantity);

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
     */
    void purchaseProductCart(int userId, String address, String creditCardNumber, String creditCardMonth, String creditCardYear, String creditCardHolderFirstName, String creditCardHolderLastName, String creditCardCcv, String id, String creditCardType);

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
     * @param userId
     * @param username
     * @param password
     * @param email
     * */
    void register(int userId,String username,String password,String email);

    /**
     * #18
     * require 2.2
     * @param productName - the name of the product
     */
    void searchProductByName(String productName);

    /**
     * #18
     * require 2.2
     * @param category - the category of the product
     */
    void searchProductByCategory(String category);

    /**
     * #18
     * require 2.2
     * @param keywords - list of keywords
     */
    void searchProductByKeywords(List<String> keywords);

    /**
     * #18
     * require 2.2
     * @param minPrice - the minimum price of the product
     * @param maxPrice - the maximum price of the product
     */
    void filterByPriceRange(int minPrice, int maxPrice);

    /**
     * #18
     * require 2.2
     * @param minRating - the minimum rating of the product
     * @param maxRating - the maximum rating of the product
     */
    void filterByProductRank(int minRating, int maxRating);

    /**
     * #18
     * require 2.2
     * @param category - the category of the product
     */
    void filterByCategory(String category);

    /**
     * #18
     * require 2.2
     * @param minRating - the minimum rating of the store
     * @param maxRating - the maximum rating of the store
     */
    void filterByStoreRank(int minRating, int maxRating);

    /**
     * #16
     * require 1.4
     * @param userID - current userID - of the visitor
     * @param username
     * @param password
     * returns the new user id - of the already existing user - need to make a uniq generator if failed returns 0
     * */
    int login(int userID,String username,String password);


    /**
     * #16
     * require 3.1
     * @param userID - current userID - of the visitor
     * logout as a user
     * */
    void logout(int userID);



    /**
     * #24
     * require 3.2
     * @param userId    this user will become the founder of the store
     * @param storeName doesn't have to be unique
     * @param category  the store category, currently a free text
     * */
    void addStore(int userId, String storeName, String category);


    /**
     * #19
     * require 2.3
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     */
    void addProductToCart(int userId, int productId, int storeId);

    void getUserPurchaseHistory(int userId);

    /**
     * [#28]
     * @param userId   the user id
     * @param header  the header of the message
     * @param complaint the complaint
     */
    public void openComplaint(int userId,String header,String complaint);

    /**
     * [#47]
     * @param userId  the user id
     * @return the complaint message
     */
    public Message getComplaint(int userId);

    /**
     * [#47]
     * @param userId the user id
     * @param receiverId the receiver id
     * @param senderId the sender id
     * @param messageId the message id
     */
    public void markMessageAsRead(int userId,String receiverId,String senderId,int messageId);

    /**
     * [#47]
     * @param userId the user id
     * @param receiverId the receiver id
     * @param header the header of the message
     * @param massage the massage
     */
    public void sendMassageAdmin(int userId,String receiverId,String header,String massage);

    /**
     * [#47]
     * @param userId the user id
     * @param answer the answer
     */
    public void answerComplaint(int userId,String answer);


    /**
     *
     * @param userId the user id
     * @param receiverId the receiver id
     * @return the message
     */
    public Message readMassage(int userId,String receiverId);


    /**
     * [#27]
     * @param userId the user id
     * @param header  the header of the message
     * @param massage the massage
     * @param storeId the store id
     */
    public void sendMassageStore(int userId,String header,String massage,int storeId);

    /**
     * [#42]
     * @param userId the user id
     * @param storeId the store id
     * @return the message
     */

    public Message readUnreadMassageStore(int userId,int storeId);

    /**
     * [#42]
     * @param userId the user id
     * @param storeId the store id
     * @return the message
     */

    public Message readReadMassageStore(int userId,int storeId);

    /**
     * [#42]
     * @param userId the user id
     * @param answer the answer
     */

    public void answerQuestionStore(int userId,String answer);

    /**
     * [#42]
     * @param userId the user id
     * @param storeId the store id
     */

    public void refreshOldMessageStore(int userId,int storeId);


    /**
     * [#25]
     * @param userId the user id
     * @param review the review
     * @param storeId the store id
     * @param productId the product id
     */

    void addReview(int userId,String review, int storeId, int productId);

    /**
     * [#25]
     * @param userId the user id
     * @param storeId the store id
     * @param productId the product id
     */

    public void removeReview( int userId,int storeId, int productId);

    /**
     * [#25]
     * @param userId the user id
     * @param storeId the store id
     * @param productId the product id
     * @return the review
     */

    public Review getReview( int userId,int storeId, int productId);

    /**
     * [#26]
     * @param userId the user id
     * @param storeId the store id
     * @param productId the product id
     * @return the score
     */

    public float getProductScore( int userId,int storeId,int productId);

    /**
     * [#26]
     * @param userId the user id
     * @param storeId the store id
     * @param productId the product id
     * @param score the score
     */
    public void addAndSetProductScore( int userId,int storeId, int productId, int score);

    /**
     * [#26]
     * @param userId the user id
     * @param storeId the store id
     * @param productId the product id
     */
    public void removeProductScore( int userId,int storeId, int productId);

    /**
     * [#26]
     * @param userId the user id
     * @param storeId the store id
     * @param score the score
     */
    public void addStoreScore(int userId,int storeId ,int score) ;

    /**
     * [#26]
     * @param userId the user id
     * @param storeId the store id
     */
    public void removeStoreScore(int userId,int storeId);

    /**
     * [#26]
     * @param userId the user id
     * @param storeId the store id
     * @param score the score
     */
    public void modifyStoreScore(int userId,int storeId, int score);

    /**
     * @param userId the user id
     * @param storeId the store id
     * @return the score
     */
    public float getStoreScore( int userId,int storeId);

    /**
     * #32
     * require 4.1
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param name      the name of the product
     */
    void setProductName(int userId, int storeId, int productId, String name);

    /**
     * #32
     * require 4.1
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param category  the category of the product
     */
    void setProductCategory(int userId, int storeId, int productId, String category);

    /**
     * #32
     * require 4.1
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id
     * @param price     the price of the product
     */
    void setProductPrice(int userId, int storeId, int productId, double price);

    /**
     * #32
     * require 4.1
     * @param userId        the user id
     * @param storeId       the store id
     * @param productId     the product id
     * @param stockQuantity the stock quantity of the product
     */
    void setProductStockQuantity(int userId, int storeId, int productId, int stockQuantity);

    /**
     * #32
     * require 4.1
     * @param userId    the user id
     * @param storeId   the store id
     * @param productId the product id to remove
     */
    void removeProduct(int userId, int storeId, int productId);
}
