package BGU.Group13B.service;

import BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl.UserRepositoryAsHashmap;
import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;
import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl.UserRepositoryAsHashmap;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.User.User;
import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;

import java.time.LocalDateTime;

import java.util.List;


class Session implements ISession {
    private final Market market;
    IUserRepository userRepositoryAsHashmap;

    public Session(Market market) {
        this.market = market;

        //callbacks initialization
        SingletonCollection.setAddToUserCart(this::addToCart);
        this.userRepositoryAsHashmap = SingletonCollection.getUserRepository();
    }

    @Override
    public void addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity){
        try{
            market.addProduct(userId, storeId, productName, category, price, stockQuantity);
        }
        catch (Exception e){
            //TODO: handle exception
        }

    }


    @Override
    public void addToCart(int userId, int storeId, int productId, int quantity) {

    }

    @Override
    public void purchaseProductCart(int userId, String address, String creditCardNumber, String creditCardMonth, String creditCardYear, String creditCardHolderFirstName, String creditCardHolderLastName, String creditCardCcv, String id, String creditCardType) {

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
    public synchronized void register(int userId, String username, String password, String email) {
        User user = userRepositoryAsHashmap.getUser(userId);
        try {
            //the first "if" might not be necessary when we will connect to web
            if (!user.isRegistered()) {
                if (userRepositoryAsHashmap.checkIfUserExists(username) != null) {
                    user.register(username, password, email);
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
    public int login(int userID, String username, String password) {
        try {
            //gets the user that we want to log into
            User user = userRepositoryAsHashmap.checkIfUserExists(username);
            synchronized (user) {
                user.login(username, password);
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
    public void addStore(int userId, String storeName, String category) {
        User user = userRepositoryAsHashmap.getUser(userId);
        synchronized (user){
            if(user.isRegistered()){
                try{
                    market.addStore(userId, storeName, category);
                }
                catch(Exception e){
                    //TODO: handle exception
                }
            }
        }
    }

    @Override
    public void addProductToCart(int userId, int productId, int storeId) {
        try{
            userRepositoryAsHashmap.getUser(userId).addProductToCart(productId, storeId);
        }catch (Exception e){
            //TODO: handle exception
           }
    }

    @Override
    public void getUserPurchaseHistory(int userId) {
        throw new RuntimeException("not implemented");
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
           return  userRepositoryAsHashmap.getUser(userId).getComplaint();
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void markMessageAsRead(int userId, String receiverId, String senderId, int messageId) {
        try {
            userRepositoryAsHashmap.getUser(userId).markMessageAsRead(receiverId, senderId, messageId);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMassageAdmin(int userId, String receiverId, String header, String massage) {
        try {
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
    public Message readMassage(int userId, String receiverId) {
        try {
            return userRepositoryAsHashmap.getUser(userId).readMassage(receiverId);
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
    public void setProductName(int userId, int storeId, int productId, String name) {
        try{
            market.setProductName(userId, storeId, productId, name);
        }
        catch (Exception e){
            //TODO: handle exception
        }

    }

    @Override
    public void setProductCategory(int userId, int storeId, int productId, String category) {
        try{
            market.setProductCategory(userId, storeId, productId, category);
        }
        catch (Exception e){
            //TODO: handle exception
        }
    }

    @Override
    public void setProductPrice(int userId, int storeId, int productId, double price) {
        try{
            market.setProductPrice(userId, storeId, productId, price);
        }
        catch (Exception e){
            //TODO: handle exception
        }
    }

    @Override
    public void setProductStockQuantity(int userId, int storeId, int productId, int stockQuantity) {
        try{
            market.setProductStockQuantity(userId, storeId, productId, stockQuantity);
        }
        catch (Exception e){
            //TODO: handle exception
        }
    }

    @Override
    public int enterAsGuest() {
        int id =  userRepositoryAsHashmap.getNewUserId();
        userRepositoryAsHashmap.addUser(id, new User(id));
        return id;
    }



    @Override
    public void removeProduct(int userId, int storeId, int productId) {
        try{
            market.removeProduct(userId, storeId, productId);
        }
        catch (Exception e){
            //TODO: handle exception
        }
    }

    @Override
    public void exitSystemAsGuest(int userId) {
        userRepositoryAsHashmap.removeUser(userId);
    }

}
