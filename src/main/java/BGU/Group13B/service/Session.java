package BGU.Group13B.service;

import BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl.UserRepositoryAsHashmap;
import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.User.User;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;

import java.util.List;

class Session implements ISession {
    private final Market market;
    UserRepositoryAsHashmap userRepositoryAsHashmap;
    public Session(Market market) {
        this.market = market;
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
    public void purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice) {

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
    public void auctionPurchase(int userId, int storeId, int productId, double price) {

    }

    @Override
    public SystemInfo getSystemInformation(int adminId) {
        return null;
    }

    @Override
    public void register(int userId,String username, String password, String email) {
        User user = userRepositoryAsHashmap.getUser(userId);
        synchronized (user) {
            if(user.isRegistered()) {
                try {
                    user.register(username, password, email);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
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



}
