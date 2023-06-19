package BGU.Group13B.service;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Interfaces.IDailyUserTrafficRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;
import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.System.UserTrafficRecord;
import BGU.Group13B.backend.User.*;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.backend.storePackage.newDiscoutns.DiscountInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;
import BGU.Group13B.service.entity.ReviewService;
import BGU.Group13B.service.entity.ServiceBasketProduct;
import BGU.Group13B.service.entity.ServiceProduct;
import BGU.Group13B.service.info.DiscountAccumulationTreeInfo;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private  IUserRepository userRepository = SingletonCollection.getUserRepository();
    private static final Logger LOGGER_INFO = Logger.getLogger(Session.class.getName());
    private static final Logger LOGGER_ERROR = Logger.getLogger(Session.class.getName());

    private IDailyUserTrafficRepository dailyUserTrafficRepository = SingletonCollection.getDailyUserTrafficRepository();

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

        userRepositoryAsHashmap.setSaveMode(false);
        SingletonCollection.getUserPermissionRepository().setSaveMode(false);
        //IMPORTANT need to initialize the session AFTER loading first user (id = 1) from database
        //id should 1
        //This should do nothing if the system was initialized in the past - making first admin
        int id = 1;
        userRepositoryAsHashmap.addUser(id, new User(id));
        register(id, "kingOfTheSheep", "SheePLover420",
                "mrsheep@gmail.com", "11", "11", "11", LocalDate.MIN);
    }

    @Override
    public Response<Integer> addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity, String description) {
       try {
           Integer result = market.addProduct(userId, storeId, productName, category, price, stockQuantity, description);
           return Response.success(result);
       } catch (Exception e) {
           System.out.println(e.getCause());
           System.out.println(e.getMessage());
           System.out.println(Arrays.toString(e.getStackTrace()));
           return Response.exception(e);
    }

    }

    /*good for development no need check if the item exists*/
    public Response<VoidResponse> addToCart(int userId, int storeId, int productId, int amount, double newPrice) {
        try {
            getUserRepository().getUser(userId).addToCart(storeId, productId, amount, newPrice);
            return Response.success();
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addToCart(int userId, int storeId, int productId) {
        addToCart(userId, storeId, productId, 1, -1);
        return Response.success();
    }

    @Override
    public double purchaseProductCart(int userId, String creditCardNumber,
                                      String creditCardMonth, String creditCardYear,
                                      String creditCardHolderFirstName,
                                      String creditCardCcv, String id,
                                      HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsCoupons,
                                      String/*store coupons*/ storeCoupon) {
        try {
            return getUserRepository().getUser(userId).
                    purchaseCart(creditCardNumber, creditCardMonth,
                            creditCardYear, creditCardHolderFirstName,
                            creditCardCcv, id, productsCoupons, storeCoupon);
        } catch (PurchaseFailedException | NoPermissionException e) {
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
            getUserRepository().getUser(userId).
                    purchaseCart(
                            creditCardNumber, creditCardMonth,
                            creditCardYear, creditCardHolderFirstName,
                            creditCardCVV, id,
                            address, city, country, zip);
            LOGGER_INFO.info("User " + userId + " purchased cart successfully");
            return Response.success();
        } catch (PurchaseFailedException | NoPermissionException e) {
            LOGGER_ERROR.severe("User " + userId + " failed to purchase cart");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Pair<Double, List<ServiceBasketProduct>>> startPurchaseBasketTransaction(int userId, List<String> coupons) {
        try {
            var priceSuccessfulItems = getUserRepository().getUser(userId).startPurchaseBasketTransaction(coupons);
            LOGGER_INFO.info("User " + userId + " started purchase basket transaction successfully");
            return Response.success(new Pair<>(priceSuccessfulItems.getFirst(),
                    priceSuccessfulItems.getSecond().stream().map(ServiceBasketProduct::new).collect(Collectors.toList())));
        } catch (PurchaseFailedException | NoPermissionException e) {
            LOGGER_ERROR.severe("User " + userId + " failed to start purchase basket transaction");
            return Response.failure(e.getMessage());
        }
    }

    @Override
    public Response<List<PurchaseHistory>> getStorePurchaseHistory(int userId, int storeId) {
        try {
            var result = market.getStorePurchaseHistory(userId, storeId);
            LOGGER_INFO.info("User " + userId + " got store " + storeId + " purchase history successfully");
            return Response.success(result);
        } catch (NoPermissionException e) {
            LOGGER_ERROR.severe("User " + userId + " failed to get store " + storeId + " purchase history");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice, int amount) {
        try {
            market.purchaseProposalSubmit(userId, storeId, productId, proposedPrice, amount);
            LOGGER_INFO.info("User " + userId + " submitted purchase proposal successfully");
            return Response.success();
        } catch (NoPermissionException e) {
            LOGGER_ERROR.severe("User " + userId + " failed to submit purchase proposal");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> purchaseProposalApprove(int managerId, int storeId, int productId) {
        try {
            market.purchaseProposalApprove(managerId, storeId, productId);
            LOGGER_INFO.info("Manager " + managerId + " approved purchase proposal successfully");
            return Response.success();
        } catch (NoPermissionException e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> purchaseProposalReject(int storeId, int managerId, int bidId) {
        try {
            market.purchaseProposalReject(managerId, storeId, bidId);
            LOGGER_INFO.info("Manager " + managerId + " rejected purchase proposal successfully");
            return Response.success();
        } catch (NoPermissionException e) {
            LOGGER_ERROR.severe("Manager " + managerId + " failed to reject purchase proposal");
            return Response.exception(e);
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
                                      String email, String answer1, String answer2, String answer3, LocalDate birthDate) {
        User user = getUserRepositoryAsHashmap().getUser(userId);

        //the first "if" might not be necessary when we will connect to web
        if (this.getUserRepositoryAsHashmap().checkIfUserWithEmailExists(email)) {
            throw new IllegalArgumentException("user with this email already exists!");//temporary
        }
        if (!user.isRegistered()) {
            if (getUserRepositoryAsHashmap().checkIfUserExists(username) == null) {
                user.register(username, password, email, answer1, answer2, answer3, birthDate);
                getUserRepositoryAsHashmap().save();
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
            var result = market.search(searchWords);
            LOGGER_INFO.info("Search for " + searchWords + " was successful");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("Search for " + searchWords + " failed");
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<ProductInfo>> filterByPriceRange(double minPrice, double maxPrice) {
        try {
            return Response.success(market.filterByPriceRange(minPrice, maxPrice));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<ProductInfo>> filterByProductRank(double minRating, double maxRating) {
        try {
            return Response.success(market.filterByProductRank(minRating, maxRating));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<ProductInfo>> filterByCategory(String category) {
        try {
            return Response.success(market.filterByCategory(category));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<ProductInfo>> filterByStoreRank(double minRating, double maxRating) {
        try {
            return Response.success(market.filterByStoreRank(minRating, maxRating));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public int login(int userID, String username, String password, String answer1, String answer2, String answer3) {
        try {
            //gets the user that we want to log into
            User user = getUserRepositoryAsHashmap().checkIfUserExists(username);
            synchronized (user) {
                user.login(username, password, answer1, answer2, answer3);
                /*example of use*/
                LOGGER_INFO.info("user " + username + " logged in");
                //gets the new id - of the user we're logging into
                int id = getUserRepositoryAsHashmap().getUserId(user);

                //update daily visitors
                switch (user.getPopulationStatus()) {
                    case ADMIN ->  SingletonCollection.getDailyUserTrafficRepository().addAdmin();
                    case OWNER -> SingletonCollection.getDailyUserTrafficRepository().addStoreOwner();
                    case MANAGER_NOT_OWNER -> SingletonCollection.getDailyUserTrafficRepository().addStoreManagerThatIsNotOwner();
                    case REGULAR_MEMBER -> SingletonCollection.getDailyUserTrafficRepository().addRegularMember();
                    case GUEST -> SingletonCollection.getDailyUserTrafficRepository().addGuest();
                }
                BroadCaster.broadcastUserTraffic();
                return id;
            }

        } catch (Exception e) {
            //line below temporary
            throw new IllegalArgumentException(e.getMessage());

        }

    }

    @Override
    public void logout(int userID) {
        synchronized (getUserRepositoryAsHashmap().getUser(userID)) {
            getUserRepositoryAsHashmap().getUser(userID).logout();
        }
    }

    @Override
    public Response<Integer> addStore(int userId, String storeName, String category) {
        User user = getUserRepositoryAsHashmap().getUser(userId);
        synchronized (user) {
            if (user.isRegistered()) {
                try {
                    int storeId = market.addStore(userId, storeName, category);
                    user.addPermission(storeId, UserPermissions.StoreRole.FOUNDER);
                    LOGGER_INFO.info("store " + storeName + " was added successfully");
                    return Response.success(storeId);
                } catch (Exception e) {
                    LOGGER_ERROR.severe("store " + storeName + " was not added successfully");
                    return Response.exception(e);
                }
            }
            return Response.failure("user is not registered");
        }
    }

    @Override
    public Response<VoidResponse> addProductToCart(int userId, int productId, int storeId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).addProductToCart(productId, storeId);
            LOGGER_INFO.info("product " + productId + " was added to cart successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("product " + productId + " was not added to cart successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> clearMessageToReply(int userId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).clearMessageToReply();
            LOGGER_INFO.info("message to reply was cleared successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("message to reply was not cleared successfully");
            return Response.exception(e);
        }
    }

    public Response<VoidResponse> openComplaint(int userId, String header, String complaint) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).openComplaint(header, complaint);
            LOGGER_INFO.info("complaint was opened successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("complaint was not opened successfully");
            return Response.exception(e);
        }
    }


    public Response<Message> getComplaint(int userId) {
        try {
            var result = getUserRepositoryAsHashmap().getUser(userId).getComplaint();
            LOGGER_INFO.info("complaint was retrieved successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("complaint was not retrieved successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> markMessageAsReadAdmin(int userId, String receiverId, String senderId, int messageId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).markMessageAsReadAdmin(receiverId, senderId, messageId);
            LOGGER_INFO.info("message was marked as read successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("message was not marked as read successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> sendMassageAdmin(int userId, String receiverId, String header, String massage) {
        try {
            if (getUserRepository().checkIfUserExists(receiverId) == null)
                throw new RuntimeException("receiver Id not found");
            getUserRepositoryAsHashmap().getUser(userId).sendMassageAdmin(receiverId, header, massage);
            LOGGER_INFO.info("message was sent successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("message was not sent successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> answerComplaint(int userId, String answer) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).answerComplaint(answer);
            LOGGER_INFO.info("complaint was answered successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("complaint was not answered successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Message> readMessage(int userId) {
        try {
            var result = getUserRepositoryAsHashmap().getUser(userId).readMassage();
            LOGGER_INFO.info("message was read successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("message was not read successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> replayMessage(int userId, String message) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).replayMessage(message);
            LOGGER_INFO.info("message was replayed successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("message was not replayed successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Message> readOldMessage(int userId) {
        try {
            var result = getUserRepositoryAsHashmap().getUser(userId).readOldMessage();
            LOGGER_INFO.info("old message was read successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("old message was not read successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> refreshOldMessages(int userId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).refreshOldMessage();
            LOGGER_INFO.info("old messages were refreshed successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("old messages were not refreshed successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> sendMassageStore(int userId, String header, String massage, int storeId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).sendMassageStore(header, massage, storeId);
            LOGGER_INFO.info("message was sent successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("message was not sent successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Message> readUnreadMassageStore(int userId, int storeId) {
        try {
            var result = getUserRepositoryAsHashmap().getUser(userId).readUnreadMassageStore(storeId);
            LOGGER_INFO.info("unread message was read successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("unread message was not read successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Message> readReadMassageStore(int userId, int storeId) {
        try {
            var result = getUserRepositoryAsHashmap().getUser(userId).readReadMassageStore(storeId);
            LOGGER_INFO.info("read message was read successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("read message was not read successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> answerQuestionStore(int userId, String answer) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).answerQuestionStore(answer);
            LOGGER_INFO.info("question was answered successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("question was not answered successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> refreshOldMessageStore(int userId, int storeId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).refreshOldMessageStore(storeId);
            LOGGER_INFO.info("old messages were refreshed successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("old messages were not refreshed successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addReview(int userId, String review, int storeId, int productId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).addReview(review, storeId, productId);
            LOGGER_INFO.info("review was added successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("review was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeReview(int userId, int storeId, int productId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).removeReview(storeId, productId);
            LOGGER_INFO.info("review was removed successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("review was not removed successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Review> getReview(int userId, int storeId, int productId) {
        try {
            var result = getUserRepositoryAsHashmap().getUser(userId).getReview(storeId, productId);
            LOGGER_INFO.info("review was got successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("review was not got successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<ReviewService>> getAllReviews(int userId, int storeId, int productId) {
        try {
            List<Review> reviews = getUserRepositoryAsHashmap().getUser(userId).getAllReviews(storeId, productId, userId);
            List<ReviewService> reviewServices = new ArrayList<>();
            int i = 0;
            for (Review review : reviews) {
                Response<Float> scoreResponse = getProductScoreUser(userId, review.getStoreId(), review.getProductId(), review.getUserId());
                String scoreString;
                if (scoreResponse.didntSucceed()) {
                    scoreString = "non";
                } else {
                    scoreString = scoreResponse.getData().toString();
                }
                reviewServices.add(new ReviewService(getUserRepositoryAsHashmap().getUser(review.getUserId()).getUserName(), review.getReview(), scoreString));
            }
            LOGGER_INFO.info("all reviews were got successfully");
            return Response.success(reviewServices);
        } catch (Exception e) {
            LOGGER_ERROR.severe("all reviews were not got successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Float> getProductScore(int userId, int storeId, int productId) {
        try {
            var result = getUserRepositoryAsHashmap().getUser(userId).getProductScore(storeId, productId);
            LOGGER_INFO.info("product score was got successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("product score was not got successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Float> getProductScoreUser(int userId, int storeId, int productId, int userIdTarget) {
        try {
            var result = getUserRepositoryAsHashmap().getUser(userId).getProductScoreUser(userIdTarget, storeId, productId);
            LOGGER_INFO.info("product score was got successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("product score was not got successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addAndSetProductScore(int userId, int storeId, int productId, int score) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).addAndSetProductScore(storeId, productId, score);
            LOGGER_INFO.info("product score was added and set successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("product score was not added and set successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeProductScore(int userId, int storeId, int productId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).removeProductScore(storeId, productId);
            LOGGER_INFO.info("product score was removed successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("product score was not removed successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addStoreScore(int userId, int storeId, int score) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).addStoreScore(storeId, score);
            LOGGER_INFO.info("store score was added successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("store score was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeStoreScore(int userId, int storeId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).removeStoreScore(storeId);
            LOGGER_INFO.info("store score was removed successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("store score was not removed successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> modifyStoreScore(int userId, int storeId, int score) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).modifyStoreScore(storeId, score);
            LOGGER_INFO.info("store score was modified successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("store score was not modified successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Float> getStoreScore(int userId, int storeId) {
        try {
            var result = getUserRepositoryAsHashmap().getUser(userId).getStoreScore(storeId);
            LOGGER_INFO.info("store score was got successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store score was not got successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<String> getCartDescription(int userId) {
        try {
            return Response.success(getUserRepositoryAsHashmap().getUser(userId).getCartDescription());
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response<List<ServiceBasketProduct>> getCartContent(int userId) {
        try {
            var cartContent = getUserRepositoryAsHashmap().getUser(userId).getCartBasketProducts();
            LOGGER_INFO.info("cart content was got successfully");
            return Response.success(cartContent.stream().map(ServiceBasketProduct::new).collect(Collectors.toList()));
        } catch (Exception e) {
            LOGGER_ERROR.severe("cart content was not got successfully");
            return Response.exception(e);
        }
    }

    @Override
    public void removeProductFromCart(int userId, int storeId, int productId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).removeProductFromCart(storeId, productId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Response<VoidResponse> setProductName(int userId, int storeId, int productId, String name) {
        try {
            market.setProductName(userId, storeId, productId, name);

LOGGER_INFO.info("product name was set successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("product name was not set successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setProductCategory(int userId, int storeId, int productId, String category) {
        try {
            market.setProductCategory(userId, storeId, productId, category);
            LOGGER_INFO.info("product category was set successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("product category was not set successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setProductPrice(int userId, int storeId, int productId, double price) {
        try {
            market.setProductPrice(userId, storeId, productId, price);
            LOGGER_INFO.info("product price was set successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("product price was not set successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setProductStockQuantity(int userId, int storeId, int productId, int stockQuantity) {
        try {
            market.setProductStockQuantity(userId, storeId, productId, stockQuantity);
            LOGGER_INFO.info("product stock quantity was set successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("product stock quantity was not set successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setProductDescription(int userId, int storeId, int productId, String description) {
        try {
            market.setProductDescription(userId, storeId, productId, description);

LOGGER_INFO.info("product description was set successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("product description was not set successfully");
            return Response.exception(e);
        }
    }

    @Override
    public int enterAsGuest() {
        int id = getUserRepositoryAsHashmap().getNewUserId();
        getUserRepositoryAsHashmap().addUser(id, new User(id));
        SingletonCollection.getDailyUserTrafficRepository().addGuest();
        BroadCaster.broadcastUserTraffic();
        return id;
    }


    @Override
    public Response<VoidResponse> removeProduct(int userId, int storeId, int productId) {
        try {
            market.removeProduct(userId, storeId, productId);
            LOGGER_INFO.info("product was removed successfully");

            return Response.success(new VoidResponse());
        } catch (Exception e) {
            LOGGER_ERROR.severe("product was not removed successfully");
            return Response.exception(e);
        }
    }

    @Override
    public String getUserName(int userId) {
        return getUserRepositoryAsHashmap().getUser(userId).getUserName();
    }

    @Override
    public Response<String> getUserNameRes(int userId) {
        try {
            var result = getUserRepositoryAsHashmap().getUser(userId).getUserName();
            LOGGER_INFO.info("user name was got successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("user name was not got successfully");
            return Response.exception(e);
        }
    }

    @Override
    public void setUsername(int userId, String newUsername) {
        getUserRepositoryAsHashmap().getUser(userId).setUserName(newUsername);
    }

    @Override
    public void setUserStatus(int admin_id, int userId, int newStatus) {
        if (!getUserStatus(admin_id).equals("Admin")) {
            //should throw an exception
            throw new IllegalArgumentException("isn't an admin");
        }
        if (newStatus == 1 && getUserRepositoryAsHashmap().getUser(userId).getStatus() == UserPermissions.UserPermissionStatus.MEMBER)
            getUserRepositoryAsHashmap().getUser(userId).setPermissions(UserPermissions.UserPermissionStatus.ADMIN);

        if (newStatus == 2 && getUserRepositoryAsHashmap().getUser(userId).getStatus() == UserPermissions.UserPermissionStatus.ADMIN)
            getUserRepositoryAsHashmap().getUser(userId).setPermissions(UserPermissions.UserPermissionStatus.MEMBER);
    }

    @Override
    public String getUserStatus(int userId) {
        if (getUserRepositoryAsHashmap().getUser(userId).getStatus() == UserPermissions.UserPermissionStatus.MEMBER)
            return "Member";
        else if (getUserRepositoryAsHashmap().getUser(userId).getStatus() == UserPermissions.UserPermissionStatus.ADMIN)
            return "Admin";
        else
            return "Guest";
    }

    @Override
    public String getUserEmail(int userId) {
        return getUserRepositoryAsHashmap().getUser(userId).getEmail();
    }

    @Override
    public List<Pair<Integer, String>> getStoresOfUser(int userId) {
        return getUserRepositoryAsHashmap().getUser(userId).getStoresAndRoles();
    }

    //with logs info and error
    public Response<StoreInfo> getStoreInfo(int userId, int storeId) {
        try {
            var result = market.getStoreInfo(userId, storeId);
            LOGGER_INFO.info("store info was got successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store info was not got successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<ProductInfo> getStoreProductInfo(int userId, int storeId, int productId) {
        try {
            var result = market.getStoreProductInfo(userId, storeId, productId);
            LOGGER_INFO.info("store product info was got successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store product info was not got successfully");
            return Response.exception(e);
        }
    }

    @Override
    public void changeProductQuantityInCart(int userId, int storeId, int productId, int quantity) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).changeProductQuantityInCart(storeId, productId, quantity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response<Set<ProductInfo>> getAllStoreProductsInfo(int userId, int storeId) {
        try {
            var result = market.getAllStoreProductsInfo(userId, storeId);
            LOGGER_INFO.info("all store products info was got successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("all store products info was not got successfully");
            return Response.exception(e);
        }
    }

    public boolean SecurityAnswer1Exists(int userId) {
        return getUserRepositoryAsHashmap().getUser(userId).SecurityAnswer1Exists();
    }

    @Override
    public boolean SecurityAnswer2Exists(int userId) {
        return getUserRepositoryAsHashmap().getUser(userId).SecurityAnswer2Exists();
    }

    @Override
    public boolean SecurityAnswer3Exists(int userId) {
        return getUserRepositoryAsHashmap().getUser(userId).SecurityAnswer3Exists();
    }

    @Override
    public boolean checkIfQuestionsExist(int userId) {
        return SecurityAnswer1Exists(userId) || SecurityAnswer2Exists(userId) || SecurityAnswer3Exists(userId);
    }

    @Override
    public boolean checkIfQuestionsExist(String userName) {
        if (getUserRepositoryAsHashmap().checkIfUserExists(userName) == null)
            return false;
        return checkIfQuestionsExist(getUserRepositoryAsHashmap().checkIfUserExists(userName).getUserId());
    }

    @Override
    public void exitSystemAsGuest(int userId) {
        getUserRepositoryAsHashmap().removeUser(userId);
    }

    @Override
    public List<Integer> getFailedProducts(int userId, int storeId) {
        return getUserRepository().getUser(userId).getFailedProducts(storeId);
    }

    @Override
    public Response<Double> getTotalPriceOfCart(int userId) {
        try {
            double total = getUserRepository().getUser(userId).getTotalPriceOfCart();
            LOGGER_INFO.info("total price of cart was got successfully");
            return Response.success(total);
        } catch (Exception e) {
            LOGGER_ERROR.severe("total price of cart was not got successfully");
            return Response.failure(e.getMessage());
        }
    }

    @Override
    public Response<VoidResponse> cancelPurchase(int userId) {
        try {
            getUserRepository().getUser(userId).cancelPurchase();
            LOGGER_INFO.info("purchase was canceled successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("purchase was not canceled successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<ServiceProduct>> getAllFailedProductsAfterPayment(int userId) {
        try {
            var result = getUserRepository().getUser(userId).getAllFailedProductsAfterPayment().
                    stream().map(ServiceProduct::new).collect(Collectors.toList());
            LOGGER_INFO.info("all failed products after payment was got successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("all failed products after payment was not got successfully");
            return Response.exception(e);
        }
    }


    @Override
    public boolean isUserLogged(int userId) {
        return getUserRepositoryAsHashmap().getUser(userId).isLoggedIn();
    }

    @Override
    public Response<List<PurchaseHistory>> getUserPurchaseHistory(int userId) {
        try {
            isUserLogged(userId);
            var result = getUserRepositoryAsHashmap().getUser(userId).getPurchaseHistory();
            LOGGER_INFO.info("user purchase history was got successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("user purchase history was not got successfully");
            return Response.exception(e);
        }
    }


    @Override
    public Response<List<Pair<StoreInfo, String>>> getAllUserAssociatedStores(int userId) {
        List<Pair<Integer, String>> storeIdsAndRoles;
        try {
            storeIdsAndRoles = getStoresOfUser(userId);
        } catch (Exception e) {
            LOGGER_ERROR.severe("all user associated stores was not got successfully");
            return Response.exception(e);
        }

        List<Pair<StoreInfo, String>> storeInfosAndRoles = new LinkedList<>();
        for (Pair<Integer, String> storeIdAndRole : storeIdsAndRoles) {
            try {
                StoreInfo storeInfo = market.getStoreInfo(userId, storeIdAndRole.getFirst());
                storeInfosAndRoles.add(Pair.of(storeInfo, storeIdAndRole.getSecond()));
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        LOGGER_INFO.info("all user associated stores was got successfully");
        return Response.success(storeInfosAndRoles);
    }

    @Override
    public Response<VoidResponse> hideStore(int userId, int storeId) {
        try {
            market.hideStore(userId, storeId);
            LOGGER_INFO.info("store was hidden successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("store was not hidden successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> unhideStore(int userId, int storeId) {
        try {
            market.unhideStore(userId, storeId);
            LOGGER_INFO.info("store was unhidden successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("store was not unhidden successfully");
            return Response.exception(e);
        }
    }

    @Override
    public void pushTest() {
        PushNotification.pushNotification("MY TEST!", 2);
    }

    @Override
    public Response<Integer> getStoreFounder(int storeId) {
        try {
            var result = market.getStoreFounder(storeId);
            LOGGER_INFO.info("store founder was got successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store founder was not got successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> deleteStore(int userId, int storeId) {
        try {
            market.deleteStore(userId, storeId);
            LOGGER_INFO.info("store was deleted successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("store was not deleted successfully");
            return Response.exception(e);
        }
    }


    @Override
    public Response<VoidResponse> fetchMessages(int userId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).fetchMessages();
            LOGGER_INFO.info("messages was fetched successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("messages was not fetched successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<PurchaseHistory>> getUserPurchaseHistoryAsAdmin(int userId, int adminId) {
        try {
            if (!getUserStatus(adminId).equals("Admin") || !isUserLogged(adminId)) {
                throw new NoPermissionException("The user is not an admin or is not logged in");
            }
            User user = getUserRepositoryAsHashmap().getUser(userId);
            var result = user.getPurchaseHistory();
            LOGGER_INFO.info("user purchase history as admin was got successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("user purchase history as admin was not got successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<PurchaseHistory>> getStorePurchaseHistoryAsAdmin(int storeId, int adminId) {
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
            LOGGER_INFO.info("owner was added successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("owner was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeOwner(int userId, int removeOwnerId, int storeId) {
        try {
            market.removeOwner(userId, removeOwnerId, storeId);
            LOGGER_INFO.info("owner was removed successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("owner was not removed successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addManager(int userId, int newManagerId, int storeId) {
        try {
            market.addManager(userId, newManagerId, storeId);
            LOGGER_INFO.info("manager was added successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("manager was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeManager(int userId, int removeManagerId, int storeId) {
        try {
            market.removeManager(userId, removeManagerId, storeId);
            LOGGER_INFO.info("manager was removed successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("manager was not removed successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<WorkerCard>> getStoreWorkersInfo(int userId, int storeId) {
        try {
            var result = market.getStoreWorkersInfo(userId, storeId);
            LOGGER_INFO.info("store workers info was got successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store workers info was not got successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setPurchasePolicyCondition(int storeId, int userId, int conditionId) {
        try {
            market.setPurchasePolicyCondition(storeId, userId, conditionId);
            LOGGER_INFO.info("purchase policy condition was set successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("purchase policy condition was not set successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addORCondition(int storeId, int userId, int condition1, int condition2) {
        try {
            var result = market.addORCondition(storeId, userId, condition1, condition2);
            LOGGER_INFO.info("OR condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("OR condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addANDCondition(int storeId, int userId, int condition1, int condition2) {
        try {
            var result = market.addANDCondition(storeId, userId, condition1, condition2);
            LOGGER_INFO.info("AND condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("AND condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addXORCondition(int storeId, int userId, int condition1, int condition2) {
        try {
            var result = market.addXORCondition(storeId, userId, condition1, condition2);
            LOGGER_INFO.info("XOR condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("XOR condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addIMPLYCondition(int storeId, int userId, int condition1, int condition2) {
        try {
            var result = market.addIMPLYCondition(storeId, userId, condition1, condition2);
            LOGGER_INFO.info("IMPLY condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("IMPLY condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStorePriceCondition(int storeId, int userId, double lowerBound, double upperBound) {
        try {
            var result = market.addStorePriceCondition(storeId, userId, lowerBound, upperBound);
            LOGGER_INFO.info("store price condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store price condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStorePriceCondition(int storeId, int userId, double lowerBound) {
        try {
            var result = market.addStorePriceCondition(storeId, userId, lowerBound);
            LOGGER_INFO.info("store price condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store price condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreQuantityCondition(int storeId, int userId, int lowerBound, int upperBound) {
        try {
            var result = market.addStoreQuantityCondition(storeId, userId, lowerBound, upperBound);
            LOGGER_INFO.info("store quantity condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store quantity condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreQuantityCondition(int storeId, int userId, int lowerBound) {
        try {
            var result = market.addStoreQuantityCondition(storeId, userId, lowerBound);
            LOGGER_INFO.info("store quantity condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store quantity condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryPriceCondition(int storeId, int userId, String category, double lowerBound, double upperBound) {
        try {
            var result = market.addCategoryPriceCondition(storeId, userId, category, lowerBound, upperBound);
            LOGGER_INFO.info("category price condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("category price condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryPriceCondition(int storeId, int userId, String category, double lowerBound) {
        try {
            var result = market.addCategoryPriceCondition(storeId, userId, category, lowerBound);
            LOGGER_INFO.info("category price condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("category price condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryQuantityCondition(int storeId, int userId, String category, int lowerBound, int upperBound) {
        try {
            var result = market.addCategoryQuantityCondition(storeId, userId, category, lowerBound, upperBound);
            LOGGER_INFO.info("category quantity condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("category quantity condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryQuantityCondition(int storeId, int userId, String category, int lowerBound) {
        try {
            var result = market.addCategoryQuantityCondition(storeId, userId, category, lowerBound);
            LOGGER_INFO.info("category quantity condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("category quantity condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addDateCondition(int storeId, int userId, LocalDateTime lowerBound, LocalDateTime upperBound) {
        try {
            var result = market.addDateCondition(storeId, userId, lowerBound, upperBound);
            LOGGER_INFO.info("date condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("date condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addDateCondition(int storeId, int userId, LocalDateTime lowerBound) {
        try {

            var result = market.addDateCondition(storeId, userId, lowerBound);
            LOGGER_INFO.info("date condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("date condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductPriceCondition(int storeId, int userId, int productId, double lowerBound, double upperBound) {
        try {
            var result = market.addProductPriceCondition(storeId, userId, productId, lowerBound, upperBound);
            LOGGER_INFO.info("product price condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("product price condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductPriceCondition(int storeId, int userId, int productId, double lowerBound) {
        try {
            var result = market.addProductPriceCondition(storeId, userId, productId, lowerBound);
            LOGGER_INFO.info("product price condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("product price condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductQuantityCondition(int storeId, int userId, int productId, int lowerBound, int upperBound) {
        try {
            var result = market.addProductQuantityCondition(storeId, userId, productId, lowerBound, upperBound);
            LOGGER_INFO.info("product quantity condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("product quantity condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductQuantityCondition(int storeId, int userId, int productId, int lowerBound) {
        try {
            var result = market.addProductQuantityCondition(storeId, userId, productId, lowerBound);
            LOGGER_INFO.info("product quantity condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("product quantity condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addTimeCondition(int storeId, int userId, LocalDateTime lowerBound, LocalDateTime upperBound) {
        try {
            var result = market.addTimeCondition(storeId, userId, lowerBound, upperBound);
            LOGGER_INFO.info("time condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("time condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addTimeCondition(int storeId, int userId, LocalDateTime lowerBound) {
        try {
            var result = market.addTimeCondition(storeId, userId, lowerBound);
            LOGGER_INFO.info("time condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("time condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addUserAgeCondition(int storeId, int userId, int lowerBound, int upperBound) {
        try {
            var result = market.addUserAgeCondition(storeId, userId, lowerBound, upperBound);
            LOGGER_INFO.info("user age condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("user age condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addUserAgeCondition(int storeId, int userId, int lowerBound) {
        try {
            var result = market.addUserAgeCondition(storeId, userId, lowerBound);
            LOGGER_INFO.info("user age condition was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("user age condition was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String coupon) {
        try {
            var result = market.addStoreDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, coupon);
            LOGGER_INFO.info("store discount was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store discount was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String coupon) {
        try {
            var result = market.addStoreDiscount(storeId, userId, discountPercentage, expirationDate, coupon);
            LOGGER_INFO.info("store discount was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store discount was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate) {
        try {
            var result = market.addStoreDiscount(storeId, userId, conditionId, discountPercentage, expirationDate);
            LOGGER_INFO.info("store discount was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store discount was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate) {
        try {
            var result = market.addStoreDiscount(storeId, userId, discountPercentage, expirationDate);
            LOGGER_INFO.info("store discount was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store discount was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        try {
            var result = market.addCategoryDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, category, coupon);
            LOGGER_INFO.info("category discount was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("category discount was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        try {
            var result = market.addCategoryDiscount(storeId, userId, discountPercentage, expirationDate, category, coupon);
            LOGGER_INFO.info("category discount was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("category discount was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String category) {
        try {
            var result = market.addCategoryDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, category);
            LOGGER_INFO.info("category discount was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("category discount was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String category) {
        try {
            var result = market.addCategoryDiscount(storeId, userId, discountPercentage, expirationDate, category);
            LOGGER_INFO.info("category discount was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("category discount was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        try {
            var result = market.addProductDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, productId, coupon);
            LOGGER_INFO.info("product discount was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("product discount was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductDiscount(int storeId, int userId, double discountPercentage, LocalDate
            expirationDate, int productId, String coupon) {
        try {
            var result = market.addProductDiscount(storeId, userId, discountPercentage, expirationDate, productId, coupon);
            LOGGER_INFO.info("product discount was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("product discount was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductDiscount(int storeId, int userId, int conditionId,
                                                double discountPercentage, LocalDate expirationDate, int productId) {
        try {
            var result = market.addProductDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, productId);
            LOGGER_INFO.info("product discount was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("product discount was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductDiscount(int storeId, int userId, double discountPercentage, LocalDate
            expirationDate, int productId) {
        try {
            var result = market.addProductDiscount(storeId, userId, discountPercentage, expirationDate, productId);
            LOGGER_INFO.info("product discount was added successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("product discount was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<DiscountInfo>> getStoreDiscounts(int storeId, int userId) {
        try {
            var result = market.getStoreDiscounts(storeId, userId).stream().map(d -> new DiscountInfo(d.getDiscountId(), d.toString())).collect(Collectors.toList());
            LOGGER_INFO.info("store discounts were retrieved successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store discounts were not retrieved successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<DiscountInfo> getDiscount(int storeId, int userId, int discountId) {
        try {
            var discount = market.getDiscount(storeId, userId, discountId);
            LOGGER_INFO.info("discount was retrieved successfully");
            return Response.success(new DiscountInfo(discountId, discount.toString()));
        } catch (Exception e) {
            LOGGER_ERROR.severe("discount was not retrieved successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeDiscount(int storeId, int userId, int discountId) {
        try {
            market.removeDiscount(storeId, userId, discountId);
            LOGGER_INFO.info("discount was removed successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("discount was not removed successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addDiscountAsRoot(int storeId, int userId, int discountId) {
        try {
            market.addDiscountAsRoot(storeId, userId, discountId);
            LOGGER_INFO.info("discount was added as root successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("discount was not added as root successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addDiscountToXORRoot(int storeId, int userId, int discountId) {
        try {
            market.addDiscountToXORRoot(storeId, userId, discountId);
            LOGGER_INFO.info("discount was added to xor root successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("discount was not added to xor root successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addDiscountToMAXRoot(int storeId, int userId, int discountId) {
        try {
            market.addDiscountToMAXRoot(storeId, userId, discountId);
            LOGGER_INFO.info("discount was added to max root successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("discount was not added to max root successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addDiscountToADDRoot(int storeId, int userId, int discountId) {
        try {
            market.addDiscountToADDRoot(storeId, userId, discountId);
            LOGGER_INFO.info("discount was added to add root successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("discount was not added to add root successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<DiscountAccumulationTreeInfo> getDiscountAccumulationTree(int storeId, int userId) {
        try {
            var result = market.getDiscountAccumulationTree(storeId, userId);
            LOGGER_INFO.info("discount accumulation tree was retrieved successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("discount accumulation tree was not retrieved successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> deleteStoreAccumulationTree(int storeId, int userId) {
        try {
            market.deleteStoreAccumulationTree(storeId, userId);
            LOGGER_INFO.info("discount accumulation tree was deleted successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("discount accumulation tree was not deleted successfully");
            return Response.exception(e);
        }
    }

    @Override
    public List<Integer> getStoreOwners(int storeId) {
        try {
            return market.getStoreOwners(storeId);
        } catch (Exception e) {
            //TODO: handle exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response<VoidResponse> addIndividualPermission(int userId, int managerId, int storeId, UserPermissions.IndividualPermission individualPermission) {
        try {
            market.addIndividualPermission(userId, managerId, storeId, individualPermission);
            LOGGER_INFO.info("individual permission was added successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("individual permission was not added successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeIndividualPermission(int userId, int managerId, int storeId, UserPermissions.IndividualPermission individualPermission) {
        try {
            market.removeIndividualPermission(userId, managerId, storeId, individualPermission);
            LOGGER_INFO.info("individual permission was removed successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("individual permission was not removed successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> getUserIdByUsername(String username) {
        try {
            var result = getUserRepository().getUserIdByUsername(username);
            LOGGER_INFO.info("user id was retrieved successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("user id was not retrieved successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<HashMap<Integer, String>> getUserIdsToUsernamesMapper(List<Integer> userIds) {
        try {
            var result = getUserRepository().getUserIdsToUsernamesMapper(userIds);
            LOGGER_INFO.info("user ids to usernames mapper was retrieved successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("user ids to usernames mapper was not retrieved successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Boolean> isStoreHidden(int storeId) {
        try {
            var result = market.isStoreHidden(storeId);
            LOGGER_INFO.info("store hidden status was retrieved successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store hidden status was not retrieved successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Boolean> isAdmin(int userId) {
        try {
            var result = getUserStatus(userId).equals("Admin");
            LOGGER_INFO.info("user admin status was retrieved successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("user admin status was not retrieved successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Condition> getStorePurchasePolicy(int storeId, int userId) {
        try {
            var result = market.getStorePurchasePolicy(storeId, userId);
            LOGGER_INFO.info("store purchase policy was retrieved successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store purchase policy was not retrieved successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> resetStorePurchasePolicy(int storeId, int userId) {
        try {
            market.resetStorePurchasePolicy(storeId, userId);
            LOGGER_INFO.info("store purchase policy was reset successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("store purchase policy was not reset successfully");
            return Response.exception(e);
        }
    }

    public Response<StoreInfo> getGeneralStoreInfo(int storeId) {
        try {
            var result = market.getGeneralStoreInfo(storeId);
            LOGGER_INFO.info("general store info was retrieved successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("general store info was not retrieved successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<StoreInfo>> getAllStores() {
        try {
            Set<StoreInfo> storesInfos = market.getAllGeneralStoreInfo();
            List<StoreInfo> asList = storesInfos.stream().toList();
            LOGGER_INFO.info("all stores info was retrieved successfully");
            return Response.success(asList);
        } catch (Exception e) {
            LOGGER_ERROR.severe("all stores info was not retrieved successfully");
            return Response.exception(e);
        }

    }

    @Override
    public Response<UserTrafficRecord> getUserTrafficOfRange(int userId, LocalDate from, LocalDate to) {
        try {
            var isAdmin = isAdmin(userId);
            if (isAdmin.didntSucceed()) {
                LOGGER_ERROR.severe("failed to check user permissions");
                return Response.failure("failed to check user permissions");
            }
            if (!isAdmin.getData()){
                LOGGER_ERROR.severe("user is not an admin");
                return Response.failure("user is not an admin");
            }
            var result = SingletonCollection.getDailyUserTrafficRepository().getUserTrafficOfRage(from, to);
            LOGGER_INFO.info("user traffic of range was retrieved successfully");
            return Response.success(result);

        } catch (Exception e) {
            LOGGER_ERROR.severe("user traffic of range was not retrieved successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<Boolean> removeMember(int adminId, int userId) {
        try {
            if (!getUserStatus(adminId).equals("ADMIN") || !isUserLogged(adminId))
                throw new IllegalArgumentException("user is not an admin or not logged in");
            LOGGER_INFO.info("admin is trying to remove member: " + userId);
            getUserRepositoryAsHashmap().removeMember(adminId, userId);
            return Response.success(true);
        } catch (Exception e) {
            LOGGER_INFO.info("member removal failed");
            return Response.failure(e.getMessage());
        }
    }


    @Override
    public Response<double[]> getSystemHistoryIncome(int userId, LocalDate from, LocalDate to) {
        try {
            var isAdmin = isAdmin(userId);
            if (isAdmin.didntSucceed()) {
                return Response.failure("failed to check user permissions");
            }

            if (!isAdmin.getData()) {
                return Response.failure("user is not an admin");
            }
            var result = market.getSystemHistoryIncome(from, to);
            LOGGER_INFO.info("system history income was retrieved successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("system history income was not retrieved successfully");
            return Response.exception(e);
        }
    }

    @Override
    public Response<double[]> getStoreHistoryIncome(int storeId, int userId, LocalDate from, LocalDate to) {
        try{
            return Response.success(market.getStoreHistoryIncome(storeId, userId, from, to));
        }
        catch (Exception e){
            return Response.exception(e);
        }
    }



    public UserCard getUserInfo(int userId, int userInfoId) {
        try {
            return market.getUserInfo(userId, userInfoId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Response<List<UserCard>> getAllUserCards(int userId) {
        try {
            var result = market.getAllUserCards(userId);
            LOGGER_INFO.info("all user cards were retrieved successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("all user cards were not retrieved successfully");
            return Response.exception(e);
        }
    }

    public Response<Integer> removeUser(int userId, int removeUserId) {
        try {
            return Response.success(1);//placeholder for the feature in the branch
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    public Response<String> getStoreName(int storeId) {
        try {
            var result = market.getStoreName(storeId);
            LOGGER_INFO.info("store name was retrieved successfully");
            return Response.success(result);
        } catch (Exception e) {
            LOGGER_ERROR.severe("store name was not retrieved successfully");
            return Response.failure(e.getMessage());
        }
    }

    public Response<VoidResponse> removeBasketProducts(List<ServiceBasketProduct> serviceBasketProductsToRemove, int userId) {
        try {
            getUserRepository().getUser(userId).removeBasketProducts(serviceBasketProductsToRemove.stream().map(serviceBasketProduct -> Pair.of(serviceBasketProduct.getProductId(), serviceBasketProduct.getStoreId())).toList());
            LOGGER_INFO.info("basket products were removed successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("basket products were not removed successfully");
            return Response.failure(e.getMessage());
        }
    }

    public Response<VoidResponse> removeBasketProduct(int userId, int productId, int storeId) {
        try {
            getUserRepository().getUser(userId).removeBasketProduct(productId, storeId);
            LOGGER_INFO.info("basket product was removed successfully");
            return Response.success();
        } catch (Exception e) {
            LOGGER_ERROR.severe("basket product was not removed successfully");
            return Response.failure(e.getMessage());
        }
    }
    public IUserRepository getUserRepository() {
        userRepository = SingletonCollection.getUserRepository();
        return userRepository;
    }

    public IUserRepository getUserRepositoryAsHashmap() {
        userRepositoryAsHashmap = SingletonCollection.getUserRepository();
        return userRepositoryAsHashmap;
    }


}
