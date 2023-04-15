package BGU.Group13B.service;

import BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl.UserRepositoryAsHashmap;
import BGU.Group13B.backend.SystemInfo;
import BGU.Group13B.backend.User.User;
import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;

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
                try {
                    //the first if might not be necessary when we will connect to web
                    if(!user.isRegistered()) {
                        if(userRepositoryAsHashmap.checkIfUserExists(username) != null) {
                            user.register(username, password, email);
                        }else{
                            System.out.println("user with this username already exists!");
                        }
                    }else{
                        System.out.println("already registered!");
                    }
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }


    @Override
    public int login(int userID,String username, String password) {
        try{
            //gets the user that we want to log into
            User user = userRepositoryAsHashmap.checkIfUserExists(username);
            user.login(username,password);
            //removes the current guest profile to swap to the existing member one
            userRepositoryAsHashmap.removeUser(userID);
            //gets the new id - of the user we logging into
            return userRepositoryAsHashmap.getUserId(user);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }
    }
}
