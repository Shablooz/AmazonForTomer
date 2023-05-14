package BGU.Group13B.service;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;
import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.User.*;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;
import BGU.Group13B.service.entity.ReviewService;
import BGU.Group13B.service.entity.ServiceBasketProduct;
import BGU.Group13B.service.entity.ServiceProduct;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * IMPORTANT need to initialize the session AFTER loading first user (id = 1) from database
 */

//made it public for testing purposes - should be private
@Service
public class Session implements ISession {
    private Market market;
    private final IUserRepository userRepository = SingletonCollection.getUserRepository();
    private static final Logger LOGGER_INFO = Logger.getLogger(Session.class.getName());
    private static final Logger LOGGER_ERROR = Logger.getLogger(Session.class.getName());

    static {
        SingletonCollection.setFileHandler(LOGGER_INFO, true);
        SingletonCollection.setFileHandler(LOGGER_ERROR, false);

    }

    IUserRepository userRepositoryAsHashmap;


    //IMPORTANT need to initialize the session AFTER loading first user (id = 1) from database

    public Session() {
        this(new Market());
    }

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
                "mrsheep@gmail.com", "11", "11", "11");

    }

    @Override
    public Response<Integer> addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity, String description) {
        try {
            return Response.success(market.addProduct(userId, storeId, productName, category, price, stockQuantity, description));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    /*good for development no need check if the item exists*/
    @Override
    public void addToCart(int userId, int storeId, int productId) {
        userRepository.getUser(userId).addToCart(storeId, productId);
    }

    @Override
    public double purchaseProductCart(int userId, String creditCardNumber,
                                      String creditCardMonth, String creditCardYear,
                                      String creditCardHolderFirstName,
                                      String creditCardCcv, String id,
                                      HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsCoupons,
                                      String/*store coupons*/ storeCoupon){
            try {
                return userRepository.getUser(userId).
                        purchaseCart(creditCardNumber, creditCardMonth,
                                creditCardYear, creditCardHolderFirstName,
                                creditCardCcv, id, productsCoupons, storeCoupon);
            }catch (PurchaseFailedException | NoPermissionException e) {
                throw new RuntimeException(e);
            }
    }

    @Override
    public Response<VoidResponse> purchaseProductCart(int userId, String creditCardNumber,
                                                      String creditCardMonth, String creditCardYear,
                                                      String creditCardHolderFirstName,
                                                      String creditCardCVV, String id,
                                                      String address, String city, String country,
                                                      String zip) {
        try {
            userRepository.getUser(userId).
                    purchaseCart(
                            creditCardNumber, creditCardMonth,
                            creditCardYear, creditCardHolderFirstName,
                            creditCardCVV, id,
                            address, city, country, zip);
            return Response.success();
        } catch (PurchaseFailedException | NoPermissionException e) {
            return Response.exception(e);
        }
    }

    @Override
    public Pair<Double, List<ServiceBasketProduct>> startPurchaseBasketTransaction(int userId, HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsCoupons,
                                                                                   String/*store coupons*/ storeCoupon) {
        try {
            var priceSuccessfulItems =  userRepository.getUser(userId).startPurchaseBasketTransaction(productsCoupons, storeCoupon);
            return new Pair<>(priceSuccessfulItems.getFirst(),
                    priceSuccessfulItems.getSecond().stream().map(ServiceBasketProduct::new).collect(Collectors.toList()));
        } catch (PurchaseFailedException | NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response<List<PurchaseHistory>> getStorePurchaseHistory(int userId, int storeId) {
        try {
            return Response.success(market.getStorePurchaseHistory(userId, storeId));
        } catch (NoPermissionException e) {
            return Response.exception(e);
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

        //the first "if" might not be necessary when we will connect to web
        if (this.userRepositoryAsHashmap.checkIfUserWithEmailExists(email)) {
            throw new IllegalArgumentException("user with this email already exists!");//temporary
        }
        if (!user.isRegistered()) {
            if (userRepositoryAsHashmap.checkIfUserExists(username) == null) {
                user.register(username, password, email, answer1, answer2, answer3);
            } else {
                throw new IllegalArgumentException("user with this username already exists!");
            }
        } else {
            throw new IllegalArgumentException("already registered!");
        }
    }

    @Override
    public Response<List<ProductInfo>> search(String searchWords) {
        try {
            List<ProductInfo> products = new LinkedList<>();
            products.addAll(market.searchProductByKeywords(searchWords));
            products.addAll(market.searchProductByCategory(searchWords));
            products.addAll(market.searchProductByName(searchWords));
            return Response.success(products);
        }
        catch (Exception e){
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<ProductInfo>> filterByPriceRange(int minPrice, int maxPrice) {
        try {
            return Response.success(market.filterByPriceRange(minPrice, maxPrice));
        }catch (Exception e){
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<ProductInfo>> filterByProductRank(int minRating, int maxRating) {
        try {
            return Response.success(market.filterByProductRank(minRating, maxRating));
        }catch (Exception e){
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<ProductInfo>> filterByCategory(String category) {
        try {
            return Response.success(market.filterByCategory(category));
        }catch (Exception e){
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<ProductInfo>> filterByStoreRank(int minRating, int maxRating) {
        try {
            return Response.success(market.filterByStoreRank(minRating, maxRating));
        }catch (Exception e){
            return Response.exception(e);
        }
    }

    @Override
    public int login(int userID, String username, String password, String answer1, String answer2, String answer3) {
        try {
            //gets the user that we want to log into
            User user = userRepositoryAsHashmap.checkIfUserExists(username);
            synchronized (user) {
                user.login(username, password, answer1, answer2, answer3);
                /*example of use*/
                LOGGER_INFO.info("user " + username + " logged in");
                //gets the new id - of the user we're logging into
                return userRepositoryAsHashmap.getUserId(user);
            }
        } catch (Exception e) {
            //line below temporary
            throw new IllegalArgumentException(e.getMessage());

        }

    }

    @Override
    public void logout(int userID) {
        synchronized (userRepositoryAsHashmap.getUser(userID)) {
            userRepositoryAsHashmap.getUser(userID).logout();
        }
    }

    @Override
    public Response<Integer> addStore(int userId, String storeName, String category) {
        User user = userRepositoryAsHashmap.getUser(userId);
        synchronized (user) {
            if (user.isRegistered()) {
                try {
                    int storeId = market.addStore(userId, storeName, category);
                    user.addPermission(storeId, UserPermissions.StoreRole.FOUNDER);
                    return Response.success(storeId);
                } catch (Exception e) {
                    return Response.exception(e);
                }
            }
            return Response.failure("user is not registered");
        }
    }

    @Override
    public Response<VoidResponse> addProductToCart(int userId, int productId, int storeId) {
        try {
            userRepositoryAsHashmap.getUser(userId).addProductToCart(productId, storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> clearMessageToReply(int userId) {
        try {
            userRepositoryAsHashmap.getUser(userId).clearMessageToReply();
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    public Response<VoidResponse> openComplaint(int userId, String header, String complaint) {
        try {
            userRepositoryAsHashmap.getUser(userId).openComplaint(header, complaint);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }


    public Response<Message> getComplaint(int userId) {
        try {
            return Response.success(userRepositoryAsHashmap.getUser(userId).getComplaint());
        }catch (Exception e){
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> markMessageAsReadAdmin(int userId, String receiverId, String senderId, int messageId) {
        try {
            userRepositoryAsHashmap.getUser(userId).markMessageAsReadAdmin(receiverId, senderId, messageId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> sendMassageAdmin(int userId, String receiverId, String header, String massage) {
        try {
            if (userRepository.checkIfUserExists(receiverId) == null)
                throw new RuntimeException("receiver Id not found");
            userRepositoryAsHashmap.getUser(userId).sendMassageAdmin(receiverId, header, massage);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> answerComplaint(int userId, String answer) {
        try {
            userRepositoryAsHashmap.getUser(userId).answerComplaint(answer);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Message> readMessage(int userId) {
        try {
            return Response.success(userRepositoryAsHashmap.getUser(userId).readMassage());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> replayMessage(int userId, String message) {
        try {
            userRepositoryAsHashmap.getUser(userId).replayMessage(message);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Message> readOldMessage(int userId) {
        try {
            return Response.success(userRepositoryAsHashmap.getUser(userId).readOldMessage());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> refreshOldMessages(int userId) {
        try {
            userRepositoryAsHashmap.getUser(userId).refreshOldMessage();
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> sendMassageStore(int userId, String header, String massage, int storeId) {
        try {
            userRepositoryAsHashmap.getUser(userId).sendMassageStore(header, massage, storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Message> readUnreadMassageStore(int userId, int storeId) {
        try {
            return Response.success(userRepositoryAsHashmap.getUser(userId).readUnreadMassageStore(storeId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Message> readReadMassageStore(int userId, int storeId) {
        try {
            return Response.success( userRepositoryAsHashmap.getUser(userId).readReadMassageStore(storeId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> answerQuestionStore(int userId, String answer) {
        try {
            userRepositoryAsHashmap.getUser(userId).answerQuestionStore(answer);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> refreshOldMessageStore(int userId, int storeId) {
        try {
            userRepositoryAsHashmap.getUser(userId).refreshOldMessageStore(storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addReview(int userId, String review, int storeId, int productId) {
        try {
            userRepositoryAsHashmap.getUser(userId).addReview(review, storeId, productId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeReview(int userId, int storeId, int productId) {
        try {
            userRepositoryAsHashmap.getUser(userId).removeReview(storeId, productId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Review> getReview(int userId, int storeId, int productId) {
        try {
            return Response.success( userRepositoryAsHashmap.getUser(userId).getReview(storeId, productId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }
    @Override
    public Response<List<ReviewService>> getAllReviews(int userId, int storeId, int productId) {
        try {
            List<Review> reviews=userRepositoryAsHashmap.getUser(userId).getAllReviews(storeId, productId,userId);
            List<ReviewService> reviewServices=new ArrayList<>();
            int i=0;
            for (Review review:reviews) {
                reviewServices.add(new ReviewService(userRepositoryAsHashmap.getUser(userId).getUserName(),review.getReview()));
            }
            return Response.success( reviewServices);
        } catch (Exception e) {
            return Response.exception(e);
        }
    }
    @Override
    public Response<Float> getProductScore(int userId, int storeId, int productId) {
        try {
            return Response.success(userRepositoryAsHashmap.getUser(userId).getProductScore(storeId, productId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addAndSetProductScore(int userId, int storeId, int productId, int score) {
        try {
            userRepositoryAsHashmap.getUser(userId).addAndSetProductScore(storeId, productId, score);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeProductScore(int userId, int storeId, int productId) {
        try {
            userRepositoryAsHashmap.getUser(userId).removeProductScore(storeId, productId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addStoreScore(int userId, int storeId, int score) {
        try {
            userRepositoryAsHashmap.getUser(userId).addStoreScore(storeId, score);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeStoreScore(int userId, int storeId) {
        try {
            userRepositoryAsHashmap.getUser(userId).removeStoreScore(storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> modifyStoreScore(int userId, int storeId, int score) {
        try {
            userRepositoryAsHashmap.getUser(userId).modifyStoreScore(storeId, score);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Float> getStoreScore(int userId, int storeId) {
        try {
            return Response.success( userRepositoryAsHashmap.getUser(userId).getStoreScore(storeId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<String> getCartDescription(int userId) {
        try {
            return Response.success( userRepositoryAsHashmap.getUser(userId).getCartDescription());
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response<List<ServiceBasketProduct>> getCartContent(int userId) {
        List<BasketProduct> cartContent;
        try {
            cartContent = userRepositoryAsHashmap.getUser(userId).getCartBasketProducts();
        } catch (Exception e) {
            return Response.exception(e);
        }
        return Response.success(cartContent.stream().map(ServiceBasketProduct::new).collect(Collectors.toList()));
    }

    @Override
    public void removeProductFromCart(int userId, int storeId, int productId) {
        try {
            userRepositoryAsHashmap.getUser(userId).removeProductFromCart(storeId, productId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Response<VoidResponse> setProductName(int userId, int storeId, int productId, String name) {
        try {
            market.setProductName(userId, storeId, productId, name);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setProductCategory(int userId, int storeId, int productId, String category) {
        try {
            market.setProductCategory(userId, storeId, productId, category);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setProductPrice(int userId, int storeId, int productId, double price) {
        try {
            market.setProductPrice(userId, storeId, productId, price);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setProductStockQuantity(int userId, int storeId, int productId, int stockQuantity) {
        try {
            market.setProductStockQuantity(userId, storeId, productId, stockQuantity);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setProductDescription(int userId, int storeId, int productId, String description) {
        try {
            market.setProductDescription(userId, storeId, productId, description);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public int enterAsGuest() {
        int id = userRepositoryAsHashmap.getNewUserId();
        userRepositoryAsHashmap.addUser(id, new User(id));
        return id;
    }


    @Override
    public Response<VoidResponse> removeProduct(int userId, int storeId, int productId) {
        try {
            market.removeProduct(userId, storeId, productId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
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
            throw new IllegalArgumentException("isnt an admin");
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

    public Response<StoreInfo> getStoreInfo(int userId, int storeId) {
        try {
            return Response.success(market.getStoreInfo(userId, storeId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<ProductInfo> getStoreProductInfo(int userId, int storeId, int productId) {
        try {
            return Response.success(market.getStoreProductInfo(userId, storeId, productId));
        } catch (Exception e) {
            return Response.exception(e);
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

    @Override
    public Response<Set<ProductInfo>> getAllStoreProductsInfo(int userId, int storeId) {
        try {
            return Response.success(market.getAllStoreProductsInfo(userId, storeId));
        } catch (Exception e) {
            return Response.exception(e);
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
    public boolean checkIfQuestionsExist(String userName) {
        if (userRepositoryAsHashmap.checkIfUserExists(userName) == null)
            return false;
        return checkIfQuestionsExist(userRepositoryAsHashmap.checkIfUserExists(userName).getUserId());
    }

    @Override
    public void exitSystemAsGuest(int userId) {
        userRepositoryAsHashmap.removeUser(userId);
    }

    @Override
    public List<Integer> getFailedProducts(int userId, int storeId) {
        return userRepository.getUser(userId).getFailedProducts(storeId);
    }

    @Override
    public double getTotalPriceOfCart(int userId) {
        return userRepository.getUser(userId).getTotalPriceOfCart();
    }

    @Override
    public void cancelPurchase(int userId) {
        userRepository.getUser(userId).cancelPurchase();
    }

    @Override
    public List<ServiceProduct> getAllFailedProductsAfterPayment(int userId) {
        return userRepository.getUser(userId).getAllFailedProductsAfterPayment().
                stream().map(ServiceProduct::new).collect(Collectors.toList());
    }

    @Override
    public Response<VoidResponse> allowPurchasePolicyConflicts(int userId, int storeId) {
        try {
            market.allowPurchasePolicyConflicts(userId, storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> disallowPurchasePolicyConflicts(int userId, int storeId) {
        try {
            market.disallowPurchasePolicyConflicts(userId, storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setStorePurchaseQuantityUpperBound(int userId, int storeId, int upperBound) {
        try {
            market.setStorePurchaseQuantityUpperBound(userId, storeId, upperBound);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setStorePurchaseQuantityLowerBound(int userId, int storeId, int lowerBound) {
        try {
            market.setStorePurchaseQuantityLowerBound(userId, storeId, lowerBound);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setStorePurchaseQuantityBounds(int userId, int storeId, int lowerBound, int upperBound) {
        try {
            market.setStorePurchaseQuantityBounds(userId, storeId, lowerBound, upperBound);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setStorePurchasePriceUpperBound(int userId, int storeId, int upperBound) {
        try {
            market.setStorePurchasePriceUpperBound(userId, storeId, upperBound);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setStorePurchasePriceLowerBound(int userId, int storeId, int lowerBound) {
        try {
            market.setStorePurchasePriceLowerBound(userId, storeId, lowerBound);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setStorePurchasePriceBounds(int userId, int storeId, int lowerBound, int upperBound) {
        try {
            market.setStorePurchasePriceBounds(userId, storeId, lowerBound, upperBound);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setProductPurchaseQuantityUpperBound(int userId, int storeId, int productId, int upperBound) {
        try {
            market.setProductPurchaseQuantityUpperBound(userId, storeId, productId, upperBound);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setProductPurchaseQuantityLowerBound(int userId, int storeId, int productId, int lowerBound) {
        try {
            market.setProductPurchaseQuantityLowerBound(userId, storeId, productId, lowerBound);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setProductPurchaseQuantityBounds(int userId, int storeId, int productId, int lowerBound, int upperBound) {
        try {
            market.setProductPurchaseQuantityBounds(userId, storeId, productId, lowerBound, upperBound);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setProductPurchasePriceUpperBound(int userId, int storeId, int productId, int upperBound) {
        try {
            market.setProductPurchasePriceUpperBound(userId, storeId, productId, upperBound);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setProductPurchasePriceLowerBound(int userId, int storeId, int productId, int lowerBound) {
        try {
            market.setProductPurchasePriceLowerBound(userId, storeId, productId, lowerBound);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setProductPurchasePriceBounds(int userId, int storeId, int productId, int lowerBound, int upperBound) {
        try {
            market.setProductPurchasePriceBounds(userId, storeId, productId, lowerBound, upperBound);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreVisibleDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate) {
        try {
            return Response.success(market.addStoreVisibleDiscount(userId, storeId, discountPercentage, discountLastDate));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreConditionalDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount) {
        try {
            return Response.success(market.addStoreConditionalDiscount(userId, storeId, discountPercentage, discountLastDate, minPriceForDiscount, quantityForDiscount));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreHiddenDiscount(int userId, int storeId, double discountPercentage, LocalDateTime discountLastDate, String code) {
        try {
            return Response.success(market.addStoreHiddenDiscount(userId, storeId, discountPercentage, discountLastDate, code));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeStoreDiscount(int userId, int storeId, int discountId) {
        try {
            market.removeStoreDiscount(userId, storeId, discountId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductVisibleDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate) {
        try {
            return Response.success(market.addProductVisibleDiscount(userId, storeId, productId, discountPercentage, discountLastDate));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductConditionalDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount) {
        try {
            return Response.success(market.addProductConditionalDiscount(userId, storeId, productId, discountPercentage, discountLastDate, minPriceForDiscount, quantityForDiscount));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductHiddenDiscount(int userId, int storeId, int productId, double discountPercentage, LocalDateTime discountLastDate, String code) {
        try {
            return Response.success(market.addProductHiddenDiscount(userId, storeId, productId, discountPercentage, discountLastDate, code));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeProductDiscount(int userId, int storeId, int productId, int discountId) {
        try {
            market.removeProductDiscount(userId, storeId, productId, discountId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public boolean isUserLogged(int userId) {
        return userRepositoryAsHashmap.getUser(userId).isLoggedIn();
    }

    @Override
    public Response<String> getUserPurchaseHistory(int userId) {
        try {
            isUserLogged(userId);
            return Response.success(userRepositoryAsHashmap.getUser(userId).getPurchaseHistory());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }


    @Override
    public Response<List<Pair<StoreInfo, String>>> getAllUserAssociatedStores(int userId) {
        List<Pair<Integer, String>> storeIdsAndRoles;
        try{
            storeIdsAndRoles = getStoresOfUser(userId);
        }
        catch(Exception e){
            return Response.exception(e);
        }

        List<Pair<StoreInfo, String>> storeInfosAndRoles = new LinkedList<>();
        for(Pair<Integer, String> storeIdAndRole : storeIdsAndRoles){
            try{
                StoreInfo storeInfo = market.getStoreInfo(userId, storeIdAndRole.getFirst());
                storeInfosAndRoles.add(Pair.of(storeInfo, storeIdAndRole.getSecond()));
            }
            catch(Exception ignored){
            }
        }
        return Response.success(storeInfosAndRoles);
    }

    @Override
    public Response<VoidResponse> hideStore(int userId, int storeId) {
        try {
            market.hideStore(userId, storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> unhideStore(int userId, int storeId) {
        try {
            market.unhideStore(userId, storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public void pushTest()
    {
        PushNotification.pushNotification("MY TEST!",2);
    }

    @Override
    public int getStoreFounder(int storeId) {
        try {
            return market.getStoreFounder(storeId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response<VoidResponse> deleteStore(int userId, int storeId) {
        try {
            market.deleteStore(userId, storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }


    @Override
    public Response<VoidResponse> fetchMessages(int userId) {
        try {
            userRepositoryAsHashmap.getUser(userId).fetchMessages();
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }
    @Override
    public Response<String> getUserPurchaseHistoryAsAdmin(int userId, int adminId){
        //change to return purchaseHistory
        try {
            if (!getUserStatus(adminId).equals("Admin") || !isUserLogged(adminId)) {
                throw new NoPermissionException("The user is not an admin or is not logged in");
            }
            User user = userRepositoryAsHashmap.getUser(userId);
            return Response.success(user.getPurchaseHistory());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<PurchaseHistory>> getStorePurchaseHistoryAsAdmin(int storeId, int adminId){
        try {
            if (!getUserStatus(adminId).equals("Admin") || !isUserLogged(adminId)) {
                throw new NoPermissionException("The user is not an admin or is not logged in");
            }
            return Response.success(market.getStorePurchaseHistoryAsAdmin(storeId, adminId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addOwner(int userId, int newOwnerId, int storeId) {
        try {
            market.addOwner(userId, newOwnerId, storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeOwner(int userId, int removeOwnerId, int storeId) {
        try {
            market.removeOwner(userId, removeOwnerId, storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addManager(int userId, int newManagerId, int storeId) {
        try {
            market.addManager(userId, newManagerId, storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeManager(int userId, int removeManagerId, int storeId) {
        try {
            market.removeManager(userId, removeManagerId, storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public List<WorkerCard> getStoreWorkersInfo(int userId, int storeId) {
        try {
            return market.getStoreWorkersInfo(userId, storeId);
        } catch (Exception e) {
            //TODO: handle exception
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getStoreOwners(int storeId) {
        try {
            return market.getStoreOwners(storeId);
        } catch (Exception e) {
            //TODO: handle exception
            throw new RuntimeException(e);
        }
    }

}
