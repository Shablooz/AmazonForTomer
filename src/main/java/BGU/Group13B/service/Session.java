package BGU.Group13B.service;

import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl.UserRepositoryAsHashmap;
import BGU.Group13B.backend.User.User;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;

import java.time.LocalDateTime;

import java.util.List;


class Session implements ISession {
    private final Market market;
    UserRepositoryAsHashmap userRepositoryAsHashmap;

    public Session(Market market) {
        this.market = market;

        //callbacks initialization
        SingletonCollection.setAddToUserCart(this::addToCart);
    }

    @Override
    public void addProduct(int userId, String productName, int quantity, double price, int storeId) throws NoPermissionException {
        market.addProduct(userId, productName, quantity, price, storeId);
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
}
