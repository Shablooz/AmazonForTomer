package BGU.Group13B.service;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;
import BGU.Group13B.backend.System.SystemInfo;
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
        //IMPORTANT need to initialize the session AFTER loading first user (id = 1) from database
        //id should 1
        //This should do nothing if the system was initialized in the past - making first admin
        int id = 1;
        userRepositoryAsHashmap.addUser(id, new User(id));
        register(id, "kingOfTheSheep", "SheePLover420",
                "mrsheep@gmail.com", "11", "11", "11",LocalDate.MIN);

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
            return Response.success();
        } catch (PurchaseFailedException | NoPermissionException e) {
            return Response.exception(e);
        }
    }

    @Override
    public Pair<Double, List<ServiceBasketProduct>> startPurchaseBasketTransaction(int userId, List<String> coupons) {
        try {
            var priceSuccessfulItems = getUserRepository().getUser(userId).startPurchaseBasketTransaction(coupons);
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
    public Response<VoidResponse> purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice, int amount) {
        try {
            market.purchaseProposalSubmit(userId, storeId, productId, proposedPrice, amount);
            return Response.success();
        } catch (NoPermissionException e) {
            return Response.exception(e);
        }
    }
    @Override
    public void purchaseProposalApprove(int managerId, int storeId, int productId){
        try {
            market.purchaseProposalApprove(managerId, storeId, productId);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void purchaseProposalReject(int storeId, int managerId, int bidId){
        try {
            market.purchaseProposalReject(managerId,storeId, bidId);
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
                                      String email, String answer1, String answer2, String answer3,LocalDate birthDate){
        User user = getUserRepositoryAsHashmap().getUser(userId);

        //the first "if" might not be necessary when we will connect to web
        if (this.getUserRepositoryAsHashmap().checkIfUserWithEmailExists(email)) {
            throw new IllegalArgumentException("user with this email already exists!");//temporary
        }
        if (!user.isRegistered()) {
            if (getUserRepositoryAsHashmap().checkIfUserExists(username) == null) {
                user.register(username, password, email, answer1, answer2, answer3,birthDate);
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
            return Response.success(market.search(searchWords));
        }
        catch (Exception e){
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
                return getUserRepositoryAsHashmap().getUserId(user);
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
                    return Response.success(storeId);
                } catch (Exception e) {
                    e.printStackTrace();
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
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> clearMessageToReply(int userId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).clearMessageToReply();
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    public Response<VoidResponse> openComplaint(int userId, String header, String complaint) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).openComplaint(header, complaint);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }


    public Response<Message> getComplaint(int userId) {
        try {
            return Response.success(getUserRepositoryAsHashmap().getUser(userId).getComplaint());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> markMessageAsReadAdmin(int userId, String receiverId, String senderId, int messageId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).markMessageAsReadAdmin(receiverId, senderId, messageId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> sendMassageAdmin(int userId, String receiverId, String header, String massage) {
        try {
            if (getUserRepository().checkIfUserExists(receiverId) == null)
                throw new RuntimeException("receiver Id not found");
            getUserRepositoryAsHashmap().getUser(userId).sendMassageAdmin(receiverId, header, massage);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> answerComplaint(int userId, String answer) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).answerComplaint(answer);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Message> readMessage(int userId) {
        try {
            return Response.success(getUserRepositoryAsHashmap().getUser(userId).readMassage());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> replayMessage(int userId, String message) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).replayMessage(message);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Message> readOldMessage(int userId) {
        try {
            return Response.success(getUserRepositoryAsHashmap().getUser(userId).readOldMessage());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> refreshOldMessages(int userId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).refreshOldMessage();
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> sendMassageStore(int userId, String header, String massage, int storeId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).sendMassageStore(header, massage, storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Message> readUnreadMassageStore(int userId, int storeId) {
        try {
            return Response.success(getUserRepositoryAsHashmap().getUser(userId).readUnreadMassageStore(storeId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Message> readReadMassageStore(int userId, int storeId) {
        try {
            return Response.success(getUserRepositoryAsHashmap().getUser(userId).readReadMassageStore(storeId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> answerQuestionStore(int userId, String answer) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).answerQuestionStore(answer);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> refreshOldMessageStore(int userId, int storeId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).refreshOldMessageStore(storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addReview(int userId, String review, int storeId, int productId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).addReview(review, storeId, productId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeReview(int userId, int storeId, int productId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).removeReview(storeId, productId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Review> getReview(int userId, int storeId, int productId) {
        try {
            return Response.success(getUserRepositoryAsHashmap().getUser(userId).getReview(storeId, productId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<ReviewService>> getAllReviews(int userId, int storeId, int productId) {
        try {
            List<Review> reviews=getUserRepositoryAsHashmap().getUser(userId).getAllReviews(storeId, productId,userId);
            List<ReviewService> reviewServices=new ArrayList<>();
            int i=0;
            for (Review review:reviews) {
                Response<Float> scoreResponse = getProductScoreUser(userId, review.getStoreId(), review.getProductId(), review.getUserId());
                String scoreString;
                if(scoreResponse.didntSucceed()){
                    scoreString="non";
                }else{
                    scoreString=scoreResponse.getData().toString();
                }
                reviewServices.add(new ReviewService(getUserRepositoryAsHashmap().getUser(review.getUserId()).getUserName(),review.getReview(),scoreString));
            }
            return Response.success(reviewServices);
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Float> getProductScore(int userId, int storeId, int productId) {
        try {
            return Response.success(getUserRepositoryAsHashmap().getUser(userId).getProductScore(storeId, productId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }
    @Override
    public Response<Float> getProductScoreUser(int userId, int storeId, int productId,int userIdTarget) {
        try {
            return Response.success(getUserRepositoryAsHashmap().getUser(userId).getProductScoreUser(userIdTarget,storeId, productId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addAndSetProductScore(int userId, int storeId, int productId, int score) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).addAndSetProductScore(storeId, productId, score);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeProductScore(int userId, int storeId, int productId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).removeProductScore(storeId, productId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addStoreScore(int userId, int storeId, int score) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).addStoreScore(storeId, score);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeStoreScore(int userId, int storeId) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).removeStoreScore(storeId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> modifyStoreScore(int userId, int storeId, int score) {
        try {
            getUserRepositoryAsHashmap().getUser(userId).modifyStoreScore(storeId, score);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Float> getStoreScore(int userId, int storeId) {
        try {
            return Response.success(getUserRepositoryAsHashmap().getUser(userId).getStoreScore(storeId));
        } catch (Exception e) {
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
        List<BasketProduct> cartContent;
        try {
            cartContent = getUserRepositoryAsHashmap().getUser(userId).getCartBasketProducts();
        } catch (Exception e) {
            return Response.exception(e);
        }
        return Response.success(cartContent.stream().map(ServiceBasketProduct::new).collect(Collectors.toList()));
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
        int id = getUserRepositoryAsHashmap().getNewUserId();
        getUserRepositoryAsHashmap().addUser(id, new User(id));
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
        return getUserRepositoryAsHashmap().getUser(userId).getUserName();
    }

    @Override
    public void setUsername(int userId, String newUsername) {
        getUserRepositoryAsHashmap().getUser(userId).setUserName(newUsername);
    }

    @Override
    public void setUserStatus(int admin_id, int userId, int newStatus) {
        if (!getUserStatus(admin_id).equals("Admin")) {
            //should throw an exception
            throw new IllegalArgumentException("isnt an admin");
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
            getUserRepositoryAsHashmap().getUser(userId).changeProductQuantityInCart(storeId, productId, quantity);
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
    public double getTotalPriceOfCart(int userId) {
        return getUserRepository().getUser(userId).getTotalPriceOfCart();
    }

    @Override
    public void cancelPurchase(int userId) {
        getUserRepository().getUser(userId).cancelPurchase();
    }

    @Override
    public List<ServiceProduct> getAllFailedProductsAfterPayment(int userId) {
        return getUserRepository().getUser(userId).getAllFailedProductsAfterPayment().
                stream().map(ServiceProduct::new).collect(Collectors.toList());
    }


    @Override
    public boolean isUserLogged(int userId) {
        return getUserRepositoryAsHashmap().getUser(userId).isLoggedIn();
    }

    @Override
    public Response<List<PurchaseHistory>> getUserPurchaseHistory(int userId) {
        try {
            isUserLogged(userId);
            return Response.success(getUserRepositoryAsHashmap().getUser(userId).getPurchaseHistory());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }


    @Override
    public Response<List<Pair<StoreInfo, String>>> getAllUserAssociatedStores(int userId) {
        List<Pair<Integer, String>> storeIdsAndRoles;
        try {
            storeIdsAndRoles = getStoresOfUser(userId);
        } catch (Exception e) {
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
    public void pushTest() {
        PushNotification.pushNotification("MY TEST!", 2);
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
            getUserRepositoryAsHashmap().getUser(userId).fetchMessages();
            return Response.success(new VoidResponse());
        } catch (Exception e) {
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
            return Response.success(user.getPurchaseHistory());
        } catch (Exception e) {
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
    public Response<List<WorkerCard>> getStoreWorkersInfo(int userId, int storeId) {
        try {
            return Response.success(market.getStoreWorkersInfo(userId, storeId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> setPurchasePolicyCondition(int storeId, int userId, int conditionId) {
        try {
            market.setPurchasePolicyCondition(storeId, userId, conditionId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addORCondition(int storeId, int userId, int condition1, int condition2) {
        try {
            return Response.success(market.addORCondition(storeId, userId, condition1, condition2));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addANDCondition(int storeId, int userId, int condition1, int condition2) {
        try {
            return Response.success(market.addANDCondition(storeId, userId, condition1, condition2));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addXORCondition(int storeId, int userId, int condition1, int condition2) {
        try {
            return Response.success(market.addXORCondition(storeId, userId, condition1, condition2));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addIMPLYCondition(int storeId, int userId, int condition1, int condition2) {
        try {
            return Response.success(market.addIMPLYCondition(storeId, userId, condition1, condition2));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStorePriceCondition(int storeId, int userId, double lowerBound, double upperBound) {
        try {
            return Response.success(market.addStorePriceCondition(storeId, userId, lowerBound, upperBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStorePriceCondition(int storeId, int userId, double lowerBound) {
        try {
            return Response.success(market.addStorePriceCondition(storeId, userId, lowerBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreQuantityCondition(int storeId, int userId, int lowerBound, int upperBound) {
        try{
            return Response.success(market.addStoreQuantityCondition(storeId, userId, lowerBound, upperBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreQuantityCondition(int storeId, int userId, int lowerBound) {
        try {
            return Response.success(market.addStoreQuantityCondition(storeId, userId, lowerBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryPriceCondition(int storeId, int userId, String category, double lowerBound, double upperBound) {
        try {
            return Response.success(market.addCategoryPriceCondition(storeId, userId, category, lowerBound, upperBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryPriceCondition(int storeId, int userId, String category, double lowerBound) {
        try {
            return Response.success(market.addCategoryPriceCondition(storeId, userId, category, lowerBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryQuantityCondition(int storeId, int userId, String category, int lowerBound, int upperBound) {
        try {
            return Response.success(market.addCategoryQuantityCondition(storeId, userId, category, lowerBound, upperBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryQuantityCondition(int storeId, int userId, String category, int lowerBound) {
        try {
            return Response.success(market.addCategoryQuantityCondition(storeId, userId, category, lowerBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addDateCondition(int storeId, int userId, LocalDateTime lowerBound, LocalDateTime upperBound) {
        try {
            return Response.success(market.addDateCondition(storeId, userId, lowerBound, upperBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addDateCondition(int storeId, int userId, LocalDateTime lowerBound) {
        try {
            return Response.success(market.addDateCondition(storeId, userId, lowerBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductPriceCondition(int storeId, int userId, int productId, double lowerBound, double upperBound) {
        try {
            return Response.success(market.addProductPriceCondition(storeId, userId, productId, lowerBound, upperBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductPriceCondition(int storeId, int userId, int productId, double lowerBound) {
        try {
            return Response.success(market.addProductPriceCondition(storeId, userId, productId, lowerBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductQuantityCondition(int storeId, int userId, int productId, int lowerBound, int upperBound) {
        try {
            return Response.success(market.addProductQuantityCondition(storeId, userId, productId, lowerBound, upperBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductQuantityCondition(int storeId, int userId, int productId, int lowerBound) {
        try {
            return Response.success(market.addProductQuantityCondition(storeId, userId, productId, lowerBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addTimeCondition(int storeId, int userId, LocalDateTime lowerBound, LocalDateTime upperBound) {
        try {
            return Response.success(market.addTimeCondition(storeId, userId, lowerBound, upperBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addTimeCondition(int storeId, int userId, LocalDateTime lowerBound) {
        try {
            return Response.success(market.addTimeCondition(storeId, userId, lowerBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addUserAgeCondition(int storeId, int userId, int lowerBound, int upperBound) {
        try {
            return Response.success(market.addUserAgeCondition(storeId, userId, lowerBound, upperBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addUserAgeCondition(int storeId, int userId, int lowerBound) {
        try {
            return Response.success(market.addUserAgeCondition(storeId, userId, lowerBound));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String coupon) {
        try {
            return Response.success(market.addStoreDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, coupon));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String coupon) {
        try {
            return Response.success(market.addStoreDiscount(storeId, userId, discountPercentage, expirationDate, coupon));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate) {
        try {
            return Response.success(market.addStoreDiscount(storeId, userId, conditionId, discountPercentage, expirationDate));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addStoreDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate) {
        try {
            return Response.success(market.addStoreDiscount(storeId, userId, discountPercentage, expirationDate));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        try {
            return Response.success(market.addCategoryDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, category, coupon));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        try {
            return Response.success(market.addCategoryDiscount(storeId, userId, discountPercentage, expirationDate, category, coupon));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String category) {
        try {
            return Response.success(market.addCategoryDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, category));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addCategoryDiscount(int storeId, int userId, double discountPercentage, LocalDate expirationDate, String category) {
        try {
            return Response.success(market.addCategoryDiscount(storeId, userId, discountPercentage, expirationDate, category));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductDiscount(int storeId, int userId, int conditionId, double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        try {
            return Response.success(market.addProductDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, productId, coupon));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductDiscount(int storeId, int userId, double discountPercentage, LocalDate
            expirationDate, int productId, String coupon) {
        try {
            return Response.success(market.addProductDiscount(storeId, userId, discountPercentage, expirationDate, productId, coupon));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductDiscount(int storeId, int userId, int conditionId,
                                                double discountPercentage, LocalDate expirationDate, int productId) {
        try {
            return Response.success(market.addProductDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, productId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> addProductDiscount(int storeId, int userId, double discountPercentage, LocalDate
            expirationDate, int productId) {
        try {
            return Response.success(market.addProductDiscount(storeId, userId, discountPercentage, expirationDate, productId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<DiscountInfo>> getStoreDiscounts(int storeId, int userId) {
        try {
            return Response.success(market.getStoreDiscounts(storeId, userId).stream().map(d -> new DiscountInfo(d.getDiscountId(), d.toString())).collect(Collectors.toList()));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<DiscountInfo> getDiscount(int storeId, int userId, int discountId) {
        try {
            var discount = market.getDiscount(storeId, userId, discountId);
            return Response.success(new DiscountInfo(discountId, discount.toString()));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeDiscount(int storeId, int userId, int discountId) {
        try {
            market.removeDiscount(storeId, userId, discountId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addDiscountAsRoot(int storeId, int userId, int discountId) {
        try {
            market.addDiscountAsRoot(storeId, userId, discountId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addDiscountToXORRoot(int storeId, int userId, int discountId) {
        try {
            market.addDiscountToXORRoot(storeId, userId, discountId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addDiscountToMAXRoot(int storeId, int userId, int discountId) {
        try {
            market.addDiscountToMAXRoot(storeId, userId, discountId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> addDiscountToADDRoot(int storeId, int userId, int discountId) {
        try {
            market.addDiscountToADDRoot(storeId, userId, discountId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<DiscountAccumulationTreeInfo> getDiscountAccumulationTree(int storeId, int userId) {
        try{
            return Response.success(market.getDiscountAccumulationTree(storeId, userId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> deleteStoreAccumulationTree(int storeId, int userId) {
        try {
            market.deleteStoreAccumulationTree(storeId, userId);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
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
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> removeIndividualPermission(int userId, int managerId, int storeId, UserPermissions.IndividualPermission individualPermission) {
        try {
            market.removeIndividualPermission(userId, managerId, storeId, individualPermission);
            return Response.success(new VoidResponse());
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<Integer> getUserIdByUsername(String username) {
        try{
            return Response.success(getUserRepository().getUserIdByUsername(username));
        } catch (Exception e){
            return Response.exception(e);
        }
    }

    @Override
    public Response<HashMap<Integer, String>> getUserIdsToUsernamesMapper(List<Integer> userIds) {
        try{
            return Response.success(getUserRepository().getUserIdsToUsernamesMapper(userIds));
        } catch (Exception e){
            return Response.exception(e);
        }
    }

    @Override
    public Response<Boolean> isStoreHidden(int storeId) {
        try{
            return Response.success(market.isStoreHidden(storeId));
        } catch (Exception e){
            return Response.exception(e);
        }
    }

    @Override
    public Response<Boolean> isAdmin(int userId) {
        try{
            return Response.success(getUserStatus(userId).equals("Admin"));
        } catch (Exception e){
            return Response.exception(e);
        }
    }

    @Override
    public Response<Condition> getStorePurchasePolicy(int storeId, int userId) {
        try{
            return Response.success(market.getStorePurchasePolicy(storeId, userId));
        } catch (Exception e){
            return Response.exception(e);
        }
    }

    @Override
    public Response<VoidResponse> resetStorePurchasePolicy(int storeId, int userId) {
        try{
            market.resetStorePurchasePolicy(storeId, userId);
            return Response.success(new VoidResponse());
        } catch (Exception e){
            return Response.exception(e);
        }
    }

    public Response<StoreInfo> getGeneralStoreInfo(int storeId) {
        try {
            return Response.success(market.getGeneralStoreInfo(storeId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    @Override
    public Response<List<StoreInfo>> getAllStores() {
        try {
            Set<StoreInfo> storesInfos = market.getAllGeneralStoreInfo();
            List<StoreInfo> asList = storesInfos.stream().toList();
            return Response.success(asList);
        } catch (Exception e) {
        return Response.exception(e);
    }

    }

    public UserCard getUserInfo(int userId, int userInfoId){
        try {
            return market.getUserInfo(userId, userInfoId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Response<List<UserCard>> getAllUserCards(int userId){
        try {
            return Response.success(market.getAllUserCards(userId));
        } catch (Exception e) {
            return Response.exception(e);
        }
    }

    public Response<Integer> removeUser(int userId, int removeUserId){
        try {
            return Response.success(1);//placeholder for the feature in the branch
        } catch (Exception e){
            return Response.exception(e);
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
