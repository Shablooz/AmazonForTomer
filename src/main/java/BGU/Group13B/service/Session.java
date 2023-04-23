package BGU.Group13B.service;

import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;
import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.User.*;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;
import org.springframework.data.util.Pair;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * IMPORTANT need to initialize the session AFTER loading first user (id = 1) from database
 */

//made it public for testing purposes - should be private
public class Session implements ISession {
    private final Market market;
    private final IUserRepository userRepository = SingletonCollection.getUserRepository();
    private static final Logger LOGGER = Logger.getLogger(Session.class.getName());

    static {
        SingletonCollection.setFileHandler(LOGGER);
    }

    IUserRepository userRepositoryAsHashmap;


    //IMPORTANT need to initialize the session AFTER loading first user (id = 1) from database
    public Session(Market market) {
        this.market = market;
        //callbacks initialization
        SingletonCollection.setAddToUserCart(this::addToCart);
        this.userRepositoryAsHashmap = SingletonCollection.getUserRepository();

        //IMPORTANT need to initialize the session AFTER loading first user (id = 1) from database
        //id should 1
        //This should do nothing if the system was initialized in the past - making first admin
        int id = 1;
        userRepositoryAsHashmap.addUser(id, new User(id));
        register(id, "kingOfTheSheep", "SheePLover420",
                "mrsheep@gmail.com", "054-1234567", "1234", "answer3");
    }

    @Override
    public int addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity, String description) {
        try {
            return market.addProduct(userId, storeId, productName, category, price, stockQuantity, description);
        } catch (Exception e) {
            //TODO: handle exception
            return -1;
        }
        return -1;

    }

    @Override
    public void addToCart(int userId, int storeId, int productId) {
        userRepository.getUser(userId).addToCart(storeId, productId);
    }

    @Override
    public double purchaseProductCart(int userId, String address, String creditCardNumber,
                                    String creditCardMonth, String creditCardYear,
                                    String creditCardHolderFirstName, String creditCardHolderLastName,
                                    String creditCardCcv, String id, String creditCardType,
                                    HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsCoupons,
                                    String/*store coupons*/ storeCoupon) {
        try {
            return userRepository.getUser(userId).
                    purchaseCart(address, creditCardNumber, creditCardMonth,
                            creditCardYear, creditCardHolderFirstName, creditCardHolderLastName,
                            creditCardCcv, id, creditCardType, productsCoupons, storeCoupon);
        } catch (PurchaseFailedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice, int amount) {
        try {
            market.purchaseProposalSubmit(userId, storeId, productId, proposedPrice, amount);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
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
    public void createAuctionForProduct(int storeManagerId, int storeId, int productId,
                                        double minPrice, LocalDateTime lastDate) {

    }

    @Override
    public void auctionPurchase(int userId, int storeId, int productId, double newPrice) {

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
    public synchronized void register(int userId, String username, String password,
                                      String email, String answer1, String answer2, String answer3) {
        User user = userRepositoryAsHashmap.getUser(userId);
        try {
            //the first "if" might not be necessary when we will connect to web
            if (this.userRepositoryAsHashmap.checkIfUserWithEmailExists(email)) {
                System.out.println("user with this email already exists!");
                return;//temporary
            }
            if (!user.isRegistered()) {
                if (userRepositoryAsHashmap.checkIfUserExists(username) == null) {
                    user.register(username, password, email, answer1, answer2, answer3);
                } else {
                    System.out.println("user with this username already exists!");
                }
            } else {
                System.out.println("already registered!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void searchProductByName(String productName) {
        market.searchProductByName(productName);
    }

    @Override
    public void searchProductByCategory(String category) {
        market.searchProductByCategory(category);
    }

    @Override
    public void searchProductByKeywords(List<String> keywords) {
        market.searchProductByKeywords(keywords);
    }

    @Override
    public void filterByPriceRange(int minPrice, int maxPrice) {
        market.filterByPriceRange(minPrice, maxPrice);
    }

    @Override
    public void filterByProductRank(int minRating, int maxRating) {
        market.filterByProductRank(minRating, maxRating);
    }

    @Override
    public void filterByCategory(String category) {
        market.filterByCategory(category);
    }

    @Override
    public void filterByStoreRank(int minRating, int maxRating) {
        market.filterByStoreRank(minRating, maxRating);
    }


    @Override
    public int login(int userID, String username, String password, String answer1, String answer2, String answer3) {
        try {
            //gets the user that we want to log into
            User user = userRepositoryAsHashmap.checkIfUserExists(username);
            synchronized (user) {
                user.login(username, password, answer1, answer2, answer3);
                /*example of use*/
                LOGGER.info("user " + username + " logged in");
                //removes the current guest profile to swap to the existing member one
                userRepositoryAsHashmap.removeUser(userID);
                //gets the new id - of the user we're logging into
                return userRepositoryAsHashmap.getUserId(user);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }

    }

    @Override
    public void logout(int userID) {
        synchronized (userRepositoryAsHashmap.getUser(userID)) {
            userRepositoryAsHashmap.getUser(userID).logout();
        }
    }

    @Override
    public int addStore(int userId, String storeName, String category) {
        User user = userRepositoryAsHashmap.getUser(userId);
        synchronized (user) {
            if (user.isRegistered()) {
                try {
                    int storeId = market.addStore(userId, storeName, category);
                    user.addPermission(storeId, UserPermissions.StoreRole.FOUNDER);
                    return storeId;
                } catch (Exception e) {
                    //TODO: handle exception
                    return -1;
                }
            }
            //TODO: handle exception
            return -1;
        }
    }

    @Override
    public void addProductToCart(int userId, int productId, int storeId) {
        try {
            userRepositoryAsHashmap.getUser(userId).addProductToCart(productId, storeId);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }


    @Override
    public void getUserPurchaseHistory(int userId) {
        //TODO: implement
    }

    public void openComplaint(int userId, String header, String complaint) {
        try {
            userRepositoryAsHashmap.getUser(userId).openComplaint(header, complaint);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }


    public Message getComplaint(int userId) {
        try {
            return userRepositoryAsHashmap.getUser(userId).getComplaint();
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void markMessageAsReadAdmin(int userId, String receiverId, String senderId, int messageId) {
        try {
            userRepositoryAsHashmap.getUser(userId).markMessageAsReadAdmin(receiverId, senderId, messageId);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMassageAdmin(int userId, String receiverId, String header, String massage) {
        try {
            if(userRepository.checkIfUserExists(receiverId)==null)
                throw new RuntimeException("receiver Id not found");
            userRepositoryAsHashmap.getUser(userId).sendMassageAdmin(receiverId, header, massage);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void answerComplaint(int userId, String answer) {
        try {
            userRepositoryAsHashmap.getUser(userId).answerComplaint(answer);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message readMessage(int userId) {
        try {
            return userRepositoryAsHashmap.getUser(userId).readMassage();
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMassageStore(int userId, String header, String massage, int storeId) {
        try {
            userRepositoryAsHashmap.getUser(userId).sendMassageStore(header, massage, storeId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message readUnreadMassageStore(int userId, int storeId) {
        try {
            return userRepositoryAsHashmap.getUser(userId).readUnreadMassageStore(storeId);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message readReadMassageStore(int userId, int storeId) {
        try {
            return userRepositoryAsHashmap.getUser(userId).readReadMassageStore(storeId);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void answerQuestionStore(int userId, String answer) {
        try {
            userRepositoryAsHashmap.getUser(userId).answerQuestionStore(answer);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void refreshOldMessageStore(int userId, int storeId) {
        try {
            userRepositoryAsHashmap.getUser(userId).refreshOldMessageStore(storeId);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addReview(int userId, String review, int storeId, int productId) {
        try {
            userRepositoryAsHashmap.getUser(userId).addReview(review, storeId, productId);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeReview(int userId, int storeId, int productId) {
        try {
            userRepositoryAsHashmap.getUser(userId).removeReview(storeId, productId);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Review getReview(int userId, int storeId, int productId) {
        try {
            return userRepositoryAsHashmap.getUser(userId).getReview(storeId, productId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public float getProductScore(int userId, int storeId, int productId) {
        try {
            return userRepositoryAsHashmap.getUser(userId).getProductScore(storeId, productId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addAndSetProductScore(int userId, int storeId, int productId, int score) {
        try {
            userRepositoryAsHashmap.getUser(userId).addAndSetProductScore(storeId, productId, score);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeProductScore(int userId, int storeId, int productId) {
        try {
            userRepositoryAsHashmap.getUser(userId).removeProductScore(storeId, productId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addStoreScore(int userId, int storeId, int score) {
        try {
            userRepositoryAsHashmap.getUser(userId).addStoreScore(storeId, score);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeStoreScore(int userId, int storeId) {
        try {
            userRepositoryAsHashmap.getUser(userId).removeStoreScore(storeId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void modifyStoreScore(int userId, int storeId, int score) {
        try {
            userRepositoryAsHashmap.getUser(userId).modifyStoreScore(storeId, score);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public float getStoreScore(int userId, int storeId) {
        try {
            return userRepositoryAsHashmap.getUser(userId).getStoreScore(storeId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getCartDescription(int userId) {
        userRepositoryAsHashmap.getUser(userId).getCartDescription();
    }

    @Override
    public void removeProductFromCart(int userId, int storeId, int productId) {
        try {
            userRepositoryAsHashmap.getUser(userId).removeProductFromCart(storeId, productId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setProductName(int userId, int storeId, int productId, String name) {
        try {
            market.setProductName(userId, storeId, productId, name);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setProductCategory(int userId, int storeId, int productId, String category) {
        try {
            market.setProductCategory(userId, storeId, productId, category);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setProductPrice(int userId, int storeId, int productId, double price) {
        try {
            market.setProductPrice(userId, storeId, productId, price);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setProductStockQuantity(int userId, int storeId, int productId, int stockQuantity) {
        try {
            market.setProductStockQuantity(userId, storeId, productId, stockQuantity);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setProductDescription(int userId, int storeId, int productId, String description) {
        try {
            market.setProductDescription(userId, storeId, productId, description);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public int enterAsGuest() {
        int id = userRepositoryAsHashmap.getNewUserId();
        userRepositoryAsHashmap.addUser(id, new User(id));
        return id;
    }


    @Override
    public void removeProduct(int userId, int storeId, int productId) {
        try {
            market.removeProduct(userId, storeId, productId);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override

    public String getUserName(int userId) {
        return userRepositoryAsHashmap.getUser(userId).getUserName();
    }

    @Override
    public void setUsername(int userId, String newUsername) {
        userRepositoryAsHashmap.getUser(userId).setUserName(newUsername);
    }

    @Override
    public void setUserStatus(int admin_id, int userId, int newStatus) {
        if (!getUserStatus(admin_id).equals("Admin")) {
            //should throw an exception
            System.out.println("isnt an admin");
            return;
        }
        if (newStatus == 1 && userRepositoryAsHashmap.getUser(userId).getStatus() == UserPermissions.UserPermissionStatus.MEMBER)
            userRepositoryAsHashmap.getUser(userId).setPermissions(UserPermissions.UserPermissionStatus.ADMIN);

        if (newStatus == 2 && userRepositoryAsHashmap.getUser(userId).getStatus() == UserPermissions.UserPermissionStatus.ADMIN)
            userRepositoryAsHashmap.getUser(userId).setPermissions(UserPermissions.UserPermissionStatus.MEMBER);
    }

    @Override
    public String getUserStatus(int userId) {
        if (userRepositoryAsHashmap.getUser(userId).getStatus() == UserPermissions.UserPermissionStatus.MEMBER)
            return "Member";
        else if (userRepositoryAsHashmap.getUser(userId).getStatus() == UserPermissions.UserPermissionStatus.ADMIN)
            return "Admin";
        else
            return "Guest";
    }

    @Override
    public String getUserEmail(int userId) {
        return userRepositoryAsHashmap.getUser(userId).getEmail();
    }

    @Override
    public List<Pair<Integer, String>> getStoresOfUser(int userId) {
        return userRepositoryAsHashmap.getUser(userId).getStoresAndRoles();
    }

    public StoreInfo getStoreInfo(int storeId) {
        try {
            return market.getStoreInfo(storeId);
        } catch (Exception e) {
            //TODO: handle exception
            return null;
        }
    }

    @Override
    public String getStoreName(int storeId) {
        try {
            return market.getStoreName(storeId);
        } catch (Exception e) {
            //TODO: handle exception
            return null;
        }
    }

    @Override
    public String getStoreCategory(int storeId) {
        try {
            return market.getStoreCategory(storeId);
        } catch (Exception e) {
            //TODO: handle exception
            return null;
        }
    }

    @Override
    public ProductInfo getStoreProductInfo(int storeId, int productId) {
        try {
            return market.getStoreProductInfo(storeId, productId);
        } catch (Exception e) {
            //TODO: handle exception
            return null;
        }
    }

    @Override
    public ProductInfo getProductInfo(int productId) {
        try {
            return market.getProductInfo(productId);
        } catch (Exception e) {
            //TODO: handle exception
            return null;
        }
    }

    @Override
    public String getProductName(int productId) {
        try {
            return market.getProductName(productId);
        } catch (Exception e) {
            //TODO: handle exception
            return null;
        }
    }

    @Override
    public String getProductCategory(int productId) {
        try {
            return market.getProductCategory(productId);
        } catch (Exception e) {
            //TODO: handle exception
            return null;
        }
    }

    @Override
    public void changeProductQuantityInCart(int userId, int storeId, int productId, int quantity) {
        try {
            userRepositoryAsHashmap.getUser(userId).changeProductQuantityInCart(storeId, productId, quantity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double getProductPrice(int productId) {
        try {
            return market.getProductPrice(productId);
        } catch (Exception e) {
            //TODO: handle exception
            return -1;
        }
    }

    @Override
    public int getProductStockQuantity(int productId) {
        try {
            return market.getProductStockQuantity(productId);
        } catch (Exception e) {
            //TODO: handle exception
            return -1;
        }
    }

    @Override
    public float getProductScore(int productId) {
        try {
            return market.getProductScore(productId);
        } catch (Exception e) {
            //TODO: handle exception
            return -1;
        }
    }

    @Override
    public Set<ProductInfo> getAllStoreProductsInfo(int storeId) {
        try {
            return market.getAllStoreProductsInfo(storeId);
        } catch (Exception e) {
            //TODO: handle exception
            return null;
        }
    }

    public boolean SecurityAnswer1Exists(int userId) {
        return userRepositoryAsHashmap.getUser(userId).SecurityAnswer1Exists();
    }

    @Override
    public boolean SecurityAnswer2Exists(int userId) {
        return userRepositoryAsHashmap.getUser(userId).SecurityAnswer2Exists();
    }

    @Override
    public boolean SecurityAnswer3Exists(int userId) {
        return userRepositoryAsHashmap.getUser(userId).SecurityAnswer3Exists();
    }

    @Override
    public boolean checkIfQuestionsExist(int userId) {
        return SecurityAnswer1Exists(userId) || SecurityAnswer2Exists(userId) || SecurityAnswer3Exists(userId);
    }

    @Override
    public void exitSystemAsGuest(int userId) {
        userRepositoryAsHashmap.removeUser(userId);
    }

    @Override
    public void allowPurchasePolicyConflicts(int userId, int storeId) {
        try {
            market.allowPurchasePolicyConflicts(userId, storeId);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void disallowPurchasePolicyConflicts(int userId, int storeId) {
        try {
            market.disallowPurchasePolicyConflicts(userId, storeId);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setStorePurchaseQuantityUpperBound(int userId, int storeId, int upperBound) {
        try {
            market.setStorePurchaseQuantityUpperBound(userId, storeId, upperBound);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setStorePurchaseQuantityLowerBound(int userId, int storeId, int lowerBound) {
        try {
            market.setStorePurchaseQuantityLowerBound(userId, storeId, lowerBound);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setStorePurchaseQuantityBounds(int userId, int storeId, int lowerBound, int upperBound) {
        try {
            market.setStorePurchaseQuantityBounds(userId, storeId, lowerBound, upperBound);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setStorePurchasePriceUpperBound(int userId, int storeId, int upperBound) {
        try {
            market.setStorePurchasePriceUpperBound(userId, storeId, upperBound);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setStorePurchasePriceLowerBound(int userId, int storeId, int lowerBound) {
        try {
            market.setStorePurchasePriceLowerBound(userId, storeId, lowerBound);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setStorePurchasePriceBounds(int userId, int storeId, int lowerBound, int upperBound) {
        try {
            market.setStorePurchasePriceBounds(userId, storeId, lowerBound, upperBound);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setProductPurchaseQuantityUpperBound(int userId, int storeId, int productId, int upperBound) {
        try {
            market.setProductPurchaseQuantityUpperBound(userId, storeId, productId, upperBound);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setProductPurchaseQuantityLowerBound(int userId, int storeId, int productId, int lowerBound) {
        try {
            market.setProductPurchaseQuantityLowerBound(userId, storeId, productId, lowerBound);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setProductPurchaseQuantityBounds(int userId, int storeId, int productId, int lowerBound, int upperBound) {
        try {
            market.setProductPurchaseQuantityBounds(userId, storeId, productId, lowerBound, upperBound);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setProductPurchasePriceUpperBound(int userId, int storeId, int productId, int upperBound) {
        try {
            market.setProductPurchasePriceUpperBound(userId, storeId, productId, upperBound);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setProductPurchasePriceLowerBound(int userId, int storeId, int productId, int lowerBound) {
        try {
            market.setProductPurchasePriceLowerBound(userId, storeId, productId, lowerBound);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void setProductPurchasePriceBounds(int userId, int storeId, int productId, int lowerBound, int upperBound) {
        try {
            market.setProductPurchasePriceBounds(userId, storeId, productId, lowerBound, upperBound);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public int addStoreVisibleDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate) {
        try {
            return market.addStoreVisibleDiscount(userId, storeId, discountPercentage, discountLastDate);
        } catch (Exception e) {
            //TODO: handle exception
            return -1;
        }
    }

    @Override
    public int addStoreConditionalDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount) {
        try {
            return market.addStoreConditionalDiscount(userId, storeId, discountPercentage, discountLastDate, minPriceForDiscount, quantityForDiscount);
        } catch (Exception e) {
            //TODO: handle exception
            return -1;
        }
    }

    @Override
    public int addStoreHiddenDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate, String code) {
        try {
            return market.addStoreHiddenDiscount(userId, storeId, discountPercentage, discountLastDate, code);
        } catch (Exception e) {
            //TODO: handle exception
            return -1;
        }
    }

    @Override
    public void removeStoreDiscount(int userId, int storeId, int discountId) {
        try {
            market.removeStoreDiscount(userId, storeId, discountId);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public int addProductVisibleDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate) {
        try {
            return market.addProductVisibleDiscount(userId, storeId, productId, discountPercentage, discountLastDate);
        } catch (Exception e) {
            //TODO: handle exception
            return -1;
        }
    }

    @Override
    public int addProductConditionalDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount) {
        try {
            return market.addProductConditionalDiscount(userId, storeId, productId, discountPercentage, discountLastDate, minPriceForDiscount, quantityForDiscount);
        } catch (Exception e) {
            //TODO: handle exception
            return -1;
        }
    }

    @Override
    public int addProductHiddenDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate, String code) {
        try {
            return market.addProductHiddenDiscount(userId, storeId, productId, discountPercentage, discountLastDate, code);
        } catch (Exception e) {
            //TODO: handle exception
            return -1;
        }
    }

    @Override
    public void removeProductDiscount(int userId, int storeId, int productId, int discountId) {
        try {
            market.removeProductDiscount(userId, storeId, productId, discountId);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public int getBasketProductQuantity(int userId, int storeId, int productId) throws Exception {
        return userRepository.getUser(userId).getBasketProductQuantity(storeId, productId);
    }
}
