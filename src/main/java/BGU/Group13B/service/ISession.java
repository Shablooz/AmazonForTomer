package BGU.Group13B.service;

import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;

import java.time.LocalDateTime;

import java.util.List;

public interface ISession {
    void addProduct(int userId, String productName, int quantity, double price, int storeId) throws NoPermissionException;
    //generate param documentation

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
     * @param answer1 - optional for security question number 1 can put empty String
     * @param answer2 - optional for security question number 2 can put empty String
     * @param answer1 - optional for security question number 3 can put empty String
     * */
    void register(int userId,String username,String password,String email,String answer1,String answer2,String answer3);

    /**
     * #18
     * require 2.2
     * @param productName
     */
    void searchProductByName(String productName);

    /**
     * #18
     * require 2.2
     * @param category
     */
    void searchProductByCategory(String category);

    /**
     * #18
     * require 2.2
     * @param keywords
     */
    void searchProductByKeywords(List<String> keywords);

    /**
     * #18
     * require 2.2
     * @param minPrice
     * @param maxPrice
     */
    void filterByPriceRange(int minPrice, int maxPrice);

    /**
     * #18
     * require 2.2
     * @param minRating
     * @param maxRating
     */
    void filterByProductRank(int minRating, int maxRating);

    /**
     * #18
     * require 2.2
     * @param category
     */
    void filterByCategory(String category);

    /**
     * #18
     * require 2.2
     * @param minRating
     * @param maxRating
     */
    void filterByStoreRank(int minRating, int maxRating);

    /**
     * #16
     * require 1.4
     * @param userID - current userID - of the visitor
     * @param username
     * @param password
     * @param answer1 - optional for security question number 1 can put empty String
     * @param answer2 - optional for security question number 2 can put empty String
     * @param answer3 - optional for security question number 3 can put empty String
     * returns the new user id - of the already existing user - need to make a uniq generator if failed returns 0
     * */
    int login(int userID,String username,String password,String answer1,String answer2,String answer3);


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
     * #31
     * require 3.9
     * @param userId
     * */
    boolean SecurityAnswer1Exists(int userId);

    /**
     * #31
     * require 3.9
     * @param userId
     * */
    boolean SecurityAnswer2Exists(int userId);


    /**
     * #31
     * require 3.9
     * @param userId
     * */
    boolean SecurityAnswer3Exists(int userId);

}
